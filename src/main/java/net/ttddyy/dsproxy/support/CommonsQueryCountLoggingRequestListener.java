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
    private QueryCountLogFormatter logFormatter = new DefaultQueryCountLogFormatter();
    private boolean writeAsJson = false;

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
            QueryCount count = QueryCountHolder.get(dsName);
            String message;
            if (this.writeAsJson) {
                message = logFormatter.getLogMessageAsJson(dsName, count);
            } else {
                message = logFormatter.getLogMessage(dsName, count);
            }

            CommonsLogUtils.writeLog(log, logLevel, message);
        }

        QueryCountHolder.clear();
    }

    public void setLogFormatter(QueryCountLogFormatter logFormatter) {
        this.logFormatter = logFormatter;
    }

    public void setWriteAsJson(boolean writeAsJson) {
        this.writeAsJson = writeAsJson;
    }
}
