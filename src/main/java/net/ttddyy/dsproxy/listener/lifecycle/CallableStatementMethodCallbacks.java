package net.ttddyy.dsproxy.listener.lifecycle;

import net.ttddyy.dsproxy.listener.MethodExecutionContext;

/**
 * Defines callback methods for {@link java.sql.CallableStatement}.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.5
 */
public interface CallableStatementMethodCallbacks {

    //
    // CallableStatement methods
    //

    void beforeGetArray(MethodExecutionContext executionContext);

    void beforeGetBigDecimal(MethodExecutionContext executionContext);

    void beforeGetBlob(MethodExecutionContext executionContext);

    void beforeGetBoolean(MethodExecutionContext executionContext);

    void beforeGetByte(MethodExecutionContext executionContext);

    void beforeGetBytes(MethodExecutionContext executionContext);

    void beforeGetCharacterStream(MethodExecutionContext executionContext);

    void beforeGetClob(MethodExecutionContext executionContext);

    void beforeGetDate(MethodExecutionContext executionContext);

    void beforeGetDouble(MethodExecutionContext executionContext);

    void beforeGetFloat(MethodExecutionContext executionContext);

    void beforeGetInt(MethodExecutionContext executionContext);

    void beforeGetLong(MethodExecutionContext executionContext);

    void beforeGetNCharacterStream(MethodExecutionContext executionContext);

    void beforeGetNClob(MethodExecutionContext executionContext);

    void beforeGetNString(MethodExecutionContext executionContext);

    void beforeGetObject(MethodExecutionContext executionContext);

    void beforeGetRef(MethodExecutionContext executionContext);

    void beforeGetRowId(MethodExecutionContext executionContext);

    void beforeGetShort(MethodExecutionContext executionContext);

    void beforeGetSQLXML(MethodExecutionContext executionContext);

    void beforeGetString(MethodExecutionContext executionContext);

    void beforeGetTime(MethodExecutionContext executionContext);

    void beforeGetTimestamp(MethodExecutionContext executionContext);

    void beforeGetURL(MethodExecutionContext executionContext);

    void beforeRegisterOutParameter(MethodExecutionContext executionContext);

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

    void beforeSetRowId(MethodExecutionContext executionContext);

    void beforeSetShort(MethodExecutionContext executionContext);

    void beforeSetSQLXML(MethodExecutionContext executionContext);

    void beforeSetString(MethodExecutionContext executionContext);

    void beforeSetTime(MethodExecutionContext executionContext);

    void beforeSetTimestamp(MethodExecutionContext executionContext);

    void beforeSetURL(MethodExecutionContext executionContext);

    void beforeWasNull(MethodExecutionContext executionContext);

    void afterGetArray(MethodExecutionContext executionContext);

    void afterGetBigDecimal(MethodExecutionContext executionContext);

    void afterGetBlob(MethodExecutionContext executionContext);

    void afterGetBoolean(MethodExecutionContext executionContext);

    void afterGetByte(MethodExecutionContext executionContext);

    void afterGetBytes(MethodExecutionContext executionContext);

    void afterGetCharacterStream(MethodExecutionContext executionContext);

    void afterGetClob(MethodExecutionContext executionContext);

    void afterGetDate(MethodExecutionContext executionContext);

    void afterGetDouble(MethodExecutionContext executionContext);

    void afterGetFloat(MethodExecutionContext executionContext);

    void afterGetInt(MethodExecutionContext executionContext);

    void afterGetLong(MethodExecutionContext executionContext);

    void afterGetNCharacterStream(MethodExecutionContext executionContext);

    void afterGetNClob(MethodExecutionContext executionContext);

    void afterGetNString(MethodExecutionContext executionContext);

    void afterGetObject(MethodExecutionContext executionContext);

    void afterGetRef(MethodExecutionContext executionContext);

    void afterGetRowId(MethodExecutionContext executionContext);

    void afterGetShort(MethodExecutionContext executionContext);

    void afterGetSQLXML(MethodExecutionContext executionContext);

    void afterGetString(MethodExecutionContext executionContext);

    void afterGetTime(MethodExecutionContext executionContext);

    void afterGetTimestamp(MethodExecutionContext executionContext);

    void afterGetURL(MethodExecutionContext executionContext);

    void afterRegisterOutParameter(MethodExecutionContext executionContext);

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

    void afterSetRowId(MethodExecutionContext executionContext);

    void afterSetShort(MethodExecutionContext executionContext);

    void afterSetSQLXML(MethodExecutionContext executionContext);

    void afterSetString(MethodExecutionContext executionContext);

    void afterSetTime(MethodExecutionContext executionContext);

    void afterSetTimestamp(MethodExecutionContext executionContext);

    void afterSetURL(MethodExecutionContext executionContext);

    void afterWasNull(MethodExecutionContext executionContext);

}
