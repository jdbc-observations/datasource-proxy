package net.ttddyy.dsproxy.test.assertj.data;

import java.sql.SQLType;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class ExecutionParameter {
    public static ExecutionParameter param(int paramIndex, Object value) {
        return null;
    }

    public static ExecutionParameter param(String paramName, Object value) {
        return null;
    }

    public static ExecutionParameter outParam(int paramIndex, int sqlType) {
        return null;
    }

    public static ExecutionParameter outParam(int paramIndex, SQLType sqlType) {
        return null;
    }

    public static ExecutionParameter outParam(String paramName, int sqlType) {
        return null;
    }

    public static ExecutionParameter outParam(String paramName, SQLType sqlType) {
        return null;
    }

    public static ExecutionParameter nullParam(int index, int sqlType) {
        return null;
    }

    public static ExecutionParameter nullParam(int index) {
        return null;
    }

    public static ExecutionParameter nullParam(String name, int sqlType) {
        return null;
    }

    public static ExecutionParameter nullParam(String name) {
        return null;
    }
}
