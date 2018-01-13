package net.ttddyy.dsproxy.proxy;

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


    DataSource createDataSource(DataSource dataSource, ProxyConfig proxyConfig);

    Connection createConnection(Connection connection, ConnectionInfo connectionInfo, ProxyConfig proxyConfig);

    Statement createStatement(Statement statement, ConnectionInfo connectionInfo, Connection proxyConnection,
                              ProxyConfig proxyConfig);

    PreparedStatement createPreparedStatement(PreparedStatement preparedStatement, String query,
                                              ConnectionInfo connectionInfo, Connection proxyConnection,
                                              ProxyConfig proxyConfig, boolean generateKey);

    CallableStatement createCallableStatement(CallableStatement callableStatement, String query,
                                              ConnectionInfo connectionInfo,
                                              Connection proxyConnection, ProxyConfig proxyConfig);

    /**
     * Create a proxy for {@link ResultSet}.
     *
     * @since 1.4.3
     */
    ResultSet createResultSet(ResultSet resultSet, ConnectionInfo connectionInfo, ProxyConfig proxyConfig);

    /**
     * Create a proxy for {@link ResultSet} generated keys.
     *
     * @since 1.4.5
     */
    ResultSet createGeneratedKeys(ResultSet resultSet, ConnectionInfo connectionInfo, ProxyConfig proxyConfig);

}