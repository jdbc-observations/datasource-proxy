package net.ttddyy.dsproxy.proxy.jdk;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.StatementType;
import net.ttddyy.dsproxy.proxy.ProxyConfig;
import net.ttddyy.dsproxy.proxy.StatementProxyLogic;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;

/**
 * Proxy InvocationHandler for {@link java.sql.CallableStatement}.
 *
 * @author Tadaya Tsuyukubo
 */
public class CallableStatementInvocationHandler implements InvocationHandler {

    private StatementProxyLogic delegate;

    public CallableStatementInvocationHandler(
            CallableStatement cs, String query, ConnectionInfo connectionInfo,
            Connection proxyConnection, ProxyConfig proxyConfig) {

        this.delegate = StatementProxyLogic.Builder.create()
                .statement(cs, StatementType.CALLABLE)
                .query(query)
                .connectionInfo(connectionInfo)
                .proxyConnection(proxyConnection)
                .proxyConfig(proxyConfig)
                .build();
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return delegate.invoke(method, args);
    }

}
