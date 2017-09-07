package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.proxy.InterceptorHolder;

import java.lang.reflect.Method;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4.3
 */
public class MethodExecutionListenerUtils {

    public interface MethodExecutionCallback {
        Object execute(Object proxyTarget, Method method, Object[] args) throws Throwable;
    }

    public static Object invoke(MethodExecutionCallback callback, InterceptorHolder interceptorHolder,
                                Object proxyTarget, Method method, Object[] args) throws Throwable {

        MethodExecutionListener methodExecutionListener = interceptorHolder.getMethodListener();

        MethodExecutionContext methodContext = new MethodExecutionContext();



        methodExecutionListener.beforeMethod(proxyTarget, method, args);

        final long beforeTime = System.currentTimeMillis();
        Object result = null;
        Throwable thrown = null;
        try {
            result = callback.execute(proxyTarget, method, args);
        } catch (Throwable throwable) {
            thrown = throwable;
            throw throwable;
        } finally {
            final long afterTime = System.currentTimeMillis();
            methodExecutionListener.afterMethod(proxyTarget, method, args, result, thrown, afterTime - beforeTime);
        }
        return result;
    }

}
