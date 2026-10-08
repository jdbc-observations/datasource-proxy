package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.proxy.Stopwatch;
import net.ttddyy.dsproxy.proxy.StopwatchFactory;
import net.ttddyy.dsproxy.proxy.SystemStopwatchFactory;

import java.util.List;
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
 * When a query exceeds the specified threshold, the {@link #onSlowQuery(ExecutionInfo, List, long)} callback is
 * invoked while the query is still running. The callback is invoked only once for each query that exceeds the
 * threshold. The running query is not interrupted or cancelled.
 *
 * <p>NOTE:
 * <ul>
 * <li>The callback is invoked by a thread from this listener's scheduled executor, not by the thread executing the query.
 * <li>{@link ExecutionInfo#getElapsedTime()} contains the elapsed time at the moment of the check, which is usually
 * close to the specified threshold rather than the query's actual execution time.
 * <li>The query result and success or failure status in {@link ExecutionInfo} are not available because the query
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
 *      public void afterQuery(ExecutionInfo execInfo, List&lt;QueryInfo&gt; queryInfoList) {
 *          if (execInfo.getElapsedTime() &gt;= thresholdInMillis) {
 *              super.afterQuery(execInfo, queryInfoList);
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
public class SlowQueryListener implements QueryExecutionListener {

    /**
     * Data holder for the currently running query.
     *
     * This structure prevents a hard reference from scheduled {@link Runnable} instances to {@link ExecutionInfo} and
     * related objects.
     */
    protected static class RunningQueryContext {
        protected ExecutionInfo executionInfo;
        protected List<QueryInfo> queryInfoList;
        protected long startTimeInMills;
        protected Stopwatch stopwatch;

        public RunningQueryContext(ExecutionInfo executionInfo, List<QueryInfo> queryInfoList, long nowInMills, Stopwatch stopwatch) {
            this.executionInfo = executionInfo;
            this.queryInfoList = queryInfoList;
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
    public void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {

        final String execInfoKey = getExecutionInfoKey(execInfo);

        // Pass only the key to prevent a hard reference from the Runnable to ExecutionInfo. (Issue-53)
        Runnable check = new Runnable() {
            @Override
            public void run() {
                // If the query is still in the map, it is still running.
                RunningQueryContext context = SlowQueryListener.this.inExecution.get(execInfoKey);

                if (context != null) {
                    long elapsedTime = context.stopwatch.getElapsedTime();
                    // Set the elapsed time.
                    if (context.executionInfo.getElapsedTime() == 0) {
                        context.executionInfo.setElapsedTime(elapsedTime);
                    }

                    onSlowQuery(context.executionInfo, context.queryInfoList, context.startTimeInMills);
                }
            }
        };
        this.executor.schedule(check, this.threshold, this.thresholdTimeUnit);

        long now = System.currentTimeMillis();
        Stopwatch stopwatch = this.stopwatchFactory.create().start();
        RunningQueryContext context = new RunningQueryContext(execInfo, queryInfoList, now, stopwatch);
        this.inExecution.put(execInfoKey, context);

    }

    @Override
    public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        String executionInfoKey = getExecutionInfoKey(execInfo);
        this.inExecution.remove(executionInfoKey);
    }


    /**
     * Calculates a key for the given {@link ExecutionInfo}.
     *
     * <p>This key is passed to the slow-query check {@link Runnable} and is also used for removal in
     * {@link #afterQuery(ExecutionInfo, List)}.
     *
     * <p>The default implementation uses {@link System#identityHashCode(Object)}. This does not guarantee 100% uniqueness,
     * but it is sufficient for the short-lived usage in this class.
     * <p>Subclasses may override this method to provide a different implementation that uniquely represents
     * {@link ExecutionInfo}.
     *
     * @param executionInfo execution info
     * @return key
     */
    protected String getExecutionInfoKey(ExecutionInfo executionInfo) {
        int exeInfoKey = System.identityHashCode(executionInfo);
        return String.valueOf(exeInfoKey);
    }

    /**
     * Callback invoked when query execution exceeds the threshold.
     *
     * <p>This callback is invoked only once per query if it exceeds the threshold.
     * It is invoked by a thread from the scheduled executor while the query is still running.
     * {@link ExecutionInfo#getElapsedTime()} contains the elapsed time at the moment of the check.
     *
     * @param execInfo        query execution info
     * @param queryInfoList   query parameter info
     * @param startTimeInMills time in milliseconds when the query started
     */
    protected void onSlowQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList, long startTimeInMills) {
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
     * Sets the {@link StopwatchFactory} used to compute {@link ExecutionInfo#getElapsedTime()} for slow queries.
     *
     * @param stopwatchFactory factory used to create a {@link Stopwatch} for slow-query timing
     * @since 1.5.1
     */
    public void setStopwatchFactory(StopwatchFactory stopwatchFactory) {
        this.stopwatchFactory = stopwatchFactory;
    }
}
