package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.listener.LastExecutionAwareListener;
import net.ttddyy.dsproxy.listener.MethodExecutionContext;
import net.ttddyy.dsproxy.listener.ProxyDataSourceListener;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Tadaya Tsuyukubo
 */
public class ProxyLogicSupportProceedMethodExecutionTest {

    private ProxyLogicSupport proxyLogicSupport = spy(ProxyLogicSupport.class);

    @Test
    public void invokeNormal() throws Throwable {

        final Object target = new Object();
        final Method method = Statement.class.getMethod("getConnection");
        final Object[] methodArgs = new Object[]{};
        final Object returnObj = new Object();
        final ConnectionInfo connectionInfo = new ConnectionInfo();

        // since no concurrency change in this test
        Thread currentThread = Thread.currentThread();
        long threadId = currentThread.getId();
        String threadName = currentThread.getName();

        LastExecutionAwareListener listener = new LastExecutionAwareListener() {
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

                assertThat(executionContext.getThreadId()).isEqualTo(threadId);
                assertThat(executionContext.getThreadName()).isEqualTo(threadName);
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

                assertThat(executionContext.getThreadId()).isEqualTo(threadId);
                assertThat(executionContext.getThreadName()).isEqualTo(threadName);
            }
        };


        ProxyConfig proxyConfig = ProxyConfig.Builder.create().listener(listener).build();

        when(this.proxyLogicSupport.performProxyLogic(any(), any(), any(), any())).thenReturn(returnObj);

        Object result = this.proxyLogicSupport.proceedMethodExecution(proxyConfig, target, connectionInfo, null, method, methodArgs);

        verify(this.proxyLogicSupport).performProxyLogic(any(), any(), any(), any());

        assertSame(returnObj, result);
        assertTrue(listener.isBeforeMethodCalled());
        assertTrue(listener.isAfterMethodCalled());

        MethodExecutionContext beforeMethodContext = listener.getBeforeMethodContext();
        MethodExecutionContext afterMethodContext = listener.getAfterMethodContext();

        assertSame(beforeMethodContext, afterMethodContext, "each method should be passed same context object");
        assertThat(beforeMethodContext.getProxyConfig()).isSameAs(proxyConfig);
    }

    @Test
    public void invokeWithException() throws Throwable {

        final Object target = new Object();
        final Method method = Statement.class.getMethod("getConnection");
        final Object[] methodArgs = new Object[]{};
        final Exception exception = new Exception();
        final ConnectionInfo connectionInfo = new ConnectionInfo();

        LastExecutionAwareListener listener = new LastExecutionAwareListener() {
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


        ProxyConfig proxyConfig = ProxyConfig.Builder.create().listener(listener).build();

        when(this.proxyLogicSupport.performProxyLogic(any(), any(), any(), any())).thenThrow(exception);

        // when callback throws exception
        Throwable thrownException = null;
        try {
            this.proxyLogicSupport.proceedMethodExecution(proxyConfig, target, connectionInfo, null, method, methodArgs);

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

        LastExecutionAwareListener listener = new LastExecutionAwareListener() {
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


        ProxyConfig proxyConfig = ProxyConfig.Builder.create().listener(listener).build();

        ArgumentCaptor<Method> invokedMethodCaptor = ArgumentCaptor.forClass(Method.class);
        ArgumentCaptor<Object[]> invokedMethodArgsCaptor = ArgumentCaptor.forClass(Object[].class);

        this.proxyLogicSupport.proceedMethodExecution(proxyConfig, target, connectionInfo, null, method, methodArgs);

        verify(this.proxyLogicSupport).performProxyLogic(any(), invokedMethodCaptor.capture(), invokedMethodArgsCaptor.capture(), any());

        assertSame(replacedMethod, invokedMethodCaptor.getValue());
        assertSame(replacedMethodArgs, invokedMethodArgsCaptor.getValue());

    }

    @Test
    void differentThread() throws Throwable {
        final Object target = new Object();
        final Method method = Statement.class.getMethod("execute", String.class);
        final Object[] methodArgs = new Object[]{};
        final ConnectionInfo connectionInfo = new ConnectionInfo();

        AtomicLong beforeMethodThreadId = new AtomicLong();
        AtomicLong afterMethodThreadId = new AtomicLong();
        AtomicReference<String> beforeMethodThreadName = new AtomicReference<>();
        AtomicReference<String> afterMethodThreadName = new AtomicReference<>();

        ProxyDataSourceListener listener = new ProxyDataSourceListener() {
            @Override
            public void beforeMethod(MethodExecutionContext executionContext) {
                beforeMethodThreadId.set(executionContext.getThreadId());
                beforeMethodThreadName.set(executionContext.getThreadName());
            }

            @Override
            public void afterMethod(MethodExecutionContext executionContext) {
                afterMethodThreadId.set(executionContext.getThreadId());
                afterMethodThreadName.set(executionContext.getThreadName());
            }
        };


        ProxyConfig proxyConfig = ProxyConfig.Builder.create().listener(listener).build();

        AtomicLong executedThreadId = new AtomicLong();
        AtomicReference<String> executedThreadName = new AtomicReference<>();

        AtomicBoolean failed = new AtomicBoolean();
        CountDownLatch latch = new CountDownLatch(1);

        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                executedThreadId.set(Thread.currentThread().getId());
                executedThreadName.set(Thread.currentThread().getName());
                this.proxyLogicSupport.proceedMethodExecution(proxyConfig, target, connectionInfo, null, method, methodArgs);
            } catch (Throwable throwable) {
                failed.set(true);
            }
            latch.countDown();
        });

        latch.await();

        assertFalse(failed.get());

        assertEquals(executedThreadId.get(), beforeMethodThreadId.get());
        assertEquals(executedThreadId.get(), afterMethodThreadId.get());
        assertEquals(executedThreadName.get(), beforeMethodThreadName.get());
        assertEquals(executedThreadName.get(), afterMethodThreadName.get());

    }

}
