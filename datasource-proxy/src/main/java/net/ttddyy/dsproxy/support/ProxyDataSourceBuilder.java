package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.listener.DataSourceQueryCountListener;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.listener.logging.CommonsLogLevel;
import net.ttddyy.dsproxy.listener.logging.CommonsQueryLoggingListener;
import net.ttddyy.dsproxy.listener.logging.DefaultJsonQueryLogEntryCreator;
import net.ttddyy.dsproxy.listener.logging.JULQueryLoggingListener;
import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.listener.logging.SLF4JQueryLoggingListener;
import net.ttddyy.dsproxy.listener.logging.SystemOutQueryLoggingListener;
import net.ttddyy.dsproxy.transform.ParameterTransformer;
import net.ttddyy.dsproxy.transform.QueryTransformer;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
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

    private boolean createCommonsQueryListener;
    private CommonsLogLevel commonsLogLevel;
    private boolean createSlf4jQueryListener;
    private SLF4JLogLevel slf4jLogLevel;
    private boolean createJULQueryListener;
    private Level julLogLevel;
    private String loggerName;
    private boolean createSysOutQueryListener;
    private boolean createDataSourceQueryCountListener;
    private boolean jsonFormat;
    private List<QueryExecutionListener> queryExecutionListeners = new ArrayList<QueryExecutionListener>();

    private ParameterTransformer parameterTransformer;
    private QueryTransformer queryTransformer;

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
        this.createCommonsQueryListener = true;
        return this;
    }

    /**
     * Register {@link CommonsQueryLoggingListener}.
     *
     * @param logLevel log level for commons
     * @return builder
     */
    public ProxyDataSourceBuilder logQueryByCommons(CommonsLogLevel logLevel) {
        this.createCommonsQueryListener = true;
        this.commonsLogLevel = logLevel;
        return this;
    }

    /**
     * Register {@link CommonsQueryLoggingListener}.
     *
     * @param loggerName logger name
     * @return builder
     * @since 1.3.1
     */
    public ProxyDataSourceBuilder logQueryByCommons(String loggerName) {
        this.createCommonsQueryListener = true;
        this.loggerName = loggerName;
        return this;
    }

    /**
     * Register {@link CommonsQueryLoggingListener}.
     *
     * @param logLevel   log level for commons
     * @param loggerName logger name
     * @return builder
     * @since 1.3.1
     */
    public ProxyDataSourceBuilder logQueryByCommons(CommonsLogLevel logLevel, String loggerName) {
        this.createCommonsQueryListener = true;
        this.commonsLogLevel = logLevel;
        this.loggerName = loggerName;
        return this;
    }

    /**
     * Register {@link SLF4JQueryLoggingListener}.
     *
     * @return builder
     */
    public ProxyDataSourceBuilder logQueryBySlf4j() {
        this.createSlf4jQueryListener = true;
        return this;
    }

    /**
     * Register {@link SLF4JQueryLoggingListener}.
     *
     * @param logLevel log level for slf4j
     * @return builder
     */
    public ProxyDataSourceBuilder logQueryBySlf4j(SLF4JLogLevel logLevel) {
        this.createSlf4jQueryListener = true;
        this.slf4jLogLevel = logLevel;
        return this;
    }

    /**
     * Register {@link SLF4JQueryLoggingListener}.
     *
     * @param loggerName logger name
     * @return builder
     * @since 1.3.1
     */
    public ProxyDataSourceBuilder logQueryBySlf4j(String loggerName) {
        this.createSlf4jQueryListener = true;
        this.loggerName = loggerName;
        return this;
    }

    /**
     * Register {@link SLF4JQueryLoggingListener}.
     *
     * @param logLevel   log level for slf4j
     * @param loggerName logger name
     * @return builder
     * @since 1.3.1
     */
    public ProxyDataSourceBuilder logQueryBySlf4j(SLF4JLogLevel logLevel, String loggerName) {
        this.createSlf4jQueryListener = true;
        this.slf4jLogLevel = logLevel;
        this.loggerName = loggerName;
        return this;
    }

    /**
     * Register {@link JULQueryLoggingListener}.
     *
     * @return builder
     * @since 1.4
     */
    public ProxyDataSourceBuilder logQueryByJUL() {
        this.createJULQueryListener = true;
        return this;
    }

    /**
     * Register {@link JULQueryLoggingListener}.
     *
     * @param logLevel log level for JUL
     * @return builder
     * @since 1.4
     */
    public ProxyDataSourceBuilder logQueryByJUL(Level logLevel) {
        this.createJULQueryListener = true;
        this.julLogLevel = logLevel;
        return this;
    }

    /**
     * Register {@link JULQueryLoggingListener}.
     *
     * @param loggerName logger name
     * @return builder
     * @since 1.4
     */
    public ProxyDataSourceBuilder logQueryByJUL(String loggerName) {
        this.createJULQueryListener = true;
        this.loggerName = loggerName;
        return this;
    }

    /**
     * Register {@link JULQueryLoggingListener}.
     *
     * @param logLevel   log level for JUL
     * @param loggerName logger name
     * @return builder
     * @since 1.4
     */
    public ProxyDataSourceBuilder logQueryByJUL(Level logLevel, String loggerName) {
        this.createJULQueryListener = true;
        this.julLogLevel = logLevel;
        this.loggerName = loggerName;
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
     * Create {@link net.ttddyy.dsproxy.listener.DataSourceQueryCountListener}.
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

    public ProxyDataSource build() {
        ProxyDataSource proxyDataSource = new ProxyDataSource();

        if (this.dataSource != null) {
            proxyDataSource.setDataSource(dataSource);
        }

        // DataSource Name
        if (this.dataSourceName != null) {
            proxyDataSource.setDataSourceName(dataSourceName);
        }

        // Query Logging Listeners
        List<QueryExecutionListener> listeners = new ArrayList<QueryExecutionListener>();
        if (this.createCommonsQueryListener) {
            CommonsQueryLoggingListener listener = new CommonsQueryLoggingListener();
            if (this.commonsLogLevel != null) {
                listener.setLogLevel(this.commonsLogLevel);
            }
            if (this.loggerName != null && !this.loggerName.isEmpty()) {
                listener.setLoggerName(this.loggerName);
            }
            if (this.jsonFormat) {
                listener.setQueryLogEntryCreator(new DefaultJsonQueryLogEntryCreator());
            }
            listeners.add(listener);
        }
        if (this.createSlf4jQueryListener) {
            SLF4JQueryLoggingListener listener = new SLF4JQueryLoggingListener();
            if (this.slf4jLogLevel != null) {
                listener.setLogLevel(this.slf4jLogLevel);
            }
            if (this.loggerName != null && !this.loggerName.isEmpty()) {
                listener.setLoggerName(this.loggerName);
            }
            if (this.jsonFormat) {
                listener.setQueryLogEntryCreator(new DefaultJsonQueryLogEntryCreator());
            }
            listeners.add(listener);
        }
        if (this.createJULQueryListener) {
            JULQueryLoggingListener listener = new JULQueryLoggingListener();
            if (this.julLogLevel != null) {
                listener.setLogLevel(this.julLogLevel);
            }
            if (this.loggerName != null && !this.loggerName.isEmpty()) {
                listener.setLoggerName(this.loggerName);
            }
            if (this.jsonFormat) {
                listener.setQueryLogEntryCreator(new DefaultJsonQueryLogEntryCreator());
            }
            listeners.add(listener);
        }
        if (this.createSysOutQueryListener) {
            SystemOutQueryLoggingListener listener = new SystemOutQueryLoggingListener();
            if (this.jsonFormat) {
                listener.setQueryLogEntryCreator(new DefaultJsonQueryLogEntryCreator());
            }
            listeners.add(listener);
        }

        // countQuery listener
        if (this.createDataSourceQueryCountListener) {
            listeners.add(new DataSourceQueryCountListener());
        }

        // explicitly added listeners
        listeners.addAll(this.queryExecutionListeners);

        for (QueryExecutionListener listener : listeners) {
            proxyDataSource.addListener(listener);
        }

        if (this.queryTransformer != null) {
            proxyDataSource.getInterceptorHolder().setQueryTransformer(this.queryTransformer);
        }
        if (this.parameterTransformer != null) {
            proxyDataSource.getInterceptorHolder().setParameterTransformer(this.parameterTransformer);
        }

        return proxyDataSource;
    }
}
