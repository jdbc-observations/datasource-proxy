package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tadaya Tsuyukubo
 */
public class SLF4JQueryCountLoggingHandlerInterceptor extends AbstractQueryCountLoggingHandlerInterceptor {

    private Logger logger = LoggerFactory.getLogger(SLF4JQueryCountLoggingHandlerInterceptor.class);
    private SLF4JLogLevel logLevel = SLF4JLogLevel.DEBUG;

    public SLF4JQueryCountLoggingHandlerInterceptor() {
    }

    public SLF4JQueryCountLoggingHandlerInterceptor(SLF4JLogLevel logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    protected void writeLog(String logEntry) {
        SLF4JLogUtils.writeLog(logger, logLevel, logEntry);
    }

    public void setLogLevel(SLF4JLogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public void setLogName(String logName) {
        logger = LoggerFactory.getLogger(logName);
    }

}
