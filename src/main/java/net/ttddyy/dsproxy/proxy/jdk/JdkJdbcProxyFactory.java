package net.ttddyy.dsproxy.proxy.jdk;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.proxy.JdbcProxyFactory;
import net.ttddyy.dsproxy.proxy.ProxyConfig;
import net.ttddyy.dsproxy.proxy.ProxyJdbcObject;
import net.ttddyy.dsproxy.proxy.ResultSetProxyLogicFactory;

import javax.sql.DataSource;
import java.lang.reflect.Proxy;
import java.sql.*;

/**
 * Dynamic Proxy Class(Jdk Proxy) based {@link net.ttddyy.dsproxy.proxy.JdbcProxyFactory} implementation.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.2
 */
public class JdkJdbcProxyFactory implements JdbcProxyFactory {

    @Override
    public DataSource createDataSource(DataSource dataSource, ProxyConfig proxyConfig) {
        return (DataSource) Proxy.newProxyInstance(ProxyJdbcObject.class.getClassLoader(),
                new Class[]{ProxyJdbcObject.class, DataSource.class},
                new DataSourceInvocationHandler(dataSource, proxyConfig));
    }

    @Override
    public Connection createConnection(Connection connection, ConnectionInfo connectionInfo, ProxyConfig proxyConfig) {
        return (Connection) Proxy.newProxyInstance(ProxyJdbcObject.class.getClassLoader(),
                new Class[]{ProxyJdbcObject.class, Connection.class},
                new ConnectionInvocationHandler(connection, connectionInfo, proxyConfig));
    }

    @Override
    public Statement createStatement(Statement statement, ConnectionInfo connectionInfo, Connection proxyConnection,
                                     ProxyConfig proxyConfig) {
        return (Statement) Proxy.newProxyInstance(ProxyJdbcObject.class.getClassLoader(),
                new Class[]{ProxyJdbcObject.class, Statement.class},
                new StatementInvocationHandler(statement, connectionInfo, proxyConnection, proxyConfig));
    }

    @Override
    public PreparedStatement createPreparedStatement(PreparedStatement preparedStatement, String query,
                                                     ConnectionInfo connectionInfo, Connection proxyConnection,
                                                     ProxyConfig proxyConfig) {
        return (PreparedStatement) Proxy.newProxyInstance(ProxyJdbcObject.class.getClassLoader(),
                new Class[]{ProxyJdbcObject.class, PreparedStatement.class},
                new PreparedStatementInvocationHandler(preparedStatement, query, connectionInfo, proxyConnection, proxyConfig));
    }

    @Override
    public CallableStatement createCallableStatement(CallableStatement callableStatement, String query,
                                                     ConnectionInfo connectionInfo,
                                                     Connection proxyConnection, ProxyConfig proxyConfig) {
        return (CallableStatement) Proxy.newProxyInstance(ProxyJdbcObject.class.getClassLoader(),
                new Class[]{ProxyJdbcObject.class, CallableStatement.class},
                new CallableStatementInvocationHandler(callableStatement, query, connectionInfo,
                        proxyConnection, proxyConfig));
    }

    /**
     * Create a proxy for {@link ResultSet} if given proxyConfig contains {@link net.ttddyy.dsproxy.proxy.ResultSetProxyLogicFactory}.
     *
     * When {@link ProxyConfig#getResultSetProxyLogicFactory()} returns any factory object, this returns proxied
     * {@link ResultSet}. If {@code null} is returned, this method returns non-proxied original {@link ResultSet}.
     *
     * @param resultSet   a result set
     * @param proxyConfig a proxy config
     * @return proxied ResultSet if config has ResultSetProxyLogicFactory, otherwise returns original ResultSet
     * @since 1.4.3
     */
    @Override
    public ResultSet createResultSet(ResultSet resultSet, ConnectionInfo connectionInfo, ProxyConfig proxyConfig) {
        ResultSetProxyLogicFactory factory = proxyConfig.getResultSetProxyLogicFactory();
        // when proxy logic factory is specified, create a proxy
        if (factory != null) {
            return (ResultSet) Proxy.newProxyInstance(ProxyJdbcObject.class.getClassLoader(),
                    new Class[]{ProxyJdbcObject.class, ResultSet.class},
                    new ResultSetInvocationHandler(factory, resultSet, connectionInfo, proxyConfig));
        } else {
            return resultSet;
        }
    }

    @Override
    public ResultSet createGeneratedKeys(ResultSet resultSet, ConnectionInfo connectionInfo, ProxyConfig proxyConfig) {
        ResultSetProxyLogicFactory factory = proxyConfig.getGeneratedKeysProxyLogicFactory();
        return (ResultSet) Proxy.newProxyInstance(ProxyJdbcObject.class.getClassLoader(),
                new Class[]{ProxyJdbcObject.class, ResultSet.class},
                new ResultSetInvocationHandler(factory, resultSet, connectionInfo, proxyConfig));
    }


}
