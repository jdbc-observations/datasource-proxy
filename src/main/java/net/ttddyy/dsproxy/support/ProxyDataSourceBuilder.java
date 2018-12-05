package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.ConnectionIdManager;
import net.ttddyy.dsproxy.DataSourceProxyException;
import net.ttddyy.dsproxy.listener.MethodExecutionContext;
import net.ttddyy.dsproxy.listener.ProxyDataSourceListener;
import net.ttddyy.dsproxy.listener.QueryExecutionContext;
import net.ttddyy.dsproxy.listener.SlowQueryListener;
import net.ttddyy.dsproxy.listener.TracingMethodListener;
import net.ttddyy.dsproxy.listener.count.DataSourceQueryCountListener;
import net.ttddyy.dsproxy.listener.count.QueryCountStrategy;
import net.ttddyy.dsproxy.proxy.DefaultConnectionIdManager;
import net.ttddyy.dsproxy.proxy.JdbcProxyFactory;
import net.ttddyy.dsproxy.proxy.ProxyConfig;
import net.ttddyy.dsproxy.proxy.RepeatableReadResultSetProxyLogicFactory;
import net.ttddyy.dsproxy.proxy.ResultSetProxyLogicFactory;
import net.ttddyy.dsproxy.transform.QueryTransformer;

import javax.sql.DataSource;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * Builder for proxy {@link DataSource}.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.3
 */
public class ProxyDataSourceBuilder {

    private DataSource dataSource;
    private String dataSourceName;

    // For building TracingMethodListener
    private boolean createTracingMethodListener;
    private BooleanSupplier tracingCondition;
    private Consumer<String> tracingMessageConsumer;

    private boolean createDataSourceQueryCountListener;

    private List<ProxyDataSourceListener> listeners = new ArrayList<>();

    private QueryTransformer queryTransformer;

    private JdbcProxyFactory jdbcProxyFactory;
    private ConnectionIdManager connectionIdManager;

    private ResultSetProxyLogicFactory resultSetProxyLogicFactory;

    private boolean autoRetrieveGeneratedKeys;
    private Boolean retrieveGeneratedKeysForBatchStatement;
    private Boolean retrieveGeneratedKeysForBatchPreparedOrCallable;
    private boolean autoCloseGeneratedKeys;
    private ResultSetProxyLogicFactory generatedKeysProxyLogicFactory;

    public static ProxyDataSourceBuilder create() {
        return new ProxyDataSourceBuilder();
    }

    public static ProxyDataSourceBuilder create(DataSource dataSource) {
        return new ProxyDataSourceBuilder(dataSource);
    }

    public static ProxyDataSourceBuilder create(String dataSourceName, DataSource dataSource) {
        return new ProxyDataSourceBuilder(dataSource).name(dataSourceName);
    }

    public ProxyDataSourceBuilder() {
    }

