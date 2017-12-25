package net.ttddyy.dsproxy.proxy.delegate;

import net.ttddyy.dsproxy.proxy.StatementProxyLogic;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

/**
 * A concrete class implementation of {@link Statement} that delegates to {@link StatementProxyLogic}.
 *
 * @author Tadaya Tsuyukubo
 * @see DelegatingJdbcProxyFactory
 * @since 1.5
 */
public class DelegatingStatement implements Statement {

    private static final Method EXECUTE_QUERY_METHOD = getMethodIfAvailable("executeQuery", String.class);
    private static final Method EXECUTE_UPDATE_METHOD = getMethodIfAvailable("executeUpdate", String.class);
    private static final Method CLOSE_METHOD = getMethodIfAvailable("close");
    private static final Method GET_MAX_FIELD_SIZE_METHOD = getMethodIfAvailable("getMaxFieldSize");
    private static final Method SET_MAX_FIELD_SIZE_METHOD = getMethodIfAvailable("setMaxFieldSize", int.class);
    private static final Method GET_MAX_ROWS_METHOD = getMethodIfAvailable("getMaxRows");
    private static final Method SET_MAX_ROWS_METHOD = getMethodIfAvailable("setMaxRows", int.class);
    private static final Method SET_ESCAPE_PROCESSING_METHOD = getMethodIfAvailable("setEscapeProcessing", boolean.class);
    private static final Method GET_QUERY_TIMEOUT_METHOD = getMethodIfAvailable("getQueryTimeout");
    private static final Method SET_QUERY_TIMEOUT_METHOD = getMethodIfAvailable("setQueryTimeout", int.class);
    private static final Method CANCEL_METHOD = getMethodIfAvailable("cancel");
    private static final Method GET_WARNINGS_METHOD = getMethodIfAvailable("getWarnings");
    private static final Method CLEAR_WARNINGS_METHOD = getMethodIfAvailable("clearWarnings");
    private static final Method SET_CURSOR_NAME_METHOD = getMethodIfAvailable("setCursorName", String.class);
    private static final Method EXECUTE_METHOD = getMethodIfAvailable("execute", String.class);
    private static final Method GET_RESULT_SET_METHOD = getMethodIfAvailable("getResultSet");
    private static final Method GET_UPDATE_COUNT_METHOD = getMethodIfAvailable("getUpdateCount");
    private static final Method GET_MORE_RESULTS_METHOD = getMethodIfAvailable("getMoreResults");
    private static final Method SET_FETCH_DIRECTION_METHOD = getMethodIfAvailable("setFetchDirection", int.class);
    private static final Method GET_FETCH_DIRECTION_METHOD = getMethodIfAvailable("getFetchDirection");
    private static final Method SET_FETCH_SIZE_METHOD = getMethodIfAvailable("setFetchSize", int.class);
    private static final Method GET_FETCH_SIZE_METHOD = getMethodIfAvailable("getFetchSize");
    private static final Method GET_RESULT_SET_CONCURRENCY_METHOD = getMethodIfAvailable("getResultSetConcurrency");
    private static final Method GET_RESULT_SET_TYPE_METHOD = getMethodIfAvailable("getResultSetType");
    private static final Method ADD_BATCH_METHOD = getMethodIfAvailable("addBatch", String.class);
    private static final Method CLEAR_BATCH_METHOD = getMethodIfAvailable("clearBatch");
    private static final Method EXECUTE_BATCH_METHOD = getMethodIfAvailable("executeBatch");
    private static final Method GET_CONNECTION_METHOD = getMethodIfAvailable("getConnection");
    private static final Method GET_MORE_RESULTS_WITH_INT_METHOD = getMethodIfAvailable("getMoreResults", int.class);
    private static final Method GET_GENERATED_KEYS_METHOD = getMethodIfAvailable("getGeneratedKeys");
    private static final Method EXECUTE_UPDATE_WITH_INT_METHOD = getMethodIfAvailable("executeUpdate", String.class, int.class);
    private static final Method EXECUTE_UPDATE_WITH_INT_ARRAY_METHOD = getMethodIfAvailable("executeUpdate", String.class, int[].class);
    private static final Method EXECUTE_UPDATE_WITH_STRING_ARRAY_METHOD = getMethodIfAvailable("executeUpdate", String.class, String[].class);
    private static final Method EXECUTE_WITH_INT_METHOD = getMethodIfAvailable("execute", String.class, int.class);
    private static final Method EXECUTE_WITH_INT_ARRAY_METHOD = getMethodIfAvailable("execute", String.class, int[].class);
    private static final Method EXECUTE_WITH_STRING_ARRAY_METHOD = getMethodIfAvailable("execute", String.class, String[].class);
    private static final Method GET_RESULT_SET_HOLDABILITY_METHOD = getMethodIfAvailable("getResultSetHoldability");
    private static final Method IS_CLOSED_METHOD = getMethodIfAvailable("isClosed");
    private static final Method SET_POOLABLE_METHOD = getMethodIfAvailable("setPoolable", boolean.class);
    private static final Method IS_POOLABLE_METHOD = getMethodIfAvailable("isPoolable");
    private static final Method CLOSE_ON_COMPLETION_METHOD = getMethodIfAvailable("closeOnCompletion");
    private static final Method IS_CLOSE_ON_COMPLETION_METHOD = getMethodIfAvailable("isCloseOnCompletion");
    private static final Method UNWRAP_METHOD = getMethodIfAvailable("unwrap", Class.class);
    private static final Method IS_WRAPPER_FOR_METHOD = getMethodIfAvailable("isWrapperFor", Class.class);
    private static final Method GET_LARGE_UPDATE_COUNT_METHOD = getMethodIfAvailable("getLargeUpdateCount");
    private static final Method SET_LARGE_MAX_ROWS_METHOD = getMethodIfAvailable("setLargeMaxRows", long.class);
    private static final Method GET_LARGE_MAX_ROWS_METHOD = getMethodIfAvailable("getLargeMaxRows");
    private static final Method EXECUTE_LARGE_BATCH_METHOD = getMethodIfAvailable("executeLargeBatch");
    private static final Method EXECUTE_LARGE_UPDATE_METHOD = getMethodIfAvailable("executeLargeUpdate", String.class);
    private static final Method EXECUTE_LARGE_UPDATE_WITH_INT_METHOD = getMethodIfAvailable("executeLargeUpdate", String.class, int.class);
    private static final Method EXECUTE_LARGE_UPDATE_WITH_INT_ARRAY_METHOD = getMethodIfAvailable("executeLargeUpdate", String.class, int[].class);
    private static final Method EXECUTE_LARGE_UPDATE_WITH_STRING_ARRAY_METHOD = getMethodIfAvailable("executeLargeUpdate", String.class, String[].class);

