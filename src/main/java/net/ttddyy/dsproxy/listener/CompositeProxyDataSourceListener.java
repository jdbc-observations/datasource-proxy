package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;

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
        this.listeners.forEach(listener -> listener.beforeMethod(executionContext));
    }

    @Override
    public void afterMethod(MethodExecutionContext executionContext) {
        this.listeners.forEach(listener -> listener.afterMethod(executionContext));
    }

    @Override
    public void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        this.listeners.forEach(listener -> listener.beforeQuery(execInfo, queryInfoList));
    }

    @Override
    public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        this.listeners.forEach(listener -> listener.afterQuery(execInfo, queryInfoList));
    }


    public boolean addListener(ProxyDataSourceListener listener) {
        return this.listeners.add(listener);
    }

    public boolean addListeners(Collection<ProxyDataSourceListener> listeners) {
        return this.listeners.addAll(listeners);
    }

    public List<ProxyDataSourceListener> getListeners() {
        return this.listeners;
    }

    public void setListeners(List<ProxyDataSourceListener> listeners) {
        this.listeners = listeners;
    }

}
