package net.ttddyy.dsproxy.listener.lifecycle;

import net.ttddyy.dsproxy.listener.MethodExecutionContext;

/**
 * Defines callback methods for {@link java.sql.PreparedStatement}.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.5
 */
public interface PreparedStatementMethodCallbacks {

    //
    // PreparedStatement methods
    //

    default void beforeAddBatch(MethodExecutionContext executionContext) {

    }

    default void beforeClearParameters(MethodExecutionContext executionContext) {

    }

    default void beforeExecute(MethodExecutionContext executionContext) {

    }

    default void beforeExecuteLargeUpdate(MethodExecutionContext executionContext) {

    }

    default void beforeExecuteQuery(MethodExecutionContext executionContext) {

    }

    default void beforeExecuteUpdate(MethodExecutionContext executionContext) {

    }

    default void beforeGetMetaData(MethodExecutionContext executionContext) {

    }

    default void beforeGetParameterMetaData(MethodExecutionContext executionContext) {

    }

    default void beforeSetArray(MethodExecutionContext executionContext) {

    }

    default void beforeSetAsciiStream(MethodExecutionContext executionContext) {

    }

    default void beforeSetBigDecimal(MethodExecutionContext executionContext) {

    }

    default void beforeSetBinaryStream(MethodExecutionContext executionContext) {

    }

    default void beforeSetBlob(MethodExecutionContext executionContext) {

    }

    default void beforeSetBoolean(MethodExecutionContext executionContext) {

    }

    default void beforeSetByte(MethodExecutionContext executionContext) {

    }

    default void beforeSetBytes(MethodExecutionContext executionContext) {

    }

    default void beforeSetCharacterStream(MethodExecutionContext executionContext) {

    }

    default void beforeSetClob(MethodExecutionContext executionContext) {

    }

    default void beforeSetDate(MethodExecutionContext executionContext) {

    }

    default void beforeSetDouble(MethodExecutionContext executionContext) {

    }

    default void beforeSetFloat(MethodExecutionContext executionContext) {

    }

    default void beforeSetInt(MethodExecutionContext executionContext) {

    }

    default void beforeSetLong(MethodExecutionContext executionContext) {

    }

    default void beforeSetNCharacterStream(MethodExecutionContext executionContext) {

    }

    default void beforeSetNClob(MethodExecutionContext executionContext) {

    }

    default void beforeSetNString(MethodExecutionContext executionContext) {

    }

    default void beforeSetNull(MethodExecutionContext executionContext) {

    }

    default void beforeSetObject(MethodExecutionContext executionContext) {

    }

    default void beforeSetRef(MethodExecutionContext executionContext) {

    }

    default void beforeSetRowId(MethodExecutionContext executionContext) {

    }

    default void beforeSetShort(MethodExecutionContext executionContext) {

    }

    default void beforeSetSQLXML(MethodExecutionContext executionContext) {

    }

    default void beforeSetString(MethodExecutionContext executionContext) {

    }

    default void beforeSetTime(MethodExecutionContext executionContext) {

    }

    default void beforeSetTimestamp(MethodExecutionContext executionContext) {

    }

    default void beforeSetUnicodeStream(MethodExecutionContext executionContext) {

    }

    default void beforeSetURL(MethodExecutionContext executionContext) {

    }

    default void afterAddBatch(MethodExecutionContext executionContext) {

    }

    default void afterClearParameters(MethodExecutionContext executionContext) {

    }

    default void afterExecute(MethodExecutionContext executionContext) {

    }

    default void afterExecuteLargeUpdate(MethodExecutionContext executionContext) {

    }

    default void afterExecuteQuery(MethodExecutionContext executionContext) {

    }

    default void afterExecuteUpdate(MethodExecutionContext executionContext) {

    }

    default void afterGetMetaData(MethodExecutionContext executionContext) {

    }

    default void afterGetParameterMetaData(MethodExecutionContext executionContext) {

    }

    default void afterSetArray(MethodExecutionContext executionContext) {

    }

    default void afterSetAsciiStream(MethodExecutionContext executionContext) {

    }

    default void afterSetBigDecimal(MethodExecutionContext executionContext) {

    }

    default void afterSetBinaryStream(MethodExecutionContext executionContext) {

    }

    default void afterSetBlob(MethodExecutionContext executionContext) {

    }

    default void afterSetBoolean(MethodExecutionContext executionContext) {

    }

    default void afterSetByte(MethodExecutionContext executionContext) {

    }

    default void afterSetBytes(MethodExecutionContext executionContext) {

    }

    default void afterSetCharacterStream(MethodExecutionContext executionContext) {

    }

    default void afterSetClob(MethodExecutionContext executionContext) {

    }

    default void afterSetDate(MethodExecutionContext executionContext) {

    }

    default void afterSetDouble(MethodExecutionContext executionContext) {

    }

    default void afterSetFloat(MethodExecutionContext executionContext) {

    }

    default void afterSetInt(MethodExecutionContext executionContext) {

    }

    default void afterSetLong(MethodExecutionContext executionContext) {

    }

    default void afterSetNCharacterStream(MethodExecutionContext executionContext) {

    }

    default void afterSetNClob(MethodExecutionContext executionContext) {

    }

    default void afterSetNString(MethodExecutionContext executionContext) {

    }

    default void afterSetNull(MethodExecutionContext executionContext) {

    }

    default void afterSetObject(MethodExecutionContext executionContext) {

    }

    default void afterSetRef(MethodExecutionContext executionContext) {

    }

    default void afterSetRowId(MethodExecutionContext executionContext) {

    }

    default void afterSetShort(MethodExecutionContext executionContext) {

    }

    default void afterSetSQLXML(MethodExecutionContext executionContext) {

    }

    default void afterSetString(MethodExecutionContext executionContext) {

    }

    default void afterSetTime(MethodExecutionContext executionContext) {

    }

    default void afterSetTimestamp(MethodExecutionContext executionContext) {

    }

    default void afterSetUnicodeStream(MethodExecutionContext executionContext) {

    }

    default void afterSetURL(MethodExecutionContext executionContext) {

    }

}
