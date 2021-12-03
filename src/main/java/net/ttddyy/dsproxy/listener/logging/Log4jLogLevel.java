package net.ttddyy.dsproxy.listener.logging;

/**
 * Apache Commons Logging log4j log level representation.
 *
 * @author Ivan Jose Sanchez Pagador
 */
public enum Log4jLogLevel {
    // least serious to most serious
    TRACE, DEBUG, INFO, WARN, ERROR;

    public static Log4jLogLevel nullSafeValueOf(String name) {
        if (name == null) {
            return null;
        }
        return valueOf(name);
    }

}
