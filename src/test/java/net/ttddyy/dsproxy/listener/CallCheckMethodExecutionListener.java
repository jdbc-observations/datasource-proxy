package net.ttddyy.dsproxy.listener;

/**
 * @author Tadaya Tsuyukubo
 */
public class CallCheckMethodExecutionListener implements MethodExecutionListener {

    boolean isBeforeMethodCalled;
    boolean isAfterMethodCalled;
    MethodExecutionContext beforeMethodContext;
    MethodExecutionContext afterMethodContext;

    @Override
    public void beforeMethod(MethodExecutionContext executionContext) {
        this.isBeforeMethodCalled = true;
        this.beforeMethodContext = executionContext;
    }

    @Override
    public void afterMethod(MethodExecutionContext executionContext) {
        this.isAfterMethodCalled = true;
        this.afterMethodContext = executionContext;
    }

    public boolean isBeforeMethodCalled() {
        return isBeforeMethodCalled;
    }

    public boolean isAfterMethodCalled() {
        return isAfterMethodCalled;
    }

    public MethodExecutionContext getBeforeMethodContext() {
        return beforeMethodContext;
    }

    public MethodExecutionContext getAfterMethodContext() {
        return afterMethodContext;
    }

    public void reset() {
        this.isBeforeMethodCalled = false;
        this.isAfterMethodCalled = false;
        this.beforeMethodContext = null;
        this.afterMethodContext = null;
    }
}
