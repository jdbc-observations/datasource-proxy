package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.listener.ChainConnectionAcquiringListener;
import net.ttddyy.dsproxy.listener.ChainListener;
import net.ttddyy.dsproxy.listener.ConnectionAcquiringListener;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
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

    private ChainListener chainListener = new ChainListener();  // empty default
    private ChainConnectionAcquiringListener chainConnectionAcquiringListener = new ChainConnectionAcquiringListener();  // empty default
    private QueryTransformer queryTransformer = QueryTransformer.DEFAULT;
    private ParameterTransformer parameterTransformer = ParameterTransformer.DEFAULT;

    public InterceptorHolder() {
    }

    public InterceptorHolder(QueryExecutionListener listener, QueryTransformer queryTransformer) {
        this.chainListener.addListener(listener);
        this.queryTransformer = queryTransformer;
    }

    public InterceptorHolder(QueryExecutionListener listener, QueryTransformer queryTransformer, ParameterTransformer parameterTransformer) {
        this.chainListener.addListener(listener);
        this.queryTransformer = queryTransformer;
        this.parameterTransformer = parameterTransformer;
    }

    public QueryExecutionListener getListener() {
        return this.chainListener;
    }

    public ConnectionAcquiringListener getConnectionAcquiringListener() {
        return this.chainConnectionAcquiringListener;
    }

    public void setListener(QueryExecutionListener listener) {
        if (listener instanceof ChainListener) {
            this.chainListener = (ChainListener) listener;
        } else {
            this.addListener(listener);
        }
    }

    /**
     * Add {@link QueryExecutionListener}
     *
     * @param listener a query execution listener
     * @since 1.4
     */
    public void addListener(QueryExecutionListener listener) {
        this.chainListener.addListener(listener);
    }

    /**
     * Add {@link ConnectionAcquiringListener}
     *
     * @param listener a query execution listener
     */
    public void addConnectionAcquiringListener(ConnectionAcquiringListener listener) {
        this.chainConnectionAcquiringListener.addListener(listener);
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
