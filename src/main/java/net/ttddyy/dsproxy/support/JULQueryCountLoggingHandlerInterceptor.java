package net.ttddyy.dsproxy.support;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4.1
 */
public class JULQueryCountLoggingHandlerInterceptor extends AbstractQueryCountLoggingHandlerInterceptor {

    protected Logger logger = Logger.getLogger(JULQueryCountLoggingHandlerInterceptor.class.getName());
    protected Level logLevel = Level.FINE; // default FINE

    public JULQueryCountLoggingHandlerInterceptor() {
    }

    public JULQueryCountLoggingHandlerInterceptor(Level logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    protected void writeLog(String message) {
        this.logger.log(this.logLevel, message);
    }

    public void setLogLevel(Level logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    protected void resetLogger(String loggerName) {
        this.logger = Logger.getLogger(loggerName);
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

}
