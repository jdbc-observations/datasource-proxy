package net.ttddyy.dsproxy.proxy.delegate;

import net.ttddyy.dsproxy.proxy.StatementProxyLogic;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * A concrete class implementation of {@link PreparedStatement} that delegates to {@link StatementProxyLogic}.
 *
 * @author Tadaya Tsuyukubo
 * @see DelegatingJdbcProxyFactory
 * @since 1.5
 */
public class DelegatingPreparedStatement extends DelegatingStatement implements PreparedStatement {

    private static final Method EXECUTE_QUERY_METHOD = getMethodIfAvailable("executeQuery");
    private static final Method EXECUTE_UPDATE_METHOD = getMethodIfAvailable("executeUpdate");
    private static final Method SET_NULL_METHOD = getMethodIfAvailable("setNull", int.class, int.class);
    private static final Method SET_BOOLEAN_METHOD = getMethodIfAvailable("setBoolean", int.class, boolean.class);
    private static final Method SET_BYTE_METHOD = getMethodIfAvailable("setByte", int.class, byte.class);
    private static final Method SET_SHORT_METHOD = getMethodIfAvailable("setShort", int.class, short.class);
    private static final Method SET_INT_METHOD = getMethodIfAvailable("setInt", int.class, int.class);
    private static final Method SET_LONG_METHOD = getMethodIfAvailable("setLong", int.class, long.class);
    private static final Method SET_FLOAT_METHOD = getMethodIfAvailable("setFloat", int.class, float.class);
    private static final Method SET_DOUBLE_METHOD = getMethodIfAvailable("setDouble", int.class, double.class);
    private static final Method SET_BIG_DECIMAL_METHOD = getMethodIfAvailable("setBigDecimal", int.class, BigDecimal.class);
    private static final Method SET_STRING_METHOD = getMethodIfAvailable("setString", int.class, String.class);
    private static final Method SET_BYTES_METHOD = getMethodIfAvailable("setBytes", int.class, byte[].class);
    private static final Method SET_DATE_METHOD = getMethodIfAvailable("setDate", int.class, Date.class);
    private static final Method SET_TIME_METHOD = getMethodIfAvailable("setTime", int.class, Time.class);
    private static final Method SET_TIMESTAMP_METHOD = getMethodIfAvailable("setTimestamp", int.class, Timestamp.class);
    private static final Method SET_ASCII_STREAM_WITH_INT_METHOD = getMethodIfAvailable("setAsciiStream", int.class, InputStream.class, int.class);
    private static final Method SET_UNICODES_STREAM_METHOD = getMethodIfAvailable("setUnicodeStream", int.class, InputStream.class, int.class);
    private static final Method SET_BINARY_STREAM_WITH_INT_METHOD = getMethodIfAvailable("setBinaryStream", int.class, InputStream.class, int.class);
    private static final Method CLEAR_PARAMETERS_METHOD = getMethodIfAvailable("clearParameters");
    private static final Method SET_OBJECT_WITH_INT_METHOD = getMethodIfAvailable("setObject", int.class, Object.class, int.class);
    private static final Method SET_OBJECT_METHOD = getMethodIfAvailable("setObject", int.class, Object.class);
    private static final Method EXECUTE_METHOD = getMethodIfAvailable("execute");
    private static final Method ADD_BATCH_METHOD = getMethodIfAvailable("addBatch");
    private static final Method SET_CHRACTER_STREAM_METHOD = getMethodIfAvailable("setCharacterStream", int.class, Reader.class, int.class);
    private static final Method SET_REF_METHOD = getMethodIfAvailable("setRef", int.class, Ref.class);
    private static final Method SET_BLOB_METHOD = getMethodIfAvailable("setBlob", int.class, Blob.class);
    private static final Method SET_CLOB_METHOD = getMethodIfAvailable("setClob", int.class, Clob.class);
    private static final Method SET_ARRAY_METHOD = getMethodIfAvailable("setArray", int.class, Array.class);
    private static final Method SET_GET_META_DATA_METHOD = getMethodIfAvailable("getMetaData");
    private static final Method SET_DATE_WITH_CALENDAR_METHOD = getMethodIfAvailable("setDate", int.class, Date.class, Calendar.class);
    private static final Method SET_TIME_WITH_CALENDAR_METHOD = getMethodIfAvailable("setTime", int.class, Time.class, Calendar.class);
    private static final Method SET_TIMESTAMP_WITH_CALENDAR_METHOD = getMethodIfAvailable("setTimestamp", int.class, Timestamp.class, Calendar.class);
    private static final Method SET_NULL_WITH_STRING_METHOD = getMethodIfAvailable("setNull", int.class, int.class, String.class);
    private static final Method SET_URL_METHOD = getMethodIfAvailable("setURL", int.class, URL.class);
    private static final Method GET_PARAMETER_META_DATA_METHOD = getMethodIfAvailable("getParameterMetaData");
    private static final Method SET_ROW_ID_METHOD = getMethodIfAvailable("setRowId", int.class, RowId.class);
    private static final Method SET_N_STRING_METHOD = getMethodIfAvailable("setNString", int.class, String.class);
    private static final Method SET_N_CHARACTER_STREAM_WITH_LONG_METHOD = getMethodIfAvailable("setNCharacterStream", int.class, Reader.class, long.class);
    private static final Method SET_N_CLOB_METHOD = getMethodIfAvailable("setNClob", int.class, NClob.class);
    private static final Method SET_CLOB_WITH_READER_AND_LONG_METHOD = getMethodIfAvailable("setClob", int.class, Reader.class, long.class);
    private static final Method SET_BLOB_WITH_INPUT_STREAM_AND_LONG_METHOD = getMethodIfAvailable("setBlob", int.class, InputStream.class, long.class);
    private static final Method SET_N_CLOB_WITH_READER_AND_LONG_METHOD = getMethodIfAvailable("setNClob", int.class, Reader.class, long.class);
    private static final Method SET_SQL_XML_METHOD = getMethodIfAvailable("setSQLXML", int.class, SQLXML.class);
    private static final Method SET_OBJECT_WITH_INT_AND_INT_METHOD = getMethodIfAvailable("setObject", int.class, Object.class, int.class, int.class);
    private static final Method SET_ASCII_STREAM_WITH_LONG_METHOD = getMethodIfAvailable("setAsciiStream", int.class, InputStream.class, long.class);
    private static final Method SET_BINARY_STREAM_WITH_LONG_METHOD = getMethodIfAvailable("setBinaryStream", int.class, InputStream.class, long.class);
    private static final Method SET_CHARACTER_STREAM_WITH_LONG_METHOD = getMethodIfAvailable("setCharacterStream", int.class, Reader.class, long.class);
    private static final Method SET_ASCII_STREAM_METHOD = getMethodIfAvailable("setAsciiStream", int.class, InputStream.class);
    private static final Method SET_BINARY_STREAM_METHOD = getMethodIfAvailable("setBinaryStream", int.class, InputStream.class);
    private static final Method SET_CHARACTER_STREAM_METHOD = getMethodIfAvailable("setCharacterStream", int.class, Reader.class);
    private static final Method SET_N_CHARACTER_STREAM_METHOD = getMethodIfAvailable("setNCharacterStream", int.class, Reader.class);
    private static final Method SET_CLOB_WITH_READER_METHOD = getMethodIfAvailable("setClob", int.class, Reader.class);
    private static final Method SET_BLOB_WITH_INPUT_STREAM_METHOD = getMethodIfAvailable("setBlob", int.class, InputStream.class);
    private static final Method SET_N_CLOB_WITH_READER_METHOD = getMethodIfAvailable("setNClob", int.class, Reader.class);
    private static final Method SET_OBJECT_WITH_SQL_TYPE_AND_INT_METHOD = getMethodIfAvailable("setObject", int.class, Object.class, SQLType.class, int.class);
    private static final Method SET_OBJECT_WITH_SQL_TYPE_METHOD = getMethodIfAvailable("setObject", int.class, Object.class, SQLType.class);
    private static final Method SET_EXECUTE_LARGE_UPDATE_METHOD = getMethodIfAvailable("executeLargeUpdate");

