package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.ExecutionInfo;

/**
 * Keep the last invoked execution.
 *
 * Used for validating last execution.
 *
 * @author Tadaya Tsuyukubo
 * @since 2.0
 */
public class LastExecutionAwareListener implements ProxyDataSourceListener {

    private MethodExecutionContext beforeMethodContext;
    private MethodExecutionContext afterMethodContext;
    private ExecutionInfo beforeQueryContext;
    private ExecutionInfo afterQueryContext;

    @Override
    public void beforeMethod(MethodExecutionContext executionContext) {
        this.beforeMethodContext = executionContext;
    }

    @Override
    public void afterMethod(MethodExecutionContext executionContext) {
        this.afterMethodContext = executionContext;
    }

    @Override
    public void beforeQuery(ExecutionInfo execInfo) {
        this.beforeQueryContext = execInfo;
    }

    @Override
    public void afterQuery(ExecutionInfo execInfo) {
        this.afterQueryContext = execInfo;
    }

    public boolean isBeforeMethodCalled() {
        return this.beforeMethodContext != null;
    }

    public boolean isAfterMethodCalled() {
        return this.afterMethodContext != null;
    }

    public boolean isBeforeQueryCalled() {
        return this.beforeQueryContext != null;
    }

    public boolean isAfterQueryCalled() {
        return this.afterQueryContext != null;
    }

    public MethodExecutionContext getBeforeMethodContext() {
        return beforeMethodContext;
    }

    public MethodExecutionContext getAfterMethodContext() {
        return afterMethodContext;
    }

    public ExecutionInfo getBeforeQueryContext() {
        return beforeQueryContext;
    }

    public ExecutionInfo getAfterQueryContext() {
        return afterQueryContext;
    }

    public void reset() {
        this.beforeMethodContext = null;
        this.afterMethodContext = null;
        this.beforeQueryContext = null;
        this.afterQueryContext = null;
    }

}
