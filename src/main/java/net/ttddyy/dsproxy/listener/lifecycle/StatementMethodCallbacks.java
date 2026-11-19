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

    default void beforeAddBatch(MethodExecutionContext executionContext) {

    }

    default void beforeCancel(MethodExecutionContext executionContext) {

    }

    default void beforeClearBatch(MethodExecutionContext executionContext) {

    }

    default void beforeClearWarnings(MethodExecutionContext executionContext) {

    }

    default void beforeClose(MethodExecutionContext executionContext) {

    }

    default void beforeEnquoteIdentifier(MethodExecutionContext executionContext) {

    }

    default void beforeEnquoteLiteral(MethodExecutionContext executionContext) {

    }

    default void beforeEnquoteNCharLiteral(MethodExecutionContext executionContext) {

    }

    default void beforeCloseOnCompletion(MethodExecutionContext executionContext) {

    }

    default void beforeExecute(MethodExecutionContext executionContext) {

    }

    default void beforeExecuteBatch(MethodExecutionContext executionContext) {

    }

    default void beforeExecuteLargeBatch(MethodExecutionContext executionContext) {

    }

    default void beforeExecuteLargeUpdate(MethodExecutionContext executionContext) {

    }

    default void beforeExecuteQuery(MethodExecutionContext executionContext) {

    }

    default void beforeExecuteUpdate(MethodExecutionContext executionContext) {

    }

    default void beforeGetConnection(MethodExecutionContext executionContext) {

    }

    default void beforeGetFetchDirection(MethodExecutionContext executionContext) {

    }

    default void beforeGetFetchSize(MethodExecutionContext executionContext) {

    }

    default void beforeGetGeneratedKeys(MethodExecutionContext executionContext) {

    }

    default void beforeGetLargeMaxRows(MethodExecutionContext executionContext) {

    }

    default void beforeGetLargeUpdateCount(MethodExecutionContext executionContext) {

    }

    default void beforeGetMaxFieldSize(MethodExecutionContext executionContext) {

    }

    default void beforeGetMaxRows(MethodExecutionContext executionContext) {

    }

    default void beforeGetMoreResults(MethodExecutionContext executionContext) {

    }

    default void beforeGetQueryTimeout(MethodExecutionContext executionContext) {

    }

    default void beforeGetResultSet(MethodExecutionContext executionContext) {

    }

    default void beforeGetResultSetConcurrency(MethodExecutionContext executionContext) {

    }

    default void beforeGetResultSetHoldability(MethodExecutionContext executionContext) {

    }

    default void beforeGetResultSetType(MethodExecutionContext executionContext) {

    }

    default void beforeGetUpdateCount(MethodExecutionContext executionContext) {

    }

    default void beforeGetWarnings(MethodExecutionContext executionContext) {

    }

    default void beforeIsClosed(MethodExecutionContext executionContext) {

    }

    default void beforeIsCloseOnCompletion(MethodExecutionContext executionContext) {

    }

    default void beforeIsPoolable(MethodExecutionContext executionContext) {

    }

    default void beforeIsSimpleIdentifier(MethodExecutionContext executionContext) {

    }

    default void beforeSetCursorName(MethodExecutionContext executionContext) {

    }

    default void beforeSetEscapeProcessing(MethodExecutionContext executionContext) {

    }

    default void beforeSetFetchDirection(MethodExecutionContext executionContext) {

    }

    default void beforeSetFetchSize(MethodExecutionContext executionContext) {

    }

    default void beforeSetLargeMaxRows(MethodExecutionContext executionContext) {

    }

    default void beforeSetMaxFieldSize(MethodExecutionContext executionContext) {

    }

    default void beforeSetMaxRows(MethodExecutionContext executionContext) {

    }

    default void beforeSetPoolable(MethodExecutionContext executionContext) {

    }

    default void beforeSetQueryTimeout(MethodExecutionContext executionContext) {

    }

    default void afterAddBatch(MethodExecutionContext executionContext) {

    }

    default void afterCancel(MethodExecutionContext executionContext) {

    }

    default void afterClearBatch(MethodExecutionContext executionContext) {

    }

    default void afterClearWarnings(MethodExecutionContext executionContext) {

    }

    default void afterClose(MethodExecutionContext executionContext) {

    }

    default void afterEnquoteIdentifier(MethodExecutionContext executionContext) {

    }

    default void afterEnquoteLiteral(MethodExecutionContext executionContext) {

    }

    default void afterEnquoteNCharLiteral(MethodExecutionContext executionContext) {

    }

    default void afterCloseOnCompletion(MethodExecutionContext executionContext) {

    }

    default void afterExecute(MethodExecutionContext executionContext) {

    }

    default void afterExecuteBatch(MethodExecutionContext executionContext) {

    }

    default void afterExecuteLargeBatch(MethodExecutionContext executionContext) {

    }

    default void afterExecuteLargeUpdate(MethodExecutionContext executionContext) {

    }

    default void afterExecuteQuery(MethodExecutionContext executionContext) {

    }

    default void afterExecuteUpdate(MethodExecutionContext executionContext) {

    }

    default void afterGetConnection(MethodExecutionContext executionContext) {

    }

    default void afterGetFetchDirection(MethodExecutionContext executionContext) {

    }

    default void afterGetFetchSize(MethodExecutionContext executionContext) {

    }

    default void afterGetGeneratedKeys(MethodExecutionContext executionContext) {

    }

    default void afterGetLargeMaxRows(MethodExecutionContext executionContext) {

    }

    default void afterGetLargeUpdateCount(MethodExecutionContext executionContext) {

    }

    default void afterGetMaxFieldSize(MethodExecutionContext executionContext) {

    }

    default void afterGetMaxRows(MethodExecutionContext executionContext) {

    }

    default void afterGetMoreResults(MethodExecutionContext executionContext) {

    }

    default void afterGetQueryTimeout(MethodExecutionContext executionContext) {

    }

    default void afterGetResultSet(MethodExecutionContext executionContext) {

    }

    default void afterGetResultSetConcurrency(MethodExecutionContext executionContext) {

    }

    default void afterGetResultSetHoldability(MethodExecutionContext executionContext) {

    }

    default void afterGetResultSetType(MethodExecutionContext executionContext) {

    }

    default void afterGetUpdateCount(MethodExecutionContext executionContext) {

    }

    default void afterGetWarnings(MethodExecutionContext executionContext) {

    }

    default void afterIsClosed(MethodExecutionContext executionContext) {

    }

    default void afterIsCloseOnCompletion(MethodExecutionContext executionContext) {

    }

    default void afterIsPoolable(MethodExecutionContext executionContext) {

    }

    default void afterIsSimpleIdentifier(MethodExecutionContext executionContext) {

    }

    default void afterSetCursorName(MethodExecutionContext executionContext) {

    }

    default void afterSetEscapeProcessing(MethodExecutionContext executionContext) {

    }

    default void afterSetFetchDirection(MethodExecutionContext executionContext) {

    }

    default void afterSetFetchSize(MethodExecutionContext executionContext) {

    }

    default void afterSetLargeMaxRows(MethodExecutionContext executionContext) {

    }

    default void afterSetMaxFieldSize(MethodExecutionContext executionContext) {

    }

    default void afterSetMaxRows(MethodExecutionContext executionContext) {

    }

    default void afterSetPoolable(MethodExecutionContext executionContext) {

    }

    default void afterSetQueryTimeout(MethodExecutionContext executionContext) {

    }

}
