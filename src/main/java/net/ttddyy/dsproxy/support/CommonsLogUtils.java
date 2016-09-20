package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.listener.logging.CommonsLogLevel;
import org.apache.commons.logging.Log;

/**
 * @author Tadaya Tsuyukubo
 */
public class CommonsLogUtils {

    public static void writeLog(Log log, CommonsLogLevel logLevel, String message) {
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

}
