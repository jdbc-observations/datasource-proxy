package net.ttddyy.dsproxy.listener;

/**
 * Single listener interface for query executions and JDBC API invocations.
 *
 * <p>All callbacks are default no-op methods; implement only what is needed.
 *
 * @author Tadaya Tsuyukubo
 * @since 2.0
 */
public interface ProxyDataSourceListener {

    /**
     * Called before a query is executed.
     *
     * @param context query execution context
     */
    default void beforeQuery(QueryExecutionContext context) {
    }

    /**
     * Called after a query is executed.
     *
     * @param context query execution context
     */
    default void afterQuery(QueryExecutionContext context) {
    }

    /**
     * Called before a JDBC API method is invoked.
     *
     * @param context method execution context
     */
    default void beforeMethod(MethodExecutionContext context) {
    }

    /**
     * Called after a JDBC API method is invoked.
     *
     * @param context method execution context
     */
    default void afterMethod(MethodExecutionContext context) {
    }

}