    private static Method getMethodIfAvailable(String name, Class... parameterTypes) {
        return DelegatingUtils.getMethodIfAvailable(PreparedStatement.class, name, parameterTypes);
    }

    public DelegatingPreparedStatement(StatementProxyLogic proxyLogic) {
        super(proxyLogic);
    }

    private Object invoke(final Method method, final Object... args) throws SQLException {
        return DelegatingUtils.invoke(method, new DelegatingUtils.InvocationCallback() {
            @Override
            public Object invoke() throws Throwable {
                return DelegatingPreparedStatement.this.proxyLogic.invoke(method, args);
            }
        });
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        return (ResultSet) invoke(EXECUTE_QUERY_METHOD);
    }


    @Override
    public int executeUpdate() throws SQLException {
        return (Integer) invoke(EXECUTE_UPDATE_METHOD);
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        invoke(SET_NULL_METHOD, parameterIndex, sqlType);
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        invoke(SET_BOOLEAN_METHOD, parameterIndex, x);
    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        invoke(SET_BYTE_METHOD, parameterIndex, x);
    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        invoke(SET_SHORT_METHOD, parameterIndex, x);
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        invoke(SET_INT_METHOD, parameterIndex, x);
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        invoke(SET_LONG_METHOD, parameterIndex, x);
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        invoke(SET_FLOAT_METHOD, parameterIndex, x);

    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        invoke(SET_DOUBLE_METHOD, parameterIndex, x);
    }


    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        invoke(SET_BIG_DECIMAL_METHOD, parameterIndex, x);
    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        invoke(SET_STRING_METHOD, parameterIndex, x);
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        invoke(SET_BYTES_METHOD, parameterIndex, x);
    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        invoke(SET_DATE_METHOD, parameterIndex, x);
    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        invoke(SET_TIME_METHOD, parameterIndex, x);
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        invoke(SET_TIMESTAMP_METHOD, parameterIndex, x);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        invoke(SET_ASCII_STREAM_WITH_INT_METHOD, parameterIndex, x, length);
    }

    @Override
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        invoke(SET_UNICODES_STREAM_METHOD, parameterIndex, x, length);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        invoke(SET_BINARY_STREAM_WITH_INT_METHOD, parameterIndex, x, length);
    }

    @Override
    public void clearParameters() throws SQLException {
        invoke(CLEAR_PARAMETERS_METHOD);
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        invoke(SET_OBJECT_WITH_INT_METHOD, parameterIndex, x, targetSqlType);
    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        invoke(SET_OBJECT_METHOD, parameterIndex, x);
    }

    @Override
    public boolean execute() throws SQLException {
        return (Boolean) invoke(EXECUTE_METHOD);
    }

    @Override
    public void addBatch() throws SQLException {
        invoke(ADD_BATCH_METHOD);
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        invoke(SET_CHRACTER_STREAM_METHOD, parameterIndex, reader, length);
    }

    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {
        invoke(SET_REF_METHOD, parameterIndex, x);
    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        invoke(SET_BLOB_METHOD, parameterIndex, x);
    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        invoke(SET_CLOB_METHOD, parameterIndex, x);
    }

    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {
        invoke(SET_ARRAY_METHOD, parameterIndex, x);
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return (ResultSetMetaData) invoke(SET_GET_META_DATA_METHOD);
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        invoke(SET_DATE_WITH_CALENDAR_METHOD, parameterIndex, x, cal);
    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        invoke(SET_TIME_WITH_CALENDAR_METHOD, parameterIndex, x, cal);
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        invoke(SET_TIMESTAMP_WITH_CALENDAR_METHOD, parameterIndex, x, cal);
    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        invoke(SET_NULL_WITH_STRING_METHOD, parameterIndex, sqlType, typeName);
    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        invoke(SET_URL_METHOD, parameterIndex, x);
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        return (ParameterMetaData) invoke(GET_PARAMETER_META_DATA_METHOD);
    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        invoke(SET_ROW_ID_METHOD, parameterIndex, x);
    }

    @Override
    public void setNString(int parameterIndex, String value) throws SQLException {
        invoke(SET_N_STRING_METHOD, parameterIndex, value);
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        invoke(SET_N_CHARACTER_STREAM_WITH_LONG_METHOD, parameterIndex, value, length);
    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        invoke(SET_N_CLOB_METHOD, parameterIndex, value);
    }

    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        invoke(SET_CLOB_WITH_READER_AND_LONG_METHOD, parameterIndex, reader, length);
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        invoke(SET_BLOB_WITH_INPUT_STREAM_AND_LONG_METHOD, parameterIndex, inputStream, length);
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        invoke(SET_N_CLOB_WITH_READER_AND_LONG_METHOD, parameterIndex, reader, length);
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        invoke(SET_SQL_XML_METHOD, parameterIndex, xmlObject);
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        invoke(SET_OBJECT_WITH_INT_AND_INT_METHOD, parameterIndex, x, targetSqlType, scaleOrLength);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        invoke(SET_ASCII_STREAM_WITH_LONG_METHOD, parameterIndex, x, length);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        invoke(SET_BINARY_STREAM_WITH_LONG_METHOD, parameterIndex, x, length);
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        invoke(SET_CHARACTER_STREAM_WITH_LONG_METHOD, parameterIndex, reader, length);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        invoke(SET_ASCII_STREAM_METHOD, parameterIndex, x);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        invoke(SET_BINARY_STREAM_METHOD, parameterIndex, x);
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        invoke(SET_CHARACTER_STREAM_METHOD, parameterIndex, reader);
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        invoke(SET_N_CHARACTER_STREAM_METHOD, parameterIndex, value);
    }

    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        invoke(SET_CLOB_WITH_READER_METHOD, parameterIndex, reader);
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        invoke(SET_BLOB_WITH_INPUT_STREAM_METHOD, parameterIndex, inputStream);
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        invoke(SET_N_CLOB_WITH_READER_METHOD, parameterIndex, reader);
    }

    @Override
    public void setObject(int parameterIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
        invoke(SET_OBJECT_WITH_SQL_TYPE_AND_INT_METHOD, parameterIndex, x, targetSqlType, scaleOrLength);
    }

    @Override
    public void setObject(int parameterIndex, Object x, SQLType targetSqlType) throws SQLException {
        invoke(SET_OBJECT_WITH_SQL_TYPE_METHOD, parameterIndex, x, targetSqlType);
    }

    @Override
    public long executeLargeUpdate() throws SQLException {
        return (Long) invoke(SET_EXECUTE_LARGE_UPDATE_METHOD);
    }

}
