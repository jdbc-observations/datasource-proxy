package net.ttddyy.dsproxy.proxy;

import java.lang.reflect.Method;
import java.sql.ResultSet;

/**
 * Simply delegate method calls to the actual {@link ResultSet}.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.3
 */
public class SimpleResultSetProxyLogic implements ResultSetProxyLogic {

    private ResultSet resultSet;

    public SimpleResultSetProxyLogic(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    @Override
    public Object invoke(Method method, Object[] args) throws Throwable {
        final String methodName = method.getName();

        // special treat for toString method
        if ("toString".equals(methodName)) {
            final StringBuilder sb = new StringBuilder();
            sb.append(this.resultSet.getClass().getSimpleName());
            sb.append(" [");
            sb.append(this.resultSet.toString());
            sb.append("]");
            return sb.toString(); // differentiate toString message.
        } else if ("getTarget".equals(methodName)) {
            // ProxyJdbcObject interface has a method to return original object.
            return this.resultSet;
        }

        return MethodUtils.proceedExecution(method, this.resultSet, args);
    }
}
