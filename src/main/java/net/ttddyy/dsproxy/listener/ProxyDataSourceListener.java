package net.ttddyy.dsproxy.listener;

/**
 * @author Tadaya Tsuyukubo
 * @since 2.0
 */
public interface ProxyDataSourceListener {

    default void beforeMethod(MethodExecutionContext executionContext) {
    }

    default void afterMethod(MethodExecutionContext executionContext) {
    }

    default void beforeQuery(QueryExecutionContext executionContext) {
    }

    default void afterQuery(QueryExecutionContext executionContext) {
    }

}
