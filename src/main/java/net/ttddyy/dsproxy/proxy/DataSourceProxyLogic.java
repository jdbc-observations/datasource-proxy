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
public class DataSourceProxyLogic extends CallbackSupport {

    private DataSource dataSource;
    private ProxyConfig proxyConfig;

    public DataSourceProxyLogic(DataSource dataSource, ProxyConfig proxyConfig) {
        this.dataSource = dataSource;
        this.proxyConfig = proxyConfig;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return proceedMethodExecution(
                (methodContext, proxyTarget, targetMethod, targetArgs) ->
                        performProxyLogic(proxy, targetMethod, targetArgs, methodContext)
                , this.proxyConfig, this.dataSource, null, method, args);
    }

    @Override
    protected Object performProxyLogic(Object proxy, Method method, Object[] args, MethodExecutionContext methodContext) throws Throwable {

        String dataSourceName = this.proxyConfig.getDataSourceName();
        JdbcProxyFactory jdbcProxyFactory = this.proxyConfig.getJdbcProxyFactory();
        ConnectionIdManager connectionIdManager = this.proxyConfig.getConnectionIdManager();

        String methodName = method.getName();

        if (isToStringMethod(methodName)) {
            return handleToStringMethod(this.dataSource);
        } else if (isGetTargetMethod(methodName)) {
            return this.dataSource;  // ProxyJdbcObject interface has method to return original object.
        } else if (isWrapperMethods(methodName)) {
            return handleWrapperMethods(methodName, this.dataSource, args);
        }


        // Invoke method on original datasource.
        Object retVal = proceedExecution(method, this.dataSource, args);

        if ("getConnection".equals(methodName)) {
            Connection conn = (Connection) retVal;
            String connId = connectionIdManager.getId(conn);
            ConnectionInfo connectionInfo = new ConnectionInfo();
            connectionInfo.setConnectionId(connId);
            connectionInfo.setDataSourceName(dataSourceName);

            // add MethodExecutionContext, so that it is available in afterMethod() callback
            methodContext.setConnectionInfo(connectionInfo);

            return jdbcProxyFactory.createConnection((Connection) retVal, connectionInfo, this.proxyConfig);
        } else {
            return retVal;
        }

    }

}
