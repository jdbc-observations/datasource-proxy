package net.ttddyy.dsproxy.proxy.delegate;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.StatementType;
import net.ttddyy.dsproxy.proxy.ConnectionProxyLogic;
import net.ttddyy.dsproxy.proxy.DataSourceProxyLogic;
import net.ttddyy.dsproxy.proxy.JdbcProxyFactory;
import net.ttddyy.dsproxy.proxy.ProxyConfig;
import net.ttddyy.dsproxy.proxy.ResultSetProxyLogic;
import net.ttddyy.dsproxy.proxy.ResultSetProxyLogicFactory;
import net.ttddyy.dsproxy.proxy.StatementProxyLogic;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Produce JDBC objects with concrete class implementation that delegates method invocations to proxy logic classes.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.5
 */
public class DelegatingJdbcProxyFactory implements JdbcProxyFactory {

    @Override
    public DataSource createDataSource(DataSource dataSource, ProxyConfig proxyConfig) {
        DataSourceProxyLogic proxyLogic = new DataSourceProxyLogic(dataSource, proxyConfig);
        return new DelegatingDataSource(proxyLogic);
    }

    @Override
    public Connection createConnection(Connection connection, ConnectionInfo connectionInfo, ProxyConfig proxyConfig) {
        ConnectionProxyLogic proxyLogic = new ConnectionProxyLogic(connection, connectionInfo, proxyConfig);
        return new DelegatingConnection(proxyLogic);
    }

    @Override
    public Statement createStatement(Statement statement, ConnectionInfo connectionInfo, Connection proxyConnection, ProxyConfig proxyConfig) {
        StatementProxyLogic proxyLogic = StatementProxyLogic.Builder.create()
                .statement(statement, StatementType.STATEMENT)
                .connectionInfo(connectionInfo)
                .proxyConnection(proxyConnection)
                .proxyConfig(proxyConfig)
                .build();

        return new DelegatingStatement(proxyLogic);
    }

    @Override
    public PreparedStatement createPreparedStatement(PreparedStatement preparedStatement, String query, ConnectionInfo connectionInfo, Connection proxyConnection, ProxyConfig proxyConfig) {
        StatementProxyLogic proxyLogic = StatementProxyLogic.Builder.create()
                .statement(preparedStatement, StatementType.PREPARED)
                .query(query)
                .connectionInfo(connectionInfo)
                .proxyConnection(proxyConnection)
                .proxyConfig(proxyConfig)
                .build();

        return new DelegatingPreparedStatement(proxyLogic);
    }

    @Override
    public CallableStatement createCallableStatement(CallableStatement callableStatement, String query, ConnectionInfo connectionInfo, Connection proxyConnection, ProxyConfig proxyConfig) {
        StatementProxyLogic proxyLogic = StatementProxyLogic.Builder.create()
                .statement(callableStatement, StatementType.CALLABLE)
                .query(query)
                .connectionInfo(connectionInfo)
                .proxyConnection(proxyConnection)
                .proxyConfig(proxyConfig)
                .build();

        return new DelegatingCallableStatement(proxyLogic);
    }

    @Override
    public ResultSet createResultSet(ResultSet resultSet, ConnectionInfo connectionInfo, ProxyConfig proxyConfig) {
        ResultSetProxyLogicFactory factory = proxyConfig.getResultSetProxyLogicFactory();
        ResultSetProxyLogic proxyLogic = factory.create(resultSet, connectionInfo, proxyConfig);
        return new DelegatingResultSet(proxyLogic);
    }

    @Override
    public ResultSet createGeneratedKeys(ResultSet resultSet, ConnectionInfo connectionInfo, ProxyConfig proxyConfig) {
        ResultSetProxyLogicFactory factory = proxyConfig.getGeneratedKeysProxyLogicFactory();
        ResultSetProxyLogic proxyLogic = factory.create(resultSet, connectionInfo, proxyConfig);
        return new DelegatingResultSet(proxyLogic);
    }
}
