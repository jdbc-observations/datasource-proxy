package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.proxy.MutableQueryExecutionContext;
import net.ttddyy.dsproxy.proxy.Stopwatch;
import net.ttddyy.dsproxy.proxy.StopwatchFactory;
import net.ttddyy.dsproxy.proxy.SystemStopwatchFactory;

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
 * When a query exceeds the specified threshold, the {@link #onSlowQuery(QueryExecutionContext, long)} callback is
 * invoked while the query is still running. The callback is invoked only once for each query that exceeds the
 * threshold. The running query is not interrupted or cancelled.
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
        protected QueryExecutionContext queryExecutionContext;
        protected long startTimeInMills;
        protected Stopwatch stopwatch;

        public RunningQueryContext(QueryExecutionContext queryExecutionContext, long nowInMills, Stopwatch stopwatch) {
            this.queryExecutionContext = queryExecutionContext;
            this.startTimeInMills = nowInMills;
            this.stopwatch = stopwatch;
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
    protected long threshold;
    protected TimeUnit thresholdTimeUnit;
    protected Map<String, RunningQueryContext> inExecution = new ConcurrentHashMap<String, RunningQueryContext>();
    protected StopwatchFactory stopwatchFactory = new SystemStopwatchFactory();

    @Override
    public void beforeQuery(QueryExecutionContext queryExecutionContext) {

        final String execInfoKey = getExecutionInfoKey(queryExecutionContext);

        // Pass only the key to prevent a hard reference from the Runnable to the context. (Issue-53)
        Runnable check = new Runnable() {
            @Override
            public void run() {
                // If the query is still in the map, it is still running.
                RunningQueryContext context = SlowQueryListener.this.inExecution.get(execInfoKey);

                if (context != null) {
                    Duration elapsedTime = context.stopwatch.getElapsedTime();
                    // Set the elapsed time on the shared context, as 1.x did.
                    // shortcut: only the proxy's MutableQueryExecutionContext can carry it; Phase 4 replaces this listener
                    if (context.queryExecutionContext instanceof MutableQueryExecutionContext mutable
                            && mutable.getElapsedTime().isZero()) {
                        mutable.setElapsedTime(elapsedTime);
                    }

                    onSlowQuery(context.queryExecutionContext, context.startTimeInMills);
                }
            }
        };
        this.executor.schedule(check, this.threshold, this.thresholdTimeUnit);

        long now = System.currentTimeMillis();
        Stopwatch stopwatch = this.stopwatchFactory.create().start();
        RunningQueryContext context = new RunningQueryContext(queryExecutionContext, now, stopwatch);
        this.inExecution.put(execInfoKey, context);

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
     * @param startTimeInMills time in milliseconds when the query started
     */
    protected void onSlowQuery(QueryExecutionContext queryExecutionContext, long startTimeInMills) {
    }

    public void setThreshold(long threshHold) {
        this.threshold = threshHold;
    }

    public void setThresholdTimeUnit(TimeUnit thresholdTimeUnit) {
        this.thresholdTimeUnit = thresholdTimeUnit;
    }

    public ScheduledExecutorService getExecutor() {
        return executor;
    }

    public long getThreshold() {
        return threshold;
    }

    public TimeUnit getThresholdTimeUnit() {
        return thresholdTimeUnit;
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


    /**
     * Sets the {@link StopwatchFactory} used to compute {@link QueryExecutionContext#getElapsedTime()} for slow queries.
     *
     * @param stopwatchFactory factory used to create a {@link Stopwatch} for slow-query timing
     * @since 1.5.1
     */
    public void setStopwatchFactory(StopwatchFactory stopwatchFactory) {
        this.stopwatchFactory = stopwatchFactory;
    }
}
