package com.tpon.dsproxy.proxy;

/**
 * Proxy object implements this interface to provide a method to return wrapped object.
 *
 * @author Tadaya Tsuyukubo
 *
 * @see JdbcProxyFactory
 * @see com.tpon.dsproxy.proxy.ConnectionInvocationHandler
 * @see com.tpon.dsproxy.proxy.StatementInvocationHandler
 * @see com.tpon.dsproxy.proxy.PreparedStatementInvocationHandler
 * @see com.tpon.dsproxy.proxy.CallableStatementInvocationHandler
 */
public interface ProxyJdbcObject {

    /**
     * Method to return wrapped source object(Connection, Statement, PreparedStatement, CallableStatement).
     * @return source object
     */
    Object getTarget();
}
