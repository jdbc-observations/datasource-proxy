package net.ttddyy.dsproxy.listener;

import org.jspecify.annotations.Nullable;

import net.ttddyy.dsproxy.proxy.MutableQueryExecutionContext;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;


/**
 * Slow query detection listener.
 *
 * <p>This listener detects slow queries <em>during execution</em> (sometimes called <em>preemptive</em>).
 * When a query exceeds the specified threshold, the {@link #onSlowQuery(QueryExecutionContext, Duration)} callback is
 * invoked while the query is still running. The callback is invoked only once for each query that exceeds the
 * threshold. The running query is not interrupted or cancelled.
 *
 * <p>The threshold must be set with {@link #setThreshold(Duration)} before the listener is used. It must be positive:
 * zero and negative values are rejected (in 1.x, {@code 0} reported every query as slow).
 *
 * <p>NOTE:
 * <ul>
 * <li>The callback is invoked by a thread from this listener's scheduled executor, not by the thread executing the query.
 * <li>{@link QueryExecutionContext#getElapsedTime()} contains the elapsed time at the moment of the check, which is usually
 * close to the specified threshold rather than the query's actual execution time.
 * <li>The query result and success or failure status in {@link QueryExecutionContext} are not available because the query
 * has not finished.
 * <li>A check is scheduled for each query execution.
 * </ul>
 *
 * <p>If you want to log or take action <em>after execution</em> (sometimes called <em>non-preemptive</em>) for queries
 * that exceed a specified threshold, use a regular logging listener like this:
 * <pre>
 * long thresholdInMillis = ...
 * SLF4JQueryLoggingListener listener = new SLF4JQueryLoggingListener(){
 *      {@literal @}Override
 *      public void afterQuery(QueryExecutionContext context) {
 *          if (context.getElapsedTime().toMillis() &gt;= thresholdInMillis) {
 *              super.afterQuery(context);
 *          }
 *      }
 * };
 * </pre>
 * This approach makes the actual query execution time available, but does not detect queries that never return.
 * Both approaches can be used together.
 *
 * @author Tadaya Tsuyukubo
 * @see net.ttddyy.dsproxy.listener.logging.CommonsSlowQueryListener
 * @see net.ttddyy.dsproxy.listener.logging.JULSlowQueryListener
 * @see net.ttddyy.dsproxy.listener.logging.SLF4JSlowQueryListener
 * @see net.ttddyy.dsproxy.listener.logging.SystemOutSlowQueryListener
 * @since 1.4.1
 */
public class SlowQueryListener implements ProxyDataSourceListener {

    /**
     * Data holder for the currently running query.
     *
     * This structure prevents a hard reference from scheduled {@link Runnable} instances to {@link QueryExecutionContext} and
     * related objects.
     */
    protected static class RunningQueryContext {
        protected final QueryExecutionContext queryExecutionContext;
        protected final long startNanos;

        public RunningQueryContext(QueryExecutionContext queryExecutionContext, long startNanos) {
            this.queryExecutionContext = queryExecutionContext;
            this.startNanos = startNanos;
        }
    }

    protected boolean useDaemonThread = true;

    protected ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = Executors.defaultThreadFactory().newThread(r);
            thread.setDaemon(SlowQueryListener.this.useDaemonThread);
            return thread;
        }
    });
    protected @Nullable Duration threshold;  // required; checked in beforeQuery
    protected Map<String, RunningQueryContext> inExecution = new ConcurrentHashMap<String, RunningQueryContext>();

    @Override
    public void beforeQuery(QueryExecutionContext queryExecutionContext) {
        if (this.threshold == null) {
            throw new IllegalStateException("threshold is not set");
        }

        final String execInfoKey = getExecutionInfoKey(queryExecutionContext);

        // Pass only the key to prevent a hard reference from the Runnable to the context. (Issue-53)
        Runnable check = new Runnable() {
            @Override
            public void run() {
                // If the query is still in the map, it is still running.
                RunningQueryContext context = SlowQueryListener.this.inExecution.get(execInfoKey);

                if (context != null) {
                    Duration elapsedTime = Duration.ofNanos(System.nanoTime() - context.startNanos);
                    // Set the elapsed time on the shared context, as 1.x did.
                    // shortcut: only the proxy's MutableQueryExecutionContext can carry it; Phase 4 replaces this listener
                    if (context.queryExecutionContext instanceof MutableQueryExecutionContext mutable
                            && mutable.getElapsedTime().isZero()) {
                        mutable.setElapsedTime(elapsedTime);
                    }

                    onSlowQuery(context.queryExecutionContext, elapsedTime);
                }
            }
        };
        RunningQueryContext context = new RunningQueryContext(queryExecutionContext, System.nanoTime());
        this.inExecution.put(execInfoKey, context);
        this.executor.schedule(check, this.threshold.toNanos(), TimeUnit.NANOSECONDS);

    }

    @Override
    public void afterQuery(QueryExecutionContext queryExecutionContext) {
        String executionInfoKey = getExecutionInfoKey(queryExecutionContext);
        this.inExecution.remove(executionInfoKey);
    }


    /**
     * Calculates a key for the given {@link QueryExecutionContext}.
     *
     * <p>This key is passed to the slow-query check {@link Runnable} and is also used for removal in
     * {@link #afterQuery(QueryExecutionContext)}.
     *
     * <p>The default implementation uses {@link System#identityHashCode(Object)}. This does not guarantee 100% uniqueness,
     * but it is sufficient for the short-lived usage in this class.
     * <p>Subclasses may override this method to provide a different implementation that uniquely represents
     * {@link QueryExecutionContext}.
     *
     * @param queryExecutionContext query execution context
     * @return key
     */
    protected String getExecutionInfoKey(QueryExecutionContext queryExecutionContext) {
        int exeInfoKey = System.identityHashCode(queryExecutionContext);
        return String.valueOf(exeInfoKey);
    }

    /**
     * Callback invoked when query execution exceeds the threshold.
     *
     * <p>This callback is invoked only once per query if it exceeds the threshold.
     * It is invoked by a thread from the scheduled executor while the query is still running.
     * {@link QueryExecutionContext#getElapsedTime()} contains the elapsed time at the moment of the check.
     *
     * @param queryExecutionContext query execution context
     * @param elapsedTime time since the query started, measured at the check (at least the threshold)
     */
    protected void onSlowQuery(QueryExecutionContext queryExecutionContext, Duration elapsedTime) {
    }

    /**
     * Sets the threshold after which a running query is reported as slow.
     *
     * <p>Required: {@link #beforeQuery(QueryExecutionContext)} throws {@link IllegalStateException} if it is not set.
     *
     * @param threshold positive, non-null duration
     * @throws IllegalArgumentException if {@code threshold} is {@code null}, zero or negative
     */
    public void setThreshold(Duration threshold) {
        if (threshold == null || threshold.isZero() || threshold.isNegative()) {
            throw new IllegalArgumentException("threshold must be positive: " + threshold);
        }
        this.threshold = threshold;
    }

    public ScheduledExecutorService getExecutor() {
        return executor;
    }

    public @Nullable Duration getThreshold() {
        return this.threshold;
    }

    /**
     * Sets whether the executor creates daemon threads to check for slow queries.
     *
     * @param useDaemonThread whether to use daemon threads; defaults to {@code true}
     * @since 1.4.2
     */
    public void setUseDaemonThread(boolean useDaemonThread) {
        this.useDaemonThread = useDaemonThread;
    }

}
