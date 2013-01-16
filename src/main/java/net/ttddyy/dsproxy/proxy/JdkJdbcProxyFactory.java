package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.listener.QueryExecutionListener;

import javax.sql.DataSource;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * Dynamic Proxy Class(Jdk Proxy) based {@link JdbcProxyFactory} implementation.
 *
 * @author Tadaya Tsuyukubo
 */
public class JdkJdbcProxyFactory implements JdbcProxyFactory {

    public DataSource createDataSource(DataSource dataSource, QueryExecutionListener listener, String dataSourceName) {
        return (DataSource) Proxy.newProxyInstance(ProxyJdbcObject.class.getClassLoader(),
                new Class[]{ProxyJdbcObject.class, DataSource.class},
                new DataSourceInvocationHandler(dataSource, listener, dataSourceName, this));
    }

    public Connection createConnection(Connection connection, QueryExecutionListener listener) {
        return createConnection(connection, listener, "");
    }

    public Connection createConnection(Connection connection, QueryExecutionListener listener, String dataSourceName) {
        return (Connection) Proxy.newProxyInstance(ProxyJdbcObject.class.getClassLoader(),
                new Class[]{ProxyJdbcObject.class, Connection.class},
                new ConnectionInvocationHandler(connection, listener, dataSourceName, this));
    }

    public Statement createStatement(Statement statement, QueryExecutionListener listener) {
        return createStatement(statement, listener, "");
    }

    public Statement createStatement(Statement statement, QueryExecutionListener listener, String dataSourceName) {
        return (Statement) Proxy.newProxyInstance(ProxyJdbcObject.class.getClassLoader(),
                new Class[]{ProxyJdbcObject.class, Statement.class},
                new StatementInvocationHandler(statement, listener, dataSourceName, this));
    }

    public PreparedStatement createPreparedStatement(PreparedStatement preparedStatement, String query,
                                                     QueryExecutionListener listener) {
        return createPreparedStatement(preparedStatement, query, listener, "");
    }

    public PreparedStatement createPreparedStatement(PreparedStatement preparedStatement, String query,
                                                     QueryExecutionListener listener, String dataSourceName) {
        return (PreparedStatement) Proxy.newProxyInstance(ProxyJdbcObject.class.getClassLoader(),
                new Class[]{ProxyJdbcObject.class, PreparedStatement.class},
                new PreparedStatementInvocationHandler(
                        (PreparedStatement) preparedStatement, query, listener, dataSourceName, this));
    }

    public CallableStatement createCallableStatement(CallableStatement callableStatement, String query,
                                                     QueryExecutionListener listener, String dataSourceName) {
        return (CallableStatement) Proxy.newProxyInstance(ProxyJdbcObject.class.getClassLoader(),
                new Class[]{ProxyJdbcObject.class, CallableStatement.class},
                new CallableStatementInvocationHandler(
                        (CallableStatement) callableStatement, query, listener, dataSourceName, this));
    }
}
