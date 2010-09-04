package net.ttddyy.dsproxy.proxy;

/**
 * Proxy object implements this interface to provide a method to return wrapped object.
 *
 * @author Tadaya Tsuyukubo
 *
 * @see JdbcProxyFactory
 * @see ConnectionInvocationHandler
 * @see StatementInvocationHandler
 * @see PreparedStatementInvocationHandler
 * @see CallableStatementInvocationHandler
 */
public interface ProxyJdbcObject {

    /**
     * Method to return wrapped source object(Connection, Statement, PreparedStatement, CallableStatement).
     * @return source object
     */
    Object getTarget();
}
