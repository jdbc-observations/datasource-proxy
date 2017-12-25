package net.ttddyy.dsproxy.proxy.delegate;

import net.ttddyy.dsproxy.proxy.StatementProxyLogic;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

/**
 * A concrete class implementation of {@link CallableStatement} that delegates to {@link StatementProxyLogic}.
 *
 * @author Tadaya Tsuyukubo
 * @see DelegatingJdbcProxyFactory
 * @since 1.5
 */
public class DelegatingCallableStatement extends DelegatingPreparedStatement implements CallableStatement {

    private static Method getMethodIfAvailable(String name, Class... parameterTypes) {
        return DelegatingUtils.getMethodIfAvailable(CallableStatement.class, name, parameterTypes);
    }

    public DelegatingCallableStatement(StatementProxyLogic proxyLogic) {
        super(proxyLogic);
    }

    private Object invoke(final Method method, final Object... args) throws SQLException {
        return DelegatingUtils.invoke(method, new DelegatingUtils.InvocationCallback() {
            @Override
            public Object invoke() throws Throwable {
                return DelegatingCallableStatement.this.proxyLogic.invoke(method, args);
            }
        });
    }

    private static final Method REGISTER_OUT_PARAMETER_WITH_INT_AND_INT_METHOD = getMethodIfAvailable("registerOutParameter", int.class, int.class);

