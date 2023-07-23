package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.listener.MethodExecutionListenerUtils;
import org.springframework.jdbc.support.rowset.ResultSetWrappingSqlRowSet;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Delegate method calls to the {@link ResultSetWrappingSqlRowSet}.
 * <br/>
 * Provides support for JDBC 4.1 methods:
 * <ul>
 *     <li>{@link com.sun.rowset.CachedRowSetImpl#getObject(int, Class)}</li>
 *     <li>{@link com.sun.rowset.CachedRowSetImpl#getObject(String, Class)}</li>
 * </ul>
 *
 * @author a-simeshin
 * @since 1.10
 */
@SuppressWarnings("Convert2Lambda")
public class SpringSqlRowSetProxyLogic implements ResultSetProxyLogic {

    private final ResultSet originalRowSet;
    private final ResultSetWrappingSqlRowSet springRowSet;
    private final ConnectionInfo connectionInfo;
    private final ProxyConfig proxyConfig;

    protected boolean supportIsClosedMethod = true;
    protected boolean isClosed;

    public SpringSqlRowSetProxyLogic(ResultSet originalRowSet,
                                     ResultSetWrappingSqlRowSet springRowSet,
                                     ConnectionInfo connectionInfo,
                                     ProxyConfig proxyConfig) {
        this.originalRowSet = originalRowSet;
        this.springRowSet = springRowSet;
        this.connectionInfo = connectionInfo;
        this.proxyConfig = proxyConfig;
    }

    /**
     * Invokes a method on the SqlRowSet proxy while listening to method execution through a callback.
     *
     * @param method The method being called on the SqlRowSet proxy.
     * @param args The arguments passed to the method.
     * @return The result of the method call.
     * @throws Throwable If any error or exception occurs during the method execution.
     */
    @Override
    public Object invoke(Method method, Object[] args) throws Throwable {
        return MethodExecutionListenerUtils.invoke(new MethodExecutionListenerUtils.MethodExecutionCallback() {
            @Override
            public Object execute(Object proxyTarget, Method method, Object[] args) throws Throwable {
                return performQueryExecutionListener(method, args);
            }
        }, this.proxyConfig, this.springRowSet, this.connectionInfo, method, args);
    }

    /**
     * Performs query execution listener for a method call on the SqlRowSet proxy.
     *
     * @param method The method being called on the SqlRowSet proxy.
     * @param args The arguments passed to the method.
     * @return The result of the method call.
     * @throws Throwable If any error or exception occurs during the method execution.
     */
    private Object performQueryExecutionListener(Method method, Object[] args) throws Throwable {
        //TODO using switch - need JDK COMPATIBLE
        final String methodName = method.getName();

        if ("toString".equals(methodName)) {
            return this.originalRowSet.getClass().getSimpleName() + " [" + this.originalRowSet + "]";
        }
        if ("hashCode".equals(methodName)) {
            return this.originalRowSet.hashCode();
        }
        if ("equals".equals(methodName)) {
            return this.originalRowSet.equals(args[0]);
        }
        if ("close".equals(methodName)) {
            // 🤯🤯🤯
            this.isClosed = true;
            return null;
        }
        if (this.supportIsClosedMethod && "isClosed".equals(methodName)) {
            return this.isClosed;
        }
        if ("getTarget".equals(methodName)) {
            return this.originalRowSet;
        }
        if ("getMetaData".equals(methodName)) {
            return this.originalRowSet.getMetaData();
        }

        try {
            final Method newMethod = convertResultSetMethodToSpringSqlRowSetMethod(method);
            return MethodUtils.proceedExecution(newMethod, this.springRowSet, args);
        } catch (Throwable throwable) {
            if (throwable instanceof SQLException) {
                throw throwable;
            }
            final String reason = String.format("SqlRowSet threw exception: %s", throwable);
            throw new SQLException(reason, throwable);
        }
    }

    /**
     * Converts a method from the original ResultSet-based interface to the corresponding method
     * in the Spring SqlRowSet interface, using reflection.
     *
     * @param originalMethod The method to be converted from the ResultSet-based interface.
     * @return The corresponding method from the Spring SqlRowSet interface, if found.
     * @throws IllegalStateException If the corresponding method cannot be found in the Spring SqlRowSet interface.
     */
    private Method convertResultSetMethodToSpringSqlRowSetMethod(final Method originalMethod) {
        final String originalMethodName = originalMethod.getName();
        final Class<?>[] originalMethodParameterTypes = originalMethod.getParameterTypes();
        final Method[] methods = this.springRowSet.getClass().getMethods();

        for (Method method : methods) {
            if (method.getName().equals(originalMethodName)) {
                final Class<?>[] methodParameterTypes = method.getParameterTypes();
                if (methodParameterTypes.length == originalMethodParameterTypes.length) {
                    boolean match = true;
                    for (int i = 0; i < originalMethodParameterTypes.length; i++) {
                        if (!methodParameterTypes[i].isAssignableFrom(originalMethodParameterTypes[i])) {
                            match = false;
                            break;
                        }
                    }
                    if (match) {
                        return method;
                    }
                }
            }
        }

        throw new IllegalStateException(
                "Cannot find proxy method "
                        + originalMethodName
                        + " and types "
                        + Arrays.toString(originalMethodParameterTypes)
                        + " for class: "
                        + springRowSet.getClass().getName());
    }

}
