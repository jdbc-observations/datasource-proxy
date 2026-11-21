package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.listener.QueryExecutionContext;
import net.ttddyy.dsproxy.listener.ProxyDataSourceListener;
import net.ttddyy.dsproxy.ConnectionIdManager;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.proxy.CompositeProxyDataSourceListener;
import net.ttddyy.dsproxy.proxy.MutableQueryExecutionContext;
import net.ttddyy.dsproxy.DataSourceQueryCounter;
import net.ttddyy.dsproxy.QueryCountListener;
import net.ttddyy.dsproxy.QueryType;
import net.ttddyy.dsproxy.listener.MethodExecutionContext;
import net.ttddyy.dsproxy.listener.QueryTypeDetector;
import net.ttddyy.dsproxy.listener.TracingMethodListener;
import net.ttddyy.dsproxy.listener.lifecycle.JdbcLifecycleEventExecutionListener;
import net.ttddyy.dsproxy.listener.lifecycle.JdbcLifecycleEventListener;
import net.ttddyy.dsproxy.listener.logging.AbstractQueryLoggingListener;
import net.ttddyy.dsproxy.listener.logging.AbstractSlowQueryLoggingListener;
import net.ttddyy.dsproxy.listener.logging.CommonsLogLevel;
import net.ttddyy.dsproxy.listener.logging.CommonsQueryLoggingListener;
import net.ttddyy.dsproxy.listener.logging.CommonsSlowQueryListener;
import net.ttddyy.dsproxy.listener.logging.DefaultQueryLogEntryCreator;
import net.ttddyy.dsproxy.listener.logging.InMemorySLF4JLogger;
import net.ttddyy.dsproxy.listener.logging.JULQueryLoggingListener;
import net.ttddyy.dsproxy.listener.logging.JULSlowQueryListener;
import net.ttddyy.dsproxy.listener.logging.Log4jLogLevel;
import net.ttddyy.dsproxy.listener.logging.Log4jSlowQueryListener;
import net.ttddyy.dsproxy.listener.logging.LoggingFilter;
import net.ttddyy.dsproxy.listener.logging.QueryLogEntryCreator;
import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.listener.logging.SLF4JQueryLoggingListener;
import net.ttddyy.dsproxy.listener.logging.SLF4JSlowQueryListener;
import net.ttddyy.dsproxy.listener.logging.SystemOutQueryLoggingListener;
import net.ttddyy.dsproxy.listener.logging.SystemOutSlowQueryListener;
import net.ttddyy.dsproxy.proxy.JdbcProxyFactory;
import net.ttddyy.dsproxy.proxy.ProxyConfig;
import net.ttddyy.dsproxy.proxy.ProxyJdbcObject;
import net.ttddyy.dsproxy.proxy.RepeatableReadResultSetProxyLogicFactory;
import net.ttddyy.dsproxy.proxy.ResultSetProxyLogicFactory;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

import static org.apache.logging.log4j.FormatterLoggerManualExample.logger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author Tadaya Tsuyukubo
 */
class ProxyDataSourceBuilderTest {

