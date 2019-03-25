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
    // Statement methods
    //

    void beforeAddBatch(MethodExecutionContext executionContext);

    void beforeCancel(MethodExecutionContext executionContext);

    void beforeClearBatch(MethodExecutionContext executionContext);

    void beforeClearWarnings(MethodExecutionContext executionContext);

    void beforeClose(MethodExecutionContext executionContext);

    void beforeCloseOnCompletion(MethodExecutionContext executionContext);

    void beforeExecute(MethodExecutionContext executionContext);

    void beforeExecuteBatch(MethodExecutionContext executionContext);

    void beforeExecuteLargeBatch(MethodExecutionContext executionContext);

    void beforeExecuteLargeUpdate(MethodExecutionContext executionContext);

    void beforeExecuteQuery(MethodExecutionContext executionContext);

    void beforeExecuteUpdate(MethodExecutionContext executionContext);

    void beforeGetConnection(MethodExecutionContext executionContext);

    void beforeGetFetchDirection(MethodExecutionContext executionContext);

    void beforeGetFetchSize(MethodExecutionContext executionContext);

    void beforeGetGeneratedKeys(MethodExecutionContext executionContext);

    void beforeGetLargeMaxRows(MethodExecutionContext executionContext);

    void beforeGetLargeUpdateCount(MethodExecutionContext executionContext);

    void beforeGetMaxFieldSize(MethodExecutionContext executionContext);

    void beforeGetMaxRows(MethodExecutionContext executionContext);

    void beforeGetMoreResults(MethodExecutionContext executionContext);

    void beforeGetQueryTimeout(MethodExecutionContext executionContext);

    void beforeGetResultSet(MethodExecutionContext executionContext);

    void beforeGetResultSetConcurrency(MethodExecutionContext executionContext);

    void beforeGetResultSetHoldability(MethodExecutionContext executionContext);

    void beforeGetResultSetType(MethodExecutionContext executionContext);

    void beforeGetUpdateCount(MethodExecutionContext executionContext);

    void beforeGetWarnings(MethodExecutionContext executionContext);

    void beforeIsClosed(MethodExecutionContext executionContext);

    void beforeIsCloseOnCompletion(MethodExecutionContext executionContext);

    void beforeIsPoolable(MethodExecutionContext executionContext);

    void beforeSetCursorName(MethodExecutionContext executionContext);

    void beforeSetEscapeProcessing(MethodExecutionContext executionContext);

    void beforeSetFetchDirection(MethodExecutionContext executionContext);

    void beforeSetFetchSize(MethodExecutionContext executionContext);

    void beforeSetLargeMaxRows(MethodExecutionContext executionContext);

    void beforeSetMaxFieldSize(MethodExecutionContext executionContext);

    void beforeSetMaxRows(MethodExecutionContext executionContext);

    void beforeSetPoolable(MethodExecutionContext executionContext);

    void beforeSetQueryTimeout(MethodExecutionContext executionContext);

    void afterAddBatch(MethodExecutionContext executionContext);

    void afterCancel(MethodExecutionContext executionContext);

    void afterClearBatch(MethodExecutionContext executionContext);

    void afterClearWarnings(MethodExecutionContext executionContext);

    void afterClose(MethodExecutionContext executionContext);

    void afterCloseOnCompletion(MethodExecutionContext executionContext);

    void afterExecute(MethodExecutionContext executionContext);

    void afterExecuteBatch(MethodExecutionContext executionContext);

    void afterExecuteLargeBatch(MethodExecutionContext executionContext);

    void afterExecuteLargeUpdate(MethodExecutionContext executionContext);

    void afterExecuteQuery(MethodExecutionContext executionContext);

    void afterExecuteUpdate(MethodExecutionContext executionContext);

    void afterGetConnection(MethodExecutionContext executionContext);

    void afterGetFetchDirection(MethodExecutionContext executionContext);

    void afterGetFetchSize(MethodExecutionContext executionContext);

    void afterGetGeneratedKeys(MethodExecutionContext executionContext);

    void afterGetLargeMaxRows(MethodExecutionContext executionContext);

    void afterGetLargeUpdateCount(MethodExecutionContext executionContext);

    void afterGetMaxFieldSize(MethodExecutionContext executionContext);

    void afterGetMaxRows(MethodExecutionContext executionContext);

    void afterGetMoreResults(MethodExecutionContext executionContext);

    void afterGetQueryTimeout(MethodExecutionContext executionContext);

    void afterGetResultSet(MethodExecutionContext executionContext);

    void afterGetResultSetConcurrency(MethodExecutionContext executionContext);

    void afterGetResultSetHoldability(MethodExecutionContext executionContext);

    void afterGetResultSetType(MethodExecutionContext executionContext);

    void afterGetUpdateCount(MethodExecutionContext executionContext);

    void afterGetWarnings(MethodExecutionContext executionContext);

    void afterIsClosed(MethodExecutionContext executionContext);

    void afterIsCloseOnCompletion(MethodExecutionContext executionContext);

    void afterIsPoolable(MethodExecutionContext executionContext);

    void afterSetCursorName(MethodExecutionContext executionContext);

    void afterSetEscapeProcessing(MethodExecutionContext executionContext);

    void afterSetFetchDirection(MethodExecutionContext executionContext);

    void afterSetFetchSize(MethodExecutionContext executionContext);

    void afterSetLargeMaxRows(MethodExecutionContext executionContext);

    void afterSetMaxFieldSize(MethodExecutionContext executionContext);

    void afterSetMaxRows(MethodExecutionContext executionContext);

    void afterSetPoolable(MethodExecutionContext executionContext);

    void afterSetQueryTimeout(MethodExecutionContext executionContext);

}
