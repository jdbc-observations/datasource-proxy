package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.proxy.ProxyConfig;
import org.junit.Test;

import java.lang.reflect.Method;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * @author Tadaya Tsuyukubo
 */
public class MethodExecutionListenerUtilsTest {

    @Test
    public void invokeNormal() throws Throwable {

        final Object target = new Object();
        final Method method = Statement.class.getMethod("getConnection");
        final Object[] methodArgs = new Object[]{};
        final Object returnObj = new Object();
        final ConnectionInfo connectionInfo = new ConnectionInfo();

        CallCheckMethodExecutionListener listener = new CallCheckMethodExecutionListener() {
            @Override
            public void beforeMethod(MethodExecutionContext executionContext) {
                super.beforeMethod(executionContext);

                assertThat(executionContext).isNotNull();
                assertThat(executionContext.getMethod()).isSameAs(method);
                assertThat(executionContext.getMethodArgs()).isSameAs(methodArgs);
                assertThat(executionContext.getTarget()).isSameAs(target);
                assertThat(executionContext.getElapsedTime()).isEqualTo(0);
                assertThat(executionContext.getThrown()).isNull();
                assertThat(executionContext.getResult()).isNull();

                assertThat(executionContext.getConnectionInfo()).isSameAs(connectionInfo);
                assertThat(executionContext.getProxyConfig()).isNotNull();
            }

            @Override
            public void afterMethod(MethodExecutionContext executionContext) {
                super.afterMethod(executionContext);

                assertThat(executionContext).isNotNull();
                assertThat(executionContext.getMethod()).isSameAs(method);
                assertThat(executionContext.getMethodArgs()).isSameAs(methodArgs);
                assertThat(executionContext.getTarget()).isSameAs(target);
                assertThat(executionContext.getElapsedTime()).isGreaterThanOrEqualTo(0);
                assertThat(executionContext.getThrown()).isNull();
                assertThat(executionContext.getResult()).isSameAs(returnObj);

                assertThat(executionContext.getConnectionInfo()).isSameAs(connectionInfo);
                assertThat(executionContext.getProxyConfig()).isNotNull();
            }
        };


        ProxyConfig proxyConfig = ProxyConfig.Builder.create().methodListener(listener).build();

        Object result = MethodExecutionListenerUtils.invoke(new MethodExecutionListenerUtils.MethodExecutionCallback() {
            @Override
            public Object execute(Object proxyTarget, Method method, Object[] args) throws Throwable {
                return returnObj;
            }
        }, proxyConfig, target, connectionInfo, method, methodArgs);

        assertSame(returnObj, result);
        assertTrue(listener.isBeforeMethodCalled());
        assertTrue(listener.isAfterMethodCalled());

        MethodExecutionContext beforeMethodContext = listener.getBeforeMethodContext();
        MethodExecutionContext afterMethodContext = listener.getAfterMethodContext();

        assertSame("each method should be passed same context object", beforeMethodContext, afterMethodContext);
        assertThat(beforeMethodContext.getProxyConfig()).isSameAs(proxyConfig);
    }

    @Test
    public void invokeWithException() throws Throwable {

        final Object target = new Object();
        final Method method = Statement.class.getMethod("getConnection");
        final Object[] methodArgs = new Object[]{};
        final Exception exception = new Exception();
        final ConnectionInfo connectionInfo = new ConnectionInfo();

        CallCheckMethodExecutionListener listener = new CallCheckMethodExecutionListener() {
            @Override
            public void beforeMethod(MethodExecutionContext executionContext) {
                super.beforeMethod(executionContext);

                assertThat(executionContext.getThrown()).isNull();
                assertThat(executionContext.getResult()).isNull();
            }

            @Override
            public void afterMethod(MethodExecutionContext executionContext) {
                super.afterMethod(executionContext);

                assertThat(executionContext.getThrown()).isSameAs(exception);
            }
        };


        ProxyConfig proxyConfig = ProxyConfig.Builder.create().methodListener(listener).build();

        // when callback throws exception
        Throwable thrownException = null;
        try {
            MethodExecutionListenerUtils.invoke(new MethodExecutionListenerUtils.MethodExecutionCallback() {
                @Override
                public Object execute(Object proxyTarget, Method method, Object[] args) throws Throwable {
                    throw exception;
                }
            }, proxyConfig, target, connectionInfo, method, methodArgs);
        } catch (Throwable throwable) {
            thrownException = throwable;
        }

        assertSame(exception, thrownException);
        assertTrue(listener.isBeforeMethodCalled());
        assertTrue(listener.isAfterMethodCalled());
    }

    @Test
    public void methodAndParameterUpdate() throws Throwable {
        final Object target = new Object();
        final Method method = Statement.class.getMethod("execute", String.class);
        final Object[] methodArgs = new Object[]{};
        final Method replacedMethod = Statement.class.getMethod("execute", String.class, int.class);
        final Object[] replacedMethodArgs = new Object[]{Statement.RETURN_GENERATED_KEYS};


        final ConnectionInfo connectionInfo = new ConnectionInfo();

        CallCheckMethodExecutionListener listener = new CallCheckMethodExecutionListener() {
            @Override
            public void beforeMethod(MethodExecutionContext executionContext) {
                super.beforeMethod(executionContext);

                // replace method and args
                executionContext.setMethod(replacedMethod);
                executionContext.setMethodArgs(replacedMethodArgs);
            }

            @Override
            public void afterMethod(MethodExecutionContext executionContext) {
                super.afterMethod(executionContext);

                assertThat(executionContext.getMethod()).isSameAs(replacedMethod);
                assertThat(executionContext.getMethodArgs()).isSameAs(replacedMethodArgs);
            }
        };


        ProxyConfig proxyConfig = ProxyConfig.Builder.create().methodListener(listener).build();

        final AtomicReference<Method> invokedMethod = new AtomicReference<Method>();
        final AtomicReference<Object[]> invokedMethodArgs = new AtomicReference<Object[]>();

        MethodExecutionListenerUtils.invoke(new MethodExecutionListenerUtils.MethodExecutionCallback() {
            @Override
            public Object execute(Object proxyTarget, Method method, Object[] args) throws Throwable {
                invokedMethod.set(method);
                invokedMethodArgs.set(args);
                return null;
            }
        }, proxyConfig, target, connectionInfo, method, methodArgs);

        assertThat(invokedMethod.get()).isSameAs(replacedMethod);
        assertThat(invokedMethodArgs.get()).isSameAs(replacedMethodArgs);

    }

}
