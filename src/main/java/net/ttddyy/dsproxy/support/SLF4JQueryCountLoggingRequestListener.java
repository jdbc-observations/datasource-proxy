package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.QueryCount;
import net.ttddyy.dsproxy.QueryCountHolder;
import net.ttddyy.dsproxy.listener.SLF4JLogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import java.util.Collections;
import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 */
public class SLF4JQueryCountLoggingRequestListener implements ServletRequestListener {

    private static final String LOG_LEVEL_PARAM = "queryCountSLF4JLogLevel";
    private static final SLF4JLogLevel DEFAULT_LOG_LEVEL = SLF4JLogLevel.DEBUG;

    private Logger logger = LoggerFactory.getLogger(SLF4JQueryCountLoggingRequestListener.class);
    private QueryCountLogFormatter logFormatter = new DefaultQueryCountLogFormatter();

    public void requestInitialized(ServletRequestEvent sre) {
    }

    public void requestDestroyed(ServletRequestEvent sre) {

        final ServletContext context = sre.getServletContext();
        final String logLevelParam = context.getInitParameter(LOG_LEVEL_PARAM);
        SLF4JLogLevel logLevel = SLF4JLogLevel.nullSafeValueOf(logLevelParam);
        if (logLevel == null) {
            logLevel = DEFAULT_LOG_LEVEL;
        }

        final List<String> dsNames = QueryCountHolder.getDataSourceNamesAsList();
        Collections.sort(dsNames);

        for (String dsName : dsNames) {
            final QueryCount count = QueryCountHolder.get(dsName);
            final String message = logFormatter.getLogMessage(dsName, count);
            SLF4JLogUtils.writeLog(logger, logLevel, message);
        }

        QueryCountHolder.clear();

    }

    public void setLogFormatter(QueryCountLogFormatter logFormatter) {
        this.logFormatter = logFormatter;
    }
}
