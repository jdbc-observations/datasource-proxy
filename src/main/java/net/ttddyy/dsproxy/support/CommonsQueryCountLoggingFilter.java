package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.QueryCount;
import net.ttddyy.dsproxy.QueryCountHolder;
import net.ttddyy.dsproxy.listener.CommonsLogLevel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Servlet Filter to log the query metrics during a http request lifecycle using Apache Commons Logging.
 *
 * Some application server reuse threads without cleaning thread local values. Default, this filter clear
 * the thread local value used to hold the query metrics. If you do not want to clear the thread local value,
 * set "clearQueryCounter", a servlet parameter, to false.
 *
 * Also, you can set a log level.  
 *
 * @author Tadaya Tsuyukubo
 * @see CommonsQueryCountLoggingHandlerInterceptor
 * @see CommonsQueryCountLoggingRequestListener
 */
public class CommonsQueryCountLoggingFilter implements Filter {

    private static final String CLEAR_QUERY_COUNTER_PARAM = "clearQueryCounter";
    private static final String LOG_LEVEL_PARAM = "logLevel";

    private Log log = LogFactory.getLog(CommonsQueryCountLoggingFilter.class);

    private boolean clearQueryCounter = true;
    private CommonsLogLevel logLevel = CommonsLogLevel.DEBUG;

    public void init(FilterConfig filterConfig) throws ServletException {

        final String clearQueryCounterParam = filterConfig.getInitParameter(CLEAR_QUERY_COUNTER_PARAM);
        if (clearQueryCounterParam != null && "false".equalsIgnoreCase(clearQueryCounterParam)) {
            this.clearQueryCounter = false;
        }

        final String logLevelParam = filterConfig.getInitParameter(LOG_LEVEL_PARAM);
        final CommonsLogLevel logLevel = CommonsLogLevel.nullSafeValueOf(logLevelParam);
        if (logLevel != null) {
            this.logLevel = logLevel;
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);

        final List<String> dsNames = QueryCountHolder.getDataSourceNamesAsList();
        Collections.sort(dsNames);

        for (String dsName : dsNames) {
            final QueryCount counter = QueryCountHolder.get(dsName);
            final String message = CommonsLogUtils.getCountLogMessage(counter, dsName);
            writeLog(message);
        }

        if (clearQueryCounter) {
            QueryCountHolder.clear();
        }
    }

    private void writeLog(String message) {
        switch (logLevel) {
            case DEBUG:
                log.debug(message);
                break;
            case ERROR:
                log.error(message);
                break;
            case FATAL:
                log.fatal(message);
                break;
            case INFO:
                log.info(message);
                break;
            case TRACE:
                log.trace(message);
                break;
            case WARN:
                log.warn(message);
                break;
        }
    }


    public void destroy() {
    }

    public void setClearQueryCounter(boolean clearQueryCounter) {
        this.clearQueryCounter = clearQueryCounter;
    }

    public void setLogLevel(CommonsLogLevel logLevel) {
        this.logLevel = logLevel;
    }
}
