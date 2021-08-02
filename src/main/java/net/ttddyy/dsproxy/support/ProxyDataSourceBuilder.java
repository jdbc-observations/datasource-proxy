package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.ConnectionIdManager;
import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.DataSourceQueryCountListener;
import net.ttddyy.dsproxy.listener.MethodExecutionContext;
import net.ttddyy.dsproxy.listener.MethodExecutionListener;
import net.ttddyy.dsproxy.listener.NoOpMethodExecutionListener;
import net.ttddyy.dsproxy.listener.NoOpQueryExecutionListener;
import net.ttddyy.dsproxy.listener.QueryCountStrategy;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.listener.TracingMethodListener;
import net.ttddyy.dsproxy.listener.lifecycle.JdbcLifecycleEventExecutionListener;
import net.ttddyy.dsproxy.listener.lifecycle.JdbcLifecycleEventListener;
import net.ttddyy.dsproxy.listener.logging.CommonsLogLevel;
import net.ttddyy.dsproxy.listener.logging.CommonsQueryLoggingListener;
import net.ttddyy.dsproxy.listener.logging.CommonsSlowQueryListener;
import net.ttddyy.dsproxy.listener.logging.DefaultJsonQueryLogEntryCreator;
import net.ttddyy.dsproxy.listener.logging.DefaultQueryLogEntryCreator;
import net.ttddyy.dsproxy.listener.logging.JULQueryLoggingListener;
import net.ttddyy.dsproxy.listener.logging.JULSlowQueryListener;
import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.listener.logging.SLF4JQueryLoggingListener;
import net.ttddyy.dsproxy.listener.logging.SLF4JSlowQueryListener;
import net.ttddyy.dsproxy.listener.logging.SystemOutQueryLoggingListener;
import net.ttddyy.dsproxy.listener.logging.SystemOutSlowQueryListener;
import net.ttddyy.dsproxy.proxy.DefaultConnectionIdManager;
import net.ttddyy.dsproxy.proxy.JdbcProxyFactory;
import net.ttddyy.dsproxy.proxy.ProxyConfig;
import net.ttddyy.dsproxy.proxy.RepeatableReadResultSetProxyLogicFactory;
import net.ttddyy.dsproxy.proxy.ResultSetProxyLogicFactory;
import net.ttddyy.dsproxy.transform.ParameterTransformer;
import net.ttddyy.dsproxy.transform.QueryTransformer;

import javax.sql.DataSource;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Builder for {@link net.ttddyy.dsproxy.support.ProxyDataSource}.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.3
 */
public class ProxyDataSourceBuilder {

    /**
     * Functional interface to simplify adding {@link MethodExecutionListener}.
     *
     * @see #beforeMethod(SingleMethodExecution)
     * @see #afterMethod(SingleMethodExecution)
     * @since 1.4.3
     */
    // TODO: add @FunctionalInterface once codebase is java8
    public interface SingleMethodExecution {
        void execute(MethodExecutionContext executionContext);
    }

    /**
     * Functional interface to simplify adding {@link QueryExecutionListener}.
     *
     * @see #beforeQuery(SingleQueryExecution)
     * @see #afterQuery(SingleQueryExecution)
     * @since 1.4.3
     */
    // TODO: add @FunctionalInterface once codebase is java8
    public interface SingleQueryExecution {
        void execute(ExecutionInfo execInfo, List<QueryInfo> queryInfoList);
    }

    private DataSource dataSource;
    private String dataSourceName;

    // For building TracingMethodListener
    private boolean createTracingMethodListener;
    private TracingMethodListener.TracingCondition tracingCondition;
    private TracingMethodListener.TracingMessageConsumer tracingMessageConsumer;

    // For building QueryLoggingListeners

    // CommonsQueryLoggingListener
    private boolean createCommonsQueryListener;
    private CommonsLogLevel commonsLogLevel;
    private String commonsLoggerName;

    // for SLF4JQueryLoggingListener
    private boolean createSlf4jQueryListener;
    private SLF4JLogLevel slf4jLogLevel;
    private String slf4jLoggerName;

    // for JULQueryLoggingListener
    private boolean createJulQueryListener;
    private Level julLogLevel;
    private String julLoggerName;

