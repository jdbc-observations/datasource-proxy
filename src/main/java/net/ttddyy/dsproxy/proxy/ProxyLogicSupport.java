package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.listener.MethodExecutionContext;
import net.ttddyy.dsproxy.listener.ProxyDataSourceListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.sql.Wrapper;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Parent class for all proxy logic.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.10
 */
public abstract class ProxyLogicSupport {

    protected static final Set<String> COMMON_METHOD_NAMES = Collections.unmodifiableSet(
            new HashSet<String>(Arrays.asList(
                    "getTarget", "getProxyConfig",
                    "getDataSourceName",
                    "unwrap", "isWrapperFor",
                    "toString", "equals", "hashCode"))
    );


    protected boolean isCommonMethod(String methodName) {
        return COMMON_METHOD_NAMES.contains(methodName);
    }

    protected Object handleCommonMethod(String methodName, Object original, ProxyConfig proxyConfig, Object[] args) throws SQLException {
        if ("toString".equals(methodName)) {
            // special treat for toString method
            final StringBuilder sb = new StringBuilder();
            sb.append(original.getClass().getSimpleName());
            sb.append(" [");
            sb.append(original);
            sb.append("]");
            return sb.toString(); // differentiate toString message.
        } else if ("equals".equals(methodName)) {
            // when target is a proxy, also compares the proxied object
            return original.equals(args[0]) || ((args[0] instanceof ProxyJdbcObject) && (original.equals(((ProxyJdbcObject)args[0]).getTarget())));
        } else if ("hashCode".equals(methodName)) {
            return original.hashCode();
        } else if ("getDataSourceName".equals(methodName)) {
            return proxyConfig.getDataSourceName();
        } else if ("getTarget".equals(methodName)) {
            return original;  // ProxyJdbcObject interface has a method to return original object.
        } else if ("getProxyConfig".equals(methodName)) {
            return proxyConfig;
        } else if ("unwrap".equals(methodName)) {
            final Class<?> clazz = (Class<?>) args[0];
            return ((Wrapper) original).unwrap(clazz);
        } else if ("isWrapperFor".equals(methodName)) {
            final Class<?> clazz = (Class<?>) args[0];
            return ((Wrapper) original).isWrapperFor(clazz);
        }
        throw new IllegalStateException(methodName + " does not match with " + COMMON_METHOD_NAMES);
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
     * Populates {@link MethodExecutionContext} and invokes the before- and after-method callbacks.
     */
    protected Object proceedMethodExecution(ProxyConfig proxyConfig, Object original, ConnectionInfo connectionInfo,
                                            Object proxy, Method method, Object[] args) throws Throwable {
        MethodExecutionContext methodContext = MethodExecutionContext.Builder.create()
                .target(original)
                .proxy(proxy)
                .method(method)
                .methodArgs(args)
                .connectionInfo(connectionInfo)
                .proxyConfig(proxyConfig)
                .build();

        ProxyDataSourceListener listener = proxyConfig.getListener();
        listener.beforeMethod(methodContext);

        // method and args may be replaced in beforeMethod
        Method methodToInvoke = methodContext.getMethod();
        Object[] methodArgsToInvoke = methodContext.getMethodArgs();

        final long startNanos = System.nanoTime();
        Object result = null;
        Throwable thrown = null;
        try {
            result = performProxyLogic(proxy, methodToInvoke, methodArgsToInvoke, methodContext);
        } catch (Throwable throwable) {
            thrown = throwable;
            throw throwable;
        } finally {
            methodContext.setElapsedTime(Duration.ofNanos(System.nanoTime() - startNanos));
            methodContext.setResult(result);
            methodContext.setThrown(thrown);

            try {
                listener.afterMethod(methodContext);
            } catch (Throwable listenerException) {
                if (thrown == null) {
                    throw listenerException;
                }
                // keep the JDBC exception; the listener's exception must not hide it
                if (listenerException != thrown) {
                    thrown.addSuppressed(listenerException);
                }
            }
        }
        return result;
    }


    protected abstract Object performProxyLogic(Object proxy, Method method, Object[] args,
                                                MethodExecutionContext methodContext) throws Throwable;

}
