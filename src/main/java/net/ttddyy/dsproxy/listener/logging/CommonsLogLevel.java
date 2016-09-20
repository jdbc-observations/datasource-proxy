package net.ttddyy.dsproxy.listener.logging;

/**
 * Apache Commons-Logging log level representation. 
 *
 * @author Tadaya Tsuyukubo
 */
public enum CommonsLogLevel {
    DEBUG, ERROR, FATAL, INFO, TRACE, WARN;

    public static CommonsLogLevel nullSafeValueOf(String name) {
        if (name == null) {
            return null;
        }
        return valueOf(name);
    }
}
