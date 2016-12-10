package net.ttddyy.dsproxy.proxy.jdk;

import net.ttddyy.dsproxy.proxy.ProxyJdbcObject;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * This proxies any {@link java.sql.ResultSet} results so that they can be consumed more than once.
 *
 * @param <T> The {@link java.sql.Statement} type the proxy is for.
 *
 * @author Liam Williams
 */
class StatementResultSetResultInvocationHandler<T extends Statement> implements InvocationHandler {

    private final T target;

    private StatementResultSetResultInvocationHandler(T target) {
        this.target = target;
    }

    public static <T extends Statement> T statementResultSetResultProxy(T target, Class<T> interfaceToProxy) {
        return interfaceToProxy.cast(Proxy.newProxyInstance(ProxyJdbcObject.class.getClassLoader(), new Class<?>[]{ProxyJdbcObject.class, interfaceToProxy}, new StatementResultSetResultInvocationHandler<T>(target)));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = method.invoke(target, args);
        if (result instanceof ResultSet) {
            return ResultSetInvocationHandler.proxy((ResultSet) result);
        }
        return result;
    }
}
