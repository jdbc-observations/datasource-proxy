package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ConnectionIdManager;
import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.proxy.jdk.JdkJdbcProxyFactory;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
}
