package net.ttddyy.dsproxy.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Tadaya Tsuyukubo
 */
public class MethodUtils {

    public static Object proceedExecution(Method method, Object target, Object[] args) throws Throwable {
        try {
            return method.invoke(target, args);
        }
        catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        }
    }

}
