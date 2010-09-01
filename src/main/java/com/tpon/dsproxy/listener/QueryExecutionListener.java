package com.tpon.dsproxy.listener;

import com.tpon.dsproxy.ExecutionInfo;
import com.tpon.dsproxy.QueryInfo;

import java.util.List;

/**
 * Listener interface. Inject the implementation to proxy handler interceptors.
 *
 * @author Tadaya Tsuyukubo
 * @see com.tpon.dsproxy.listener.ChainListener
 * @see com.tpon.dsproxy.proxy.ConnectionInvocationHandler
 * @see com.tpon.dsproxy.proxy.PreparedStatementInvocationHandler
 * @see com.tpon.dsproxy.proxy.StatementInvocationHandler
 */
public interface QueryExecutionListener {

    void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList);

    void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList);
}
