package net.ttddyy.dsproxy.proxy;

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
    static final JdbcProxyFactory DEFAULT = new JdkJdbcProxyFactory();

    DataSource createDataSource(DataSource dataSource, InterceptorHolder interceptorHolder, String dataSourceName);

    Connection createConnection(Connection connection, InterceptorHolder interceptorHolder);

    Connection createConnection(Connection connection, InterceptorHolder interceptorHolder, String dataSourceName);

    Statement createStatement(Statement statement, InterceptorHolder interceptorHolder);

    Statement createStatement(Statement statement, InterceptorHolder interceptorHolder, String dataSourceName);

    PreparedStatement createPreparedStatement(PreparedStatement preparedStatement, String query, InterceptorHolder interceptorHolder);

    PreparedStatement createPreparedStatement(PreparedStatement preparedStatement, String query, InterceptorHolder interceptorHolder, String dataSourceName);

    CallableStatement createCallableStatement(CallableStatement callableStatement, String query, InterceptorHolder interceptorHolder, String dataSourceName);
}
