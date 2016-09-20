package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.support.SLF4JLogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Log executed query information using SLF4J.
 *
 * @author Tadaya Tsuyukubo
 */
public class SLF4JQueryLoggingListener extends AbstractQueryLoggingListener {

    protected Logger logger = LoggerFactory.getLogger(SLF4JQueryLoggingListener.class);
    protected SLF4JLogLevel logLevel = SLF4JLogLevel.DEBUG; // default DEBUG

    @Override
    protected void writeLog(String message) {
        SLF4JLogUtils.writeLog(logger, this.logLevel, message);
    }

    public void setLogLevel(SLF4JLogLevel logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    protected void resetLogger(String loggerName) {
        this.logger = LoggerFactory.getLogger(loggerName);
    }
}
