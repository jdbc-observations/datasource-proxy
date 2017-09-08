package net.ttddyy.dsproxy.proxy.jdk;

import net.ttddyy.dsproxy.proxy.ResultSetProxyLogic;
import net.ttddyy.dsproxy.proxy.ResultSetUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.Map;

/**
 * Proxy InvocationHandler for {@link java.sql.ResultSet}.
 *
 * @author Liam Williams
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class ResultSetInvocationHandler implements InvocationHandler {

    private ResultSetProxyLogic delegate;

    public ResultSetInvocationHandler(ResultSet resultSet) {
        Map<String, Integer> columnNameToIndex = ResultSetUtils.columnNameToIndex(resultSet);
        int columnCount = ResultSetUtils.columnCount(resultSet);
        this.delegate = ResultSetProxyLogic.Builder.create()
                .resultSet(resultSet)
                .columnNameToIndex(columnNameToIndex)
                .columnCount(columnCount)
                .build();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return delegate.invoke(method, args);
    }
}
