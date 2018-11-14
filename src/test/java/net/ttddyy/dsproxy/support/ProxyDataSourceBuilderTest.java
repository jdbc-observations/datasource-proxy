package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.ConnectionIdManager;
import net.ttddyy.dsproxy.DataSourceProxyException;
import net.ttddyy.dsproxy.listener.QueryExecutionContext;
import net.ttddyy.dsproxy.listener.CompositeProxyDataSourceListener;
import net.ttddyy.dsproxy.listener.DataSourceQueryCountListener;
import net.ttddyy.dsproxy.listener.ProxyDataSourceListener;
import net.ttddyy.dsproxy.listener.QueryCountStrategy;
import net.ttddyy.dsproxy.listener.SlowQueryListener;
import net.ttddyy.dsproxy.listener.ThreadQueryCountHolder;
import net.ttddyy.dsproxy.listener.TracingMethodListener;
import net.ttddyy.dsproxy.proxy.DataSourceProxyLogic;
import net.ttddyy.dsproxy.proxy.JdbcProxyFactory;
import net.ttddyy.dsproxy.proxy.ProxyConfig;
import net.ttddyy.dsproxy.proxy.ProxyJdbcObject;
import net.ttddyy.dsproxy.proxy.RepeatableReadResultSetProxyLogicFactory;
import net.ttddyy.dsproxy.proxy.ResultSetProxyLogicFactory;
import net.ttddyy.dsproxy.proxy.jdk.DataSourceInvocationHandler;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Tadaya Tsuyukubo
 */
public class ProxyDataSourceBuilderTest {


    private DataSource mockDs = mock(DataSource.class);

    @Test
    void build() {

        ProxyDataSourceBuilder builder = ProxyDataSourceBuilder.create();

        // not specifying datasource
        assertThrows(DataSourceProxyException.class, builder::build);

        // specify ds
        builder.dataSource(this.mockDs);

        DataSource result = builder.build();
        assertTrue(Proxy.isProxyClass(result.getClass()));
        assertThat(result).isInstanceOf(ProxyJdbcObject.class);
        Object target = ((ProxyJdbcObject) result).getTarget();
        assertSame(this.mockDs, target);

        // create() method with datasource argument
        result = ProxyDataSourceBuilder.create(this.mockDs).build();
        assertNotNull(result);

    }

