package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.listener.QueryExecutionListener;

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

    DataSource createDataSource(DataSource dataSource, QueryExecutionListener listener, String dataSourceName);

    Connection createConnection(Connection connection, QueryExecutionListener listener);

    Connection createConnection(Connection connection, QueryExecutionListener listener, String dataSourceName);

    Statement createStatement(Statement statement, QueryExecutionListener listener);

    Statement createStatement(Statement statement, QueryExecutionListener listener, String dataSourceName);

    PreparedStatement createPreparedStatement(PreparedStatement preparedStatement, String query, QueryExecutionListener listener);

    PreparedStatement createPreparedStatement(PreparedStatement preparedStatement, String query, QueryExecutionListener listener, String dataSourceName);

    CallableStatement createCallableStatement(CallableStatement callableStatement, String query, QueryExecutionListener listener, String dataSourceName);
}
