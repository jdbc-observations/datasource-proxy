package net.ttddyy.dsproxy.proxy.jdk;

import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.proxy.InterceptorHolder;
import net.ttddyy.dsproxy.proxy.JdbcProxyFactory;
import net.ttddyy.dsproxy.proxy.StatementProxyLogic;
import net.ttddyy.dsproxy.transform.QueryTransformer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Statement;

/**
 * Proxy InvocationHandler for {@link java.sql.Statement}.
 *
 * @author Tadaya Tsuyukubo
 */
public class StatementInvocationHandler implements InvocationHandler {

    private StatementProxyLogic delegate;

    public StatementInvocationHandler(Statement stmt) {
        final InterceptorHolder interceptors = new InterceptorHolder(QueryExecutionListener.DEFAULT, QueryTransformer.DEFAULT);
        delegate = new StatementProxyLogic(stmt, interceptors, "", JdbcProxyFactory.DEFAULT);
    }

    public StatementInvocationHandler(
            Statement stmt, InterceptorHolder interceptorHolder, String dataSourceName, JdbcProxyFactory jdbcProxyFactory) {
        delegate = new StatementProxyLogic(stmt, interceptorHolder, dataSourceName, jdbcProxyFactory);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return delegate.invoke(method, args);
    }
}
