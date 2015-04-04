package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.QueryCount;
import net.ttddyy.dsproxy.QueryCountHolder;

import javax.servlet.*;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Servlet filter to output query statistics.
 *
 * <p>By default, after logging the query count, it resets the {@link net.ttddyy.dsproxy.QueryCountHolder}, so that
 * each http request will have fresh set of query statistics.
 * If you want to manage when to reset the counter, you can disable this filter to clear the counter by setting
 * filter parameter <em>clearQueryCounter</em> to {@code false}.
 *
 * <pre>
 * {@code
 *   <filter>
 *     <filter-name>count</filter-name>
 *     <filter-class>net.ttddyy.dsproxy.support.CommonsQueryCountLoggingFilter</filter-class>
 *     <init-param>
 *       <param-name>logLevel</param-name>
 *       <param-value>INFO</param-value>
 *     <init-param>
 *     <init-param>
 *       <param-name>clearQueryCounter</param-name>
 *       <param-value>false</param-value>
 *     <init-param>
 *   </filter>
 * }
 * </pre>
 *
 * @author Tadaya Tsuyukubo
 * @see net.ttddyy.dsproxy.support.CommonsQueryCountLoggingFilter
 * @see net.ttddyy.dsproxy.support.SLF4JQueryCountLoggingFilter
 * @since 1.3
 */
public abstract class AbstractQueryCountLoggingFilter implements Filter {
    public static final String CLEAR_QUERY_COUNTER_PARAM = "clearQueryCounter";
    public static final String LOG_LEVEL_PARAM = "logLevel";

    protected boolean clearQueryCounter = true;
    protected QueryCountLogFormatter logFormatter = new DefaultQueryCountLogFormatter();


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        String clearQueryCounterParam = filterConfig.getInitParameter(CLEAR_QUERY_COUNTER_PARAM);
        if (clearQueryCounterParam != null && "false".equalsIgnoreCase(clearQueryCounterParam)) {
            this.clearQueryCounter = false;
        }

        String logLevelParam = filterConfig.getInitParameter(LOG_LEVEL_PARAM);
        if (logLevelParam != null) {
            initLogLevelFromFilterConfigIfSpecified(logLevelParam);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);

        final List<String> dsNames = QueryCountHolder.getDataSourceNamesAsList();
        Collections.sort(dsNames);

        for (String dsName : dsNames) {
            QueryCount count = QueryCountHolder.get(dsName);
            String message = logFormatter.getLogMessage(dsName, count);
            writeLog(message);
        }

        if (clearQueryCounter) {
            QueryCountHolder.clear();
        }
    }

    @Override
    public void destroy() {
    }

    protected abstract void initLogLevelFromFilterConfigIfSpecified(String logLevelParam);

    protected abstract void writeLog(String message);

    public void setClearQueryCounter(boolean clearQueryCounter) {
        this.clearQueryCounter = clearQueryCounter;
    }

    public void setLogFormatter(QueryCountLogFormatter logFormatter) {
        this.logFormatter = logFormatter;
    }

}
