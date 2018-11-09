package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.listener.MethodExecutionContext;
import net.ttddyy.dsproxy.listener.ProxyDataSourceListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.sql.Wrapper;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Tadaya Tsuyukubo
 * @since 2.0
 */
public abstract class CallbackSupport implements ProxyLogic {

    protected static final Set<String> WRAPPER_METHODS = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList("unwrap", "isWrapperFor"))
    );

    protected static final String TO_STRING_METHOD = "toString";
    protected static final String GET_TARGET_METHOD = "getTarget";


    // handle wrapper methods
    protected boolean isWrapperMethods(String methodName) {
        return WRAPPER_METHODS.contains(methodName);
    }

    /**
     * Handling for {@link Wrapper#unwrap(Class)} and {@link Wrapper#isWrapperFor(Class)}.
     */
    protected Object handleWrapperMethods(String methodName, Wrapper wrapper, Object[] args) throws SQLException {
        final Class<?> clazz = (Class<?>) args[0];
        if ("unwrap".equals(methodName)) {
            return wrapper.unwrap(clazz);
        } else {
            return wrapper.isWrapperFor(clazz);
        }
    }

    protected boolean isToStringMethod(String methodName) {
        return TO_STRING_METHOD.contains(methodName);
    }

    protected String handleToStringMethod(Object target) {
        final StringBuilder sb = new StringBuilder();
        sb.append(target.getClass().getSimpleName());
        sb.append(" [");
        sb.append(target.toString());
        sb.append("]");
        return sb.toString(); // differentiate toString message.
    }

    /**
     * {@link ProxyJdbcObject} interface has a method to return original object.
     */
    protected boolean isGetTargetMethod(String methodName) {
        return GET_TARGET_METHOD.contains(methodName);
    }


    /**
     * Invoke the method on target object.
     */
    protected Object proceedExecution(Method method, Object target, Object[] args) throws Throwable {
        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        }
    }

    /**
     * Populate {@link MethodExecutionContext} and calls before/after method callback.
     */
    protected Object proceedMethodExecution(ProxyConfig proxyConfig, Object proxyTarget, ConnectionInfo connectionInfo,
                                            Object proxy, Method method, Object[] args) throws Throwable {

        MethodExecutionContext methodContext = MethodExecutionContext.Builder.create()
                .target(proxyTarget)
                .method(method)
                .methodArgs(args)
                .connectionInfo(connectionInfo)
                .proxyConfig(proxyConfig)
                .build();

        ProxyDataSourceListener methodExecutionListener = proxyConfig.getListeners();
        methodExecutionListener.beforeMethod(methodContext);

        // method and args may be replaced in MethodExecutionListener
        Method methodToInvoke = methodContext.getMethod();
        Object[] methodArgsToInvoke = methodContext.getMethodArgs();

        long beforeTime = System.currentTimeMillis();
        Object result = null;
        Throwable thrown = null;
        try {
            result = performProxyLogic(proxy, methodToInvoke, methodArgsToInvoke, methodContext);
        } catch (Throwable throwable) {
            thrown = throwable;
            throw throwable;
        } finally {
            long afterTime = System.currentTimeMillis();
            long elapsedTime = afterTime - beforeTime;

            methodContext.setElapsedTime(elapsedTime);
            methodContext.setResult(result);
            methodContext.setThrown(thrown);

            methodExecutionListener.afterMethod(methodContext);
        }
        return result;
    }


    protected abstract Object performProxyLogic(Object proxy, Method method, Object[] args,
                                                MethodExecutionContext methodContext) throws Throwable;

}
