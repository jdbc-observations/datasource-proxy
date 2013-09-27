package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.transform.NoOpParameterTransformer;
import net.ttddyy.dsproxy.transform.NoOpQueryTransformer;
import net.ttddyy.dsproxy.transform.ParameterTransformer;
import net.ttddyy.dsproxy.transform.QueryTransformer;

/**
 * Container of interceptors.
 *
 * @author Tadaya Tsuyukubo
 * @see QueryExecutionListener
 * @see QueryTransformer
 * @see ParameterTransformer
 * @since 1.2
 */
public class InterceptorHolder {

    private QueryExecutionListener listener = QueryExecutionListener.DEFAULT;
    private QueryTransformer queryTransformer = QueryTransformer.DEFAULT;
    private ParameterTransformer parameterTransformer = ParameterTransformer.DEFAULT;

    public InterceptorHolder() {
    }

    public InterceptorHolder(QueryExecutionListener listener, QueryTransformer queryTransformer) {
        this.listener = listener;
        this.queryTransformer = queryTransformer;
    }
    public InterceptorHolder(QueryExecutionListener listener, QueryTransformer queryTransformer, ParameterTransformer parameterTransformer) {
        this.listener = listener;
        this.queryTransformer = queryTransformer;
        this.parameterTransformer = parameterTransformer;
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

    public ParameterTransformer getParameterTransformer() {
        return parameterTransformer;
    }

    public void setParameterTransformer(ParameterTransformer parameterTransformer) {
        this.parameterTransformer = parameterTransformer;
    }
}
