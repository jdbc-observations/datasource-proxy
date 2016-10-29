package net.ttddyy.dsproxy.proxy;

/**
 * Provide a method to unwrap the original jdbc object from proxy object.
 *
 * <p>Proxy object created by {@link JdbcProxyFactory} implements this interface.
 *
 * @author Tadaya Tsuyukubo
 * @see JdbcProxyFactory
 * @see net.ttddyy.dsproxy.proxy.jdk.ConnectionInvocationHandler
 * @see net.ttddyy.dsproxy.proxy.jdk.StatementInvocationHandler
 * @see net.ttddyy.dsproxy.proxy.jdk.PreparedStatementInvocationHandler
 * @see net.ttddyy.dsproxy.proxy.jdk.CallableStatementInvocationHandler
 * @see net.ttddyy.dsproxy.proxy.jdk.ResultSetInvocationHandler
 */
public interface ProxyJdbcObject {

    /**
     * Method to return wrapped source object(Connection, Statement, PreparedStatement, CallableStatement).
     *
     * @return source object
     */
    Object getTarget();
}
