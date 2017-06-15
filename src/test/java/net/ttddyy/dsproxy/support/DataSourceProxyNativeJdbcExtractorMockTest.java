package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.proxy.jdk.JdkJdbcProxyFactory;
import org.junit.Test;
import org.springframework.jdbc.support.nativejdbc.NativeJdbcExtractor;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Wrapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
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

        Connection proxy = new JdkJdbcProxyFactory().createConnection(org, null, getConnectionInfo());

        boolean i = proxy instanceof Wrapper;
        System.out.println(i);

        DataSourceProxyNativeJdbcExtractor extractor = new DataSourceProxyNativeJdbcExtractor();
        Connection result = extractor.doGetNativeConnection(proxy);
        assertThat(result, is(notNullValue()));
        assertThat(result, is(sameInstance(org)));

        Connection nonProxy = mock(Connection.class);
        result = extractor.doGetNativeConnection(nonProxy);
        assertThat(result, is(notNullValue()));
        assertThat(result, is(sameInstance(nonProxy)));
    }

    @Test
    public void testDoGetNativeConnectionWithDelegate() throws Exception {

        Connection org = mock(Connection.class);
        Connection proxy = new JdkJdbcProxyFactory().createConnection(org, null, getConnectionInfo());

        NativeJdbcExtractor delegate = mock(NativeJdbcExtractor.class);
        Connection expected = mock(Connection.class);
        when(delegate.getNativeConnection(org)).thenReturn(expected);

        DataSourceProxyNativeJdbcExtractor extractor = new DataSourceProxyNativeJdbcExtractor();
        extractor.setDelegate(delegate);

        Connection result = extractor.doGetNativeConnection(proxy);

        verify(delegate).getNativeConnection(org);
        assertThat(result, is(sameInstance(expected)));
    }

    @Test
    public void testGetNativeStatement() throws Exception {
        Statement org = mock(Statement.class);
        Statement proxy = new JdkJdbcProxyFactory().createStatement(org, null, getConnectionInfo(), null);

        DataSourceProxyNativeJdbcExtractor extractor = new DataSourceProxyNativeJdbcExtractor();
        Statement result = extractor.getNativeStatement(proxy);
        assertThat(result, is(notNullValue()));
        assertThat(result, is(sameInstance(org)));

        Statement nonProxy = mock(Statement.class);
        result = extractor.getNativeStatement(nonProxy);
        assertThat(result, is(notNullValue()));
        assertThat(result, is(sameInstance(nonProxy)));
    }

    @Test
    public void testGetNativeStatementWithDelegate() throws Exception {

        Statement org = mock(Statement.class);
        Statement proxy = new JdkJdbcProxyFactory().createStatement(org, null, getConnectionInfo(), null);

        NativeJdbcExtractor delegate = mock(NativeJdbcExtractor.class);
        Statement expected = mock(Statement.class);
        when(delegate.getNativeStatement(org)).thenReturn(expected);

        DataSourceProxyNativeJdbcExtractor extractor = new DataSourceProxyNativeJdbcExtractor();
        extractor.setDelegate(delegate);

        Statement result = extractor.getNativeStatement(proxy);

        verify(delegate).getNativeStatement(org);
        assertThat(result, is(sameInstance(expected)));
    }


    @Test
    public void testGetNativePreparedStatement() throws Exception {
        PreparedStatement org = mock(PreparedStatement.class);
        PreparedStatement proxy = new JdkJdbcProxyFactory().createPreparedStatement(org, null, null, getConnectionInfo(), null);

        DataSourceProxyNativeJdbcExtractor extractor = new DataSourceProxyNativeJdbcExtractor();
        PreparedStatement result = extractor.getNativePreparedStatement(proxy);
        assertThat(result, is(notNullValue()));
        assertThat(result, is(sameInstance(org)));

        PreparedStatement nonProxy = mock(PreparedStatement.class);
        result = extractor.getNativePreparedStatement(nonProxy);
        assertThat(result, is(notNullValue()));
        assertThat(result, is(sameInstance(nonProxy)));
    }

    @Test
    public void testGetNativePreparedStatementWithDelegate() throws Exception {

        PreparedStatement org = mock(PreparedStatement.class);
        PreparedStatement proxy = new JdkJdbcProxyFactory().createPreparedStatement(org, null, null, getConnectionInfo(), null);

        NativeJdbcExtractor delegate = mock(NativeJdbcExtractor.class);
        PreparedStatement expected = mock(PreparedStatement.class);
        when(delegate.getNativePreparedStatement(org)).thenReturn(expected);

        DataSourceProxyNativeJdbcExtractor extractor = new DataSourceProxyNativeJdbcExtractor();
        extractor.setDelegate(delegate);

        PreparedStatement result = extractor.getNativePreparedStatement(proxy);

        verify(delegate).getNativePreparedStatement(org);
        assertThat(result, is(sameInstance(expected)));
    }


    @Test
    public void testGetNativeCallableStatement() throws Exception {
        CallableStatement org = mock(CallableStatement.class);
        CallableStatement proxy = new JdkJdbcProxyFactory().createCallableStatement(org, null, null, null, null);

        DataSourceProxyNativeJdbcExtractor extractor = new DataSourceProxyNativeJdbcExtractor();
        CallableStatement result = extractor.getNativeCallableStatement(proxy);
        assertThat(result, is(notNullValue()));
        assertThat(result, is(sameInstance(org)));

        CallableStatement nonProxy = mock(CallableStatement.class);
        result = extractor.getNativeCallableStatement(nonProxy);
        assertThat(result, is(notNullValue()));
        assertThat(result, is(sameInstance(nonProxy)));
    }

    @Test
    public void testGetNativeCallableStatementWithDelegate() throws Exception {

        CallableStatement org = mock(CallableStatement.class);
        CallableStatement proxy = new JdkJdbcProxyFactory().createCallableStatement(org, null, null, null, null);

        NativeJdbcExtractor delegate = mock(NativeJdbcExtractor.class);
        CallableStatement expected = mock(CallableStatement.class);
        when(delegate.getNativeCallableStatement(org)).thenReturn(expected);

        DataSourceProxyNativeJdbcExtractor extractor = new DataSourceProxyNativeJdbcExtractor();
        extractor.setDelegate(delegate);

        CallableStatement result = extractor.getNativeCallableStatement(proxy);

        verify(delegate).getNativeCallableStatement(org);
        assertThat(result, is(sameInstance(expected)));
    }

    private ConnectionInfo getConnectionInfo() {
        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setDataSourceName("myDS");
        return connectionInfo;
    }
}
