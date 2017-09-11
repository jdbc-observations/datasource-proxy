package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.ConnectionIdManager;
import net.ttddyy.dsproxy.listener.ChainListener;
import net.ttddyy.dsproxy.listener.CompositeMethodListener;
import net.ttddyy.dsproxy.listener.DataSourceQueryCountListener;
import net.ttddyy.dsproxy.listener.MethodExecutionListener;
import net.ttddyy.dsproxy.listener.QueryCountStrategy;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.listener.ThreadQueryCountHolder;
import net.ttddyy.dsproxy.listener.logging.AbstractQueryLoggingListener;
import net.ttddyy.dsproxy.listener.logging.AbstractSlowQueryLoggingListener;
import net.ttddyy.dsproxy.listener.logging.CommonsLogLevel;
import net.ttddyy.dsproxy.listener.logging.CommonsQueryLoggingListener;
import net.ttddyy.dsproxy.listener.logging.CommonsSlowQueryListener;
import net.ttddyy.dsproxy.listener.logging.DefaultQueryLogEntryCreator;
import net.ttddyy.dsproxy.listener.logging.JULQueryLoggingListener;
import net.ttddyy.dsproxy.listener.logging.JULSlowQueryListener;
import net.ttddyy.dsproxy.listener.logging.QueryLogEntryCreator;
import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.listener.logging.SLF4JQueryLoggingListener;
import net.ttddyy.dsproxy.listener.logging.SLF4JSlowQueryListener;
import net.ttddyy.dsproxy.listener.logging.SystemOutQueryLoggingListener;
import net.ttddyy.dsproxy.listener.logging.SystemOutSlowQueryListener;
import net.ttddyy.dsproxy.proxy.JdbcProxyFactory;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author Tadaya Tsuyukubo
 */
public class ProxyDataSourceBuilderTest {

    @Test
    public void buildCommonsQueryLoggingListener() {

        ProxyDataSource ds;
        CommonsQueryLoggingListener listener;

        ds = ProxyDataSourceBuilder.create().logQueryByCommons().build();
        listener = getAndVerifyListener(ds, CommonsQueryLoggingListener.class);
        assertThat(listener.getLogLevel()).as("default log level").isEqualTo(CommonsLogLevel.DEBUG);

        // with logLevel
        ds = ProxyDataSourceBuilder.create().logQueryByCommons(CommonsLogLevel.ERROR).build();
        listener = getAndVerifyListener(ds, CommonsQueryLoggingListener.class);
        assertThat(listener.getLogLevel()).as("log level").isEqualTo(CommonsLogLevel.ERROR);

        // with name and logLevel
        ds = ProxyDataSourceBuilder.create().logQueryByCommons(CommonsLogLevel.FATAL, "my.log").build();
        listener = getAndVerifyListener(ds, CommonsQueryLoggingListener.class);
        assertThat(listener.getLogLevel()).as("log level").isEqualTo(CommonsLogLevel.FATAL);
    }

    @Test
    public void buildSLF4JQueryLoggingListener() {

        ProxyDataSource ds;
        SLF4JQueryLoggingListener listener;
        org.slf4j.Logger logger;

        // default
        ds = ProxyDataSourceBuilder.create().logQueryBySlf4j().build();
        listener = getAndVerifyListener(ds, SLF4JQueryLoggingListener.class);
        logger = listener.getLogger();
        assertThat(logger.getName()).isEqualTo("net.ttddyy.dsproxy.listener.logging.SLF4JQueryLoggingListener");
        assertThat(listener.getLogLevel()).as("default log level").isEqualTo(SLF4JLogLevel.DEBUG);

        // with level
        ds = ProxyDataSourceBuilder.create().logQueryBySlf4j(SLF4JLogLevel.TRACE).build();
        listener = getAndVerifyListener(ds, SLF4JQueryLoggingListener.class);
        assertThat(listener.getLogLevel()).as("log level").isEqualTo(SLF4JLogLevel.TRACE);

        // with logger name
        ds = ProxyDataSourceBuilder.create().logQueryBySlf4j("my.logger").build();
        listener = getAndVerifyListener(ds, SLF4JQueryLoggingListener.class);
        logger = listener.getLogger();
        assertThat(logger.getName()).isEqualTo("my.logger");

        // with level and logger name
        ds = ProxyDataSourceBuilder.create().logQueryBySlf4j(SLF4JLogLevel.ERROR, "my.logger").build();
        listener = getAndVerifyListener(ds, SLF4JQueryLoggingListener.class);
        logger = listener.getLogger();
        assertThat(logger.getName()).isEqualTo("my.logger");
        assertThat(listener.getLogLevel()).as("log level").isEqualTo(SLF4JLogLevel.ERROR);
    }

