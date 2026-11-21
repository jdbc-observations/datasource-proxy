package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.listener.QueryExecutionContext;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * Counts queries run inside a scope.
 *
 * <pre>{@code
 * QueryCount count = QueryCounter.count(() -> service.findRecent());
 *
 * try (var scope = QueryCounter.start()) {
 *     service.findRecent();
 *     scope.count().select();
 * }
 * }</pre>
 *
 * <p>Only DataSources built with {@code ProxyDataSourceBuilder.countQuery()} report to scopes; a scope around any
 * other DataSource counts 0. Only the thread that opened the scope counts, unless work is handed to other threads
 * through {@code wrap(...)}. Scopes nest: a query counts in the current scope and every enclosing one.
 *
 * @author Tadaya Tsuyukubo
 * @see DataSourceQueryCounter
 */
public final class QueryCounter {

    private static final ThreadLocal<@Nullable Scope> CURRENT = new ThreadLocal<>();

    private QueryCounter() {
    }

    /**
     * Open a scope on this thread. Close it, preferably with try-with-resources.
     *
     * @return the new scope
     */
    public static Scope start() {
        Scope scope = new Scope(current());
        setCurrent(scope);
        return scope;
    }

    /**
     * Run the task in a new scope.
     *
     * @param task task to run
     * @return grand total of the queries the task ran
     */
    public static QueryCount count(Runnable task) {
        try (Scope scope = start()) {
            task.run();
            return scope.count();
        }
    }

    /**
     * Carry the current scope into a task run on another thread. The task's queries count in that scope (and its
     * enclosing scopes), even if the task finishes after the scope is closed.
     *
     * @param task task
     * @return wrapped task, or {@code task} itself when no scope is open
     */
    public static Runnable wrap(Runnable task) {
        Scope captured = current();
        if (captured == null) {
            return task;
        }
        return () -> {
            Scope previous = current();
            setCurrent(captured);
            try {
                task.run();
            } finally {
                setCurrent(previous);
            }
        };
    }

    /**
     * Callable variant of {@link #wrap(Runnable)}.
     *
     * @param task task
     * @param <T>  result type
     * @return wrapped task, or {@code task} itself when no scope is open
     */
    public static <T> Callable<T> wrap(Callable<T> task) {
        Scope captured = current();
        if (captured == null) {
            return task;
        }
        return () -> {
            Scope previous = current();
            setCurrent(captured);
            try {
                return task.call();
            } finally {
                setCurrent(previous);
            }
        };
    }

    /**
     * Each submitted task runs in the scope that was current on the submitting thread.
     *
     * @param executor executor
     * @return wrapping executor
     */
    public static Executor wrap(Executor executor) {
        return command -> executor.execute(wrap(command));
    }

    /**
     * Each submitted task runs in the scope that was current on the submitting thread. Works with virtual-thread
     * executors.
     *
     * @param executor executor service
     * @return wrapping executor service; lifecycle methods are delegated
     */
    public static ExecutorService wrap(ExecutorService executor) {
        return new ScopedExecutorService(executor);
    }

    static @Nullable Scope current() {
        return CURRENT.get();
    }

    static void setCurrent(@Nullable Scope scope) {
        if (scope == null) {
            CURRENT.remove();
        } else {
            CURRENT.set(scope);
        }
    }

    /**
     * A query-counting scope. Counts stay readable after it is closed.
     */
    public static final class Scope implements AutoCloseable {

        private final @Nullable Scope parent;

        private final QueryCountTable table = new QueryCountTable();

        private final Thread owner = Thread.currentThread();

        private volatile boolean closed;

        private Scope(@Nullable Scope parent) {
            this.parent = parent;
        }

        /**
         * @return grand total across all DataSources
         */
        public QueryCount count() {
            return this.table.total();
        }

        /**
         * @param dataSourceName DataSource name; {@code ""} for a DataSource without a name
         * @return counts for that DataSource; all zero if it ran nothing
         */
        public QueryCount count(String dataSourceName) {
            return this.table.get(dataSourceName);
        }

        /**
         * Close this scope and make its parent current again. Closing twice does nothing.
         *
         * <p>If inner scopes were left open, they are closed too (so the thread does not keep counting into them)
         * and an {@link IllegalStateException} is thrown.
         *
         * @throws IllegalStateException if called on a thread other than the one that opened this scope, or if
         *                               inner scopes were left open
         */
        @Override
        public void close() {
            if (this.closed) {
                return;
            }
            if (Thread.currentThread() != this.owner) {
                throw new IllegalStateException("QueryCounter scope closed on another thread");
            }
            Scope current = current();
            if (current == this) {
                this.closed = true;
                setCurrent(this.parent);
                return;
            }
            boolean leaked = false;
            for (Scope s = current; s != null; s = s.parent) {
                if (s == this) {
                    leaked = true;
                    break;
                }
            }
            if (leaked) {
                for (Scope s = current; s != this; s = s.parent) {
                    s.closed = true;
                }
                this.closed = true;
                setCurrent(this.parent);
            }
            throw new IllegalStateException("QueryCounter scope closed out of order; inner scopes were left open");
        }

        @Nullable Scope parent() {
            return this.parent;
        }

        void record(@Nullable String dataSourceName, QueryExecutionContext context, List<QueryType> types) {
            this.table.record(dataSourceName, context, types);
        }

    }

}
