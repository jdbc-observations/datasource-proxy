package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.QueryCountHolder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * Servlet filter to clear the {@link net.ttddyy.dsproxy.QueryCount} stored in thread local at the end of the
 * http servlet request lifecycle when {@link net.ttddyy.dsproxy.listener.DataSourceQueryCountListener} is used.
 *
 * @author Tadaya Tsuyukubo
 * @see QueryCounterClearHandlerInterceptor
 * @see QueryCounterClearServletRequestListener
 */
public class QueryCounterClearFilter implements Filter {
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);

        QueryCountHolder.clear();
    }

    public void destroy() {
    }
}
