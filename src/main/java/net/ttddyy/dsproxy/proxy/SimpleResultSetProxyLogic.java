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

        String methodName = method.getName();

        if (isToStringMethod(methodName)) {
            return handleToStringMethod(this.resultSet);  // special treat for toString method
        } else if (isGetTargetMethod(methodName)) {
            return this.resultSet;  // ProxyJdbcObject interface has a method to return original object.
        }

        return proceedExecution(method, this.resultSet, args);
    }
}