    private static Method getMethodIfAvailable(String name, Class... parameterTypes) {
        return DelegatingUtils.getMethodIfAvailable(Statement.class, name, parameterTypes);
    }

    protected StatementProxyLogic proxyLogic;

    public DelegatingStatement(StatementProxyLogic proxyLogic) {
        this.proxyLogic = proxyLogic;
    }

    private Object invoke(final Method method, final Object... args) throws SQLException {
        return DelegatingUtils.invoke(method, new DelegatingUtils.InvocationCallback() {
            @Override
            public Object invoke() throws Throwable {
                return DelegatingStatement.this.proxyLogic.invoke(method, args);
            }
        });
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        return (ResultSet) invoke(EXECUTE_QUERY_METHOD, sql);
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        return (Integer) invoke(EXECUTE_UPDATE_METHOD, sql);
    }

    @Override
    public void close() throws SQLException {
        invoke(CLOSE_METHOD);
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return (Integer) invoke(GET_MAX_FIELD_SIZE_METHOD);
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        invoke(SET_MAX_FIELD_SIZE_METHOD, max);
    }

    @Override
    public int getMaxRows() throws SQLException {
        return (Integer) invoke(GET_MAX_ROWS_METHOD);
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        invoke(SET_MAX_ROWS_METHOD, max);
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        invoke(SET_ESCAPE_PROCESSING_METHOD, enable);
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return (Integer) invoke(GET_QUERY_TIMEOUT_METHOD);
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        invoke(SET_QUERY_TIMEOUT_METHOD, seconds);
    }

    @Override
    public void cancel() throws SQLException {
        invoke(CANCEL_METHOD);
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return (SQLWarning) invoke(GET_WARNINGS_METHOD);
    }

