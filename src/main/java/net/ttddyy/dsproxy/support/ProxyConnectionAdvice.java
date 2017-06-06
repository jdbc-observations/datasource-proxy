package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.ConnectionIdManager;
import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.proxy.JdbcProxyFactory;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.sql.Connection;

/**
 * Support injecting proxies by AOP.
 *
 * @author Tadaya Tsuyukubo
 */
public class ProxyConnectionAdvice implements MethodInterceptor {

    private JdbcProxyFactory jdbcProxyFactory = JdbcProxyFactory.DEFAULT;
    private ConnectionIdManager connectionIdManager = ConnectionIdManager.DEFAULT;

    public Object invoke(MethodInvocation invocation) throws Throwable {

        Object retVal = invocation.proceed();

        // only when return value is connection, return proxy.
        if (!(retVal instanceof Connection)) {
            return retVal;
        }

        Connection conn = (Connection) retVal;
        long connId = this.connectionIdManager.getId(conn);
        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setConnectionId(connId);
        connectionInfo.setDataSourceName("");

        return jdbcProxyFactory.createConnection((Connection) retVal, null, connectionInfo);
    }

    public JdbcProxyFactory getJdbcProxyFactory() {
        return jdbcProxyFactory;
    }

    public void setJdbcProxyFactory(JdbcProxyFactory jdbcProxyFactory) {
        this.jdbcProxyFactory = jdbcProxyFactory;
    }

    /**
     * @since 1.4.2
     */
    public ConnectionIdManager getConnectionIdManager() {
        return connectionIdManager;
    }

    /**
     * @since 1.4.2
     */
    public void setConnectionIdManager(ConnectionIdManager connectionIdManager) {
        this.connectionIdManager = connectionIdManager;
    }

}
