package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.listener.ProxyDataSourceListener;
import net.ttddyy.dsproxy.listener.CallCheckMethodListener;
import net.ttddyy.dsproxy.listener.MethodExecutionContext;
import net.ttddyy.dsproxy.proxy.jdk.ConnectionInvocationHandler;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Tadaya Tsuyukubo
 */
class DataSourceProxyLogicMockTest {

    private static final String DS_NAME = "myDS";

    @Test
    void getConnection() throws Throwable {
        DataSource ds = mock(DataSource.class);
        DataSourceProxyLogic logic = getProxyLogic(ds);

        Method method = DataSource.class.getMethod("getConnection");
        Object result = logic.invoke(null, method, null);

        assertThat(result).isInstanceOf(Connection.class);
        verifyConnection((Connection) result);
        verify(ds).getConnection();
    }

    private DataSourceProxyLogic getProxyLogic(DataSource ds) throws Throwable {
        ProxyDataSourceListener listener = mock(ProxyDataSourceListener.class);
        when(ds.getConnection()).thenReturn(mock(Connection.class));

        ProxyConfig proxyConfig = ProxyConfig.Builder.create()
                .dataSourceName(DS_NAME)
                .listener(listener)
                .build();
        return new DataSourceProxyLogic(ds, proxyConfig);
    }

    private void verifyConnection(Connection conn) {
        assertThat(conn).isNotNull();

        assertThat(Proxy.isProxyClass(conn.getClass())).isTrue();
        InvocationHandler handler = Proxy.getInvocationHandler(conn);
        assertThat(handler).isInstanceOf(ConnectionInvocationHandler.class);

        assertThat(conn).isInstanceOf(ProxyJdbcObject.class);
    }

    @Test
    void getTarget() throws Throwable {
        DataSource ds = mock(DataSource.class);
        DataSourceProxyLogic logic = getProxyLogic(ds);

        Method method = ProxyJdbcObject.class.getMethod("getTarget");
        Object result = logic.invoke(null, method, null);

        assertThat(result).isInstanceOf(DataSource.class);
        DataSource resultDS = (DataSource) result;
        assertThat(resultDS).isSameAs(ds);
    }

    @Test
    void unwrap() throws Throwable {
        DataSource ds = mock(DataSource.class);
        when(ds.unwrap(String.class)).thenReturn("called");

        DataSourceProxyLogic logic = getProxyLogic(ds);

        Method method = Connection.class.getMethod("unwrap", Class.class);
        Object result = logic.invoke(null, method, new Object[]{String.class});

        verify(ds).unwrap(String.class);
        assertThat(result).isEqualTo("called");
    }

    @Test
    void isWrapperFor() throws Throwable {
        DataSource ds = mock(DataSource.class);
        when(ds.isWrapperFor(String.class)).thenReturn(true);

        DataSourceProxyLogic logic = getProxyLogic(ds);

        Method method = Connection.class.getMethod("isWrapperFor", Class.class);
        Object result = logic.invoke(null, method, new Object[]{String.class});

        verify(ds).isWrapperFor(String.class);
        assertThat(result).isEqualTo(true);
    }

    @Test
    void testToString() throws Throwable {
        DataSource ds = mock(DataSource.class);

        when(ds.toString()).thenReturn("my ds");
        DataSourceProxyLogic logic = getProxyLogic(ds);

        Method method = Object.class.getMethod("toString");
        Object result = logic.invoke(null, method, null);

        assertThat(result).isEqualTo(ds.getClass().getSimpleName() + " [my ds]");
    }

    @Test
    void testHashCode() throws Throwable {
        DataSource ds = mock(DataSource.class);
        DataSourceProxyLogic logic = getProxyLogic(ds);

        Method method = Object.class.getMethod("hashCode");
        Object result = logic.invoke(null, method, null);

        assertThat(result).isEqualTo(ds.hashCode());
    }

    @Test
    void equals() throws Throwable {
        DataSource ds = mock(DataSource.class);
        DataSourceProxyLogic logic = getProxyLogic(ds);

        Method method = Object.class.getMethod("equals", Object.class);

        // equals(null)
        Object result = logic.invoke(null, method, new Object[]{null});
        assertThat(result).isEqualTo(false);

        // equals(true)
        result = logic.invoke(null, method, new Object[]{ds});
        assertThat(result).isEqualTo(true);
    }

    @Test
    void methodExecutionListener() throws Throwable {
        CallCheckMethodListener listener = new CallCheckMethodListener();

        DataSource ds = mock(DataSource.class);
        when(ds.getConnection()).thenReturn(mock(Connection.class));

        ProxyConfig proxyConfig = ProxyConfig.Builder.create()
                .dataSourceName(DS_NAME)
                .listener(listener)
                .build();
        DataSourceProxyLogic logic = new DataSourceProxyLogic(ds, proxyConfig);


        Method method = DataSource.class.getMethod("getConnection");
        logic.invoke(null, method, new Object[]{});

        assertThat(listener.isBeforeMethodCalled()).isTrue();
        assertThat(listener.isAfterMethodCalled()).isTrue();

        MethodExecutionContext executionContext = listener.getAfterMethodContext();
        assertThat(executionContext.getMethod().getDeclaringClass()).as("method should come from interface").isSameAs(DataSource.class);
        assertThat(executionContext.getMethod().getName()).isSameAs("getConnection");
        assertThat(executionContext.getTarget()).isSameAs(ds);
        assertThat(executionContext.getConnectionInfo()).isNotNull();
    }

    @Test
    void proxyConfig() throws Throwable {
        DataSource ds = mock(DataSource.class);
        ProxyConfig proxyConfig = ProxyConfig.Builder.create().build();
        DataSourceProxyLogic logic = new DataSourceProxyLogic(ds, proxyConfig);

        Method method = ProxyJdbcObject.class.getMethod("getProxyConfig");
        Object result = logic.invoke(null, method, null);

        assertThat(result).isSameAs(proxyConfig);
    }

}