    @Test
    public void buildJULQueryLoggingListener() {

        ProxyDataSource ds;
        JULQueryLoggingListener listener;
        java.util.logging.Logger logger;

        // default
        ds = ProxyDataSourceBuilder.create().logQueryByJUL().build();
        listener = getAndVerifyListener(ds, JULQueryLoggingListener.class);
        logger = listener.getLogger();
        assertThat(logger.getName()).isEqualTo("net.ttddyy.dsproxy.listener.logging.JULQueryLoggingListener");
        assertThat(listener.getLogLevel()).as("default log level").isEqualTo(Level.FINE);

        // with level
        ds = ProxyDataSourceBuilder.create().logQueryByJUL(Level.WARNING).build();
        listener = getAndVerifyListener(ds, JULQueryLoggingListener.class);
        assertThat(listener.getLogLevel()).as("log level").isEqualTo(Level.WARNING);

        // with logger name
        ds = ProxyDataSourceBuilder.create().logQueryByJUL("my.logger").build();
        listener = getAndVerifyListener(ds, JULQueryLoggingListener.class);
        logger = listener.getLogger();
        assertThat(logger.getName()).isEqualTo("my.logger");

        // with level and logger name
        ds = ProxyDataSourceBuilder.create().logQueryByJUL(Level.SEVERE, "my.logger").build();
        listener = getAndVerifyListener(ds, JULQueryLoggingListener.class);
        logger = listener.getLogger();
        assertThat(logger.getName()).isEqualTo("my.logger");
        assertThat(listener.getLogLevel()).as("log level").isEqualTo(Level.SEVERE);
    }

    @Test
    public void buildSysOutQueryLoggingListener() {
        ProxyDataSource ds = ProxyDataSourceBuilder.create().logQueryToSysOut().build();
        getAndVerifyListener(ds, SystemOutQueryLoggingListener.class);
    }


    @Test
    public void buildCommonsSlowQueryListener() {

        ProxyDataSource ds;
        CommonsSlowQueryListener listener;

        // default
        ds = ProxyDataSourceBuilder.create().logSlowQueryByCommons(10, TimeUnit.SECONDS).build();
        listener = getAndVerifyListener(ds, CommonsSlowQueryListener.class);
        assertThat(listener.getThreshold()).isEqualTo(10);
        assertThat(listener.getThresholdTimeUnit()).isEqualTo(TimeUnit.SECONDS);
        assertThat(listener.getLogLevel()).as("default log level").isEqualTo(CommonsLogLevel.WARN);

        // with logLevel
        ds = ProxyDataSourceBuilder.create().logSlowQueryByCommons(10, TimeUnit.SECONDS, CommonsLogLevel.INFO).build();
        listener = getAndVerifyListener(ds, CommonsSlowQueryListener.class);
        assertThat(listener.getLogLevel()).as("log level").isEqualTo(CommonsLogLevel.INFO);

        // with logLevel and logger name
        ds = ProxyDataSourceBuilder.create().logSlowQueryByCommons(10, TimeUnit.SECONDS, CommonsLogLevel.FATAL, "my.logger").build();
        listener = getAndVerifyListener(ds, CommonsSlowQueryListener.class);
        assertThat(listener.getLogLevel()).as("log level").isEqualTo(CommonsLogLevel.FATAL);

    }

