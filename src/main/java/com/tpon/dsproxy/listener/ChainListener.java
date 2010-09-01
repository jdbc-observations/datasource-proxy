package com.tpon.dsproxy.listener;

import com.tpon.dsproxy.ExecutionInfo;
import com.tpon.dsproxy.QueryInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Execute chain of listeners.
 *
 * @author Tadaya Tsuyukubo
 */
public class ChainListener implements QueryExecutionListener {
    private List<QueryExecutionListener> listeners = new ArrayList<QueryExecutionListener>();

    public void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        for (QueryExecutionListener listener : listeners) {
            listener.beforeQuery(execInfo, queryInfoList);
        }
    }

    public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        for (QueryExecutionListener listener : listeners) {
            listener.afterQuery(execInfo, queryInfoList);
        }
    }

    public void addListener(QueryExecutionListener listner) {
        this.listeners.add(listner);
    }

    public List<QueryExecutionListener> getListeners() {
        return listeners;
    }

    public void setListeners(List<QueryExecutionListener> listeners) {
        this.listeners = listeners;
    }
}
