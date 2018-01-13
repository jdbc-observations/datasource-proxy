package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.proxy.jdk.JdkJdbcProxyFactory;
import org.junit.Test;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author Tadaya Tsuyukubo
 */
public class NativeJdbcExtractUtilsTest {

    @Test
    public void testGetConnection() {
        Connection source = mock(Connection.class);
        Connection proxy = new JdkJdbcProxyFactory().createConnection(source, getConnectionInfo(), ProxyConfig.Builder.create().build());

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
        Statement proxy = new JdkJdbcProxyFactory().createStatement(source, getConnectionInfo(), null, ProxyConfig.Builder.create().build());

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
        PreparedStatement proxy = new JdkJdbcProxyFactory().createPreparedStatement(source, null, getConnectionInfo(), null, ProxyConfig.Builder.create().build(), false);

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
        CallableStatement proxy = new JdkJdbcProxyFactory().createCallableStatement(source, null, getConnectionInfo(), null, ProxyConfig.Builder.create().build());

        // check proxy
        CallableStatement result = NativeJdbcExtractUtils.getCallableStatement(proxy);
        assertThat(result).isSameAs(source);

        // check non-proxy
        result = NativeJdbcExtractUtils.getCallableStatement(source);
        assertThat(result).isSameAs(source);
    }

    private ConnectionInfo getConnectionInfo() {
        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setDataSourceName("myDs");
        return connectionInfo;
    }
}
