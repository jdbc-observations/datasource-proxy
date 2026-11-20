package net.ttddyy.dsproxy.listener.logging;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Log slow query using JUL(Java Util Logging).
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.1
 */
public class JULSlowQueryListener extends AbstractSlowQueryLoggingListener {

    protected Logger logger = Logger.getLogger(JULSlowQueryListener.class.getName());
    protected Level logLevel = Level.WARNING; // default WARNING

    public JULSlowQueryListener() {
    }

    public JULSlowQueryListener(long threshold, TimeUnit thresholdTimeUnit) {
        setThreshold(Duration.of(threshold, thresholdTimeUnit.toChronoUnit()));
    }

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

    public Level getLogLevel() {
        return logLevel;
    }

}
