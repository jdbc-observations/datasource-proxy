package net.ttddyy.dsproxy.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Log executed query information using SLF4J.
 *
 * @author Tadaya Tsuyukubo
 */
public class SLF4JQueryLoggingListener extends AbstractLoggingListener {

    protected static final Logger logger = LoggerFactory.getLogger(SLF4JQueryLoggingListener.class);
    protected SLF4JLogLevel logLevel = SLF4JLogLevel.DEBUG; // default DEBUG

    @Override
    protected void writeLog(String message) {
        switch (logLevel) {
            case DEBUG:
                logger.debug(message);
                break;
            case ERROR:
                logger.error(message);
                break;
            case INFO:
                logger.info(message);
                break;
            case TRACE:
                logger.trace(message);
                break;
            case WARN:
                logger.warn(message);
                break;
        }
    }

    public void setLogLevel(SLF4JLogLevel logLevel) {
        this.logLevel = logLevel;
    }

}
