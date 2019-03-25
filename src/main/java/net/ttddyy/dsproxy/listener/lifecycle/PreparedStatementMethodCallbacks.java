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

    void beforeAddBatch(MethodExecutionContext executionContext);

    void beforeClearParameters(MethodExecutionContext executionContext);

    void beforeExecute(MethodExecutionContext executionContext);

    void beforeExecuteLargeUpdate(MethodExecutionContext executionContext);

    void beforeExecuteQuery(MethodExecutionContext executionContext);

    void beforeExecuteUpdate(MethodExecutionContext executionContext);

    void beforeGetMetaData(MethodExecutionContext executionContext);

    void beforeGetParameterMetaData(MethodExecutionContext executionContext);

    void beforeSetArray(MethodExecutionContext executionContext);

    void beforeSetAsciiStream(MethodExecutionContext executionContext);

    void beforeSetBigDecimal(MethodExecutionContext executionContext);

    void beforeSetBinaryStream(MethodExecutionContext executionContext);

    void beforeSetBlob(MethodExecutionContext executionContext);

    void beforeSetBoolean(MethodExecutionContext executionContext);

    void beforeSetByte(MethodExecutionContext executionContext);

    void beforeSetBytes(MethodExecutionContext executionContext);

    void beforeSetCharacterStream(MethodExecutionContext executionContext);

    void beforeSetClob(MethodExecutionContext executionContext);

    void beforeSetDate(MethodExecutionContext executionContext);

    void beforeSetDouble(MethodExecutionContext executionContext);

    void beforeSetFloat(MethodExecutionContext executionContext);

    void beforeSetInt(MethodExecutionContext executionContext);

    void beforeSetLong(MethodExecutionContext executionContext);

    void beforeSetNCharacterStream(MethodExecutionContext executionContext);

    void beforeSetNClob(MethodExecutionContext executionContext);

    void beforeSetNString(MethodExecutionContext executionContext);

    void beforeSetNull(MethodExecutionContext executionContext);

    void beforeSetObject(MethodExecutionContext executionContext);

    void beforeSetRef(MethodExecutionContext executionContext);

    void beforeSetRowId(MethodExecutionContext executionContext);

    void beforeSetShort(MethodExecutionContext executionContext);

    void beforeSetSQLXML(MethodExecutionContext executionContext);

    void beforeSetString(MethodExecutionContext executionContext);

    void beforeSetTime(MethodExecutionContext executionContext);

    void beforeSetTimestamp(MethodExecutionContext executionContext);

    void beforeSetUnicodeStream(MethodExecutionContext executionContext);

    void beforeSetURL(MethodExecutionContext executionContext);

    void afterAddBatch(MethodExecutionContext executionContext);

    void afterClearParameters(MethodExecutionContext executionContext);

    void afterExecute(MethodExecutionContext executionContext);

    void afterExecuteLargeUpdate(MethodExecutionContext executionContext);

    void afterExecuteQuery(MethodExecutionContext executionContext);

    void afterExecuteUpdate(MethodExecutionContext executionContext);

    void afterGetMetaData(MethodExecutionContext executionContext);

    void afterGetParameterMetaData(MethodExecutionContext executionContext);

    void afterSetArray(MethodExecutionContext executionContext);

    void afterSetAsciiStream(MethodExecutionContext executionContext);

    void afterSetBigDecimal(MethodExecutionContext executionContext);

    void afterSetBinaryStream(MethodExecutionContext executionContext);

    void afterSetBlob(MethodExecutionContext executionContext);

    void afterSetBoolean(MethodExecutionContext executionContext);

    void afterSetByte(MethodExecutionContext executionContext);

    void afterSetBytes(MethodExecutionContext executionContext);

    void afterSetCharacterStream(MethodExecutionContext executionContext);

    void afterSetClob(MethodExecutionContext executionContext);

    void afterSetDate(MethodExecutionContext executionContext);

    void afterSetDouble(MethodExecutionContext executionContext);

    void afterSetFloat(MethodExecutionContext executionContext);

    void afterSetInt(MethodExecutionContext executionContext);

    void afterSetLong(MethodExecutionContext executionContext);

    void afterSetNCharacterStream(MethodExecutionContext executionContext);

    void afterSetNClob(MethodExecutionContext executionContext);

    void afterSetNString(MethodExecutionContext executionContext);

    void afterSetNull(MethodExecutionContext executionContext);

    void afterSetObject(MethodExecutionContext executionContext);

    void afterSetRef(MethodExecutionContext executionContext);

    void afterSetRowId(MethodExecutionContext executionContext);

    void afterSetShort(MethodExecutionContext executionContext);

    void afterSetSQLXML(MethodExecutionContext executionContext);

    void afterSetString(MethodExecutionContext executionContext);

    void afterSetTime(MethodExecutionContext executionContext);

    void afterSetTimestamp(MethodExecutionContext executionContext);

    void afterSetUnicodeStream(MethodExecutionContext executionContext);

    void afterSetURL(MethodExecutionContext executionContext);

}
