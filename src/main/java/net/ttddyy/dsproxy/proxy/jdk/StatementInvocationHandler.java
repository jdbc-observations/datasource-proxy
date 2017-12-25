package net.ttddyy.dsproxy.proxy.jdk;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.StatementType;
import net.ttddyy.dsproxy.proxy.StatementProxyLogic;
import net.ttddyy.dsproxy.proxy.ProxyConfig;

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
            Statement stmt, ConnectionInfo connectionInfo,
            Connection proxyConnection, ProxyConfig proxyConfig) {
        this.delegate = StatementProxyLogic.Builder.create()
                .statement(stmt, StatementType.STATEMENT)
                .connectionInfo(connectionInfo)
                .proxyConnection(proxyConnection)
                .proxyConfig(proxyConfig)
                .build();
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return delegate.invoke(method, args);
    }
}
