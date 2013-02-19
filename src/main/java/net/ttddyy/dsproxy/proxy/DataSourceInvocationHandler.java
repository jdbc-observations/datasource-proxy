package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.transform.QueryTransformer;

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

    public DataSourceInvocationHandler() {
    }

    @Deprecated
    public DataSourceInvocationHandler(DataSource dataSource, QueryExecutionListener listener, String dataSourceName,
                                       JdbcProxyFactory jdbcProxyFactory) {
        InterceptorHolder interceptorHolder = new InterceptorHolder(listener, QueryTransformer.DEFAULT);
        delegate = new DataSourceProxyLogic(dataSource, interceptorHolder, dataSourceName, jdbcProxyFactory);
    }

    public DataSourceInvocationHandler(DataSource dataSource, InterceptorHolder interceptorHolder, String dataSourceName,
                                       JdbcProxyFactory jdbcProxyFactory) {
        delegate = new DataSourceProxyLogic(dataSource, interceptorHolder, dataSourceName, jdbcProxyFactory);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return delegate.invoke(proxy, method, args);
    }
}
