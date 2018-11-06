package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.ExecutionInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


/**
 * Slow query detection listener.
 *
 * When query takes more than specified threshold, {@link #onSlowQuery} callback is called.
 * The callback is called only once for the target query if it exceeds the threshold time.
 *
 * NOTE:
 * {@link ExecutionInfo#elapsedTime} contains the time when callback is triggered which usually is the specified threshold time.
 *
 * If you want to log or do something with AFTER execution that has exceeded specified threshold time, use normal
 * logging listener like following:
 * <pre>
 * {@code}
 * long thresholdInMills = ...
 * ProxyDataSourceListener listener = new ProxyDataSourceListener(){
 *      {@literal @}Override
 *      public void afterQuery(ExecutionInfo execInfo) {
 *          if (execInfo.getElapsedTime() >= thresholdInMills) {
 *              super.afterQuery(execInfo, queryInfoList);
 *          }
 *      }
 * };
 * {@code}
 * </pre>
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.1
 */
public class SlowQueryListener implements ProxyDataSourceListener {

    /**
     * Data holder for currently running query.
     *
     * This structure is used to avoid hard reference from scheduled {@link Runnable} to {@link ExecutionInfo} and etc.
     */
    protected static class RunningQueryContext {
        protected ExecutionInfo executionInfo;
        protected long startTimeInMills;

        public RunningQueryContext(ExecutionInfo executionInfo, long nowInMills) {
            this.executionInfo = executionInfo;
            this.startTimeInMills = nowInMills;
        }
    }

    protected boolean useDaemonThread = true;

    protected ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor((r) -> {
        Thread thread = Executors.defaultThreadFactory().newThread(r);
        thread.setDaemon(SlowQueryListener.this.useDaemonThread);
        return thread;
    });
    protected long threshold;
    protected TimeUnit thresholdTimeUnit;

    protected Map<String, RunningQueryContext> inExecution = new ConcurrentHashMap<>();

    // Callback when query execution time exceeds the threshold.
    // This callback is called only once per query if it exceeds the threshold time.
    protected Consumer<ExecutionInfo> onSlowQuery = executionInfo -> {
    };

    public SlowQueryListener() {
    }

    public SlowQueryListener(long threshold, TimeUnit thresholdTimeUnit, Consumer<ExecutionInfo> onSlowQuery) {
        this.threshold = threshold;
        this.thresholdTimeUnit = thresholdTimeUnit;
        this.onSlowQuery = onSlowQuery;
    }

    @Override
    public void beforeQuery(ExecutionInfo execInfo) {

        String execInfoKey = getExecutionInfoKey(execInfo);

        // only pass the key to prevent hard reference from Runnable to ExecutionInfo. (Issue-53)
        Runnable check = () -> {
            // if it's still in map, that means it's still running
            RunningQueryContext context = this.inExecution.get(execInfoKey);

            if (context != null) {
                // populate elapsed time
                if (context.executionInfo.getElapsedTime() == 0) {
                    long elapsedTime = System.currentTimeMillis() - context.startTimeInMills;
                    context.executionInfo.setElapsedTime(elapsedTime);
                }

                this.onSlowQuery.accept(context.executionInfo);
            }
        };
        this.executor.schedule(check, this.threshold, this.thresholdTimeUnit);

        long now = System.currentTimeMillis();
        RunningQueryContext context = new RunningQueryContext(execInfo, now);
        this.inExecution.put(execInfoKey, context);

    }

    @Override
    public void afterQuery(ExecutionInfo execInfo) {
        String executionInfoKey = getExecutionInfoKey(execInfo);
        this.inExecution.remove(executionInfoKey);
    }


    /**
     * Calculate a key for given {@link ExecutionInfo}.
     *
     * <p>This key is passed to the slow query check {@link Runnable} as well as for removal in {@link #afterQuery(ExecutionInfo)}.
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
     * Callback when query execution time exceeds the threshold.
     *
     * @param onSlowQuery a consumer that is called only once per query if it exceeds the threshold time.
     * @since 2.0
     */
    public void setOnSlowQuery(Consumer<ExecutionInfo> onSlowQuery) {
        this.onSlowQuery = onSlowQuery;
    }
}