    @Test
    public void buildSLF4JSlowQueryListener() {

        ProxyDataSource ds;
        SLF4JSlowQueryListener listener;
        org.slf4j.Logger logger;

        // default
        ds = ProxyDataSourceBuilder.create().logSlowQueryBySlf4j(10, TimeUnit.SECONDS).build();
        listener = getAndVerifyListener(ds, SLF4JSlowQueryListener.class);
        assertThat(listener.getThreshold()).isEqualTo(10);
        assertThat(listener.getThresholdTimeUnit()).isEqualTo(TimeUnit.SECONDS);
        logger = listener.getLogger();
        assertThat(logger.getName()).isEqualTo("net.ttddyy.dsproxy.listener.logging.SLF4JSlowQueryListener");
        assertThat(listener.getLogLevel()).as("default log level").isEqualTo(SLF4JLogLevel.WARN);

        // with logLevel
        ds = ProxyDataSourceBuilder.create().logSlowQueryBySlf4j(10, TimeUnit.SECONDS, SLF4JLogLevel.TRACE).build();
        listener = getAndVerifyListener(ds, SLF4JSlowQueryListener.class);
        assertThat(listener.getLogLevel()).as("log level").isEqualTo(SLF4JLogLevel.TRACE);

        // with logger name
        ds = ProxyDataSourceBuilder.create().logSlowQueryBySlf4j(10, TimeUnit.SECONDS, "my.logger").build();
        listener = getAndVerifyListener(ds, SLF4JSlowQueryListener.class);
        logger = listener.getLogger();
        assertThat(logger.getName()).isEqualTo("my.logger");

        // with logLevel and logger name
        ds = ProxyDataSourceBuilder.create().logSlowQueryBySlf4j(10, TimeUnit.SECONDS, SLF4JLogLevel.INFO, "my.logger").build();
        listener = getAndVerifyListener(ds, SLF4JSlowQueryListener.class);
        logger = listener.getLogger();
        assertThat(logger.getName()).isEqualTo("my.logger");
        assertThat(listener.getLogLevel()).as("log level").isEqualTo(SLF4JLogLevel.INFO);

    }

    @Test
    public void buildJULJSlowQueryListener() {

        ProxyDataSource ds;
        JULSlowQueryListener listener;
        java.util.logging.Logger logger;

        // default
        ds = ProxyDataSourceBuilder.create().logSlowQueryByJUL(10, TimeUnit.SECONDS).build();
        listener = getAndVerifyListener(ds, JULSlowQueryListener.class);
        assertThat(listener.getThreshold()).isEqualTo(10);
        assertThat(listener.getThresholdTimeUnit()).isEqualTo(TimeUnit.SECONDS);
        logger = listener.getLogger();
        assertThat(logger.getName()).isEqualTo("net.ttddyy.dsproxy.listener.logging.JULSlowQueryListener");
        assertThat(listener.getLogLevel()).as("default log level").isEqualTo(Level.WARNING);

        // with logLevel
        ds = ProxyDataSourceBuilder.create().logSlowQueryByJUL(10, TimeUnit.SECONDS, Level.FINE).build();
        listener = getAndVerifyListener(ds, JULSlowQueryListener.class);
        assertThat(listener.getLogLevel()).as("ølog level").isEqualTo(Level.FINE);

        // with logger name
        ds = ProxyDataSourceBuilder.create().logSlowQueryByJUL(10, TimeUnit.SECONDS, "my.logger").build();
        listener = getAndVerifyListener(ds, JULSlowQueryListener.class);
        logger = listener.getLogger();
        assertThat(logger.getName()).isEqualTo("my.logger");

        // with logLevel and logger name
        ds = ProxyDataSourceBuilder.create().logSlowQueryByJUL(10, TimeUnit.SECONDS, Level.INFO, "my.logger").build();
        listener = getAndVerifyListener(ds, JULSlowQueryListener.class);
        logger = listener.getLogger();
        assertThat(logger.getName()).isEqualTo("my.logger");
        assertThat(listener.getLogLevel()).as("log level").isEqualTo(Level.INFO);

    }

    @Test
    public void buildSysOutSlowQueryListener() {
        ProxyDataSource ds = ProxyDataSourceBuilder.create().logSlowQueryToSysOut(10, TimeUnit.SECONDS).build();
        getAndVerifyListener(ds, SystemOutSlowQueryListener.class);
    }

    @Test
    public void multiline() {
        ProxyDataSource ds;

        ds = ProxyDataSourceBuilder.create().multiline().logQueryByCommons().build();
        verifyMultiline(ds, CommonsQueryLoggingListener.class);

        ds = ProxyDataSourceBuilder.create().multiline().logQueryBySlf4j().build();
        verifyMultiline(ds, SLF4JQueryLoggingListener.class);

        ds = ProxyDataSourceBuilder.create().multiline().logQueryByJUL().build();
        verifyMultiline(ds, JULQueryLoggingListener.class);

        ds = ProxyDataSourceBuilder.create().multiline().logQueryToSysOut().build();
        verifyMultiline(ds, SystemOutQueryLoggingListener.class);


        long threshold = 10;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        ds = ProxyDataSourceBuilder.create().multiline().logSlowQueryByCommons(threshold, timeUnit).build();
        verifyMultiline(ds, CommonsSlowQueryListener.class);

        ds = ProxyDataSourceBuilder.create().multiline().logSlowQueryBySlf4j(threshold, timeUnit).build();
        verifyMultiline(ds, SLF4JSlowQueryListener.class);

        ds = ProxyDataSourceBuilder.create().multiline().logSlowQueryByJUL(threshold, timeUnit).build();
        verifyMultiline(ds, JULSlowQueryListener.class);

        ds = ProxyDataSourceBuilder.create().multiline().logSlowQueryToSysOut(threshold, timeUnit).build();
        verifyMultiline(ds, SystemOutSlowQueryListener.class);
    }

