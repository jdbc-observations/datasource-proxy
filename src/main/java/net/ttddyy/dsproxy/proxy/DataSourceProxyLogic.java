package net.ttddyy.dsproxy.proxy;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Tadaya Tsuyukubo
 */
public class DataSourceProxyLogic {

    private static final Set<String> JDBC4_METHODS = Collections.unmodifiableSet(
            new HashSet<String>(Arrays.asList("unwrap", "isWrapperFor"))
    );

    private DataSource dataSource;
    private InterceptorHolder interceptorHolder;
    private String dataSourceName;
    private JdbcProxyFactory jdbcProxyFactory = JdbcProxyFactory.DEFAULT;

    public DataSourceProxyLogic() {
    }

    public DataSourceProxyLogic(DataSource dataSource, InterceptorHolder interceptorHolder, String dataSourceName,
                                JdbcProxyFactory jdbcProxyFactory) {
        this.dataSource = dataSource;
        this.interceptorHolder = interceptorHolder;
        this.dataSourceName = dataSourceName;
        this.jdbcProxyFactory = jdbcProxyFactory;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

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
                return jdbcProxyFactory.createConnection((Connection) retVal, interceptorHolder, dataSourceName);
            }
            return retVal;
        } catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        }
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setInterceptorHolder(InterceptorHolder interceptorHolder) {
        this.interceptorHolder = interceptorHolder;
    }

    public void setJdbcProxyFactory(JdbcProxyFactory jdbcProxyFactory) {
        this.jdbcProxyFactory = jdbcProxyFactory;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

}