    // for SystemOutQueryLoggingListener
    private boolean createSysOutQueryListener;


    // For building  SlowQueryListeners

    private long slowQueryThreshold;
    private TimeUnit slowQueryTimeUnit;

    // for CommonsSlowQueryListener
    private boolean createCommonsSlowQueryListener;
    private CommonsLogLevel commonsSlowQueryLogLevel;
    private String commonsSlowQueryLogName;

    // for SLF4JSlowQueryListener
    private boolean createSlf4jSlowQueryListener;
    private SLF4JLogLevel slf4jSlowQueryLogLevel;
    private String slf4jSlowQueryLoggerName;

    // for JULSlowQueryListener
    private boolean createJulSlowQueryListener;
    private Level julSlowQueryLogLevel;
    private String julSlowQueryLoggerName;

    // for SystemOutSlowQueryListener
    private boolean createSysOutSlowQueryListener;

    private boolean createDataSourceQueryCountListener;
    private QueryCountStrategy queryCountStrategy;

    private boolean jsonFormat;
    private boolean multiline;
    private boolean writeIsolation;
    private List<QueryExecutionListener> queryExecutionListeners = new ArrayList<QueryExecutionListener>();

    private ParameterTransformer parameterTransformer;
    private QueryTransformer queryTransformer;

    private JdbcProxyFactory jdbcProxyFactory;
    private ConnectionIdManager connectionIdManager;

    private ResultSetProxyLogicFactory resultSetProxyLogicFactory;

    private boolean autoRetrieveGeneratedKeys;
    private Boolean retrieveGeneratedKeysForBatchStatement;
    private Boolean retrieveGeneratedKeysForBatchPreparedOrCallable;
    private boolean autoCloseGeneratedKeys;
    private ResultSetProxyLogicFactory generatedKeysProxyLogicFactory;

    private List<MethodExecutionListener> methodExecutionListeners = new ArrayList<MethodExecutionListener>();

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
     * Register {@link CommonsQueryLoggingListener}.
     *
     * @return builder
     */
    public ProxyDataSourceBuilder logQueryByCommons() {
        return logQueryByCommons(null, null);
    }

    /**
     * Register {@link CommonsQueryLoggingListener}.
     *
     * @param logLevel log level for commons
     * @return builder
     */
    public ProxyDataSourceBuilder logQueryByCommons(CommonsLogLevel logLevel) {
        return logQueryByCommons(logLevel, null);
    }

    /**
     * Register {@link CommonsQueryLoggingListener}.
     *
     * @param commonsLoggerName commons logger name
     * @return builder
     * @since 1.3.1
     */
    public ProxyDataSourceBuilder logQueryByCommons(String commonsLoggerName) {
        return logQueryByCommons(null, commonsLoggerName);
    }

    /**
     * Register {@link CommonsQueryLoggingListener}.
     *
     * @param logLevel          log level for commons
     * @param commonsLoggerName commons logger name
     * @return builder
     * @since 1.3.1
     */
    public ProxyDataSourceBuilder logQueryByCommons(CommonsLogLevel logLevel, String commonsLoggerName) {
        this.createCommonsQueryListener = true;
        this.commonsLogLevel = logLevel;
        this.commonsLoggerName = commonsLoggerName;
        return this;
    }

    /**
     * Register {@link CommonsSlowQueryListener}.
     *
     * @param thresholdTime slow query threshold time
     * @param timeUnit      slow query threshold time unit
     * @return builder
     * @since 1.4.1
     */
    public ProxyDataSourceBuilder logSlowQueryByCommons(long thresholdTime, TimeUnit timeUnit) {
        return logSlowQueryByCommons(thresholdTime, timeUnit, null, null);
    }

    /**
     * Register {@link CommonsSlowQueryListener}.
     *
     * @param thresholdTime slow query threshold time
     * @param timeUnit      slow query threshold time unit
     * @param logLevel      log level for commons
     * @return builder
     * @since 1.4.1
     */
    public ProxyDataSourceBuilder logSlowQueryByCommons(long thresholdTime, TimeUnit timeUnit, CommonsLogLevel logLevel) {
        return logSlowQueryByCommons(thresholdTime, timeUnit, logLevel, null);
    }

