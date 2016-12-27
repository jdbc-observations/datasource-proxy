package net.ttddyy.dsproxy.support;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4.1
 */
public class JULQueryCountLoggingServletFilter extends AbstractQueryCountLoggingServletFilter {
    protected Logger logger = Logger.getLogger(JULQueryCountLoggingHandlerInterceptor.class.getName());

    private Level logLevel = Level.FINE;  // default

    public JULQueryCountLoggingServletFilter() {
    }

    public JULQueryCountLoggingServletFilter(Level logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    protected void initLogLevelFromFilterConfigIfSpecified(String logLevelParam) {
        Level logLevel = Level.parse(logLevelParam);
        if (logLevel != null) {
            this.logLevel = logLevel;
        }
    }

    @Override
    protected void writeLog(String message) {
        this.logger.log(logLevel, message);
    }

    @Override
    protected void resetLogger(String loggerName) {
        this.logger = Logger.getLogger(loggerName);
    }

    public void setLogLevel(Level logLevel) {
        this.logLevel = logLevel;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
