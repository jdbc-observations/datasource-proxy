package net.ttddyy.dsproxy;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.3
 */
public enum StatementType {
    STATEMENT, PREPARED, CALLABLE;

    /**
     * @param statement statement
     * @param <T> statement type
     * @return statement type
     * @since 1.3.1
     */
    public static <T extends Statement> StatementType valueOf(T statement) {
        if (statement instanceof CallableStatement) {
            return CALLABLE;
        } else if (statement instanceof PreparedStatement) {
            return PREPARED;
        } else {
            return STATEMENT;
        }
    }
}
