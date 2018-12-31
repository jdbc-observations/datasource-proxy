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
    // Wrapper methods
    //

    void beforeIsWrapperForOnPreparedStatement(MethodExecutionContext executionContext);

    void beforeUnwrapOnPreparedStatement(MethodExecutionContext executionContext);

    void afterIsWrapperForOnPreparedStatement(MethodExecutionContext executionContext);

    void afterUnwrapOnPreparedStatement(MethodExecutionContext executionContext);

    //
    // PreparedStatement methods
    //

    void beforeAddBatchOnPreparedStatement(MethodExecutionContext executionContext);

    void beforeClearParametersOnPreparedStatement(MethodExecutionContext executionContext);

    void beforeExecuteOnPreparedStatement(MethodExecutionContext executionContext);

    void beforeExecuteLargeUpdateOnPreparedStatement(MethodExecutionContext executionContext);

    void beforeExecuteQueryOnPreparedStatement(MethodExecutionContext executionContext);

    void beforeExecuteUpdateOnPreparedStatement(MethodExecutionContext executionContext);

    void beforeGetMetaDataOnPreparedStatement(MethodExecutionContext executionContext);

    void beforeGetParameterMetaDataOnPreparedStatement(MethodExecutionContext executionContext);

    void beforeSetArrayOnPreparedStatement(MethodExecutionContext executionContext);

    void beforeSetAsciiStreamOnPreparedStatement(MethodExecutionContext executionContext);

    void beforeSetBigDecimalOnPreparedStatement(MethodExecutionContext executionContext);

    void beforeSetBinaryStreamOnPreparedStatement(MethodExecutionContext executionContext);

    void beforeSetBlobOnPreparedStatement(MethodExecutionContext executionContext);

    void beforeSetBooleanOnPreparedStatement(MethodExecutionContext executionContext);

    void beforeSetByteOnPreparedStatement(MethodExecutionContext executionContext);

    void beforeSetBytesOnPreparedStatement(MethodExecutionContext executionContext);

    void beforeSetCharacterStreamOnPreparedStatement(MethodExecutionContext executionContext);

    void beforeSetClobOnPreparedStatement(MethodExecutionContext executionContext);

    void beforeSetDateOnPreparedStatement(MethodExecutionContext executionContext);

    void beforeSetDoubleOnPreparedStatement(MethodExecutionContext executionContext);

    void beforeSetFloatOnPreparedStatement(MethodExecutionContext executionContext);

    void beforeSetIntOnPreparedStatement(MethodExecutionContext executionContext);

    void beforeSetLongOnPreparedStatement(MethodExecutionContext executionContext);

    void beforeSetNCharacterStreamOnPreparedStatement(MethodExecutionContext executionContext);

    void beforeSetNClobOnPreparedStatement(MethodExecutionContext executionContext);

    void beforeSetNStringOnPreparedStatement(MethodExecutionContext executionContext);

    void beforeSetNullOnPreparedStatement(MethodExecutionContext executionContext);

    void beforeSetObjectOnPreparedStatement(MethodExecutionContext executionContext);

    void beforeSetRefOnPreparedStatement(MethodExecutionContext executionContext);

    void beforeSetRowIdOnPreparedStatement(MethodExecutionContext executionContext);

    void beforeSetShortOnPreparedStatement(MethodExecutionContext executionContext);

    void beforeSetSQLXMLOnPreparedStatement(MethodExecutionContext executionContext);

    void beforeSetStringOnPreparedStatement(MethodExecutionContext executionContext);

    void beforeSetTimeOnPreparedStatement(MethodExecutionContext executionContext);

    void beforeSetTimestampOnPreparedStatement(MethodExecutionContext executionContext);

    void beforeSetUnicodeStreamOnPreparedStatement(MethodExecutionContext executionContext);

    void beforeSetURLOnPreparedStatement(MethodExecutionContext executionContext);

    void afterAddBatchOnPreparedStatement(MethodExecutionContext executionContext);

    void afterClearParametersOnPreparedStatement(MethodExecutionContext executionContext);

    void afterExecuteOnPreparedStatement(MethodExecutionContext executionContext);

    void afterExecuteLargeUpdateOnPreparedStatement(MethodExecutionContext executionContext);

    void afterExecuteQueryOnPreparedStatement(MethodExecutionContext executionContext);

    void afterExecuteUpdateOnPreparedStatement(MethodExecutionContext executionContext);

    void afterGetMetaDataOnPreparedStatement(MethodExecutionContext executionContext);

    void afterGetParameterMetaDataOnPreparedStatement(MethodExecutionContext executionContext);

    void afterSetArrayOnPreparedStatement(MethodExecutionContext executionContext);

    void afterSetAsciiStreamOnPreparedStatement(MethodExecutionContext executionContext);

    void afterSetBigDecimalOnPreparedStatement(MethodExecutionContext executionContext);

    void afterSetBinaryStreamOnPreparedStatement(MethodExecutionContext executionContext);

    void afterSetBlobOnPreparedStatement(MethodExecutionContext executionContext);

    void afterSetBooleanOnPreparedStatement(MethodExecutionContext executionContext);

    void afterSetByteOnPreparedStatement(MethodExecutionContext executionContext);

    void afterSetBytesOnPreparedStatement(MethodExecutionContext executionContext);

    void afterSetCharacterStreamOnPreparedStatement(MethodExecutionContext executionContext);

    void afterSetClobOnPreparedStatement(MethodExecutionContext executionContext);

    void afterSetDateOnPreparedStatement(MethodExecutionContext executionContext);

    void afterSetDoubleOnPreparedStatement(MethodExecutionContext executionContext);

    void afterSetFloatOnPreparedStatement(MethodExecutionContext executionContext);

    void afterSetIntOnPreparedStatement(MethodExecutionContext executionContext);

    void afterSetLongOnPreparedStatement(MethodExecutionContext executionContext);

    void afterSetNCharacterStreamOnPreparedStatement(MethodExecutionContext executionContext);

    void afterSetNClobOnPreparedStatement(MethodExecutionContext executionContext);

    void afterSetNStringOnPreparedStatement(MethodExecutionContext executionContext);

    void afterSetNullOnPreparedStatement(MethodExecutionContext executionContext);

    void afterSetObjectOnPreparedStatement(MethodExecutionContext executionContext);

    void afterSetRefOnPreparedStatement(MethodExecutionContext executionContext);

    void afterSetRowIdOnPreparedStatement(MethodExecutionContext executionContext);

    void afterSetShortOnPreparedStatement(MethodExecutionContext executionContext);

    void afterSetSQLXMLOnPreparedStatement(MethodExecutionContext executionContext);

    void afterSetStringOnPreparedStatement(MethodExecutionContext executionContext);

    void afterSetTimeOnPreparedStatement(MethodExecutionContext executionContext);

    void afterSetTimestampOnPreparedStatement(MethodExecutionContext executionContext);

    void afterSetUnicodeStreamOnPreparedStatement(MethodExecutionContext executionContext);

    void afterSetURLOnPreparedStatement(MethodExecutionContext executionContext);

}
