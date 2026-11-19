package net.ttddyy.dsproxy.listener.lifecycle;

import static net.ttddyy.dsproxy.QueryExecutionContextBuilder.withQueries;
import net.ttddyy.dsproxy.proxy.MutableQueryExecutionContext;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.MethodExecutionContext;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static java.lang.Character.toUpperCase;

/**
 * @author Tadaya Tsuyukubo
 */
class JdbcLifecycleEventExecutionListenerTest {

    @Test
    void queryExecution() {
        JdbcLifecycleEventListener mock = mock(JdbcLifecycleEventListener.class);
        JdbcLifecycleEventExecutionListener listener = new JdbcLifecycleEventExecutionListener(mock);

        MutableQueryExecutionContext executionInfo = new MutableQueryExecutionContext();
        List<QueryInfo> queryInfoList = new ArrayList<QueryInfo>();

        listener.beforeQuery(withQueries(executionInfo, queryInfoList));
        verify(mock).beforeQuery(withQueries(executionInfo, queryInfoList));

        listener.afterQuery(withQueries(executionInfo, queryInfoList));
        verify(mock).afterQuery(withQueries(executionInfo, queryInfoList));
    }

    @Test
    void methodInvocation() {

        // TODO: parameterized test
        List<Class<? extends Wrapper>> proxyClasses = Arrays.asList(DataSource.class, Connection.class, ResultSet.class,
                Statement.class, PreparedStatement.class, CallableStatement.class);

        for (Class<? extends Wrapper> proxyClass : proxyClasses) {
            Wrapper mock = mock(proxyClass);

            Method[] methods = proxyClass.getMethods();

            for (Method method : methods) {

                String expectedBeforeName = "before" + toUpperCase(method.getName().charAt(0)) + method.getName().substring(1);
                String expectedAfterName = "after" + toUpperCase(method.getName().charAt(0)) + method.getName().substring(1);

                // create a listener
                List<String> invokedMethodNames = new ArrayList<String>();
                List<List<Object>> invokedMethodArgs = new ArrayList<List<Object>>();
                JdbcLifecycleEventListener proxyListener = createProxyListener(invokedMethodNames, invokedMethodArgs);
                JdbcLifecycleEventExecutionListener listener = new JdbcLifecycleEventExecutionListener(proxyListener);

                MethodExecutionContext methodExecContext = new MethodExecutionContext();
                methodExecContext.setMethod(method);
                methodExecContext.setTarget(mock);

                listener.beforeMethod(methodExecContext);
                String description = "method=" + method.getName();
                assertThat(invokedMethodNames).describedAs(description).containsExactly("beforeMethod", expectedBeforeName);
                assertThat(invokedMethodArgs).describedAs(description).hasSize(2);
                assertThat(invokedMethodArgs.get(0)).describedAs(description).containsExactly(methodExecContext);
                assertThat(invokedMethodArgs.get(1)).describedAs(description).containsExactly(methodExecContext);

                invokedMethodNames.clear();
                invokedMethodArgs.clear();

                listener.afterMethod(methodExecContext);
                assertThat(invokedMethodNames).containsExactly(expectedAfterName, "afterMethod");
                assertThat(invokedMethodArgs).hasSize(2);
                assertThat(invokedMethodArgs.get(0)).containsExactly(methodExecContext);
                assertThat(invokedMethodArgs.get(1)).containsExactly(methodExecContext);
            }
        }

    }

    private JdbcLifecycleEventListener createProxyListener(final List<String> invokedMethodNames, final List<List<Object>> invokedMethodArgs) {
        // create a proxy that captures all invoked method names
        return (JdbcLifecycleEventListener) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class[]{JdbcLifecycleEventListener.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        invokedMethodNames.add(method.getName());
                        invokedMethodArgs.add(Arrays.asList(args));
                        return null;
                    }
                });

    }

    @Test
    void objectMethods() {

        for (Method method : Object.class.getMethods()) {

            // create a listener
            List<String> invokedMethodNames = new ArrayList<String>();
            List<List<Object>> invokedMethodArgs = new ArrayList<List<Object>>();
            JdbcLifecycleEventListener proxyListener = createProxyListener(invokedMethodNames, invokedMethodArgs);
            JdbcLifecycleEventExecutionListener listener = new JdbcLifecycleEventExecutionListener(proxyListener);

            MethodExecutionContext methodExecContext = new MethodExecutionContext();
            methodExecContext.setMethod(method);
            methodExecContext.setTarget(Statement.class);

            listener.beforeMethod(methodExecContext);
            assertThat(invokedMethodNames).containsExactly("beforeMethod");
            assertThat(invokedMethodArgs).hasSize(1);
            assertThat(invokedMethodArgs.get(0)).containsExactly(methodExecContext);

            invokedMethodNames.clear();
            invokedMethodArgs.clear();

            listener.afterMethod(methodExecContext);
            assertThat(invokedMethodNames).containsExactly("afterMethod");
            assertThat(invokedMethodArgs).hasSize(1);
            assertThat(invokedMethodArgs.get(0)).containsExactly(methodExecContext);
        }
    }

}
