package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ConnectionIdManager;
import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.proxy.jdk.JdkJdbcProxyFactory;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;

/**
 * @author Tadaya Tsuyukubo
 */
public class JdkJdbcProxyFactoryTest {

    private JdkJdbcProxyFactory factory = new JdkJdbcProxyFactory();

    @Test
    public void testCreateConnection() {
        Connection conn = mock(Connection.class);
        InterceptorHolder interceptors = mock(InterceptorHolder.class);

        Connection result = factory.createConnection(conn, interceptors, getConnectionInfo());

        assertThat(result, is(notNullValue()));
        assertThat(result, is(not(sameInstance(conn))));
        assertThat(result, is(instanceOf(ProxyJdbcObject.class)));
    }

    @Test
    public void testCreateStatement() {
        Statement stmt = mock(Statement.class);
        InterceptorHolder interceptors = mock(InterceptorHolder.class);

        Statement result = factory.createStatement(stmt, interceptors, getConnectionInfo(), null);

        assertThat(result, is(notNullValue()));
        assertThat(result, is(not(sameInstance(stmt))));
        assertThat(result, is(instanceOf(ProxyJdbcObject.class)));
    }

    @Test
    public void testCreatePreparedStatement() {
        PreparedStatement ps = mock(PreparedStatement.class);
        InterceptorHolder interceptors = mock(InterceptorHolder.class);

        PreparedStatement result = factory.createPreparedStatement(ps, "my-query", interceptors, getConnectionInfo(), null);

        assertThat(result, is(notNullValue()));
        assertThat(result, is(not(sameInstance(ps))));
        assertThat(result, is(instanceOf(ProxyJdbcObject.class)));
    }

    @Test
    public void testCreateCallableStatement() {
        CallableStatement cs = mock(CallableStatement.class);
        InterceptorHolder interceptors = mock(InterceptorHolder.class);

        CallableStatement result = factory.createCallableStatement(cs, "my-query", interceptors, getConnectionInfo(), null);

        assertThat(result, is(notNullValue()));
        assertThat(result, is(not(sameInstance(cs))));
        assertThat(result, is(instanceOf(ProxyJdbcObject.class)));
    }

    @Test
    public void testCreateDataSource() {
        DataSource ds = mock(DataSource.class);
        InterceptorHolder interceptors = mock(InterceptorHolder.class);

        DataSource result = factory.createDataSource(ds, interceptors, "my-ds", ConnectionIdManager.DEFAULT);

        assertThat(result, is(notNullValue()));
        assertThat(result, is(not(sameInstance(ds))));
        assertThat(result, is(instanceOf(ProxyJdbcObject.class)));
    }


    private ConnectionInfo getConnectionInfo() {
        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setDataSourceName("my-ds");
        return connectionInfo;
    }

}
