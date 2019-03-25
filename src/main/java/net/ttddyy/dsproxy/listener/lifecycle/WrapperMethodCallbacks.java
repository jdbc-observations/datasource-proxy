package net.ttddyy.dsproxy.listener.lifecycle;

import net.ttddyy.dsproxy.listener.MethodExecutionContext;

/**
 * Defines callback methods for {@link java.sql.Wrapper}.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.6
 */
public interface WrapperMethodCallbacks {

    void beforeIsWrapperFor(MethodExecutionContext executionContext);

    void beforeUnwrap(MethodExecutionContext executionContext);

    void afterIsWrapperFor(MethodExecutionContext executionContext);

    void afterUnwrap(MethodExecutionContext executionContext);

}
