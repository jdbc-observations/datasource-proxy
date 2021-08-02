package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ConnectionIdManager;
import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.listener.MethodExecutionListenerUtils;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Proxy Logic implementation for {@link DataSource} methods.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.2
 */
public class DataSourceProxyLogic {

    private static final Set<String> JDBC4_METHODS = Collections.unmodifiableSet(
            new HashSet<String>(Arrays.asList("unwrap", "isWrapperFor"))
    );

    private DataSource dataSource;
    private ProxyConfig proxyConfig;

    public DataSourceProxyLogic(DataSource dataSource, ProxyConfig proxyConfig) {
        this.dataSource = dataSource;
        this.proxyConfig = proxyConfig;
    }

    public Object invoke(Method method, Object[] args) throws Throwable {

        return MethodExecutionListenerUtils.invoke(new MethodExecutionListenerUtils.MethodExecutionCallback() {
            @Override
            public Object execute(Object proxy, Method method, Object[] args) throws Throwable {
                return performQueryExecutionListener(method, args);
            }
        }, this.proxyConfig, this.dataSource, null, method, args);

    }

    private Object performQueryExecutionListener(Method method, Object[] args) throws Throwable {

        String dataSourceName = this.proxyConfig.getDataSourceName();
        JdbcProxyFactory jdbcProxyFactory = this.proxyConfig.getJdbcProxyFactory();
        ConnectionIdManager connectionIdManager = this.proxyConfig.getConnectionIdManager();

        final String methodName = method.getName();

        if ("toString".equals(methodName)) {
            StringBuilder sb = new StringBuilder();
            sb.append(dataSource.getClass().getSimpleName());
            sb.append(" [");
            sb.append(dataSource.toString());
            sb.append("]");
            return sb.toString(); // differentiate toString message.
        } else if ("getDataSourceName".equals(methodName)) {
            return dataSourceName;
        } else if ("getTarget".equals(methodName)) {
            // ProxyJdbcObject interface has method to return original object.
            return dataSource;
        }

        if (JDBC4_METHODS.contains(methodName)) {
            final Class<?> clazz = (Class<?>) args[0];
            if ("unwrap".equals(methodName)) {
                return dataSource.unwrap(clazz);
            } else if ("isWrapperFor".equals(methodName)) {
                return dataSource.isWrapperFor(clazz);
            }
        }

        // Invoke method on original datasource.
        try {
            final Object retVal = method.invoke(dataSource, args);

            if ("getConnection".equals(methodName)) {
                Connection conn = (Connection) retVal;
                String connId = connectionIdManager.getId(conn);
                ConnectionInfo connectionInfo = new ConnectionInfo();
                connectionInfo.setConnectionId(connId);
                connectionInfo.setIsolationLevel(conn.getTransactionIsolation());
                connectionInfo.setDataSourceName(dataSourceName);

                return jdbcProxyFactory.createConnection((Connection) retVal, connectionInfo, this.proxyConfig);
            }
            return retVal;
        } catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        }
    }

}
