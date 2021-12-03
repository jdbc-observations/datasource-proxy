package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.listener.logging.Log4jLogLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Ivan Jose Sanchez Pagador
 */
public class Log4jQueryCountLoggingServletFilter extends AbstractQueryCountLoggingServletFilter {

    protected Logger log = LogManager.getLogger(Log4jQueryCountLoggingServletFilter.class.getName());
    private Log4jLogLevel logLevel = Log4jLogLevel.DEBUG;

    @Override
    protected void initLogLevelFromFilterConfigIfSpecified(String logLevelParam) {
        Log4jLogLevel logLevel = Log4jLogLevel.nullSafeValueOf(logLevelParam);
        if (logLevel != null) {
            this.logLevel = logLevel;
        }
    }

    @Override
    protected void writeLog(String message) {
        Log4jLogUtils.writeLog(log, this.logLevel, message);
    }

    @Override
    protected void resetLogger(String loggerName) {
        this.log = LogManager.getLogger(loggerName);
    }

    public void setLogLevel(Log4jLogLevel logLevel) {
        this.logLevel = logLevel;
    }

    /**
     * Override {@link Logger} instance.
     *
     * @param log new log instance
     * @since 1.4.8
     */
    public void setLogger(Logger log) {
        this.log = log;
    }

}
