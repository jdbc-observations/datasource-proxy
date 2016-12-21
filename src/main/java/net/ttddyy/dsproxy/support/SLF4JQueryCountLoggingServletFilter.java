package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tadaya Tsuyukubo
 */
public class SLF4JQueryCountLoggingServletFilter extends AbstractQueryCountLoggingServletFilter {

    private Logger logger = LoggerFactory.getLogger(SLF4JQueryCountLoggingServletFilter.class);
    private SLF4JLogLevel logLevel = SLF4JLogLevel.DEBUG;

    @Override
    protected void initLogLevelFromFilterConfigIfSpecified(String logLevelParam) {
        SLF4JLogLevel logLevel = SLF4JLogLevel.nullSafeValueOf(logLevelParam);
        if (logLevel != null) {
            this.logLevel = logLevel;
        }
    }

    @Override
    protected void writeLog(String message) {
        SLF4JLogUtils.writeLog(logger, this.logLevel, message);
    }

    public void setLogLevel(SLF4JLogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public void setLogName(String logName) {
        logger = LoggerFactory.getLogger(logName);
    }

}
