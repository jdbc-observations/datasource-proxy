package net.ttddyy.dsproxy.listener;

import java.lang.reflect.Method;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4.3
 */
public interface MethodExecutionListener {
    MethodExecutionListener DEFAULT = new MethodExecutionListener() {
        @Override
        public void beforeMethod(Object target, Method method, Object[] args) {
            // no-op
        }

        @Override
        public void afterMethod(Object target, Method method, Object[] args, Object result, Throwable thrown, long elapsedTime) {
            // no-op
        }
    };

    void beforeMethod(Object target, Method method, Object[] args);

    void afterMethod(Object target, Method method, Object[] args, Object result, Throwable thrown, long elapsedTime);

}
