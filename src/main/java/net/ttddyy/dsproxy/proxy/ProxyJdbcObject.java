package net.ttddyy.dsproxy.proxy;

/**
 * Provide a method to unwrap the original jdbc object from proxy object.
 * <p/>
 * Proxy object created by {@link JdbcProxyFactory} implements this interface.
 *
 * @author Tadaya Tsuyukubo
 * @see JdbcProxyFactory
 * @see ConnectionInvocationHandler
 * @see StatementInvocationHandler
 * @see PreparedStatementInvocationHandler
 * @see CallableStatementInvocationHandler
 */
public interface ProxyJdbcObject {

    /**
     * Method to return wrapped source object(Connection, Statement, PreparedStatement, CallableStatement).
     *
     * @return source object
     */
    Object getTarget();
}
