package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.listener.CallCheckMethodListener;
import net.ttddyy.dsproxy.listener.MethodExecutionContext;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.sql.ResultSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Tadaya Tsuyukubo
 */
class SimpleResultSetProxyLogicTest {

    @Test
    void testToString() throws Throwable {

        ResultSet rs = mock(ResultSet.class);
        SimpleResultSetProxyLogic logic = new SimpleResultSetProxyLogic(rs, new ConnectionInfo(), ProxyConfig.Builder.create().build());

        when(rs.toString()).thenReturn("my rs");

        Method method = Object.class.getMethod("toString");
        Object result = logic.invoke(null, method, null);

        assertThat(result).isInstanceOf(String.class).isEqualTo(rs.getClass().getSimpleName() + " [my rs]");
    }

    @Test
    void testHashCode() throws Throwable {
        ResultSet rs = mock(ResultSet.class);
        SimpleResultSetProxyLogic logic = new SimpleResultSetProxyLogic(rs, new ConnectionInfo(), ProxyConfig.Builder.create().build());

        Method method = Object.class.getMethod("hashCode");
        Object result = logic.invoke(null, method, null);

        assertThat(result).isInstanceOf(Integer.class).isEqualTo(rs.hashCode());
    }

    @Test
    void equals() throws Throwable {
        ResultSet rs = mock(ResultSet.class);
        SimpleResultSetProxyLogic logic = new SimpleResultSetProxyLogic(rs, new ConnectionInfo(), ProxyConfig.Builder.create().build());

        Method method = Object.class.getMethod("equals", Object.class);

        // equals(null)
        Object result = logic.invoke(null, method, new Object[]{null});
        assertThat(result).isEqualTo(false);

        // equals(true)
        result = logic.invoke(null, method, new Object[]{rs});
        assertThat(result).isEqualTo(true);
    }

    @Test
    void methodExecutionListener() throws Throwable {
        CallCheckMethodListener listener = new CallCheckMethodListener();
        ProxyConfig proxyConfig = ProxyConfig.Builder.create().listener(listener).build();
        ResultSet rs = mock(ResultSet.class);
        ConnectionInfo connectionInfo = new ConnectionInfo();

        SimpleResultSetProxyLogic logic = new SimpleResultSetProxyLogic(rs, connectionInfo, proxyConfig);

        Method method = ResultSet.class.getMethod("close");
        logic.invoke(null, method, new Object[]{});

        assertThat(listener.isBeforeMethodCalled()).isTrue();
        assertThat(listener.isAfterMethodCalled()).isTrue();

        MethodExecutionContext executionContext = listener.getAfterMethodContext();
        assertThat(executionContext.getMethod().getDeclaringClass()).as("method should come from interface").isSameAs(ResultSet.class);
        assertThat(executionContext.getMethod().getName()).isSameAs("close");
        assertThat(executionContext.getTarget()).isSameAs(rs);
        assertThat(executionContext.getConnectionInfo()).isSameAs(connectionInfo);
    }

    @Test
    void proxyConfig() throws Throwable {
        ResultSet rs = mock(ResultSet.class);
        ProxyConfig proxyConfig = ProxyConfig.Builder.create().build();
        SimpleResultSetProxyLogic logic = new SimpleResultSetProxyLogic(rs, null, proxyConfig);
        Method method = ProxyJdbcObject.class.getMethod("getProxyConfig");
        Object result = logic.invoke(null, method, null);
        assertThat(result).isSameAs(proxyConfig);
    }

}
