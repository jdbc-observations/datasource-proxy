package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.listener.MethodExecutionContext;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Uses {@link javax.sql.rowset.CachedRowSet} to provide a repeatable-read {@link ResultSet} proxy.
 *
 * <p>A {@link javax.sql.rowset.CachedRowSet} is a disconnected, scrollable {@link javax.sql.RowSet} that implements a
 * subset of {@link ResultSet}. Its features are used to provide a repeatable-read result set proxy.
 *
 * <p>The default Oracle JDK implementation, {@code com.sun.rowset.CachedRowSetImpl}, does not support the
 * {@code isClosed} method. This class therefore tracks the closed state and returns it when {@code isClosed()} is called.
 *
 * @author Tadaya Tsuyukubo
 * @see CachedRowSetResultSetProxyLogicFactory
 * @since 1.4.7
 */
public class CachedRowSetResultSetProxyLogic extends ProxyLogicSupport implements ResultSetProxyLogic {

    private ResultSet resultSet;  // original result set
    private ResultSet cachedRowSet;
    private ConnectionInfo connectionInfo;
    private ProxyConfig proxyConfig;

    // The default implementation does not support isClosed(), so handle it in this class.
    protected boolean supportIsClosedMethod = true;
    protected boolean isClosed;

    public CachedRowSetResultSetProxyLogic(ResultSet resultSet, ResultSet cachedRowSet, ConnectionInfo connectionInfo, ProxyConfig proxyConfig) {
        this.resultSet = resultSet;
        this.cachedRowSet = cachedRowSet;
        this.connectionInfo = connectionInfo;
        this.proxyConfig = proxyConfig;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return proceedMethodExecution(this.proxyConfig, this.resultSet, this.connectionInfo, proxy, method, args);
    }

    @Override
    protected Object performProxyLogic(Object proxy, Method method, Object[] args, MethodExecutionContext methodContext) throws Throwable {
        String methodName = method.getName();

        if (isCommonMethod(methodName)) {
            return handleCommonMethod(methodName, this.resultSet, this.proxyConfig, args);
        }

        if ("close".equals(methodName)) {
            this.isClosed = true;
        } else if ("getTarget".equals(methodName)) {
            // ProxyJdbcObject interface has a method to return original object.
            return this.resultSet;
        }

        if (this.supportIsClosedMethod && "isClosed".equals(methodName)) {
            return this.isClosed;
        }

        // TODO: handle getStatement() method to return proxied statement

        try {
            return proceedExecution(method, this.cachedRowSet, args);
        } catch (Throwable throwable) {
            if (throwable instanceof SQLException) {
                throw throwable;
            }
            // Convert any exception to SQLException.
            String reason = String.format("CachedRowSet threw exception: %s", throwable);
            throw new SQLException(reason, throwable);
        }
    }
}
