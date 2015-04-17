package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.listener.CommonsLogLevel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Servlet Filter to log the query metrics during a http request lifecycle using Apache Commons Logging.
 *
 * <p>Some application server reuse threads without cleaning thread local values. Default, this filter clear
 * the thread local value used to hold the query metrics. If you do not want to clear the thread local value,
 * set "clearQueryCounter", a servlet parameter, to false.
 *
 * <p>Also, you can set a log level.
 *
 * @author Tadaya Tsuyukubo
 * @see CommonsQueryCountLoggingHandlerInterceptor
 * @see CommonsQueryCountLoggingRequestListener
 */
public class CommonsQueryCountLoggingServletFilter extends AbstractQueryCountLoggingServletFilter {

    private static final Log log = LogFactory.getLog(CommonsQueryCountLoggingServletFilter.class);
    private CommonsLogLevel logLevel = CommonsLogLevel.DEBUG;  // default

    public CommonsQueryCountLoggingServletFilter() {
    }

    public CommonsQueryCountLoggingServletFilter(CommonsLogLevel logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    protected void initLogLevelFromFilterConfigIfSpecified(String logLevelParam) {
        CommonsLogLevel logLevel = CommonsLogLevel.nullSafeValueOf(logLevelParam);
        if (logLevel != null) {
            this.logLevel = logLevel;
        }
    }

    @Override
    protected void writeLog(String message) {
        CommonsLogUtils.writeLog(log, this.logLevel, message);
    }

    public void setLogLevel(CommonsLogLevel logLevel) {
        this.logLevel = logLevel;
    }
}
