package net.ttddyy.dsproxy.proxy;

import org.testng.annotations.Test;

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

        Connection result = factory.createConnection(conn, interceptors, "my-ds");

        assertThat(result, is(notNullValue()));
        assertThat(result, is(not(sameInstance(conn))));
        assertThat(result, is(instanceOf(ProxyJdbcObject.class)));
    }

    @Test
    public void testCreateStatement() {
        Statement stmt = mock(Statement.class);
        InterceptorHolder interceptors = mock(InterceptorHolder.class);

        Statement result = factory.createStatement(stmt, interceptors, "my-ds");

        assertThat(result, is(notNullValue()));
        assertThat(result, is(not(sameInstance(stmt))));
        assertThat(result, is(instanceOf(ProxyJdbcObject.class)));
    }

    @Test
    public void testCreatePreparedStatement() {
        PreparedStatement ps = mock(PreparedStatement.class);
        InterceptorHolder interceptors = mock(InterceptorHolder.class);

        PreparedStatement result = factory.createPreparedStatement(ps, "my-query", interceptors, "my-ds");

        assertThat(result, is(notNullValue()));
        assertThat(result, is(not(sameInstance(ps))));
        assertThat(result, is(instanceOf(ProxyJdbcObject.class)));
    }

    @Test
    public void testCreateCallableStatement() {
        CallableStatement cs = mock(CallableStatement.class);
        InterceptorHolder interceptors = mock(InterceptorHolder.class);

        CallableStatement result = factory.createCallableStatement(cs, "my-query", interceptors, "my-ds");

        assertThat(result, is(notNullValue()));
        assertThat(result, is(not(sameInstance(cs))));
        assertThat(result, is(instanceOf(ProxyJdbcObject.class)));
    }

    @Test
    public void testCreateDataSource() {
        DataSource ds = mock(DataSource.class);
        InterceptorHolder interceptors = mock(InterceptorHolder.class);

        DataSource result = factory.createDataSource(ds, interceptors, "my-ds");

        assertThat(result, is(notNullValue()));
        assertThat(result, is(not(sameInstance(ds))));
        assertThat(result, is(instanceOf(ProxyJdbcObject.class)));
    }


}
