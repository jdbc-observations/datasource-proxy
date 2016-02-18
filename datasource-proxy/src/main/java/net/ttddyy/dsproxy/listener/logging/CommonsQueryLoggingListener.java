package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.support.CommonsLogUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Record executed query information using Commons-Logging.
 *
 * @author Tadaya Tsuyukubo
 */
public class CommonsQueryLoggingListener extends AbstractQueryLoggingListener {

    protected Log log = LogFactory.getLog(CommonsQueryLoggingListener.class);
    protected CommonsLogLevel logLevel = CommonsLogLevel.DEBUG; // default DEBUG

    @Override
    protected void writeLog(String message) {
        CommonsLogUtils.writeLog(log, this.logLevel, message);
    }

    public void setLogLevel(CommonsLogLevel logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    protected void resetLogger(String loggerName) {
        this.log = LogFactory.getLog(loggerName);
    }
}
