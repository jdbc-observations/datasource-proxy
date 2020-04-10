package net.ttddyy.dsproxy.listener;

/**
 * @author Tadaya Tsuyukubo
 * @since 2.0
 */
public interface ProxyDataSourceListener {

    void beforeMethod(MethodExecutionContext executionContext);

    void afterMethod(MethodExecutionContext executionContext);

    void beforeQuery(QueryExecutionContext executionContext);

    void afterQuery(QueryExecutionContext executionContext);

}
