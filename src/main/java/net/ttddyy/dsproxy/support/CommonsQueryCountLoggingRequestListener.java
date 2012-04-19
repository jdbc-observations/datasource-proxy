package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.QueryCount;
import net.ttddyy.dsproxy.QueryCountHolder;
import net.ttddyy.dsproxy.listener.CommonsLogLevel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import java.util.Collections;
import java.util.List;

/**
 * {@link javax.servlet.ServletRequestListener} to log the query metrics during a http request lifecycle
 * using Apache Commons Logging.
 *
 * @author Tadaya Tsuyukubo
 * @see CommonsQueryCountLoggingFilter
 * @see CommonsQueryCountLoggingHandlerInterceptor
 */
public class CommonsQueryCountLoggingRequestListener implements ServletRequestListener {

    private static final String LOG_LEVEL_PARAM = "queryCountCommonsLogLevel";
    private static final CommonsLogLevel DEFAULT_LOG_LEVEL = CommonsLogLevel.DEBUG;

    private Log log = LogFactory.getLog(CommonsQueryCountLoggingRequestListener.class);

    public void requestInitialized(ServletRequestEvent sre) {
    }

    public void requestDestroyed(ServletRequestEvent sre) {
        final ServletContext context = sre.getServletContext();
        final String logLevelParam = context.getInitParameter(LOG_LEVEL_PARAM);
        CommonsLogLevel logLevel = CommonsLogLevel.nullSafeValueOf(logLevelParam);
        if (logLevel == null) {
            logLevel = DEFAULT_LOG_LEVEL;
        }

        final List<String> dsNames = QueryCountHolder.getDataSourceNamesAsList();
        Collections.sort(dsNames);

        for (String dsName : dsNames) {
            final QueryCount counter = QueryCountHolder.get(dsName);
            final String message = CommonsLogUtils.getCountLogMessage(counter, dsName);
            writeLog(logLevel, message);
        }

        QueryCountHolder.clear();
    }

    private void writeLog(CommonsLogLevel logLevel, String message) {
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


}
