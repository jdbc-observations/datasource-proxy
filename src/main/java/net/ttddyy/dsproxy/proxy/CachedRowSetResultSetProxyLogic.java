package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.listener.MethodExecutionListenerUtils;

import java.lang.reflect.Method;
import java.sql.ResultSet;

/**
 * Uses {@link javax.sql.rowset.CachedRowSet} to provide repeatable read {@link ResultSet} proxy.
 *
 * {@link javax.sql.rowset.CachedRowSet} is disconnected scrollable {@link javax.sql.RowSet} which is a
 * subset of {@link ResultSet}. Using those feature to provide repeatable read resultset proxy.
 *
 * The default oracle jdk implementation {@code com.sun.rowset.CachedRowSetImpl} does not support {@code isClosed}
 * method. Thus, keep close status and return it when {@code isClosed()} is called.
 *
 * @author Tadaya Tsuyukubo
 * @see CachedRowSetResultSetProxyLogicFactory
 * @since 1.4.7
 */
public class CachedRowSetResultSetProxyLogic implements ResultSetProxyLogic {

    private ResultSet resultSet;
    private ConnectionInfo connectionInfo;
    private ProxyConfig proxyConfig;

    // default impl "com.sun.rowset.CachedRowSetImpl" does NOT support "isClosed()", so enable handling in this class
    protected boolean supportIsClosedMethod = true;
    protected boolean isClosed;

    public CachedRowSetResultSetProxyLogic(ResultSet resultSet, ConnectionInfo connectionInfo, ProxyConfig proxyConfig) {
        this.resultSet = resultSet;
        this.connectionInfo = connectionInfo;
        this.proxyConfig = proxyConfig;
    }

    @Override
    public Object invoke(Method method, Object[] args) throws Throwable {
        return MethodExecutionListenerUtils.invoke(new MethodExecutionListenerUtils.MethodExecutionCallback() {
            @Override
            public Object execute(Object proxyTarget, Method method, Object[] args) throws Throwable {
                return performQueryExecutionListener(method, args);
            }
        }, this.proxyConfig, this.resultSet, this.connectionInfo, method, args);
    }

    private Object performQueryExecutionListener(Method method, Object[] args) throws Throwable {

        String methodName = method.getName();

        if ("close".equals(methodName)) {
            this.isClosed = true;
        }

        if (this.supportIsClosedMethod && "isClosed".equals(methodName)) {
            return this.isClosed;
        }

        // TODO: handle getStatement() method to return proxied statement

        return MethodUtils.proceedExecution(method, this.resultSet, args);
    }
}
