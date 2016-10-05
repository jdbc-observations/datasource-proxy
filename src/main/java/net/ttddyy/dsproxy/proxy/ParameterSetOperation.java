package net.ttddyy.dsproxy.proxy;

import java.lang.reflect.Method;

/**
 * Keeps a method and its arguments when parameter-set-method is called.
 *
 * @author Tadaya Tsuyukubo
 * @see net.ttddyy.dsproxy.proxy.jdk.PreparedStatementInvocationHandler
 * @since 1.2
 */
public class ParameterSetOperation {

    /**
     * Check the given operation is {@link java.sql.CallableStatement#registerOutParameter} method by method name.
     *
     * @param operation a parameter set operation
     * @return true if it is a {@code registerOutParameter} operation
     * @since 1.4
     */
    public static boolean isRegisterOutParameterOperation(ParameterSetOperation operation) {
        String methodName = operation.getMethod().getName();
        return StatementMethodNames.PARAMETER_METHOD_REGISTER_OUT_PARAMETER.equals(methodName);
    }

    /**
     * Check the given operation is {@code setNull} method by method name.
     *
     * @param operation a parameter set operation
     * @return true if it is a {@code setNull} operation
     * @since 1.4
     */
    public static boolean isSetNullParameterOperation(ParameterSetOperation operation) {
        String methodName = operation.getMethod().getName();
        return StatementMethodNames.PARAMETER_METHOD_SET_NULL.equals(methodName);
    }

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