    private ProxyConfig getProxyConfig(DataSource proxyDataSource) {
        // reflectively retrieve ProxyConfig for test sake
        try {
            InvocationHandler ih = Proxy.getInvocationHandler(proxyDataSource);
            Field delegateField = DataSourceInvocationHandler.class.getDeclaredField("delegate");
            delegateField.setAccessible(true);
            DataSourceProxyLogic logic = (DataSourceProxyLogic) delegateField.get(ih);
            Field proxyConfigField = DataSourceProxyLogic.class.getDeclaredField("proxyConfig");
            proxyConfigField.setAccessible(true);
            return (ProxyConfig) proxyConfigField.get(logic);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void onSlowQuery() {

        DataSource ds;
        SlowQueryListener listener;

        Consumer<QueryExecutionContext> consumer = executionContext -> {
        };

        ds = ProxyDataSourceBuilder.create(this.mockDs).onSlowQuery(10, TimeUnit.SECONDS, consumer).build();
        listener = getAndVerifyListener(ds, SlowQueryListener.class);
        assertThat(listener.getThreshold()).isEqualTo(10);
        assertThat(listener.getThresholdTimeUnit()).isEqualTo(TimeUnit.SECONDS);
    }

    @SuppressWarnings("unchecked")
    private <T extends ProxyDataSourceListener> T getAndVerifyListener(DataSource ds, Class<T> listenerClass) {
        ProxyDataSourceListener listener = getProxyConfig(ds).getListeners();
        assertThat(listener).isInstanceOf(CompositeProxyDataSourceListener.class);
        List<ProxyDataSourceListener> listeners = ((CompositeProxyDataSourceListener) listener).getListeners();
        assertThat(listeners).hasSize(1);

        ProxyDataSourceListener target = listeners.get(0);
        assertThat(target).isInstanceOf(listenerClass);

        return (T) target;
    }

    @Test
    public void jdbcProxyFactory() {
        DataSource ds;
        ds = ProxyDataSourceBuilder.create(this.mockDs).build();
        assertThat(getProxyConfig(ds).getJdbcProxyFactory()).as("Default one should be used").isSameAs(JdbcProxyFactory.DEFAULT);

        DataSource mockDs = mock(DataSource.class);
        JdbcProxyFactory proxyFactory = mock(JdbcProxyFactory.class);
        when(proxyFactory.createDataSource(any(), any())).thenReturn(mockDs);

        ds = ProxyDataSourceBuilder.create(this.mockDs).jdbcProxyFactory(proxyFactory).build();
        assertSame(mockDs, ds);

        verify(proxyFactory).createDataSource(any(), any());
    }

    @Test
    public void connectionIdManager() {
        DataSource ds1 = ProxyDataSourceBuilder.create(this.mockDs).build();
        DataSource ds2 = ProxyDataSourceBuilder.create(this.mockDs).build();
        assertThat(getProxyConfig(ds1).getConnectionIdManager()).as("new instance should be created").isNotSameAs(getProxyConfig(ds2).getConnectionIdManager());

        ConnectionIdManager connectionIdManager = mock(ConnectionIdManager.class);
        DataSource ds = ProxyDataSourceBuilder.create(this.mockDs).connectionIdManager(connectionIdManager).build();
        assertThat(getProxyConfig(ds).getConnectionIdManager()).isSameAs(connectionIdManager);
    }

    @Test
    public void countListener() {

        DataSource ds;
        DataSourceQueryCountListener listener;

        // default strategy
        ds = ProxyDataSourceBuilder.create(this.mockDs).countQuery().build();
        listener = getAndVerifyListener(ds, DataSourceQueryCountListener.class);
        assertThat(listener.getQueryCountStrategy()).as("default count listener")
                .isNotNull()
                .isInstanceOf(ThreadQueryCountHolder.class);

        // specify strategy
        QueryCountStrategy strategy = mock(QueryCountStrategy.class);

        ds = ProxyDataSourceBuilder.create(this.mockDs).countQuery(strategy).build();
        listener = getAndVerifyListener(ds, DataSourceQueryCountListener.class);
        assertThat(listener.getQueryCountStrategy()).as("count listener with strategy")
                .isNotNull()
                .isSameAs(strategy);
    }

    @Test
    public void tracingListener() {

        DataSource ds;
        TracingMethodListener listener;

        // default strategy
        ds = ProxyDataSourceBuilder.create(this.mockDs).traceMethods().build();
        listener = getAndVerifyMethodListener(ds, TracingMethodListener.class);

        assertThat(listener).as("default tracing listener").isNotNull();

        // with message consumer
        Consumer<String> consumer = (str) -> {
        };

        ds = ProxyDataSourceBuilder.create(this.mockDs).traceMethods(consumer).build();
        listener = getAndVerifyMethodListener(ds, TracingMethodListener.class);
        assertThat(listener.getTracingMessageConsumer()).as("tracing listener with message consumer")
                .isNotNull()
                .isSameAs(consumer);

        // with tracing condition
        BooleanSupplier condition = mock(BooleanSupplier.class);

        ds = ProxyDataSourceBuilder.create(this.mockDs).traceMethodsWhen(condition).build();
        listener = getAndVerifyMethodListener(ds, TracingMethodListener.class);
        assertThat(listener.getTracingCondition()).as("tracing listener with tracing condition")
                .isNotNull()
                .isSameAs(condition);

        // with message consumer and tracing condition
        ds = ProxyDataSourceBuilder.create(this.mockDs).traceMethodsWhen(condition, consumer).build();
        listener = getAndVerifyMethodListener(ds, TracingMethodListener.class);
        assertThat(listener.getTracingMessageConsumer()).as("tracing listener with tracing condition and message consumer")
                .isNotNull()
                .isSameAs(consumer);
        assertThat(listener.getTracingCondition()).as("tracing listener with tracing condition and message consumer")
                .isNotNull()
                .isSameAs(condition);

    }

    @SuppressWarnings("unchecked")
    private <T extends ProxyDataSourceListener> T getAndVerifyMethodListener(DataSource ds, Class<T> listenerClass) {
        CompositeProxyDataSourceListener compositeListener = getProxyConfig(ds).getListeners();
        List<ProxyDataSourceListener> listeners = compositeListener.getListeners();
        assertThat(listeners).hasSize(1);

        ProxyDataSourceListener target = listeners.get(0);
        assertThat(target).isInstanceOf(listenerClass);

        return (T) target;
    }

    @Test
    public void buildMethodListener() {
        DataSource ds;
        CompositeProxyDataSourceListener methodListener;

        ProxyDataSourceListener listener1 = mock(ProxyDataSourceListener.class);
        ProxyDataSourceListener listener2 = mock(ProxyDataSourceListener.class);

        // single listener
        ds = ProxyDataSourceBuilder.create(this.mockDs).methodListener(listener1).build();
        methodListener = getProxyConfig(ds).getListeners();
        assertThat(methodListener.getListeners()).hasSize(1).contains(listener1);

        // multiple listeners
        ds = ProxyDataSourceBuilder.create(this.mockDs).methodListener(listener1).methodListener(listener2).build();
        methodListener = getProxyConfig(ds).getListeners();
        assertThat(methodListener.getListeners()).hasSize(2).contains(listener1, listener2);
    }

    @Test
    public void singleMethodExecutionWithBeforeMethodAndAfterMethod() {
        DataSource ds;
        CompositeProxyDataSourceListener compositeListener;
        ProxyDataSourceListener listener;

        // check beforeMethod()
        final AtomicBoolean isBeforeInvoked = new AtomicBoolean();
        ds = ProxyDataSourceBuilder.create(this.mockDs)
                .beforeMethod(executionContext -> {
                    isBeforeInvoked.set(true);
                })
                .build();
        compositeListener = getProxyConfig(ds).getListeners();
        assertThat(compositeListener.getListeners()).hasSize(1);

        // invoke found listener and verify invocation
        listener = compositeListener.getListeners().get(0);
        listener.beforeMethod(null);  // invoke
        assertThat(isBeforeInvoked.get()).isTrue();

        // check afterMethod()
        final AtomicBoolean isAfterInvoked = new AtomicBoolean();
        ds = ProxyDataSourceBuilder.create(this.mockDs)
                .afterMethod(executionContext -> {
                    isAfterInvoked.set(true);
                })
                .build();
        compositeListener = getProxyConfig(ds).getListeners();
        assertThat(compositeListener.getListeners()).hasSize(1);

        // invoke found listener and verify invocation
        listener = compositeListener.getListeners().get(0);
        listener.afterMethod(null);  // invoke
        assertThat(isAfterInvoked.get()).isTrue();

    }

    @Test
    public void singleQueryExecutionWithBeforeQueryAndAfterQuery() {
        DataSource ds;
        CompositeProxyDataSourceListener CompositeProxyDataSourceListener;
        ProxyDataSourceListener listener;

        // check beforeMethod()
        final AtomicBoolean isBeforeInvoked = new AtomicBoolean();
        ds = ProxyDataSourceBuilder.create(this.mockDs)
                .beforeQuery((queryContext) -> {
                    isBeforeInvoked.set(true);
                })
                .build();
        CompositeProxyDataSourceListener = getProxyConfig(ds).getListeners();
        assertThat(CompositeProxyDataSourceListener.getListeners()).hasSize(1);

        // invoke found listener and verify invocation
        listener = CompositeProxyDataSourceListener.getListeners().get(0);
        listener.beforeQuery(null);  // invoke
        assertThat(isBeforeInvoked.get()).isTrue();

        // check afterMethod()
        final AtomicBoolean isAfterInvoked = new AtomicBoolean();
        ds = ProxyDataSourceBuilder.create(this.mockDs)
                .afterQuery((queryContext) -> {
                    isAfterInvoked.set(true);
                })
                .build();
        CompositeProxyDataSourceListener = getProxyConfig(ds).getListeners();
        assertThat(CompositeProxyDataSourceListener.getListeners()).hasSize(1);

        // invoke found listener and verify invocation
        listener = CompositeProxyDataSourceListener.getListeners().get(0);
        listener.afterQuery(null);  // invoke
        assertThat(isAfterInvoked.get()).isTrue();

    }

    @Test
    public void proxyResultSet() {
        DataSource ds;
        ResultSetProxyLogicFactory factory = mock(ResultSetProxyLogicFactory.class);

        // default
        ds = ProxyDataSourceBuilder.create(this.mockDs).build();
        assertThat(getProxyConfig(ds).isResultSetProxyEnabled()).isFalse();

        // with default proxy logic factory
        ds = ProxyDataSourceBuilder.create(this.mockDs).proxyResultSet().build();
        assertThat(getProxyConfig(ds).isResultSetProxyEnabled()).isTrue();
        assertThat(getProxyConfig(ds).getResultSetProxyLogicFactory()).isSameAs(ResultSetProxyLogicFactory.DEFAULT);

        // with proxy factory
        ds = ProxyDataSourceBuilder.create(this.mockDs).proxyResultSet(factory).build();
        assertThat(getProxyConfig(ds).isResultSetProxyEnabled()).isTrue();
        assertThat(getProxyConfig(ds).getResultSetProxyLogicFactory()).isSameAs(factory);

        // with repeatable read proxy factory
        ds = ProxyDataSourceBuilder.create(this.mockDs).repeatableReadResultSet().build();
        assertThat(getProxyConfig(ds).isResultSetProxyEnabled()).isTrue();
        assertThat(getProxyConfig(ds).getResultSetProxyLogicFactory()).isInstanceOf(RepeatableReadResultSetProxyLogicFactory.class);

    }

    @Test
    public void proxyGeneratedKeys() {
        DataSource ds;
        ResultSetProxyLogicFactory factory = mock(ResultSetProxyLogicFactory.class);

        // default
        ds = ProxyDataSourceBuilder.create(this.mockDs).build();
        assertThat(getProxyConfig(ds).isAutoRetrieveGeneratedKeys()).isFalse();
        assertThat(getProxyConfig(ds).isGeneratedKeysProxyEnabled()).isFalse();

        // with default proxy logic factory
        ds = ProxyDataSourceBuilder.create(this.mockDs).proxyGeneratedKeys().build();
        assertThat(getProxyConfig(ds).isGeneratedKeysProxyEnabled()).isTrue();
        assertThat(getProxyConfig(ds).isAutoRetrieveGeneratedKeys()).isFalse();
        assertThat(getProxyConfig(ds).getGeneratedKeysProxyLogicFactory()).isSameAs(ResultSetProxyLogicFactory.DEFAULT);

        // with proxy factory
        ds = ProxyDataSourceBuilder.create(this.mockDs).proxyGeneratedKeys(factory).build();
        assertThat(getProxyConfig(ds).isGeneratedKeysProxyEnabled()).isTrue();
        assertThat(getProxyConfig(ds).isAutoRetrieveGeneratedKeys()).isFalse();
        assertThat(getProxyConfig(ds).getGeneratedKeysProxyLogicFactory()).isSameAs(factory);

        // with repeatable read proxy factory
        ds = ProxyDataSourceBuilder.create(this.mockDs).repeatableReadGeneratedKeys().build();
        assertThat(getProxyConfig(ds).isGeneratedKeysProxyEnabled()).isTrue();
        assertThat(getProxyConfig(ds).isAutoRetrieveGeneratedKeys()).isFalse();
        assertThat(getProxyConfig(ds).getGeneratedKeysProxyLogicFactory()).isInstanceOf(RepeatableReadResultSetProxyLogicFactory.class);

    }

    @Test
    public void autoRetrievalGeneratedKeys() {
        DataSource ds;
        ResultSetProxyLogicFactory factory = mock(ResultSetProxyLogicFactory.class);

        // default
        ds = ProxyDataSourceBuilder.create(this.mockDs).build();
        assertThat(getProxyConfig(ds).isAutoRetrieveGeneratedKeys()).isFalse();
        assertThat(getProxyConfig(ds).isAutoCloseGeneratedKeys()).isFalse();
        assertThat(getProxyConfig(ds).isGeneratedKeysProxyEnabled()).isFalse();

        // set true
        ds = ProxyDataSourceBuilder.create(this.mockDs).autoRetrieveGeneratedKeys(true).build();
        assertThat(getProxyConfig(ds).isAutoRetrieveGeneratedKeys()).isTrue();
        assertThat(getProxyConfig(ds).isAutoCloseGeneratedKeys()).isTrue();
        assertThat(getProxyConfig(ds).isGeneratedKeysProxyEnabled()).isFalse();

        // set false
        ds = ProxyDataSourceBuilder.create(this.mockDs).autoRetrieveGeneratedKeys(false).build();
        assertThat(getProxyConfig(ds).isAutoRetrieveGeneratedKeys()).isTrue();
        assertThat(getProxyConfig(ds).isAutoCloseGeneratedKeys()).isFalse();
        assertThat(getProxyConfig(ds).isGeneratedKeysProxyEnabled()).isFalse();


        // with proxy factory
        ds = ProxyDataSourceBuilder.create(this.mockDs).autoRetrieveGeneratedKeys(true, factory).build();
        assertThat(getProxyConfig(ds).isAutoRetrieveGeneratedKeys()).isTrue();
        assertThat(getProxyConfig(ds).isAutoCloseGeneratedKeys()).isTrue();
        assertThat(getProxyConfig(ds).isGeneratedKeysProxyEnabled()).isTrue();
        assertThat(getProxyConfig(ds).getGeneratedKeysProxyLogicFactory()).isSameAs(factory);

        ds = ProxyDataSourceBuilder.create(this.mockDs).autoRetrieveGeneratedKeys(false, factory).build();
        assertThat(getProxyConfig(ds).isAutoRetrieveGeneratedKeys()).isTrue();
        assertThat(getProxyConfig(ds).isAutoCloseGeneratedKeys()).isFalse();
        assertThat(getProxyConfig(ds).isGeneratedKeysProxyEnabled()).isTrue();
        assertThat(getProxyConfig(ds).getGeneratedKeysProxyLogicFactory()).isSameAs(factory);

        // with repeatable read proxy factory
        ds = ProxyDataSourceBuilder.create(this.mockDs).autoRetrieveGeneratedKeysWithRepeatableReadProxy(true).build();
        assertThat(getProxyConfig(ds).isAutoRetrieveGeneratedKeys()).isTrue();
        assertThat(getProxyConfig(ds).isAutoCloseGeneratedKeys()).isTrue();
        assertThat(getProxyConfig(ds).isGeneratedKeysProxyEnabled()).isTrue();
        assertThat(getProxyConfig(ds).getGeneratedKeysProxyLogicFactory()).isInstanceOf(RepeatableReadResultSetProxyLogicFactory.class);

        ds = ProxyDataSourceBuilder.create(this.mockDs).autoRetrieveGeneratedKeysWithRepeatableReadProxy(false).build();
        assertThat(getProxyConfig(ds).isAutoRetrieveGeneratedKeys()).isTrue();
        assertThat(getProxyConfig(ds).isAutoCloseGeneratedKeys()).isFalse();
        assertThat(getProxyConfig(ds).isGeneratedKeysProxyEnabled()).isTrue();
        assertThat(getProxyConfig(ds).getGeneratedKeysProxyLogicFactory()).isInstanceOf(RepeatableReadResultSetProxyLogicFactory.class);

    }

    @Test
    public void autoRetrievalGeneratedKeysForBatch() {
        DataSource ds;

        // default
        ds = ProxyDataSourceBuilder.create(this.mockDs).build();
        assertThat(getProxyConfig(ds).isRetrieveGeneratedKeysForBatchStatement()).isFalse();
        assertThat(getProxyConfig(ds).isRetrieveGeneratedKeysForBatchPreparedOrCallable()).isTrue();

        // set true
        ds = ProxyDataSourceBuilder.create(this.mockDs)
                .retrieveGeneratedKeysForBatch(true, true)
                .build();
        assertThat(getProxyConfig(ds).isRetrieveGeneratedKeysForBatchStatement()).isTrue();
        assertThat(getProxyConfig(ds).isRetrieveGeneratedKeysForBatchPreparedOrCallable()).isTrue();


        // set false
        ds = ProxyDataSourceBuilder.create(this.mockDs)
                .retrieveGeneratedKeysForBatch(false, false)
                .build();
        assertThat(getProxyConfig(ds).isRetrieveGeneratedKeysForBatchStatement()).isFalse();
        assertThat(getProxyConfig(ds).isRetrieveGeneratedKeysForBatchPreparedOrCallable()).isFalse();

    }
}
