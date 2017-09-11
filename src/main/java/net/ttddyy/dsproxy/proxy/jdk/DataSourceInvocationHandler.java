package net.ttddyy.dsproxy.proxy.jdk;

import net.ttddyy.dsproxy.proxy.DataSourceProxyLogic;
import net.ttddyy.dsproxy.proxy.ProxyConfig;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Proxy InvocationHandler for {@link javax.sql.DataSource}.
 *
 * @author Tadaya Tsuyukubo
 */
public class DataSourceInvocationHandler implements InvocationHandler {

    private DataSourceProxyLogic delegate;

    public DataSourceInvocationHandler(DataSource dataSource, ProxyConfig proxyConfig) {
        delegate = new DataSourceProxyLogic(dataSource, proxyConfig);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return delegate.invoke(method, args);
    }
}