    /**
     * Register {@link CommonsSlowQueryListener}.
     *
     * @param thresholdTime slow query threshold time
     * @param timeUnit      slow query threshold time unit
     * @param logName       commons logger name
     * @return builder
     * @since 1.4.1
     */
    public ProxyDataSourceBuilder logSlowQueryByCommons(long thresholdTime, TimeUnit timeUnit, String logName) {
        return logSlowQueryByCommons(thresholdTime, timeUnit, null, logName);
    }

    /**
     * Register {@link CommonsSlowQueryListener}.
     *
     * @param thresholdTime slow query threshold time
     * @param timeUnit      slow query threshold time unit
     * @param logLevel      log level for commons
     * @param logName       commons logger name
     * @return builder
     * @since 1.4.1
     */
    public ProxyDataSourceBuilder logSlowQueryByCommons(long thresholdTime, TimeUnit timeUnit, CommonsLogLevel logLevel, String logName) {
        this.createCommonsSlowQueryListener = true;
        this.slowQueryThreshold = thresholdTime;
        this.slowQueryTimeUnit = timeUnit;
        this.commonsSlowQueryLogLevel = logLevel;
        this.commonsSlowQueryLogName = logName;
        return this;
    }

    /**
     * Register {@link SLF4JQueryLoggingListener}.
     *
     * @return builder
     */
    public ProxyDataSourceBuilder logQueryBySlf4j() {
        return logQueryBySlf4j(null, null);
    }

    /**
     * Register {@link SLF4JQueryLoggingListener}.
     *
     * @param logLevel log level for slf4j
     * @return builder
     */
    public ProxyDataSourceBuilder logQueryBySlf4j(SLF4JLogLevel logLevel) {
        return logQueryBySlf4j(logLevel, null);
    }

    /**
     * Register {@link SLF4JQueryLoggingListener}.
     *
     * @param slf4jLoggerName slf4j logger name
     * @return builder
     * @since 1.3.1
     */
    public ProxyDataSourceBuilder logQueryBySlf4j(String slf4jLoggerName) {
        return logQueryBySlf4j(null, slf4jLoggerName);
    }

    /**
     * Register {@link SLF4JQueryLoggingListener}.
     *
     * @param logLevel        log level for slf4j
     * @param slf4jLoggerName slf4j logger name
     * @return builder
     * @since 1.3.1
     */
    public ProxyDataSourceBuilder logQueryBySlf4j(SLF4JLogLevel logLevel, String slf4jLoggerName) {
        this.createSlf4jQueryListener = true;
        this.slf4jLogLevel = logLevel;
        this.slf4jLoggerName = slf4jLoggerName;
        return this;
    }

    /**
     * Register {@link SLF4JSlowQueryListener}.
     *
     * @param thresholdTime slow query threshold time
     * @param timeUnit      slow query threshold time unit
     * @return builder
     * @since 1.4.1
     */
    public ProxyDataSourceBuilder logSlowQueryBySlf4j(long thresholdTime, TimeUnit timeUnit) {
        return logSlowQueryBySlf4j(thresholdTime, timeUnit, null, null);
    }

    /**
     * Register {@link SLF4JSlowQueryListener}.
     *
     * @param thresholdTime slow query threshold time
     * @param timeUnit      slow query threshold time unit
     * @param logLevel      log level for slf4j
     * @return builder
     * @since 1.4.1
     */
    public ProxyDataSourceBuilder logSlowQueryBySlf4j(long thresholdTime, TimeUnit timeUnit, SLF4JLogLevel logLevel) {
        return logSlowQueryBySlf4j(thresholdTime, timeUnit, logLevel, null);
    }

    /**
     * Register {@link SLF4JSlowQueryListener}.
     *
     * @param thresholdTime slow query threshold time
     * @param timeUnit      slow query threshold time unit
     * @param loggerName    slf4j logger name
     * @return builder
     * @since 1.4.1
     */
    public ProxyDataSourceBuilder logSlowQueryBySlf4j(long thresholdTime, TimeUnit timeUnit, String loggerName) {
        return logSlowQueryBySlf4j(thresholdTime, timeUnit, null, loggerName);
    }

