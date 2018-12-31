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
    // Wrapper methods
    //

    void beforeIsWrapperForOnCallableStatement(MethodExecutionContext executionContext);

    void beforeUnwrapOnCallableStatement(MethodExecutionContext executionContext);

    void afterIsWrapperForOnCallableStatement(MethodExecutionContext executionContext);

    void afterUnwrapOnCallableStatement(MethodExecutionContext executionContext);

    //
    // CallableStatement methods
    //

    void beforeGetArrayOnCallableStatement(MethodExecutionContext executionContext);

    void beforeGetBigDecimalOnCallableStatement(MethodExecutionContext executionContext);

    void beforeGetBlobOnCallableStatement(MethodExecutionContext executionContext);

    void beforeGetBooleanOnCallableStatement(MethodExecutionContext executionContext);

    void beforeGetByteOnCallableStatement(MethodExecutionContext executionContext);

    void beforeGetBytesOnCallableStatement(MethodExecutionContext executionContext);

    void beforeGetCharacterStreamOnCallableStatement(MethodExecutionContext executionContext);

    void beforeGetClobOnCallableStatement(MethodExecutionContext executionContext);

    void beforeGetDateOnCallableStatement(MethodExecutionContext executionContext);

    void beforeGetDoubleOnCallableStatement(MethodExecutionContext executionContext);

    void beforeGetFloatOnCallableStatement(MethodExecutionContext executionContext);

    void beforeGetIntOnCallableStatement(MethodExecutionContext executionContext);

    void beforeGetLongOnCallableStatement(MethodExecutionContext executionContext);

    void beforeGetNCharacterStreamOnCallableStatement(MethodExecutionContext executionContext);

    void beforeGetNClobOnCallableStatement(MethodExecutionContext executionContext);

    void beforeGetNStringOnCallableStatement(MethodExecutionContext executionContext);

    void beforeGetObjectOnCallableStatement(MethodExecutionContext executionContext);

    void beforeGetRefOnCallableStatement(MethodExecutionContext executionContext);

    void beforeGetRowIdOnCallableStatement(MethodExecutionContext executionContext);

    void beforeGetShortOnCallableStatement(MethodExecutionContext executionContext);

    void beforeGetSQLXMLOnCallableStatement(MethodExecutionContext executionContext);

    void beforeGetStringOnCallableStatement(MethodExecutionContext executionContext);

    void beforeGetTimeOnCallableStatement(MethodExecutionContext executionContext);

    void beforeGetTimestampOnCallableStatement(MethodExecutionContext executionContext);

    void beforeGetURLOnCallableStatement(MethodExecutionContext executionContext);

    void beforeRegisterOutParameterOnCallableStatement(MethodExecutionContext executionContext);

    void beforeSetAsciiStreamOnCallableStatement(MethodExecutionContext executionContext);

    void beforeSetBigDecimalOnCallableStatement(MethodExecutionContext executionContext);

    void beforeSetBinaryStreamOnCallableStatement(MethodExecutionContext executionContext);

    void beforeSetBlobOnCallableStatement(MethodExecutionContext executionContext);

    void beforeSetBooleanOnCallableStatement(MethodExecutionContext executionContext);

    void beforeSetByteOnCallableStatement(MethodExecutionContext executionContext);

    void beforeSetBytesOnCallableStatement(MethodExecutionContext executionContext);

    void beforeSetCharacterStreamOnCallableStatement(MethodExecutionContext executionContext);

    void beforeSetClobOnCallableStatement(MethodExecutionContext executionContext);

    void beforeSetDateOnCallableStatement(MethodExecutionContext executionContext);

    void beforeSetDoubleOnCallableStatement(MethodExecutionContext executionContext);

    void beforeSetFloatOnCallableStatement(MethodExecutionContext executionContext);

    void beforeSetIntOnCallableStatement(MethodExecutionContext executionContext);

    void beforeSetLongOnCallableStatement(MethodExecutionContext executionContext);

    void beforeSetNCharacterStreamOnCallableStatement(MethodExecutionContext executionContext);

    void beforeSetNClobOnCallableStatement(MethodExecutionContext executionContext);

    void beforeSetNStringOnCallableStatement(MethodExecutionContext executionContext);

    void beforeSetNullOnCallableStatement(MethodExecutionContext executionContext);

    void beforeSetObjectOnCallableStatement(MethodExecutionContext executionContext);

    void beforeSetRowIdOnCallableStatement(MethodExecutionContext executionContext);

    void beforeSetShortOnCallableStatement(MethodExecutionContext executionContext);

    void beforeSetSQLXMLOnCallableStatement(MethodExecutionContext executionContext);

    void beforeSetStringOnCallableStatement(MethodExecutionContext executionContext);

    void beforeSetTimeOnCallableStatement(MethodExecutionContext executionContext);

    void beforeSetTimestampOnCallableStatement(MethodExecutionContext executionContext);

    void beforeSetURLOnCallableStatement(MethodExecutionContext executionContext);

    void beforeWasNullOnCallableStatement(MethodExecutionContext executionContext);

    void afterGetArrayOnCallableStatement(MethodExecutionContext executionContext);

    void afterGetBigDecimalOnCallableStatement(MethodExecutionContext executionContext);

    void afterGetBlobOnCallableStatement(MethodExecutionContext executionContext);

    void afterGetBooleanOnCallableStatement(MethodExecutionContext executionContext);

    void afterGetByteOnCallableStatement(MethodExecutionContext executionContext);

    void afterGetBytesOnCallableStatement(MethodExecutionContext executionContext);

    void afterGetCharacterStreamOnCallableStatement(MethodExecutionContext executionContext);

    void afterGetClobOnCallableStatement(MethodExecutionContext executionContext);

    void afterGetDateOnCallableStatement(MethodExecutionContext executionContext);

    void afterGetDoubleOnCallableStatement(MethodExecutionContext executionContext);

    void afterGetFloatOnCallableStatement(MethodExecutionContext executionContext);

    void afterGetIntOnCallableStatement(MethodExecutionContext executionContext);

    void afterGetLongOnCallableStatement(MethodExecutionContext executionContext);

    void afterGetNCharacterStreamOnCallableStatement(MethodExecutionContext executionContext);

    void afterGetNClobOnCallableStatement(MethodExecutionContext executionContext);

    void afterGetNStringOnCallableStatement(MethodExecutionContext executionContext);

    void afterGetObjectOnCallableStatement(MethodExecutionContext executionContext);

    void afterGetRefOnCallableStatement(MethodExecutionContext executionContext);

    void afterGetRowIdOnCallableStatement(MethodExecutionContext executionContext);

    void afterGetShortOnCallableStatement(MethodExecutionContext executionContext);

    void afterGetSQLXMLOnCallableStatement(MethodExecutionContext executionContext);

    void afterGetStringOnCallableStatement(MethodExecutionContext executionContext);

    void afterGetTimeOnCallableStatement(MethodExecutionContext executionContext);

    void afterGetTimestampOnCallableStatement(MethodExecutionContext executionContext);

    void afterGetURLOnCallableStatement(MethodExecutionContext executionContext);

    void afterRegisterOutParameterOnCallableStatement(MethodExecutionContext executionContext);

    void afterSetAsciiStreamOnCallableStatement(MethodExecutionContext executionContext);

    void afterSetBigDecimalOnCallableStatement(MethodExecutionContext executionContext);

    void afterSetBinaryStreamOnCallableStatement(MethodExecutionContext executionContext);

    void afterSetBlobOnCallableStatement(MethodExecutionContext executionContext);

    void afterSetBooleanOnCallableStatement(MethodExecutionContext executionContext);

    void afterSetByteOnCallableStatement(MethodExecutionContext executionContext);

    void afterSetBytesOnCallableStatement(MethodExecutionContext executionContext);

    void afterSetCharacterStreamOnCallableStatement(MethodExecutionContext executionContext);

    void afterSetClobOnCallableStatement(MethodExecutionContext executionContext);

    void afterSetDateOnCallableStatement(MethodExecutionContext executionContext);

    void afterSetDoubleOnCallableStatement(MethodExecutionContext executionContext);

    void afterSetFloatOnCallableStatement(MethodExecutionContext executionContext);

    void afterSetIntOnCallableStatement(MethodExecutionContext executionContext);

    void afterSetLongOnCallableStatement(MethodExecutionContext executionContext);

    void afterSetNCharacterStreamOnCallableStatement(MethodExecutionContext executionContext);

    void afterSetNClobOnCallableStatement(MethodExecutionContext executionContext);

    void afterSetNStringOnCallableStatement(MethodExecutionContext executionContext);

    void afterSetNullOnCallableStatement(MethodExecutionContext executionContext);

    void afterSetObjectOnCallableStatement(MethodExecutionContext executionContext);

    void afterSetRowIdOnCallableStatement(MethodExecutionContext executionContext);

    void afterSetShortOnCallableStatement(MethodExecutionContext executionContext);

    void afterSetSQLXMLOnCallableStatement(MethodExecutionContext executionContext);

    void afterSetStringOnCallableStatement(MethodExecutionContext executionContext);

    void afterSetTimeOnCallableStatement(MethodExecutionContext executionContext);

    void afterSetTimestampOnCallableStatement(MethodExecutionContext executionContext);

    void afterSetURLOnCallableStatement(MethodExecutionContext executionContext);

    void afterWasNullOnCallableStatement(MethodExecutionContext executionContext);

}
