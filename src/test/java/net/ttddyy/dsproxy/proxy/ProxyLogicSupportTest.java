package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.listener.CallCheckMethodExecutionListener;
import net.ttddyy.dsproxy.listener.MethodExecutionContext;
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
public class ProxyLogicSupportTest {

    @Test
    public void proceedMethodExecution() throws Throwable {
        final Object target = new Object();
        final Object proxy = new Object();
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
                assertThat(executionContext.getProxy()).isSameAs(proxy);
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
                assertThat(executionContext.getProxy()).isSameAs(proxy);
            }
        };


        ProxyConfig proxyConfig = ProxyConfig.Builder.create().methodListener(listener).build();

        Custom custom = new Custom((proxyTarget, m, args) -> returnObj);
        Object result = custom.proceedMethodExecution(proxyConfig, target, connectionInfo, proxy, method, methodArgs);

        assertSame(returnObj, result);
        assertTrue(listener.isBeforeMethodCalled());
        assertTrue(listener.isAfterMethodCalled());

        MethodExecutionContext beforeMethodContext = listener.getBeforeMethodContext();
        MethodExecutionContext afterMethodContext = listener.getAfterMethodContext();

        assertSame("each method should be passed same context object", beforeMethodContext, afterMethodContext);
        assertThat(beforeMethodContext.getProxyConfig()).isSameAs(proxyConfig);
    }


    @Test
    public void proceedMethodExecutionWithException() throws Throwable {
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

        Custom custom = new Custom((proxyTarget, m, args) -> {
            throw exception;
        });

        // when callback throws exception
        Throwable thrownException = null;
        try {
            custom.proceedMethodExecution(proxyConfig, target, connectionInfo, null, method, methodArgs);
        } catch (Throwable throwable) {
            thrownException = throwable;
        }

        assertSame(exception, thrownException);
        assertTrue(listener.isBeforeMethodCalled());
        assertTrue(listener.isAfterMethodCalled());
    }

    @Test
    public void proceedMethodExecutionWithMethodAndParameterUpdate() throws Throwable {
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

        Custom custom = new Custom((proxyTarget, m, args) -> {
            invokedMethod.set(m);
            invokedMethodArgs.set(args);
            return null;
        });

        custom.proceedMethodExecution(proxyConfig, target, connectionInfo, null, method, methodArgs);

        assertThat(invokedMethod.get()).isSameAs(replacedMethod);
        assertThat(invokedMethodArgs.get()).isSameAs(replacedMethodArgs);
    }

    private static class Custom extends ProxyLogicSupport {

        private final CustomCallback callback;

        public Custom(CustomCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Object performProxyLogic(Object proxy, Method method, Object[] args, MethodExecutionContext methodContext) throws Throwable {
            return this.callback.invoke(proxy, method, args);
        }
    }

    private interface CustomCallback {
        Object invoke(Object proxyTarget, Method method, Object[] args) throws Exception;
    }

}

