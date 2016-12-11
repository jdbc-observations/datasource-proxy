package net.ttddyy.dsproxy.proxy.jdk;

import net.ttddyy.dsproxy.proxy.ProxyJdbcObject;
import net.ttddyy.dsproxy.proxy.ResultSetProxyLogic;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

import static java.lang.String.format;
import static java.util.Arrays.stream;

/**
 * Proxy InvocationHandler for {@link java.sql.ResultSet}.
 *
 * @author Liam Williams
 * @since 1.4
 */
public class ResultSetInvocationHandler implements InvocationHandler {

    private final ResultSetProxyLogic delegate;

    public ResultSetInvocationHandler(ResultSetProxyLogic delegate) {
        this.delegate = delegate;
    }

    public static ResultSet proxy(ResultSet target) throws SQLException {
        ResultSetInvocationHandler resultSetProxy = new ResultSetInvocationHandler(ResultSetProxyLogic.resultSetProxyLogic(target));
        return (ResultSet) Proxy.newProxyInstance(ProxyJdbcObject.class.getClassLoader(), new Class<?>[]{ProxyJdbcObject.class, ResultSet.class}, resultSetProxy);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return delegate.invoke(method, args);
    }
}
