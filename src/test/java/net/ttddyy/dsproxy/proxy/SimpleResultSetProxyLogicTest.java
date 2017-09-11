package net.ttddyy.dsproxy.proxy;

import org.junit.Test;

import java.lang.reflect.Method;
import java.sql.ResultSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Tadaya Tsuyukubo
 */
public class SimpleResultSetProxyLogicTest {

    @Test
    public void testToString() throws Throwable {

        ResultSet rs = mock(ResultSet.class);
        SimpleResultSetProxyLogic logic = new SimpleResultSetProxyLogic(rs, ProxyConfig.Builder.create().build());

        when(rs.toString()).thenReturn("my rs");

        Method method = Object.class.getMethod("toString");
        Object result = logic.invoke(method, null);

        assertThat(result).isInstanceOf(String.class).isEqualTo(rs.getClass().getSimpleName() + " [my rs]");
    }

    @Test
    public void testHashCode() throws Throwable {
        ResultSet rs = mock(ResultSet.class);
        SimpleResultSetProxyLogic logic = new SimpleResultSetProxyLogic(rs, ProxyConfig.Builder.create().build());

        Method method = Object.class.getMethod("hashCode");
        Object result = logic.invoke(method, null);

        assertThat(result).isInstanceOf(Integer.class).isEqualTo(rs.hashCode());
    }

    @Test
    public void testEquals() throws Throwable {
        ResultSet rs = mock(ResultSet.class);
        SimpleResultSetProxyLogic logic = new SimpleResultSetProxyLogic(rs, ProxyConfig.Builder.create().build());

        Method method = Object.class.getMethod("equals", Object.class);

        // equals(null)
        Object result = logic.invoke(method, new Object[]{null});
        assertThat(result).isEqualTo(false);

        // equals(true)
        result = logic.invoke(method, new Object[]{rs});
        assertThat(result).isEqualTo(true);
    }

}
