package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.listener.lifecycle.JdbcLifecycleEventExecutionListener;
import net.ttddyy.dsproxy.listener.lifecycle.JdbcLifecycleEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Execute chain of {@link ProxyDataSourceListener}.
 *
 * @author Tadaya Tsuyukubo
 * @since 2.0
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

    @Override
    public void beforeQuery(QueryExecutionContext executionContext) {
        for (ProxyDataSourceListener listener : this.listeners) {
            listener.beforeQuery(executionContext);
        }
    }

    @Override
    public void afterQuery(QueryExecutionContext executionContext) {
        for (ProxyDataSourceListener listener : this.listeners) {
            listener.afterQuery(executionContext);
        }
    }


    /**
     * Add a {@link ProxyDataSourceListener}.
     *
     * @param listener a listener
     * @return {@code true} as specified by {@link List#add(Object)}
     */
    public boolean addListener(ProxyDataSourceListener listener) {
        if (listener instanceof JdbcLifecycleEventListener) {
            return this.listeners.add(new JdbcLifecycleEventExecutionListener((JdbcLifecycleEventListener) listener));
        }
        return this.listeners.add(listener);
    }

    /**
     * Adda collection of {@link ProxyDataSourceListener}s.
     *
     * @param listeners collection of listeners
     * @return {@code true} if this list changed as a result of the call
     */
    public boolean addListeners(Collection<ProxyDataSourceListener> listeners) {
        boolean result = false;
        for (ProxyDataSourceListener listener : listeners) {
            result |= addListener(listener);  // perform "JdbcLifecycleEventExecutionListener" conversion
        }
        return result;
    }

    public List<ProxyDataSourceListener> getListeners() {
        return this.listeners;
    }

    public void setListeners(List<ProxyDataSourceListener> listeners) {
        this.listeners = listeners;
    }

}
