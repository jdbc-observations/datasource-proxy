package net.ttddyy.dsproxy.proxy;

import java.sql.SQLException;
import java.sql.Wrapper;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Tadaya Tsuyukubo
 * @since 2.0
 */
public abstract class CallbackSupport {

    protected static final Set<String> WRAPPER_METHODS = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList("unwrap", "isWrapperFor"))
    );


    // handle wrapper methods
    protected boolean isWrapperMethods(String methodName) {
        return WRAPPER_METHODS.contains(methodName);
    }

    /**
     * Handling for {@link Wrapper#unwrap(Class)} and {@link Wrapper#isWrapperFor(Class)}.
     */
    protected Object handleWrapperMethods(String methodName, Wrapper wrapper, Object[] args) throws SQLException {
        final Class<?> clazz = (Class<?>) args[0];
        if ("unwrap".equals(methodName)) {
            return wrapper.unwrap(clazz);
        } else {
            return wrapper.isWrapperFor(clazz);
        }
    }
}
