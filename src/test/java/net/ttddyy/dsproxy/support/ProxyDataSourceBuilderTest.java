package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.ConnectionIdManager;
import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.listener.CompositeProxyDataSourceListener;
import net.ttddyy.dsproxy.listener.DataSourceQueryCountListener;
import net.ttddyy.dsproxy.listener.ProxyDataSourceListener;
import net.ttddyy.dsproxy.listener.QueryCountStrategy;
import net.ttddyy.dsproxy.listener.SlowQueryListener;
import net.ttddyy.dsproxy.listener.ThreadQueryCountHolder;
import net.ttddyy.dsproxy.listener.TracingMethodListener;
import net.ttddyy.dsproxy.listener.logging.AbstractQueryLoggingListener;
import net.ttddyy.dsproxy.listener.logging.CommonsLogLevel;
import net.ttddyy.dsproxy.listener.logging.CommonsQueryLoggingListener;
import net.ttddyy.dsproxy.listener.logging.DefaultQueryLogEntryCreator;
import net.ttddyy.dsproxy.listener.logging.JULQueryLoggingListener;
import net.ttddyy.dsproxy.listener.logging.QueryLogEntryCreator;
import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.listener.logging.SLF4JQueryLoggingListener;
import net.ttddyy.dsproxy.listener.logging.SystemOutQueryLoggingListener;
import net.ttddyy.dsproxy.proxy.JdbcProxyFactory;
import net.ttddyy.dsproxy.proxy.RepeatableReadResultSetProxyLogicFactory;
import net.ttddyy.dsproxy.proxy.ResultSetProxyLogicFactory;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
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
    public void onSlowQuery() {

        ProxyDataSource ds;
        SlowQueryListener listener;

        Consumer<ExecutionInfo> consumer = executionInfo -> {
        };

        ds = ProxyDataSourceBuilder.create().onSlowQuery(10, TimeUnit.SECONDS, consumer).build();
        listener = getAndVerifyListener(ds, SlowQueryListener.class);
        assertThat(listener.getThreshold()).isEqualTo(10);
        assertThat(listener.getThresholdTimeUnit()).isEqualTo(TimeUnit.SECONDS);
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

    }

    private void verifyMultiline(ProxyDataSource ds, Class<? extends ProxyDataSourceListener> listenerClass) {
        ProxyDataSourceListener listener = getAndVerifyListener(ds, listenerClass);

        QueryLogEntryCreator entryCreator = ((AbstractQueryLoggingListener) listener).getQueryLogEntryCreator();
        assertThat(entryCreator).isInstanceOf(DefaultQueryLogEntryCreator.class);
        assertThat(((DefaultQueryLogEntryCreator) entryCreator).isMultiline()).as("multiline output").isTrue();
    }


    @SuppressWarnings("unchecked")
    private <T extends ProxyDataSourceListener> T getAndVerifyListener(ProxyDataSource ds, Class<T> listenerClass) {
        ProxyDataSourceListener listener = ds.getProxyConfig().getListeners();
        assertThat(listener).isInstanceOf(CompositeProxyDataSourceListener.class);
        List<ProxyDataSourceListener> listeners = ((CompositeProxyDataSourceListener) listener).getListeners();
        assertThat(listeners).hasSize(1);

        ProxyDataSourceListener target = listeners.get(0);
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
        Consumer<String> consumer = (str) -> {
        };

        ds = ProxyDataSourceBuilder.create().traceMethods(consumer).build();
        listener = getAndVerifyMethodListener(ds, TracingMethodListener.class);
        assertThat(listener.getTracingMessageConsumer()).as("tracing listener with message consumer")
                .isNotNull()
                .isSameAs(consumer);

        // with tracing condition
        BooleanSupplier condition = mock(BooleanSupplier.class);

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
    private <T extends ProxyDataSourceListener> T getAndVerifyMethodListener(ProxyDataSource ds, Class<T> listenerClass) {
        CompositeProxyDataSourceListener compositeListener = ds.getProxyConfig().getListeners();
        List<ProxyDataSourceListener> listeners = compositeListener.getListeners();
        assertThat(listeners).hasSize(1);

        ProxyDataSourceListener target = listeners.get(0);
        assertThat(target).isInstanceOf(listenerClass);

        return (T) target;
    }

    @Test
    public void buildMethodListener() {
        ProxyDataSource ds;
        CompositeProxyDataSourceListener methodListener;

        ProxyDataSourceListener listener1 = mock(ProxyDataSourceListener.class);
        ProxyDataSourceListener listener2 = mock(ProxyDataSourceListener.class);

        // single listener
        ds = ProxyDataSourceBuilder.create().methodListener(listener1).build();
        methodListener = ds.getProxyConfig().getListeners();
        assertThat(methodListener.getListeners()).hasSize(1).contains(listener1);

        // multiple listeners
        ds = ProxyDataSourceBuilder.create().methodListener(listener1).methodListener(listener2).build();
        methodListener = ds.getProxyConfig().getListeners();
        assertThat(methodListener.getListeners()).hasSize(2).contains(listener1, listener2);
    }

    @Test
    public void singleMethodExecutionWithBeforeMethodAndAfterMethod() {
        ProxyDataSource ds;
        CompositeProxyDataSourceListener compositeListener;
        ProxyDataSourceListener listener;

        // check beforeMethod()
        final AtomicBoolean isBeforeInvoked = new AtomicBoolean();
        ds = ProxyDataSourceBuilder.create()
                .beforeMethod(executionContext -> {
                    isBeforeInvoked.set(true);
                })
                .build();
        compositeListener = ds.getProxyConfig().getListeners();
        assertThat(compositeListener.getListeners()).hasSize(1);

        // invoke found listener and verify invocation
        listener = compositeListener.getListeners().get(0);
        listener.beforeMethod(null);  // invoke
        assertThat(isBeforeInvoked.get()).isTrue();

        // check afterMethod()
        final AtomicBoolean isAfterInvoked = new AtomicBoolean();
        ds = ProxyDataSourceBuilder.create()
                .afterMethod(executionContext -> {
                    isAfterInvoked.set(true);
                })
                .build();
        compositeListener = ds.getProxyConfig().getListeners();
        assertThat(compositeListener.getListeners()).hasSize(1);

        // invoke found listener and verify invocation
        listener = compositeListener.getListeners().get(0);
        listener.afterMethod(null);  // invoke
        assertThat(isAfterInvoked.get()).isTrue();

    }

    @Test
    public void singleQueryExecutionWithBeforeQueryAndAfterQuery() {
        ProxyDataSource ds;
        CompositeProxyDataSourceListener CompositeProxyDataSourceListener;
        ProxyDataSourceListener listener;

        // check beforeMethod()
        final AtomicBoolean isBeforeInvoked = new AtomicBoolean();
        ds = ProxyDataSourceBuilder.create()
                .beforeQuery((execInfo) -> {
                    isBeforeInvoked.set(true);
                })
                .build();
        CompositeProxyDataSourceListener = ds.getProxyConfig().getListeners();
        assertThat(CompositeProxyDataSourceListener.getListeners()).hasSize(1);

        // invoke found listener and verify invocation
        listener = CompositeProxyDataSourceListener.getListeners().get(0);
        listener.beforeQuery(null);  // invoke
        assertThat(isBeforeInvoked.get()).isTrue();

        // check afterMethod()
        final AtomicBoolean isAfterInvoked = new AtomicBoolean();
        ds = ProxyDataSourceBuilder.create()
                .afterQuery((execInfo) -> {
                    isAfterInvoked.set(true);
                })
                .build();
        CompositeProxyDataSourceListener = ds.getProxyConfig().getListeners();
        assertThat(CompositeProxyDataSourceListener.getListeners()).hasSize(1);

        // invoke found listener and verify invocation
        listener = CompositeProxyDataSourceListener.getListeners().get(0);
        listener.afterQuery(null);  // invoke
        assertThat(isAfterInvoked.get()).isTrue();

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
