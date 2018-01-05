package net.ttddyy.dsproxy.proxy.delegate;

import net.ttddyy.dsproxy.DataSourceProxyException;
import net.ttddyy.dsproxy.proxy.ConnectionProxyLogic;
import net.ttddyy.dsproxy.proxy.ProxyJdbcObject;

import java.lang.reflect.Method;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * A concrete class implementation of {@link Connection} that delegates to {@link ConnectionProxyLogic}.
 *
 * @author Tadaya Tsuyukubo
 * @see DelegatingJdbcProxyFactory
 * @since 1.5
 */
public class DelegatingConnection implements Connection, ProxyJdbcObject {

    private static final Method CREATE_STATEMENT_METHOD = getMethodIfAvailable("createStatement");
    private static final Method PREPARE_STATEMENT_METHOD = getMethodIfAvailable("prepareStatement", String.class);
    private static final Method PREPARE_CALL_METHOD = getMethodIfAvailable("prepareCall", String.class);
    private static final Method NATIVE_SQL_METHOD = getMethodIfAvailable("nativeSQL", String.class);
    private static final Method SET_AUTO_COMMIT_METHOD = getMethodIfAvailable("setAutoCommit", boolean.class);
    private static final Method GET_AUTO_COMMIT_METHOD = getMethodIfAvailable("getAutoCommit");
    private static final Method COMMIT_METHOD = getMethodIfAvailable("commit");
    private static final Method ROLLBACK_METHOD = getMethodIfAvailable("rollback");
    private static final Method CLOSE_METHOD = getMethodIfAvailable("close");
    private static final Method IS_CLOSED_METHOD = getMethodIfAvailable("isClosed");
    private static final Method GET_META_DATA_METHOD = getMethodIfAvailable("getMetaData");
    private static final Method SET_READONLY_METHOD = getMethodIfAvailable("setReadOnly", boolean.class);
    private static final Method IS_READ_ONLY_METHOD = getMethodIfAvailable("isReadOnly");
    private static final Method SET_CATALOG_METHOD = getMethodIfAvailable("setCatalog", String.class);
    private static final Method GET_CATALOG_METHOD = getMethodIfAvailable("getCatalog");
    private static final Method SET_TRANSACTION_ISOLATION_METHOD = getMethodIfAvailable("setTransactionIsolation", int.class);
    private static final Method GET_TRANSACTION_ISOLATION_METHOD = getMethodIfAvailable("getTransactionIsolation");
    private static final Method GET_WARNINGS_METHOD = getMethodIfAvailable("getWarnings");
    private static final Method CLEAR_WARNINGS_METHOD = getMethodIfAvailable("clearWarnings");
    private static final Method CREATE_STATEMENT_WITH_INT_AND_INT_METHOD = getMethodIfAvailable("createStatement", int.class, int.class);
    private static final Method PREPARE_STATEMENT_WITH_INT_AND_INT_METHOD = getMethodIfAvailable("prepareCall", String.class, int.class, int.class);
    private static final Method PREPARE_CALL_WITH_INT_AND_INT_METHOD = getMethodIfAvailable("prepareCall", String.class, int.class, int.class);
    private static final Method GET_TYPE_MAP_METHOD = getMethodIfAvailable("getTypeMap");
    private static final Method SET_TYPE_MAP_METHOD = getMethodIfAvailable("setTypeMap", Map.class);
    private static final Method SET_HOLDABILITY_METHOD = getMethodIfAvailable("setHoldability", int.class);
    private static final Method GET_HOLDABILITY_METHOD = getMethodIfAvailable("getHoldability");
    private static final Method SET_SAVEPOINT_METHOD = getMethodIfAvailable("setSavepoint");
    private static final Method SET_SAVEPOINT_WITH_STRING_METHOD = getMethodIfAvailable("setSavepoint", String.class);
    private static final Method ROLLBACK_WITH_SAVEPOINT_METHOD = getMethodIfAvailable("rollback", Savepoint.class);
    private static final Method RELEASE_SAVEPOINT_METHOD = getMethodIfAvailable("releaseSavepoint", Savepoint.class);
    private static final Method CREATE_STATEMENT_WITH_INT_AND_INT_AND_INT_METHOD = getMethodIfAvailable("createStatement", int.class, int.class, int.class);
    private static final Method PREPARE_STATEMENT_WITH_INT_AND_INT_AND_INT_METHOD = getMethodIfAvailable("prepareCall", String.class, int.class, int.class, int.class);
    private static final Method PREPARE_CALL_WITH_INT_AND_INT_AND_INT_METHOD = getMethodIfAvailable("prepareCall", String.class, int.class, int.class, int.class);
    private static final Method PREPARE_STATEMENT_WITH_INT_METHOD = getMethodIfAvailable("prepareStatement", String.class, int.class);
    private static final Method PREPARE_STATEMENT_WITH_INT_ARRAY_METHOD = getMethodIfAvailable("prepareStatement", String.class, int[].class);
    private static final Method PREPARE_STATEMENT_WITH_STRING_ARRAY_METHOD = getMethodIfAvailable("prepareStatement", String.class, String[].class);
    private static final Method CREATE_CLOB_METHOD = getMethodIfAvailable("createClob");
    private static final Method CREATE_BLOB_METHOD = getMethodIfAvailable("createBlob");
    private static final Method CREATE_NCLOB_METHOD = getMethodIfAvailable("createNClob");
    private static final Method CREATE_SQL_XML_METHOD = getMethodIfAvailable("createSQLXML");
    private static final Method IS_VALID_METHOD = getMethodIfAvailable("isValid", int.class);
    private static final Method SET_CLIENT_INFO_WITH_STRING_AND_STRING_METHOD = getMethodIfAvailable("setClientInfo", String.class, String.class);
    private static final Method SET_CLIENT_INFO_WITH_PROPERTIES_METHOD = getMethodIfAvailable("setClientInfo", Properties.class);
    private static final Method SET_CLIENT_INFO_WITH_STRING_METHOD = getMethodIfAvailable("getClientInfo", String.class);
    private static final Method GET_CLIENT_INFO_METHOD = getMethodIfAvailable("getClientInfo");
    private static final Method CREATE_ARRAY_OF_METHOD = getMethodIfAvailable("createArrayOf", String.class, Object[].class);
    private static final Method CREATE_STRUCT_METHOD = getMethodIfAvailable("createStruct", String.class, Object[].class);
    private static final Method SET_SCHEMA_METHOD = getMethodIfAvailable("setSchema", String.class);
    private static final Method GET_SCHEMA_METHOD = getMethodIfAvailable("getSchema");
    private static final Method ABORT_METHOD = getMethodIfAvailable("abort", Executor.class);
    private static final Method SET_NETWORK_TIMEOUT_METHOD = getMethodIfAvailable("setNetworkTimeout", Executor.class, int.class);
    private static final Method GET_NETWORK_TIMEOUT_METHOD = getMethodIfAvailable("getNetworkTimeout");
    private static final Method UNWRAP_METHOD = getMethodIfAvailable("unwrap", Class.class);
    private static final Method IS_RAPPER_FOR_METHOD = getMethodIfAvailable("isWrapperFor", Class.class);

