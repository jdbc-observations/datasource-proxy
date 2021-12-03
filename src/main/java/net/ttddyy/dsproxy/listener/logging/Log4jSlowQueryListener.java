package net.ttddyy.dsproxy.listener.logging;


import net.ttddyy.dsproxy.support.Log4jLogUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

/**
 * Log slow query using Apache Commons Logging log4j.
 *
 * @author Ivan Jose Sanchez Pagador
 */
public class Log4jSlowQueryListener extends AbstractSlowQueryLoggingListener {

    protected Logger logger = LogManager.getLogger(Log4jSlowQueryListener.class);
    protected Log4jLogLevel logLevel = Log4jLogLevel.WARN; // default WARN

    public Log4jSlowQueryListener() {
    }

    public Log4jSlowQueryListener(long threshold, TimeUnit thresholdTimeUnit) {
        this.threshold = threshold;
        this.thresholdTimeUnit = thresholdTimeUnit;
    }

    @Override
    protected void writeLog(String message) {
        Log4jLogUtils.writeLog(logger, this.logLevel, message);
    }

    public void setLogger(String loggerName) {
        setLogger(LogManager.getLogger(loggerName));
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void setLogLevel(Log4jLogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public Logger getLogger() {
        return logger;
    }

    public Log4jLogLevel getLogLevel() {
        return logLevel;
    }

}
