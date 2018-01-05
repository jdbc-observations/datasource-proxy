package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ConnectionInfo;
import org.junit.Before;
import org.junit.Test;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author Tadaya Tsuyukubo
 */
public class NativeJdbcExtractUtilsTest {

    private ProxyConfig proxyConfig;
    private JdbcProxyFactory jdbcProxyFactory;

    @Before
    public void setUp() {
        this.proxyConfig = TestProxyConfigBuilder.create().build();
        this.jdbcProxyFactory = this.proxyConfig.getJdbcProxyFactory();
    }

    @Test
    public void testGetConnection() {

        Connection source = mock(Connection.class);
        Connection proxy = this.jdbcProxyFactory.createConnection(source, getConnectionInfo(), this.proxyConfig);

        // check proxy
        Connection result = NativeJdbcExtractUtils.getConnection(proxy);
        assertThat(result).isSameAs(source);

        // check non-proxy
        result = NativeJdbcExtractUtils.getConnection(source);
        assertThat(result).isSameAs(source);
    }

    @Test
    public void testGetStatement() {
        Statement source = mock(Statement.class);
        Statement proxy = this.jdbcProxyFactory.createStatement(source, getConnectionInfo(), null, this.proxyConfig);

        // check proxy
        Statement result = NativeJdbcExtractUtils.getStatement(proxy);
        assertThat(result).isSameAs(source);

        // check non-proxy
        result = NativeJdbcExtractUtils.getStatement(source);
        assertThat(result).isSameAs(source);
    }

    @Test
    public void testGetPreparedStatement() {
        PreparedStatement source = mock(PreparedStatement.class);
        PreparedStatement proxy = this.jdbcProxyFactory.createPreparedStatement(source, null, getConnectionInfo(), null, this.proxyConfig);

        // check proxy
        PreparedStatement result = NativeJdbcExtractUtils.getPreparedStatement(proxy);
        assertThat(result).isSameAs(source);

        // check non-proxy
        result = NativeJdbcExtractUtils.getPreparedStatement(source);
        assertThat(result).isSameAs(source);
    }

    @Test
    public void testGetCallableStatement() {
        CallableStatement source = mock(CallableStatement.class);
        CallableStatement proxy = this.jdbcProxyFactory.createCallableStatement(source, null, getConnectionInfo(), null, this.proxyConfig);

        // check proxy
        CallableStatement result = NativeJdbcExtractUtils.getCallableStatement(proxy);
        assertThat(result).isSameAs(source);

        // check non-proxy
        result = NativeJdbcExtractUtils.getCallableStatement(source);
        assertThat(result).isSameAs(source);
    }

    @Test
    public void testGetResultSet() {

        // specify result-set proxy factory
        this.proxyConfig = TestProxyConfigBuilder.create().resultSetProxyLogicFactory(new SimpleResultSetProxyLogicFactory()).build();
        this.jdbcProxyFactory = this.proxyConfig.getJdbcProxyFactory();

        ResultSet source = mock(ResultSet.class);
        ResultSet proxy = this.jdbcProxyFactory.createResultSet(source, getConnectionInfo(), this.proxyConfig);

        // check proxy
        ResultSet result = NativeJdbcExtractUtils.getResultSet(proxy);
        assertThat(result).isSameAs(source);

        // check non-proxy
        result = NativeJdbcExtractUtils.getResultSet(source);
        assertThat(result).isSameAs(source);
    }

    private ConnectionInfo getConnectionInfo() {
        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setDataSourceName("myDs");
        return connectionInfo;
    }
}
