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
 * When query takes more than specified threshold, {@link #onSlowQuery(ExecutionInfo, List, long)} callback method
 * is called while the query is still running. The callback is called only once for the target query if it exceeds
 * the threshold time. The running query is NOT interrupted or cancelled.
 *
 * <p>NOTE:
 * <ul>
 * <li>The callback is performed by a thread of the scheduled executor in this listener, not by the thread that
 * executes the query.
 * <li>{@link ExecutionInfo#getElapsedTime()} contains the time at the moment of the check, which usually is about the
 * specified threshold time, not the actual execution time of the query.
 * <li>Query result and success/failure in {@link ExecutionInfo} are not available, since the query has not finished.
 * <li>A check is scheduled for each query execution.
 * </ul>
 *
 * <p>If you want to log or do something <em>after execution</em> (sometimes called <em>non-preemptive</em>) for
 * queries that have exceeded specified threshold time, use normal logging listener like following:
 * <pre>
 * long thresholdInMills = ...
 * SLF4JQueryLoggingListener listener = new SLF4JQueryLoggingListener(){
 *      {@literal @}Override
 *      public void afterQuery(ExecutionInfo execInfo, List&lt;QueryInfo&gt; queryInfoList) {
 *          if (execInfo.getElapsedTime() &gt;= thresholdInMills) {
 *              super.afterQuery(execInfo, queryInfoList);
 *          }
 *      }
 * };
 * </pre>
 * This way, actual query execution time is available, but queries that never return are not detected.
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
     * Data holder for currently running query.
     *
     * This structure is used to avoid hard reference from scheduled {@link Runnable} to {@link ExecutionInfo} and etc.
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

        // only pass the key to prevent hard reference from Runnable to ExecutionInfo. (Issue-53)
        Runnable check = new Runnable() {
            @Override
            public void run() {
                // if it's still in map, that means it's still running
                RunningQueryContext context = SlowQueryListener.this.inExecution.get(execInfoKey);

                if (context != null) {
                    long elapsedTime = context.stopwatch.getElapsedTime();
                    // populate elapsed time
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
     * Calculate a key for given {@link ExecutionInfo}.
     *
     * <p>This key is passed to the slow query check {@link Runnable} as well as for removal in {@link #afterQuery(ExecutionInfo, List)}.
     *
     * <p>Default implementation uses {@link System#identityHashCode(Object)}. This does NOT guarantee 100% of uniqueness; however, since
     * the current usage of the key is short lived and good enough for this use case.
     * <p>Subclass can override this method to provide different implementation to uniquely represent {@link ExecutionInfo}.
     *
     * @param executionInfo execution info
     * @return key
     */
    protected String getExecutionInfoKey(ExecutionInfo executionInfo) {
        int exeInfoKey = System.identityHashCode(executionInfo);
        return String.valueOf(exeInfoKey);
    }

    /**
     * Callback when query execution time exceeds the threshold.
     *
     * This callback is called only once per query if it exceeds the threshold time.
     * It is called by a thread of the scheduled executor while the query is still running.
     * {@link ExecutionInfo#getElapsedTime()} contains the time at the moment of the check.
     *
     * @param execInfo        query execution info
     * @param queryInfoList    query parameter info
     * @param startTimeInMills time in mills when the query started
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
     * When set to {@code true}(default), the executor creates daemon threads to check slow queries.
     *
     * @param useDaemonThread use daemon thread or not. (default is true)
     * @since 1.4.2
     */
    public void setUseDaemonThread(boolean useDaemonThread) {
        this.useDaemonThread = useDaemonThread;
    }


    /**
     * Set {@link StopwatchFactory} which is used to calculate {@link ExecutionInfo#getElapsedTime()} for slow queries.
     *
     * @param stopwatchFactory factory to create {@link Stopwatch} used for calculating elapsed time for slow queries
     * @since 1.5.1
     */
    public void setStopwatchFactory(StopwatchFactory stopwatchFactory) {
        this.stopwatchFactory = stopwatchFactory;
    }
}
