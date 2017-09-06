package net.ttddyy.dsproxy.listener.logging;

/**
 * SLF4J log level representation.
 *
 * @author Tadaya Tsuyukubo
 */
public enum SLF4JLogLevel {
    // least serious to most serious
    TRACE, DEBUG, INFO, WARN, ERROR;

    public static SLF4JLogLevel nullSafeValueOf(String name) {
        if (name == null) {
            return null;
        }
        return valueOf(name);
    }

}
