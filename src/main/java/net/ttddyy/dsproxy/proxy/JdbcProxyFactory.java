package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ConnectionIdManager;
import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.proxy.jdk.JdkJdbcProxyFactory;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Factory interface to return a proxy with InvocationHandler used by datasource-proxy.
 *
 * @author Tadaya Tsuyukubo
 */
public interface JdbcProxyFactory {

    /**
     * use JDK proxy as default.
     */
    JdbcProxyFactory DEFAULT = new JdkJdbcProxyFactory();


    DataSource createDataSource(DataSource dataSource, InterceptorHolder interceptorHolder, String dataSourceName,
                                ConnectionIdManager connectionIdManager);

    Connection createConnection(Connection connection, InterceptorHolder interceptorHolder, ConnectionInfo connectionInfo);

    Statement createStatement(Statement statement, InterceptorHolder interceptorHolder, ConnectionInfo connectionInfo,
                              Connection proxyConnection);

    PreparedStatement createPreparedStatement(PreparedStatement preparedStatement, String query,
                                              InterceptorHolder interceptorHolder, ConnectionInfo connectionInfo,
                                              Connection proxyConnection);

    CallableStatement createCallableStatement(CallableStatement callableStatement, String query,
                                              InterceptorHolder interceptorHolder, ConnectionInfo connectionInfo,
                                              Connection proxyConnection);

    /**
     * Flag this factory instance to create a proxy for {@link ResultSet}.
     *
     * @param createResultSetProxy true to proxy ResultSet
     * @return this instance
     * @since 1.4.3
     */
    JdkJdbcProxyFactory createResultSetProxy(boolean createResultSetProxy);

    /**
     * Create a proxy for {@link ResultSet} if enabled.
     *
     * When {@link #createResultSetProxy(boolean)} is set to {@code true}, this method returns a proxied
     * {@link ResultSet}. If {@code false} is set, passed original {@link ResultSet} is returned.
     *
     * @param resultSet a result set
     * @return proxied ResultSet if enabled
     * @since 1.4.3
     */
    ResultSet createResultSet(ResultSet resultSet);

}