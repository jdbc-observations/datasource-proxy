package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Execute chain of listeners.
 *
 * @author Tadaya Tsuyukubo
 */
public class ChainListener implements QueryExecutionListener {
    private List<QueryExecutionListener> listeners = new ArrayList<QueryExecutionListener>();

    @Override
    public void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        for (QueryExecutionListener listener : listeners) {
            listener.beforeQuery(execInfo, queryInfoList);
        }
    }

    @Override
    public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        for (QueryExecutionListener listener : listeners) {
            listener.afterQuery(execInfo, queryInfoList);
        }
    }

    public void addListener(QueryExecutionListener listener) {
        this.listeners.add(listener);
    }

    public List<QueryExecutionListener> getListeners() {
        return listeners;
    }

    public void setListeners(List<QueryExecutionListener> listeners) {
        this.listeners = listeners;
    }
}
