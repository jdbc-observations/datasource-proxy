package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.listener.ChainListener;
import net.ttddyy.dsproxy.listener.CompositeMethodListener;
import net.ttddyy.dsproxy.listener.MethodExecutionListener;
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

    @Test
    public void multipleMethodListeners() {
        ProxyConfig proxyConfig;
        List<MethodExecutionListener> listeners;
        CompositeMethodListener compositeListener;

        MethodExecutionListener listener1 = mock(MethodExecutionListener.class);
        MethodExecutionListener listener2 = mock(MethodExecutionListener.class);
        MethodExecutionListener listener3 = mock(MethodExecutionListener.class);

        // specify listeners directly one by one
        proxyConfig = ProxyConfig.Builder.create().methodListener(listener1).methodListener(listener2).build();
        listeners = proxyConfig.getMethodListener().getListeners();
        assertThat(listeners).hasSize(2).contains(listener1, listener2);

        // with empty CompositeMethodListener
        proxyConfig = ProxyConfig.Builder.create().methodListener(new CompositeMethodListener()).build();
        listeners = proxyConfig.getMethodListener().getListeners();
        assertThat(listeners).isEmpty();

        // with CompositeMethodListener containing some listeners
        compositeListener = new CompositeMethodListener();
        compositeListener.addListener(listener1);
        compositeListener.addListener(listener2);

        proxyConfig = ProxyConfig.Builder.create().methodListener(compositeListener).build();
        listeners = proxyConfig.getMethodListener().getListeners();
        assertThat(listeners).hasSize(2).contains(listener1, listener2);

        // with adding a chain listener and a listener
        compositeListener = new CompositeMethodListener();
        compositeListener.addListener(listener1);
        compositeListener.addListener(listener2);

        proxyConfig = ProxyConfig.Builder.create().methodListener(compositeListener).methodListener(listener3).build();
        listeners = proxyConfig.getMethodListener().getListeners();
        assertThat(listeners).hasSize(3).contains(listener1, listener2, listener3);


    }
}
