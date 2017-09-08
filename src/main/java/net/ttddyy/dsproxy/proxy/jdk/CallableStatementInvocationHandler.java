package net.ttddyy.dsproxy.proxy.jdk;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.proxy.InterceptorHolder;
import net.ttddyy.dsproxy.proxy.JdbcProxyFactory;
import net.ttddyy.dsproxy.proxy.PreparedStatementProxyLogic;

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

    private PreparedStatementProxyLogic delegate;

    public CallableStatementInvocationHandler() {
    }

    public CallableStatementInvocationHandler(
            CallableStatement cs, String query, InterceptorHolder interceptorHolder, ConnectionInfo connectionInfo,
            Connection proxyConnection, JdbcProxyFactory proxyFactory) {
        this.delegate = PreparedStatementProxyLogic.Builder.create()
                .preparedStatement(cs)
                .query(query)
                .interceptorHolder(interceptorHolder)
                .connectionInfo(connectionInfo)
                .proxyConnection(proxyConnection)
                .proxyFactory(proxyFactory)
                .build();
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return delegate.invoke(method, args);
    }

}
