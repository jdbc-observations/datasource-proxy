package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ConnectionIdManager;
import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.listener.MethodExecutionContext;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;

/**
 * Proxy Logic implementation for {@link DataSource} methods.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.2
 */
public class DataSourceProxyLogic extends ProxyLogicSupport {

    private DataSource dataSource;
    private ProxyConfig proxyConfig;

    public DataSourceProxyLogic(DataSource dataSource, ProxyConfig proxyConfig) {
        this.dataSource = dataSource;
        this.proxyConfig = proxyConfig;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return proceedMethodExecution(this.proxyConfig, this.dataSource, null, proxy, method, args);
    }

    @Override
    protected Object performProxyLogic(Object proxy, Method method, Object[] args, MethodExecutionContext methodContext) throws Throwable {
        String dataSourceName = this.proxyConfig.getDataSourceName();
        JdbcProxyFactory jdbcProxyFactory = this.proxyConfig.getJdbcProxyFactory();
        ConnectionIdManager connectionIdManager = this.proxyConfig.getConnectionIdManager();

        final String methodName = method.getName();
        if (isCommonMethod(methodName)) {
            return handleCommonMethod(methodName, this.dataSource, dataSourceName, args);
        }

        final Object retVal = proceedExecution(method, this.dataSource, args);

        if ("getConnection".equals(methodName)) {
            Connection conn = (Connection) retVal;
            String connId = connectionIdManager.getId(conn);
            ConnectionInfo connectionInfo = new ConnectionInfo();
            connectionInfo.setConnectionId(connId);
            connectionInfo.setIsolationLevel(conn.getTransactionIsolation());
            connectionInfo.setDataSourceName(dataSourceName);

            // make ConnectionInfo available in afterMethod() callback
            methodContext.setConnectionInfo(connectionInfo);

            return jdbcProxyFactory.createConnection((Connection) retVal, connectionInfo, this.proxyConfig);
        }

        return retVal;
    }

}
