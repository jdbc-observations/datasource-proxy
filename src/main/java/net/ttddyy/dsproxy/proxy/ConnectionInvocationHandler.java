package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.transform.QueryTransformer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Proxy InvocationHandler for {@link java.sql.Connection}.
 *
 * @author Tadaya Tsuyukubo
 */
public class ConnectionInvocationHandler implements InvocationHandler {

    private static final Set<String> JDBC4_METHODS = Collections.unmodifiableSet(
            new HashSet<String>(Arrays.asList("unwrap", "isWrapperFor"))
    );


    private Connection connection;
    private InterceptorHolder interceptorHolder;
    private String dataSourceName;
    private JdbcProxyFactory jdbcProxyFactory = JdbcProxyFactory.DEFAULT;

    public ConnectionInvocationHandler(Connection connection) {
        this.connection = connection;
    }

    @Deprecated
    public ConnectionInvocationHandler(Connection connection, QueryExecutionListener listener) {
        this.connection = connection;
        this.interceptorHolder = new InterceptorHolder(listener, QueryTransformer.DEFAULT);
    }

    @Deprecated
    public ConnectionInvocationHandler(
            Connection connection, QueryExecutionListener listener, String dataSourceName, JdbcProxyFactory jdbcProxyFactory) {
        this.connection = connection;
        this.interceptorHolder = new InterceptorHolder(listener, QueryTransformer.DEFAULT);
        this.dataSourceName = dataSourceName;
        this.jdbcProxyFactory = jdbcProxyFactory;
    }

    public ConnectionInvocationHandler(
            Connection connection, InterceptorHolder interceptorHolder, String dataSourceName, JdbcProxyFactory jdbcProxyFactory) {
        this.connection = connection;
        this.interceptorHolder = interceptorHolder;
        this.dataSourceName = dataSourceName;
        this.jdbcProxyFactory = jdbcProxyFactory;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        final String methodName = method.getName();

        if ("toString".equals(methodName)) {
            StringBuilder sb = new StringBuilder("ConnectionInvocationHandler [");
            sb.append(connection.toString());
            sb.append("]");
            return sb.toString(); // differentiate toString message.
        } else if ("getDataSourceName".equals(methodName)) {
            return dataSourceName;
        } else if ("getTarget".equals(methodName)) {
            // ProxyJdbcObject interface has method to return original object.
            return connection;
        }

        if (JDBC4_METHODS.contains(methodName)) {
            final Class<?> clazz = (Class<?>) args[0];
            if ("unwrap".equals(methodName)) {
                return connection.unwrap(clazz);
            } else if ("isWrapperFor".equals(methodName)) {
                return connection.isWrapperFor(clazz);
            }
        }

        // Invoke method on original Connection.
        try {
            Object retVal = method.invoke(connection, args);

            // when it is a call to createStatement or prepareStaement, or prepareCall return proxy
            // most of the time, spring and hibernate use prepareStatement to execute query as batch
            if ("createStatement".equals(methodName)) {
                // for normal statement, transforming query is handled inside of handler.
                return jdbcProxyFactory.createStatement((Statement) retVal, interceptorHolder, dataSourceName);
            } else if ("prepareStatement".equals(methodName)) {
                if (ObjectArrayUtils.isFirstArgString(args)) {
                    final String query = (String) args[0];
                    final String transformedQuery = interceptorHolder.getQueryTransformer().transformQuery(query);
                    return jdbcProxyFactory.createPreparedStatement((PreparedStatement) retVal, transformedQuery,
                            interceptorHolder, dataSourceName);
                }
            } else if ("prepareCall".equals(methodName)) {  // for stored procedure call
                if (ObjectArrayUtils.isFirstArgString(args)) {
                    final String query = (String) args[0];
                    final String transformedQuery = interceptorHolder.getQueryTransformer().transformQuery(query);
                    return jdbcProxyFactory.createCallableStatement((CallableStatement) retVal, transformedQuery,
                            interceptorHolder, dataSourceName);
                }
            }

            return retVal;
        }
        catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        }


    }

}
