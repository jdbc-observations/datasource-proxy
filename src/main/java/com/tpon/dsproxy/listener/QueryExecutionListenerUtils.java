package com.tpon.dsproxy.listener;

/**
 * @author Tadaya Tsuyukubo
 */
public class QueryExecutionListenerUtils {

    public void addListener(QueryExecutionListener source, QueryExecutionListener toAdd) {
        if (source instanceof ChainListener) {
            ChainListener listener = (ChainListener) source;
            listener.addListener(toAdd);
        } else {
            ChainListener listener = new ChainListener();
            listener.addListener(source);
            listener.addListener(toAdd);
        }
    }
}
