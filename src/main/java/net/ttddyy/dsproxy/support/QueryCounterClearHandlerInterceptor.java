package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.QueryCountHolder;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Spring {@link org.springframework.web.servlet.HandlerInterceptor} to clear {@link net.ttddyy.dsproxy.QueryCount}
 * stored in thread local when {@link net.ttddyy.dsproxy.listener.DataSourceQueryCountListener} is used.
 *
 * @author Tadaya Tsuyukubo
 * @see QueryCounterClearFilter
 * @see QueryCounterClearServletRequestListener
 */
public class QueryCounterClearHandlerInterceptor extends HandlerInterceptorAdapter {

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        QueryCountHolder.clear();
    }

}