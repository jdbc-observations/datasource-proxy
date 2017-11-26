package net.ttddyy.dsproxy.proxy.jdk;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.proxy.ProxyConfig;
import net.ttddyy.dsproxy.proxy.ResultSetProxyLogic;
import net.ttddyy.dsproxy.proxy.ResultSetProxyLogicFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.ResultSet;

/**
 * Proxy InvocationHandler for {@link java.sql.ResultSet}.
 *
 * @author Liam Williams
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class ResultSetInvocationHandler implements InvocationHandler {

    private ResultSetProxyLogic delegate;

    public ResultSetInvocationHandler(ResultSetProxyLogicFactory factory, ResultSet resultSet, ConnectionInfo connectionInfo, ProxyConfig proxyConfig) {
        this.delegate = factory.create(resultSet, connectionInfo, proxyConfig);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return delegate.invoke(method, args);
    }
}
