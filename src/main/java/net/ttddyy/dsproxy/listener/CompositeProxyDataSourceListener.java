package net.ttddyy.dsproxy.listener;

import java.util.ArrayList;
import java.util.List;

/**
 * Execute chain of {@link ProxyDataSourceListener}.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.3
 */
public class CompositeProxyDataSourceListener implements ProxyDataSourceListener {
    private List<ProxyDataSourceListener> listeners = new ArrayList<>();

    @Override
    public void beforeMethod(MethodExecutionContext executionContext) {
        for (ProxyDataSourceListener listener : this.listeners) {
            listener.beforeMethod(executionContext);
        }
    }

    @Override
    public void afterMethod(MethodExecutionContext executionContext) {
        for (ProxyDataSourceListener listener : this.listeners) {
            listener.afterMethod(executionContext);
        }
    }

    public boolean addListener(ProxyDataSourceListener listener) {
        return this.listeners.add(listener);
    }

    public List<ProxyDataSourceListener> getListeners() {
        return listeners;
    }

    public void setListeners(List<ProxyDataSourceListener> listeners) {
        this.listeners = listeners;
    }
}
