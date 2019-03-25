package net.ttddyy.dsproxy.listener.lifecycle;

import net.ttddyy.dsproxy.DataSourceProxyException;
import net.ttddyy.dsproxy.listener.MethodExecutionContext;
import net.ttddyy.dsproxy.listener.ProxyDataSourceListener;
import net.ttddyy.dsproxy.listener.QueryExecutionContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Holder for {@link JdbcLifecycleEventListener} and adapt it to {@link ProxyDataSourceListener}.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.5
 */
public class JdbcLifecycleEventExecutionListener implements ProxyDataSourceListener {

    private JdbcLifecycleEventListener delegate;

    public JdbcLifecycleEventExecutionListener(JdbcLifecycleEventListener delegate) {
        this.delegate = delegate;
    }

    @Override
    public void beforeMethod(MethodExecutionContext executionContext) {
        this.delegate.beforeMethod(executionContext);
        methodCallback(executionContext, true);
    }

    @Override
    public void afterMethod(MethodExecutionContext executionContext) {
        methodCallback(executionContext, false);
        this.delegate.afterMethod(executionContext);
    }

    @Override
    public void beforeQuery(QueryExecutionContext executionContext) {
        this.delegate.beforeQuery(executionContext);
    }

    @Override
    public void afterQuery(QueryExecutionContext executionContext) {
        this.delegate.afterQuery(executionContext);
    }

    private void methodCallback(MethodExecutionContext methodContext, boolean isBefore) {

        // dynamically invoke corresponding callback method on JdbcLifecycleEventListener.

        String methodName = methodContext.getMethod().getName();
        Method lifecycleMethod = JdbcLifecycleEventListenerUtils.getListenerMethod(methodName, isBefore);

        if (lifecycleMethod == null) {
            // when there is no corresponding life cycle callback, just skip it.
            // This happens when method on Object is called. e.g.: toString(), hashCode(), etc.
            return;
        }

        try {
            lifecycleMethod.invoke(this.delegate, methodContext);
        } catch (InvocationTargetException ex) {
            throw new DataSourceProxyException(ex.getTargetException());
        } catch (Exception ex) {
            throw new DataSourceProxyException(ex);
        }

    }

    public void setDelegate(JdbcLifecycleEventListener delegate) {
        this.delegate = delegate;
    }

    public JdbcLifecycleEventListener getDelegate() {
        return delegate;
    }
}
