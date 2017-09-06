package net.ttddyy.dsproxy.listener.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Log executed query information using JUL(Java Util Logging).
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class JULQueryLoggingListener extends AbstractQueryLoggingListener {

    protected Logger logger = Logger.getLogger(JULQueryLoggingListener.class.getName());
    protected Level logLevel = Level.FINE; // default FINE

    public JULQueryLoggingListener() {
        // initialize condition that checks the current logger level
        this.loggingCondition = new LoggingCondition() {
            @Override
            public boolean getAsBoolean() {
                return logger.isLoggable(logLevel);
            }
        };
    }

    @Override
    protected void writeLog(String message) {
        this.logger.log(this.logLevel, message);
    }

    public void setLogLevel(Level logLevel) {
        this.logLevel = logLevel;
    }

    /**
     * @deprecated use {{@link #setLogger(String)}}
     */
    @Override
    @Deprecated
    protected void resetLogger(String loggerName) {
        this.logger = Logger.getLogger(loggerName);
    }

    /**
     * Override {@link Logger} instance that has specified logger name
     *
     * @param loggerName new logger name
     * @since 1.4.1
     */
    public void setLogger(String loggerName) {
        this.logger = Logger.getLogger(loggerName);
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
    public Level getLogLevel() {
        return logLevel;
    }

}
