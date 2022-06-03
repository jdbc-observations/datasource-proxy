package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.support.Log4jLogUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Log executed query information using Apache Commons Logging log4j..
 *
 * @author Ivan Jose Sanchez Pagador
 * @since 1.8
 */
public class Log4jQueryLoggingListener extends AbstractQueryLoggingListener {

    protected Logger log = LogManager.getLogger(Log4jQueryLoggingListener.class.getName());
    protected Log4jLogLevel logLevel = Log4jLogLevel.DEBUG; // default DEBUG

    public Log4jQueryLoggingListener() {
        this.loggingCondition = new LoggingCondition() {
            @Override
            public boolean getAsBoolean() {
                switch (logLevel) {
                    case TRACE:
                        return log.isTraceEnabled();
                    case DEBUG:
                        return log.isDebugEnabled();
                    case INFO:
                        return log.isInfoEnabled();
                    case WARN:
                        return log.isWarnEnabled();
                    case ERROR:
                        return log.isErrorEnabled();
                }
                return false;
            }
        };
    }

    @Override
    protected void writeLog(String message) {
        Log4jLogUtils.writeLog(log, this.logLevel, message);
    }

    public void setLogLevel(Log4jLogLevel logLevel) {
        this.logLevel = logLevel;
    }

    /**
     * @deprecated use {{@link #setLogger(String)}}
     */
    @Override
    @Deprecated
    protected void resetLogger(String loggerName) {
        this.log = LogManager.getLogger(loggerName);
    }

    /**
     * Override {@link Logger} instance that has specified logger name.
     *
     * @param log log name
     */
    public void setLogger(String log) {
        this.log = LogManager.getLogger(log);
    }

    /**
     * Override {@link Logger} instance.
     *
     * @param log new LogManager instance
     */
    public void setLogger(Logger log) {
        this.log = log;
    }

    /**
     * @return logger
     */
    public Logger getLogger() {
        return log;
    }

    /**
     * @return log level to write
     */
    public Log4jLogLevel getLogLevel() {
        return logLevel;
    }

}
