package net.ttddyy.dsproxy.test.assertj.data;

import java.sql.SQLType;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class BatchParameter {
    public static BatchParameter param(int paramIndex, Object value) {
        return null;
    }

    public static BatchParameter param(String paramName, Object value) {
        return null;
    }

    public static BatchParameter outParam(int paramIndex, int sqlType) {
        return null;
    }

    public static BatchParameter outParam(int paramIndex, SQLType sqlType) {
        return null;
    }

    public static BatchParameter outParam(String paramName, int sqlType) {
        return null;
    }

    public static BatchParameter outParam(String paramName, SQLType sqlType) {
        return null;
    }

    public static BatchParameter nullParam(int index, int sqlType) {
        return null;
    }

    public static BatchParameter nullParam(int index) {
        return null;
    }

    public static BatchParameter nullParam(String name, int sqlType) {
        return null;
    }

    public static BatchParameter nullParam(String name) {
        return null;
    }
}
