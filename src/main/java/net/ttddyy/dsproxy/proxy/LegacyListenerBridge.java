package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.listener.MethodExecutionContext;
import net.ttddyy.dsproxy.listener.MethodExecutionListener;
import net.ttddyy.dsproxy.listener.ProxyDataSourceListener;
import net.ttddyy.dsproxy.listener.QueryExecutionContext;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;

import java.time.Duration;

// shortcut: temporary 1.x listener bridge, removed in Task 7 (Phase 2)

/**
 * Internal adapter that runs a 1.x {@link QueryExecutionListener} or {@link MethodExecutionListener} as a
 * {@link ProxyDataSourceListener}. Not part of the public API.
 *
 * <p>All bridged query listeners of one execution share a single {@link ExecutionInfo}, as the 1.x chain did, and its
 * custom values are the context's custom values.
 */
public final class LegacyListenerBridge implements ProxyDataSourceListener {

    private static final String EXECUTION_INFO_KEY = LegacyListenerBridge.class.getName() + ".executionInfo";

    private final QueryExecutionListener queryListener;
    private final MethodExecutionListener methodListener;

    private LegacyListenerBridge(QueryExecutionListener queryListener, MethodExecutionListener methodListener) {
        this.queryListener = queryListener;
        this.methodListener = methodListener;
    }

    public static ProxyDataSourceListener ofQuery(QueryExecutionListener listener) {
        return new LegacyListenerBridge(listener, null);
    }

    public static ProxyDataSourceListener ofMethod(MethodExecutionListener listener) {
        return new LegacyListenerBridge(null, listener);
    }

    public QueryExecutionListener getQueryListener() {
        return this.queryListener;
    }

    public MethodExecutionListener getMethodListener() {
        return this.methodListener;
    }

    /**
     * Converts to the 1.x {@code long} elapsed time, whose unit depended on the stopwatch factory.
     */
    public static long toLegacyElapsedTime(Duration elapsedTime, StopwatchFactory stopwatchFactory) {
        return stopwatchFactory instanceof NanoTimeStopwatchFactory ? elapsedTime.toNanos() : elapsedTime.toMillis();
    }

    @Override
    public void beforeQuery(QueryExecutionContext context) {
        if (this.queryListener != null) {
            this.queryListener.beforeQuery(executionInfo(context), context.getQueries());
        }
    }

    @Override
    public void afterQuery(QueryExecutionContext context) {
        if (this.queryListener == null) {
            return;
        }
        BridgedExecutionInfo info = executionInfo(context);
        if (!info.afterPopulated) {  // populate once so later listeners see earlier listeners' changes, as in 1.x
            info.afterPopulated = true;
            StopwatchFactory factory = context instanceof MutableQueryExecutionContext
                    ? ((MutableQueryExecutionContext) context).stopwatchFactory : null;
            info.setElapsedTime(toLegacyElapsedTime(context.getElapsedTime(), factory));
            info.setSuccess(context.isSuccess());
            if (context.isSuccess()) {
                info.setResult(context.getResult());
                info.setGeneratedKeys(context.getGeneratedKeys());
            } else {
                info.setThrowable(context.getThrowable());
            }
        }
        this.queryListener.afterQuery(info, context.getQueries());
    }

    @Override
    public void beforeMethod(MethodExecutionContext context) {
        if (this.methodListener != null) {
            this.methodListener.beforeMethod(context);
        }
    }

    @Override
    public void afterMethod(MethodExecutionContext context) {
        if (this.methodListener != null) {
            this.methodListener.afterMethod(context);
        }
    }

    private static BridgedExecutionInfo executionInfo(QueryExecutionContext context) {
        BridgedExecutionInfo info = context.getCustomValue(EXECUTION_INFO_KEY, BridgedExecutionInfo.class);
        if (info == null) {
            info = new BridgedExecutionInfo(context);
            context.addCustomValue(EXECUTION_INFO_KEY, info);
        }
        return info;
    }

    private static final class BridgedExecutionInfo extends ExecutionInfo {

        private final QueryExecutionContext context;
        private boolean afterPopulated;

        BridgedExecutionInfo(QueryExecutionContext context) {
            this.context = context;
            setDataSourceName(context.getDataSourceName());
            setConnectionId(context.getConnectionId());
            setIsolationLevel(context.getIsolationLevel());
            setStatement(context.getStatement());
            setStatementType(context.getStatementType());
            setBatch(context.isBatch());
            setBatchSize(context.getBatchSize());
            setMethod(context.getMethod());
            setMethodArgs(context.getMethodArgs());
        }

        @Override
        public void addCustomValue(String key, Object value) {
            this.context.addCustomValue(key, value);
        }

        @Override
        public <T> T getCustomValue(String key, Class<T> type) {
            return this.context.getCustomValue(key, type);
        }
    }

}
