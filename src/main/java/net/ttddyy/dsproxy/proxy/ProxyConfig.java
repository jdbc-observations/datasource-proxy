package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ConnectionIdManager;

/**
 * Hold configuration objects for creating a proxy.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.3
 */
public class ProxyConfig {

    public static class Builder {
        private String dataSourceName = "";
        private InterceptorHolder interceptorHolder = new InterceptorHolder();
        private JdbcProxyFactory jdbcProxyFactory = JdbcProxyFactory.DEFAULT;
        private ResultSetProxyLogicFactory resultSetProxyLogicFactory;  // can be null if resultset proxy is disabled
        private ConnectionIdManager connectionIdManager = ConnectionIdManager.DEFAULT;

        public static Builder create() {
            return new Builder();
        }

        public static Builder from(ProxyConfig proxyConfig) {
            return new Builder()
                    .dataSourceName(proxyConfig.dataSourceName)
                    .interceptorHolder(proxyConfig.interceptorHolder)
                    .jdbcProxyFactory(proxyConfig.jdbcProxyFactory)
                    .resultSetProxyLogicFactory(proxyConfig.resultSetProxyLogicFactory)
                    .connectionIdManager(proxyConfig.connectionIdManager);
        }

        public ProxyConfig build() {
            ProxyConfig proxyConfig = new ProxyConfig();
            proxyConfig.dataSourceName = this.dataSourceName;
            proxyConfig.interceptorHolder = this.interceptorHolder;
            proxyConfig.jdbcProxyFactory = this.jdbcProxyFactory;
            proxyConfig.resultSetProxyLogicFactory = this.resultSetProxyLogicFactory;
            proxyConfig.connectionIdManager = this.connectionIdManager;
            return proxyConfig;
        }

        public Builder dataSourceName(String dataSourceName) {
            this.dataSourceName = dataSourceName;
            return this;
        }

        public Builder interceptorHolder(InterceptorHolder interceptorHolder) {
            this.interceptorHolder = interceptorHolder;
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
    }

    private String dataSourceName;
    private InterceptorHolder interceptorHolder;
    private JdbcProxyFactory jdbcProxyFactory;
    private ResultSetProxyLogicFactory resultSetProxyLogicFactory;
    private ConnectionIdManager connectionIdManager;

    public String getDataSourceName() {
        return dataSourceName;
    }

    public InterceptorHolder getInterceptorHolder() {
        return interceptorHolder;
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
}
