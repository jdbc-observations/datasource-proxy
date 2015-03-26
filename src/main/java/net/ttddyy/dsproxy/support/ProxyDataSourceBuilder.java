package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.listener.*;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

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
    private SLF4JLogLevel slf4JLogLevel;
    private boolean createSysOutQueryListener;

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

    public ProxyDataSourceBuilder dataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public ProxyDataSourceBuilder logQueryByCommons() {
        this.createCommonsQueryListener = true;
        return this;
    }

    public ProxyDataSourceBuilder logQueryByCommons(CommonsLogLevel logLevel) {
        this.createCommonsQueryListener = true;
        this.commonsLogLevel = logLevel;
        return this;
    }

    public ProxyDataSourceBuilder logQueryBySlf4j() {
        this.createSlf4jQueryListener = true;
        return this;
    }

    public ProxyDataSourceBuilder logQueryBySlf4j(SLF4JLogLevel logLevel) {
        this.createSlf4jQueryListener = true;
        this.slf4JLogLevel = logLevel;
        return this;
    }

    public ProxyDataSourceBuilder logQueryToSysOut() {
        this.createSysOutQueryListener = true;
        return this;
    }

    public ProxyDataSourceBuilder name(String dataSourceName) {
        this.dataSourceName = dataSourceName;
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
            listeners.add(listener);
        }
        if (this.createSlf4jQueryListener) {
            SLF4JQueryLoggingListener listener = new SLF4JQueryLoggingListener();
            if (this.slf4JLogLevel != null) {
                listener.setLogLevel(this.slf4JLogLevel);
            }
            listeners.add(listener);
        }
        if (this.createSysOutQueryListener) {
            listeners.add(new SystemOutQueryLoggingListener());
        }

        if (!listeners.isEmpty()) {
            if (listeners.size() == 1) {
                proxyDataSource.setListener(listeners.get(0));
            } else {
                ChainListener chainListener = new ChainListener();
                chainListener.getListeners().addAll(listeners);
                proxyDataSource.setListener(chainListener);
            }
        }

        return proxyDataSource;
    }
}
