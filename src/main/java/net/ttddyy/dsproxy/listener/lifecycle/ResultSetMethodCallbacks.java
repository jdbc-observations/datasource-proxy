package net.ttddyy.dsproxy.listener.lifecycle;

import net.ttddyy.dsproxy.listener.MethodExecutionContext;

/**
 * Defines callback methods for {@link java.sql.ResultSet}.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.5
 */
public interface ResultSetMethodCallbacks {

    //
    // ResultSet methods
    //

    void beforeAbsolute(MethodExecutionContext executionContext);

    void beforeAfterLast(MethodExecutionContext executionContext);

    void beforeBeforeFirst(MethodExecutionContext executionContext);

    void beforeCancelRowUpdates(MethodExecutionContext executionContext);

    void beforeClearWarnings(MethodExecutionContext executionContext);

    void beforeClose(MethodExecutionContext executionContext);

    void beforeDeleteRow(MethodExecutionContext executionContext);

    void beforeFindColumn(MethodExecutionContext executionContext);

    void beforeFirst(MethodExecutionContext executionContext);

    void beforeGetArray(MethodExecutionContext executionContext);

    void beforeGetAsciiStream(MethodExecutionContext executionContext);

    void beforeGetBigDecimal(MethodExecutionContext executionContext);

    void beforeGetBinaryStream(MethodExecutionContext executionContext);

    void beforeGetBlob(MethodExecutionContext executionContext);

    void beforeGetBoolean(MethodExecutionContext executionContext);

    void beforeGetByte(MethodExecutionContext executionContext);

    void beforeGetBytes(MethodExecutionContext executionContext);

    void beforeGetCharacterStream(MethodExecutionContext executionContext);

    void beforeGetClob(MethodExecutionContext executionContext);

    void beforeGetConcurrency(MethodExecutionContext executionContext);

    void beforeGetCursorName(MethodExecutionContext executionContext);

    void beforeGetDate(MethodExecutionContext executionContext);

    void beforeGetDouble(MethodExecutionContext executionContext);

    void beforeGetFetchDirection(MethodExecutionContext executionContext);

    void beforeGetFetchSize(MethodExecutionContext executionContext);

    void beforeGetFloat(MethodExecutionContext executionContext);

    void beforeGetHoldability(MethodExecutionContext executionContext);

    void beforeGetInt(MethodExecutionContext executionContext);

    void beforeGetLong(MethodExecutionContext executionContext);

    void beforeGetMetaData(MethodExecutionContext executionContext);

    void beforeGetNCharacterStream(MethodExecutionContext executionContext);

    void beforeGetNClob(MethodExecutionContext executionContext);

    void beforeGetNString(MethodExecutionContext executionContext);

    void beforeGetObject(MethodExecutionContext executionContext);

    void beforeGetRef(MethodExecutionContext executionContext);

    void beforeGetRow(MethodExecutionContext executionContext);

    void beforeGetRowId(MethodExecutionContext executionContext);

    void beforeGetShort(MethodExecutionContext executionContext);

    void beforeGetSQLXML(MethodExecutionContext executionContext);

    void beforeGetStatement(MethodExecutionContext executionContext);

    void beforeGetString(MethodExecutionContext executionContext);

    void beforeGetTime(MethodExecutionContext executionContext);

    void beforeGetTimestamp(MethodExecutionContext executionContext);

    void beforeGetType(MethodExecutionContext executionContext);

    void beforeGetUnicodeStream(MethodExecutionContext executionContext);

    void beforeGetURL(MethodExecutionContext executionContext);

    void beforeGetWarnings(MethodExecutionContext executionContext);

    void beforeInsertRow(MethodExecutionContext executionContext);

    void beforeIsAfterLast(MethodExecutionContext executionContext);

    void beforeIsBeforeFirst(MethodExecutionContext executionContext);

    void beforeIsClosed(MethodExecutionContext executionContext);

    void beforeIsFirst(MethodExecutionContext executionContext);

    void beforeIsLast(MethodExecutionContext executionContext);

    void beforeLast(MethodExecutionContext executionContext);

    void beforeMoveToCurrentRow(MethodExecutionContext executionContext);

    void beforeMoveToInsertRow(MethodExecutionContext executionContext);

    void beforeNext(MethodExecutionContext executionContext);

    void beforePrevious(MethodExecutionContext executionContext);

