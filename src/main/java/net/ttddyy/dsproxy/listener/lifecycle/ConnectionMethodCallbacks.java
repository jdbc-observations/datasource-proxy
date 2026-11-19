package net.ttddyy.dsproxy.listener.lifecycle;

import net.ttddyy.dsproxy.listener.MethodExecutionContext;

/**
 * Defines callback methods for {@link java.sql.Connection}.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.5
 */
public interface ConnectionMethodCallbacks {

    //
    // Connection methods
    //

    default void beforeAbort(MethodExecutionContext executionContext) {

    }

    default void beforeBeginRequest(MethodExecutionContext executionContext) {

    }

    default void beforeClearWarnings(MethodExecutionContext executionContext) {

    }

    default void beforeClose(MethodExecutionContext executionContext) {

    }

    default void beforeCommit(MethodExecutionContext executionContext) {

    }

    default void beforeCreateArrayOf(MethodExecutionContext executionContext) {

    }

    default void beforeCreateBlob(MethodExecutionContext executionContext) {

    }

    default void beforeCreateClob(MethodExecutionContext executionContext) {

    }

    default void beforeCreateNClob(MethodExecutionContext executionContext) {

    }

    default void beforeCreateSQLXML(MethodExecutionContext executionContext) {

    }

    default void beforeCreateStatement(MethodExecutionContext executionContext) {

    }

    default void beforeCreateStruct(MethodExecutionContext executionContext) {

    }

    default void beforeEndRequest(MethodExecutionContext executionContext) {

    }

    default void beforeGetAutoCommit(MethodExecutionContext executionContext) {

    }

    default void beforeGetCatalog(MethodExecutionContext executionContext) {

    }

    default void beforeGetClientInfo(MethodExecutionContext executionContext) {

    }

    default void beforeGetHoldability(MethodExecutionContext executionContext) {

    }

    default void beforeGetMetaData(MethodExecutionContext executionContext) {

    }

    default void beforeGetNetworkTimeout(MethodExecutionContext executionContext) {

    }

    default void beforeGetSchema(MethodExecutionContext executionContext) {

    }

    default void beforeGetTransactionIsolation(MethodExecutionContext executionContext) {

    }

    default void beforeGetTypeMap(MethodExecutionContext executionContext) {

    }

    default void beforeGetWarnings(MethodExecutionContext executionContext) {

    }

    default void beforeIsClosed(MethodExecutionContext executionContext) {

    }

    default void beforeIsReadOnly(MethodExecutionContext executionContext) {

    }

    default void beforeIsValid(MethodExecutionContext executionContext) {

    }

    default void beforeNativeSQL(MethodExecutionContext executionContext) {

    }

    default void beforePrepareCall(MethodExecutionContext executionContext) {

    }

    default void beforePrepareStatement(MethodExecutionContext executionContext) {

    }

    default void beforeReleaseSavepoint(MethodExecutionContext executionContext) {

    }

    default void beforeRollback(MethodExecutionContext executionContext) {

    }

    default void beforeSetAutoCommit(MethodExecutionContext executionContext) {

    }

    default void beforeSetCatalog(MethodExecutionContext executionContext) {

    }

    default void beforeSetClientInfo(MethodExecutionContext executionContext) {

    }

    default void beforeSetHoldability(MethodExecutionContext executionContext) {

    }

    default void beforeSetNetworkTimeout(MethodExecutionContext executionContext) {

    }

    default void beforeSetReadOnly(MethodExecutionContext executionContext) {

    }

    default void beforeSetSavepoint(MethodExecutionContext executionContext) {

    }

    default void beforeSetSchema(MethodExecutionContext executionContext) {

    }

    default void beforeSetShardingKey(MethodExecutionContext executionContext) {

    }

    default void beforeSetShardingKeyIfValid(MethodExecutionContext executionContext) {

    }

    default void beforeSetTransactionIsolation(MethodExecutionContext executionContext) {

    }

    default void beforeSetTypeMap(MethodExecutionContext executionContext) {

    }

    default void afterAbort(MethodExecutionContext executionContext) {

    }

    default void afterBeginRequest(MethodExecutionContext executionContext) {

    }

    default void afterClearWarnings(MethodExecutionContext executionContext) {

    }

    default void afterClose(MethodExecutionContext executionContext) {

    }

    default void afterCommit(MethodExecutionContext executionContext) {

    }

    default void afterCreateArrayOf(MethodExecutionContext executionContext) {

    }

    default void afterCreateBlob(MethodExecutionContext executionContext) {

    }

    default void afterCreateClob(MethodExecutionContext executionContext) {

    }

    default void afterCreateNClob(MethodExecutionContext executionContext) {

    }

    default void afterCreateSQLXML(MethodExecutionContext executionContext) {

    }

    default void afterCreateStatement(MethodExecutionContext executionContext) {

    }

    default void afterCreateStruct(MethodExecutionContext executionContext) {

    }

    default void afterEndRequest(MethodExecutionContext executionContext) {

    }

    default void afterGetAutoCommit(MethodExecutionContext executionContext) {

    }

    default void afterGetCatalog(MethodExecutionContext executionContext) {

    }

    default void afterGetClientInfo(MethodExecutionContext executionContext) {

    }

    default void afterGetHoldability(MethodExecutionContext executionContext) {

    }

    default void afterGetMetaData(MethodExecutionContext executionContext) {

    }

    default void afterGetNetworkTimeout(MethodExecutionContext executionContext) {

    }

    default void afterGetSchema(MethodExecutionContext executionContext) {

    }

    default void afterGetTransactionIsolation(MethodExecutionContext executionContext) {

    }

    default void afterGetTypeMap(MethodExecutionContext executionContext) {

    }

    default void afterGetWarnings(MethodExecutionContext executionContext) {

    }

    default void afterIsClosed(MethodExecutionContext executionContext) {

    }

    default void afterIsReadOnly(MethodExecutionContext executionContext) {

    }

    default void afterIsValid(MethodExecutionContext executionContext) {

    }

    default void afterNativeSQL(MethodExecutionContext executionContext) {

    }

    default void afterPrepareCall(MethodExecutionContext executionContext) {

    }

    default void afterPrepareStatement(MethodExecutionContext executionContext) {

    }

    default void afterReleaseSavepoint(MethodExecutionContext executionContext) {

    }

    default void afterRollback(MethodExecutionContext executionContext) {

    }

    default void afterSetAutoCommit(MethodExecutionContext executionContext) {

    }

    default void afterSetCatalog(MethodExecutionContext executionContext) {

    }

    default void afterSetClientInfo(MethodExecutionContext executionContext) {

    }

    default void afterSetHoldability(MethodExecutionContext executionContext) {

    }

    default void afterSetNetworkTimeout(MethodExecutionContext executionContext) {

    }

    default void afterSetReadOnly(MethodExecutionContext executionContext) {

    }

    default void afterSetSavepoint(MethodExecutionContext executionContext) {

    }

    default void afterSetSchema(MethodExecutionContext executionContext) {

    }

    default void afterSetShardingKey(MethodExecutionContext executionContext) {

    }

    default void afterSetShardingKeyIfValid(MethodExecutionContext executionContext) {

    }

    default void afterSetTransactionIsolation(MethodExecutionContext executionContext) {

    }

    default void afterSetTypeMap(MethodExecutionContext executionContext) {

    }
}
