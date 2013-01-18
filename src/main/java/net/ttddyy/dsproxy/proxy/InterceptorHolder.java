package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.transform.NoOpQueryTransformer;
import net.ttddyy.dsproxy.transform.QueryTransformer;

/**
 * Container of interceptors.
 *
 * @author Tadaya Tsuyukubo
 * @see QueryExecutionListener
 * @see QueryTransformer
 * @since 1.2
 */
public class InterceptorHolder {

    private QueryExecutionListener listener;
    private QueryTransformer queryTransformer = new NoOpQueryTransformer();

    public InterceptorHolder() {
    }

    public InterceptorHolder(QueryExecutionListener listener, QueryTransformer queryTransformer) {
        this.listener = listener;
        this.queryTransformer = queryTransformer;
    }

    public QueryExecutionListener getListener() {
        return listener;
    }

    public void setListener(QueryExecutionListener listener) {
        this.listener = listener;
    }

    public QueryTransformer getQueryTransformer() {
        return queryTransformer;
    }

    public void setQueryTransformer(QueryTransformer queryTransformer) {
        this.queryTransformer = queryTransformer;
    }
}
