package net.ttddyy.dsproxy.proxy.jdk;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.proxy.ConnectionProxyLogic;
import net.ttddyy.dsproxy.proxy.InterceptorHolder;
import net.ttddyy.dsproxy.proxy.JdbcProxyFactory;

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

    public ConnectionInvocationHandler(Connection connection, InterceptorHolder interceptorHolder,
                                       ConnectionInfo connectionInfo, JdbcProxyFactory jdbcProxyFactory) {
        this.delegate = new ConnectionProxyLogic(connection, interceptorHolder, connectionInfo, jdbcProxyFactory);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return delegate.invoke(proxy, method, args);
    }

}
