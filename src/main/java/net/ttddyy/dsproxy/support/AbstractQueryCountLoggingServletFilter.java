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
 * <p><em>loggerName</em> parameter(Optional): create a logger instance by this name if specified.
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
 *     <init-param>
 *       <param-name>loggerName</param-name>
 *       <param-value>myLogger</param-value>
 *     <init-param>
 *   </filter>
 * }
 * </pre>
 *
 * @author Tadaya Tsuyukubo
 * @see CommonsQueryCountLoggingServletFilter
 * @see SLF4JQueryCountLoggingServletFilter
 * @since 1.3
 */
public abstract class AbstractQueryCountLoggingServletFilter implements Filter {
    public static final String CLEAR_QUERY_COUNTER_PARAM = "clearQueryCounter";
    public static final String LOG_LEVEL_PARAM = "logLevel";
    public static final String FORMAT_PARAM = "format";
    public static final String LOGGER_NAME = "loggerName";

    protected boolean clearQueryCounter = true;
    protected boolean writeAsJson = false;
    protected QueryCountLogEntryCreator logFormatter = new DefaultQueryCountLogEntryCreator();


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        String clearQueryCounterParam = filterConfig.getInitParameter(CLEAR_QUERY_COUNTER_PARAM);
        if (clearQueryCounterParam != null && "false".equalsIgnoreCase(clearQueryCounterParam)) {
            this.clearQueryCounter = false;
        }

        String loggerName = filterConfig.getInitParameter(LOGGER_NAME);
        if (loggerName != null) {
            resetLogger(loggerName);
        }

        String logLevelParam = filterConfig.getInitParameter(LOG_LEVEL_PARAM);
        if (logLevelParam != null) {
            initLogLevelFromFilterConfigIfSpecified(logLevelParam);
        }

        String format = filterConfig.getInitParameter(FORMAT_PARAM);
        if (format != null && "json".equalsIgnoreCase(format)) {
            this.writeAsJson = true;
        }

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);

        final List<String> dsNames = QueryCountHolder.getDataSourceNamesAsList();
        Collections.sort(dsNames);

        for (String dsName : dsNames) {
            QueryCount count = QueryCountHolder.get(dsName);
            String message;
            if (this.writeAsJson) {
                message = this.logFormatter.getLogMessageAsJson(dsName, count);
            } else {
                message = this.logFormatter.getLogMessage(dsName, count);
            }
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

    /**
     * Callback method to reset the logger object in concrete class when log name is specified.
     *
     * @param loggerName logger name
     * @since 1.4.1
     */
    protected abstract void resetLogger(String loggerName);


    public void setClearQueryCounter(boolean clearQueryCounter) {
        this.clearQueryCounter = clearQueryCounter;
    }

    public void setLogFormatter(QueryCountLogEntryCreator logFormatter) {
        this.logFormatter = logFormatter;
    }

    public void setWriteAsJson(boolean writeAsJson) {
        this.writeAsJson = writeAsJson;
    }
}
