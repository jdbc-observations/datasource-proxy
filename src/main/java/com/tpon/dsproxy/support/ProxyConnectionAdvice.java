package com.tpon.dsproxy.support;

import com.tpon.dsproxy.proxy.JdbcProxyFactory;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.sql.Connection;

/**
 * Support injecting proxies by AOP. 
 *
 * @author Tadaya Tsuyukubo
 */
public class ProxyConnectionAdvice implements MethodInterceptor {
    public Object invoke(MethodInvocation invocation) throws Throwable {

        Object retVal = invocation.proceed();

        // only when return value is connection, return proxy.
        if (!(retVal instanceof Connection)) {
            return retVal;
        }

        return JdbcProxyFactory.createConnection((Connection) retVal, null);
    }

}
