package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.ConnectionIdManager;
import net.ttddyy.dsproxy.listener.DataSourceQueryCountListener;
import net.ttddyy.dsproxy.listener.QueryCountStrategy;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
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
import net.ttddyy.dsproxy.proxy.JdbcProxyFactory;
import net.ttddyy.dsproxy.proxy.ProxyConfig;
import net.ttddyy.dsproxy.proxy.RepeatableReadResultSetProxyLogicFactory;
import net.ttddyy.dsproxy.proxy.ResultSetProxyLogicFactory;
import net.ttddyy.dsproxy.transform.ParameterTransformer;
import net.ttddyy.dsproxy.transform.QueryTransformer;

import javax.sql.DataSource;
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

    private DataSource dataSource;
    private String dataSourceName;

    // for building QueryLoggingListeners

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
    private List<QueryExecutionListener> queryExecutionListeners = new ArrayList<QueryExecutionListener>();

    private ParameterTransformer parameterTransformer;
    private QueryTransformer queryTransformer;

    private JdbcProxyFactory jdbcProxyFactory;
    private ConnectionIdManager connectionIdManager;

    private ResultSetProxyLogicFactory resultSetProxyLogicFactory;

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


        // explicitly added listeners
        listeners.addAll(this.queryExecutionListeners);


        // build proxy config
        ProxyConfig.Builder proxyConfigBuilder = ProxyConfig.Builder.create();

        for (QueryExecutionListener listener : listeners) {
            proxyConfigBuilder.queryListener(listener);
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
            proxyConfigBuilder.connectionIdManager(ConnectionIdManager.DEFAULT);
        }

        // this can be null if creation of resultset proxy is disabled
        proxyConfigBuilder.resultSetProxyLogicFactory(this.resultSetProxyLogicFactory);

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
        if (this.multiline) {
            listener.setQueryLogEntryCreator(buildMultilineQueryLogEntryCreator());
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
        if (this.multiline) {
            listener.setQueryLogEntryCreator(buildMultilineQueryLogEntryCreator());
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
        if (this.multiline) {
            listener.setQueryLogEntryCreator(buildMultilineQueryLogEntryCreator());
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
        return listener;
    }

    private SystemOutSlowQueryListener buildSysOutSlowQueryListener() {
        SystemOutSlowQueryListener listener = new SystemOutSlowQueryListener(this.slowQueryThreshold, this.slowQueryTimeUnit);
        if (this.multiline) {
            listener.setQueryLogEntryCreator(buildMultilineQueryLogEntryCreator());
        }
        return listener;
    }

    private DefaultQueryLogEntryCreator buildMultilineQueryLogEntryCreator() {
        DefaultQueryLogEntryCreator entryCreator = new DefaultQueryLogEntryCreator();
        entryCreator.setMultiline(true);
        return entryCreator;
    }

}
