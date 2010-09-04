package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.listener.QueryExecutionListener;

import javax.sql.DataSource;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * Factory class to return a proxy with InvocationHandler used by datasource-proxy.
 *
 * @author Tadaya Tsuyukubo
 */
public class JdbcProxyFactory {

    public static DataSource createDataSource(DataSource dataSource, QueryExecutionListener listener, String dataSourceName) {
        return (DataSource) Proxy.newProxyInstance(ProxyJdbcObject.class.getClassLoader(),
                new Class[]{ProxyJdbcObject.class, DataSource.class},
                new DataSourceInvocationHandler(dataSource, listener, dataSourceName));
    }

    public static Connection createConnection(Connection connection, QueryExecutionListener listener) {
        return createConnection(connection, listener, "");
    }

    public static Connection createConnection(
            Connection connection, QueryExecutionListener listener, String dataSourceName) {
        return (Connection) Proxy.newProxyInstance(ProxyJdbcObject.class.getClassLoader(),
                new Class[]{ProxyJdbcObject.class, Connection.class},
                new ConnectionInvocationHandler(connection, listener, dataSourceName));
    }

    public static Statement createStatement(Statement statement, QueryExecutionListener listener) {
        return createStatement(statement, listener, "");
    }

    public static Statement createStatement(
            Statement statement, QueryExecutionListener listener, String dataSourceName) {
        return (Statement) Proxy.newProxyInstance(ProxyJdbcObject.class.getClassLoader(),
                new Class[]{ProxyJdbcObject.class, Statement.class},
                new StatementInvocationHandler(statement, listener, dataSourceName));
    }

    public static PreparedStatement createPreparedStatement(PreparedStatement preparedStatement, String query, QueryExecutionListener listener) {
        return createPreparedStatement(preparedStatement, query, listener, "");
    }

    public static PreparedStatement createPreparedStatement(
            PreparedStatement preparedStatement, String query,
            QueryExecutionListener listener, String dataSourceName) {
        return (PreparedStatement) Proxy.newProxyInstance(ProxyJdbcObject.class.getClassLoader(),
                new Class[]{ProxyJdbcObject.class, PreparedStatement.class},
                new PreparedStatementInvocationHandler(
                        (PreparedStatement) preparedStatement, query, listener, dataSourceName));
    }

    public static CallableStatement createCallableStatement(
            CallableStatement callableStatement, String query,
            QueryExecutionListener listener, String dataSourceName) {
        return (CallableStatement) Proxy.newProxyInstance(ProxyJdbcObject.class.getClassLoader(),
                new Class[]{ProxyJdbcObject.class, CallableStatement.class},
                new CallableStatementInvocationHandler(
                        (CallableStatement) callableStatement, query, listener, dataSourceName));
    }
}
