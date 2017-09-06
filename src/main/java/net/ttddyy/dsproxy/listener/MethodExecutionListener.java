package net.ttddyy.dsproxy.listener;

import java.lang.reflect.Method;

/**
 * Callback listener for JDBC API method invocations.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.3
 */
public interface MethodExecutionListener {

    MethodExecutionListener DEFAULT = new NoOpMethodExecutionListener();

    void beforeMethod(Object target, Method method, Object[] args);

    void afterMethod(Object target, Method method, Object[] args, Object result, Throwable thrown, long elapsedTime);

}
