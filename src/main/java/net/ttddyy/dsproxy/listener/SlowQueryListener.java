package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.function.DSProxyConsumer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * Slow query detection listener.
 *
 * When query takes more than specified threshold, {@link #onSlowQuery} callback is called.
 * The callback is called only once for the target query if it exceeds the threshold time.
 *
 * NOTE:
 * {@link QueryExecutionContext#elapsedTime} contains the time when callback is triggered which usually is the specified threshold time.
 *
 * If you want to log or do something with AFTER execution that has exceeded specified threshold time, use normal
 * logging listener like following:
 * <pre>
 * {@code}
 * long thresholdInMills = ...
 * ProxyDataSourceListener listener = new ProxyDataSourceListenerAdapter() {
 *      {@literal @}Override
 *      public void afterQuery(QueryExecutionContext queryContext) {
 *          if (queryContext.getElapsedTime() >= thresholdInMills) {
 *              super.afterQuery(queryContext, queryInfoList);
 *          }
 *      }
 * };
 * {@code}
 * </pre>
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.1
 */
public class SlowQueryListener extends ProxyDataSourceListenerAdapter {

    /**
     * Data holder for currently running query.
     *
     * This structure is used to avoid hard reference from scheduled {@link Runnable} to {@link QueryExecutionContext} and etc.
     */
    protected static class RunningQueryContext {
        protected QueryExecutionContext queryExecutionContext;
        protected long startTimeInMills;

        public RunningQueryContext(QueryExecutionContext queryExecutionContext, long nowInMills) {
            this.queryExecutionContext = queryExecutionContext;
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
    protected DSProxyConsumer<QueryExecutionContext> onSlowQuery = executionContext -> {
    };

    public SlowQueryListener() {
    }

    public SlowQueryListener(long threshold, TimeUnit thresholdTimeUnit, DSProxyConsumer<QueryExecutionContext> onSlowQuery) {
        this.threshold = threshold;
        this.thresholdTimeUnit = thresholdTimeUnit;
        this.onSlowQuery = onSlowQuery;
    }

    @Override
    public void beforeQuery(QueryExecutionContext executionContext) {

        String queryContextKey = getExecutionContextKey(executionContext);

        // only pass the key to prevent hard reference from Runnable to QueryExecutionContext. (Issue-53)
        Runnable check = () -> {
            // if it's still in map, that means it's still running
            RunningQueryContext context = this.inExecution.get(queryContextKey);

            if (context != null) {
                // populate elapsed time
                if (context.queryExecutionContext.getElapsedTime() == 0) {
                    long elapsedTime = System.currentTimeMillis() - context.startTimeInMills;
                    context.queryExecutionContext.setElapsedTime(elapsedTime);
                }

                onSlowQuery(context.queryExecutionContext, context.startTimeInMills);
            }
        };
        this.executor.schedule(check, this.threshold, this.thresholdTimeUnit);

        long now = System.currentTimeMillis();
        RunningQueryContext context = new RunningQueryContext(executionContext, now);
        this.inExecution.put(queryContextKey, context);

    }

    @Override
    public void afterQuery(QueryExecutionContext executionContext) {
        String executionContextKey = getExecutionContextKey(executionContext);
        this.inExecution.remove(executionContextKey);
    }


    /**
     * Calculate a key for given {@link QueryExecutionContext}.
     *
     * <p>This key is passed to the slow query check {@link Runnable} as well as for removal in {@link #afterQuery(QueryExecutionContext)}.
     *
     * <p>Default implementation uses {@link System#identityHashCode(Object)}. This does NOT guarantee 100% of uniqueness; however, since
     * the current usage of the key is short lived and good enough for this use case.
     * <p>Subclass can override this method to provide different implementation to uniquely represent {@link QueryExecutionContext}.
     *
     * @param queryExecutionContext execution info
     * @return key
     */
    protected String getExecutionContextKey(QueryExecutionContext queryExecutionContext) {
        int exeInfoKey = System.identityHashCode(queryExecutionContext);
        return String.valueOf(exeInfoKey);
    }

    /**
     * Callback when query execution time exceeds the threshold.
     *
     * Subclass can override this method to add behavior.
     * This callback is called only once per query if it exceeds the threshold time.
     *
     * @param queryContext     query execution info
     * @param startTimeInMills time in mills when the query started
     */
    protected void onSlowQuery(QueryExecutionContext queryContext, long startTimeInMills) {
        // default implementation delegates the action to the consumer
        this.onSlowQuery.accept(queryContext);
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
    public void setOnSlowQuery(DSProxyConsumer<QueryExecutionContext> onSlowQuery) {
        this.onSlowQuery = onSlowQuery;
    }
}
