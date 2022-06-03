package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.listener.logging.Log4jLogLevel;
import net.ttddyy.dsproxy.listener.logging.Log4jQueryLoggingListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Ivan Jose Sanchez Pagador
 * @since 1.8
 */
public class Log4jQueryCountLoggingHandlerInterceptor extends AbstractQueryCountLoggingHandlerInterceptor {

    protected org.apache.logging.log4j.Logger log = LogManager.getLogger(Log4jQueryCountLoggingHandlerInterceptor.class.getName());
    protected Log4jLogLevel logLevel = Log4jLogLevel.DEBUG; // default DEBUG

    public Log4jQueryCountLoggingHandlerInterceptor() {
    }

    public Log4jQueryCountLoggingHandlerInterceptor(Log4jLogLevel logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    protected void writeLog(String logEntry) {
        Log4jLogUtils.writeLog(log, logLevel, logEntry);
    }

    public void setLogLevel(Log4jLogLevel logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    protected void resetLogger(String loggerName) {
        this.log = LogManager.getLogger(loggerName);
    }

    /**
     * Override {@link Logger} instance.
     *
     * @param logger new log instance
     */
    public void setLogger(Logger logger) {
        this.log = logger;
    }

}
