package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.listener.MethodExecutionContext;

import java.lang.reflect.Method;
import java.sql.ResultSet;

/**
 * Simply delegate method calls to the actual {@link ResultSet}.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.3
 */
public class SimpleResultSetProxyLogic extends ProxyLogicSupport implements ResultSetProxyLogic {

    private ResultSet resultSet;
    private ConnectionInfo connectionInfo;
    private ProxyConfig proxyConfig;

    public SimpleResultSetProxyLogic(ResultSet resultSet, ConnectionInfo connectionInfo, ProxyConfig proxyConfig) {
        this.resultSet = resultSet;
        this.connectionInfo = connectionInfo;
        this.proxyConfig = proxyConfig;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return proceedMethodExecution(this.proxyConfig, this.resultSet, this.connectionInfo, proxy, method, args);
    }

    @Override
    protected Object performProxyLogic(Object proxy, Method method, Object[] args, MethodExecutionContext methodContext) throws Throwable {
        final String methodName = method.getName();
        if (isCommonMethod(methodName)) {
            return handleCommonMethod(methodName, this.resultSet, this.connectionInfo.getDataSourceName(), args);
        }
        return proceedExecution(method, this.resultSet, args);
    }

}
