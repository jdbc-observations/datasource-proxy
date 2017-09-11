package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.listener.ChainListener;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.proxy.ProxyConfig;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author Tadaya Tsuyukubo
 */
public class ProxyConfigBuilderTest {

    @Test
    public void multipleQueryListeners() {
        ProxyConfig proxyConfig;
        List<QueryExecutionListener> listeners;
        ChainListener chainListener;

        QueryExecutionListener listener1 = mock(QueryExecutionListener.class);
        QueryExecutionListener listener2 = mock(QueryExecutionListener.class);
        QueryExecutionListener listener3 = mock(QueryExecutionListener.class);

        // specify listeners directly one by one
        proxyConfig = ProxyConfig.Builder.create().queryListener(listener1).queryListener(listener2).build();
        listeners = proxyConfig.getQueryListener().getListeners();
        assertThat(listeners).hasSize(2).contains(listener1, listener2);

        // with empty ChainListener
        proxyConfig = ProxyConfig.Builder.create().queryListener(new ChainListener()).build();
        listeners = proxyConfig.getQueryListener().getListeners();
        assertThat(listeners).isEmpty();

        // with ChainListener containing some listeners
        chainListener = new ChainListener();
        chainListener.addListener(listener1);
        chainListener.addListener(listener2);

        proxyConfig = ProxyConfig.Builder.create().queryListener(chainListener).build();
        listeners = proxyConfig.getQueryListener().getListeners();
        assertThat(listeners).hasSize(2).contains(listener1, listener2);

        // with adding a chain listener and a listener
        chainListener = new ChainListener();
        chainListener.addListener(listener1);
        chainListener.addListener(listener2);

        proxyConfig = ProxyConfig.Builder.create().queryListener(chainListener).queryListener(listener3).build();
        listeners = proxyConfig.getQueryListener().getListeners();
        assertThat(listeners).hasSize(3).contains(listener1, listener2, listener3);


    }
}
