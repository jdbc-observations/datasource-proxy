package net.ttddyy.dsproxy.listener;

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
    private QueryExecutionContext beforeQueryContext;
    private QueryExecutionContext afterQueryContext;

    @Override
    public void beforeMethod(MethodExecutionContext executionContext) {
        this.beforeMethodContext = executionContext;
    }

    @Override
    public void afterMethod(MethodExecutionContext executionContext) {
        this.afterMethodContext = executionContext;
    }

    @Override
    public void beforeQuery(QueryExecutionContext executionContext) {
        this.beforeQueryContext = executionContext;
    }

    @Override
    public void afterQuery(QueryExecutionContext executionContext) {
        this.afterQueryContext = executionContext;
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

    public QueryExecutionContext getBeforeQueryContext() {
        return beforeQueryContext;
    }

    public QueryExecutionContext getAfterQueryContext() {
        return afterQueryContext;
    }

    public void reset() {
        this.beforeMethodContext = null;
        this.afterMethodContext = null;
        this.beforeQueryContext = null;
        this.afterQueryContext = null;
    }

}
