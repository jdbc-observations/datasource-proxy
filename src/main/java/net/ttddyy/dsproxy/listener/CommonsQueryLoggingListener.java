package net.ttddyy.dsproxy.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Record executed query information using Commons-Logging.
 *
 * @author Tadaya Tsuyukubo
 */
public class CommonsQueryLoggingListener extends AbstractQueryLoggingListener {

    protected static final Log log = LogFactory.getLog(CommonsQueryLoggingListener.class);
    protected CommonsLogLevel logLevel = CommonsLogLevel.DEBUG; // default DEBUG

    @Override
    protected void writeLog(String message) {
        switch (logLevel) {
            case DEBUG:
                log.debug(message);
                break;
            case ERROR:
                log.error(message);
                break;
            case FATAL:
                log.fatal(message);
                break;
            case INFO:
                log.info(message);
                break;
            case TRACE:
                log.trace(message);
                break;
            case WARN:
                log.warn(message);
                break;
        }
    }

    public void setLogLevel(CommonsLogLevel logLevel) {
        this.logLevel = logLevel;
    }

}
