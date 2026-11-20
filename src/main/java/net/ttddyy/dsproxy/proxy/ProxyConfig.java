package net.ttddyy.dsproxy.proxy;

import org.jspecify.annotations.Nullable;

import net.ttddyy.dsproxy.ConnectionIdManager;
import net.ttddyy.dsproxy.listener.ProxyDataSourceListener;
import net.ttddyy.dsproxy.transform.ParameterTransformer;
import net.ttddyy.dsproxy.transform.QueryTransformer;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Hold configuration objects for creating a proxy.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.3
 */
public class ProxyConfig {

    private static class GeneratedKeysConfig {
        private @Nullable ResultSetProxyLogicFactory proxyLogicFactory; // can be null if generated keys proxy is disabled
        private boolean autoRetrieve;
        private boolean retrieveForBatchStatement = false;  // default false
        private boolean retrieveForBatchPreparedOrCallable = true;  // default true
        private boolean autoClose;
    }

    public static class Builder {
        private String dataSourceName = "";
        private final List<ProxyDataSourceListener> listeners = new ArrayList<>();
        private QueryTransformer queryTransformer = QueryTransformer.DEFAULT;
        private ParameterTransformer parameterTransformer = ParameterTransformer.DEFAULT;
        private JdbcProxyFactory jdbcProxyFactory = JdbcProxyFactory.DEFAULT;
        private @Nullable ResultSetProxyLogicFactory resultSetProxyLogicFactory;  // can be null if resultset proxy is disabled
        private ConnectionIdManager connectionIdManager = new DefaultConnectionIdManager();  // create instance every time
        private GeneratedKeysConfig generatedKeysConfig = new GeneratedKeysConfig();
        private boolean retrieveIsolationLevel;

        public static Builder create() {
            return new Builder();
        }

        public static Builder from(ProxyConfig proxyConfig) {
            Builder builder = new Builder();
            builder.listeners.addAll(proxyConfig.listener.getListeners());
            return builder
                    .dataSourceName(proxyConfig.dataSourceName)
                    .queryTransformer(proxyConfig.queryTransformer)
                    .parameterTransformer(proxyConfig.parameterTransformer)
                    .jdbcProxyFactory(proxyConfig.jdbcProxyFactory)
                    .resultSetProxyLogicFactory(proxyConfig.resultSetProxyLogicFactory)
                    .connectionIdManager(proxyConfig.connectionIdManager)
                    .generatedKeysProxyLogicFactory(proxyConfig.generatedKeysConfig.proxyLogicFactory)
                    .autoRetrieveGeneratedKeys(proxyConfig.generatedKeysConfig.autoRetrieve)
                    .retrieveGeneratedKeysForBatchStatement(proxyConfig.generatedKeysConfig.retrieveForBatchStatement)
                    .retrieveGeneratedKeysForBatchPreparedOrCallable(proxyConfig.generatedKeysConfig.retrieveForBatchPreparedOrCallable)
                    .autoCloseGeneratedKeys(proxyConfig.generatedKeysConfig.autoClose)
                    .retrieveIsolationLevel(proxyConfig.retrieveIsolationLevel)
                    ;
        }

        public ProxyConfig build() {
            ProxyConfig proxyConfig = new ProxyConfig();
            proxyConfig.dataSourceName = this.dataSourceName;
            proxyConfig.listener = new CompositeProxyDataSourceListener(this.listeners);
            proxyConfig.queryTransformer = this.queryTransformer;
            proxyConfig.parameterTransformer = this.parameterTransformer;
            proxyConfig.jdbcProxyFactory = this.jdbcProxyFactory;
            proxyConfig.resultSetProxyLogicFactory = this.resultSetProxyLogicFactory;
            proxyConfig.connectionIdManager = this.connectionIdManager;
            proxyConfig.retrieveIsolationLevel = this.retrieveIsolationLevel;

            // generated keys
            proxyConfig.generatedKeysConfig.proxyLogicFactory = this.generatedKeysConfig.proxyLogicFactory;
            proxyConfig.generatedKeysConfig.autoRetrieve = this.generatedKeysConfig.autoRetrieve;
            proxyConfig.generatedKeysConfig.autoClose = this.generatedKeysConfig.autoClose;
            proxyConfig.generatedKeysConfig.retrieveForBatchStatement = this.generatedKeysConfig.retrieveForBatchStatement;
            proxyConfig.generatedKeysConfig.retrieveForBatchPreparedOrCallable = this.generatedKeysConfig.retrieveForBatchPreparedOrCallable;

            return proxyConfig;
        }

        public Builder dataSourceName(String dataSourceName) {
            this.dataSourceName = dataSourceName;
            return this;
        }

        /**
         * Append a listener. Listeners are called in registration order.
         *
         * @param listener listener
         * @return builder
         * @since 2.0
         */
        public Builder listener(ProxyDataSourceListener listener) {
            this.listeners.add(listener);
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
            this.generatedKeysConfig.autoRetrieve = autoRetrieveGeneratedKeys;
            return this;
        }

        public Builder autoCloseGeneratedKeys(boolean autoCloseGeneratedKeys) {
            this.generatedKeysConfig.autoClose = autoCloseGeneratedKeys;
            return this;
        }

        public Builder retrieveGeneratedKeysForBatchStatement(boolean retrieveForBatchStatement) {
            this.generatedKeysConfig.retrieveForBatchStatement = retrieveForBatchStatement;
            return this;
        }

