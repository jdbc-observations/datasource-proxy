package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;

import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 * @since 2.0
 */
public interface ProxyDataSourceListener {

    default void beforeMethod(MethodExecutionContext executionContext) {
    }

    default void afterMethod(MethodExecutionContext executionContext) {
    }

    default void beforeQuery(ExecutionInfo execInfo) {
    }

    default void afterQuery(ExecutionInfo execInfo) {
    }

}
