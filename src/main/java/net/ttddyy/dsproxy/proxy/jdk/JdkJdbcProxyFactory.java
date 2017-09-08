package net.ttddyy.dsproxy.proxy.jdk;

import net.ttddyy.dsproxy.ConnectionIdManager;
import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.proxy.InterceptorHolder;
import net.ttddyy.dsproxy.proxy.JdbcProxyFactory;
import net.ttddyy.dsproxy.proxy.ProxyJdbcObject;

import javax.sql.DataSource;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Dynamic Proxy Class(Jdk Proxy) based {@link net.ttddyy.dsproxy.proxy.JdbcProxyFactory} implementation.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.2
 */
public class JdkJdbcProxyFactory implements JdbcProxyFactory {

    private boolean createResultSetProxy;

    public DataSource createDataSource(DataSource dataSource, InterceptorHolder interceptorHolder, String dataSourceName, ConnectionIdManager connectionIdManager) {
        return (DataSource) Proxy.newProxyInstance(ProxyJdbcObject.class.getClassLoader(),
                new Class[]{ProxyJdbcObject.class, DataSource.class},
                new DataSourceInvocationHandler(dataSource, interceptorHolder, dataSourceName, this, connectionIdManager));
    }

    public Connection createConnection(Connection connection, InterceptorHolder interceptorHolder, ConnectionInfo connectionInfo) {
        return (Connection) Proxy.newProxyInstance(ProxyJdbcObject.class.getClassLoader(),
                new Class[]{ProxyJdbcObject.class, Connection.class},
                new ConnectionInvocationHandler(connection, interceptorHolder, connectionInfo, this));
    }

    public Statement createStatement(Statement statement, InterceptorHolder interceptorHolder,
                                     ConnectionInfo connectionInfo, Connection proxyConnection) {
        return (Statement) Proxy.newProxyInstance(ProxyJdbcObject.class.getClassLoader(),
                new Class[]{ProxyJdbcObject.class, Statement.class},
                new StatementInvocationHandler(statement, interceptorHolder, connectionInfo, proxyConnection, this));
    }

    public PreparedStatement createPreparedStatement(PreparedStatement preparedStatement, String query,
                                                     InterceptorHolder interceptorHolder, ConnectionInfo connectionInfo,
                                                     Connection proxyConnection) {
        return (PreparedStatement) Proxy.newProxyInstance(ProxyJdbcObject.class.getClassLoader(),
                new Class[]{ProxyJdbcObject.class, PreparedStatement.class},
                new PreparedStatementInvocationHandler(preparedStatement, query, interceptorHolder, connectionInfo,
                        proxyConnection, this));
    }

    public CallableStatement createCallableStatement(CallableStatement callableStatement, String query,
                                                     InterceptorHolder interceptorHolder, ConnectionInfo connectionInfo,
                                                     Connection proxyConnection) {
        return (CallableStatement) Proxy.newProxyInstance(ProxyJdbcObject.class.getClassLoader(),
                new Class[]{ProxyJdbcObject.class, CallableStatement.class},
                new CallableStatementInvocationHandler(callableStatement, query, interceptorHolder, connectionInfo,
                        proxyConnection, this));
    }

    public JdkJdbcProxyFactory createResultSetProxy(boolean createResultSetProxy) {
        this.createResultSetProxy = createResultSetProxy;
        return this;
    }

    // TODO: currently this is for repeatable read resultset
    public ResultSet createResultSet(ResultSet resultSet) {
        if (this.createResultSetProxy) {
            return (ResultSet) Proxy.newProxyInstance(ProxyJdbcObject.class.getClassLoader(),
                    new Class[]{ProxyJdbcObject.class, ResultSet.class},
                    new ResultSetInvocationHandler(resultSet));
        } else {
            return resultSet;
        }
    }


}
