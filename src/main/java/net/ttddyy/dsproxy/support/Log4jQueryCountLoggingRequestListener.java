package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.listener.logging.Log4jLogLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequestEvent;

/**
 * @author Ivan Jose Sanchez Pagador
 */
public class Log4jQueryCountLoggingRequestListener extends AbstractQueryCountLoggingRequestListener {

    private static final String LOG_LEVEL_PARAM = "queryCountLog4jLogLevel";
    private static final Log4jLogLevel DEFAULT_LOG_LEVEL = Log4jLogLevel.DEBUG;
    private Logger log = LogManager.getLogger(Log4jQueryCountLoggingRequestListener.class.getName());


    @Override
    protected void writeLog(ServletRequestEvent servletRequestEvent, String logEntry) {
        ServletContext context = servletRequestEvent.getServletContext();
        String logLevelParam = context.getInitParameter(LOG_LEVEL_PARAM);
        Log4jLogLevel logLevel = Log4jLogLevel.nullSafeValueOf(logLevelParam);
        if (logLevel == null) {
            logLevel = DEFAULT_LOG_LEVEL;
        }

        Log4jLogUtils.writeLog(log, logLevel, logEntry);
    }

    /**
     * Override {@link Logger} instance.
     *
     * @param logger new log instance
     * @since 1.4.8
     */
    public void setLogger(Logger logger) {
        this.log = logger;
    }

}