    /**
     * Register {@link SLF4JSlowQueryListener}.
     *
     * @param thresholdTime slow query threshold time
     * @param timeUnit      slow query threshold time unit
     * @param logLevel      log level for slf4j
     * @param loggerName    slf4j logger name
     * @return builder
     * @since 1.4.1
     */
    public ProxyDataSourceBuilder logSlowQueryBySlf4j(long thresholdTime, TimeUnit timeUnit, SLF4JLogLevel logLevel, String loggerName) {
        this.createSlf4jSlowQueryListener = true;
        this.slowQueryThreshold = thresholdTime;
        this.slowQueryTimeUnit = timeUnit;
        this.slf4jSlowQueryLogLevel = logLevel;
        this.slf4jSlowQueryLoggerName = loggerName;
        return this;
    }

    /**
     * Register {@link JULQueryLoggingListener}.
     *
     * @return builder
     * @since 1.4
     */
    public ProxyDataSourceBuilder logQueryByJUL() {
        return logQueryByJUL(null, null);
    }

    /**
     * Register {@link JULQueryLoggingListener}.
     *
     * @param logLevel log level for JUL
     * @return builder
     * @since 1.4
     */
    public ProxyDataSourceBuilder logQueryByJUL(Level logLevel) {
        return logQueryByJUL(logLevel, null);
    }

    /**
     * Register {@link JULQueryLoggingListener}.
     *
     * @param julLoggerName JUL logger name
     * @return builder
     * @since 1.4
     */
    public ProxyDataSourceBuilder logQueryByJUL(String julLoggerName) {
        return logQueryByJUL(null, julLoggerName);
    }

    /**
     * Register {@link JULQueryLoggingListener}.
     *
     * @param logLevel      log level for JUL
     * @param julLoggerName JUL logger name
     * @return builder
     * @since 1.4
     */
    public ProxyDataSourceBuilder logQueryByJUL(Level logLevel, String julLoggerName) {
        this.createJulQueryListener = true;
        this.julLogLevel = logLevel;
        this.julLoggerName = julLoggerName;
        return this;
    }

    /**
     * Register {@link JULSlowQueryListener}.
     *
     * @param thresholdTime slow query threshold time
     * @param timeUnit      slow query threshold time unit
     * @return builder
     * @since 1.4.1
     */
    public ProxyDataSourceBuilder logSlowQueryByJUL(long thresholdTime, TimeUnit timeUnit) {
        return logSlowQueryByJUL(thresholdTime, timeUnit, null, null);
    }

    /**
     * Register {@link JULSlowQueryListener}.
     *
     * @param thresholdTime slow query threshold time
     * @param timeUnit      slow query threshold time unit
     * @param logLevel      log level for JUL
     * @return builder
     * @since 1.4.1
     */
    public ProxyDataSourceBuilder logSlowQueryByJUL(long thresholdTime, TimeUnit timeUnit, Level logLevel) {
        return logSlowQueryByJUL(thresholdTime, timeUnit, logLevel, null);
    }

    /**
     * Register {@link JULSlowQueryListener}.
     *
     * @param thresholdTime slow query threshold time
     * @param timeUnit      slow query threshold time unit
     * @param loggerName    JUL logger name
     * @return builder
     * @since 1.4.1
     */
    public ProxyDataSourceBuilder logSlowQueryByJUL(long thresholdTime, TimeUnit timeUnit, String loggerName) {
        return logSlowQueryByJUL(thresholdTime, timeUnit, null, loggerName);
    }

    /**
     * Register {@link JULSlowQueryListener}.
     *
     * @param thresholdTime slow query threshold time
     * @param timeUnit      slow query threshold time unit
     * @param logLevel      log level for JUL
     * @param loggerName    JUL logger name
     * @return builder
     * @since 1.4.1
     */
    public ProxyDataSourceBuilder logSlowQueryByJUL(long thresholdTime, TimeUnit timeUnit, Level logLevel, String loggerName) {
        this.createJulSlowQueryListener = true;
        this.slowQueryThreshold = thresholdTime;
        this.slowQueryTimeUnit = timeUnit;
        this.julSlowQueryLogLevel = logLevel;
        this.julSlowQueryLoggerName = loggerName;
        return this;
    }


