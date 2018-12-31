package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.ConnectionIdManager;
import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.ChainListener;
import net.ttddyy.dsproxy.listener.CompositeMethodListener;
import net.ttddyy.dsproxy.listener.DataSourceQueryCountListener;
import net.ttddyy.dsproxy.listener.MethodExecutionContext;
import net.ttddyy.dsproxy.listener.MethodExecutionListener;
import net.ttddyy.dsproxy.listener.QueryCountStrategy;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.listener.ThreadQueryCountHolder;
import net.ttddyy.dsproxy.listener.TracingMethodListener;
import net.ttddyy.dsproxy.listener.lifecycle.JdbcLifecycleEventExecutionListener;
import net.ttddyy.dsproxy.listener.lifecycle.JdbcLifecycleEventListener;
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
import net.ttddyy.dsproxy.proxy.RepeatableReadResultSetProxyLogicFactory;
import net.ttddyy.dsproxy.proxy.ResultSetProxyLogicFactory;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
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
        ProxyDataSource ds1 = ProxyDataSourceBuilder.create().build();
        ProxyDataSource ds2 = ProxyDataSourceBuilder.create().build();
        assertThat(ds1.getConnectionIdManager()).as("new instance should be created").isNotSameAs(ds2.getConnectionIdManager());

        ConnectionIdManager connectionIdManager = mock(ConnectionIdManager.class);
        ProxyDataSource ds = ProxyDataSourceBuilder.create().connectionIdManager(connectionIdManager).build();
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
    public void tracingListener() {

        ProxyDataSource ds;
        TracingMethodListener listener;

        // default strategy
        ds = ProxyDataSourceBuilder.create().traceMethods().build();
        listener = getAndVerifyMethodListener(ds, TracingMethodListener.class);

        assertThat(listener).as("default tracing listener").isNotNull();

        // with message consumer
        TracingMethodListener.TracingMessageConsumer consumer = mock(TracingMethodListener.TracingMessageConsumer.class);

        ds = ProxyDataSourceBuilder.create().traceMethods(consumer).build();
        listener = getAndVerifyMethodListener(ds, TracingMethodListener.class);
        assertThat(listener.getTracingMessageConsumer()).as("tracing listener with message consumer")
                .isNotNull()
                .isSameAs(consumer);

        // with tracing condition
        TracingMethodListener.TracingCondition condition = mock(TracingMethodListener.TracingCondition.class);

        ds = ProxyDataSourceBuilder.create().traceMethodsWhen(condition).build();
        listener = getAndVerifyMethodListener(ds, TracingMethodListener.class);
        assertThat(listener.getTracingCondition()).as("tracing listener with tracing condition")
                .isNotNull()
                .isSameAs(condition);

        // with message consumer and tracing condition
        ds = ProxyDataSourceBuilder.create().traceMethodsWhen(condition, consumer).build();
        listener = getAndVerifyMethodListener(ds, TracingMethodListener.class);
        assertThat(listener.getTracingMessageConsumer()).as("tracing listener with tracing condition and message consumer")
                .isNotNull()
                .isSameAs(consumer);
        assertThat(listener.getTracingCondition()).as("tracing listener with tracing condition and message consumer")
                .isNotNull()
                .isSameAs(condition);

    }

    @SuppressWarnings("unchecked")
    private <T extends MethodExecutionListener> T getAndVerifyMethodListener(ProxyDataSource ds, Class<T> listenerClass) {
        CompositeMethodListener compositeListener = ds.getProxyConfig().getMethodListener();
        List<MethodExecutionListener> listeners = compositeListener.getListeners();
        assertThat(listeners).hasSize(1);

        MethodExecutionListener target = listeners.get(0);
        assertThat(target).isInstanceOf(listenerClass);

        return (T) target;
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

    @Test
    public void singleMethodExecutionWithBeforeMethodAndAfterMethod() {
        ProxyDataSource ds;
        CompositeMethodListener compositeListener;
        MethodExecutionListener listener;

        // check beforeMethod()
        final AtomicBoolean isBeforeInvoked = new AtomicBoolean();
        ds = ProxyDataSourceBuilder.create()
                .beforeMethod(new ProxyDataSourceBuilder.SingleMethodExecution() {
                    @Override
                    public void execute(MethodExecutionContext executionContext) {
                        isBeforeInvoked.set(true);
                    }
                })
                .build();
        compositeListener = ds.getProxyConfig().getMethodListener();
        assertThat(compositeListener.getListeners()).hasSize(1);

        // invoke found listener and verify invocation
        listener = compositeListener.getListeners().get(0);
        listener.beforeMethod(null);  // invoke
        assertThat(isBeforeInvoked.get()).isTrue();

        // check afterMethod()
        final AtomicBoolean isAfterInvoked = new AtomicBoolean();
        ds = ProxyDataSourceBuilder.create()
                .afterMethod(new ProxyDataSourceBuilder.SingleMethodExecution() {
                    @Override
                    public void execute(MethodExecutionContext executionContext) {
                        isAfterInvoked.set(true);
                    }
                })
                .build();
        compositeListener = ds.getProxyConfig().getMethodListener();
        assertThat(compositeListener.getListeners()).hasSize(1);

        // invoke found listener and verify invocation
        listener = compositeListener.getListeners().get(0);
        listener.afterMethod(null);  // invoke
        assertThat(isAfterInvoked.get()).isTrue();

    }

    @Test
    public void singleQueryExecutionWithBeforeQueryAndAfterQuery() {
        ProxyDataSource ds;
        ChainListener chainListener;
        QueryExecutionListener listener;

        // check beforeMethod()
        final AtomicBoolean isBeforeInvoked = new AtomicBoolean();
        ds = ProxyDataSourceBuilder.create()
                .beforeQuery(new ProxyDataSourceBuilder.SingleQueryExecution() {
                    @Override
                    public void execute(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
                        isBeforeInvoked.set(true);
                    }
                })
                .build();
        chainListener = ds.getProxyConfig().getQueryListener();
        assertThat(chainListener.getListeners()).hasSize(1);

        // invoke found listener and verify invocation
        listener = chainListener.getListeners().get(0);
        listener.beforeQuery(null, null);  // invoke
        assertThat(isBeforeInvoked.get()).isTrue();

        // check afterMethod()
        final AtomicBoolean isAfterInvoked = new AtomicBoolean();
        ds = ProxyDataSourceBuilder.create()
                .afterQuery(new ProxyDataSourceBuilder.SingleQueryExecution() {
                    @Override
                    public void execute(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
                        isAfterInvoked.set(true);
                    }
                })
                .build();
        chainListener = ds.getProxyConfig().getQueryListener();
        assertThat(chainListener.getListeners()).hasSize(1);

        // invoke found listener and verify invocation
        listener = chainListener.getListeners().get(0);
        listener.afterQuery(null, null);  // invoke
        assertThat(isAfterInvoked.get()).isTrue();

    }

    @Test
    public void buildJdbcLifecycleEventListener() {
        ProxyDataSource ds;

        JdbcLifecycleEventListener listener = mock(JdbcLifecycleEventListener.class);

        ds = ProxyDataSourceBuilder.create().listener(listener).build();
        CompositeMethodListener compositeMethodListener = ds.getProxyConfig().getMethodListener();
        ChainListener chainListener = ds.getProxyConfig().getQueryListener();

        assertThat(compositeMethodListener.getListeners()).hasSize(1);
        assertThat(chainListener.getListeners()).hasSize(1);

        MethodExecutionListener methodListener = compositeMethodListener.getListeners().get(0);
        QueryExecutionListener queryListener = chainListener.getListeners().get(0);

        assertThat(methodListener).isInstanceOf(JdbcLifecycleEventExecutionListener.class);
        assertThat(queryListener).isInstanceOf(JdbcLifecycleEventExecutionListener.class);
        assertThat(methodListener).isSameAs(queryListener);

        JdbcLifecycleEventListener eventListener = ((JdbcLifecycleEventExecutionListener) methodListener).getDelegate();
        assertThat(eventListener).isSameAs(listener);
    }


    @Test
    public void proxyResultSet() {
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
    public void proxyGeneratedKeys() {
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
    public void autoRetrievalGeneratedKeys() {
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
    public void autoRetrievalGeneratedKeysForBatch() {
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
}
