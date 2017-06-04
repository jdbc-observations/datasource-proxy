package net.ttddyy.dsproxy.proxy.jdk;

import net.ttddyy.dsproxy.proxy.InterceptorHolder;
import net.ttddyy.dsproxy.proxy.JdbcProxyFactory;
import net.ttddyy.dsproxy.proxy.StatementProxyLogic;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Proxy InvocationHandler for {@link java.sql.Statement}.
 *
 * @author Tadaya Tsuyukubo
 */
public class StatementInvocationHandler implements InvocationHandler {

    private StatementProxyLogic delegate;

    public StatementInvocationHandler(
            Statement stmt, InterceptorHolder interceptorHolder, String dataSourceName, JdbcProxyFactory jdbcProxyFactory,
            Connection proxyConnection) {
        delegate = new StatementProxyLogic(stmt, interceptorHolder, dataSourceName, jdbcProxyFactory, proxyConnection);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return delegate.invoke(method, args);
    }
}