    private static Method getMethodIfAvailable(String name, Class... parameterTypes) {
        return DelegatingUtils.getMethodIfAvailable(Connection.class, name, parameterTypes);
    }


    private ConnectionProxyLogic proxyLogic;

    public DelegatingConnection(ConnectionProxyLogic proxyLogic) {
        this.proxyLogic = proxyLogic;
    }

    @Override
    public Object getTarget() {
        try {
            return this.proxyLogic.invoke(DelegatingConnection.this, DelegatingUtils.GET_TARGET_METHOD, null);
        } catch (Throwable throwable) {
            throw new DataSourceProxyException("Failed to invoke method: getTarget", throwable);
        }
    }

    private Object invoke(final Method method, final Object... args) throws SQLException {
        return DelegatingUtils.invoke(method, new DelegatingUtils.InvocationCallback() {
            @Override
            public Object invoke() throws Throwable {
                return proxyLogic.invoke(DelegatingConnection.this, method, args);
            }
        });
    }

    @Override
    public Statement createStatement() throws SQLException {
        return (Statement) invoke(CREATE_STATEMENT_METHOD);
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return (PreparedStatement) invoke(PREPARE_STATEMENT_METHOD, sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return (CallableStatement) invoke(PREPARE_CALL_METHOD, sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return (String) invoke(NATIVE_SQL_METHOD, sql);
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        invoke(SET_AUTO_COMMIT_METHOD, autoCommit);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return (Boolean) invoke(GET_AUTO_COMMIT_METHOD);
    }

    @Override
    public void commit() throws SQLException {
        invoke(COMMIT_METHOD);
    }

    @Override
    public void rollback() throws SQLException {
        invoke(ROLLBACK_METHOD);
    }

    @Override
    public void close() throws SQLException {
        invoke(CLOSE_METHOD);
    }

    @Override
    public boolean isClosed() throws SQLException {
        return (Boolean) invoke(IS_CLOSED_METHOD);
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return (DatabaseMetaData) invoke(GET_META_DATA_METHOD);
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        invoke(SET_READONLY_METHOD, readOnly);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return (Boolean) invoke(IS_READ_ONLY_METHOD);
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        invoke(SET_CATALOG_METHOD, catalog);
    }

    @Override
    public String getCatalog() throws SQLException {
        return (String) invoke(GET_CATALOG_METHOD);
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        invoke(SET_TRANSACTION_ISOLATION_METHOD, level);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return (Integer) invoke(GET_TRANSACTION_ISOLATION_METHOD);
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
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return (Statement) invoke(CREATE_STATEMENT_WITH_INT_AND_INT_METHOD, resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return (PreparedStatement) invoke(PREPARE_STATEMENT_WITH_INT_AND_INT_METHOD, sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return (CallableStatement) invoke(PREPARE_CALL_WITH_INT_AND_INT_METHOD, sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return (Map<String, Class<?>>) invoke(GET_TYPE_MAP_METHOD);
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        invoke(SET_TYPE_MAP_METHOD, map);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        invoke(SET_HOLDABILITY_METHOD, holdability);
    }

    @Override
    public int getHoldability() throws SQLException {
        return (Integer) invoke(GET_HOLDABILITY_METHOD);
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return (Savepoint) invoke(SET_SAVEPOINT_METHOD);
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return (Savepoint) invoke(SET_SAVEPOINT_WITH_STRING_METHOD, name);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        invoke(ROLLBACK_WITH_SAVEPOINT_METHOD, savepoint);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        invoke(RELEASE_SAVEPOINT_METHOD, savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return (Statement) invoke(CREATE_STATEMENT_WITH_INT_AND_INT_AND_INT_METHOD, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return (PreparedStatement) invoke(PREPARE_STATEMENT_WITH_INT_AND_INT_AND_INT_METHOD, sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return (CallableStatement) invoke(PREPARE_CALL_WITH_INT_AND_INT_AND_INT_METHOD, sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return (PreparedStatement) invoke(PREPARE_STATEMENT_WITH_INT_METHOD, sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return (PreparedStatement) invoke(PREPARE_STATEMENT_WITH_INT_ARRAY_METHOD, sql, columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return (PreparedStatement) invoke(PREPARE_STATEMENT_WITH_STRING_ARRAY_METHOD, sql, columnNames);
    }

    @Override
    public Clob createClob() throws SQLException {
        return (Clob) invoke(CREATE_CLOB_METHOD);
    }

    @Override
    public Blob createBlob() throws SQLException {
        return (Blob) invoke(CREATE_BLOB_METHOD);
    }

    @Override
    public NClob createNClob() throws SQLException {
        return (NClob) invoke(CREATE_NCLOB_METHOD);
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return (SQLXML) invoke(CREATE_SQL_XML_METHOD);
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return (Boolean) invoke(IS_VALID_METHOD, timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        Method method = SET_CLIENT_INFO_WITH_STRING_AND_STRING_METHOD;

        if (method == null) {
            throw new DataSourceProxyException("Could not find setClientInfo method");
        }

        try {
            this.proxyLogic.invoke(this, method, new Object[]{name, value});
        } catch (Throwable throwable) {
            if (throwable instanceof SQLClientInfoException) {
                throw (SQLClientInfoException) throwable;
            }
            throw new DataSourceProxyException("Failed to invoke method: setClientInfo", throwable);
        }
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        Method method = SET_CLIENT_INFO_WITH_PROPERTIES_METHOD;

        if (method == null) {
            throw new DataSourceProxyException("Could not find setClientInfo method");
        }

        try {
            this.proxyLogic.invoke(this, method, new Object[]{properties});
        } catch (Throwable throwable) {
            if (throwable instanceof SQLClientInfoException) {
                throw (SQLClientInfoException) throwable;
            }
            throw new DataSourceProxyException("Failed to invoke method: setClientInfo", throwable);
        }
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return (String) invoke(SET_CLIENT_INFO_WITH_STRING_METHOD, name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return (Properties) invoke(GET_CLIENT_INFO_METHOD);
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return (Array) invoke(CREATE_ARRAY_OF_METHOD, typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return (Struct) invoke(CREATE_STRUCT_METHOD, typeName, attributes);
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        invoke(SET_SCHEMA_METHOD, schema);
    }

    @Override
    public String getSchema() throws SQLException {
        return (String) invoke(GET_SCHEMA_METHOD);
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        invoke(ABORT_METHOD, executor);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        invoke(SET_NETWORK_TIMEOUT_METHOD, executor, milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return (Integer) invoke(GET_NETWORK_TIMEOUT_METHOD);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return (T) invoke(UNWRAP_METHOD, iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return (Boolean) invoke(IS_RAPPER_FOR_METHOD, iface);
    }
}
