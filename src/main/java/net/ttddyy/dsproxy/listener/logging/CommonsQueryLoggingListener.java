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


    public CommonsQueryLoggingListener() {

        // initialize logging condition that checks current log level
        this.loggingCondition = new LoggingCondition() {
            @Override
            public boolean getAsBoolean() {
                switch (logLevel) {
                    case DEBUG:
                        return log.isDebugEnabled();
                    case ERROR:
                        return log.isErrorEnabled();
                    case FATAL:
                        return log.isFatalEnabled();
                    case INFO:
                        return log.isInfoEnabled();
                    case TRACE:
                        return log.isTraceEnabled();
                    case WARN:
                        return log.isWarnEnabled();
                }
                return false;
            }
        };

    }

    @Override
    protected void writeLog(String message) {
        CommonsLogUtils.writeLog(log, this.logLevel, message);
    }

    public void setLogLevel(CommonsLogLevel logLevel) {
        this.logLevel = logLevel;
    }

    /**
     * @deprecated use {{@link #setLog(String)}}
     */
    @Override
    @Deprecated
    protected void resetLogger(String loggerName) {
        this.log = LogFactory.getLog(loggerName);
    }

    /**
     * Override {@link Log} instance with specified log name.
     *
     * @param logName log name
     * @since 1.4.1
     */
    public void setLog(String logName) {
        this.log = LogFactory.getLog(logName);
    }

    /**
     * Override {@link Log} instance.
     *
     * @param log new log instance
     * @since 1.4.1
     */
    public void setLog(Log log) {
        this.log = log;
    }

    /**
     * @return log
     * @since 1.4.1
     */
    public Log getLog() {
        return log;
    }

    /**
     * @return log level to write
     * @since 1.4.1
     */
    public CommonsLogLevel getLogLevel() {
        return logLevel;
    }

}
