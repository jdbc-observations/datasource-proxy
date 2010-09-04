package net.ttddyy.dsproxy.proxy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;

import net.ttddyy.dsproxy.proxy.JdbcProxyFactory;
import net.ttddyy.dsproxy.proxy.NativeJdbcExtractUtils;
import org.testng.annotations.Test;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * @author Tadaya Tsuyukubo
 */
public class NativeJdbcExtractUtilsTest {

    @Test
    public void testGetConnection() {
        Connection source = mock(Connection.class);
        Connection proxy = JdbcProxyFactory.createConnection(source, null);

        // check proxy
        Connection result = NativeJdbcExtractUtils.getConnection(proxy);
        assertThat(result, is(sameInstance(source)));

        // check non-proxy
        result = NativeJdbcExtractUtils.getConnection(source);
        assertThat(result, is(sameInstance(source)));
    }

    @Test
    public void testGetStatement() {
        Statement source = mock(Statement.class);
        Statement proxy = JdbcProxyFactory.createStatement(source, null);

        // check proxy
        Statement result = NativeJdbcExtractUtils.getStatement(proxy);
        assertThat(result, is(sameInstance(source)));

        // check non-proxy
        result = NativeJdbcExtractUtils.getStatement(source);
        assertThat(result, is(sameInstance(source)));
    }

    @Test
    public void testGetPreparedStatement() {
        PreparedStatement source = mock(PreparedStatement.class);
        PreparedStatement proxy = JdbcProxyFactory.createPreparedStatement(source, null, null);

        // check proxy
        PreparedStatement result = NativeJdbcExtractUtils.getPreparedStatement(proxy);
        assertThat(result, is(sameInstance(source)));

        // check non-proxy
        result = NativeJdbcExtractUtils.getPreparedStatement(source);
        assertThat(result, is(sameInstance(source)));
    }

    @Test
    public void testGetCallableStatement() {
        CallableStatement source = mock(CallableStatement.class);
        CallableStatement proxy = JdbcProxyFactory.createCallableStatement(source, null, null, null);

        // check proxy
        CallableStatement result = NativeJdbcExtractUtils.getCallableStatement(proxy);
        assertThat(result, is(sameInstance(source)));

        // check non-proxy
        result = NativeJdbcExtractUtils.getCallableStatement(source);
        assertThat(result, is(sameInstance(source)));
    }
}