    @Override
    public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
        invoke(REGISTER_OUT_PARAMETER_WITH_INT_AND_INT_METHOD, parameterIndex, sqlType);
    }

    private static final Method REGISTER_OUT_PARAMETER_WITH_INT_AND_INT_AND_INT_METHOD = getMethodIfAvailable("registerOutParameter", int.class, int.class, int.class);

    @Override
    public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
        invoke(REGISTER_OUT_PARAMETER_WITH_INT_AND_INT_AND_INT_METHOD, parameterIndex, sqlType, scale);
    }

    private static final Method WAS_NULL_METHOD = getMethodIfAvailable("wasNull");

    @Override
    public boolean wasNull() throws SQLException {
        return (Boolean) invoke(WAS_NULL_METHOD);
    }

    private static final Method GET_STRING_WITH_INT_METHOD = getMethodIfAvailable("getString", int.class);

    @Override
    public String getString(int parameterIndex) throws SQLException {
        return (String) invoke(GET_STRING_WITH_INT_METHOD, parameterIndex);
    }

    private static final Method GET_BOOLEAN_WITH_INT_METHOD = getMethodIfAvailable("getBoolean", int.class);

    @Override
    public boolean getBoolean(int parameterIndex) throws SQLException {
        return (Boolean) invoke(GET_BOOLEAN_WITH_INT_METHOD, parameterIndex);
    }

    private static final Method GET_BYTE_WITH_INT_METHOD = getMethodIfAvailable("getByte", int.class);

    @Override
    public byte getByte(int parameterIndex) throws SQLException {
        return (Byte) invoke(GET_BYTE_WITH_INT_METHOD, parameterIndex);
    }

    private static final Method GET_SHORT_WITH_INT_METHOD = getMethodIfAvailable("getShort", int.class);

    @Override
    public short getShort(int parameterIndex) throws SQLException {
        return (Short) invoke(GET_SHORT_WITH_INT_METHOD, parameterIndex);
    }

    private static final Method GET_INT_WITH_INT_METHOD = getMethodIfAvailable("getInt", int.class);

    @Override
    public int getInt(int parameterIndex) throws SQLException {
        return (Integer) invoke(GET_INT_WITH_INT_METHOD, parameterIndex);
    }

    private static final Method GET_LONG_WITH_INT_METHOD = getMethodIfAvailable("getLong", int.class);

    @Override
    public long getLong(int parameterIndex) throws SQLException {
        return (Long) invoke(GET_LONG_WITH_INT_METHOD, parameterIndex);
    }

    private static final Method GET_FLOAT_WITH_INT_METHOD = getMethodIfAvailable("getFloat", int.class);

    @Override
    public float getFloat(int parameterIndex) throws SQLException {
        return (Float) invoke(GET_FLOAT_WITH_INT_METHOD, parameterIndex);
    }

    private static final Method GET_DOUBLE_WITH_INT_METHOD = getMethodIfAvailable("getDouble", int.class);

    @Override
    public double getDouble(int parameterIndex) throws SQLException {
        return (Double) invoke(GET_DOUBLE_WITH_INT_METHOD, parameterIndex);
    }

    private static final Method GET_BIG_DECIMAL_WITH_INT_METHOD = getMethodIfAvailable("getBigDecimal", int.class, int.class);

    @Override
    public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
        return (BigDecimal) invoke(GET_BIG_DECIMAL_WITH_INT_METHOD, parameterIndex, scale);
    }

    private static final Method GET_BYTES_WITH_INT_METHOD = getMethodIfAvailable("getBytes", int.class);

    @Override
    public byte[] getBytes(int parameterIndex) throws SQLException {
        return (byte[]) invoke(GET_BYTES_WITH_INT_METHOD, parameterIndex);
    }

    private static final Method GET_DATE_WITH_INT_METHOD = getMethodIfAvailable("getDate", int.class);

    @Override
    public Date getDate(int parameterIndex) throws SQLException {
        return (Date) invoke(GET_DATE_WITH_INT_METHOD, parameterIndex);
    }

    private static final Method GET_TIME_WITH_INT_METHOD = getMethodIfAvailable("getTime", int.class);

    @Override
    public Time getTime(int parameterIndex) throws SQLException {
        return (Time) invoke(GET_TIME_WITH_INT_METHOD, parameterIndex);
    }

    private static final Method GET_TIMESTAMP_WITH_INT_METHOD = getMethodIfAvailable("getTimestamp", int.class);

    @Override
    public Timestamp getTimestamp(int parameterIndex) throws SQLException {
        return (Timestamp) invoke(GET_TIMESTAMP_WITH_INT_METHOD, parameterIndex);
    }

    private static final Method GET_OBJECT_WITH_INT_METHOD = getMethodIfAvailable("getObject", int.class);

    @Override
    public Object getObject(int parameterIndex) throws SQLException {
        return invoke(GET_OBJECT_WITH_INT_METHOD, parameterIndex);
    }

    private static final Method GET_BIGDECIMAL_WITH_INT_METHOD = getMethodIfAvailable("getBigDecimal", int.class);

    @Override
    public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
        return (BigDecimal) invoke(GET_BIGDECIMAL_WITH_INT_METHOD, parameterIndex);
    }

    private static final Method GET_OBJECT_WITH_MAP_METHOD = getMethodIfAvailable("getObject", int.class, Map.class);

    @Override
    public Object getObject(int parameterIndex, Map<String, Class<?>> map) throws SQLException {
        return invoke(GET_OBJECT_WITH_MAP_METHOD, parameterIndex, map);
    }

    private static final Method GET_REF_WITH_INT_METHOD = getMethodIfAvailable("getRef", int.class);

    @Override
    public Ref getRef(int parameterIndex) throws SQLException {
        return (Ref) invoke(GET_REF_WITH_INT_METHOD, parameterIndex);
    }

    private static final Method GET_BLOB_WITH_INT_METHOD = getMethodIfAvailable("getBlob", int.class);

    @Override
    public Blob getBlob(int parameterIndex) throws SQLException {
        return (Blob) invoke(GET_BLOB_WITH_INT_METHOD, parameterIndex);
    }

    private static final Method GET_CLOB_WITH_INT_METHOD = getMethodIfAvailable("getClob", int.class);

    @Override
    public Clob getClob(int parameterIndex) throws SQLException {
        return (Clob) invoke(GET_CLOB_WITH_INT_METHOD, parameterIndex);
    }

    private static final Method GET_ARRAY_WITH_INT_METHOD = getMethodIfAvailable("getArray", int.class);

    @Override
    public Array getArray(int parameterIndex) throws SQLException {
        return (Array) invoke(GET_ARRAY_WITH_INT_METHOD, parameterIndex);
    }

    private static final Method GET_DATE_WITH_INT_AND_CALENDAR_METHOD = getMethodIfAvailable("getDate", int.class, Calendar.class);

    @Override
    public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
        return (Date) invoke(GET_DATE_WITH_INT_AND_CALENDAR_METHOD, parameterIndex, cal);
    }

    private static final Method GET_TIME_WITH_INT_AND_CALENDAR_METHOD = getMethodIfAvailable("getTime", int.class, Calendar.class);

    @Override
    public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
        return (Time) invoke(GET_TIME_WITH_INT_AND_CALENDAR_METHOD, parameterIndex, cal);
    }

    private static final Method GET_TIMESTAMP_WITH_INT_AND_CALENDAR_METHOD = getMethodIfAvailable("getTimestamp", int.class, Calendar.class);

    @Override
    public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
        return (Timestamp) invoke(GET_TIMESTAMP_WITH_INT_AND_CALENDAR_METHOD, parameterIndex, cal);
    }

    private static final Method REGISTER_OUT_PARAMETER_WITH_INT_AND_INT_AND_STRING_METHOD = getMethodIfAvailable("registerOutParameter", int.class, int.class, String.class);

    @Override
    public void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException {
        invoke(REGISTER_OUT_PARAMETER_WITH_INT_AND_INT_AND_STRING_METHOD, parameterIndex, sqlType, typeName);
    }

    private static final Method REGISTER_OUT_PARAMETER_WITH_STRING_AND_INT_METHOD = getMethodIfAvailable("registerOutParameter", String.class, int.class);

    @Override
    public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
        invoke(REGISTER_OUT_PARAMETER_WITH_STRING_AND_INT_METHOD, parameterName, sqlType);
    }

    private static final Method REGISTER_OUT_PARAMETER_WITH_STRING_AND_INT_AND_INT_METHOD = getMethodIfAvailable("registerOutParameter", String.class, int.class, int.class);

    @Override
    public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
        invoke(REGISTER_OUT_PARAMETER_WITH_STRING_AND_INT_AND_INT_METHOD, parameterName, sqlType, scale);
    }

    private static final Method REGISTER_OUT_PARAMETER_WITH_STRING_AND_INT_AND_STRING_METHOD = getMethodIfAvailable("registerOutParameter", String.class, int.class, String.class);

    @Override
    public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
        invoke(REGISTER_OUT_PARAMETER_WITH_STRING_AND_INT_AND_STRING_METHOD, parameterName, sqlType, typeName);
    }

    private static final Method GET_URL_WITH_INT_METHOD = getMethodIfAvailable("getURL", int.class);

    @Override
    public URL getURL(int parameterIndex) throws SQLException {
        return (URL) invoke(GET_URL_WITH_INT_METHOD, parameterIndex);
    }

    private static final Method SET_URL_METHOD = getMethodIfAvailable("setURL", String.class, URL.class);

    @Override
    public void setURL(String parameterName, URL val) throws SQLException {
        invoke(SET_URL_METHOD, parameterName, val);
    }

    private static final Method SET_NULL_METHOD = getMethodIfAvailable("setNull", String.class, int.class);

    @Override
    public void setNull(String parameterName, int sqlType) throws SQLException {
        invoke(SET_NULL_METHOD, parameterName, sqlType);
    }

    private static final Method SET_BOOLEAN_METHOD = getMethodIfAvailable("setBoolean", String.class, boolean.class);

    @Override
    public void setBoolean(String parameterName, boolean x) throws SQLException {
        invoke(SET_BOOLEAN_METHOD, parameterName, x);
    }

    private static final Method SET_BYTE_METHOD = getMethodIfAvailable("setByte", String.class, byte.class);

    @Override
    public void setByte(String parameterName, byte x) throws SQLException {
        invoke(SET_BYTE_METHOD, parameterName, x);
    }

    private static final Method SET_SHORT_METHOD = getMethodIfAvailable("setShort", String.class, short.class);

    @Override
    public void setShort(String parameterName, short x) throws SQLException {
        invoke(SET_SHORT_METHOD, parameterName, x);
    }

    private static final Method SET_INT_METHOD = getMethodIfAvailable("setInt", String.class, int.class);

    @Override
    public void setInt(String parameterName, int x) throws SQLException {
        invoke(SET_INT_METHOD, parameterName, x);
    }

    private static final Method SET_LONG_METHOD = getMethodIfAvailable("setLong", String.class, long.class);

    @Override
    public void setLong(String parameterName, long x) throws SQLException {
        invoke(SET_LONG_METHOD, parameterName, x);
    }

    private static final Method SET_FLOAT_METHOD = getMethodIfAvailable("setFloat", String.class, float.class);

    @Override
    public void setFloat(String parameterName, float x) throws SQLException {
        invoke(SET_FLOAT_METHOD, parameterName, x);
    }

    private static final Method SET_DOUBLE_METHOD = getMethodIfAvailable("setDouble", String.class, double.class);

    @Override
    public void setDouble(String parameterName, double x) throws SQLException {
        invoke(SET_DOUBLE_METHOD, parameterName, x);
    }

    private static final Method SET_BIG_DECIMAL_METHOD = getMethodIfAvailable("setBigDecimal", String.class, BigDecimal.class);

    @Override
    public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
        invoke(SET_BIG_DECIMAL_METHOD, parameterName, x);
    }

    private static final Method SET_STRING_METHOD = getMethodIfAvailable("setString", String.class, String.class);

    @Override
    public void setString(String parameterName, String x) throws SQLException {
        invoke(SET_STRING_METHOD, parameterName, x);
    }

    private static final Method SET_BYTES_METHOD = getMethodIfAvailable("setBytes", String.class, byte[].class);

    @Override
    public void setBytes(String parameterName, byte[] x) throws SQLException {
        invoke(SET_BYTES_METHOD, parameterName, x);
    }

    private static final Method SET_DATE_METHOD = getMethodIfAvailable("setDate", String.class, Date.class);

    @Override
    public void setDate(String parameterName, Date x) throws SQLException {
        invoke(SET_DATE_METHOD, parameterName, x);
    }

    private static final Method SET_TIME_METHOD = getMethodIfAvailable("setTime", String.class, Time.class);

    @Override
    public void setTime(String parameterName, Time x) throws SQLException {
        invoke(SET_TIME_METHOD, parameterName, x);
    }

    private static final Method SET_TIMESTAMP_METHOD = getMethodIfAvailable("setTimestamp", String.class, Timestamp.class);

    @Override
    public void setTimestamp(String parameterName, Timestamp x) throws SQLException {
        invoke(SET_TIMESTAMP_METHOD, parameterName, x);
    }

    private static final Method SET_ASCII_STREAM_WITH_INT_METHOD = getMethodIfAvailable("setAsciiStream", String.class, InputStream.class, int.class);

    @Override
    public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
        invoke(SET_ASCII_STREAM_WITH_INT_METHOD, parameterName, x, length);
    }

    private static final Method SET_BINARY_STREAM_WITH_INT_METHOD = getMethodIfAvailable("setBinaryStream", String.class, InputStream.class, int.class);

    @Override
    public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
        invoke(SET_BINARY_STREAM_WITH_INT_METHOD, parameterName, x, length);
    }

    private static final Method SET_OBJECT_WITH_INT_AND_INT_METHOD = getMethodIfAvailable("setObject", String.class, Object.class, int.class, int.class);

    @Override
    public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
        invoke(SET_OBJECT_WITH_INT_AND_INT_METHOD, parameterName, x, targetSqlType, scale);
    }

    private static final Method SET_OBJECT_WITH_INT_METHOD = getMethodIfAvailable("setObject", String.class, Object.class, int.class);

    @Override
    public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
        invoke(SET_OBJECT_WITH_INT_METHOD, parameterName, x, targetSqlType);
    }

    private static final Method SET_OBJECT_METHOD = getMethodIfAvailable("setObject", String.class, Object.class);

    @Override
    public void setObject(String parameterName, Object x) throws SQLException {
        invoke(SET_OBJECT_METHOD, parameterName, x);
    }

    private static final Method SET_CHARACTER_STREAM_WITH_INT_METHOD = getMethodIfAvailable("setCharacterStream", String.class, Reader.class, int.class);

    @Override
    public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException {
        invoke(SET_CHARACTER_STREAM_WITH_INT_METHOD, parameterName, reader, length);
    }

    private static final Method SET_DATE_WITH_CALENDAR_METHOD = getMethodIfAvailable("setDate", String.class, Date.class, Calendar.class);

    @Override
    public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
        invoke(SET_DATE_WITH_CALENDAR_METHOD, parameterName, x, cal);
    }

    private static final Method SET_TIME_WITH_CALENDAR_METHOD = getMethodIfAvailable("setTime", String.class, Time.class, Calendar.class);

    @Override
    public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
        invoke(SET_TIME_WITH_CALENDAR_METHOD, parameterName, x, cal);
    }

    private static final Method SET_TIMESTAMP_WITH_CALENDAR_METHOD = getMethodIfAvailable("setTimestamp", String.class, Timestamp.class, Calendar.class);

    @Override
    public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
        invoke(SET_TIMESTAMP_WITH_CALENDAR_METHOD, parameterName, x, cal);
    }

    private static final Method SET_NULL_WITH_STRING_METHOD = getMethodIfAvailable("setNull", String.class, int.class, String.class);

    @Override

    public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
        invoke(SET_NULL_WITH_STRING_METHOD, parameterName, sqlType, typeName);
    }

    private static final Method GET_STRING_WITH_STRING_METHOD = getMethodIfAvailable("getString", String.class);

    @Override
    public String getString(String parameterName) throws SQLException {
        return (String) invoke(GET_STRING_WITH_STRING_METHOD, parameterName);
    }

    private static final Method GET_BOOLEAN_WITH_STRING_METHOD = getMethodIfAvailable("getBoolean", String.class);

    @Override
    public boolean getBoolean(String parameterName) throws SQLException {
        return (Boolean) invoke(GET_BOOLEAN_WITH_STRING_METHOD, parameterName);
    }

    private static final Method GET_BYTE_WITH_STRING_METHOD = getMethodIfAvailable("getByte", String.class);

    @Override
    public byte getByte(String parameterName) throws SQLException {
        return (Byte) invoke(GET_BYTE_WITH_STRING_METHOD, parameterName);
    }

    private static final Method GET_SHORT_WITH_STRING_METHOD = getMethodIfAvailable("getShort", String.class);

    @Override
    public short getShort(String parameterName) throws SQLException {
        return (Short) invoke(GET_SHORT_WITH_STRING_METHOD, parameterName);
    }

    private static final Method GET_INT_WITH_STRING_METHOD = getMethodIfAvailable("getInt", String.class);

    @Override
    public int getInt(String parameterName) throws SQLException {
        return (Integer) invoke(GET_INT_WITH_STRING_METHOD, parameterName);
    }

    private static final Method GET_LONG_WITH_STRING_METHOD = getMethodIfAvailable("getLong", String.class);

    @Override
    public long getLong(String parameterName) throws SQLException {
        return (Long) invoke(GET_LONG_WITH_STRING_METHOD, parameterName);
    }

    private static final Method GET_FLOAT_WITH_STRING_METHOD = getMethodIfAvailable("getFloat");

    @Override
    public float getFloat(String parameterName) throws SQLException {
        return (Float) invoke(GET_FLOAT_WITH_STRING_METHOD, parameterName);
    }

    private static final Method GET_DOUBLE_WITH_STRING_METHOD = getMethodIfAvailable("getDouble", String.class);

    @Override
    public double getDouble(String parameterName) throws SQLException {
        return (Double) invoke(GET_DOUBLE_WITH_STRING_METHOD, parameterName);
    }

    private static final Method GET_BYTES_WITH_STRING_METHOD = getMethodIfAvailable("getBytes", String.class);

    @Override
    public byte[] getBytes(String parameterName) throws SQLException {
        return (byte[]) invoke(GET_BYTES_WITH_STRING_METHOD, parameterName);
    }

    private static final Method GET_DATE_WITH_STRING_METHOD = getMethodIfAvailable("getDate", String.class);

    @Override
    public Date getDate(String parameterName) throws SQLException {
        return (Date) invoke(GET_DATE_WITH_STRING_METHOD, parameterName);
    }

    private static final Method GET_TIME_WITH_STRING_METHOD = getMethodIfAvailable("getTime", String.class);

    @Override
    public Time getTime(String parameterName) throws SQLException {
        return (Time) invoke(GET_TIME_WITH_STRING_METHOD, parameterName);
    }

    private static final Method GET_TIMESTAMP_WITH_STRING_METHOD = getMethodIfAvailable("getTimestamp", String.class);

    @Override
    public Timestamp getTimestamp(String parameterName) throws SQLException {
        return (Timestamp) invoke(GET_TIMESTAMP_WITH_STRING_METHOD, parameterName);
    }

    private static final Method GET_OBJECT_WITH_STRING_METHOD = getMethodIfAvailable("getObject", String.class);

    @Override
    public Object getObject(String parameterName) throws SQLException {
        return invoke(GET_OBJECT_WITH_STRING_METHOD, parameterName);
    }

    private static final Method GET_BIGDECIMAL_WITH_STRING_METHOD = getMethodIfAvailable("getBigDecimal", String.class);

    @Override
    public BigDecimal getBigDecimal(String parameterName) throws SQLException {
        return (BigDecimal) invoke(GET_BIGDECIMAL_WITH_STRING_METHOD, parameterName);
    }

    private static final Method GET_OBJECT_WITH_STRING_AND_MAP_METHOD = getMethodIfAvailable("getObject", String.class, Map.class);

    @Override
    public Object getObject(String parameterName, Map<String, Class<?>> map) throws SQLException {
        return invoke(GET_OBJECT_WITH_STRING_AND_MAP_METHOD, parameterName, map);
    }

    private static final Method GET_REF_WITH_STRING_METHOD = getMethodIfAvailable("getRef", String.class);

    @Override
    public Ref getRef(String parameterName) throws SQLException {
        return (Ref) invoke(GET_REF_WITH_STRING_METHOD, parameterName);
    }

    private static final Method GET_BLOB_WITH_STRING_METHOD = getMethodIfAvailable("getBlob", String.class);

    @Override
    public Blob getBlob(String parameterName) throws SQLException {
        return (Blob) invoke(GET_BLOB_WITH_STRING_METHOD, parameterName);
    }

    private static final Method GET_CLOB_WITH_STRING_METHOD = getMethodIfAvailable("getClob", String.class);

    @Override
    public Clob getClob(String parameterName) throws SQLException {
        return (Clob) invoke(GET_CLOB_WITH_STRING_METHOD, parameterName);
    }

    private static final Method GET_ARRAY_WITH_STRING_METHOD = getMethodIfAvailable("getArray", String.class);

    @Override
    public Array getArray(String parameterName) throws SQLException {
        return (Array) invoke(GET_ARRAY_WITH_STRING_METHOD, parameterName);
    }

    private static final Method GET_DATE_WITH_STRING_AND_CALENDAR_METHOD = getMethodIfAvailable("getDate", String.class, Calendar.class);

    @Override
    public Date getDate(String parameterName, Calendar cal) throws SQLException {
        return (Date) invoke(GET_DATE_WITH_STRING_AND_CALENDAR_METHOD, parameterName, cal);
    }

    private static final Method GET_TIME_WITH_STRING_AND_CALENDAR_METHOD = getMethodIfAvailable("getTime", String.class, Calendar.class);

    @Override
    public Time getTime(String parameterName, Calendar cal) throws SQLException {
        return (Time) invoke(GET_TIME_WITH_STRING_AND_CALENDAR_METHOD, parameterName, cal);
    }

    private static final Method GET_TIMESTAMP_WITH_STRING_AND_CALENDAR_METHOD = getMethodIfAvailable("getTimestamp", String.class, Calendar.class);

    @Override
    public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
        return (Timestamp) invoke(GET_TIMESTAMP_WITH_STRING_AND_CALENDAR_METHOD, parameterName, cal);
    }

    private static final Method GET_URL_WITH_STRING_METHOD = getMethodIfAvailable("getURL", String.class);

    @Override
    public URL getURL(String parameterName) throws SQLException {
        return (URL) invoke(GET_URL_WITH_STRING_METHOD, parameterName);
    }

    private static final Method GET_ROW_ID_WITH_INT_METHOD = getMethodIfAvailable("getRowId", int.class);

    @Override
    public RowId getRowId(int parameterIndex) throws SQLException {
        return (RowId) invoke(GET_ROW_ID_WITH_INT_METHOD, parameterIndex);
    }

    private static final Method GET_ROW_ID_WITH_STRING_METHOD = getMethodIfAvailable("getRowId", String.class);

    @Override
    public RowId getRowId(String parameterName) throws SQLException {
        return (RowId) invoke(GET_ROW_ID_WITH_STRING_METHOD, parameterName);
    }

    private static final Method SET_ROW_ID_METHOD = getMethodIfAvailable("setRowId", String.class, RowId.class);

    @Override
    public void setRowId(String parameterName, RowId x) throws SQLException {
        invoke(SET_ROW_ID_METHOD, parameterName, x);
    }

    private static final Method SET_NSTRING_METHOD = getMethodIfAvailable("setNString", String.class, String.class);

    @Override
    public void setNString(String parameterName, String value) throws SQLException {
        invoke(SET_NSTRING_METHOD, parameterName, value);
    }

    private static final Method SET_NCHARACTER_STREAM_WITH_LONG_METHOD = getMethodIfAvailable("setNCharacterStream", String.class, Reader.class, long.class);

    @Override
    public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {
        invoke(SET_NCHARACTER_STREAM_WITH_LONG_METHOD, parameterName, value, length);
    }

    private static final Method SET_NCLOB_METHOD = getMethodIfAvailable("setNClob", String.class, NClob.class);

    @Override
    public void setNClob(String parameterName, NClob value) throws SQLException {
        invoke(SET_NCLOB_METHOD, parameterName, value);
    }

    private static final Method SET_CLOB_WITH_READER_AND_LONG_METHOD = getMethodIfAvailable("setClob", String.class, Reader.class, long.class);

    @Override
    public void setClob(String parameterName, Reader reader, long length) throws SQLException {
        invoke(SET_CLOB_WITH_READER_AND_LONG_METHOD, parameterName, reader, length);
    }

    private static final Method SET_BLOB_WITH_INPUT_STREAM_AND_LONG_METHOD = getMethodIfAvailable("setBlob", String.class, InputStream.class, long.class);

    @Override
    public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
        invoke(SET_BLOB_WITH_INPUT_STREAM_AND_LONG_METHOD, parameterName, inputStream, length);
    }

    private static final Method SET_NCLOB_WITH_READER_AND_LONG_METHOD = getMethodIfAvailable("setNClob", String.class, Reader.class, long.class);

    @Override
    public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
        invoke(SET_NCLOB_WITH_READER_AND_LONG_METHOD, parameterName, reader, length);
    }

    private static final Method GET_NCLOB_WITH_INT_METHOD = getMethodIfAvailable("getNClob", int.class);

    @Override
    public NClob getNClob(int parameterIndex) throws SQLException {
        return (NClob) invoke(GET_NCLOB_WITH_INT_METHOD, parameterIndex);
    }

    private static final Method GET_NCLOB_WITH_STRING_METHOD = getMethodIfAvailable("getNClob", String.class);

    @Override
    public NClob getNClob(String parameterName) throws SQLException {
        return (NClob) invoke(GET_NCLOB_WITH_STRING_METHOD, parameterName);
    }

    private static final Method SET_SQLXML_METHOD = getMethodIfAvailable("setSQLXML", String.class, SQLXML.class);

    @Override
    public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
        invoke(SET_SQLXML_METHOD, parameterName, xmlObject);
    }

    private static final Method GET_SQLXML_WITH_INT_METHOD = getMethodIfAvailable("getSQLXML", int.class);

    @Override
    public SQLXML getSQLXML(int parameterIndex) throws SQLException {
        return (SQLXML) invoke(GET_SQLXML_WITH_INT_METHOD, parameterIndex);
    }

    private static final Method GET_SQLXML_WITH_STRING_METHOD = getMethodIfAvailable("getSQLXML", String.class);

    @Override
    public SQLXML getSQLXML(String parameterName) throws SQLException {
        return (SQLXML) invoke(GET_SQLXML_WITH_STRING_METHOD, parameterName);
    }

    private static final Method GET_NSTRING_WITH_INT_METHOD = getMethodIfAvailable("getNString", int.class);

    @Override
    public String getNString(int parameterIndex) throws SQLException {
        return (String) invoke(GET_NSTRING_WITH_INT_METHOD, parameterIndex);
    }

    private static final Method GET_NSTRING_WITH_STRING_METHOD = getMethodIfAvailable("getNString", String.class);

    @Override
    public String getNString(String parameterName) throws SQLException {
        return (String) invoke(GET_NSTRING_WITH_STRING_METHOD, parameterName);
    }

    private static final Method GET_NCHARACTER_STREAM_WITH_INT_METHOD = getMethodIfAvailable("getNCharacterStream", int.class);

    @Override
    public Reader getNCharacterStream(int parameterIndex) throws SQLException {
        return (Reader) invoke(GET_NCHARACTER_STREAM_WITH_INT_METHOD, parameterIndex);
    }

    private static final Method GET_NCHARACTER_STREAM_WITH_STRING_METHOD = getMethodIfAvailable("getNCharacterStream", String.class);

    @Override
    public Reader getNCharacterStream(String parameterName) throws SQLException {
        return (Reader) invoke(GET_NCHARACTER_STREAM_WITH_STRING_METHOD, parameterName);
    }

    private static final Method GET_CHARACTER_STREAM_WITH_INT_METHOD = getMethodIfAvailable("getCharacterStream", int.class);

    @Override
    public Reader getCharacterStream(int parameterIndex) throws SQLException {
        return (Reader) invoke(GET_CHARACTER_STREAM_WITH_INT_METHOD, parameterIndex);
    }

    private static final Method GET_CHARACTER_STREAM_WITH_STRING_METHOD = getMethodIfAvailable("getCharacterStream", String.class);

    @Override
    public Reader getCharacterStream(String parameterName) throws SQLException {
        return (Reader) invoke(GET_CHARACTER_STREAM_WITH_STRING_METHOD, parameterName);
    }

    private static final Method SET_BLOB_METHOD = getMethodIfAvailable("setBlob", String.class, Blob.class);

    @Override
    public void setBlob(String parameterName, Blob x) throws SQLException {
        invoke(SET_BLOB_METHOD, parameterName, x);
    }

    private static final Method SET_CLOB_METHOD = getMethodIfAvailable("setClob", String.class, Clob.class);

    @Override
    public void setClob(String parameterName, Clob x) throws SQLException {
        invoke(SET_CLOB_METHOD, parameterName, x);
    }

    private static final Method SET_ASCII_STREAM_WITH_LONG_METHOD = getMethodIfAvailable("setAsciiStream", String.class, InputStream.class, long.class);

    @Override
    public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
        invoke(SET_ASCII_STREAM_WITH_LONG_METHOD, parameterName, x, length);
    }

    private static final Method SET_BINARY_STREAM_WITH_LONG_METHOD = getMethodIfAvailable("setBinaryStream", String.class, InputStream.class, long.class);

    @Override
    public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {
        invoke(SET_BINARY_STREAM_WITH_LONG_METHOD, parameterName, x, length);
    }

    private static final Method SET_CHARACTER_STREAM_STREAM_WITH_LONG_METHOD = getMethodIfAvailable("setCharacterStream", String.class, Reader.class, long.class);

    @Override
    public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
        invoke(SET_CHARACTER_STREAM_STREAM_WITH_LONG_METHOD, parameterName, reader, length);
    }

    private static final Method SET_ASCII_STREAM_METHOD = getMethodIfAvailable("setAsciiStream", String.class, InputStream.class);

    @Override
    public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
        invoke(SET_ASCII_STREAM_METHOD, parameterName, x);
    }

    private static final Method SET_BINARY_STREAM_METHOD = getMethodIfAvailable("setBinaryStream", String.class, InputStream.class);

    @Override
    public void setBinaryStream(String parameterName, InputStream x) throws SQLException {
        invoke(SET_BINARY_STREAM_METHOD, parameterName, x);
    }

    private static final Method SET_CHARACTER_STREAM_METHOD = getMethodIfAvailable("setCharacterStream", String.class, Reader.class);

    @Override
    public void setCharacterStream(String parameterName, Reader reader) throws SQLException {
        invoke(SET_CHARACTER_STREAM_METHOD, parameterName, reader);
    }

    private static final Method SET_NCHARACTER_STREAM_METHOD = getMethodIfAvailable("setNCharacterStream", String.class, Reader.class);

    @Override
    public void setNCharacterStream(String parameterName, Reader value) throws SQLException {
        invoke(SET_NCHARACTER_STREAM_METHOD, parameterName, value);
    }

    private static final Method SET_CLOB_WITH_READER_METHOD = getMethodIfAvailable("setClob", String.class, Reader.class);

    @Override
    public void setClob(String parameterName, Reader reader) throws SQLException {
        invoke(SET_CLOB_WITH_READER_METHOD, parameterName, reader);
    }

    private static final Method SET_BLOB_WITH_INPUT_STREAM_METHOD = getMethodIfAvailable("setBlob", String.class, InputStream.class);

    @Override
    public void setBlob(String parameterName, InputStream inputStream) throws SQLException {
        invoke(SET_BLOB_WITH_INPUT_STREAM_METHOD, parameterName, inputStream);
    }

    private static final Method SET_NCLOB_WITH_READER_METHOD = getMethodIfAvailable("setNClob", String.class, Reader.class);

    @Override
    public void setNClob(String parameterName, Reader reader) throws SQLException {
        invoke(SET_NCLOB_WITH_READER_METHOD, parameterName, reader);
    }

    private static final Method GET_OBJECT_WITH_INT_AND_CLASS_METHOD = getMethodIfAvailable("getObject", int.class, Class.class);

    @Override
    public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
        return (T) invoke(GET_OBJECT_WITH_INT_AND_CLASS_METHOD, parameterIndex, type);
    }

    private static final Method GET_OBJECT_WITH_STRING_AND_CLASS_METHOD = getMethodIfAvailable("getObject", String.class, Class.class);

    @Override
    public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
        return (T) invoke(GET_OBJECT_WITH_STRING_AND_CLASS_METHOD, parameterName, type);
    }

    private static final Method SET_OBJECT_AND_SQL_TYPE_AND_INT_METHOD = getMethodIfAvailable("setObject", String.class, Object.class, SQLType.class, int.class);

    @Override
    public void setObject(String parameterName, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
        invoke(SET_OBJECT_AND_SQL_TYPE_AND_INT_METHOD, parameterName, x, targetSqlType, scaleOrLength);
    }

    private static final Method SET_OBJECT_AND_SQL_TYPE_AND_METHOD = getMethodIfAvailable("setObject", String.class, Object.class, SQLType.class);

    @Override
    public void setObject(String parameterName, Object x, SQLType targetSqlType) throws SQLException {
        invoke(SET_OBJECT_AND_SQL_TYPE_AND_METHOD, parameterName, x, targetSqlType);
    }

    private static final Method REGISTER_OUT_PARAMETER_WITH_INT_AND_SQL_TYPE_METHOD = getMethodIfAvailable("registerOutParameter", int.class, SQLType.class);

    @Override
    public void registerOutParameter(int parameterIndex, SQLType sqlType) throws SQLException {
        invoke(REGISTER_OUT_PARAMETER_WITH_INT_AND_SQL_TYPE_METHOD, parameterIndex, sqlType);
    }

    private static final Method REGISTER_OUT_PARAMETER_WITH_INT_AND_SQL_TYPE_AND_INT_METHOD = getMethodIfAvailable("registerOutParameter", int.class, SQLType.class, int.class);

    @Override
    public void registerOutParameter(int parameterIndex, SQLType sqlType, int scale) throws SQLException {
        invoke(REGISTER_OUT_PARAMETER_WITH_INT_AND_SQL_TYPE_AND_INT_METHOD, parameterIndex, sqlType, scale);
    }

    private static final Method REGISTER_OUT_PARAMETER_WITH_INT_AND_SQL_TYPE_AND_STRING_METHOD = getMethodIfAvailable("registerOutParameter", int.class, SQLType.class, String.class);

    @Override
    public void registerOutParameter(int parameterIndex, SQLType sqlType, String typeName) throws SQLException {
        invoke(REGISTER_OUT_PARAMETER_WITH_INT_AND_SQL_TYPE_AND_STRING_METHOD, parameterIndex, sqlType, typeName);
    }

    private static final Method REGISTER_OUT_PARAMETER_WITH_STRING_AND_SQL_TYPE_METHOD = getMethodIfAvailable("registerOutParameter", String.class, SQLType.class);

    @Override
    public void registerOutParameter(String parameterName, SQLType sqlType) throws SQLException {
        invoke(REGISTER_OUT_PARAMETER_WITH_STRING_AND_SQL_TYPE_METHOD, parameterName, sqlType);
    }

    private static final Method REGISTER_OUT_PARAMETER_WITH_STRING_AND_SQL_TYPE_AND_INT_METHOD = getMethodIfAvailable("registerOutParameter", String.class, SQLType.class, int.class);

    @Override
    public void registerOutParameter(String parameterName, SQLType sqlType, int scale) throws SQLException {
        invoke(REGISTER_OUT_PARAMETER_WITH_STRING_AND_SQL_TYPE_AND_INT_METHOD, parameterName, sqlType, scale);
    }

    private static final Method REGISTER_OUT_PARAMETER_WITH_INTSTRING_AND_SQL_TYPE_AND_STRING_METHOD = getMethodIfAvailable("registerOutParameter", String.class, SQLType.class, String.class);

    @Override
    public void registerOutParameter(String parameterName, SQLType sqlType, String typeName) throws SQLException {
        invoke(REGISTER_OUT_PARAMETER_WITH_INTSTRING_AND_SQL_TYPE_AND_STRING_METHOD, parameterName, sqlType, typeName);
    }

}
