package net.ttddyy.dsproxy.listener;

import java.lang.reflect.Method;

/**
 * @author Tadaya Tsuyukubo
 */
public class MethodExecutionContext {
    private Object target;
    private Method method;
    private Object[] methodArgs;
    private Throwable thrown;
    private long elapsedTime;
}
