package net.ttddyy.dsproxy.proxy;

import org.jspecify.annotations.Nullable;

import java.lang.reflect.Method;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4.6
 */
public class ReflectionUtils {

    public static @Nullable Method getMethodIfAvailable(Class<?> clazz, String name, Class... parameterTypes) {
        try {
            return clazz.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

}
