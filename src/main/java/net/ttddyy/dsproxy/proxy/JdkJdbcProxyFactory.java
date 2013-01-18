package net.ttddyy.dsproxy.proxy;

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

    public DataSource createDataSource(DataSource dataSource, InterceptorHolder interceptorHolder, String dataSourceName) {
        return (DataSource) Proxy.newProxyInstance(ProxyJdbcObject.class.getClassLoader(),
                new Class[]{ProxyJdbcObject.class, DataSource.class},
                new DataSourceInvocationHandler(dataSource, interceptorHolder, dataSourceName, this));
    }

    public Connection createConnection(Connection connection, InterceptorHolder interceptorHolder) {
        return createConnection(connection, interceptorHolder, "");
    }

    public Connection createConnection(Connection connection, InterceptorHolder interceptorHolder, String dataSourceName) {
        return (Connection) Proxy.newProxyInstance(ProxyJdbcObject.class.getClassLoader(),
                new Class[]{ProxyJdbcObject.class, Connection.class},
                new ConnectionInvocationHandler(connection, interceptorHolder, dataSourceName, this));
    }

    public Statement createStatement(Statement statement, InterceptorHolder interceptorHolder) {
        return createStatement(statement, interceptorHolder, "");
    }

    public Statement createStatement(Statement statement, InterceptorHolder interceptorHolder, String dataSourceName) {
        return (Statement) Proxy.newProxyInstance(ProxyJdbcObject.class.getClassLoader(),
                new Class[]{ProxyJdbcObject.class, Statement.class},
                new StatementInvocationHandler(statement, interceptorHolder, dataSourceName, this));
    }

    public PreparedStatement createPreparedStatement(PreparedStatement preparedStatement, String query,
                                                     InterceptorHolder interceptorHolder) {
        return createPreparedStatement(preparedStatement, query, interceptorHolder, "");
    }

    public PreparedStatement createPreparedStatement(PreparedStatement preparedStatement, String query,
                                                     InterceptorHolder interceptorHolder, String dataSourceName) {
        return (PreparedStatement) Proxy.newProxyInstance(ProxyJdbcObject.class.getClassLoader(),
                new Class[]{ProxyJdbcObject.class, PreparedStatement.class},
                new PreparedStatementInvocationHandler(
                        (PreparedStatement) preparedStatement, query, interceptorHolder, dataSourceName, this));
    }

    public CallableStatement createCallableStatement(CallableStatement callableStatement, String query,
                                                     InterceptorHolder interceptorHolder, String dataSourceName) {
        return (CallableStatement) Proxy.newProxyInstance(ProxyJdbcObject.class.getClassLoader(),
                new Class[]{ProxyJdbcObject.class, CallableStatement.class},
                new CallableStatementInvocationHandler(
                        (CallableStatement) callableStatement, query, interceptorHolder, dataSourceName, this));
    }
}
