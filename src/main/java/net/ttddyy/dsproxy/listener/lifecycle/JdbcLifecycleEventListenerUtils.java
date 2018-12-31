package net.ttddyy.dsproxy.listener.lifecycle;

import net.ttddyy.dsproxy.DataSourceProxyException;

import javax.sql.CommonDataSource;
import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Wrapper;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tadaya Tsuyukubo
 */
public class JdbcLifecycleEventListenerUtils {

    private static final Map<Method, Method> beforeMethods = new HashMap<Method, Method>();
    private static final Map<Method, Method> afterMethods = new HashMap<Method, Method>();

    // special handling for Wrapper methods
    private static final Map<Class<?>, Method> beforeMethodForUnwrap = new HashMap<Class<?>, Method>();
    private static final Map<Class<?>, Method> afterMethodForUnwrap = new HashMap<Class<?>, Method>();
    private static final Map<Class<?>, Method> beforeMethodForIsWrapperFor = new HashMap<Class<?>, Method>();
    private static final Map<Class<?>, Method> afterMethodForIsWrapperFor = new HashMap<Class<?>, Method>();

    static {
        init();
    }

    private static void init() {
        List<Class<? extends Wrapper>> proxiedClasses = Arrays.asList(DataSource.class, Connection.class,
                Statement.class, PreparedStatement.class, CallableStatement.class, ResultSet.class);


        Map<String, Method> lifeCycleMethodByName = new HashMap<String, Method>();
        // method defined in Object will not be part of this. (see getMethods() javadoc)
        for (Method method : JdbcLifecycleEventListener.class.getMethods()) {
            lifeCycleMethodByName.put(method.getName(), method);
        }

        for (Class<? extends Wrapper> proxiedClass : proxiedClasses) {
            for (Method method : proxiedClass.getMethods()) {
                boolean isWrapperMethod = method.getDeclaringClass() == Wrapper.class;

                String beforeMethodName = getTargetMethodName(method, proxiedClass, true);
                String afterMethodName = getTargetMethodName(method, proxiedClass, false);

                Method beforeMethod = lifeCycleMethodByName.get(beforeMethodName);
                Method afterMethod = lifeCycleMethodByName.get(afterMethodName);

                // populate method-to-method cache
                if (isWrapperMethod) {
                    if ("unwrap".equals(method.getName())) {
                        beforeMethodForUnwrap.put(proxiedClass, beforeMethod);
                        afterMethodForUnwrap.put(proxiedClass, afterMethod);
                    } else {
                        beforeMethodForIsWrapperFor.put(proxiedClass, beforeMethod);
                        afterMethodForIsWrapperFor.put(proxiedClass, afterMethod);
                    }
                } else {
                    beforeMethods.put(method, beforeMethod);
                    afterMethods.put(method, afterMethod);
                }
            }
        }
    }

    /**
     * Find corresponding callback method on {@link JdbcLifecycleEventListener}.
     *
     * @param invokedMethod invoked method
     * @param proxyTarget   proxy target
     * @param isBefore      before method or not
     * @return corresponding callback method
     */
    public static Method getListenerMethod(Method invokedMethod, Object proxyTarget, boolean isBefore) {

        Class<?> declaringClass = invokedMethod.getDeclaringClass();
        boolean isWrapperMethod = declaringClass == Wrapper.class;

        if (isWrapperMethod) {
            Class<?> key;
            if (proxyTarget instanceof DataSource) {
                key = DataSource.class;
            } else if (proxyTarget instanceof Connection) {
                key = Connection.class;
            } else if (proxyTarget instanceof CallableStatement) {
                key = CallableStatement.class;
            } else if (proxyTarget instanceof PreparedStatement) {
                key = PreparedStatement.class;
            } else if (proxyTarget instanceof Statement) {
                key = Statement.class;
            } else if (proxyTarget instanceof ResultSet) {
                key = ResultSet.class;
            } else {
                throw new DataSourceProxyException("Unknown target type. proxyTarget=" + proxyTarget);
            }

            if ("unwrap".equals(invokedMethod.getName())) {
                if (isBefore) {
                    return beforeMethodForUnwrap.get(key);
                } else {
                    return afterMethodForUnwrap.get(key);
                }
            } else {
                if (isBefore) {
                    return beforeMethodForIsWrapperFor.get(key);
                } else {
                    return afterMethodForIsWrapperFor.get(key);
                }
            }
        }

        if (isBefore) {
            return beforeMethods.get(invokedMethod);
        } else {
            return afterMethods.get(invokedMethod);
        }

    }

    public static String getTargetMethodName(Method invokedMethod, Class<?> proxiedClass, boolean isBefore) {
        String methodName = invokedMethod.getName();

        // Generate "beforeXxxOnYyy" or "afterXxxOnYyy"

        StringBuilder sb = new StringBuilder();
        if (isBefore) {
            sb.append("before");
        } else {
            sb.append("after");
        }


        if (isWrapperMethod(methodName)) {
            // special handling for Wrapper methods
            if ("unwrap".equals(methodName)) {
                sb.append("UnwrapOn");
            } else {
                sb.append("IsWrapperForOn");
            }
            if (proxiedClass == DataSource.class) {
                sb.append("DataSource");
            } else if (proxiedClass == Connection.class) {
                sb.append("Connection");
            } else if (proxiedClass == CallableStatement.class) {
                sb.append("CallableStatement");
            } else if (proxiedClass == PreparedStatement.class) {
                sb.append("PreparedStatement");
            } else if (proxiedClass == Statement.class) {
                sb.append("Statement");
            } else if (proxiedClass == ResultSet.class) {
                sb.append("ResultSet");
            }
            return sb.toString();
        }


        sb.append(Character.toUpperCase(methodName.charAt(0)));
        sb.append(methodName.substring(1));
        sb.append("On");

        Class<?> declaringClass = invokedMethod.getDeclaringClass();
        if (declaringClass == CommonDataSource.class) {
            sb.append("DataSource");
        } else {
            sb.append(declaringClass.getSimpleName());
        }

        return sb.toString();
    }

    private static boolean isWrapperMethod(String methodName) {
        return "unwrap".equals(methodName) || "isWrapperFor".equals(methodName);
    }
}
