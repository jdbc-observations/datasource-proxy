package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.proxy.ProxyConfig;

import java.lang.reflect.Method;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4.3
 */
public class MethodExecutionListenerUtils {

    @FunctionalInterface
    public interface MethodExecutionCallback {
        Object execute(Object proxyTarget, Method targetMethod, Object[] targetArgs) throws Throwable;
    }

    public static Object invoke(MethodExecutionCallback callback, ProxyConfig proxyConfig,
                                Object proxyTarget, ConnectionInfo connectionInfo, Method method,
                                Object[] args) throws Throwable {

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
            result = callback.execute(proxyTarget, methodToInvoke, methodArgsToInvoke);
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

}
