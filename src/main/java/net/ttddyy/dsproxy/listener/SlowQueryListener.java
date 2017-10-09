package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;

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
 * When query takes more than specified threshold, {@link #onSlowQuery(ExecutionInfo, List, long)} callback method
 * is called. The callback is called only once for the target query if it exceeds the threshold time.
 *
 * NOTE:
 * {@link ExecutionInfo#elapsedTime} contains the time when callback is triggered which usually is the specified threshold time.
 *
 * If you want to log or do something with AFTER execution that has exceeded specified threshold time, use normal
 * logging listener like following:
 * <pre>
 * {@code}
 * long thresholdInMills = ...
 * SLF4JQueryLoggingListener listener = new SLF4JQueryLoggingListener(){
 *      {@literal @}Override
 *      public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
 *          if (execInfo.getElapsedTime() >= thresholdInMills) {
 *              super.afterQuery(execInfo, queryInfoList);
 *          }
 *      }
 * };
 * {@code}
 * </pre>
 *
 * @author Tadaya Tsuyukubo
 * @see net.ttddyy.dsproxy.listener.logging.CommonsSlowQueryListener
 * @see net.ttddyy.dsproxy.listener.logging.JULSlowQueryListener
 * @see net.ttddyy.dsproxy.listener.logging.SLF4JSlowQueryListener
 * @see net.ttddyy.dsproxy.listener.logging.SystemOutSlowQueryListener
 * @since 1.4.1
 */
public class SlowQueryListener implements QueryExecutionListener {

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
    protected Map<ExecutionInfo, Long> inExecution = new ConcurrentHashMap<ExecutionInfo, Long>();

    @Override
    public void beforeQuery(final ExecutionInfo execInfo, final List<QueryInfo> queryInfoList) {

        Runnable check = new Runnable() {
            @Override
            public void run() {
                // if it's still in map, that means it's still running
                Long startTimeInMills = inExecution.get(execInfo);
                if (startTimeInMills != null) {
                    // populate elapsed time
                    if (execInfo.getElapsedTime() == 0) {
                        long elapsedTime = System.currentTimeMillis() - startTimeInMills;
                        execInfo.setElapsedTime(elapsedTime);
                    }

                    onSlowQuery(execInfo, queryInfoList, startTimeInMills);
                }
            }
        };
        this.executor.schedule(check, this.threshold, this.thresholdTimeUnit);

        long now = System.currentTimeMillis();
        this.inExecution.put(execInfo, now);
    }

    @Override
    public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        this.inExecution.remove(execInfo);
    }


    /**
     * Callback when query execution time exceeds the threshold.
     *
     * This callback is called only once per query if it exceeds the threshold time.
     *
     * @param execInfo         query execution info
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

}
