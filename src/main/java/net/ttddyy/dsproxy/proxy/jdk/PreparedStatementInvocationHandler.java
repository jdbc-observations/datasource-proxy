package net.ttddyy.dsproxy.proxy.jdk;

import net.ttddyy.dsproxy.proxy.InterceptorHolder;
import net.ttddyy.dsproxy.proxy.JdbcProxyFactory;
import net.ttddyy.dsproxy.proxy.PreparedStatementProxyLogic;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Proxy InvocationHandler for {@link java.sql.PreparedStatement}.
 *
 * @author Tadaya Tsuyukubo
 */
public class PreparedStatementInvocationHandler implements InvocationHandler {

    private PreparedStatementProxyLogic delegate;

    public PreparedStatementInvocationHandler(
            PreparedStatement ps, String query, InterceptorHolder interceptorHolder, String dataSourceName,
            JdbcProxyFactory jdbcProxyFactory, Connection proxyConnection) {

        this.delegate = PreparedStatementProxyLogic.Builder.create()
                .setPreparedStatement(ps)
                .setQuery(query)
                .setInterceptorHolder(interceptorHolder)
                .setDataSourceName(dataSourceName)
                .setJdbcProxyFactory(jdbcProxyFactory)
                .setProxyConnection(proxyConnection)
                .build();
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return delegate.invoke(method, args);
    }

}