    @Test
    void buildCommonsQueryLoggingListener() {

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
    void buildSLF4JQueryLoggingListener() {

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
    void buildJULQueryLoggingListener() {

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
    void buildSysOutQueryLoggingListener() {
        ProxyDataSource ds = ProxyDataSourceBuilder.create().logQueryToSysOut().build();
        getAndVerifyListener(ds, SystemOutQueryLoggingListener.class);
    }


    @Test
    void buildCommonsSlowQueryListener() {

        ProxyDataSource ds;
        CommonsSlowQueryListener listener;

        // default
        ds = ProxyDataSourceBuilder.create().logSlowQueryByCommons(10, TimeUnit.SECONDS).build();
        listener = getAndVerifyListener(ds, CommonsSlowQueryListener.class);
        assertThat(listener.getThreshold()).isEqualTo(Duration.ofSeconds(10));
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
    void buildSLF4JSlowQueryListener() {

        ProxyDataSource ds;
        SLF4JSlowQueryListener listener;
        org.slf4j.Logger logger;

        // default
        ds = ProxyDataSourceBuilder.create().logSlowQueryBySlf4j(10, TimeUnit.SECONDS).build();
        listener = getAndVerifyListener(ds, SLF4JSlowQueryListener.class);
        assertThat(listener.getThreshold()).isEqualTo(Duration.ofSeconds(10));
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
    void buildJULJSlowQueryListener() {

        ProxyDataSource ds;
        JULSlowQueryListener listener;
        java.util.logging.Logger logger;

        // default
        ds = ProxyDataSourceBuilder.create().logSlowQueryByJUL(10, TimeUnit.SECONDS).build();
        listener = getAndVerifyListener(ds, JULSlowQueryListener.class);
        assertThat(listener.getThreshold()).isEqualTo(Duration.ofSeconds(10));
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
    void buildLog4jSlowQueryListener() {

        ProxyDataSource ds;
        Log4jSlowQueryListener listener;
        org.apache.logging.log4j.Logger logger;

        // default
        ds = ProxyDataSourceBuilder.create().logSlowQueryByLog4j(10, TimeUnit.SECONDS).build();
        listener = getAndVerifyListener(ds, Log4jSlowQueryListener.class);
        assertThat(listener.getThreshold()).isEqualTo(Duration.ofSeconds(10));
        logger = listener.getLogger();
        assertThat(logger.getName()).isEqualTo("net.ttddyy.dsproxy.listener.logging.Log4jSlowQueryListener");
        assertThat(listener.getLogLevel()).as("default log level").isEqualTo(Log4jLogLevel.WARN);

        // with logLevel
        ds = ProxyDataSourceBuilder.create().logSlowQueryByLog4j(10, TimeUnit.SECONDS, Log4jLogLevel.TRACE).build();
        listener = getAndVerifyListener(ds, Log4jSlowQueryListener.class);
        assertThat(listener.getLogLevel()).as("log level").isEqualTo(Log4jLogLevel.TRACE);

        // with logger name
        ds = ProxyDataSourceBuilder.create().logSlowQueryByLog4j(10, TimeUnit.SECONDS, "my.logger").build();
        listener = getAndVerifyListener(ds, Log4jSlowQueryListener.class);
        logger = listener.getLogger();
        assertThat(logger.getName()).isEqualTo("my.logger");

        // with logLevel and logger name
        ds = ProxyDataSourceBuilder.create().logSlowQueryByLog4j(10, TimeUnit.SECONDS, Log4jLogLevel.INFO, "my.logger").build();
        listener = getAndVerifyListener(ds, Log4jSlowQueryListener.class);
        logger = listener.getLogger();
        assertThat(logger.getName()).isEqualTo("my.logger");
        assertThat(listener.getLogLevel()).as("log level").isEqualTo(Log4jLogLevel.INFO);

    }

    @Test
    void buildSysOutSlowQueryListener() {
        ProxyDataSource ds = ProxyDataSourceBuilder.create().logSlowQueryToSysOut(10, TimeUnit.SECONDS).build();
        getAndVerifyListener(ds, SystemOutSlowQueryListener.class);
    }

    @Test
    void multiline() {
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

    private void verifyMultiline(ProxyDataSource ds, Class<? extends ProxyDataSourceListener> listenerClass) {
        ProxyDataSourceListener listener = getAndVerifyListener(ds, listenerClass);

        QueryLogEntryCreator entryCreator;
        if (listener instanceof AbstractQueryLoggingListener) {
            entryCreator = ((AbstractQueryLoggingListener) listener).getQueryLogEntryCreator();
        } else {
            entryCreator = ((AbstractSlowQueryLoggingListener) listener).getQueryLogEntryCreator();
        }
        assertThat(entryCreator).isInstanceOf(DefaultQueryLogEntryCreator.class);
        assertThat(((DefaultQueryLogEntryCreator) entryCreator).isMultiline()).as("multiline output").isTrue();
    }


    private static List<ProxyDataSourceListener> listeners(ProxyConfig proxyConfig) {
        return ((CompositeProxyDataSourceListener) proxyConfig.getListener()).getListeners();
    }

    @SuppressWarnings("unchecked")
    private <T extends ProxyDataSourceListener> T getAndVerifyListener(ProxyDataSource ds, Class<T> listenerClass) {
        List<ProxyDataSourceListener> listeners = listeners(ds.getProxyConfig());
        assertThat(listeners).hasSize(1);

        ProxyDataSourceListener target = listeners.get(0);
        assertThat(target).isInstanceOf(listenerClass);

        return (T) target;
    }

    @Test
    void formatQuery() {
        AtomicBoolean formatCalled = new AtomicBoolean();
        ProxyDataSourceBuilder.FormatQueryCallback formatCallback = query -> {
            formatCalled.set(true);
            return query + "-formatted";
        };

        ProxyDataSource ds = ProxyDataSourceBuilder.create().logQueryToSysOut().formatQuery(formatCallback).build();
        AbstractQueryLoggingListener listener = getAndVerifyListener(ds, AbstractQueryLoggingListener.class);
        QueryLogEntryCreator logEntryCreator = listener.getQueryLogEntryCreator();
        assertThat(logEntryCreator).isInstanceOf(DefaultQueryLogEntryCreator.class);
        DefaultQueryLogEntryCreator defaultQueryLogEntryCreator = (DefaultQueryLogEntryCreator) logEntryCreator;
        assertThat(defaultQueryLogEntryCreator.isMultiline()).isFalse();

        MutableQueryExecutionContext executionContext = new MutableQueryExecutionContext();
        QueryInfo queryInfo = new QueryInfo();
        queryInfo.setQuery("SELECT 1");
        executionContext.setQueries(List.of(queryInfo));
        String result = logEntryCreator.getLogEntry(executionContext, false, false, false);

        assertThat(formatCalled).isTrue();
        assertThat(result).contains("SELECT 1-formatted");
    }

    @Test
    void jdbcProxyFactory() {
        ProxyDataSource ds;

        ds = ProxyDataSourceBuilder.create().build();
        assertThat(ds.getProxyConfig().getJdbcProxyFactory()).as("Default one should be used").isSameAs(JdbcProxyFactory.DEFAULT);

        JdbcProxyFactory proxyFactory = mock(JdbcProxyFactory.class);
        ds = ProxyDataSourceBuilder.create().jdbcProxyFactory(proxyFactory).build();
        assertThat(ds.getProxyConfig().getJdbcProxyFactory()).isSameAs(proxyFactory);
    }

    @Test
    void connectionIdManager() {
        ProxyDataSource ds1 = ProxyDataSourceBuilder.create().build();
        ProxyDataSource ds2 = ProxyDataSourceBuilder.create().build();
        assertThat(ds1.getConnectionIdManager()).as("new instance should be created").isNotSameAs(ds2.getConnectionIdManager());

        ConnectionIdManager connectionIdManager = mock(ConnectionIdManager.class);
        ProxyDataSource ds = ProxyDataSourceBuilder.create().connectionIdManager(connectionIdManager).build();
        assertThat(ds.getProxyConfig().getConnectionIdManager()).isSameAs(connectionIdManager);
    }

    @Test
    void countListener() {
        QueryCountListener listener = getAndVerifyListener(ProxyDataSourceBuilder.create().countQuery().build(), QueryCountListener.class);
        assertThat(listener.getCounter()).isNull();
        assertThat(listener.getQueryTypeDetector().detect("select 1")).isEqualTo(QueryType.SELECT);

        DataSourceQueryCounter counter = new DataSourceQueryCounter();
        QueryTypeDetector detector = q -> QueryType.OTHER;
        listener = getAndVerifyListener(ProxyDataSourceBuilder.create().countQuery(counter).queryTypeDetector(detector).build(), QueryCountListener.class);
        assertThat(listener.getCounter()).isSameAs(counter);
        assertThat(listener.getQueryTypeDetector()).isSameAs(detector);
    }

    @Test
    void tracingListener() {

        ProxyDataSource ds;
        TracingMethodListener listener;

        // default strategy
        ds = ProxyDataSourceBuilder.create().traceMethods().build();
        listener = getAndVerifyListener(ds, TracingMethodListener.class);

        assertThat(listener).as("default tracing listener").isNotNull();

        // with message consumer
        TracingMethodListener.TracingMessageConsumer consumer = mock(TracingMethodListener.TracingMessageConsumer.class);

        ds = ProxyDataSourceBuilder.create().traceMethods(consumer).build();
        listener = getAndVerifyListener(ds, TracingMethodListener.class);
        assertThat(listener.getTracingMessageConsumer()).as("tracing listener with message consumer")
                .isNotNull()
                .isSameAs(consumer);

        // with tracing condition
        TracingMethodListener.TracingCondition condition = mock(TracingMethodListener.TracingCondition.class);

        ds = ProxyDataSourceBuilder.create().traceMethodsWhen(condition).build();
        listener = getAndVerifyListener(ds, TracingMethodListener.class);
        assertThat(listener.getTracingCondition()).as("tracing listener with tracing condition")
                .isNotNull()
                .isSameAs(condition);

        // with message consumer and tracing condition
        ds = ProxyDataSourceBuilder.create().traceMethodsWhen(condition, consumer).build();
        listener = getAndVerifyListener(ds, TracingMethodListener.class);
        assertThat(listener.getTracingMessageConsumer()).as("tracing listener with tracing condition and message consumer")
                .isNotNull()
                .isSameAs(consumer);
        assertThat(listener.getTracingCondition()).as("tracing listener with tracing condition and message consumer")
                .isNotNull()
                .isSameAs(condition);

    }

    @Test
    void buildListener() {
        ProxyDataSource ds;

        ProxyDataSourceListener listener1 = mock(ProxyDataSourceListener.class);
        ProxyDataSourceListener listener2 = mock(ProxyDataSourceListener.class);

        // single listener
        ds = ProxyDataSourceBuilder.create().listener(listener1).build();
        assertThat(listeners(ds.getProxyConfig())).containsExactly(listener1);

        // multiple listeners, in registration order
        ds = ProxyDataSourceBuilder.create().listener(listener1).listener(listener2).build();
        assertThat(listeners(ds.getProxyConfig())).containsExactly(listener1, listener2);
    }

    @Test
    void consumerShortcutsForMethod() {
        final AtomicBoolean before = new AtomicBoolean();
        final AtomicBoolean after = new AtomicBoolean();
        ProxyDataSource ds = ProxyDataSourceBuilder.create()
                .beforeMethod(ctx -> before.set(true))
                .afterMethod(ctx -> after.set(true))
                .build();
        ProxyDataSourceListener listener = ds.getProxyConfig().getListener();
        listener.beforeMethod(null);
        assertThat(before).isTrue();
        assertThat(after).isFalse();
        listener.afterMethod(null);
        assertThat(after).isTrue();
    }

    @Test
    void consumerShortcutsForQuery() {
        final AtomicBoolean before = new AtomicBoolean();
        final AtomicBoolean after = new AtomicBoolean();
        ProxyDataSource ds = ProxyDataSourceBuilder.create()
                .beforeQuery(ctx -> before.set(true))
                .afterQuery(ctx -> after.set(true))
                .build();
        ProxyDataSourceListener listener = ds.getProxyConfig().getListener();
        listener.beforeQuery(null);
        assertThat(before).isTrue();
        assertThat(after).isFalse();
        listener.afterQuery(null);
        assertThat(after).isTrue();
    }

    @Test
    void buildJdbcLifecycleEventListener() {
        ProxyDataSource ds;

        JdbcLifecycleEventListener listener = mock(JdbcLifecycleEventListener.class);

        ds = ProxyDataSourceBuilder.create().listener(JdbcLifecycleEventListener.asListener(listener)).build();
        JdbcLifecycleEventExecutionListener executionListener = getAndVerifyListener(ds, JdbcLifecycleEventExecutionListener.class);

        JdbcLifecycleEventListener eventListener = executionListener.getDelegate();
        assertThat(eventListener).isSameAs(listener);
    }


    @Test
    void proxyResultSet() {
        ProxyDataSource ds;
        ResultSetProxyLogicFactory factory = mock(ResultSetProxyLogicFactory.class);

        // default
        ds = ProxyDataSourceBuilder.create().build();
        assertThat(ds.getProxyConfig().isResultSetProxyEnabled()).isFalse();

        // with default proxy logic factory
        ds = ProxyDataSourceBuilder.create().proxyResultSet().build();
        assertThat(ds.getProxyConfig().isResultSetProxyEnabled()).isTrue();
        assertThat(ds.getProxyConfig().getResultSetProxyLogicFactory()).isSameAs(ResultSetProxyLogicFactory.DEFAULT);

        // with proxy factory
        ds = ProxyDataSourceBuilder.create().proxyResultSet(factory).build();
        assertThat(ds.getProxyConfig().isResultSetProxyEnabled()).isTrue();
        assertThat(ds.getProxyConfig().getResultSetProxyLogicFactory()).isSameAs(factory);

        // with repeatable read proxy factory
        ds = ProxyDataSourceBuilder.create().repeatableReadResultSet().build();
        assertThat(ds.getProxyConfig().isResultSetProxyEnabled()).isTrue();
        assertThat(ds.getProxyConfig().getResultSetProxyLogicFactory()).isInstanceOf(RepeatableReadResultSetProxyLogicFactory.class);

    }

    @Test
    void proxyGeneratedKeys() {
        ProxyDataSource ds;
        ResultSetProxyLogicFactory factory = mock(ResultSetProxyLogicFactory.class);

        // default
        ds = ProxyDataSourceBuilder.create().build();
        assertThat(ds.getProxyConfig().isAutoRetrieveGeneratedKeys()).isFalse();
        assertThat(ds.getProxyConfig().isGeneratedKeysProxyEnabled()).isFalse();

        // with default proxy logic factory
        ds = ProxyDataSourceBuilder.create().proxyGeneratedKeys().build();
        assertThat(ds.getProxyConfig().isGeneratedKeysProxyEnabled()).isTrue();
        assertThat(ds.getProxyConfig().isAutoRetrieveGeneratedKeys()).isFalse();
        assertThat(ds.getProxyConfig().getGeneratedKeysProxyLogicFactory()).isSameAs(ResultSetProxyLogicFactory.DEFAULT);

        // with proxy factory
        ds = ProxyDataSourceBuilder.create().proxyGeneratedKeys(factory).build();
        assertThat(ds.getProxyConfig().isGeneratedKeysProxyEnabled()).isTrue();
        assertThat(ds.getProxyConfig().isAutoRetrieveGeneratedKeys()).isFalse();
        assertThat(ds.getProxyConfig().getGeneratedKeysProxyLogicFactory()).isSameAs(factory);

        // with repeatable read proxy factory
        ds = ProxyDataSourceBuilder.create().repeatableReadGeneratedKeys().build();
        assertThat(ds.getProxyConfig().isGeneratedKeysProxyEnabled()).isTrue();
        assertThat(ds.getProxyConfig().isAutoRetrieveGeneratedKeys()).isFalse();
        assertThat(ds.getProxyConfig().getGeneratedKeysProxyLogicFactory()).isInstanceOf(RepeatableReadResultSetProxyLogicFactory.class);

    }

    @Test
    void autoRetrievalGeneratedKeys() {
        ProxyDataSource ds;
        ResultSetProxyLogicFactory factory = mock(ResultSetProxyLogicFactory.class);

        // default
        ds = ProxyDataSourceBuilder.create().build();
        assertThat(ds.getProxyConfig().isAutoRetrieveGeneratedKeys()).isFalse();
        assertThat(ds.getProxyConfig().isAutoCloseGeneratedKeys()).isFalse();
        assertThat(ds.getProxyConfig().isGeneratedKeysProxyEnabled()).isFalse();

        // set true
        ds = ProxyDataSourceBuilder.create().autoRetrieveGeneratedKeys(true).build();
        assertThat(ds.getProxyConfig().isAutoRetrieveGeneratedKeys()).isTrue();
        assertThat(ds.getProxyConfig().isAutoCloseGeneratedKeys()).isTrue();
        assertThat(ds.getProxyConfig().isGeneratedKeysProxyEnabled()).isFalse();

        // set false
        ds = ProxyDataSourceBuilder.create().autoRetrieveGeneratedKeys(false).build();
        assertThat(ds.getProxyConfig().isAutoRetrieveGeneratedKeys()).isTrue();
        assertThat(ds.getProxyConfig().isAutoCloseGeneratedKeys()).isFalse();
        assertThat(ds.getProxyConfig().isGeneratedKeysProxyEnabled()).isFalse();


        // with proxy factory
        ds = ProxyDataSourceBuilder.create().autoRetrieveGeneratedKeys(true, factory).build();
        assertThat(ds.getProxyConfig().isAutoRetrieveGeneratedKeys()).isTrue();
        assertThat(ds.getProxyConfig().isAutoCloseGeneratedKeys()).isTrue();
        assertThat(ds.getProxyConfig().isGeneratedKeysProxyEnabled()).isTrue();
        assertThat(ds.getProxyConfig().getGeneratedKeysProxyLogicFactory()).isSameAs(factory);

        ds = ProxyDataSourceBuilder.create().autoRetrieveGeneratedKeys(false, factory).build();
        assertThat(ds.getProxyConfig().isAutoRetrieveGeneratedKeys()).isTrue();
        assertThat(ds.getProxyConfig().isAutoCloseGeneratedKeys()).isFalse();
        assertThat(ds.getProxyConfig().isGeneratedKeysProxyEnabled()).isTrue();
        assertThat(ds.getProxyConfig().getGeneratedKeysProxyLogicFactory()).isSameAs(factory);

        // with repeatable read proxy factory
        ds = ProxyDataSourceBuilder.create().autoRetrieveGeneratedKeysWithRepeatableReadProxy(true).build();
        assertThat(ds.getProxyConfig().isAutoRetrieveGeneratedKeys()).isTrue();
        assertThat(ds.getProxyConfig().isAutoCloseGeneratedKeys()).isTrue();
        assertThat(ds.getProxyConfig().isGeneratedKeysProxyEnabled()).isTrue();
        assertThat(ds.getProxyConfig().getGeneratedKeysProxyLogicFactory()).isInstanceOf(RepeatableReadResultSetProxyLogicFactory.class);

        ds = ProxyDataSourceBuilder.create().autoRetrieveGeneratedKeysWithRepeatableReadProxy(false).build();
        assertThat(ds.getProxyConfig().isAutoRetrieveGeneratedKeys()).isTrue();
        assertThat(ds.getProxyConfig().isAutoCloseGeneratedKeys()).isFalse();
        assertThat(ds.getProxyConfig().isGeneratedKeysProxyEnabled()).isTrue();
        assertThat(ds.getProxyConfig().getGeneratedKeysProxyLogicFactory()).isInstanceOf(RepeatableReadResultSetProxyLogicFactory.class);

    }

    @Test
    void autoRetrievalGeneratedKeysForBatch() {
        ProxyDataSource ds;

        // default
        ds = ProxyDataSourceBuilder.create().build();
        assertThat(ds.getProxyConfig().isRetrieveGeneratedKeysForBatchStatement()).isFalse();
        assertThat(ds.getProxyConfig().isRetrieveGeneratedKeysForBatchPreparedOrCallable()).isTrue();

        // set true
        ds = ProxyDataSourceBuilder.create()
                .retrieveGeneratedKeysForBatch(true, true)
                .build();
        assertThat(ds.getProxyConfig().isRetrieveGeneratedKeysForBatchStatement()).isTrue();
        assertThat(ds.getProxyConfig().isRetrieveGeneratedKeysForBatchPreparedOrCallable()).isTrue();


        // set false
        ds = ProxyDataSourceBuilder.create()
                .retrieveGeneratedKeysForBatch(false, false)
                .build();
        assertThat(ds.getProxyConfig().isRetrieveGeneratedKeysForBatchStatement()).isFalse();
        assertThat(ds.getProxyConfig().isRetrieveGeneratedKeysForBatchPreparedOrCallable()).isFalse();

    }

    @Test
    void buildProxy() {
        DataSource original = mock(DataSource.class);
        DataSource proxy = ProxyDataSourceBuilder.create(original).buildProxy();
        assertThat(proxy).isInstanceOf(ProxyJdbcObject.class);
        Object target = ((ProxyJdbcObject) proxy).getTarget();
        assertThat(target).isInstanceOf(DataSource.class).isSameAs(original);
    }

    @Test
    void isolationRetrieval() {
        DataSource proxy = ProxyDataSourceBuilder.create().buildProxy();
        assertThat(proxy).isInstanceOf(ProxyJdbcObject.class);
        ProxyConfig proxyConfig = ((ProxyJdbcObject) proxy).getProxyConfig();
        assertThat(proxyConfig.isRetrieveIsolationLevel()).as("Default isolation retrieval").isFalse();

        proxy = ProxyDataSourceBuilder.create().writeIsolation().buildProxy();
        proxyConfig = ((ProxyJdbcObject) proxy).getProxyConfig();
        assertThat(proxyConfig.isRetrieveIsolationLevel()).as("writeIsolation() does not enable retrieval").isFalse();

        proxy = ProxyDataSourceBuilder.create().retrieveIsolation().buildProxy();
        proxyConfig = ((ProxyJdbcObject) proxy).getProxyConfig();
        assertThat(proxyConfig.isRetrieveIsolationLevel()).isTrue();
    }

    @Test
    void loggingFilter() {
        LoggingFilter filter = new LoggingFilter() {
            public boolean shouldLog(QueryExecutionContext execInfo) {
                return false;
            }
        };

        ProxyDataSource ds = ProxyDataSourceBuilder.create().logQueryByCommons().loggingFilter(filter).build();
        verifyLoggingFilter(ds, CommonsQueryLoggingListener.class, filter);

        ds = ProxyDataSourceBuilder.create().logQueryBySlf4j().loggingFilter(filter).build();
        verifyLoggingFilter(ds, SLF4JQueryLoggingListener.class, filter);

        ds = ProxyDataSourceBuilder.create().logQueryByJUL().loggingFilter(filter).build();
        verifyLoggingFilter(ds, JULQueryLoggingListener.class, filter);

        ds = ProxyDataSourceBuilder.create().logQueryToSysOut().loggingFilter(filter).build();
        verifyLoggingFilter(ds, SystemOutQueryLoggingListener.class, filter);
    }

    private void verifyLoggingFilter(ProxyDataSource ds, Class<? extends AbstractQueryLoggingListener> listenerClass, LoggingFilter expectedFilter) {
        AbstractQueryLoggingListener listener = getAndVerifyListener(ds, listenerClass);
        assertThat(listener.getLoggingFilter()).as("logging filter").isSameAs(expectedFilter);
    }
}