    private void verifyMultiline(ProxyDataSource ds, Class<? extends QueryExecutionListener> listenerClass) {
        QueryExecutionListener listener = getAndVerifyListener(ds, listenerClass);

        QueryLogEntryCreator entryCreator;
        if (listener instanceof AbstractQueryLoggingListener) {
            entryCreator = ((AbstractQueryLoggingListener) listener).getQueryLogEntryCreator();
        } else {
            entryCreator = ((AbstractSlowQueryLoggingListener) listener).getQueryLogEntryCreator();
        }
        assertThat(entryCreator).isInstanceOf(DefaultQueryLogEntryCreator.class);
        assertThat(((DefaultQueryLogEntryCreator) entryCreator).isMultiline()).as("multiline output").isTrue();
    }


    @SuppressWarnings("unchecked")
    private <T extends QueryExecutionListener> T getAndVerifyListener(ProxyDataSource ds, Class<T> listenerClass) {
        QueryExecutionListener listener = ds.getProxyConfig().getQueryListener();
        assertThat(listener).isInstanceOf(ChainListener.class);
        List<QueryExecutionListener> listeners = ((ChainListener) listener).getListeners();
        assertThat(listeners).hasSize(1);

        QueryExecutionListener target = listeners.get(0);
        assertThat(target).isInstanceOf(listenerClass);

        return (T) target;
    }

    @Test
    public void jdbcProxyFactory() {
        ProxyDataSource ds;

        ds = ProxyDataSourceBuilder.create().build();
        assertThat(ds.getProxyConfig().getJdbcProxyFactory()).as("Default one should be used").isSameAs(JdbcProxyFactory.DEFAULT);

        JdbcProxyFactory proxyFactory = mock(JdbcProxyFactory.class);
        ds = ProxyDataSourceBuilder.create().jdbcProxyFactory(proxyFactory).build();
        assertThat(ds.getProxyConfig().getJdbcProxyFactory()).isSameAs(proxyFactory);
    }

    @Test
    public void connectionIdManager() {
        ProxyDataSource ds;

        ds = ProxyDataSourceBuilder.create().build();
        assertThat(ds.getProxyConfig().getConnectionIdManager()).as("Default one should be used").isSameAs(ConnectionIdManager.DEFAULT);

        ConnectionIdManager connectionIdManager = mock(ConnectionIdManager.class);
        ds = ProxyDataSourceBuilder.create().connectionIdManager(connectionIdManager).build();
        assertThat(ds.getProxyConfig().getConnectionIdManager()).isSameAs(connectionIdManager);
    }

    @Test
    public void countListener() {

        ProxyDataSource ds;
        DataSourceQueryCountListener listener;

        // default strategy
        ds = ProxyDataSourceBuilder.create().countQuery().build();
        listener = getAndVerifyListener(ds, DataSourceQueryCountListener.class);
        assertThat(listener.getQueryCountStrategy()).as("default count listener")
                .isNotNull()
                .isInstanceOf(ThreadQueryCountHolder.class);

        // specify strategy
        QueryCountStrategy strategy = mock(QueryCountStrategy.class);

        ds = ProxyDataSourceBuilder.create().countQuery(strategy).build();
        listener = getAndVerifyListener(ds, DataSourceQueryCountListener.class);
        assertThat(listener.getQueryCountStrategy()).as("count listener with strategy")
                .isNotNull()
                .isSameAs(strategy);
    }

    @Test
    public void buildMethodListener() {
        ProxyDataSource ds;
        CompositeMethodListener methodListener;

        MethodExecutionListener listener1 = mock(MethodExecutionListener.class);
        MethodExecutionListener listener2 = mock(MethodExecutionListener.class);

        // single listener
        ds = ProxyDataSourceBuilder.create().methodListener(listener1).build();
        methodListener = ds.getProxyConfig().getMethodListener();
        assertThat(methodListener.getListeners()).hasSize(1).contains(listener1);

        // multiple listeners
        ds = ProxyDataSourceBuilder.create().methodListener(listener1).methodListener(listener2).build();
        methodListener = ds.getProxyConfig().getMethodListener();
        assertThat(methodListener.getListeners()).hasSize(2).contains(listener1, listener2);
    }
}
