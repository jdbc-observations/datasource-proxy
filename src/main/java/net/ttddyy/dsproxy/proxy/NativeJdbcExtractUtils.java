package net.ttddyy.dsproxy.proxy;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * @author Tadaya Tsuyukubo
 */
public class NativeJdbcExtractUtils {
    public static Connection getConnection(Connection connection) {
        return getNativeJdbcObject(connection);
    }

    public static Statement getStatement(Statement statement) {
        return getNativeJdbcObject(statement);
    }

    public static PreparedStatement getPreparedStatement(PreparedStatement preparedStatement) {
        return getNativeJdbcObject(preparedStatement);
    }

    public static CallableStatement getCallableStatement(CallableStatement callableStatement) {
        return getNativeJdbcObject(callableStatement);
    }

    @SuppressWarnings("unchecked")
    private static <T> T getNativeJdbcObject(T obj) {
        T objToUse = obj;
        while (objToUse instanceof ProxyJdbcObject) {
            objToUse = (T) ((ProxyJdbcObject) objToUse).getTarget();
        }
        return objToUse;
    }

}
