package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.support.SLF4JLogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Log slow query using SLF4j.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.1
 */
public class SLF4JSlowQueryListener extends AbstractSlowQueryLoggingListener {

    protected Logger logger = LoggerFactory.getLogger(SLF4JSlowQueryListener.class);
    protected SLF4JLogLevel logLevel = SLF4JLogLevel.WARN; // default WARN

    public SLF4JSlowQueryListener() {
    }

    public SLF4JSlowQueryListener(long threshold, TimeUnit thresholdTimeUnit) {
        this.threshold = threshold;
        this.thresholdTimeUnit = thresholdTimeUnit;
    }

    @Override
    protected void writeLog(String message) {
        SLF4JLogUtils.writeLog(logger, this.logLevel, message);
    }

    public void setLogger(String loggerName) {
        setLogger(LoggerFactory.getLogger(loggerName));
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void setLogLevel(SLF4JLogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public Logger getLogger() {
        return logger;
    }

    public SLF4JLogLevel getLogLevel() {
        return logLevel;
    }

}
