package net.ttddyy.dsproxy.proxy;

import java.lang.reflect.Method;

/**
 * Proxy logic for {@link java.sql.ResultSet}.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.3
 */
public interface ResultSetProxyLogic {

    Object invoke(Method method, Object[] args) throws Throwable;

}
