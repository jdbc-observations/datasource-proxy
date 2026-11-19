package net.ttddyy.dsproxy.listener.lifecycle;

import net.ttddyy.dsproxy.listener.MethodExecutionContext;
import net.ttddyy.dsproxy.listener.ProxyDataSourceListener;
import net.ttddyy.dsproxy.listener.QueryExecutionContext;

/**
 * Callback for all JDBC proxy methods.
 *
 * <p>This interface provides before- and after-method callbacks for all JDBC proxy interfaces ({@link javax.sql.DataSource},
 * {@link java.sql.Connection}, {@link java.sql.Statement}, {@link java.sql.PreparedStatement},
 * {@link java.sql.CallableStatement}, {@link java.sql.ResultSet}), as well as
 * callbacks for any method calls ({@link #beforeMethod(MethodExecutionContext)}, {@link #afterMethod(MethodExecutionContext)})
 * and query executions ({@link #beforeQuery(QueryExecutionContext)}, {@link #afterQuery(QueryExecutionContext)}).
 *
 * <p>Every callback is a no-op by default; override only what you need. Register it with
 * {@link #asListener(JdbcLifecycleEventListener)}.
 *
 * @author Tadaya Tsuyukubo
 * @see JdbcLifecycleEventExecutionListener
 * @since 1.5
 */
// Intentionally does not extend ProxyDataSourceListener: registering it directly would silently skip the typed callbacks.
public interface JdbcLifecycleEventListener extends WrapperMethodCallbacks, DataSourceMethodCallbacks, ConnectionMethodCallbacks,
        StatementMethodCallbacks, PreparedStatementMethodCallbacks, CallableStatementMethodCallbacks,
        ResultSetMethodCallbacks {

    /**
     * Adapt the given lifecycle listener to a {@link ProxyDataSourceListener} that dispatches each JDBC method to
     * its typed callback.
     *
     * @param listener lifecycle listener
     * @return a listener to register with {@code ProxyDataSourceBuilder.listener(...)}
     * @since 2.0
     */
    static ProxyDataSourceListener asListener(JdbcLifecycleEventListener listener) {
        return new JdbcLifecycleEventExecutionListener(listener);
    }

    default void beforeMethod(MethodExecutionContext executionContext) {
    }

    default void afterMethod(MethodExecutionContext executionContext) {
    }

    default void beforeQuery(QueryExecutionContext context) {
    }

    default void afterQuery(QueryExecutionContext context) {
    }

    // Callbacks declared by more than one *MethodCallbacks interface; redeclared so the inherited defaults don't clash.

    @Override
    default void afterAddBatch(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterClearWarnings(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterClose(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterExecute(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterExecuteLargeUpdate(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterExecuteQuery(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterExecuteUpdate(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterGetArray(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterGetBigDecimal(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterGetBlob(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterGetBoolean(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterGetByte(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterGetBytes(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterGetCharacterStream(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterGetClob(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterGetConnection(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterGetDate(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterGetDouble(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterGetFetchDirection(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterGetFetchSize(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterGetFloat(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterGetHoldability(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterGetInt(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterGetLong(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterGetMetaData(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterGetNCharacterStream(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterGetNClob(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterGetNString(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterGetObject(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterGetRef(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterGetRowId(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterGetSQLXML(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterGetShort(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterGetString(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterGetTime(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterGetTimestamp(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterGetURL(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterGetWarnings(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterIsClosed(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterSetAsciiStream(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterSetBigDecimal(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterSetBinaryStream(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterSetBlob(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterSetBoolean(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterSetByte(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterSetBytes(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterSetCharacterStream(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterSetClob(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterSetDate(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterSetDouble(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterSetFetchDirection(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterSetFetchSize(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterSetFloat(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterSetInt(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterSetLong(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterSetNCharacterStream(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterSetNClob(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterSetNString(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterSetNull(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterSetObject(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterSetRowId(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterSetSQLXML(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterSetShort(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterSetString(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterSetTime(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterSetTimestamp(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterSetURL(MethodExecutionContext executionContext) {
    }

    @Override
    default void afterWasNull(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeAddBatch(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeClearWarnings(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeClose(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeExecute(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeExecuteLargeUpdate(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeExecuteQuery(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeExecuteUpdate(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeGetArray(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeGetBigDecimal(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeGetBlob(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeGetBoolean(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeGetByte(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeGetBytes(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeGetCharacterStream(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeGetClob(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeGetConnection(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeGetDate(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeGetDouble(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeGetFetchDirection(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeGetFetchSize(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeGetFloat(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeGetHoldability(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeGetInt(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeGetLong(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeGetMetaData(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeGetNCharacterStream(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeGetNClob(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeGetNString(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeGetObject(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeGetRef(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeGetRowId(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeGetSQLXML(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeGetShort(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeGetString(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeGetTime(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeGetTimestamp(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeGetURL(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeGetWarnings(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeIsClosed(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeSetAsciiStream(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeSetBigDecimal(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeSetBinaryStream(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeSetBlob(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeSetBoolean(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeSetByte(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeSetBytes(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeSetCharacterStream(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeSetClob(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeSetDate(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeSetDouble(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeSetFetchDirection(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeSetFetchSize(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeSetFloat(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeSetInt(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeSetLong(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeSetNCharacterStream(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeSetNClob(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeSetNString(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeSetNull(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeSetObject(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeSetRowId(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeSetSQLXML(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeSetShort(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeSetString(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeSetTime(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeSetTimestamp(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeSetURL(MethodExecutionContext executionContext) {
    }

    @Override
    default void beforeWasNull(MethodExecutionContext executionContext) {
    }

}