    /**
     * Register {@link SystemOutQueryLoggingListener}.
     *
     * @return builder
     */
    public ProxyDataSourceBuilder logQueryToSysOut() {
        this.createSysOutQueryListener = true;
        return this;

    }

    /**
     * Register {@link SystemOutSlowQueryListener}.
     *
     * @return builder
     * @since 1.4.1
     */
    public ProxyDataSourceBuilder logSlowQueryToSysOut(long thresholdTime, TimeUnit timeUnit) {
        this.createSysOutSlowQueryListener = true;
        this.slowQueryThreshold = thresholdTime;
        this.slowQueryTimeUnit = timeUnit;
        return this;
    }

    /**
     * Create {@link net.ttddyy.dsproxy.listener.DataSourceQueryCountListener}.
     *
     * @return builder
     */
    public ProxyDataSourceBuilder countQuery() {
        this.createDataSourceQueryCountListener = true;
        return this;
    }

    /**
     * Create {@link net.ttddyy.dsproxy.listener.DataSourceQueryCountListener}.
     *
     * @return builder
     * @since 1.4.2
     */
    public ProxyDataSourceBuilder countQuery(QueryCountStrategy queryCountStrategy) {
        this.createDataSourceQueryCountListener = true;
        this.queryCountStrategy = queryCountStrategy;
        return this;
    }

    /**
     * Register given listener.
     *
     * @param listener a listener to register
     * @return builder
     */
    public ProxyDataSourceBuilder listener(QueryExecutionListener listener) {
        this.queryExecutionListeners.add(listener);
        return this;
    }

    /**
     * Register given {@link JdbcLifecycleEventListener}.
     *
     * @param listener a listener to register
     * @return builder
     * @since 1.5
     */
    public ProxyDataSourceBuilder listener(JdbcLifecycleEventListener listener) {
        JdbcLifecycleEventExecutionListener executionListener = new JdbcLifecycleEventExecutionListener(listener);
        this.queryExecutionListeners.add(executionListener);
        this.methodExecutionListeners.add(executionListener);
        return this;
    }

