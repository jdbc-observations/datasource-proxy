package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.ConnectionIdManager;
import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.proxy.JdbcProxyFactory;
import net.ttddyy.dsproxy.proxy.ProxyConfig;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.sql.Connection;

/**
 * Support injecting proxies by AOP.
 *
 * @author Tadaya Tsuyukubo
 */
public class ProxyConnectionAdvice implements MethodInterceptor {

    private ProxyConfig proxyConfig = ProxyConfig.Builder.create().build();  // default

    public Object invoke(MethodInvocation invocation) throws Throwable {

        Object retVal = invocation.proceed();

        // only when return value is connection, return proxy.
        if (!(retVal instanceof Connection)) {
            return retVal;
        }

        Connection conn = (Connection) retVal;
        String connId = this.proxyConfig.getConnectionIdManager().getId(conn);
        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setConnectionId(connId);
        connectionInfo.setIsolationLevel(conn.getTransactionIsolation());
        connectionInfo.setDataSourceName("");

        return this.proxyConfig.getJdbcProxyFactory().createConnection((Connection) retVal, connectionInfo, this.proxyConfig);
    }


    public JdbcProxyFactory getJdbcProxyFactory() {
        return this.proxyConfig.getJdbcProxyFactory();
    }

    public void setJdbcProxyFactory(JdbcProxyFactory jdbcProxyFactory) {
        this.proxyConfig = ProxyConfig.Builder.from(this.proxyConfig)
                .jdbcProxyFactory(jdbcProxyFactory)
                .build();
    }

    /**
     * @since 1.4.2
     */
    public ConnectionIdManager getConnectionIdManager() {
        return this.proxyConfig.getConnectionIdManager();
    }

    /**
     * @since 1.4.2
     */
    public void setConnectionIdManager(ConnectionIdManager connectionIdManager) {
        this.proxyConfig = ProxyConfig.Builder.from(this.proxyConfig)
                .connectionIdManager(connectionIdManager)
                .build();
    }

    /**
     * @since 1.4.3
     */
    public ProxyConfig getProxyConfig() {
        return this.proxyConfig;
    }

    /**
     * @since 1.4.3
     */
    public void setProxyConfig(ProxyConfig proxyConfig) {
        this.proxyConfig = proxyConfig;
    }
}
