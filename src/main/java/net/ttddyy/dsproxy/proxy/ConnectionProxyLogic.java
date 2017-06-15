package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.transform.TransformInfo;

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
 * Proxy Logic implementation for {@link Connection} methods.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.2
 */
public class ConnectionProxyLogic {

    private static final Set<String> JDBC4_METHODS = Collections.unmodifiableSet(
            new HashSet<String>(Arrays.asList("unwrap", "isWrapperFor"))
    );

    private Connection connection;
    private InterceptorHolder interceptorHolder;
    private ConnectionInfo connectionInfo;
    private JdbcProxyFactory jdbcProxyFactory = JdbcProxyFactory.DEFAULT;

    public ConnectionProxyLogic() {
    }

    public ConnectionProxyLogic(
            Connection connection, InterceptorHolder interceptorHolder, ConnectionInfo connectionInfo, JdbcProxyFactory jdbcProxyFactory) {
        this.connection = connection;
        this.interceptorHolder = interceptorHolder;
        this.connectionInfo = connectionInfo;
        this.jdbcProxyFactory = jdbcProxyFactory;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final Connection proxyConnection = (Connection) proxy;
        final String methodName = method.getName();

        if ("toString".equals(methodName)) {
            final StringBuilder sb = new StringBuilder();
            sb.append(connection.getClass().getSimpleName());
            sb.append(" [");
            sb.append(connection.toString());
            sb.append("]");
            return sb.toString(); // differentiate toString message.
        } else if ("getDataSourceName".equals(methodName)) {
            return this.connectionInfo.getDataSourceName();
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

        // replace query for PreparedStatement and CallableStatement
        if ("prepareStatement".equals(methodName) || "prepareCall".equals(methodName)) {
            if (ObjectArrayUtils.isFirstArgString(args)) {
                final String query = (String) args[0];
                final Class<? extends Statement> clazz =
                        "prepareStatement".equals(methodName) ? PreparedStatement.class : CallableStatement.class;
                final TransformInfo transformInfo = new TransformInfo(clazz, this.connectionInfo.getDataSourceName(), query, false, 0);
                final String transformedQuery = interceptorHolder.getQueryTransformer().transformQuery(transformInfo);
                args[0] = transformedQuery;
            }
        }

        // Invoke method on original Connection.
        final Object retVal;
        try {
            retVal = method.invoke(connection, args);
        } catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        }

        // when it is a call to createStatement, prepareStatement or prepareCall, returns a proxy.
        // most of the time, spring and hibernate use prepareStatement to execute query as batch
        if ("createStatement".equals(methodName)) {
            // for normal statement, transforming query is handled inside of handler.
            return jdbcProxyFactory.createStatement((Statement) retVal, interceptorHolder, this.connectionInfo, proxyConnection);
        } else if ("prepareStatement".equals(methodName)) {
            if (ObjectArrayUtils.isFirstArgString(args)) {
                final String query = (String) args[0];
                return jdbcProxyFactory.createPreparedStatement((PreparedStatement) retVal, query,
                        interceptorHolder, this.connectionInfo, proxyConnection);
            }
        } else if ("prepareCall".equals(methodName)) {  // for stored procedure call
            if (ObjectArrayUtils.isFirstArgString(args)) {
                final String query = (String) args[0];
                return jdbcProxyFactory.createCallableStatement((CallableStatement) retVal, query,
                        interceptorHolder, this.connectionInfo, proxyConnection);
            }
        }

        return retVal;
    }

}