    public ProxyDataSourceBuilder(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Set actual datasource.
     *
     * @param dataSource actual datasource
     * @return builder
     */
    public ProxyDataSourceBuilder dataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    /**
     * Create {@link DataSourceQueryCountListener}.
     *
     * @return builder
     */
    public ProxyDataSourceBuilder countQuery() {
        this.createDataSourceQueryCountListener = true;
        return this;
    }

    /**
     * Register given listener.
     *
     * @param listener a listener to register
     * @return builder
     */
    public ProxyDataSourceBuilder listener(ProxyDataSourceListener listener) {
        this.listeners.add(listener);
        return this;
    }

    /**
     * Add {@link ProxyDataSourceListener} that performs given lambda on {@link ProxyDataSourceListener#beforeQuery(QueryExecutionContext)}.
     *
     * @param callback a lambda function executed on {@link ProxyDataSourceListener#beforeQuery(QueryExecutionContext)}
     * @return builder
     * @since 1.4.3
     */
    public ProxyDataSourceBuilder beforeQuery(Consumer<QueryExecutionContext> callback) {
        ProxyDataSourceListener listener = new ProxyDataSourceListener() {
            @Override
            public void beforeQuery(QueryExecutionContext executionContext) {
                callback.accept(executionContext);
            }
        };
        this.listeners.add(listener);
        return this;
    }

    /**
     * Add {@link ProxyDataSourceListener} that performs given lambda on {@link ProxyDataSourceListener#afterQuery(QueryExecutionContext)}.
     *
     * @param callback a lambda function executed on {@link ProxyDataSourceListener#afterQuery(QueryExecutionContext)}
     * @return builder
     * @since 1.4.3
     */
    public ProxyDataSourceBuilder afterQuery(Consumer<QueryExecutionContext> callback) {
        ProxyDataSourceListener listener = new ProxyDataSourceListener() {
            @Override
            public void afterQuery(QueryExecutionContext executionContext) {
                callback.accept(executionContext);
            }
        };
        this.listeners.add(listener);
        return this;
    }

    /**
     * Set datasource name.
     *
     * @param dataSourceName datasource name
     * @return builder
     */
    public ProxyDataSourceBuilder name(String dataSourceName) {
        this.dataSourceName = dataSourceName;
        return this;
    }

    /**
     * Register {@link net.ttddyy.dsproxy.transform.QueryTransformer}.
     *
     * @param queryTransformer a query-transformer to register
     * @return builder
     */
    public ProxyDataSourceBuilder queryTransformer(QueryTransformer queryTransformer) {
        this.queryTransformer = queryTransformer;
        return this;
    }

    /**
     * Register {@link JdbcProxyFactory}.
     *
     * @param jdbcProxyFactory a JdbcProxyFactory to register
     * @return builder
     * @since 1.4.2
     */
    public ProxyDataSourceBuilder jdbcProxyFactory(JdbcProxyFactory jdbcProxyFactory) {
        this.jdbcProxyFactory = jdbcProxyFactory;
        return this;
    }

    /**
     * Register {@link ConnectionIdManager}.
     *
     * @param connectionIdManager a ConnectionIdManager to register
     * @return builder
     * @since 1.4.2
     */
    public ProxyDataSourceBuilder connectionIdManager(ConnectionIdManager connectionIdManager) {
        this.connectionIdManager = connectionIdManager;
        return this;
    }

    /**
     * Enable resultset proxy.
     *
     * When it is enabled, returned ResultSet will be proxied(e.g.: Statement#executeQuery()).
     *
     * @return builder
     * @since 1.4.3
     */
    public ProxyDataSourceBuilder proxyResultSet() {
        this.resultSetProxyLogicFactory = ResultSetProxyLogicFactory.DEFAULT;
        return this;
    }

    /**
     * Enable resultset proxy with given proxy logic factory.
     *
     * @return builder
     * @since 1.4.3
     */
    public ProxyDataSourceBuilder proxyResultSet(ResultSetProxyLogicFactory factory) {
        this.resultSetProxyLogicFactory = factory;
        return this;
    }

    /**
     * Enable {@link java.sql.ResultSet} proxy for generated keys(e.g.: Statement#getGeneratedKeys()).
     *
     * @return builder
     * @since 1.4.5
     */
    public ProxyDataSourceBuilder proxyGeneratedKeys() {
        this.generatedKeysProxyLogicFactory = ResultSetProxyLogicFactory.DEFAULT;
        return this;
    }

    /**
     * Enable {@link java.sql.ResultSet} proxy for generated keys with given proxy logic factory.
     *
     * @return builder
     * @since 1.4.5
     */
    public ProxyDataSourceBuilder proxyGeneratedKeys(ResultSetProxyLogicFactory factory) {
        this.generatedKeysProxyLogicFactory = factory;
        return this;
    }

    /**
     * Enable {@link java.sql.ResultSet} proxy for generated keys that allows repeatable read.
     *
     * @return builder
     * @since 1.4.5
     */
    public ProxyDataSourceBuilder repeatableReadGeneratedKeys() {
        this.generatedKeysProxyLogicFactory = new RepeatableReadResultSetProxyLogicFactory();
        return this;
    }

    /**
     * Enable auto retrieval of generated keys.
     *
     * When it is enabled, after executing query, it always call {@link Statement#getGeneratedKeys()}.
     * The retrieved {@link java.sql.ResultSet} is available via {@link QueryExecutionContext#getGeneratedKeys()}.
     *
     * When this configuration is combined with {@link #proxyGeneratedKeys(ResultSetProxyLogicFactory)}, the proxied
     * {@link java.sql.ResultSet} will be returned from {@link QueryExecutionContext#getGeneratedKeys()}.
     *
     * When autoClose parameter is set to {@code true}, datasource-proxy will close the generated-keys {@link java.sql.ResultSet}
     * after it called {@link ProxyDataSourceListener#afterQuery(QueryExecutionContext)}.
     * This behavior might not be ideal if above layer, such as OR Mapper or application code, need to access generated-keys
     * because when they access generated-keys, the resultset is already closed.
     *
     * To support such usecase, specify {@link RepeatableReadResultSetProxyLogicFactory} and set {@code autoClose=false}.
     * This way, even though your {@link ProxyDataSourceListener} has accessed generated-keys, it is still readable at
     * upper layer of the code, and they can close the generated-keys resultset.
     *
     * @param autoClose set {@code true} to close the generated-keys {@link java.sql.ResultSet} after query listener execution
     * @return builder
     * @since 1.4.5
     */
    public ProxyDataSourceBuilder autoRetrieveGeneratedKeys(boolean autoClose) {
        this.autoRetrieveGeneratedKeys = true;
        this.autoCloseGeneratedKeys = autoClose;
        return this;
    }

    /**
     * Enable auto retrieval of generated keys with proxy created by specified factory.
     *
     * See detail on {@link #autoRetrieveGeneratedKeys(boolean)}.
     *
     * @param autoClose set {@code true} to close the generated-keys {@link java.sql.ResultSet} after query listener execution
     * @param factory   a factory to create a generated-keys proxy
     * @return builder
     * @since 1.4.5
     */
    public ProxyDataSourceBuilder autoRetrieveGeneratedKeys(boolean autoClose, ResultSetProxyLogicFactory factory) {
        this.autoRetrieveGeneratedKeys = true;
        this.autoCloseGeneratedKeys = autoClose;
        this.generatedKeysProxyLogicFactory = factory;
        return this;
    }

    /**
     * Enable auto retrieval of generated keys with {@link RepeatableReadResultSetProxyLogicFactory}.
     *
     * See detail on {@link #autoRetrieveGeneratedKeys(boolean)}.
     *
     * @param autoClose set {@code true} to close the generated-keys {@link java.sql.ResultSet} after query listener execution
     * @return builder
     * @since 1.4.5
     */
    public ProxyDataSourceBuilder autoRetrieveGeneratedKeysWithRepeatableReadProxy(boolean autoClose) {
        this.autoRetrieveGeneratedKeys = true;
        this.autoCloseGeneratedKeys = autoClose;
        this.generatedKeysProxyLogicFactory = new RepeatableReadResultSetProxyLogicFactory();
        return this;
    }

    /**
     * Configure generated-keys retrieval for batch statement and prepared/callable when auto retrieval is enabled.
     *
     * Since JDBC spec defines creation of generated-keys for batch executions are driver implementation specific,
     * this method controls whether to auto-retrieve generated-keys for batch execution of {@link Statement} and
     * {@link java.sql.PreparedStatement} / {@link java.sql.CallableStatement}.
     * Setting is only effective when generated-keys auto-retrieval is enabled.
     *
     * Defult values are set {@code false} for {@link Statement}, {@code true} for {@link java.sql.PreparedStatement}
     * and {@link java.sql.CallableStatement}.
     *
     * @param forStatement          for {@link Statement}
     * @param forPreparedOrCallable for {@link java.sql.PreparedStatement} and {@link java.sql.CallableStatement}
     * @return builder
     * @since 1.4.6
     */
    public ProxyDataSourceBuilder retrieveGeneratedKeysForBatch(boolean forStatement, boolean forPreparedOrCallable) {
        this.retrieveGeneratedKeysForBatchStatement = forStatement;
        this.retrieveGeneratedKeysForBatchPreparedOrCallable = forPreparedOrCallable;
        return this;
    }


    /**
     * Enable resultset proxy that allows repeatable read.
     *
     * Equivalent to {@code proxyResultSet(new RepeatableReadResultSetProxyLogicFactory())}
     *
     * @return builder
     * @since 1.4.3
     */
    public ProxyDataSourceBuilder repeatableReadResultSet() {
        this.resultSetProxyLogicFactory = new RepeatableReadResultSetProxyLogicFactory();
        return this;
    }

    /**
     * Add {@link ProxyDataSourceListener}.
     *
     * @param listener a method execution listener
     * @return builder
     * @since 1.4.3
     */
    public ProxyDataSourceBuilder methodListener(ProxyDataSourceListener listener) {
        this.listeners.add(listener);
        return this;
    }

    /**
     * Add {@link ProxyDataSourceListener} that performs given lambda on {@link ProxyDataSourceListener#beforeMethod(MethodExecutionContext)}.
     *
     * @param callback a lambda function executed on {@link ProxyDataSourceListener#beforeMethod(MethodExecutionContext)}
     * @return builder
     * @since 1.4.3
     */
    public ProxyDataSourceBuilder beforeMethod(Consumer<MethodExecutionContext> callback) {
        ProxyDataSourceListener listener = new ProxyDataSourceListener() {
            @Override
            public void beforeMethod(MethodExecutionContext executionContext) {
                callback.accept(executionContext);
            }
        };
        this.listeners.add(listener);
        return this;
    }

    /**
     * Add {@link ProxyDataSourceListener} that performs given lambda on {@link ProxyDataSourceListener#afterMethod(MethodExecutionContext)}.
     *
     * @param callback a lambda function executed on {@link ProxyDataSourceListener#afterMethod(MethodExecutionContext)}
     * @return builder
     * @since 1.4.3
     */
    public ProxyDataSourceBuilder afterMethod(Consumer<MethodExecutionContext> callback) {
        ProxyDataSourceListener listener = new ProxyDataSourceListener() {
            @Override
            public void afterMethod(MethodExecutionContext executionContext) {
                callback.accept(executionContext);
            }
        };
        this.listeners.add(listener);
        return this;
    }

    /**
     * Add {@link SlowQueryListener} that performs given lambda.
     *
     * @param threshold  threshold time
     * @param timeUnit   threshold time unit
     * @param callback   a lambda function executed only once per query if it exceeds the threshold time.
     * @return builder
     * @since 2.0
     */
    public ProxyDataSourceBuilder onSlowQuery(long threshold, TimeUnit timeUnit, Consumer<QueryExecutionContext> callback) {
        this.listeners.add(new SlowQueryListener(threshold, timeUnit, callback));
        return this;
    }

    /**
     * Enable {@link TracingMethodListener}.
     *
     * @return builder
     * @since 1.4.4
     */
    public ProxyDataSourceBuilder traceMethods() {
        this.createTracingMethodListener = true;
        return this;
    }

    /**
     * Enable {@link TracingMethodListener} with consumer that receives trace logging message.
     *
     * @param messageConsumer receives trace logging message
     * @return builder
     * @since 1.4.4
     */
    public ProxyDataSourceBuilder traceMethods(Consumer<String> messageConsumer) {
        this.createTracingMethodListener = true;
        this.tracingMessageConsumer = messageConsumer;
        return this;
    }

    /**
     * Enable {@link TracingMethodListener}.
     *
     * When given condition returns {@code true}, it prints out trace log.
     * The condition is used for dynamically turn on/off tracing.
     *
     * @param condition decide to turn on/off tracing
     * @return builder
     * @since 1.4.4
     */
    public ProxyDataSourceBuilder traceMethodsWhen(BooleanSupplier condition) {
        this.createTracingMethodListener = true;
        this.tracingCondition = condition;
        return this;
    }

    /**
     * Enable {@link TracingMethodListener}.
     *
     * When given condition returns {@code true}, it prints out trace log.
     * The condition is used for dynamically turn on/off tracing.
     * The message consumer receives a tracing message that can be printed to console, logger, etc.
     *
     * @param condition       decide to turn on/off tracing
     * @param messageConsumer receives trace logging message
     * @return builder
     * @since 1.4.4
     */
    public ProxyDataSourceBuilder traceMethodsWhen(BooleanSupplier condition, Consumer<String> messageConsumer) {
        this.createTracingMethodListener = true;
        this.tracingCondition = condition;
        this.tracingMessageConsumer = messageConsumer;
        return this;
    }


    public DataSource build() {

        // Query Logging Listeners
        List<ProxyDataSourceListener> listeners = new ArrayList<>();

        // countQuery listener
        if (this.createDataSourceQueryCountListener) {
            listeners.add(new DataSourceQueryCountListener());
        }

        // tracing listener
        if (this.createTracingMethodListener) {
            this.listeners.add(buildTracingMethodListener());
        }

        // explicitly added listeners
        listeners.addAll(this.listeners);


        // build proxy config
        ProxyConfig.Builder proxyConfigBuilder = ProxyConfig.Builder.create();

        for (ProxyDataSourceListener listener : listeners) {
            proxyConfigBuilder.listener(listener);
        }

        if (this.queryTransformer != null) {
            proxyConfigBuilder.queryTransformer(this.queryTransformer);
        }


        // DataSource Name
        if (this.dataSourceName != null) {
            proxyConfigBuilder.dataSourceName(dataSourceName);
        }


        if (this.jdbcProxyFactory != null) {
            proxyConfigBuilder.jdbcProxyFactory(this.jdbcProxyFactory);
        } else {
            proxyConfigBuilder.jdbcProxyFactory(JdbcProxyFactory.DEFAULT);

        }

        if (this.connectionIdManager != null) {
            proxyConfigBuilder.connectionIdManager(this.connectionIdManager);
        } else {
            proxyConfigBuilder.connectionIdManager(new DefaultConnectionIdManager());
        }

        // this can be null if creation of resultset proxy is disabled
        proxyConfigBuilder.resultSetProxyLogicFactory(this.resultSetProxyLogicFactory);

        // generated keys
        proxyConfigBuilder.autoRetrieveGeneratedKeys(this.autoRetrieveGeneratedKeys);
        if (this.retrieveGeneratedKeysForBatchStatement != null) {
            proxyConfigBuilder.retrieveGeneratedKeysForBatchStatement(this.retrieveGeneratedKeysForBatchStatement);
        }
        if (this.retrieveGeneratedKeysForBatchPreparedOrCallable != null) {
            proxyConfigBuilder.retrieveGeneratedKeysForBatchPreparedOrCallable(this.retrieveGeneratedKeysForBatchPreparedOrCallable);
        }
        proxyConfigBuilder.autoCloseGeneratedKeys(this.autoCloseGeneratedKeys);
        // this can be null if creation of generated keys proxy is disabled
        proxyConfigBuilder.generatedKeysProxyLogicFactory(this.generatedKeysProxyLogicFactory);


        if (this.dataSource == null) {
            throw new DataSourceProxyException("Original DataSource is required.");
        }

        ProxyConfig proxyConfig = proxyConfigBuilder.build();
        DataSource proxyDataSource = proxyConfig.getJdbcProxyFactory().createDataSource(this.dataSource, proxyConfig);

        return proxyDataSource;
    }

    private TracingMethodListener buildTracingMethodListener() {
        TracingMethodListener listener = new TracingMethodListener();
        if (this.tracingMessageConsumer != null) {
            listener.setTracingMessageConsumer(this.tracingMessageConsumer);
        }
        if (this.tracingCondition != null) {
            listener.setTracingCondition(this.tracingCondition);
        }
        return listener;
    }

}
