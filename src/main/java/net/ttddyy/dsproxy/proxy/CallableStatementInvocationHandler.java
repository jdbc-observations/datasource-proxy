package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.transform.QueryTransformer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.CallableStatement;

/**
 * Proxy InvocationHandler for {@link java.sql.CallableStatement}.
 *
 * @author Tadaya Tsuyukubo
 */
public class CallableStatementInvocationHandler implements InvocationHandler {

    private PreparedStatementProxyLogic delegate;

    public CallableStatementInvocationHandler() {
    }

    @Deprecated
    public CallableStatementInvocationHandler(
            CallableStatement cs, String query, QueryExecutionListener listener, String dataSourceName, JdbcProxyFactory jdbcProxyFactory) {
        delegate = new PreparedStatementProxyLogic(cs, query, new InterceptorHolder(listener, QueryTransformer.DEFAULT), dataSourceName, jdbcProxyFactory);
    }

    public CallableStatementInvocationHandler(
            CallableStatement cs, String query, InterceptorHolder interceptorHolder, String dataSourceName, JdbcProxyFactory jdbcProxyFactory) {
        delegate = new PreparedStatementProxyLogic(cs, query, interceptorHolder, dataSourceName, jdbcProxyFactory);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return delegate.invoke(method, args);
    }

}