    /**
     * Add {@link QueryExecutionListener} that performs given lambda on {@link QueryExecutionListener#beforeQuery(ExecutionInfo, List)}.
     *
     * @param callback a lambda function executed on {@link QueryExecutionListener#beforeQuery(ExecutionInfo, List)}
     * @return builder
     * @since 1.4.3
     */
    public ProxyDataSourceBuilder beforeQuery(final SingleQueryExecution callback) {
        QueryExecutionListener listener = new NoOpQueryExecutionListener() {
            @Override
            public void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
                callback.execute(execInfo, queryInfoList);
            }
        };
        this.queryExecutionListeners.add(listener);
        return this;
    }

    /**
     * Add {@link QueryExecutionListener} that performs given lambda on {@link QueryExecutionListener#afterQuery(ExecutionInfo, List)}.
     *
     * @param callback a lambda function executed on {@link QueryExecutionListener#afterQuery(ExecutionInfo, List)}
     * @return builder
     * @since 1.4.3
     */
    public ProxyDataSourceBuilder afterQuery(final SingleQueryExecution callback) {
        QueryExecutionListener listener = new NoOpQueryExecutionListener() {
            @Override
            public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
                callback.execute(execInfo, queryInfoList);
            }
        };
        this.queryExecutionListeners.add(listener);
        return this;
    }

    /**
     * Format logging output as JSON.
     *
     * @return builder
     */
    public ProxyDataSourceBuilder asJson() {
        this.jsonFormat = true;
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
     * Register {@link net.ttddyy.dsproxy.transform.ParameterTransformer}.
     *
     * @param parameterTransformer a query-parameter-transformer to register
     * @return builder
     */
    public ProxyDataSourceBuilder parameterTransformer(ParameterTransformer parameterTransformer) {
        this.parameterTransformer = parameterTransformer;
        return this;
    }

    /**
     * Use multiline output for logging query.
     *
     * @return builder
     * @since 1.4.1
     */
    public ProxyDataSourceBuilder multiline() {
        this.multiline = true;
        return this;
    }

    /**
     * Add connection isolation to logging query.
     *
     * @return builder
     * @since 1.8
     */
    public ProxyDataSourceBuilder writeIsolation() {
        this.writeIsolation = true;
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
     * The retrieved {@link java.sql.ResultSet} is available via {@link ExecutionInfo#getGeneratedKeys()}.
     *
     * When this configuration is combined with {@link #proxyGeneratedKeys(ResultSetProxyLogicFactory)}, the proxied
     * {@link java.sql.ResultSet} will be returned from {@link ExecutionInfo#getGeneratedKeys()}.
     *
     * When autoClose parameter is set to {@code true}, datasource-proxy will close the generated-keys {@link java.sql.ResultSet}
     * after it called {@link QueryExecutionListener#afterQuery(ExecutionInfo, List)}.
     * This behavior might not be ideal if above layer, such as OR Mapper or application code, need to access generated-keys
     * because when they access generated-keys, the resultset is already closed.
     *
     * To support such usecase, specify {@link RepeatableReadResultSetProxyLogicFactory} and set {@code autoClose=false}.
     * This way, even though your {@link QueryExecutionListener} has accessed generated-keys, it is still readable at
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
     * Add {@link MethodExecutionListener}.
     *
     * @param listener a method execution listener
     * @return builder
     * @since 1.4.3
     */
    public ProxyDataSourceBuilder methodListener(MethodExecutionListener listener) {
        this.methodExecutionListeners.add(listener);
        return this;
    }

    /**
     * Add {@link MethodExecutionListener} that performs given lambda on {@link MethodExecutionListener#beforeMethod(MethodExecutionContext)}.
     *
     * @param callback a lambda function executed on {@link MethodExecutionListener#beforeMethod(MethodExecutionContext)}
     * @return builder
     * @since 1.4.3
     */
    public ProxyDataSourceBuilder beforeMethod(final SingleMethodExecution callback) {
        MethodExecutionListener listener = new NoOpMethodExecutionListener() {
            @Override
            public void beforeMethod(MethodExecutionContext executionContext) {
                callback.execute(executionContext);
            }
        };
        this.methodExecutionListeners.add(listener);
        return this;
    }

    /**
     * Add {@link MethodExecutionListener} that performs given lambda on {@link MethodExecutionListener#afterMethod(MethodExecutionContext)}.
     *
     * @param callback a lambda function executed on {@link MethodExecutionListener#afterMethod(MethodExecutionContext)}
     * @return builder
     * @since 1.4.3
     */
    public ProxyDataSourceBuilder afterMethod(final SingleMethodExecution callback) {
        MethodExecutionListener listener = new NoOpMethodExecutionListener() {
            @Override
            public void afterMethod(MethodExecutionContext executionContext) {
                callback.execute(executionContext);
            }
        };
        this.methodExecutionListeners.add(listener);
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
    public ProxyDataSourceBuilder traceMethods(TracingMethodListener.TracingMessageConsumer messageConsumer) {
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
    public ProxyDataSourceBuilder traceMethodsWhen(TracingMethodListener.TracingCondition condition) {
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
    public ProxyDataSourceBuilder traceMethodsWhen(TracingMethodListener.TracingCondition condition, TracingMethodListener.TracingMessageConsumer messageConsumer) {
        this.createTracingMethodListener = true;
        this.tracingCondition = condition;
        this.tracingMessageConsumer = messageConsumer;
        return this;
    }


    public ProxyDataSource build() {

        // Query Logging Listeners
        List<QueryExecutionListener> listeners = new ArrayList<QueryExecutionListener>();

        // query logging listeners
        if (this.createCommonsQueryListener) {
            listeners.add(buildCommonsQueryListener());
        }
        if (this.createSlf4jQueryListener) {
            listeners.add(buildSlf4jQueryListener());
        }
        if (this.createJulQueryListener) {
            listeners.add(buildJulQueryListener());
        }
        if (this.createSysOutQueryListener) {
            listeners.add(buildSysOutQueryListener());
        }

        // slow query logging listeners
        if (this.createCommonsSlowQueryListener) {
            listeners.add(buildCommonsSlowQueryListener());
        }
        if (this.createSlf4jSlowQueryListener) {
            listeners.add(buildSlf4jSlowQueryListener());
        }
        if (this.createJulSlowQueryListener) {
            listeners.add(buildJulSlowQueryListener());
        }
        if (this.createSysOutSlowQueryListener) {
            listeners.add(buildSysOutSlowQueryListener());
        }


        // countQuery listener
        if (this.createDataSourceQueryCountListener) {
            DataSourceQueryCountListener countListener = new DataSourceQueryCountListener();

            if (this.queryCountStrategy != null) {
                countListener.setQueryCountStrategy(this.queryCountStrategy);
            }

            listeners.add(countListener);
        }

        // tracing listener
        if (this.createTracingMethodListener) {
            this.methodExecutionListeners.add(buildTracingMethodListenr());
        }

        // explicitly added listeners
        listeners.addAll(this.queryExecutionListeners);


        // build proxy config
        ProxyConfig.Builder proxyConfigBuilder = ProxyConfig.Builder.create();

        for (QueryExecutionListener listener : listeners) {
            proxyConfigBuilder.queryListener(listener);
        }

        for (MethodExecutionListener methodListener : this.methodExecutionListeners) {
            proxyConfigBuilder.methodListener(methodListener);
        }

        if (this.queryTransformer != null) {
            proxyConfigBuilder.queryTransformer(this.queryTransformer);
        }
        if (this.parameterTransformer != null) {
            proxyConfigBuilder.parameterTransformer(this.parameterTransformer);
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


        // build ProxyDataSource
        ProxyDataSource proxyDataSource = new ProxyDataSource();
        if (this.dataSource != null) {
            proxyDataSource.setDataSource(dataSource);
        }
        ProxyConfig proxyConfig = proxyConfigBuilder.build();
        proxyDataSource.setProxyConfig(proxyConfig);

        return proxyDataSource;
    }

    private CommonsQueryLoggingListener buildCommonsQueryListener() {
        CommonsQueryLoggingListener listener = new CommonsQueryLoggingListener();
        if (this.commonsLogLevel != null) {
            listener.setLogLevel(this.commonsLogLevel);
        }
        if (this.commonsLoggerName != null && !this.commonsLoggerName.isEmpty()) {
            listener.setLog(this.commonsLoggerName);
        }
        if (this.jsonFormat) {
            listener.setQueryLogEntryCreator(new DefaultJsonQueryLogEntryCreator());
        }
        if (this.multiline) {
            listener.setQueryLogEntryCreator(buildMultilineQueryLogEntryCreator());
        }
        if (this.writeIsolation) {
            listener.setWriteIsolation(true);
        }
        return listener;
    }

    private CommonsSlowQueryListener buildCommonsSlowQueryListener() {
        CommonsSlowQueryListener listener = new CommonsSlowQueryListener(this.slowQueryThreshold, this.slowQueryTimeUnit);
        if (this.commonsSlowQueryLogLevel != null) {
            listener.setLogLevel(this.commonsSlowQueryLogLevel);
        }
        if (this.commonsSlowQueryLogName != null) {
            listener.setLog(this.commonsSlowQueryLogName);
        }
        if (this.jsonFormat) {
            listener.setQueryLogEntryCreator(new DefaultJsonQueryLogEntryCreator());
        }
        if (this.multiline) {
            listener.setQueryLogEntryCreator(buildMultilineQueryLogEntryCreator());
        }
        if (this.writeIsolation) {
            listener.setWriteIsolation(true);
        }
        return listener;
    }

    private SLF4JQueryLoggingListener buildSlf4jQueryListener() {
        SLF4JQueryLoggingListener listener = new SLF4JQueryLoggingListener();
        if (this.slf4jLogLevel != null) {
            listener.setLogLevel(this.slf4jLogLevel);
        }
        if (this.slf4jLoggerName != null && !this.slf4jLoggerName.isEmpty()) {
            listener.setLogger(this.slf4jLoggerName);
        }
        if (this.jsonFormat) {
            listener.setQueryLogEntryCreator(new DefaultJsonQueryLogEntryCreator());
        }
        if (this.multiline) {
            listener.setQueryLogEntryCreator(buildMultilineQueryLogEntryCreator());
        }
        if (this.writeIsolation) {
            listener.setWriteIsolation(true);
        }
        return listener;
    }

    private SLF4JSlowQueryListener buildSlf4jSlowQueryListener() {
        SLF4JSlowQueryListener listener = new SLF4JSlowQueryListener(this.slowQueryThreshold, this.slowQueryTimeUnit);
        if (this.slf4jSlowQueryLogLevel != null) {
            listener.setLogLevel(this.slf4jSlowQueryLogLevel);
        }
        if (this.slf4jSlowQueryLoggerName != null) {
            listener.setLogger(this.slf4jSlowQueryLoggerName);
        }
        if (this.jsonFormat) {
            listener.setQueryLogEntryCreator(new DefaultJsonQueryLogEntryCreator());
        }
        if (this.multiline) {
            listener.setQueryLogEntryCreator(buildMultilineQueryLogEntryCreator());
        }
        if (this.writeIsolation) {
            listener.setWriteIsolation(true);
        }
        return listener;
    }

    private JULQueryLoggingListener buildJulQueryListener() {
        JULQueryLoggingListener listener = new JULQueryLoggingListener();
        if (this.julLogLevel != null) {
            listener.setLogLevel(this.julLogLevel);
        }
        if (this.julLoggerName != null && !this.julLoggerName.isEmpty()) {
            listener.setLogger(this.julLoggerName);
        }
        if (this.jsonFormat) {
            listener.setQueryLogEntryCreator(new DefaultJsonQueryLogEntryCreator());
        }
        if (this.multiline) {
            listener.setQueryLogEntryCreator(buildMultilineQueryLogEntryCreator());
        }
        if (this.writeIsolation) {
            listener.setWriteIsolation(true);
        }
        return listener;
    }

    private JULSlowQueryListener buildJulSlowQueryListener() {
        JULSlowQueryListener listener = new JULSlowQueryListener(this.slowQueryThreshold, this.slowQueryTimeUnit);
        if (this.julSlowQueryLogLevel != null) {
            listener.setLogLevel(this.julSlowQueryLogLevel);
        }
        if (this.julSlowQueryLoggerName != null) {
            listener.setLogger(this.julSlowQueryLoggerName);
        }
        if (this.jsonFormat) {
            listener.setQueryLogEntryCreator(new DefaultJsonQueryLogEntryCreator());
        }
        if (this.multiline) {
            listener.setQueryLogEntryCreator(buildMultilineQueryLogEntryCreator());
        }
        if (this.writeIsolation) {
            listener.setWriteIsolation(true);
        }
        return listener;
    }

    private SystemOutQueryLoggingListener buildSysOutQueryListener() {
        SystemOutQueryLoggingListener listener = new SystemOutQueryLoggingListener();
        if (this.jsonFormat) {
            listener.setQueryLogEntryCreator(new DefaultJsonQueryLogEntryCreator());
        }
        if (this.multiline) {
            listener.setQueryLogEntryCreator(buildMultilineQueryLogEntryCreator());
        }
        if (this.writeIsolation) {
            listener.setWriteIsolation(true);
        }
        return listener;
    }

    private SystemOutSlowQueryListener buildSysOutSlowQueryListener() {
        SystemOutSlowQueryListener listener = new SystemOutSlowQueryListener(this.slowQueryThreshold, this.slowQueryTimeUnit);
        if (this.jsonFormat) {
            listener.setQueryLogEntryCreator(new DefaultJsonQueryLogEntryCreator());
        }
        if (this.multiline) {
            listener.setQueryLogEntryCreator(buildMultilineQueryLogEntryCreator());
        }
        if (this.writeIsolation) {
            listener.setWriteIsolation(true);
        }
        return listener;
    }

    private DefaultQueryLogEntryCreator buildMultilineQueryLogEntryCreator() {
        DefaultQueryLogEntryCreator entryCreator = new DefaultQueryLogEntryCreator();
        entryCreator.setMultiline(true);
        return entryCreator;
    }

    private TracingMethodListener buildTracingMethodListenr() {
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
