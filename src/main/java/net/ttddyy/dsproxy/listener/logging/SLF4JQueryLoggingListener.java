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

    public SLF4JQueryLoggingListener() {
        this.loggingCondition = new LoggingCondition() {
            @Override
            public boolean getAsBoolean() {
                switch (logLevel) {
                    case TRACE:
                        return logger.isTraceEnabled();
                    case DEBUG:
                        return logger.isDebugEnabled();
                    case INFO:
                        return logger.isInfoEnabled();
                    case WARN:
                        return logger.isWarnEnabled();
                    case ERROR:
                        return logger.isErrorEnabled();
                }
                return false;
            }
        };
    }

    @Override
    protected void writeLog(String message) {
        SLF4JLogUtils.writeLog(logger, this.logLevel, message);
    }

    public void setLogLevel(SLF4JLogLevel logLevel) {
        this.logLevel = logLevel;
    }

    /**
     * @deprecated use {{@link #setLogger(String)}}
     */
    @Override
    @Deprecated
    protected void resetLogger(String loggerName) {
        this.logger = LoggerFactory.getLogger(loggerName);
    }

    /**
     * Override {@link Logger} instance that has specified logger name.
     *
     * @param loggerName logger name
     * @since 1.4.1
     */
    public void setLogger(String loggerName) {
        this.logger = LoggerFactory.getLogger(loggerName);
    }

    /**
     * Override {@link Logger} instance.
     *
     * @param logger new logger instance
     * @since 1.4.1
     */
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    /**
     * @return logger
     * @since 1.4.1
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * @return log level to write
     * @since 1.4.1
     */
    public SLF4JLogLevel getLogLevel() {
        return logLevel;
    }

}
