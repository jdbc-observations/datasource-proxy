package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.proxy.ProxyConfig;
import net.ttddyy.dsproxy.proxy.Stopwatch;

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
                                Object proxyTarget, ConnectionInfo connectionInfo, Method method,
                                Object[] args) throws Throwable {

        MethodExecutionContext methodContext = MethodExecutionContext.Builder.create()
                .target(proxyTarget)
                .method(method)
                .methodArgs(args)
                .connectionInfo(connectionInfo)
                .proxyConfig(proxyConfig)
                .build();

        MethodExecutionListener methodExecutionListener = proxyConfig.getMethodListener();
        methodExecutionListener.beforeMethod(methodContext);

        // method and args may be replaced in MethodExecutionListener
        Method methodToInvoke = methodContext.getMethod();
        Object[] methodArgsToInvoke = methodContext.getMethodArgs();

        final Stopwatch stopwatch = proxyConfig.getStopwatchFactory().create().start();
        Object result = null;
        Throwable thrown = null;
        try {
            result = callback.execute(proxyTarget, methodToInvoke, methodArgsToInvoke);
        } catch (Throwable throwable) {
            thrown = throwable;
            throw throwable;
        } finally {
            final long elapsedTime = stopwatch.getElapsedTime();

            methodContext.setElapsedTime(elapsedTime);
            methodContext.setResult(result);
            methodContext.setThrown(thrown);

            methodExecutionListener.afterMethod(methodContext);
        }
        return result;
    }

}