    void beforeRefreshRow(MethodExecutionContext executionContext);

    void beforeRelative(MethodExecutionContext executionContext);

    void beforeRowDeleted(MethodExecutionContext executionContext);

    void beforeRowInserted(MethodExecutionContext executionContext);

    void beforeRowUpdated(MethodExecutionContext executionContext);

    void beforeSetFetchDirection(MethodExecutionContext executionContext);

    void beforeSetFetchSize(MethodExecutionContext executionContext);

    void beforeUpdateArray(MethodExecutionContext executionContext);

    void beforeUpdateAsciiStream(MethodExecutionContext executionContext);

    void beforeUpdateBigDecimal(MethodExecutionContext executionContext);

    void beforeUpdateBinaryStream(MethodExecutionContext executionContext);

    void beforeUpdateBlob(MethodExecutionContext executionContext);

    void beforeUpdateBoolean(MethodExecutionContext executionContext);

    void beforeUpdateByte(MethodExecutionContext executionContext);

    void beforeUpdateBytes(MethodExecutionContext executionContext);

    void beforeUpdateCharacterStream(MethodExecutionContext executionContext);

    void beforeUpdateClob(MethodExecutionContext executionContext);

    void beforeUpdateDate(MethodExecutionContext executionContext);

    void beforeUpdateDouble(MethodExecutionContext executionContext);

    void beforeUpdateFloat(MethodExecutionContext executionContext);

    void beforeUpdateInt(MethodExecutionContext executionContext);

    void beforeUpdateLong(MethodExecutionContext executionContext);

    void beforeUpdateNCharacterStream(MethodExecutionContext executionContext);

    void beforeUpdateNClob(MethodExecutionContext executionContext);

    void beforeUpdateNString(MethodExecutionContext executionContext);

    void beforeUpdateNull(MethodExecutionContext executionContext);

    void beforeUpdateObject(MethodExecutionContext executionContext);

    void beforeUpdateRef(MethodExecutionContext executionContext);

    void beforeUpdateRow(MethodExecutionContext executionContext);

    void beforeUpdateRowId(MethodExecutionContext executionContext);

    void beforeUpdateShort(MethodExecutionContext executionContext);

    void beforeUpdateSQLXML(MethodExecutionContext executionContext);

    void beforeUpdateString(MethodExecutionContext executionContext);

    void beforeUpdateTime(MethodExecutionContext executionContext);

    void beforeUpdateTimestamp(MethodExecutionContext executionContext);

    void beforeWasNull(MethodExecutionContext executionContext);

    void afterAbsolute(MethodExecutionContext executionContext);

    void afterAfterLast(MethodExecutionContext executionContext);

    void afterBeforeFirst(MethodExecutionContext executionContext);

    void afterCancelRowUpdates(MethodExecutionContext executionContext);

    void afterClearWarnings(MethodExecutionContext executionContext);

    void afterClose(MethodExecutionContext executionContext);

    void afterDeleteRow(MethodExecutionContext executionContext);

    void afterFindColumn(MethodExecutionContext executionContext);

    void afterFirst(MethodExecutionContext executionContext);

    void afterGetArray(MethodExecutionContext executionContext);

    void afterGetAsciiStream(MethodExecutionContext executionContext);

    void afterGetBigDecimal(MethodExecutionContext executionContext);

    void afterGetBinaryStream(MethodExecutionContext executionContext);

    void afterGetBlob(MethodExecutionContext executionContext);

    void afterGetBoolean(MethodExecutionContext executionContext);

    void afterGetByte(MethodExecutionContext executionContext);

    void afterGetBytes(MethodExecutionContext executionContext);

    void afterGetCharacterStream(MethodExecutionContext executionContext);

    void afterGetClob(MethodExecutionContext executionContext);

    void afterGetConcurrency(MethodExecutionContext executionContext);

    void afterGetCursorName(MethodExecutionContext executionContext);

    void afterGetDate(MethodExecutionContext executionContext);

    void afterGetDouble(MethodExecutionContext executionContext);

    void afterGetFetchDirection(MethodExecutionContext executionContext);

    void afterGetFetchSize(MethodExecutionContext executionContext);

    void afterGetFloat(MethodExecutionContext executionContext);

    void afterGetHoldability(MethodExecutionContext executionContext);

    void afterGetInt(MethodExecutionContext executionContext);

