package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.listener.CommonsLogLevel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequestEvent;

/**
 * {@link javax.servlet.ServletRequestListener} to log the query metrics during a http request lifecycle
 * using Apache Commons Logging.
 *
 * @author Tadaya Tsuyukubo
 * @see CommonsQueryCountLoggingFilter
 * @see CommonsQueryCountLoggingHandlerInterceptor
 */
public class CommonsQueryCountLoggingRequestListener extends AbstractQueryCountLoggingRequestListener {

    private static final String LOG_LEVEL_PARAM = "queryCountCommonsLogLevel";
    private static final CommonsLogLevel DEFAULT_LOG_LEVEL = CommonsLogLevel.DEBUG;

    private Log log = LogFactory.getLog(CommonsQueryCountLoggingRequestListener.class);

    @Override
    protected void writeLog(ServletRequestEvent servletRequestEvent, String logEntry) {

        ServletContext context = servletRequestEvent.getServletContext();
        String logLevelParam = context.getInitParameter(LOG_LEVEL_PARAM);
        CommonsLogLevel logLevel = CommonsLogLevel.nullSafeValueOf(logLevelParam);
        if (logLevel == null) {
            logLevel = DEFAULT_LOG_LEVEL;
        }

        CommonsLogUtils.writeLog(log, logLevel, logEntry);
    }

}
