package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.ConnectionInfo;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tadaya Tsuyukubo
 */
public class TracingMethodListenerTest {

    @Test
    public void tracingCondition() {

        BooleanSupplier falseCondition = () -> false;

        final AtomicBoolean isCalled = new AtomicBoolean(false);
        TracingMethodListener listener = new TracingMethodListener() {
            @Override
            protected void logMessage(String message) {
                isCalled.set(true);
            }
        };
        listener.setTracingCondition(falseCondition);

        // invoke
        listener.afterMethod(MethodExecutionContext.Builder.create().build());

        assertThat(isCalled.get()).isFalse();
    }

    @Test
    public void logMessage() throws Exception {
        final AtomicReference<String> messageHolder = new AtomicReference<String>();
        TracingMethodListener listener = new TracingMethodListener() {
            @Override
            protected void logMessage(String message) {
                messageHolder.set(message);
            }
        };

        class MyTarget {
        }

        MyTarget target = new MyTarget();

        Method method = PreparedStatement.class.getMethod("setString", int.class, String.class);
        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setConnectionId(100);

        MethodExecutionContext context = MethodExecutionContext.Builder.create()
                .target(target)
                .method(method)
                .elapsedTime(99)
                .connectionInfo(connectionInfo)
                .methodArgs(new Object[]{50, "foo"})
                .build();

        listener.afterMethod(context);

        assertThat(messageHolder.get()).isEqualTo("[1][success][99ms][conn=100] MyTarget#setString(50,\"foo\")");
    }

    @Test
    public void tracingMessageConsumer() throws Exception {
        final AtomicReference<String> messageHolder = new AtomicReference<String>();
        TracingMethodListener listener = new TracingMethodListener();

        listener.setTracingMessageConsumer(messageHolder::set);

        class MyTarget {
        }

        MyTarget target = new MyTarget();

        Method method = PreparedStatement.class.getMethod("setString", int.class, String.class);
        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setConnectionId(100);

        MethodExecutionContext context = MethodExecutionContext.Builder.create()
                .target(target)
                .method(method)
                .elapsedTime(99)
                .connectionInfo(connectionInfo)
                .methodArgs(new Object[]{50, "foo"})
                .build();

        listener.afterMethod(context);

        assertThat(messageHolder.get()).isEqualTo("[1][success][99ms][conn=100] MyTarget#setString(50,\"foo\")");
    }
}
