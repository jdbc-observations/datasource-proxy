package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.listener.CallCheckMethodExecutionListener;
import net.ttddyy.dsproxy.listener.MethodExecutionContext;
import org.junit.Test;

import java.lang.reflect.Method;
import java.sql.ResultSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Tadaya Tsuyukubo
 */
public class SimpleResultSetProxyLogicTest {

    @Test
    public void testToString() throws Throwable {

        ResultSet rs = mock(ResultSet.class);
        SimpleResultSetProxyLogic logic = new SimpleResultSetProxyLogic(rs, new ConnectionInfo(), ProxyConfig.Builder.create().build());

        when(rs.toString()).thenReturn("my rs");

        Method method = Object.class.getMethod("toString");
        Object result = logic.invoke(method, null);

        assertThat(result).isInstanceOf(String.class).isEqualTo(rs.getClass().getSimpleName() + " [my rs]");
    }

    @Test
    public void testHashCode() throws Throwable {
        ResultSet rs = mock(ResultSet.class);
        SimpleResultSetProxyLogic logic = new SimpleResultSetProxyLogic(rs, new ConnectionInfo(), ProxyConfig.Builder.create().build());

        Method method = Object.class.getMethod("hashCode");
        Object result = logic.invoke(method, null);

        assertThat(result).isInstanceOf(Integer.class).isEqualTo(rs.hashCode());
    }

    @Test
    public void testEquals() throws Throwable {
        ResultSet rs = mock(ResultSet.class);
        SimpleResultSetProxyLogic logic = new SimpleResultSetProxyLogic(rs, new ConnectionInfo(), ProxyConfig.Builder.create().build());

        Method method = Object.class.getMethod("equals", Object.class);

        // equals(null)
        Object result = logic.invoke(method, new Object[]{null});
        assertThat(result).isEqualTo(false);

        // equals(true)
        result = logic.invoke(method, new Object[]{rs});
        assertThat(result).isEqualTo(true);
    }

    @Test
    public void methodExecutionListener() throws Throwable {
        CallCheckMethodExecutionListener listener = new CallCheckMethodExecutionListener();
        ProxyConfig proxyConfig = ProxyConfig.Builder.create().methodListener(listener).build();
        ResultSet rs = mock(ResultSet.class);
        ConnectionInfo connectionInfo = new ConnectionInfo();

        SimpleResultSetProxyLogic logic = new SimpleResultSetProxyLogic(rs, connectionInfo, proxyConfig);

        Method method = ResultSet.class.getMethod("close");
        logic.invoke(method, new Object[]{});

        assertTrue(listener.isBeforeMethodCalled());
        assertTrue(listener.isAfterMethodCalled());

        MethodExecutionContext executionContext = listener.getAfterMethodContext();
        assertSame("method should come from interface",
                ResultSet.class, executionContext.getMethod().getDeclaringClass());
        assertSame("close", executionContext.getMethod().getName());
        assertSame(rs, executionContext.getTarget());
        assertSame(connectionInfo, executionContext.getConnectionInfo());
    }

}
