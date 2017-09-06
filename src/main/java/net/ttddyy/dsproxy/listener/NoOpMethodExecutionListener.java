package net.ttddyy.dsproxy.listener;

import java.lang.reflect.Method;

/**
 * No-op implementation of {@link MethodExecutionListener}
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.3
 */
public class NoOpMethodExecutionListener implements MethodExecutionListener {

    @Override
    public void beforeMethod(Object target, Method method, Object[] args) {
        // no-op
    }

    @Override
    public void afterMethod(Object target, Method method, Object[] args, Object result, Throwable thrown, long elapsedTime) {
        // no-op
    }

}
