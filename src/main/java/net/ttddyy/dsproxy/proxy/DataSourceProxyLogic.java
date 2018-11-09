package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ConnectionIdManager;
import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.listener.MethodExecutionListenerUtils;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;

/**
 * Proxy Logic implementation for {@link DataSource} methods.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.2
 */
public class DataSourceProxyLogic extends CallbackSupport {

    private DataSource dataSource;
    private ProxyConfig proxyConfig;

    public DataSourceProxyLogic(DataSource dataSource, ProxyConfig proxyConfig) {
        this.dataSource = dataSource;
        this.proxyConfig = proxyConfig;
    }

    public Object invoke(Method method, Object[] args) throws Throwable {

        return MethodExecutionListenerUtils.invoke(
                (proxyTarget, targetMethod, targetArgs) ->
                        performQueryExecutionListener(targetMethod, targetArgs)
                , this.proxyConfig, this.dataSource, null, method, args);

    }

    private Object performQueryExecutionListener(Method method, Object[] args) throws Throwable {

        String dataSourceName = this.proxyConfig.getDataSourceName();
        JdbcProxyFactory jdbcProxyFactory = this.proxyConfig.getJdbcProxyFactory();
        ConnectionIdManager connectionIdManager = this.proxyConfig.getConnectionIdManager();

        String methodName = method.getName();

        if (isToStringMethod(methodName)) {
            return handleToStringMethod(this.dataSource);
        } else if ("getTarget".equals(methodName)) {
            // ProxyJdbcObject interface has method to return original object.
            return dataSource;
        }

        if (isWrapperMethods(methodName)) {
            return handleWrapperMethods(methodName, this.dataSource, args);
        }

        // Invoke method on original datasource.
        try {
            Object retVal = method.invoke(dataSource, args);

            if ("getConnection".equals(methodName)) {
                Connection conn = (Connection) retVal;
                String connId = connectionIdManager.getId(conn);
                ConnectionInfo connectionInfo = new ConnectionInfo();
                connectionInfo.setConnectionId(connId);
                connectionInfo.setDataSourceName(dataSourceName);

                return jdbcProxyFactory.createConnection((Connection) retVal, connectionInfo, this.proxyConfig);
            }
            return retVal;
        } catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        }
    }

}