    void afterGetLong(MethodExecutionContext executionContext);

    void afterGetMetaData(MethodExecutionContext executionContext);

    void afterGetNCharacterStream(MethodExecutionContext executionContext);

    void afterGetNClob(MethodExecutionContext executionContext);

    void afterGetNString(MethodExecutionContext executionContext);

    void afterGetObject(MethodExecutionContext executionContext);

    void afterGetRef(MethodExecutionContext executionContext);

    void afterGetRow(MethodExecutionContext executionContext);

    void afterGetRowId(MethodExecutionContext executionContext);

    void afterGetShort(MethodExecutionContext executionContext);

    void afterGetSQLXML(MethodExecutionContext executionContext);

    void afterGetStatement(MethodExecutionContext executionContext);

    void afterGetString(MethodExecutionContext executionContext);

    void afterGetTime(MethodExecutionContext executionContext);

    void afterGetTimestamp(MethodExecutionContext executionContext);

    void afterGetType(MethodExecutionContext executionContext);

    void afterGetUnicodeStream(MethodExecutionContext executionContext);

    void afterGetURL(MethodExecutionContext executionContext);

    void afterGetWarnings(MethodExecutionContext executionContext);

    void afterInsertRow(MethodExecutionContext executionContext);

    void afterIsAfterLast(MethodExecutionContext executionContext);

    void afterIsBeforeFirst(MethodExecutionContext executionContext);

    void afterIsClosed(MethodExecutionContext executionContext);

    void afterIsFirst(MethodExecutionContext executionContext);

    void afterIsLast(MethodExecutionContext executionContext);

    void afterLast(MethodExecutionContext executionContext);

    void afterMoveToCurrentRow(MethodExecutionContext executionContext);

    void afterMoveToInsertRow(MethodExecutionContext executionContext);

    void afterNext(MethodExecutionContext executionContext);

    void afterPrevious(MethodExecutionContext executionContext);

    void afterRefreshRow(MethodExecutionContext executionContext);

    void afterRelative(MethodExecutionContext executionContext);

    void afterRowDeleted(MethodExecutionContext executionContext);

    void afterRowInserted(MethodExecutionContext executionContext);

    void afterRowUpdated(MethodExecutionContext executionContext);

    void afterSetFetchDirection(MethodExecutionContext executionContext);

    void afterSetFetchSize(MethodExecutionContext executionContext);

    void afterUpdateArray(MethodExecutionContext executionContext);

    void afterUpdateAsciiStream(MethodExecutionContext executionContext);

    void afterUpdateBigDecimal(MethodExecutionContext executionContext);

    void afterUpdateBinaryStream(MethodExecutionContext executionContext);

    void afterUpdateBlob(MethodExecutionContext executionContext);

    void afterUpdateBoolean(MethodExecutionContext executionContext);

    void afterUpdateByte(MethodExecutionContext executionContext);

    void afterUpdateBytes(MethodExecutionContext executionContext);

    void afterUpdateCharacterStream(MethodExecutionContext executionContext);

    void afterUpdateClob(MethodExecutionContext executionContext);

    void afterUpdateDate(MethodExecutionContext executionContext);

    void afterUpdateDouble(MethodExecutionContext executionContext);

    void afterUpdateFloat(MethodExecutionContext executionContext);

    void afterUpdateInt(MethodExecutionContext executionContext);

    void afterUpdateLong(MethodExecutionContext executionContext);

    void afterUpdateNCharacterStream(MethodExecutionContext executionContext);

    void afterUpdateNClob(MethodExecutionContext executionContext);

    void afterUpdateNString(MethodExecutionContext executionContext);

    void afterUpdateNull(MethodExecutionContext executionContext);

    void afterUpdateObject(MethodExecutionContext executionContext);

    void afterUpdateRef(MethodExecutionContext executionContext);

    void afterUpdateRow(MethodExecutionContext executionContext);

    void afterUpdateRowId(MethodExecutionContext executionContext);

    void afterUpdateShort(MethodExecutionContext executionContext);

    void afterUpdateSQLXML(MethodExecutionContext executionContext);

    void afterUpdateString(MethodExecutionContext executionContext);

    void afterUpdateTime(MethodExecutionContext executionContext);

    void afterUpdateTimestamp(MethodExecutionContext executionContext);

    void afterWasNull(MethodExecutionContext executionContext);

}
