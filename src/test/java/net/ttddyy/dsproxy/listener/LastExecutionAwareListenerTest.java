package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.ExecutionInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Tadaya Tsuyukubo
 */
public class LastExecutionAwareListenerTest {

    private LastExecutionAwareListener listener;

    @BeforeEach
    void setUp() {
        this.listener = new LastExecutionAwareListener();
    }

    @Test
    void beforeMethod() {
        MethodExecutionContext executionContext = new MethodExecutionContext();

        assertNull(listener.getBeforeMethodContext());
        assertFalse(listener.isBeforeMethodCalled());

        listener.beforeMethod(executionContext);

        assertSame(executionContext, listener.getBeforeMethodContext());
        assertTrue(listener.isBeforeMethodCalled());


        MethodExecutionContext anotherExecutionContext = new MethodExecutionContext();

        listener.beforeMethod(anotherExecutionContext);

        assertSame(anotherExecutionContext, listener.getBeforeMethodContext());
    }

    @Test
    void afterMethod() {
        MethodExecutionContext executionContext = new MethodExecutionContext();

        assertNull(listener.getAfterMethodContext());
        assertFalse(listener.isAfterMethodCalled());

        listener.afterMethod(executionContext);

        assertSame(executionContext, listener.getAfterMethodContext());
        assertTrue(listener.isAfterMethodCalled());


        MethodExecutionContext anotherExecutionContext = new MethodExecutionContext();

        listener.afterMethod(anotherExecutionContext);

        assertSame(anotherExecutionContext, listener.getAfterMethodContext());
    }

    @Test
    void beforeQuery() {
        ExecutionInfo executionInfo = new ExecutionInfo();

        assertNull(listener.getBeforeQueryContext());
        assertFalse(listener.isBeforeQueryCalled());

        listener.beforeQuery(executionInfo);

        assertSame(executionInfo, listener.getBeforeQueryContext());
        assertTrue(listener.isBeforeQueryCalled());

        ExecutionInfo anotherExecutionInfo = new ExecutionInfo();

        listener.beforeQuery(anotherExecutionInfo);

        assertSame(anotherExecutionInfo, listener.getBeforeQueryContext());

    }

    @Test
    void afterQuery() {
        ExecutionInfo executionInfo = new ExecutionInfo();

        assertNull(listener.getAfterQueryContext());
        assertFalse(listener.isAfterQueryCalled());

        listener.afterQuery(executionInfo);

        assertSame(executionInfo, listener.getAfterQueryContext());
        assertTrue(listener.isAfterQueryCalled());

        ExecutionInfo anotherExecutionInfo = new ExecutionInfo();

        listener.afterQuery(anotherExecutionInfo);

        assertSame(anotherExecutionInfo, listener.getAfterQueryContext());
    }

}
