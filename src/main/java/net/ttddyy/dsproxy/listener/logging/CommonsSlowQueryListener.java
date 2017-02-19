package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.support.CommonsLogUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.TimeUnit;

/**
 * Log slow query using Commons-Logging.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.1
 */
public class CommonsSlowQueryListener extends AbstractSlowQueryLoggingListener {

    protected Log log = LogFactory.getLog(CommonsSlowQueryListener.class);
    protected CommonsLogLevel logLevel = CommonsLogLevel.WARN; // default WARN

    public CommonsSlowQueryListener() {
    }

    public CommonsSlowQueryListener(long threshold, TimeUnit thresholdTimeUnit) {
        this.threshold = threshold;
        this.thresholdTimeUnit = thresholdTimeUnit;
    }

    @Override
    protected void writeLog(String message) {
        CommonsLogUtils.writeLog(this.log, this.logLevel, message);
    }

    public void setLogLevel(CommonsLogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public void setLog(String logName) {
        setLog(LogFactory.getLog(logName));
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public Log getLog() {
        return log;
    }

    public CommonsLogLevel getLogLevel() {
        return logLevel;
    }

}