        public Builder retrieveGeneratedKeysForBatchPreparedOrCallable(boolean retrieveForBatchPreparedOrCallable) {
            this.generatedKeysConfig.retrieveForBatchPreparedOrCallable = retrieveForBatchPreparedOrCallable;
            return this;
        }

        public Builder generatedKeysProxyLogicFactory(ResultSetProxyLogicFactory generatedKeysProxyLogicFactory) {
            this.generatedKeysConfig.proxyLogicFactory = generatedKeysProxyLogicFactory;
            return this;
        }

        public Builder connectionIdManager(ConnectionIdManager connectionIdManager) {
            this.connectionIdManager = connectionIdManager;
            return this;
        }

        /**
         * When set to {@code true}, retrieve the isolation level from the connection.
         *
         * @param retrieveIsolationLevel whether to retrieve the isolation level
         * @return builder
         * @since 1.10
         */
        public Builder retrieveIsolationLevel(boolean retrieveIsolationLevel) {
            this.retrieveIsolationLevel = retrieveIsolationLevel;
            return this;
        }

    }

    private String dataSourceName;
    private CompositeProxyDataSourceListener listener;
    private QueryTransformer queryTransformer;
    private ParameterTransformer parameterTransformer;
    private JdbcProxyFactory jdbcProxyFactory;
    private @Nullable ResultSetProxyLogicFactory resultSetProxyLogicFactory;
    private ConnectionIdManager connectionIdManager;
    private GeneratedKeysConfig generatedKeysConfig = new GeneratedKeysConfig();
    private boolean retrieveIsolationLevel;

    public String getDataSourceName() {
        return dataSourceName;
    }

    /**
     * @return composite of the registered listeners
     * @since 2.0
     */
    public ProxyDataSourceListener getListener() {
        return this.listener;
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

    public @Nullable ResultSetProxyLogicFactory getResultSetProxyLogicFactory() {
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
    public @Nullable ResultSetProxyLogicFactory getGeneratedKeysProxyLogicFactory() {
        return this.generatedKeysConfig.proxyLogicFactory;
    }

    /**
     * When this returns {@code true}, the proxy logic always calls {@link Statement#getGeneratedKeys()} and stores the result in
     * {@link net.ttddyy.dsproxy.listener.QueryExecutionContext}.
     * Also, if {@link Statement#getGeneratedKeys()} is called while the cached generated-keys {@link ResultSet} is
     * still open, the cached result set is returned. If it is closed, calling {@link Statement#getGeneratedKeys()}
     * returns a new {@link ResultSet}. (Calling {@link Statement#getGeneratedKeys()} multiple times is not defined
     * in the JDBC specification, so behavior depends on the JDBC driver.)
     *
     * If {@code false} is returned, {@link net.ttddyy.dsproxy.listener.QueryExecutionContext#getGeneratedKeys()} returns {@code null}.
     *
     * @return {@code true} if generated-keys retrieval is enabled
     * @since 1.4.5
     */
    public boolean isAutoRetrieveGeneratedKeys() {
        return this.generatedKeysConfig.autoRetrieve;
    }

    /**
     * Returns {@code true} when a proxy logic factory for generated keys is specified.
     *
     * @return {@code true} when {@link ResultSetProxyLogicFactory} for generated keys is specified
     * @see ProxyConfig.Builder#generatedKeysProxyLogicFactory(ResultSetProxyLogicFactory)
     * @since 1.4.5
     */
    public boolean isGeneratedKeysProxyEnabled() {
        return this.generatedKeysConfig.proxyLogicFactory != null;
    }

    /**
     * Whether to automatically close the {@link ResultSet} for retrieved generated keys.
     *
     * When this returns {@code true}, the automatically retrieved generated-keys {@link ResultSet} is closed after
     * {@link ProxyDataSourceListener#afterQuery} finishes. The result of an explicit call to
     * {@link Statement#getGeneratedKeys()} is not closed by this setting.
     *
     * @since 1.4.5
     */
    public boolean isAutoCloseGeneratedKeys() {
        return this.generatedKeysConfig.autoClose;
    }

    /**
     * Whether to automatically retrieve generated keys for batch statements.
     *
     * The default is {@code false}.
     *
     * @since 1.4.6
     */
    public boolean isRetrieveGeneratedKeysForBatchStatement() {
        return this.generatedKeysConfig.retrieveForBatchStatement;
    }

    /**
     * Whether to automatically retrieve generated keys for batch prepared or callable statements.
     *
     * The default is {@code true}.
     *
     * @since 1.4.6
     */
    public boolean isRetrieveGeneratedKeysForBatchPreparedOrCallable() {
        return this.generatedKeysConfig.retrieveForBatchPreparedOrCallable;
    }

    public ConnectionIdManager getConnectionIdManager() {
        return connectionIdManager;
    }

    /**
     * Whether to retrieve the isolation level from the connection.
     *
     * @return {@code true} to retrieve the isolation level from the connection
     * @since 1.10
     */
    public boolean isRetrieveIsolationLevel() {
        return this.retrieveIsolationLevel;
    }

    /**
     * Set whether to retrieve the isolation level from the connection.
     *
     * @param retrieveIsolationLevel {@code true} to retrieve
     * @since 1.10
     */
    public void setRetrieveIsolationLevel(boolean retrieveIsolationLevel) {
        this.retrieveIsolationLevel = retrieveIsolationLevel;
    }
}
