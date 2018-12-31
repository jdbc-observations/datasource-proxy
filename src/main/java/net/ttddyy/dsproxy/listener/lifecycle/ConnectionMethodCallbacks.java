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
    // Wrapper methods
    //

    void beforeIsWrapperForOnConnection(MethodExecutionContext executionContext);

    void beforeUnwrapOnConnection(MethodExecutionContext executionContext);

    void afterIsWrapperForOnConnection(MethodExecutionContext executionContext);

    void afterUnwrapOnConnection(MethodExecutionContext executionContext);

    //
    // Connection methods
    //

    void beforeAbortOnConnection(MethodExecutionContext executionContext);

    void beforeClearWarningsOnConnection(MethodExecutionContext executionContext);

    void beforeCloseOnConnection(MethodExecutionContext executionContext);

    void beforeCommitOnConnection(MethodExecutionContext executionContext);

    void beforeCreateArrayOfOnConnection(MethodExecutionContext executionContext);

    void beforeCreateBlobOnConnection(MethodExecutionContext executionContext);

    void beforeCreateClobOnConnection(MethodExecutionContext executionContext);

    void beforeCreateNClobOnConnection(MethodExecutionContext executionContext);

    void beforeCreateSQLXMLOnConnection(MethodExecutionContext executionContext);

    void beforeCreateStatementOnConnection(MethodExecutionContext executionContext);

    void beforeCreateStructOnConnection(MethodExecutionContext executionContext);

    void beforeGetAutoCommitOnConnection(MethodExecutionContext executionContext);

    void beforeGetCatalogOnConnection(MethodExecutionContext executionContext);

    void beforeGetClientInfoOnConnection(MethodExecutionContext executionContext);

    void beforeGetHoldabilityOnConnection(MethodExecutionContext executionContext);

    void beforeGetMetaDataOnConnection(MethodExecutionContext executionContext);

    void beforeGetNetworkTimeoutOnConnection(MethodExecutionContext executionContext);

    void beforeGetSchemaOnConnection(MethodExecutionContext executionContext);

    void beforeGetTransactionIsolationOnConnection(MethodExecutionContext executionContext);

    void beforeGetTypeMapOnConnection(MethodExecutionContext executionContext);

    void beforeGetWarningsOnConnection(MethodExecutionContext executionContext);

    void beforeIsClosedOnConnection(MethodExecutionContext executionContext);

    void beforeIsReadOnlyOnConnection(MethodExecutionContext executionContext);

    void beforeIsValidOnConnection(MethodExecutionContext executionContext);

    void beforeNativeSQLOnConnection(MethodExecutionContext executionContext);

    void beforePrepareCallOnConnection(MethodExecutionContext executionContext);

    void beforePrepareStatementOnConnection(MethodExecutionContext executionContext);

    void beforeReleaseSavepointOnConnection(MethodExecutionContext executionContext);

    void beforeRollbackOnConnection(MethodExecutionContext executionContext);

    void beforeSetAutoCommitOnConnection(MethodExecutionContext executionContext);

    void beforeSetCatalogOnConnection(MethodExecutionContext executionContext);

    void beforeSetClientInfoOnConnection(MethodExecutionContext executionContext);

    void beforeSetHoldabilityOnConnection(MethodExecutionContext executionContext);

    void beforeSetNetworkTimeoutOnConnection(MethodExecutionContext executionContext);

    void beforeSetReadOnlyOnConnection(MethodExecutionContext executionContext);

    void beforeSetSavepointOnConnection(MethodExecutionContext executionContext);

    void beforeSetSchemaOnConnection(MethodExecutionContext executionContext);

    void beforeSetTransactionIsolationOnConnection(MethodExecutionContext executionContext);

    void beforeSetTypeMapOnConnection(MethodExecutionContext executionContext);

    void afterAbortOnConnection(MethodExecutionContext executionContext);

    void afterClearWarningsOnConnection(MethodExecutionContext executionContext);

    void afterCloseOnConnection(MethodExecutionContext executionContext);

    void afterCommitOnConnection(MethodExecutionContext executionContext);

    void afterCreateArrayOfOnConnection(MethodExecutionContext executionContext);

    void afterCreateBlobOnConnection(MethodExecutionContext executionContext);

    void afterCreateClobOnConnection(MethodExecutionContext executionContext);

    void afterCreateNClobOnConnection(MethodExecutionContext executionContext);

    void afterCreateSQLXMLOnConnection(MethodExecutionContext executionContext);

    void afterCreateStatementOnConnection(MethodExecutionContext executionContext);

    void afterCreateStructOnConnection(MethodExecutionContext executionContext);

    void afterGetAutoCommitOnConnection(MethodExecutionContext executionContext);

    void afterGetCatalogOnConnection(MethodExecutionContext executionContext);

    void afterGetClientInfoOnConnection(MethodExecutionContext executionContext);

    void afterGetHoldabilityOnConnection(MethodExecutionContext executionContext);

    void afterGetMetaDataOnConnection(MethodExecutionContext executionContext);

    void afterGetNetworkTimeoutOnConnection(MethodExecutionContext executionContext);

    void afterGetSchemaOnConnection(MethodExecutionContext executionContext);

    void afterGetTransactionIsolationOnConnection(MethodExecutionContext executionContext);

    void afterGetTypeMapOnConnection(MethodExecutionContext executionContext);

    void afterGetWarningsOnConnection(MethodExecutionContext executionContext);

    void afterIsClosedOnConnection(MethodExecutionContext executionContext);

    void afterIsReadOnlyOnConnection(MethodExecutionContext executionContext);

    void afterIsValidOnConnection(MethodExecutionContext executionContext);

    void afterNativeSQLOnConnection(MethodExecutionContext executionContext);

    void afterPrepareCallOnConnection(MethodExecutionContext executionContext);

    void afterPrepareStatementOnConnection(MethodExecutionContext executionContext);

    void afterReleaseSavepointOnConnection(MethodExecutionContext executionContext);

    void afterRollbackOnConnection(MethodExecutionContext executionContext);

    void afterSetAutoCommitOnConnection(MethodExecutionContext executionContext);

    void afterSetCatalogOnConnection(MethodExecutionContext executionContext);

    void afterSetClientInfoOnConnection(MethodExecutionContext executionContext);

    void afterSetHoldabilityOnConnection(MethodExecutionContext executionContext);

    void afterSetNetworkTimeoutOnConnection(MethodExecutionContext executionContext);

    void afterSetReadOnlyOnConnection(MethodExecutionContext executionContext);

    void afterSetSavepointOnConnection(MethodExecutionContext executionContext);

    void afterSetSchemaOnConnection(MethodExecutionContext executionContext);

    void afterSetTransactionIsolationOnConnection(MethodExecutionContext executionContext);

    void afterSetTypeMapOnConnection(MethodExecutionContext executionContext);
}
