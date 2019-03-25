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

    void beforeAbort(MethodExecutionContext executionContext);

    void beforeClearWarnings(MethodExecutionContext executionContext);

    void beforeClose(MethodExecutionContext executionContext);

    void beforeCommit(MethodExecutionContext executionContext);

    void beforeCreateArrayOf(MethodExecutionContext executionContext);

    void beforeCreateBlob(MethodExecutionContext executionContext);

    void beforeCreateClob(MethodExecutionContext executionContext);

    void beforeCreateNClob(MethodExecutionContext executionContext);

    void beforeCreateSQLXML(MethodExecutionContext executionContext);

    void beforeCreateStatement(MethodExecutionContext executionContext);

    void beforeCreateStruct(MethodExecutionContext executionContext);

    void beforeGetAutoCommit(MethodExecutionContext executionContext);

    void beforeGetCatalog(MethodExecutionContext executionContext);

    void beforeGetClientInfo(MethodExecutionContext executionContext);

    void beforeGetHoldability(MethodExecutionContext executionContext);

    void beforeGetMetaData(MethodExecutionContext executionContext);

    void beforeGetNetworkTimeout(MethodExecutionContext executionContext);

    void beforeGetSchema(MethodExecutionContext executionContext);

    void beforeGetTransactionIsolation(MethodExecutionContext executionContext);

    void beforeGetTypeMap(MethodExecutionContext executionContext);

    void beforeGetWarnings(MethodExecutionContext executionContext);

    void beforeIsClosed(MethodExecutionContext executionContext);

    void beforeIsReadOnly(MethodExecutionContext executionContext);

    void beforeIsValid(MethodExecutionContext executionContext);

    void beforeNativeSQL(MethodExecutionContext executionContext);

    void beforePrepareCall(MethodExecutionContext executionContext);

    void beforePrepareStatement(MethodExecutionContext executionContext);

    void beforeReleaseSavepoint(MethodExecutionContext executionContext);

    void beforeRollback(MethodExecutionContext executionContext);

    void beforeSetAutoCommit(MethodExecutionContext executionContext);

    void beforeSetCatalog(MethodExecutionContext executionContext);

    void beforeSetClientInfo(MethodExecutionContext executionContext);

    void beforeSetHoldability(MethodExecutionContext executionContext);

    void beforeSetNetworkTimeout(MethodExecutionContext executionContext);

    void beforeSetReadOnly(MethodExecutionContext executionContext);

    void beforeSetSavepoint(MethodExecutionContext executionContext);

    void beforeSetSchema(MethodExecutionContext executionContext);

    void beforeSetTransactionIsolation(MethodExecutionContext executionContext);

    void beforeSetTypeMap(MethodExecutionContext executionContext);

    void afterAbort(MethodExecutionContext executionContext);

    void afterClearWarnings(MethodExecutionContext executionContext);

    void afterClose(MethodExecutionContext executionContext);

    void afterCommit(MethodExecutionContext executionContext);

    void afterCreateArrayOf(MethodExecutionContext executionContext);

    void afterCreateBlob(MethodExecutionContext executionContext);

    void afterCreateClob(MethodExecutionContext executionContext);

    void afterCreateNClob(MethodExecutionContext executionContext);

    void afterCreateSQLXML(MethodExecutionContext executionContext);

    void afterCreateStatement(MethodExecutionContext executionContext);

    void afterCreateStruct(MethodExecutionContext executionContext);

    void afterGetAutoCommit(MethodExecutionContext executionContext);

    void afterGetCatalog(MethodExecutionContext executionContext);

    void afterGetClientInfo(MethodExecutionContext executionContext);

    void afterGetHoldability(MethodExecutionContext executionContext);

    void afterGetMetaData(MethodExecutionContext executionContext);

    void afterGetNetworkTimeout(MethodExecutionContext executionContext);

    void afterGetSchema(MethodExecutionContext executionContext);

    void afterGetTransactionIsolation(MethodExecutionContext executionContext);

    void afterGetTypeMap(MethodExecutionContext executionContext);

    void afterGetWarnings(MethodExecutionContext executionContext);

    void afterIsClosed(MethodExecutionContext executionContext);

    void afterIsReadOnly(MethodExecutionContext executionContext);

    void afterIsValid(MethodExecutionContext executionContext);

    void afterNativeSQL(MethodExecutionContext executionContext);

    void afterPrepareCall(MethodExecutionContext executionContext);

    void afterPrepareStatement(MethodExecutionContext executionContext);

    void afterReleaseSavepoint(MethodExecutionContext executionContext);

    void afterRollback(MethodExecutionContext executionContext);

    void afterSetAutoCommit(MethodExecutionContext executionContext);

    void afterSetCatalog(MethodExecutionContext executionContext);

    void afterSetClientInfo(MethodExecutionContext executionContext);

    void afterSetHoldability(MethodExecutionContext executionContext);

    void afterSetNetworkTimeout(MethodExecutionContext executionContext);

    void afterSetReadOnly(MethodExecutionContext executionContext);

    void afterSetSavepoint(MethodExecutionContext executionContext);

    void afterSetSchema(MethodExecutionContext executionContext);

    void afterSetTransactionIsolation(MethodExecutionContext executionContext);

    void afterSetTypeMap(MethodExecutionContext executionContext);
}
