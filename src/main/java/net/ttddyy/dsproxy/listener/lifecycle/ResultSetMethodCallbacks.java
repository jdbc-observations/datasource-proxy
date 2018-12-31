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
    // Wrapper methods
    //
    void beforeIsWrapperForOnResultSet(MethodExecutionContext executionContext);

    void beforeUnwrapOnResultSet(MethodExecutionContext executionContext);

    void afterIsWrapperForOnResultSet(MethodExecutionContext executionContext);

    void afterUnwrapOnResultSet(MethodExecutionContext executionContext);

    //
    // ResultSet methods
    //

    void beforeAbsoluteOnResultSet(MethodExecutionContext executionContext);

    void beforeAfterLastOnResultSet(MethodExecutionContext executionContext);

    void beforeBeforeFirstOnResultSet(MethodExecutionContext executionContext);

    void beforeCancelRowUpdatesOnResultSet(MethodExecutionContext executionContext);

    void beforeClearWarningsOnResultSet(MethodExecutionContext executionContext);

    void beforeCloseOnResultSet(MethodExecutionContext executionContext);

    void beforeDeleteRowOnResultSet(MethodExecutionContext executionContext);

    void beforeFindColumnOnResultSet(MethodExecutionContext executionContext);

    void beforeFirstOnResultSet(MethodExecutionContext executionContext);

    void beforeGetArrayOnResultSet(MethodExecutionContext executionContext);

    void beforeGetAsciiStreamOnResultSet(MethodExecutionContext executionContext);

    void beforeGetBigDecimalOnResultSet(MethodExecutionContext executionContext);

    void beforeGetBinaryStreamOnResultSet(MethodExecutionContext executionContext);

    void beforeGetBlobOnResultSet(MethodExecutionContext executionContext);

    void beforeGetBooleanOnResultSet(MethodExecutionContext executionContext);

    void beforeGetByteOnResultSet(MethodExecutionContext executionContext);

    void beforeGetBytesOnResultSet(MethodExecutionContext executionContext);

    void beforeGetCharacterStreamOnResultSet(MethodExecutionContext executionContext);

    void beforeGetClobOnResultSet(MethodExecutionContext executionContext);

    void beforeGetConcurrencyOnResultSet(MethodExecutionContext executionContext);

    void beforeGetCursorNameOnResultSet(MethodExecutionContext executionContext);

    void beforeGetDateOnResultSet(MethodExecutionContext executionContext);

    void beforeGetDoubleOnResultSet(MethodExecutionContext executionContext);

    void beforeGetFetchDirectionOnResultSet(MethodExecutionContext executionContext);

    void beforeGetFetchSizeOnResultSet(MethodExecutionContext executionContext);

    void beforeGetFloatOnResultSet(MethodExecutionContext executionContext);

    void beforeGetHoldabilityOnResultSet(MethodExecutionContext executionContext);

    void beforeGetIntOnResultSet(MethodExecutionContext executionContext);

    void beforeGetLongOnResultSet(MethodExecutionContext executionContext);

    void beforeGetMetaDataOnResultSet(MethodExecutionContext executionContext);

    void beforeGetNCharacterStreamOnResultSet(MethodExecutionContext executionContext);

    void beforeGetNClobOnResultSet(MethodExecutionContext executionContext);

    void beforeGetNStringOnResultSet(MethodExecutionContext executionContext);

    void beforeGetObjectOnResultSet(MethodExecutionContext executionContext);

    void beforeGetRefOnResultSet(MethodExecutionContext executionContext);

    void beforeGetRowOnResultSet(MethodExecutionContext executionContext);

    void beforeGetRowIdOnResultSet(MethodExecutionContext executionContext);

    void beforeGetShortOnResultSet(MethodExecutionContext executionContext);

    void beforeGetSQLXMLOnResultSet(MethodExecutionContext executionContext);

    void beforeGetStatementOnResultSet(MethodExecutionContext executionContext);

    void beforeGetStringOnResultSet(MethodExecutionContext executionContext);

    void beforeGetTimeOnResultSet(MethodExecutionContext executionContext);

    void beforeGetTimestampOnResultSet(MethodExecutionContext executionContext);

    void beforeGetTypeOnResultSet(MethodExecutionContext executionContext);

    void beforeGetUnicodeStreamOnResultSet(MethodExecutionContext executionContext);

    void beforeGetURLOnResultSet(MethodExecutionContext executionContext);

    void beforeGetWarningsOnResultSet(MethodExecutionContext executionContext);

    void beforeInsertRowOnResultSet(MethodExecutionContext executionContext);

    void beforeIsAfterLastOnResultSet(MethodExecutionContext executionContext);

    void beforeIsBeforeFirstOnResultSet(MethodExecutionContext executionContext);

    void beforeIsClosedOnResultSet(MethodExecutionContext executionContext);

    void beforeIsFirstOnResultSet(MethodExecutionContext executionContext);

    void beforeIsLastOnResultSet(MethodExecutionContext executionContext);

    void beforeLastOnResultSet(MethodExecutionContext executionContext);

    void beforeMoveToCurrentRowOnResultSet(MethodExecutionContext executionContext);

    void beforeMoveToInsertRowOnResultSet(MethodExecutionContext executionContext);

    void beforeNextOnResultSet(MethodExecutionContext executionContext);

    void beforePreviousOnResultSet(MethodExecutionContext executionContext);

    void beforeRefreshRowOnResultSet(MethodExecutionContext executionContext);

    void beforeRelativeOnResultSet(MethodExecutionContext executionContext);

    void beforeRowDeletedOnResultSet(MethodExecutionContext executionContext);

    void beforeRowInsertedOnResultSet(MethodExecutionContext executionContext);

    void beforeRowUpdatedOnResultSet(MethodExecutionContext executionContext);

    void beforeSetFetchDirectionOnResultSet(MethodExecutionContext executionContext);

    void beforeSetFetchSizeOnResultSet(MethodExecutionContext executionContext);

    void beforeUpdateArrayOnResultSet(MethodExecutionContext executionContext);

    void beforeUpdateAsciiStreamOnResultSet(MethodExecutionContext executionContext);

    void beforeUpdateBigDecimalOnResultSet(MethodExecutionContext executionContext);

    void beforeUpdateBinaryStreamOnResultSet(MethodExecutionContext executionContext);

    void beforeUpdateBlobOnResultSet(MethodExecutionContext executionContext);

    void beforeUpdateBooleanOnResultSet(MethodExecutionContext executionContext);

    void beforeUpdateByteOnResultSet(MethodExecutionContext executionContext);

    void beforeUpdateBytesOnResultSet(MethodExecutionContext executionContext);

    void beforeUpdateCharacterStreamOnResultSet(MethodExecutionContext executionContext);

    void beforeUpdateClobOnResultSet(MethodExecutionContext executionContext);

    void beforeUpdateDateOnResultSet(MethodExecutionContext executionContext);

    void beforeUpdateDoubleOnResultSet(MethodExecutionContext executionContext);

    void beforeUpdateFloatOnResultSet(MethodExecutionContext executionContext);

    void beforeUpdateIntOnResultSet(MethodExecutionContext executionContext);

    void beforeUpdateLongOnResultSet(MethodExecutionContext executionContext);

    void beforeUpdateNCharacterStreamOnResultSet(MethodExecutionContext executionContext);

    void beforeUpdateNClobOnResultSet(MethodExecutionContext executionContext);

    void beforeUpdateNStringOnResultSet(MethodExecutionContext executionContext);

    void beforeUpdateNullOnResultSet(MethodExecutionContext executionContext);

    void beforeUpdateObjectOnResultSet(MethodExecutionContext executionContext);

    void beforeUpdateRefOnResultSet(MethodExecutionContext executionContext);

    void beforeUpdateRowOnResultSet(MethodExecutionContext executionContext);

    void beforeUpdateRowIdOnResultSet(MethodExecutionContext executionContext);

    void beforeUpdateShortOnResultSet(MethodExecutionContext executionContext);

    void beforeUpdateSQLXMLOnResultSet(MethodExecutionContext executionContext);

    void beforeUpdateStringOnResultSet(MethodExecutionContext executionContext);

    void beforeUpdateTimeOnResultSet(MethodExecutionContext executionContext);

    void beforeUpdateTimestampOnResultSet(MethodExecutionContext executionContext);

    void beforeWasNullOnResultSet(MethodExecutionContext executionContext);

    void afterAbsoluteOnResultSet(MethodExecutionContext executionContext);

    void afterAfterLastOnResultSet(MethodExecutionContext executionContext);

    void afterBeforeFirstOnResultSet(MethodExecutionContext executionContext);

    void afterCancelRowUpdatesOnResultSet(MethodExecutionContext executionContext);

    void afterClearWarningsOnResultSet(MethodExecutionContext executionContext);

    void afterCloseOnResultSet(MethodExecutionContext executionContext);

    void afterDeleteRowOnResultSet(MethodExecutionContext executionContext);

    void afterFindColumnOnResultSet(MethodExecutionContext executionContext);

    void afterFirstOnResultSet(MethodExecutionContext executionContext);

    void afterGetArrayOnResultSet(MethodExecutionContext executionContext);

    void afterGetAsciiStreamOnResultSet(MethodExecutionContext executionContext);

    void afterGetBigDecimalOnResultSet(MethodExecutionContext executionContext);

    void afterGetBinaryStreamOnResultSet(MethodExecutionContext executionContext);

    void afterGetBlobOnResultSet(MethodExecutionContext executionContext);

    void afterGetBooleanOnResultSet(MethodExecutionContext executionContext);

    void afterGetByteOnResultSet(MethodExecutionContext executionContext);

    void afterGetBytesOnResultSet(MethodExecutionContext executionContext);

    void afterGetCharacterStreamOnResultSet(MethodExecutionContext executionContext);

    void afterGetClobOnResultSet(MethodExecutionContext executionContext);

    void afterGetConcurrencyOnResultSet(MethodExecutionContext executionContext);

    void afterGetCursorNameOnResultSet(MethodExecutionContext executionContext);

    void afterGetDateOnResultSet(MethodExecutionContext executionContext);

    void afterGetDoubleOnResultSet(MethodExecutionContext executionContext);

    void afterGetFetchDirectionOnResultSet(MethodExecutionContext executionContext);

    void afterGetFetchSizeOnResultSet(MethodExecutionContext executionContext);

    void afterGetFloatOnResultSet(MethodExecutionContext executionContext);

    void afterGetHoldabilityOnResultSet(MethodExecutionContext executionContext);

    void afterGetIntOnResultSet(MethodExecutionContext executionContext);

    void afterGetLongOnResultSet(MethodExecutionContext executionContext);

    void afterGetMetaDataOnResultSet(MethodExecutionContext executionContext);

    void afterGetNCharacterStreamOnResultSet(MethodExecutionContext executionContext);

    void afterGetNClobOnResultSet(MethodExecutionContext executionContext);

    void afterGetNStringOnResultSet(MethodExecutionContext executionContext);

    void afterGetObjectOnResultSet(MethodExecutionContext executionContext);

    void afterGetRefOnResultSet(MethodExecutionContext executionContext);

    void afterGetRowOnResultSet(MethodExecutionContext executionContext);

    void afterGetRowIdOnResultSet(MethodExecutionContext executionContext);

    void afterGetShortOnResultSet(MethodExecutionContext executionContext);

    void afterGetSQLXMLOnResultSet(MethodExecutionContext executionContext);

    void afterGetStatementOnResultSet(MethodExecutionContext executionContext);

    void afterGetStringOnResultSet(MethodExecutionContext executionContext);

    void afterGetTimeOnResultSet(MethodExecutionContext executionContext);

    void afterGetTimestampOnResultSet(MethodExecutionContext executionContext);

    void afterGetTypeOnResultSet(MethodExecutionContext executionContext);

    void afterGetUnicodeStreamOnResultSet(MethodExecutionContext executionContext);

    void afterGetURLOnResultSet(MethodExecutionContext executionContext);

    void afterGetWarningsOnResultSet(MethodExecutionContext executionContext);

    void afterInsertRowOnResultSet(MethodExecutionContext executionContext);

    void afterIsAfterLastOnResultSet(MethodExecutionContext executionContext);

    void afterIsBeforeFirstOnResultSet(MethodExecutionContext executionContext);

    void afterIsClosedOnResultSet(MethodExecutionContext executionContext);

    void afterIsFirstOnResultSet(MethodExecutionContext executionContext);

    void afterIsLastOnResultSet(MethodExecutionContext executionContext);

    void afterLastOnResultSet(MethodExecutionContext executionContext);

    void afterMoveToCurrentRowOnResultSet(MethodExecutionContext executionContext);

    void afterMoveToInsertRowOnResultSet(MethodExecutionContext executionContext);

    void afterNextOnResultSet(MethodExecutionContext executionContext);

    void afterPreviousOnResultSet(MethodExecutionContext executionContext);

    void afterRefreshRowOnResultSet(MethodExecutionContext executionContext);

    void afterRelativeOnResultSet(MethodExecutionContext executionContext);

    void afterRowDeletedOnResultSet(MethodExecutionContext executionContext);

    void afterRowInsertedOnResultSet(MethodExecutionContext executionContext);

    void afterRowUpdatedOnResultSet(MethodExecutionContext executionContext);

    void afterSetFetchDirectionOnResultSet(MethodExecutionContext executionContext);

    void afterSetFetchSizeOnResultSet(MethodExecutionContext executionContext);

    void afterUpdateArrayOnResultSet(MethodExecutionContext executionContext);

    void afterUpdateAsciiStreamOnResultSet(MethodExecutionContext executionContext);

    void afterUpdateBigDecimalOnResultSet(MethodExecutionContext executionContext);

    void afterUpdateBinaryStreamOnResultSet(MethodExecutionContext executionContext);

    void afterUpdateBlobOnResultSet(MethodExecutionContext executionContext);

    void afterUpdateBooleanOnResultSet(MethodExecutionContext executionContext);

    void afterUpdateByteOnResultSet(MethodExecutionContext executionContext);

    void afterUpdateBytesOnResultSet(MethodExecutionContext executionContext);

    void afterUpdateCharacterStreamOnResultSet(MethodExecutionContext executionContext);

    void afterUpdateClobOnResultSet(MethodExecutionContext executionContext);

    void afterUpdateDateOnResultSet(MethodExecutionContext executionContext);

    void afterUpdateDoubleOnResultSet(MethodExecutionContext executionContext);

    void afterUpdateFloatOnResultSet(MethodExecutionContext executionContext);

    void afterUpdateIntOnResultSet(MethodExecutionContext executionContext);

    void afterUpdateLongOnResultSet(MethodExecutionContext executionContext);

    void afterUpdateNCharacterStreamOnResultSet(MethodExecutionContext executionContext);

    void afterUpdateNClobOnResultSet(MethodExecutionContext executionContext);

    void afterUpdateNStringOnResultSet(MethodExecutionContext executionContext);

    void afterUpdateNullOnResultSet(MethodExecutionContext executionContext);

    void afterUpdateObjectOnResultSet(MethodExecutionContext executionContext);

    void afterUpdateRefOnResultSet(MethodExecutionContext executionContext);

    void afterUpdateRowOnResultSet(MethodExecutionContext executionContext);

    void afterUpdateRowIdOnResultSet(MethodExecutionContext executionContext);

    void afterUpdateShortOnResultSet(MethodExecutionContext executionContext);

    void afterUpdateSQLXMLOnResultSet(MethodExecutionContext executionContext);

    void afterUpdateStringOnResultSet(MethodExecutionContext executionContext);

    void afterUpdateTimeOnResultSet(MethodExecutionContext executionContext);

    void afterUpdateTimestampOnResultSet(MethodExecutionContext executionContext);

    void afterWasNullOnResultSet(MethodExecutionContext executionContext);

}
