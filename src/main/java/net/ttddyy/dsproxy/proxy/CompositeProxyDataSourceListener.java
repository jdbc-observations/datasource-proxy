package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.listener.MethodExecutionContext;
import net.ttddyy.dsproxy.listener.ProxyDataSourceListener;
import net.ttddyy.dsproxy.listener.QueryExecutionContext;

import java.util.List;

/**
 * Dispatches callbacks to the listeners in registration order.
 * An exception from a listener propagates and stops the later listeners.
 *
 * @author Tadaya Tsuyukubo
 * @since 2.0
 */
public class CompositeProxyDataSourceListener implements ProxyDataSourceListener {

    private final List<ProxyDataSourceListener> listeners;

    public CompositeProxyDataSourceListener(List<ProxyDataSourceListener> listeners) {
        this.listeners = List.copyOf(listeners);
    }

    public List<ProxyDataSourceListener> getListeners() {
        return this.listeners;
    }

    @Override
    public void beforeQuery(QueryExecutionContext context) {
        for (ProxyDataSourceListener listener : this.listeners) {
            listener.beforeQuery(context);
        }
    }

    @Override
    public void afterQuery(QueryExecutionContext context) {
        for (ProxyDataSourceListener listener : this.listeners) {
            listener.afterQuery(context);
        }
    }

    @Override
    public void beforeMethod(MethodExecutionContext context) {
        for (ProxyDataSourceListener listener : this.listeners) {
            listener.beforeMethod(context);
        }
    }

    @Override
    public void afterMethod(MethodExecutionContext context) {
        for (ProxyDataSourceListener listener : this.listeners) {
            listener.afterMethod(context);
        }
    }

}
