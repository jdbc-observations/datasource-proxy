package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ConnectionIdManager;
import net.ttddyy.dsproxy.listener.ChainListener;
import net.ttddyy.dsproxy.listener.MethodExecutionListener;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.transform.ParameterTransformer;
import net.ttddyy.dsproxy.transform.QueryTransformer;

/**
 * Hold configuration objects for creating a proxy.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.3
 */
public class ProxyConfig {

    public static class Builder {
        private String dataSourceName = "";
        private ChainListener queryListener = new ChainListener();  // empty default
        private QueryTransformer queryTransformer = QueryTransformer.DEFAULT;
        private ParameterTransformer parameterTransformer = ParameterTransformer.DEFAULT;
        private JdbcProxyFactory jdbcProxyFactory = JdbcProxyFactory.DEFAULT;
        private ResultSetProxyLogicFactory resultSetProxyLogicFactory;  // can be null if resultset proxy is disabled
        private ConnectionIdManager connectionIdManager = ConnectionIdManager.DEFAULT;
        private MethodExecutionListener methodListener = MethodExecutionListener.DEFAULT;

        public static Builder create() {
            return new Builder();
        }

        public static Builder from(ProxyConfig proxyConfig) {
            return new Builder()
                    .dataSourceName(proxyConfig.dataSourceName)
                    .queryListener(proxyConfig.queryListener)
                    .queryTransformer(proxyConfig.queryTransformer)
                    .parameterTransformer(proxyConfig.parameterTransformer)
                    .jdbcProxyFactory(proxyConfig.jdbcProxyFactory)
                    .resultSetProxyLogicFactory(proxyConfig.resultSetProxyLogicFactory)
                    .connectionIdManager(proxyConfig.connectionIdManager)
                    .methodListener(proxyConfig.methodListener);
        }

        public ProxyConfig build() {
            ProxyConfig proxyConfig = new ProxyConfig();
            proxyConfig.dataSourceName = this.dataSourceName;
            proxyConfig.queryListener = this.queryListener;
            proxyConfig.queryTransformer = this.queryTransformer;
            proxyConfig.parameterTransformer = this.parameterTransformer;
            proxyConfig.jdbcProxyFactory = this.jdbcProxyFactory;
            proxyConfig.resultSetProxyLogicFactory = this.resultSetProxyLogicFactory;
            proxyConfig.connectionIdManager = this.connectionIdManager;
            proxyConfig.methodListener = this.methodListener;
            return proxyConfig;
        }

        public Builder dataSourceName(String dataSourceName) {
            this.dataSourceName = dataSourceName;
            return this;
        }

        public Builder queryListener(QueryExecutionListener queryListener) {
            if (queryListener instanceof ChainListener) {
                for (QueryExecutionListener listener : ((ChainListener) queryListener).getListeners()) {
                    this.queryListener.addListener(listener);
                }
            }
            this.queryListener.addListener(queryListener);
            return this;
        }

        public Builder queryTransformer(QueryTransformer queryTransformer) {
            this.queryTransformer = queryTransformer;
            return this;
        }

        public Builder parameterTransformer(ParameterTransformer parameterTransformer) {
            this.parameterTransformer = parameterTransformer;
            return this;
        }

        public Builder jdbcProxyFactory(JdbcProxyFactory jdbcProxyFactory) {
            this.jdbcProxyFactory = jdbcProxyFactory;
            return this;
        }

        public Builder resultSetProxyLogicFactory(ResultSetProxyLogicFactory resultSetProxyLogicFactory) {
            this.resultSetProxyLogicFactory = resultSetProxyLogicFactory;
            return this;
        }

        public Builder connectionIdManager(ConnectionIdManager connectionIdManager) {
            this.connectionIdManager = connectionIdManager;
            return this;
        }

        public Builder methodListener(MethodExecutionListener methodListener) {
            this.methodListener = methodListener;
            return this;
        }
    }

    private String dataSourceName;
    private ChainListener queryListener;
    private QueryTransformer queryTransformer;
    private ParameterTransformer parameterTransformer;
    private JdbcProxyFactory jdbcProxyFactory;
    private ResultSetProxyLogicFactory resultSetProxyLogicFactory;
    private ConnectionIdManager connectionIdManager;
    private MethodExecutionListener methodListener;

    public String getDataSourceName() {
        return dataSourceName;
    }

    public ChainListener getQueryListener() {
        return queryListener;
    }

    public QueryTransformer getQueryTransformer() {
        return queryTransformer;
    }

    public ParameterTransformer getParameterTransformer() {
        return parameterTransformer;
    }

    public JdbcProxyFactory getJdbcProxyFactory() {
        return jdbcProxyFactory;
    }

    public ResultSetProxyLogicFactory getResultSetProxyLogicFactory() {
        return resultSetProxyLogicFactory;
    }

    public ConnectionIdManager getConnectionIdManager() {
        return connectionIdManager;
    }

    public MethodExecutionListener getMethodListener() {
        return methodListener;
    }

}
