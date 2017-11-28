package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ConnectionIdManager;
import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.listener.ChainListener;
import net.ttddyy.dsproxy.listener.CompositeMethodListener;
import net.ttddyy.dsproxy.listener.MethodExecutionListener;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.transform.ParameterTransformer;
import net.ttddyy.dsproxy.transform.QueryTransformer;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

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
        private ResultSetProxyLogicFactory generatedKeysProxyLogicFactory; // can be null if generated keys proxy is disabled
        private ConnectionIdManager connectionIdManager = new DefaultConnectionIdManager();  // create instance every time
        private CompositeMethodListener methodListener = new CompositeMethodListener();  // empty default
        private boolean autoRetrieveGeneratedKeys;
        private boolean autoCloseGeneratedKeys;

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
                    .autoRetrieveGeneratedKeys(proxyConfig.autoRetrieveGeneratedKeys)
                    .autoCloseGeneratedKeys(proxyConfig.autoCloseGeneratedKeys)
                    .generatedKeysProxyLogicFactory(proxyConfig.generatedKeysProxyLogicFactory)
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
            proxyConfig.generatedKeysProxyLogicFactory = this.generatedKeysProxyLogicFactory;
            proxyConfig.connectionIdManager = this.connectionIdManager;
            proxyConfig.methodListener = this.methodListener;
            proxyConfig.autoRetrieveGeneratedKeys = this.autoRetrieveGeneratedKeys;
            proxyConfig.autoCloseGeneratedKeys = this.autoCloseGeneratedKeys;
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
            } else {
                this.queryListener.addListener(queryListener);
            }
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

        public Builder autoRetrieveGeneratedKeys(boolean autoRetrieveGeneratedKeys) {
            this.autoRetrieveGeneratedKeys = autoRetrieveGeneratedKeys;
            return this;
        }

        public Builder autoCloseGeneratedKeys(boolean autoCloseGeneratedKeys) {
            this.autoCloseGeneratedKeys = autoCloseGeneratedKeys;
            return this;
        }

        public Builder generatedKeysProxyLogicFactory(ResultSetProxyLogicFactory generatedKeysProxyLogicFactory) {
            this.generatedKeysProxyLogicFactory = generatedKeysProxyLogicFactory;
            return this;
        }

        public Builder connectionIdManager(ConnectionIdManager connectionIdManager) {
            this.connectionIdManager = connectionIdManager;
            return this;
        }

        public Builder methodListener(MethodExecutionListener methodListener) {
            if (methodListener instanceof CompositeMethodListener) {
                for (MethodExecutionListener listener : ((CompositeMethodListener) methodListener).getListeners()) {
                    this.methodListener.addListener(listener);
                }
            } else {
                this.methodListener.addListener(methodListener);
            }
            return this;
        }
    }

    private String dataSourceName;
    private ChainListener queryListener;
    private QueryTransformer queryTransformer;
    private ParameterTransformer parameterTransformer;
    private JdbcProxyFactory jdbcProxyFactory;
    private ResultSetProxyLogicFactory resultSetProxyLogicFactory;
    private ResultSetProxyLogicFactory generatedKeysProxyLogicFactory;
    private boolean autoRetrieveGeneratedKeys;
    private boolean autoCloseGeneratedKeys;
    private ConnectionIdManager connectionIdManager;
    private CompositeMethodListener methodListener;

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

    /**
     * @return {@code true} when {@link ResultSetProxyLogicFactory} for {@link ResultSet} is specified
     * @since 1.4.5
     */
    public boolean isResultSetProxyEnabled() {
        return this.resultSetProxyLogicFactory != null;
    }


    /**
     * @since 1.4.5
     */
    public ResultSetProxyLogicFactory getGeneratedKeysProxyLogicFactory() {
        return generatedKeysProxyLogicFactory;
    }

    /**
     * When this returns {@code true}, the proxy logic always call {@link Statement#getGeneratedKeys()} and set it to
     * {@link net.ttddyy.dsproxy.ExecutionInfo}.
     * If {@code false} is returned, {@link ExecutionInfo#getGeneratedKeys()} returns {@code null}.
     *
     * @return true if generated-keys retrieval is enabled
     * @since 1.4.5
     */
    public boolean isAutoRetrieveGeneratedKeys() {
        return this.autoRetrieveGeneratedKeys;
    }

    /**
     * When {@link JdbcProxyFactory} for generated-keys is specified, return {@code true}.
     *
     * @return {@code true} when {@link ResultSetProxyLogicFactory} for generated keys is specified
     * @see ProxyConfig.Builder#generatedKeysProxyLogicFactory(ResultSetProxyLogicFactory)
     * @since 1.4.5
     */
    public boolean isGeneratedKeysProxyEnabled() {
        return this.generatedKeysProxyLogicFactory != null;
    }

    /**
     * Whether auto closing {@link ResultSet} for generated-keys is enabled.
     *
     * When this returns {@code true}, always close the {@link ResultSet} for generated keys when
     * {@link QueryExecutionListener#afterQuery(ExecutionInfo, List)} has finished.
     *
     * @since 1.4.5
     */
    public boolean isAutoCloseGeneratedKeys() {
        return this.autoCloseGeneratedKeys;
    }

    public ConnectionIdManager getConnectionIdManager() {
        return connectionIdManager;
    }

    public CompositeMethodListener getMethodListener() {
        return methodListener;
    }

}
