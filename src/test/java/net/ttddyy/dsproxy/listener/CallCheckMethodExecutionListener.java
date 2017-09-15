package net.ttddyy.dsproxy.listener;

/**
 * @author Tadaya Tsuyukubo
 */
public class CallCheckMethodExecutionListener implements MethodExecutionListener {

    boolean isBeforeMethodCalled;
    boolean isAfterMethodCalled;

    @Override
    public void beforeMethod(MethodExecutionContext executionContext) {
        this.isBeforeMethodCalled = true;
    }

    @Override
    public void afterMethod(MethodExecutionContext executionContext) {
        this.isAfterMethodCalled = true;
    }

    public boolean isBeforeMethodCalled() {
        return isBeforeMethodCalled;
    }

    public boolean isAfterMethodCalled() {
        return isAfterMethodCalled;
    }

    public void reset() {
        this.isBeforeMethodCalled = false;
        this.isAfterMethodCalled = false;
    }
}
