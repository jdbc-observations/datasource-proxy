package net.ttddyy.dsproxy.proxy;

import java.lang.reflect.Method;

/**
 * Keeps method and arguments data when parameter set method is called.
 *
 * @author Tadaya Tsuyukubo
 * @see PreparedStatementInvocationHandler
 * @since 1.2
 */
public class ParameterSetOperation {
    private Method method;
    private Object[] args;

    public ParameterSetOperation() {
    }

    public ParameterSetOperation(Method method, Object[] args) {
        this.method = method;
        this.args = args;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
