package net.ttddyy.dsproxy.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author Tadaya Tsuyukubo
 * @since 2.0
 */
public interface ProxyLogic {

    /**
     * Analogous to {@link InvocationHandler#invoke(Object, Method, Object[])}
     *
     * @param proxy  proxy object
     * @param method invoked method
     * @param args   invoked method args
     * @return result of invocation
     * @throws Throwable any error
     */
    Object invoke(Object proxy, Method method, Object[] args) throws Throwable;

}
