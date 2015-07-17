package net.ttddyy.dsproxy.proxy.jdk;

import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.proxy.ConnectionProxyLogic;
import net.ttddyy.dsproxy.proxy.InterceptorHolder;
import net.ttddyy.dsproxy.proxy.JdbcProxyFactory;
import net.ttddyy.dsproxy.transform.QueryTransformer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;

/**
 * Proxy InvocationHandler for {@link java.sql.Connection}.
 *
 * @author Tadaya Tsuyukubo
 */
public class ConnectionInvocationHandler implements InvocationHandler {

    private ConnectionProxyLogic delegate;

    public ConnectionInvocationHandler(Connection connection) {
        final InterceptorHolder interceptorHolder = new InterceptorHolder(QueryExecutionListener.DEFAULT, QueryTransformer.DEFAULT);
        this.delegate = new ConnectionProxyLogic(connection, interceptorHolder, "", JdbcProxyFactory.DEFAULT);
    }

    public ConnectionInvocationHandler(
            Connection connection, InterceptorHolder interceptorHolder, String dataSourceName, JdbcProxyFactory jdbcProxyFactory) {
        this.delegate = new ConnectionProxyLogic(connection, interceptorHolder, dataSourceName, jdbcProxyFactory);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return delegate.invoke(method, args);
    }

}
