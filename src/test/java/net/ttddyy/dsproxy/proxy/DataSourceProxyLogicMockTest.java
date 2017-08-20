package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ConnectionIdManager;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.proxy.jdk.ConnectionInvocationHandler;
import net.ttddyy.dsproxy.proxy.jdk.JdkJdbcProxyFactory;
import net.ttddyy.dsproxy.transform.QueryTransformer;
import org.junit.Test;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * @author Tadaya Tsuyukubo
 */
public class DataSourceProxyLogicMockTest {

    private static final String DS_NAME = "myDS";

    @Test
    public void testGetConnection() throws Throwable {
        DataSource ds = mock(DataSource.class);
        DataSourceProxyLogic logic = getProxyLogic(ds);

        Method method = DataSource.class.getMethod("getConnection");
        Object result = logic.invoke(method, null);

        assertThat(result, is(instanceOf(Connection.class)));
        verifyConnection((Connection) result);
        verify(ds).getConnection();
    }

    private DataSourceProxyLogic getProxyLogic(DataSource ds) {
        QueryExecutionListener listener = mock(QueryExecutionListener.class);
        InterceptorHolder interceptorHolder = new InterceptorHolder(listener, QueryTransformer.DEFAULT);
        return new DataSourceProxyLogic(ds, interceptorHolder, DS_NAME, new JdkJdbcProxyFactory(), ConnectionIdManager.DEFAULT);
    }

    private void verifyConnection(Connection conn) {
        assertThat(conn, notNullValue());

        assertThat(Proxy.isProxyClass(conn.getClass()), is(true));
        InvocationHandler handler = Proxy.getInvocationHandler(conn);
        assertThat(handler, is(instanceOf(ConnectionInvocationHandler.class)));

        assertThat(conn, is(instanceOf(ProxyJdbcObject.class)));
    }

    @Test
    public void testGetTarget() throws Throwable {
        DataSource ds = mock(DataSource.class);
        DataSourceProxyLogic logic = getProxyLogic(ds);

        Method method = ProxyJdbcObject.class.getMethod("getTarget");
        Object result = logic.invoke(method, null);

        assertThat(result, is(instanceOf(DataSource.class)));
        DataSource resultDS = (DataSource) result;
        assertThat(resultDS, is(sameInstance(ds)));
    }

    @Test
    public void testUnwrap() throws Throwable {
        DataSource ds = mock(DataSource.class);
        when(ds.unwrap(String.class)).thenReturn("called");

        DataSourceProxyLogic logic = getProxyLogic(ds);

        Method method = Connection.class.getMethod("unwrap", Class.class);
        Object result = logic.invoke(method, new Object[]{String.class});

        verify(ds).unwrap(String.class);
        assertThat(result, is(instanceOf(String.class)));
        assertThat((String) result, is("called"));
    }

    @Test
    public void testIsWrapperFor() throws Throwable {
        DataSource ds = mock(DataSource.class);
        when(ds.isWrapperFor(String.class)).thenReturn(true);

        DataSourceProxyLogic logic = getProxyLogic(ds);

        Method method = Connection.class.getMethod("isWrapperFor", Class.class);
        Object result = logic.invoke(method, new Object[]{String.class});

        verify(ds).isWrapperFor(String.class);
        assertThat(result, is(instanceOf(boolean.class)));
        assertThat((Boolean) result, is(true));
    }

    @Test
    public void testToString() throws Throwable {
        DataSource ds = mock(DataSource.class);

        when(ds.toString()).thenReturn("my ds");
        DataSourceProxyLogic logic = getProxyLogic(ds);

        Method method = Object.class.getMethod("toString");
        Object result = logic.invoke(method, null);

        assertThat(result, is(instanceOf(String.class)));
        assertThat((String) result, is(ds.getClass().getSimpleName() + " [my ds]"));
    }

    @Test
    public void testHashCode() throws Throwable {
        DataSource ds = mock(DataSource.class);
        DataSourceProxyLogic logic = getProxyLogic(ds);

        Method method = Object.class.getMethod("hashCode");
        Object result = logic.invoke(method, null);

        assertThat(result, is(instanceOf(Integer.class)));
        assertThat((Integer) result, is(ds.hashCode()));
    }

    @Test
    public void testEquals() throws Throwable {
        DataSource ds = mock(DataSource.class);
        DataSourceProxyLogic logic = getProxyLogic(ds);

        Method method = Object.class.getMethod("equals", Object.class);

        // equals(null)
        Object result = logic.invoke(method, new Object[]{null});
        assertThat(result, is(instanceOf(Boolean.class)));
        assertThat((Boolean) result, is(false));

        // equals(true)
        result = logic.invoke(method, new Object[]{ds});
        assertThat(result, is(instanceOf(Boolean.class)));
        assertThat((Boolean) result, is(true));
    }

}
