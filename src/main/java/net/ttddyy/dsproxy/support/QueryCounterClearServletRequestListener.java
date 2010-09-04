package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.QueryCountHolder;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

/**
 * {@link javax.servlet.ServletRequestListener} to clear {@link net.ttddyy.dsproxy.QueryCount} stored in
 * thread local when {@link net.ttddyy.dsproxy.listener.DataSourceQueryCountListener} is used.
 *
 * @author Tadaya Tsuyukubo
 * @see QueryCounterClearFilter
 * @see QueryCounterClearHandlerInterceptor
 */
public class QueryCounterClearServletRequestListener implements ServletRequestListener {

    public void requestInitialized(ServletRequestEvent sre) {
    }

    public void requestDestroyed(ServletRequestEvent sre) {
        QueryCountHolder.clear();
    }

}