    @Override
    public void clearWarnings() throws SQLException {
        invoke(CLEAR_WARNINGS_METHOD);
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        invoke(SET_CURSOR_NAME_METHOD, name);
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        return (Boolean) invoke(EXECUTE_METHOD, sql);
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return (ResultSet) invoke(GET_RESULT_SET_METHOD);
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return (Integer) invoke(GET_UPDATE_COUNT_METHOD);
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return (Boolean) invoke(GET_MORE_RESULTS_METHOD);
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        invoke(SET_FETCH_DIRECTION_METHOD, direction);
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return (Integer) invoke(GET_FETCH_DIRECTION_METHOD);
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        invoke(SET_FETCH_SIZE_METHOD, rows);
    }

    @Override
    public int getFetchSize() throws SQLException {
        return (Integer) invoke(GET_FETCH_SIZE_METHOD);
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return (Integer) invoke(GET_RESULT_SET_CONCURRENCY_METHOD);
    }

    @Override
    public int getResultSetType() throws SQLException {
        return (Integer) invoke(GET_RESULT_SET_TYPE_METHOD);
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        invoke(ADD_BATCH_METHOD, sql);
    }

    @Override
    public void clearBatch() throws SQLException {
        invoke(CLEAR_BATCH_METHOD);
    }

    @Override
    public int[] executeBatch() throws SQLException {
        return (int[]) invoke(EXECUTE_BATCH_METHOD);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return (Connection) invoke(GET_CONNECTION_METHOD);
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return (Boolean) invoke(GET_MORE_RESULTS_WITH_INT_METHOD, current);
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return (ResultSet) invoke(GET_GENERATED_KEYS_METHOD);
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return (Integer) invoke(EXECUTE_UPDATE_WITH_INT_METHOD, sql, autoGeneratedKeys);
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return (Integer) invoke(EXECUTE_UPDATE_WITH_INT_ARRAY_METHOD, sql, columnIndexes);
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return (Integer) invoke(EXECUTE_UPDATE_WITH_STRING_ARRAY_METHOD, sql, columnNames);
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return (Boolean) invoke(EXECUTE_WITH_INT_METHOD, sql, autoGeneratedKeys);
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return (Boolean) invoke(EXECUTE_WITH_INT_ARRAY_METHOD, sql, columnIndexes);
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return (Boolean) invoke(EXECUTE_WITH_STRING_ARRAY_METHOD, sql, columnNames);
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return (Integer) invoke(GET_RESULT_SET_HOLDABILITY_METHOD);
    }

    @Override
    public boolean isClosed() throws SQLException {
        return (Boolean) invoke(IS_CLOSED_METHOD);
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        invoke(SET_POOLABLE_METHOD, poolable);
    }

    @Override
    public boolean isPoolable() throws SQLException {
        return (Boolean) invoke(IS_POOLABLE_METHOD);
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        invoke(CLOSE_ON_COMPLETION_METHOD);
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return (Boolean) invoke(IS_CLOSE_ON_COMPLETION_METHOD);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return (T) invoke(UNWRAP_METHOD, iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return (Boolean) invoke(IS_WRAPPER_FOR_METHOD, iface);
    }

    @Override
    public long getLargeUpdateCount() throws SQLException {
        return (Long) invoke(GET_LARGE_UPDATE_COUNT_METHOD);
    }

    @Override
    public void setLargeMaxRows(long max) throws SQLException {
        invoke(SET_LARGE_MAX_ROWS_METHOD, max);
    }

    @Override
    public long getLargeMaxRows() throws SQLException {
        return (Long) invoke(GET_LARGE_MAX_ROWS_METHOD);
    }

    @Override
    public long[] executeLargeBatch() throws SQLException {
        return (long[]) invoke(EXECUTE_LARGE_BATCH_METHOD);
    }

    @Override
    public long executeLargeUpdate(String sql) throws SQLException {
        return (Long) invoke(EXECUTE_LARGE_UPDATE_METHOD, sql);
    }

    @Override
    public long executeLargeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return (Long) invoke(EXECUTE_LARGE_UPDATE_WITH_INT_METHOD, sql, autoGeneratedKeys);
    }

    @Override
    public long executeLargeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return (Long) invoke(EXECUTE_LARGE_UPDATE_WITH_INT_ARRAY_METHOD, sql, columnIndexes);
    }

    @Override
    public long executeLargeUpdate(String sql, String[] columnNames) throws SQLException {
        return (Long) invoke(EXECUTE_LARGE_UPDATE_WITH_STRING_ARRAY_METHOD, sql, columnNames);
    }

}
