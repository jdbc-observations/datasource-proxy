package net.ttddyy.dsproxy.listener.lifecycle;

import javax.sql.CommonDataSource;
import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Wrapper;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Tadaya Tsuyukubo
 */
public class JdbcLifecycleEventListenerUtils {

    private static final Map<String, Method> beforeLifecycleMethodsByMethodName = new HashMap<String, Method>();
    private static final Map<String, Method> afterLifecycleMethodsByMethodName = new HashMap<String, Method>();

    static {

        Set<String> methodNames = getDeclaredMethodNames(
                Wrapper.class,
                DataSource.class, CommonDataSource.class,
                Connection.class,
                Statement.class, PreparedStatement.class, CallableStatement.class,
                ResultSet.class);

        beforeLifecycleMethodsByMethodName.putAll(getLifecycleMethodByMethodName(methodNames, true));
        afterLifecycleMethodsByMethodName.putAll(getLifecycleMethodByMethodName(methodNames, false));
    }


    /**
     * Retrieve corresponding lifecycle methods declared in {@link JdbcLifecycleEventListener}.
     *
     * <p>Format is {@code [before|after]<MethodName>}
     *
     * @param methodNames set of method names to find
     * @param isBefore    whether to retrieve before methods or after methods
     * @return a map that key is the method name string and value is lifecycle method
     */
    private static Map<String, Method> getLifecycleMethodByMethodName(Set<String> methodNames, boolean isBefore) {
        Map<String, Method> lifecycleMethodByName = new HashMap<String, Method>();
        for (Method method : JdbcLifecycleEventListener.class.getMethods()) {
            lifecycleMethodByName.put(method.getName(), method);
        }

        Map<String, Method> result = new HashMap<String, Method>();
        for (String methodName : methodNames) {
            String lifecycleMethodName = (isBefore ? "before" : "after") + capitalize(methodName);
            Method targetLifecycleMethod = lifecycleMethodByName.get(lifecycleMethodName);
            result.put(methodName, targetLifecycleMethod);
        }
        return result;
    }

    private static String capitalize(String methodName) {
        StringBuilder sb = new StringBuilder(methodName.length());
        sb.append(Character.toUpperCase(methodName.charAt(0)));
        sb.append(methodName.substring(1));
        return sb.toString();
    }

    private static Set<String> getDeclaredMethodNames(Class<?>... classes) {
        Set<String> names = new HashSet<String>();
        for (Class<?> clazz : classes) {
            names.addAll(getDeclaredMethodNames(clazz));
        }
        return names;
    }

    private static Set<String> getDeclaredMethodNames(Class<?> clazz) {
        Set<String> names = new HashSet<String>();
        for (Method method : clazz.getDeclaredMethods()) {
            names.add(method.getName());
        }
        return names;
    }


    /**
     * Find corresponding callback method on {@link JdbcLifecycleEventListener}.
     *
     * @param invokedMethodName invoked method name
     * @param isBefore          before method or not
     * @return corresponding callback method or {@code null} if not found. (e.g.: toString, hashCode)
     */
    public static Method getListenerMethod(String invokedMethodName, boolean isBefore) {
        if (isBefore) {
            return beforeLifecycleMethodsByMethodName.get(invokedMethodName);
        } else {
            return afterLifecycleMethodsByMethodName.get(invokedMethodName);
        }
    }

}
