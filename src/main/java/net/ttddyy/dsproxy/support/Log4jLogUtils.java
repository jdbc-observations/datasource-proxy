package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.listener.logging.Log4jLogLevel;
import org.apache.logging.log4j.Logger;

/**
 * @author Ivan Jose Sanchez Pagador
 */
public class Log4jLogUtils {

    public static void writeLog(Logger logger, Log4jLogLevel logLevel, String message) {
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

}
