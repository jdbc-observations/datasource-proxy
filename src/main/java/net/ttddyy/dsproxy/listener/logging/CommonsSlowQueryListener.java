package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.support.CommonsLogUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Log slow query using Commons-Logging.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.1
 */
public class CommonsSlowQueryListener extends AbstractSlowQueryLoggingListener {

    protected Log log = LogFactory.getLog(CommonsQueryLoggingListener.class);
    protected CommonsLogLevel logLevel = CommonsLogLevel.DEBUG; // default DEBUG

    @Override
    protected void writeLog(String message) {
        CommonsLogUtils.writeLog(this.log, this.logLevel, message);
    }

    public void setLogLevel(CommonsLogLevel logLevel) {
        this.logLevel = logLevel;
    }

    protected void setLog(String logName) {
        setLog(LogFactory.getLog(logName));
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public Log getLog() {
        return log;
    }

}
