package com.tpon.dsproxy.listener;

/**
 * SLF4J log level representation.
 *
 * @author Tadaya Tsuyukubo
 */
public enum SLF4JLogLevel {
    DEBUG, ERROR, INFO, TRAC, WARN;

    public static SLF4JLogLevel nullSafeValueOf(String name) {
        if (name == null) {
            return null;
        }
        return valueOf(name);
    }

}
