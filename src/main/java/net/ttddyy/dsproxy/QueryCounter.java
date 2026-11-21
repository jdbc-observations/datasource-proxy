package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.listener.QueryExecutionContext;
import org.jspecify.annotations.Nullable;

import java.util.List;

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
         * @throws IllegalStateException if this open scope is not the current scope on this thread
         */
        @Override
        public void close() {
            if (this.closed) {
                return;
            }
            if (current() != this) {
                throw new IllegalStateException("QueryCounter scope closed out of order or on another thread");
            }
            this.closed = true;
            setCurrent(this.parent);
        }

        @Nullable Scope parent() {
            return this.parent;
        }

        void record(@Nullable String dataSourceName, QueryExecutionContext context, List<QueryType> types) {
            this.table.record(dataSourceName, context, types);
        }

    }

}
