package net.ttddyy.dsproxy.listener.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Log slow query using JUL(Java Util Logging).
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.1
 */
public class JULSlowQueryListener extends AbstractSlowQueryLoggingListener {

    protected Logger logger = Logger.getLogger(JULQueryLoggingListener.class.getName());
    protected Level logLevel = Level.WARNING; // default WARNING

    @Override
    protected void writeLog(String message) {
        this.logger.log(this.logLevel, message);
    }

    public void setLogLevel(Level logLevel) {
        this.logLevel = logLevel;
    }

    public void setLogger(String loggerName) {
        setLogger(Logger.getLogger(loggerName));
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public Logger getLogger() {
        return logger;
    }

}
