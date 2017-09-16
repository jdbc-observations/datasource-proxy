package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.proxy.ProxyConfig;

import java.lang.reflect.Method;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4.3
 */
public class MethodExecutionListenerUtils {

    public interface MethodExecutionCallback {
        Object execute(Object proxyTarget, Method method, Object[] args) throws Throwable;
    }

    public static Object invoke(MethodExecutionCallback callback, ProxyConfig proxyConfig,
                                Object proxyTarget, Method method, Object[] args) throws Throwable {

        MethodExecutionContext methodContext = MethodExecutionContext.Builder.create()
                .target(proxyTarget)
                .method(method)
                .methodArgs(args)
                .proxyConfig(proxyConfig)
                .build();

        MethodExecutionListener methodExecutionListener = proxyConfig.getMethodListener();
        methodExecutionListener.beforeMethod(methodContext);

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
            long elapsedTime = afterTime - beforeTime;

            methodContext.setElapsedTime(elapsedTime);
            methodContext.setResult(result);
            methodContext.setThrown(thrown);

            methodExecutionListener.afterMethod(methodContext);
        }
        return result;
    }

}
