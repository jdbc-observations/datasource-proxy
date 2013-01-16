package net.ttddyy.dsproxy.proxy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertTrue;

import net.ttddyy.dsproxy.proxy.ConnectionInvocationHandler;
import net.ttddyy.dsproxy.proxy.JdbcProxyFactory;
import net.ttddyy.dsproxy.proxy.ProxyJdbcObject;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;

/**
 * @author Tadaya Tsuyukubo
 */
public class DataSourceInvocationHandlerMockTest {

    private static final String DS_NAME = "myDS";

    @Test
    public void testGetConnection() throws Throwable {
        DataSource ds = mock(DataSource.class);
        DataSource dataSource = getProxyDataSource(ds);

        Connection connection = dataSource.getConnection();

        verifyConnection(connection);
        verify(ds).getConnection();
    }

    private DataSource getProxyDataSource(DataSource ds) {
        return new JdkJdbcProxyFactory().createDataSource(ds, null, DS_NAME);
    }

    private void verifyConnection(Connection conn) {
        assertThat(conn, notNullValue());

        assertTrue(Proxy.isProxyClass(conn.getClass()));
        InvocationHandler handler = Proxy.getInvocationHandler(conn);
        assertThat(handler, is(instanceOf(ConnectionInvocationHandler.class)));

        assertThat(conn, is(instanceOf(ProxyJdbcObject.class)));
    }

    @Test
    public void testGetTarget() {
        DataSource orig = mock(DataSource.class);
        DataSource proxy = getProxyDataSource(orig);

        assertThat(proxy, is(not(sameInstance(orig))));
        assertThat(proxy, is(instanceOf(ProxyJdbcObject.class)));

        Object result = ((ProxyJdbcObject) proxy).getTarget();

        assertThat(result, is(instanceOf(DataSource.class)));

        DataSource resultDS = (DataSource) result;

        assertThat(resultDS, is(sameInstance(orig)));
    }

    @Test
    public void testUnwrap() throws Exception {
        DataSource mock = mock(DataSource.class);
        when(mock.unwrap(String.class)).thenReturn("called");

        DataSource ds = getProxyDataSource(mock);

        String result = ds.unwrap(String.class);

        verify(mock).unwrap(String.class);
        assertThat(result, is("called"));
    }

    @Test
    public void testIsWrapperFor() throws Exception {
        DataSource mock = mock(DataSource.class);
        when(mock.isWrapperFor(String.class)).thenReturn(true);

        DataSource ds = getProxyDataSource(mock);

        boolean result = ds.isWrapperFor(String.class);

        verify(mock).isWrapperFor(String.class);
        assertThat(result, is(true));
    }

}
