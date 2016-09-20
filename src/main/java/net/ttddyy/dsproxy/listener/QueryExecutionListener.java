package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;

import java.util.List;

/**
 * Listener interface. Inject the implementation to proxy handler interceptors.
 *
 * @author Tadaya Tsuyukubo
 * @see ChainListener
 * @see net.ttddyy.dsproxy.proxy.jdk.ConnectionInvocationHandler
 * @see net.ttddyy.dsproxy.proxy.jdk.PreparedStatementInvocationHandler
 * @see net.ttddyy.dsproxy.proxy.jdk.StatementInvocationHandler
 */
public interface QueryExecutionListener {

    static QueryExecutionListener DEFAULT = new NoOpQueryExecutionListener();

    void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList);

    void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList);
}
