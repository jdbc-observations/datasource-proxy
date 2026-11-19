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

    default void beforeAbsolute(MethodExecutionContext executionContext) {

    }

    default void beforeAfterLast(MethodExecutionContext executionContext) {

    }

    default void beforeBeforeFirst(MethodExecutionContext executionContext) {

    }

    default void beforeCancelRowUpdates(MethodExecutionContext executionContext) {

    }

    default void beforeClearWarnings(MethodExecutionContext executionContext) {

    }

    default void beforeClose(MethodExecutionContext executionContext) {

    }

    default void beforeDeleteRow(MethodExecutionContext executionContext) {

    }

    default void beforeFindColumn(MethodExecutionContext executionContext) {

    }

    default void beforeFirst(MethodExecutionContext executionContext) {

    }

    default void beforeGetArray(MethodExecutionContext executionContext) {

    }

    default void beforeGetAsciiStream(MethodExecutionContext executionContext) {

    }

    default void beforeGetBigDecimal(MethodExecutionContext executionContext) {

    }

    default void beforeGetBinaryStream(MethodExecutionContext executionContext) {

    }

    default void beforeGetBlob(MethodExecutionContext executionContext) {

    }

    default void beforeGetBoolean(MethodExecutionContext executionContext) {

    }

    default void beforeGetByte(MethodExecutionContext executionContext) {

    }

    default void beforeGetBytes(MethodExecutionContext executionContext) {

    }

    default void beforeGetCharacterStream(MethodExecutionContext executionContext) {

    }

    default void beforeGetClob(MethodExecutionContext executionContext) {

    }

    default void beforeGetConcurrency(MethodExecutionContext executionContext) {

    }

    default void beforeGetCursorName(MethodExecutionContext executionContext) {

    }

    default void beforeGetDate(MethodExecutionContext executionContext) {

    }

    default void beforeGetDouble(MethodExecutionContext executionContext) {

    }

    default void beforeGetFetchDirection(MethodExecutionContext executionContext) {

    }

    default void beforeGetFetchSize(MethodExecutionContext executionContext) {

    }

    default void beforeGetFloat(MethodExecutionContext executionContext) {

    }

    default void beforeGetHoldability(MethodExecutionContext executionContext) {

    }

    default void beforeGetInt(MethodExecutionContext executionContext) {

    }

    default void beforeGetLong(MethodExecutionContext executionContext) {

    }

    default void beforeGetMetaData(MethodExecutionContext executionContext) {

    }

    default void beforeGetNCharacterStream(MethodExecutionContext executionContext) {

    }

    default void beforeGetNClob(MethodExecutionContext executionContext) {

    }

    default void beforeGetNString(MethodExecutionContext executionContext) {

    }

    default void beforeGetObject(MethodExecutionContext executionContext) {

    }

    default void beforeGetRef(MethodExecutionContext executionContext) {

    }

    default void beforeGetRow(MethodExecutionContext executionContext) {

    }

    default void beforeGetRowId(MethodExecutionContext executionContext) {

    }

    default void beforeGetShort(MethodExecutionContext executionContext) {

    }

    default void beforeGetSQLXML(MethodExecutionContext executionContext) {

    }

    default void beforeGetStatement(MethodExecutionContext executionContext) {

    }

    default void beforeGetString(MethodExecutionContext executionContext) {

    }

    default void beforeGetTime(MethodExecutionContext executionContext) {

    }

    default void beforeGetTimestamp(MethodExecutionContext executionContext) {

    }

    default void beforeGetType(MethodExecutionContext executionContext) {

    }

    default void beforeGetUnicodeStream(MethodExecutionContext executionContext) {

    }

    default void beforeGetURL(MethodExecutionContext executionContext) {

    }

    default void beforeGetWarnings(MethodExecutionContext executionContext) {

    }

    default void beforeInsertRow(MethodExecutionContext executionContext) {

    }

    default void beforeIsAfterLast(MethodExecutionContext executionContext) {

    }

    default void beforeIsBeforeFirst(MethodExecutionContext executionContext) {

    }

    default void beforeIsClosed(MethodExecutionContext executionContext) {

    }

    default void beforeIsFirst(MethodExecutionContext executionContext) {

    }

    default void beforeIsLast(MethodExecutionContext executionContext) {

    }

    default void beforeLast(MethodExecutionContext executionContext) {

    }

    default void beforeMoveToCurrentRow(MethodExecutionContext executionContext) {

    }

    default void beforeMoveToInsertRow(MethodExecutionContext executionContext) {

    }

    default void beforeNext(MethodExecutionContext executionContext) {

    }

    default void beforePrevious(MethodExecutionContext executionContext) {

    }

    default void beforeRefreshRow(MethodExecutionContext executionContext) {

    }

    default void beforeRelative(MethodExecutionContext executionContext) {

    }

    default void beforeRowDeleted(MethodExecutionContext executionContext) {

    }

    default void beforeRowInserted(MethodExecutionContext executionContext) {

    }

    default void beforeRowUpdated(MethodExecutionContext executionContext) {

    }

    default void beforeSetFetchDirection(MethodExecutionContext executionContext) {

    }

    default void beforeSetFetchSize(MethodExecutionContext executionContext) {

    }

    default void beforeUpdateArray(MethodExecutionContext executionContext) {

    }

    default void beforeUpdateAsciiStream(MethodExecutionContext executionContext) {

    }

    default void beforeUpdateBigDecimal(MethodExecutionContext executionContext) {

    }

    default void beforeUpdateBinaryStream(MethodExecutionContext executionContext) {

    }

    default void beforeUpdateBlob(MethodExecutionContext executionContext) {

    }

    default void beforeUpdateBoolean(MethodExecutionContext executionContext) {

    }

    default void beforeUpdateByte(MethodExecutionContext executionContext) {

    }

    default void beforeUpdateBytes(MethodExecutionContext executionContext) {

    }

    default void beforeUpdateCharacterStream(MethodExecutionContext executionContext) {

    }

    default void beforeUpdateClob(MethodExecutionContext executionContext) {

    }

    default void beforeUpdateDate(MethodExecutionContext executionContext) {

    }

    default void beforeUpdateDouble(MethodExecutionContext executionContext) {

    }

    default void beforeUpdateFloat(MethodExecutionContext executionContext) {

    }

    default void beforeUpdateInt(MethodExecutionContext executionContext) {

    }

    default void beforeUpdateLong(MethodExecutionContext executionContext) {

    }

    default void beforeUpdateNCharacterStream(MethodExecutionContext executionContext) {

    }

    default void beforeUpdateNClob(MethodExecutionContext executionContext) {

    }

    default void beforeUpdateNString(MethodExecutionContext executionContext) {

    }

    default void beforeUpdateNull(MethodExecutionContext executionContext) {

    }

    default void beforeUpdateObject(MethodExecutionContext executionContext) {

    }

    default void beforeUpdateRef(MethodExecutionContext executionContext) {

    }

    default void beforeUpdateRow(MethodExecutionContext executionContext) {

    }

    default void beforeUpdateRowId(MethodExecutionContext executionContext) {

    }

    default void beforeUpdateShort(MethodExecutionContext executionContext) {

    }

    default void beforeUpdateSQLXML(MethodExecutionContext executionContext) {

    }

    default void beforeUpdateString(MethodExecutionContext executionContext) {

    }

    default void beforeUpdateTime(MethodExecutionContext executionContext) {

    }

    default void beforeUpdateTimestamp(MethodExecutionContext executionContext) {

    }

    default void beforeWasNull(MethodExecutionContext executionContext) {

    }

    default void afterAbsolute(MethodExecutionContext executionContext) {

    }

    default void afterAfterLast(MethodExecutionContext executionContext) {

    }

    default void afterBeforeFirst(MethodExecutionContext executionContext) {

    }

    default void afterCancelRowUpdates(MethodExecutionContext executionContext) {

    }

    default void afterClearWarnings(MethodExecutionContext executionContext) {

    }

    default void afterClose(MethodExecutionContext executionContext) {

    }

    default void afterDeleteRow(MethodExecutionContext executionContext) {

    }

    default void afterFindColumn(MethodExecutionContext executionContext) {

    }

    default void afterFirst(MethodExecutionContext executionContext) {

    }

    default void afterGetArray(MethodExecutionContext executionContext) {

    }

    default void afterGetAsciiStream(MethodExecutionContext executionContext) {

    }

    default void afterGetBigDecimal(MethodExecutionContext executionContext) {

    }

    default void afterGetBinaryStream(MethodExecutionContext executionContext) {

    }

    default void afterGetBlob(MethodExecutionContext executionContext) {

    }

    default void afterGetBoolean(MethodExecutionContext executionContext) {

    }

    default void afterGetByte(MethodExecutionContext executionContext) {

    }

    default void afterGetBytes(MethodExecutionContext executionContext) {

    }

    default void afterGetCharacterStream(MethodExecutionContext executionContext) {

    }

    default void afterGetClob(MethodExecutionContext executionContext) {

    }

    default void afterGetConcurrency(MethodExecutionContext executionContext) {

    }

    default void afterGetCursorName(MethodExecutionContext executionContext) {

    }

    default void afterGetDate(MethodExecutionContext executionContext) {

    }

    default void afterGetDouble(MethodExecutionContext executionContext) {

    }

    default void afterGetFetchDirection(MethodExecutionContext executionContext) {

    }

    default void afterGetFetchSize(MethodExecutionContext executionContext) {

    }

    default void afterGetFloat(MethodExecutionContext executionContext) {

    }

    default void afterGetHoldability(MethodExecutionContext executionContext) {

    }

    default void afterGetInt(MethodExecutionContext executionContext) {

    }

    default void afterGetLong(MethodExecutionContext executionContext) {

    }

    default void afterGetMetaData(MethodExecutionContext executionContext) {

    }

    default void afterGetNCharacterStream(MethodExecutionContext executionContext) {

    }

    default void afterGetNClob(MethodExecutionContext executionContext) {

    }

    default void afterGetNString(MethodExecutionContext executionContext) {

    }

    default void afterGetObject(MethodExecutionContext executionContext) {

    }

    default void afterGetRef(MethodExecutionContext executionContext) {

    }

    default void afterGetRow(MethodExecutionContext executionContext) {

    }

    default void afterGetRowId(MethodExecutionContext executionContext) {

    }

    default void afterGetShort(MethodExecutionContext executionContext) {

    }

    default void afterGetSQLXML(MethodExecutionContext executionContext) {

    }

    default void afterGetStatement(MethodExecutionContext executionContext) {

    }

    default void afterGetString(MethodExecutionContext executionContext) {

    }

    default void afterGetTime(MethodExecutionContext executionContext) {

    }

    default void afterGetTimestamp(MethodExecutionContext executionContext) {

    }

    default void afterGetType(MethodExecutionContext executionContext) {

    }

    default void afterGetUnicodeStream(MethodExecutionContext executionContext) {

    }

    default void afterGetURL(MethodExecutionContext executionContext) {

    }

    default void afterGetWarnings(MethodExecutionContext executionContext) {

    }

    default void afterInsertRow(MethodExecutionContext executionContext) {

    }

    default void afterIsAfterLast(MethodExecutionContext executionContext) {

    }

    default void afterIsBeforeFirst(MethodExecutionContext executionContext) {

    }

    default void afterIsClosed(MethodExecutionContext executionContext) {

    }

    default void afterIsFirst(MethodExecutionContext executionContext) {

    }

    default void afterIsLast(MethodExecutionContext executionContext) {

    }

    default void afterLast(MethodExecutionContext executionContext) {

    }

    default void afterMoveToCurrentRow(MethodExecutionContext executionContext) {

    }

    default void afterMoveToInsertRow(MethodExecutionContext executionContext) {

    }

    default void afterNext(MethodExecutionContext executionContext) {

    }

    default void afterPrevious(MethodExecutionContext executionContext) {

    }

    default void afterRefreshRow(MethodExecutionContext executionContext) {

    }

    default void afterRelative(MethodExecutionContext executionContext) {

    }

    default void afterRowDeleted(MethodExecutionContext executionContext) {

    }

    default void afterRowInserted(MethodExecutionContext executionContext) {

    }

    default void afterRowUpdated(MethodExecutionContext executionContext) {

    }

    default void afterSetFetchDirection(MethodExecutionContext executionContext) {

    }

    default void afterSetFetchSize(MethodExecutionContext executionContext) {

    }

    default void afterUpdateArray(MethodExecutionContext executionContext) {

    }

    default void afterUpdateAsciiStream(MethodExecutionContext executionContext) {

    }

    default void afterUpdateBigDecimal(MethodExecutionContext executionContext) {

    }

    default void afterUpdateBinaryStream(MethodExecutionContext executionContext) {

    }

    default void afterUpdateBlob(MethodExecutionContext executionContext) {

    }

    default void afterUpdateBoolean(MethodExecutionContext executionContext) {

    }

    default void afterUpdateByte(MethodExecutionContext executionContext) {

    }

    default void afterUpdateBytes(MethodExecutionContext executionContext) {

    }

    default void afterUpdateCharacterStream(MethodExecutionContext executionContext) {

    }

    default void afterUpdateClob(MethodExecutionContext executionContext) {

    }

    default void afterUpdateDate(MethodExecutionContext executionContext) {

    }

    default void afterUpdateDouble(MethodExecutionContext executionContext) {

    }

    default void afterUpdateFloat(MethodExecutionContext executionContext) {

    }

    default void afterUpdateInt(MethodExecutionContext executionContext) {

    }

    default void afterUpdateLong(MethodExecutionContext executionContext) {

    }

    default void afterUpdateNCharacterStream(MethodExecutionContext executionContext) {

    }

    default void afterUpdateNClob(MethodExecutionContext executionContext) {

    }

    default void afterUpdateNString(MethodExecutionContext executionContext) {

    }

    default void afterUpdateNull(MethodExecutionContext executionContext) {

    }

    default void afterUpdateObject(MethodExecutionContext executionContext) {

    }

    default void afterUpdateRef(MethodExecutionContext executionContext) {

    }

    default void afterUpdateRow(MethodExecutionContext executionContext) {

    }

    default void afterUpdateRowId(MethodExecutionContext executionContext) {

    }

    default void afterUpdateShort(MethodExecutionContext executionContext) {

    }

    default void afterUpdateSQLXML(MethodExecutionContext executionContext) {

    }

    default void afterUpdateString(MethodExecutionContext executionContext) {

    }

    default void afterUpdateTime(MethodExecutionContext executionContext) {

    }

    default void afterUpdateTimestamp(MethodExecutionContext executionContext) {

    }

    default void afterWasNull(MethodExecutionContext executionContext) {

    }

}
