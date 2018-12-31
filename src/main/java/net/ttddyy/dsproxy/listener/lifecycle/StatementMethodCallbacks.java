package net.ttddyy.dsproxy.listener.lifecycle;

import net.ttddyy.dsproxy.listener.MethodExecutionContext;

/**
 * Defines callback methods for {@link java.sql.Statement}.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.5
 */
public interface StatementMethodCallbacks {

    //
    // Wrapper methods
    //

    void beforeIsWrapperForOnStatement(MethodExecutionContext executionContext);

    void beforeUnwrapOnStatement(MethodExecutionContext executionContext);

    void afterIsWrapperForOnStatement(MethodExecutionContext executionContext);

    void afterUnwrapOnStatement(MethodExecutionContext executionContext);

    //
    // Statement methods
    //

    void beforeAddBatchOnStatement(MethodExecutionContext executionContext);

    void beforeCancelOnStatement(MethodExecutionContext executionContext);

    void beforeClearBatchOnStatement(MethodExecutionContext executionContext);

    void beforeClearWarningsOnStatement(MethodExecutionContext executionContext);

    void beforeCloseOnStatement(MethodExecutionContext executionContext);

    void beforeCloseOnCompletionOnStatement(MethodExecutionContext executionContext);

    void beforeExecuteOnStatement(MethodExecutionContext executionContext);

    void beforeExecuteBatchOnStatement(MethodExecutionContext executionContext);

    void beforeExecuteLargeBatchOnStatement(MethodExecutionContext executionContext);

    void beforeExecuteLargeUpdateOnStatement(MethodExecutionContext executionContext);

    void beforeExecuteQueryOnStatement(MethodExecutionContext executionContext);

    void beforeExecuteUpdateOnStatement(MethodExecutionContext executionContext);

    void beforeGetConnectionOnStatement(MethodExecutionContext executionContext);

    void beforeGetFetchDirectionOnStatement(MethodExecutionContext executionContext);

    void beforeGetFetchSizeOnStatement(MethodExecutionContext executionContext);

    void beforeGetGeneratedKeysOnStatement(MethodExecutionContext executionContext);

    void beforeGetLargeMaxRowsOnStatement(MethodExecutionContext executionContext);

    void beforeGetLargeUpdateCountOnStatement(MethodExecutionContext executionContext);

    void beforeGetMaxFieldSizeOnStatement(MethodExecutionContext executionContext);

    void beforeGetMaxRowsOnStatement(MethodExecutionContext executionContext);

    void beforeGetMoreResultsOnStatement(MethodExecutionContext executionContext);

    void beforeGetQueryTimeoutOnStatement(MethodExecutionContext executionContext);

    void beforeGetResultSetOnStatement(MethodExecutionContext executionContext);

    void beforeGetResultSetConcurrencyOnStatement(MethodExecutionContext executionContext);

    void beforeGetResultSetHoldabilityOnStatement(MethodExecutionContext executionContext);

    void beforeGetResultSetTypeOnStatement(MethodExecutionContext executionContext);

    void beforeGetUpdateCountOnStatement(MethodExecutionContext executionContext);

    void beforeGetWarningsOnStatement(MethodExecutionContext executionContext);

    void beforeIsClosedOnStatement(MethodExecutionContext executionContext);

    void beforeIsCloseOnCompletionOnStatement(MethodExecutionContext executionContext);

    void beforeIsPoolableOnStatement(MethodExecutionContext executionContext);

    void beforeSetCursorNameOnStatement(MethodExecutionContext executionContext);

    void beforeSetEscapeProcessingOnStatement(MethodExecutionContext executionContext);

    void beforeSetFetchDirectionOnStatement(MethodExecutionContext executionContext);

    void beforeSetFetchSizeOnStatement(MethodExecutionContext executionContext);

    void beforeSetLargeMaxRowsOnStatement(MethodExecutionContext executionContext);

    void beforeSetMaxFieldSizeOnStatement(MethodExecutionContext executionContext);

    void beforeSetMaxRowsOnStatement(MethodExecutionContext executionContext);

    void beforeSetPoolableOnStatement(MethodExecutionContext executionContext);

    void beforeSetQueryTimeoutOnStatement(MethodExecutionContext executionContext);

    void afterAddBatchOnStatement(MethodExecutionContext executionContext);

    void afterCancelOnStatement(MethodExecutionContext executionContext);

    void afterClearBatchOnStatement(MethodExecutionContext executionContext);

    void afterClearWarningsOnStatement(MethodExecutionContext executionContext);

    void afterCloseOnStatement(MethodExecutionContext executionContext);

    void afterCloseOnCompletionOnStatement(MethodExecutionContext executionContext);

    void afterExecuteOnStatement(MethodExecutionContext executionContext);

    void afterExecuteBatchOnStatement(MethodExecutionContext executionContext);

    void afterExecuteLargeBatchOnStatement(MethodExecutionContext executionContext);

    void afterExecuteLargeUpdateOnStatement(MethodExecutionContext executionContext);

    void afterExecuteQueryOnStatement(MethodExecutionContext executionContext);

    void afterExecuteUpdateOnStatement(MethodExecutionContext executionContext);

    void afterGetConnectionOnStatement(MethodExecutionContext executionContext);

    void afterGetFetchDirectionOnStatement(MethodExecutionContext executionContext);

    void afterGetFetchSizeOnStatement(MethodExecutionContext executionContext);

    void afterGetGeneratedKeysOnStatement(MethodExecutionContext executionContext);

    void afterGetLargeMaxRowsOnStatement(MethodExecutionContext executionContext);

    void afterGetLargeUpdateCountOnStatement(MethodExecutionContext executionContext);

    void afterGetMaxFieldSizeOnStatement(MethodExecutionContext executionContext);

    void afterGetMaxRowsOnStatement(MethodExecutionContext executionContext);

    void afterGetMoreResultsOnStatement(MethodExecutionContext executionContext);

    void afterGetQueryTimeoutOnStatement(MethodExecutionContext executionContext);

    void afterGetResultSetOnStatement(MethodExecutionContext executionContext);

    void afterGetResultSetConcurrencyOnStatement(MethodExecutionContext executionContext);

    void afterGetResultSetHoldabilityOnStatement(MethodExecutionContext executionContext);

    void afterGetResultSetTypeOnStatement(MethodExecutionContext executionContext);

    void afterGetUpdateCountOnStatement(MethodExecutionContext executionContext);

    void afterGetWarningsOnStatement(MethodExecutionContext executionContext);

    void afterIsClosedOnStatement(MethodExecutionContext executionContext);

    void afterIsCloseOnCompletionOnStatement(MethodExecutionContext executionContext);

    void afterIsPoolableOnStatement(MethodExecutionContext executionContext);

    void afterSetCursorNameOnStatement(MethodExecutionContext executionContext);

    void afterSetEscapeProcessingOnStatement(MethodExecutionContext executionContext);

    void afterSetFetchDirectionOnStatement(MethodExecutionContext executionContext);

    void afterSetFetchSizeOnStatement(MethodExecutionContext executionContext);

    void afterSetLargeMaxRowsOnStatement(MethodExecutionContext executionContext);

    void afterSetMaxFieldSizeOnStatement(MethodExecutionContext executionContext);

    void afterSetMaxRowsOnStatement(MethodExecutionContext executionContext);

    void afterSetPoolableOnStatement(MethodExecutionContext executionContext);

    void afterSetQueryTimeoutOnStatement(MethodExecutionContext executionContext);

}
