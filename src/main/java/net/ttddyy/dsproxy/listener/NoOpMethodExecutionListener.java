package net.ttddyy.dsproxy.listener;

/**
 * No-op implementation of {@link MethodExecutionListener}
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.3
 */
public class NoOpMethodExecutionListener implements MethodExecutionListener {

    @Override
    public void beforeMethod(MethodExecutionContext executionContext) {
        // no-op
    }

    @Override
    public void afterMethod(MethodExecutionContext executionContext) {
        // no-op
    }

}
