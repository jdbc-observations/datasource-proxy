package com.tpon.dsproxy.listener;

/**
 * Apache Commons-Logging log level representation. 
 *
 * @author Tadaya Tsuyukubo
 */
public enum CommonsLogLevel {
    DEBUG, ERROR, FATAL, INFO, TRAC, WARN;

    public static CommonsLogLevel nullSafeValueOf(String name) {
        if (name == null) {
            return null;
        }
        return valueOf(name);
    }
}
