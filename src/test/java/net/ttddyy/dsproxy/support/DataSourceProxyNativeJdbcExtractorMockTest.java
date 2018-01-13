package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.proxy.ProxyConfig;
import net.ttddyy.dsproxy.proxy.jdk.JdkJdbcProxyFactory;
import org.junit.Test;
import org.springframework.jdbc.support.nativejdbc.NativeJdbcExtractor;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Tadaya Tsuyukubo
 */
public class DataSourceProxyNativeJdbcExtractorMockTest {

    @Test
    public void testDoGetNativeConnection() throws Exception {
        Connection org = mock(Connection.class);

        Connection proxy = new JdkJdbcProxyFactory().createConnection(org, getConnectionInfo(), ProxyConfig.Builder.create().build());

        DataSourceProxyNativeJdbcExtractor extractor = new DataSourceProxyNativeJdbcExtractor();
        Connection result = extractor.doGetNativeConnection(proxy);
        assertThat(result).isSameAs(org);

        Connection nonProxy = mock(Connection.class);
        result = extractor.doGetNativeConnection(nonProxy);
        assertThat(result).isSameAs(nonProxy);
    }

    @Test
    public void testDoGetNativeConnectionWithDelegate() throws Exception {

        Connection org = mock(Connection.class);
        Connection proxy = new JdkJdbcProxyFactory().createConnection(org, getConnectionInfo(), ProxyConfig.Builder.create().build());

        NativeJdbcExtractor delegate = mock(NativeJdbcExtractor.class);
        Connection expected = mock(Connection.class);
        when(delegate.getNativeConnection(org)).thenReturn(expected);

        DataSourceProxyNativeJdbcExtractor extractor = new DataSourceProxyNativeJdbcExtractor();
        extractor.setDelegate(delegate);

        Connection result = extractor.doGetNativeConnection(proxy);

        verify(delegate).getNativeConnection(org);
        assertThat(result).isSameAs(expected);
    }

    @Test
    public void testGetNativeStatement() throws Exception {
        Statement org = mock(Statement.class);
        Statement proxy = new JdkJdbcProxyFactory().createStatement(org, getConnectionInfo(), null, ProxyConfig.Builder.create().build());

        DataSourceProxyNativeJdbcExtractor extractor = new DataSourceProxyNativeJdbcExtractor();
        Statement result = extractor.getNativeStatement(proxy);
        assertThat(result).isSameAs(org);

        Statement nonProxy = mock(Statement.class);
        result = extractor.getNativeStatement(nonProxy);
        assertThat(result).isSameAs(nonProxy);
    }

    @Test
    public void testGetNativeStatementWithDelegate() throws Exception {

        Statement org = mock(Statement.class);
        Statement proxy = new JdkJdbcProxyFactory().createStatement(org, getConnectionInfo(), null, ProxyConfig.Builder.create().build());

        NativeJdbcExtractor delegate = mock(NativeJdbcExtractor.class);
        Statement expected = mock(Statement.class);
        when(delegate.getNativeStatement(org)).thenReturn(expected);

        DataSourceProxyNativeJdbcExtractor extractor = new DataSourceProxyNativeJdbcExtractor();
        extractor.setDelegate(delegate);

        Statement result = extractor.getNativeStatement(proxy);

        verify(delegate).getNativeStatement(org);
        assertThat(result).isSameAs(expected);
    }


    @Test
    public void testGetNativePreparedStatement() throws Exception {
        PreparedStatement org = mock(PreparedStatement.class);
        PreparedStatement proxy = new JdkJdbcProxyFactory().createPreparedStatement(org, null, getConnectionInfo(), null, ProxyConfig.Builder.create().build(), false);

        DataSourceProxyNativeJdbcExtractor extractor = new DataSourceProxyNativeJdbcExtractor();
        PreparedStatement result = extractor.getNativePreparedStatement(proxy);
        assertThat(result).isSameAs(org);

        PreparedStatement nonProxy = mock(PreparedStatement.class);
        result = extractor.getNativePreparedStatement(nonProxy);
        assertThat(result).isSameAs(nonProxy);
    }

    @Test
    public void testGetNativePreparedStatementWithDelegate() throws Exception {

        PreparedStatement org = mock(PreparedStatement.class);
        PreparedStatement proxy = new JdkJdbcProxyFactory().createPreparedStatement(org, null, getConnectionInfo(), null, ProxyConfig.Builder.create().build(), false);

        NativeJdbcExtractor delegate = mock(NativeJdbcExtractor.class);
        PreparedStatement expected = mock(PreparedStatement.class);
        when(delegate.getNativePreparedStatement(org)).thenReturn(expected);

        DataSourceProxyNativeJdbcExtractor extractor = new DataSourceProxyNativeJdbcExtractor();
        extractor.setDelegate(delegate);

        PreparedStatement result = extractor.getNativePreparedStatement(proxy);

        verify(delegate).getNativePreparedStatement(org);
        assertThat(result).isSameAs(expected);
    }


    @Test
    public void testGetNativeCallableStatement() throws Exception {
        CallableStatement org = mock(CallableStatement.class);
        CallableStatement proxy = new JdkJdbcProxyFactory().createCallableStatement(org, null, null, null, ProxyConfig.Builder.create().build());

        DataSourceProxyNativeJdbcExtractor extractor = new DataSourceProxyNativeJdbcExtractor();
        CallableStatement result = extractor.getNativeCallableStatement(proxy);
        assertThat(result).isSameAs(org);

        CallableStatement nonProxy = mock(CallableStatement.class);
        result = extractor.getNativeCallableStatement(nonProxy);
        assertThat(result).isSameAs(nonProxy);
    }

    @Test
    public void testGetNativeCallableStatementWithDelegate() throws Exception {

        CallableStatement org = mock(CallableStatement.class);
        CallableStatement proxy = new JdkJdbcProxyFactory().createCallableStatement(org, null, null, null, ProxyConfig.Builder.create().build());

        NativeJdbcExtractor delegate = mock(NativeJdbcExtractor.class);
        CallableStatement expected = mock(CallableStatement.class);
        when(delegate.getNativeCallableStatement(org)).thenReturn(expected);

        DataSourceProxyNativeJdbcExtractor extractor = new DataSourceProxyNativeJdbcExtractor();
        extractor.setDelegate(delegate);

        CallableStatement result = extractor.getNativeCallableStatement(proxy);

        verify(delegate).getNativeCallableStatement(org);
        assertThat(result).isSameAs(expected);
    }

    private ConnectionInfo getConnectionInfo() {
        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setDataSourceName("myDS");
        return connectionInfo;
    }
}
