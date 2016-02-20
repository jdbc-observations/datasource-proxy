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

}
