package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.listener.ProxyDataSourceListener;
import net.ttddyy.dsproxy.proxy.CompositeProxyDataSourceListener;
import net.ttddyy.dsproxy.proxy.ProxyConfig;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author Tadaya Tsuyukubo
 */
class ProxyConfigBuilderTest {

    private static List<ProxyDataSourceListener> listeners(ProxyConfig proxyConfig) {
        return ((CompositeProxyDataSourceListener) proxyConfig.getListener()).getListeners();
    }

    @Test
    void multipleListeners() {
        ProxyDataSourceListener listener1 = mock(ProxyDataSourceListener.class);
        ProxyDataSourceListener listener2 = mock(ProxyDataSourceListener.class);

        // no listener
        assertThat(listeners(ProxyConfig.Builder.create().build())).isEmpty();

        // specify listeners one by one; registration order is kept
        ProxyConfig proxyConfig = ProxyConfig.Builder.create().listener(listener1).listener(listener2).build();
        assertThat(listeners(proxyConfig)).containsExactly(listener1, listener2);
    }

    @Test
    void fromCopiesListeners() {
        ProxyDataSourceListener listener1 = mock(ProxyDataSourceListener.class);
        ProxyDataSourceListener listener2 = mock(ProxyDataSourceListener.class);
        ProxyDataSourceListener listener3 = mock(ProxyDataSourceListener.class);

        ProxyConfig original = ProxyConfig.Builder.create().listener(listener1).listener(listener2).build();
        ProxyConfig copied = ProxyConfig.Builder.from(original).listener(listener3).build();

        assertThat(listeners(copied)).containsExactly(listener1, listener2, listener3);
        assertThat(listeners(original)).as("original is not modified").containsExactly(listener1, listener2);
    }
}
