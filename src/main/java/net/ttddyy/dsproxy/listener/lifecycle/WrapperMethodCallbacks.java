package net.ttddyy.dsproxy.listener.lifecycle;

import net.ttddyy.dsproxy.listener.MethodExecutionContext;

/**
 * Defines callback methods for {@link java.sql.Wrapper}.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.6
 */
public interface WrapperMethodCallbacks {

    default void beforeIsWrapperFor(MethodExecutionContext executionContext) {

    }

    default void beforeUnwrap(MethodExecutionContext executionContext) {

    }

    default void afterIsWrapperFor(MethodExecutionContext executionContext) {

    }

    default void afterUnwrap(MethodExecutionContext executionContext) {

    }

}
