package net.ttddyy.dsproxy.proxy.delegate;

import net.ttddyy.dsproxy.DataSourceProxyException;
import net.ttddyy.dsproxy.proxy.ProxyJdbcObject;
import net.ttddyy.dsproxy.proxy.ResultSetProxyLogic;

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
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

/**
 * A concrete class implementation of {@link ResultSet} that delegates to {@link ResultSetProxyLogic}.
 *
 * @author Tadaya Tsuyukubo
 * @see DelegatingJdbcProxyFactory
 * @since 1.5
 */
public class DelegatingResultSet implements ResultSet, ProxyJdbcObject {

    private static Method getMethodIfAvailable(String name, Class... parameterTypes) {
        return DelegatingUtils.getMethodIfAvailable(ResultSet.class, name, parameterTypes);
    }

    private ResultSetProxyLogic proxyLogic;

    public DelegatingResultSet(ResultSetProxyLogic proxyLogic) {
        this.proxyLogic = proxyLogic;
    }

    @Override
    public Object getTarget() {
        try {
            return this.proxyLogic.invoke(DelegatingUtils.GET_TARGET_METHOD, null);
        } catch (Throwable throwable) {
            throw new DataSourceProxyException("Failed to invoke method: getTarget", throwable);
        }
    }

    private Object invoke(final Method method, final Object... args) throws SQLException {
        return DelegatingUtils.invoke(method, new DelegatingUtils.InvocationCallback() {
            @Override
            public Object invoke() throws Throwable {
                return proxyLogic.invoke(method, args);
            }
        });
    }

    private static Method NEXT_METHOD = getMethodIfAvailable("next");

    @Override
    public boolean next() throws SQLException {
        return (Boolean) invoke(NEXT_METHOD);
    }

    private static Method CLOSE_METHOD = getMethodIfAvailable("close");

    @Override
    public void close() throws SQLException {
        invoke(CLOSE_METHOD);
    }

    private static Method WAS_NULL_METHOD = getMethodIfAvailable("wasNull");

    @Override
    public boolean wasNull() throws SQLException {
        return (Boolean) invoke(WAS_NULL_METHOD);
    }

    private static Method GET_STRING_WITH_INT_METHOD = getMethodIfAvailable("getString", int.class);

    @Override
    public String getString(int columnIndex) throws SQLException {
        return (String) invoke(GET_STRING_WITH_INT_METHOD, columnIndex);
    }

    private static Method GET_BOOLEAN_WITH_INT_METHOD = getMethodIfAvailable("getBoolean", int.class);

    @Override
    public boolean getBoolean(int columnIndex) throws SQLException {
        return (Boolean) invoke(GET_BOOLEAN_WITH_INT_METHOD, columnIndex);
    }

    private static Method GET_BYTE_WITH_INT_METHOD = getMethodIfAvailable("getByte", int.class);

    @Override
    public byte getByte(int columnIndex) throws SQLException {
        return (Byte) invoke(GET_BYTE_WITH_INT_METHOD, columnIndex);
    }

    private static Method GET_SHORT_WITH_INT_METHOD = getMethodIfAvailable("getShort", int.class);

    @Override
    public short getShort(int columnIndex) throws SQLException {
        return (Short) invoke(GET_SHORT_WITH_INT_METHOD, columnIndex);
    }

    private static Method GET_INT_WITH_INT_METHOD = getMethodIfAvailable("getInt", int.class);

    @Override
    public int getInt(int columnIndex) throws SQLException {
        return (Integer) invoke(GET_INT_WITH_INT_METHOD, columnIndex);
    }

    private static Method GET_LONG_WITH_INT_METHOD = getMethodIfAvailable("getLong", int.class);

    @Override
    public long getLong(int columnIndex) throws SQLException {
        return (Long) invoke(GET_LONG_WITH_INT_METHOD, columnIndex);
    }

    private static Method GET_FLOAT_WITH_INT_METHOD = getMethodIfAvailable("getFloat", int.class);

    @Override
    public float getFloat(int columnIndex) throws SQLException {
        return (Float) invoke(GET_FLOAT_WITH_INT_METHOD, columnIndex);
    }

    private static Method GET_DOUBLE_WITH_INT_METHOD = getMethodIfAvailable("getDouble", int.class);

    @Override
    public double getDouble(int columnIndex) throws SQLException {
        return (Double) invoke(GET_DOUBLE_WITH_INT_METHOD, columnIndex);
    }

    private static Method GET_BIG_DECIMAL_WITH_INT_AND_INT_METHOD = getMethodIfAvailable("getBigDecimal", int.class, int.class);

    @Override
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        return (BigDecimal) invoke(GET_BIG_DECIMAL_WITH_INT_AND_INT_METHOD, columnIndex, scale);
    }

    private static Method GET_BYTES_WITH_INT_METHOD = getMethodIfAvailable("getBytes", int.class);

    @Override
    public byte[] getBytes(int columnIndex) throws SQLException {
        return (byte[]) invoke(GET_BYTES_WITH_INT_METHOD, columnIndex);
    }

    private static Method GET_DATE_WITH_INT_METHOD = getMethodIfAvailable("getDate", int.class);

    @Override
    public Date getDate(int columnIndex) throws SQLException {
        return (Date) invoke(GET_DATE_WITH_INT_METHOD, columnIndex);
    }

    private static Method GET_TIME_WITH_INT_METHOD = getMethodIfAvailable("getTime", int.class);

    @Override
    public Time getTime(int columnIndex) throws SQLException {
        return (Time) invoke(GET_TIME_WITH_INT_METHOD, columnIndex);
    }

    private static Method GET_TIMESTAMP_WITH_INT_METHOD = getMethodIfAvailable("getTimestamp", int.class);

    @Override
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        return (Timestamp) invoke(GET_TIMESTAMP_WITH_INT_METHOD, columnIndex);
    }

    private static Method GET_ASCII_STREAM_WITH_INT_METHOD = getMethodIfAvailable("getAsciiStream", int.class);

    @Override
    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        return (InputStream) invoke(GET_ASCII_STREAM_WITH_INT_METHOD, columnIndex);
    }

    private static Method GET_UNICODE_STREAM_WITH_INT_METHOD = getMethodIfAvailable("getUnicodeStream", int.class);

    @Override
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        return (InputStream) invoke(GET_UNICODE_STREAM_WITH_INT_METHOD, columnIndex);
    }

    private static Method GET_BINARY_STREAM_WITH_INT_METHOD = getMethodIfAvailable("getBinaryStream", int.class);

    @Override
    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        return (InputStream) invoke(GET_BINARY_STREAM_WITH_INT_METHOD, columnIndex);
    }

    private static Method GET_STRING_WITH_STRING_METHOD = getMethodIfAvailable("getString", String.class);


    @Override
    public String getString(String columnLabel) throws SQLException {
        return (String) invoke(GET_STRING_WITH_STRING_METHOD, columnLabel);
    }

    private static Method GET_BOOLEAN_WITH_STRING_METHOD = getMethodIfAvailable("getBoolean", String.class);

    @Override
    public boolean getBoolean(String columnLabel) throws SQLException {
        return (Boolean) invoke(GET_BOOLEAN_WITH_STRING_METHOD, columnLabel);
    }

    private static Method GET_BYTE_WITH_STRING_METHOD = getMethodIfAvailable("getByte", String.class);

    @Override
    public byte getByte(String columnLabel) throws SQLException {
        return (Byte) invoke(GET_BYTE_WITH_STRING_METHOD, columnLabel);
    }

    private static Method GET_SHORT_WITH_STRING_METHOD = getMethodIfAvailable("getShort", String.class);

    @Override
    public short getShort(String columnLabel) throws SQLException {
        return (Short) invoke(GET_SHORT_WITH_STRING_METHOD, columnLabel);
    }

    private static Method GET_INT_WITH_STRING_METHOD = getMethodIfAvailable("getInt", String.class);

    @Override
    public int getInt(String columnLabel) throws SQLException {
        return (Integer) invoke(GET_INT_WITH_STRING_METHOD, columnLabel);
    }

    private static Method GET_LONG_WITH_STRING_METHOD = getMethodIfAvailable("getLong", String.class);


    @Override
    public long getLong(String columnLabel) throws SQLException {
        return (Long) invoke(GET_LONG_WITH_STRING_METHOD, columnLabel);
    }

    private static Method GET_FLOAT_WITH_STRING_METHOD = getMethodIfAvailable("getFloat", String.class);

    @Override
    public float getFloat(String columnLabel) throws SQLException {
        return (Float) invoke(GET_FLOAT_WITH_STRING_METHOD, columnLabel);
    }

    private static Method GET_DOUBLE_WITH_STRING_METHOD = getMethodIfAvailable("getDouble", String.class);


    @Override
    public double getDouble(String columnLabel) throws SQLException {
        return (Double) invoke(GET_DOUBLE_WITH_STRING_METHOD, columnLabel);
    }

    private static Method GET_BIG_DECIMAL_WITH_STRING_AND_INT_METHOD = getMethodIfAvailable("getBigDecimal", String.class, int.class);

    @Override
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        return (BigDecimal) invoke(GET_BIG_DECIMAL_WITH_STRING_AND_INT_METHOD, columnLabel, scale);
    }

    private static Method GET_BYTES_WITH_STRING_METHOD = getMethodIfAvailable("getBytes", String.class);

    @Override
    public byte[] getBytes(String columnLabel) throws SQLException {
        return (byte[]) invoke(GET_BYTES_WITH_STRING_METHOD, columnLabel);
    }

    private static Method GET_DATE_WITH_STRING_METHOD = getMethodIfAvailable("getDate", String.class);

    @Override
    public Date getDate(String columnLabel) throws SQLException {
        return (Date) invoke(GET_DATE_WITH_STRING_METHOD, columnLabel);
    }

    private static Method GET_TIME_WITH_STRING_METHOD = getMethodIfAvailable("getTime", String.class);

    @Override
    public Time getTime(String columnLabel) throws SQLException {
        return (Time) invoke(GET_TIME_WITH_STRING_METHOD, columnLabel);
    }

    private static Method GET_TIMESTAMP_WITH_STRING_METHOD = getMethodIfAvailable("getTimestamp", String.class);

    @Override
    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        return (Timestamp) invoke(GET_TIMESTAMP_WITH_STRING_METHOD, columnLabel);
    }

    private static Method GET_ASCII_STREAM_WITH_STRING_METHOD = getMethodIfAvailable("getAsciiStream", String.class);

    @Override
    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        return (InputStream) invoke(GET_ASCII_STREAM_WITH_STRING_METHOD, columnLabel);
    }

    private static Method GET_UNICODE_STREAM_WITH_STRING_METHOD = getMethodIfAvailable("getUnicodeStream", String.class);

    @Override
    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
        return (InputStream) invoke(GET_UNICODE_STREAM_WITH_STRING_METHOD, columnLabel);
    }

    private static Method GET_BINARY_STREAM_WITH_STRING_METHOD = getMethodIfAvailable("getBinaryStream", String.class);

    @Override
    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        return (InputStream) invoke(GET_BINARY_STREAM_WITH_STRING_METHOD, columnLabel);
    }

    private static Method GET_WARNINGS_METHOD = getMethodIfAvailable("getWarnings");

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return (SQLWarning) invoke(GET_WARNINGS_METHOD);
    }

    private static Method CLEAR_WARNINGS_METHOD = getMethodIfAvailable("clearWarnings");

    @Override
    public void clearWarnings() throws SQLException {
        invoke(CLEAR_WARNINGS_METHOD);
    }

    private static Method GET_CURSOR_NAME_METHOD = getMethodIfAvailable("getCursorName");

    @Override
    public String getCursorName() throws SQLException {
        return (String) invoke(GET_CURSOR_NAME_METHOD);
    }

    private static Method GET_META_DATA_METHOD = getMethodIfAvailable("getMetaData");

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return (ResultSetMetaData) invoke(GET_META_DATA_METHOD);
    }

    private static Method GET_OBJECT_WITH_INT_METHOD = getMethodIfAvailable("getObject", int.class);

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        return invoke(GET_OBJECT_WITH_INT_METHOD, columnIndex);
    }

    private static Method GET_OBJECT_WITH_STRING_METHOD = getMethodIfAvailable("getObject", String.class);

    @Override
    public Object getObject(String columnLabel) throws SQLException {
        return invoke(GET_OBJECT_WITH_STRING_METHOD, columnLabel);
    }

    private static Method FIND_COLUMN_METHOD = getMethodIfAvailable("findColumn", String.class);

    @Override
    public int findColumn(String columnLabel) throws SQLException {
        return (Integer) invoke(FIND_COLUMN_METHOD, columnLabel);
    }

    private static Method GET_CHARACTER_STREAM_WITH_INT_METHOD = getMethodIfAvailable("getCharacterStream", int.class);

    @Override
    public Reader getCharacterStream(int columnIndex) throws SQLException {
        return (Reader) invoke(GET_CHARACTER_STREAM_WITH_INT_METHOD, columnIndex);
    }

    private static Method GET_CHARACTER_STREAM_WITH_STRING_METHOD = getMethodIfAvailable("getCharacterStream", String.class);


    @Override
    public Reader getCharacterStream(String columnLabel) throws SQLException {
        return (Reader) invoke(GET_CHARACTER_STREAM_WITH_STRING_METHOD, columnLabel);
    }

    private static Method GET_BIG_DECIMAL_WITH_INT_METHOD = getMethodIfAvailable("getBigDecimal", int.class);

    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        return (BigDecimal) invoke(GET_BIG_DECIMAL_WITH_INT_METHOD, columnIndex);
    }

    private static Method GET_BIG_DECIMAL_WITH_STRING_METHOD = getMethodIfAvailable("getBigDecimal", String.class);

    @Override
    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        return (BigDecimal) invoke(GET_BIG_DECIMAL_WITH_STRING_METHOD, columnLabel);
    }

    private static Method IS_BEFORE_FIRST_METHOD = getMethodIfAvailable("isBeforeFirst");


    @Override
    public boolean isBeforeFirst() throws SQLException {
        return (Boolean) invoke(IS_BEFORE_FIRST_METHOD);
    }

    private static Method IS_AFTER_LAST_METHOD = getMethodIfAvailable("isAfterLast");

    @Override
    public boolean isAfterLast() throws SQLException {
        return (Boolean) invoke(IS_AFTER_LAST_METHOD);
    }

    private static Method IS_FIRST_METHOD = getMethodIfAvailable("isFirst");

    @Override
    public boolean isFirst() throws SQLException {
        return (Boolean) invoke(IS_FIRST_METHOD);
    }

    private static Method IS_LAST_METHOD = getMethodIfAvailable("isLast");

    @Override
    public boolean isLast() throws SQLException {
        return (Boolean) invoke(IS_LAST_METHOD);
    }

    private static Method BEFORE_FIRST_METHOD = getMethodIfAvailable("beforeFirst");


    @Override
    public void beforeFirst() throws SQLException {
        invoke(BEFORE_FIRST_METHOD);
    }

    private static Method AFTER_LAST_METHOD = getMethodIfAvailable("afterLast");


    @Override
    public void afterLast() throws SQLException {
        invoke(AFTER_LAST_METHOD);
    }

    private static Method FIRST_METHOD = getMethodIfAvailable("first");

    @Override
    public boolean first() throws SQLException {
        return (Boolean) invoke(FIRST_METHOD);
    }

    private static Method LAST_METHOD = getMethodIfAvailable("last");

    @Override
    public boolean last() throws SQLException {
        return (Boolean) invoke(LAST_METHOD);
    }

    private static Method GET_ROW_METHOD = getMethodIfAvailable("getRow");

    @Override
    public int getRow() throws SQLException {
        return (Integer) invoke(GET_ROW_METHOD);
    }

    private static Method ABSOLUTE_METHOD = getMethodIfAvailable("absolute", int.class);

    @Override
    public boolean absolute(int row) throws SQLException {
        return (Boolean) invoke(ABSOLUTE_METHOD, row);
    }

    private static Method RELATIVE_METHOD = getMethodIfAvailable("relative", int.class);

    @Override
    public boolean relative(int rows) throws SQLException {
        return (Boolean) invoke(RELATIVE_METHOD, rows);
    }


    private static Method PREVIOUS_METHOD = getMethodIfAvailable("previous");

    @Override
    public boolean previous() throws SQLException {
        return (Boolean) invoke(PREVIOUS_METHOD);
    }

    private static Method SET_FETCH_DIRECTION_METHOD = getMethodIfAvailable("setFetchDirection", int.class);


    @Override
    public void setFetchDirection(int direction) throws SQLException {
        invoke(SET_FETCH_DIRECTION_METHOD, direction);
    }

    private static Method GET_FETCH_DIRECTION_METHOD = getMethodIfAvailable("getFetchDirection");

    @Override
    public int getFetchDirection() throws SQLException {
        return (Integer) invoke(GET_FETCH_DIRECTION_METHOD);
    }

    private static Method SET_FETCH_SIZE_METHOD = getMethodIfAvailable("setFetchSize", int.class);

    @Override
    public void setFetchSize(int rows) throws SQLException {
        invoke(SET_FETCH_SIZE_METHOD, rows);
    }

    private static Method GET_FETCH_SIZE_METHOD = getMethodIfAvailable("getFetchSize");

    @Override
    public int getFetchSize() throws SQLException {
        return (Integer) invoke(GET_FETCH_SIZE_METHOD);
    }

    private static Method GET_TYPE_METHOD = getMethodIfAvailable("getType");

    @Override
    public int getType() throws SQLException {
        return (Integer) invoke(GET_TYPE_METHOD);
    }

    private static Method GET_CONCURRENCY_METHOD = getMethodIfAvailable("getConcurrency");

    @Override
    public int getConcurrency() throws SQLException {
        return (Integer) invoke(GET_CONCURRENCY_METHOD);
    }

    private static Method ROW_UPDATED_METHOD = getMethodIfAvailable("rowUpdated");

    @Override
    public boolean rowUpdated() throws SQLException {
        return (Boolean) invoke(ROW_UPDATED_METHOD);
    }

    private static Method ROW_INSERTED_METHOD = getMethodIfAvailable("rowInserted");

    @Override
    public boolean rowInserted() throws SQLException {
        return (Boolean) invoke(ROW_INSERTED_METHOD);
    }

    private static Method ROW_DELETED_METHOD = getMethodIfAvailable("rowDeleted");

    @Override
    public boolean rowDeleted() throws SQLException {
        return (Boolean) invoke(ROW_DELETED_METHOD);
    }

    private static Method UPDATE_NULL_WITH_INT_METHOD = getMethodIfAvailable("updateNull", int.class);

    @Override
    public void updateNull(int columnIndex) throws SQLException {
        invoke(UPDATE_NULL_WITH_INT_METHOD, columnIndex);
    }

    private static Method UPDATE_BOOLEAN_WITH_INT_METHOD = getMethodIfAvailable("updateBoolean", int.class, boolean.class);

    @Override
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        invoke(UPDATE_BOOLEAN_WITH_INT_METHOD, columnIndex, x);
    }

    private static Method UPDATE_BYTE_WITH_INT_METHOD = getMethodIfAvailable("updateByte", int.class, byte.class);

    @Override
    public void updateByte(int columnIndex, byte x) throws SQLException {
        invoke(UPDATE_BYTE_WITH_INT_METHOD, columnIndex, x);
    }

    private static Method UPDATE_SHORT_WITH_INT_METHOD = getMethodIfAvailable("updateShort", int.class, short.class);

    @Override
    public void updateShort(int columnIndex, short x) throws SQLException {
        invoke(UPDATE_SHORT_WITH_INT_METHOD, columnIndex, x);
    }

    private static Method UPDATE_INT_WITH_INT_METHOD = getMethodIfAvailable("updateInt", int.class, int.class);

    @Override
    public void updateInt(int columnIndex, int x) throws SQLException {
        invoke(UPDATE_INT_WITH_INT_METHOD, columnIndex, x);
    }

    private static Method UPDATE_LONG_WITH_INT_METHOD = getMethodIfAvailable("updateLong", int.class, long.class);

    @Override
    public void updateLong(int columnIndex, long x) throws SQLException {
        invoke(UPDATE_LONG_WITH_INT_METHOD, columnIndex, x);
    }

    private static Method UPDATE_FLOAT_WITH_INT_METHOD = getMethodIfAvailable("updateFloat", int.class, float.class);

    @Override
    public void updateFloat(int columnIndex, float x) throws SQLException {
        invoke(UPDATE_FLOAT_WITH_INT_METHOD, columnIndex, x);
    }

    private static Method UPDATE_DOUBLE_WITH_INT_METHOD = getMethodIfAvailable("updateDouble", int.class, double.class);

    @Override
    public void updateDouble(int columnIndex, double x) throws SQLException {
        invoke(UPDATE_DOUBLE_WITH_INT_METHOD, columnIndex, x);
    }

    private static Method UPDATE_BIG_DECIMAL_WITH_INT_METHOD = getMethodIfAvailable("updateBigDecimal", int.class, BigDecimal.class);

    @Override
    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        invoke(UPDATE_BIG_DECIMAL_WITH_INT_METHOD, columnIndex, x);
    }

    private static Method UPDATE_STRING_WITH_INT_METHOD = getMethodIfAvailable("updateString", int.class, String.class);

    @Override
    public void updateString(int columnIndex, String x) throws SQLException {
        invoke(UPDATE_STRING_WITH_INT_METHOD, columnIndex, x);
    }

    private static Method UPDATE_BYTES_WITH_INT_METHOD = getMethodIfAvailable("updateBytes", int.class, byte[].class);

    @Override
    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        invoke(UPDATE_BYTES_WITH_INT_METHOD, columnIndex, x);
    }

    private static Method UPDATE_DATE_WITH_INT_METHOD = getMethodIfAvailable("updateDate", int.class, Date.class);

    @Override
    public void updateDate(int columnIndex, Date x) throws SQLException {
        invoke(UPDATE_DATE_WITH_INT_METHOD, columnIndex, x);
    }

    private static Method UPDATE_TIME_WITH_INT_METHOD = getMethodIfAvailable("updateTime", int.class, Time.class);

    @Override
    public void updateTime(int columnIndex, Time x) throws SQLException {
        invoke(UPDATE_TIME_WITH_INT_METHOD, columnIndex, x);
    }

    private static Method UPDATE_TIMESTAMP_WITH_INT_METHOD = getMethodIfAvailable("updateTimestamp", int.class, Timestamp.class);

    @Override
    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        invoke(UPDATE_TIMESTAMP_WITH_INT_METHOD, columnIndex, x);
    }

    private static Method UPDATE_ASCII_STREAM_WITH_INT_AND_INT_METHOD = getMethodIfAvailable("updateAsciiStream", int.class, InputStream.class, int.class);

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        invoke(UPDATE_ASCII_STREAM_WITH_INT_AND_INT_METHOD, columnIndex, x, length);
    }

    private static Method UPDATE_BINARY_STREAM_WITH_INT_AND_INT_METHOD = getMethodIfAvailable("updateBinaryStream", int.class, InputStream.class, int.class);

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        invoke(UPDATE_BINARY_STREAM_WITH_INT_AND_INT_METHOD, columnIndex, x, length);
    }

    private static Method UPDATE_CHARACTER_STREAM_WITH_INT_AND_INT_METHOD = getMethodIfAvailable("updateCharacterStream", int.class, Reader.class, int.class);

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        invoke(UPDATE_CHARACTER_STREAM_WITH_INT_AND_INT_METHOD, columnIndex, x, length);
    }

    private static Method UPDATE_OBJECT_WITH_INT_AND_INT_METHOD = getMethodIfAvailable("updateObject", int.class, Object.class, int.class);

    @Override
    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        invoke(UPDATE_OBJECT_WITH_INT_AND_INT_METHOD, columnIndex, x, scaleOrLength);
    }

    private static Method UPDATE_OBJECT_WITH_INT_METHOD = getMethodIfAvailable("updateObject", int.class, Object.class);

    @Override
    public void updateObject(int columnIndex, Object x) throws SQLException {
        invoke(UPDATE_OBJECT_WITH_INT_METHOD, columnIndex, x);
    }

    private static Method UPDATE_NULL_WITH_STRING_METHOD = getMethodIfAvailable("updateNull", String.class);

    @Override
    public void updateNull(String columnLabel) throws SQLException {
        invoke(UPDATE_NULL_WITH_STRING_METHOD, columnLabel);
    }

    private static Method UPDATE_BOOLEAN_WITH_STRING_METHOD = getMethodIfAvailable("updateBoolean", String.class, boolean.class);

    @Override
    public void updateBoolean(String columnLabel, boolean x) throws SQLException {
        invoke(UPDATE_BOOLEAN_WITH_STRING_METHOD, columnLabel, x);
    }

    private static Method UPDATE_BYTE_WITH_STRING_METHOD = getMethodIfAvailable("updateByte", String.class, byte.class);

    @Override
    public void updateByte(String columnLabel, byte x) throws SQLException {
        invoke(UPDATE_BYTE_WITH_STRING_METHOD, columnLabel, x);
    }

    private static Method UPDATE_SHORT_WITH_STRING_METHOD = getMethodIfAvailable("updateShort", String.class, short.class);

    @Override
    public void updateShort(String columnLabel, short x) throws SQLException {
        invoke(UPDATE_SHORT_WITH_STRING_METHOD, columnLabel, x);
    }

    private static Method UPDATE_INT_WITH_STRING_METHOD = getMethodIfAvailable("updateInt", String.class, int.class);

    @Override
    public void updateInt(String columnLabel, int x) throws SQLException {
        invoke(UPDATE_INT_WITH_STRING_METHOD, columnLabel, x);
    }

    private static Method UPDATE_LONG_WITH_STRING_METHOD = getMethodIfAvailable("updateLong", String.class, long.class);

    @Override
    public void updateLong(String columnLabel, long x) throws SQLException {
        invoke(UPDATE_LONG_WITH_STRING_METHOD, columnLabel, x);
    }

    private static Method UPDATE_FLOAT_WITH_STRING_METHOD = getMethodIfAvailable("updateFloat", String.class, float.class);

    @Override
    public void updateFloat(String columnLabel, float x) throws SQLException {
        invoke(UPDATE_FLOAT_WITH_STRING_METHOD, columnLabel, x);
    }

    private static Method UPDATE_DOUBLE_WITH_STRING_METHOD = getMethodIfAvailable("updateDouble", String.class, double.class);

    @Override
    public void updateDouble(String columnLabel, double x) throws SQLException {
        invoke(UPDATE_DOUBLE_WITH_STRING_METHOD, columnLabel, x);
    }

    private static Method UPDATE_BIG_DECIMAL_WITH_STRING_METHOD = getMethodIfAvailable("updateBigDecimal", String.class, BigDecimal.class);

    @Override
    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
        invoke(UPDATE_BIG_DECIMAL_WITH_STRING_METHOD, columnLabel, x);
    }

    private static Method UPDATE_STRING_WITH_STRING_METHOD = getMethodIfAvailable("updateString", String.class, String.class);

    @Override
    public void updateString(String columnLabel, String x) throws SQLException {
        invoke(UPDATE_STRING_WITH_STRING_METHOD, columnLabel, x);
    }

    private static Method UPDATE_BYTES_WITH_STRING_METHOD = getMethodIfAvailable("updateBytes", String.class, byte[].class);

    @Override
    public void updateBytes(String columnLabel, byte[] x) throws SQLException {
        invoke(UPDATE_BYTES_WITH_STRING_METHOD, columnLabel, x);
    }

    private static Method UPDATE_DATE_WITH_STRING_METHOD = getMethodIfAvailable("updateDate", String.class, Date.class);

    @Override
    public void updateDate(String columnLabel, Date x) throws SQLException {
        invoke(UPDATE_DATE_WITH_STRING_METHOD, columnLabel, x);
    }

    private static Method UPDATE_TIME_WITH_STRING_METHOD = getMethodIfAvailable("updateTime", String.class, Time.class);

    @Override
    public void updateTime(String columnLabel, Time x) throws SQLException {
        invoke(UPDATE_TIME_WITH_STRING_METHOD, columnLabel, x);
    }

    private static Method UPDATE_TIMESTAMP_WITH_STRING_METHOD = getMethodIfAvailable("updateTimestamp", String.class, Timestamp.class);

    @Override
    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
        invoke(UPDATE_TIMESTAMP_WITH_STRING_METHOD, columnLabel, x);
    }

    private static Method UPDATE_ASCII_STREAM_WITH_STRING_AND_INT_METHOD = getMethodIfAvailable("updateAsciiStream", String.class, InputStream.class, int.class);


    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
        invoke(UPDATE_ASCII_STREAM_WITH_STRING_AND_INT_METHOD, columnLabel, x, length);
    }

    private static Method UPDATE_BINARY_STREAM_WITH_STRING_AND_INT_METHOD = getMethodIfAvailable("updateBinaryStream", String.class, InputStream.class, int.class);


    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
        invoke(UPDATE_BINARY_STREAM_WITH_STRING_AND_INT_METHOD, columnLabel, x, length);
    }

    private static Method UPDATE_CHARACTER_STREAM_WITH_STRING_AND_INT_METHOD = getMethodIfAvailable("updateCharacterStream", String.class, Reader.class, int.class);

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
        invoke(UPDATE_CHARACTER_STREAM_WITH_STRING_AND_INT_METHOD, columnLabel, reader, length);
    }

    private static Method UPDATE_OBJECT_WITH_STRING_AND_INT_METHOD = getMethodIfAvailable("updateObject", String.class, Object.class, int.class);

    @Override
    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        invoke(UPDATE_OBJECT_WITH_STRING_AND_INT_METHOD, columnLabel, x, scaleOrLength);
    }

    private static Method UPDATE_OBJECT_WITH_STRING_METHOD = getMethodIfAvailable("updateObject", String.class, Object.class);

    @Override
    public void updateObject(String columnLabel, Object x) throws SQLException {
        invoke(UPDATE_OBJECT_WITH_STRING_METHOD, columnLabel, x);
    }

    private static Method INSERT_ROW_METHOD = getMethodIfAvailable("insertRow");

    @Override
    public void insertRow() throws SQLException {
        invoke(INSERT_ROW_METHOD);
    }

    private static Method UPDATE_ROW_METHOD = getMethodIfAvailable("updateRow");

    @Override
    public void updateRow() throws SQLException {
        invoke(UPDATE_ROW_METHOD);
    }

    private static Method DELETE_ROW_METHOD = getMethodIfAvailable("deleteRow");

    @Override
    public void deleteRow() throws SQLException {
        invoke(DELETE_ROW_METHOD);
    }

    private static Method REFRESH_ROW_METHOD = getMethodIfAvailable("refreshRow");

    @Override
    public void refreshRow() throws SQLException {
        invoke(REFRESH_ROW_METHOD);
    }

    private static Method CANCEL_ROW_UPDATES_METHOD = getMethodIfAvailable("cancelRowUpdates");

    @Override
    public void cancelRowUpdates() throws SQLException {
        invoke(CANCEL_ROW_UPDATES_METHOD);
    }

    private static Method MOVE_TO_INSERT_ROW_METHOD = getMethodIfAvailable("moveToInsertRow");

    @Override
    public void moveToInsertRow() throws SQLException {
        invoke(MOVE_TO_INSERT_ROW_METHOD);
    }

    private static Method MOVE_TO_CURRENT_ROW_METHOD = getMethodIfAvailable("moveToCurrentRow");

    @Override
    public void moveToCurrentRow() throws SQLException {
        invoke(MOVE_TO_CURRENT_ROW_METHOD);
    }

    private static Method GET_STATEMENT_METHOD = getMethodIfAvailable("getStatement");

    @Override
    public Statement getStatement() throws SQLException {
        return (Statement) invoke(GET_STATEMENT_METHOD);
    }

    private static Method GET_OBJECT_WITH_INT_AND_MAP_METHOD = getMethodIfAvailable("getObject", int.class, Map.class);

    @Override
    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        return invoke(GET_OBJECT_WITH_INT_AND_MAP_METHOD, columnIndex, map);
    }

    private static Method GET_REF_WITH_INT_METHOD = getMethodIfAvailable("getRef", int.class);

    @Override
    public Ref getRef(int columnIndex) throws SQLException {
        return (Ref) invoke(GET_REF_WITH_INT_METHOD, columnIndex);
    }

    private static Method GET_BLOB_WITH_INT_METHOD = getMethodIfAvailable("getBlob", int.class);

    @Override
    public Blob getBlob(int columnIndex) throws SQLException {
        return (Blob) invoke(GET_BLOB_WITH_INT_METHOD, columnIndex);
    }

    private static Method GET_CLOB_WITH_INT_METHOD = getMethodIfAvailable("getClob", int.class);

    @Override
    public Clob getClob(int columnIndex) throws SQLException {
        return (Clob) invoke(GET_CLOB_WITH_INT_METHOD, columnIndex);
    }

    private static Method GET_ARRAY_WITH_INT_METHOD = getMethodIfAvailable("getArray", int.class);

    @Override
    public Array getArray(int columnIndex) throws SQLException {
        return (Array) invoke(GET_ARRAY_WITH_INT_METHOD, columnIndex);
    }

    private static Method GET_OBJECT_WITH_STRING_AND_MAP_METHOD = getMethodIfAvailable("getObject", String.class, Map.class);

    @Override
    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        return invoke(GET_OBJECT_WITH_STRING_AND_MAP_METHOD, columnLabel, map);
    }

    private static Method GET_REF_WITH_STRING_METHOD = getMethodIfAvailable("getRef", String.class);

    @Override
    public Ref getRef(String columnLabel) throws SQLException {
        return (Ref) invoke(GET_REF_WITH_STRING_METHOD, columnLabel);
    }

    private static Method GET_BLOB_WITH_STRING_METHOD = getMethodIfAvailable("getBlob", String.class);

    @Override
    public Blob getBlob(String columnLabel) throws SQLException {
        return (Blob) invoke(GET_BLOB_WITH_STRING_METHOD, columnLabel);
    }

    private static Method GET_CLOB_WITH_STRING_METHOD = getMethodIfAvailable("getClob", String.class);


    @Override
    public Clob getClob(String columnLabel) throws SQLException {
        return (Clob) invoke(GET_CLOB_WITH_STRING_METHOD, columnLabel);
    }

    private static Method GET_ARRAY_WITH_STRING_METHOD = getMethodIfAvailable("getArray", String.class);

    @Override
    public Array getArray(String columnLabel) throws SQLException {
        return (Array) invoke(GET_ARRAY_WITH_STRING_METHOD, columnLabel);
    }

    private static Method GET_DATE_WITH_INT_AND_CALENDAR_METHOD = getMethodIfAvailable("getDate", int.class, Calendar.class);

    @Override
    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        return (Date) invoke(GET_DATE_WITH_INT_AND_CALENDAR_METHOD, columnIndex, cal);
    }

    private static Method GET_DATE_WITH_STRING_AND_CALENDAR_METHOD = getMethodIfAvailable("getDate", String.class, Calendar.class);

    @Override
    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        return (Date) invoke(GET_DATE_WITH_STRING_AND_CALENDAR_METHOD, columnLabel, cal);
    }

    private static Method GET_TIME_WITH_INT_AND_CALENDAR_METHOD = getMethodIfAvailable("getTime", int.class, Calendar.class);

    @Override
    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        return (Time) invoke(GET_TIME_WITH_INT_AND_CALENDAR_METHOD, columnIndex, cal);
    }

    private static Method GET_TIME_WITH_STRING_AND_CALENDAR_METHOD = getMethodIfAvailable("getTime", String.class, Calendar.class);

    @Override
    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        return (Time) invoke(GET_TIME_WITH_STRING_AND_CALENDAR_METHOD, columnLabel, cal);
    }

    private static Method GET_TIMESTAMP_WITH_INT_AND_CALENDAR_METHOD = getMethodIfAvailable("getTimestamp", int.class, Calendar.class);

    @Override
    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        return (Timestamp) invoke(GET_TIMESTAMP_WITH_INT_AND_CALENDAR_METHOD, columnIndex, cal);
    }

    private static Method GET_TIMESTAMP_WITH_STRING_AND_CALENDAR_METHOD = getMethodIfAvailable("getTimestamp", String.class, Calendar.class);

    @Override
    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        return (Timestamp) invoke(GET_TIMESTAMP_WITH_STRING_AND_CALENDAR_METHOD, columnLabel, cal);
    }

    private static Method GET_URL_WITH_INT_METHOD = getMethodIfAvailable("getURL", int.class);


    @Override
    public URL getURL(int columnIndex) throws SQLException {
        return (URL) invoke(GET_URL_WITH_INT_METHOD, columnIndex);
    }

    private static Method GET_URL_WITH_STRING_METHOD = getMethodIfAvailable("getURL", String.class);

    @Override
    public URL getURL(String columnLabel) throws SQLException {
        return (URL) invoke(GET_URL_WITH_STRING_METHOD, columnLabel);
    }

    private static Method UPDATE_REF_WITH_INT_METHOD = getMethodIfAvailable("updateRef", int.class, Ref.class);

    @Override
    public void updateRef(int columnIndex, Ref x) throws SQLException {
        invoke(UPDATE_REF_WITH_INT_METHOD, columnIndex, x);

    }

    private static Method UPDATE_REF_WITH_STRING_METHOD = getMethodIfAvailable("updateRef", String.class, Ref.class);

    @Override
    public void updateRef(String columnLabel, Ref x) throws SQLException {
        invoke(UPDATE_REF_WITH_STRING_METHOD, columnLabel, x);
    }

    private static Method UPDATE_BLOB_WITH_INT_METHOD = getMethodIfAvailable("updateBlob", int.class, Blob.class);

    @Override
    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        invoke(UPDATE_BLOB_WITH_INT_METHOD, columnIndex, x);
    }

    private static Method UPDATE_BLOB_WITH_STRING_METHOD = getMethodIfAvailable("updateBlob", String.class, Blob.class);

    @Override
    public void updateBlob(String columnLabel, Blob x) throws SQLException {
        invoke(UPDATE_BLOB_WITH_STRING_METHOD, columnLabel, x);
    }

    private static Method UPDATE_CLOB_WITH_INT_METHOD = getMethodIfAvailable("updateClob", int.class, Clob.class);

    @Override
    public void updateClob(int columnIndex, Clob x) throws SQLException {
        invoke(UPDATE_CLOB_WITH_INT_METHOD, columnIndex, x);
    }

    private static Method UPDATE_CLOB_WITH_STRING_METHOD = getMethodIfAvailable("updateClob", String.class, Clob.class);

    @Override
    public void updateClob(String columnLabel, Clob x) throws SQLException {
        invoke(UPDATE_CLOB_WITH_STRING_METHOD, columnLabel, x);
    }

    private static Method UPDATE_ARRAY_WITH_INT_METHOD = getMethodIfAvailable("updateArray", int.class, Array.class);

    @Override
    public void updateArray(int columnIndex, Array x) throws SQLException {
        invoke(UPDATE_ARRAY_WITH_INT_METHOD, columnIndex, x);
    }

    private static Method UPDATE_ARRAY_WITH_STRING_METHOD = getMethodIfAvailable("updateArray", String.class, Array.class);

    @Override
    public void updateArray(String columnLabel, Array x) throws SQLException {
        invoke(UPDATE_ARRAY_WITH_STRING_METHOD, columnLabel, x);
    }

    private static Method GET_ROW_ID_WITH_INT_METHOD = getMethodIfAvailable("getRowId", int.class);

    @Override
    public RowId getRowId(int columnIndex) throws SQLException {
        return (RowId) invoke(GET_ROW_ID_WITH_INT_METHOD, columnIndex);
    }

    private static Method GET_ROW_ID_WITH_STRING_METHOD = getMethodIfAvailable("getRowId", String.class);

    @Override
    public RowId getRowId(String columnLabel) throws SQLException {
        return (RowId) invoke(GET_ROW_ID_WITH_STRING_METHOD, columnLabel);
    }

    private static Method GET_UPDATE_ROW_ID_WITH_INT_METHOD = getMethodIfAvailable("updateRowId", int.class, RowId.class);

    @Override
    public void updateRowId(int columnIndex, RowId x) throws SQLException {
        invoke(GET_UPDATE_ROW_ID_WITH_INT_METHOD, columnIndex, x);
    }

    private static Method GET_UPDATE_ROW_ID_WITH_STRING_METHOD = getMethodIfAvailable("updateRowId", String.class, RowId.class);

    @Override
    public void updateRowId(String columnLabel, RowId x) throws SQLException {
        invoke(GET_UPDATE_ROW_ID_WITH_STRING_METHOD, columnLabel, x);
    }

    private static Method GET_HOLDABILITY_METHOD = getMethodIfAvailable("getHoldability");

    @Override
    public int getHoldability() throws SQLException {
        return (Integer) invoke(GET_HOLDABILITY_METHOD);
    }

    private static Method IS_CLOSED_METHOD = getMethodIfAvailable("isClosed");

    @Override
    public boolean isClosed() throws SQLException {
        return (Boolean) invoke(IS_CLOSED_METHOD);
    }

    private static Method UPDATE_NSTRING_WITH_INT_METHOD = getMethodIfAvailable("updateNString", int.class, String.class);

    @Override
    public void updateNString(int columnIndex, String nString) throws SQLException {
        invoke(UPDATE_NSTRING_WITH_INT_METHOD, columnIndex, nString);
    }

    private static Method UPDATE_NSTRING_WITH_STRING_METHOD = getMethodIfAvailable("updateNString", String.class, String.class);

    @Override
    public void updateNString(String columnLabel, String nString) throws SQLException {
        invoke(UPDATE_NSTRING_WITH_STRING_METHOD, columnLabel, nString);
    }

    private static Method UPDATE_NCLOB_WITH_INT_METHOD = getMethodIfAvailable("updateNClob", int.class, NClob.class);

    @Override
    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        invoke(UPDATE_NCLOB_WITH_INT_METHOD, columnIndex, nClob);
    }

    private static Method UPDATE_NCLOB_WITH_STRING_METHOD = getMethodIfAvailable("updateNClob", String.class, NClob.class);

    @Override
    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
        invoke(UPDATE_NCLOB_WITH_STRING_METHOD, columnLabel, nClob);
    }

    private static Method GET_NCLOB_WITH_INT_METHOD = getMethodIfAvailable("getNClob", int.class);

    @Override
    public NClob getNClob(int columnIndex) throws SQLException {
        return (NClob) invoke(GET_NCLOB_WITH_INT_METHOD, columnIndex);
    }

    private static Method GET_NCLOB_WITH_STRING_METHOD = getMethodIfAvailable("getNClob", String.class);

    @Override
    public NClob getNClob(String columnLabel) throws SQLException {
        return (NClob) invoke(GET_NCLOB_WITH_STRING_METHOD, columnLabel);
    }

    private static Method GET_SQLXML_WITH_INT_METHOD = getMethodIfAvailable("getSQLXML", int.class);

    @Override
    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        return (SQLXML) invoke(GET_SQLXML_WITH_INT_METHOD, columnIndex);
    }

    private static Method GET_SQLXML_WITH_STRING_METHOD = getMethodIfAvailable("getSQLXML", String.class);

    @Override
    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        return (SQLXML) invoke(GET_SQLXML_WITH_STRING_METHOD, columnLabel);
    }

    private static Method UPDATE_SQLXML_WITH_INT_METHOD = getMethodIfAvailable("updateSQLXML", int.class, SQLXML.class);

    @Override
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        invoke(UPDATE_SQLXML_WITH_INT_METHOD, columnIndex, xmlObject);
    }

    private static Method UPDATE_SQLXML_WITH_STRING_METHOD = getMethodIfAvailable("updateSQLXML", String.class, SQLXML.class);

    @Override
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        invoke(UPDATE_SQLXML_WITH_STRING_METHOD, columnLabel, xmlObject);
    }

    private static Method GET_NSTRING_WITH_INT_METHOD = getMethodIfAvailable("getNString", int.class);

    @Override
    public String getNString(int columnIndex) throws SQLException {
        return (String) invoke(GET_NSTRING_WITH_INT_METHOD, columnIndex);
    }

    private static Method GET_NSTRING_WITH_STRING_METHOD = getMethodIfAvailable("getNString", String.class);

    @Override
    public String getNString(String columnLabel) throws SQLException {
        return (String) invoke(GET_NSTRING_WITH_STRING_METHOD, columnLabel);
    }

    private static Method GET_NCHARACTER_STREAM_WITH_INT_METHOD = getMethodIfAvailable("getNCharacterStream", int.class);

    @Override
    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        return (Reader) invoke(GET_NCHARACTER_STREAM_WITH_INT_METHOD, columnIndex);
    }

    private static Method GET_NCHARACTER_STREAM_WITH_STRING_METHOD = getMethodIfAvailable("getNCharacterStream", String.class);

    @Override
    public Reader getNCharacterStream(String columnLabel) throws SQLException {
        return (Reader) invoke(GET_NCHARACTER_STREAM_WITH_STRING_METHOD, columnLabel);
    }

    private static Method UPDATE_NCHRACTER_STREAM_WITH_INT_AND_LONG_METHOD = getMethodIfAvailable("updateNCharacterStream", int.class, Reader.class, long.class);

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        invoke(UPDATE_NCHRACTER_STREAM_WITH_INT_AND_LONG_METHOD, columnIndex, x, length);
    }

    private static Method UPDATE_NCHRACTER_STREAM_WITH_STRING_AND_LONG_METHOD = getMethodIfAvailable("updateNCharacterStream", String.class, Reader.class, long.class);


    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        invoke(UPDATE_NCHRACTER_STREAM_WITH_STRING_AND_LONG_METHOD, columnLabel, reader, length);
    }

    private static Method UPDATE_ASCII_STREAM_WITH_INT_AND_LONG_METHOD = getMethodIfAvailable("updateAsciiStream", int.class, InputStream.class, long.class);

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
        invoke(UPDATE_ASCII_STREAM_WITH_INT_AND_LONG_METHOD, columnIndex, x, length);
    }

    private static Method UPDATE_BINRARY_STREAM_WITH_INT_AND_LONG_METHOD = getMethodIfAvailable("updateBinaryStream", int.class, InputStream.class, long.class);

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
        invoke(UPDATE_BINRARY_STREAM_WITH_INT_AND_LONG_METHOD, columnIndex, x, length);
    }

    private static Method UPDATE_CHRACTER_STREAM_WITH_INT_AND_LONG_METHOD = getMethodIfAvailable("updateCharacterStream", int.class, Reader.class, long.class);

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        invoke(UPDATE_CHRACTER_STREAM_WITH_INT_AND_LONG_METHOD, columnIndex, x, length);
    }

    private static Method UPDATE_ASCII_STREAM_WITH_STRING_AND_LONG_METHOD = getMethodIfAvailable("updateAsciiStream", String.class, InputStream.class, long.class);

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
        invoke(UPDATE_ASCII_STREAM_WITH_STRING_AND_LONG_METHOD, columnLabel, x, length);
    }

    private static Method UPDATE_BINARY_STREAM_WITH_STRING_AND_LONG_METHOD = getMethodIfAvailable("updateBinaryStream", String.class, InputStream.class, long.class);

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
        invoke(UPDATE_BINARY_STREAM_WITH_STRING_AND_LONG_METHOD, columnLabel, x, length);
    }

    private static Method UPDATE_CHRACTER_STREAM_WITH_STRING_AND_LONG_METHOD = getMethodIfAvailable("updateCharacterStream", String.class, Reader.class, long.class);

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        invoke(UPDATE_CHRACTER_STREAM_WITH_STRING_AND_LONG_METHOD, columnLabel, reader, length);
    }

    private static Method UPDATE_BLOB_WITH_INT_AND_LONG_METHOD = getMethodIfAvailable("updateBlob", int.class, InputStream.class, long.class);


    @Override
    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
        invoke(UPDATE_BLOB_WITH_INT_AND_LONG_METHOD, columnIndex, inputStream, length);
    }

    private static Method UPDATE_BLOB_WITH_STRING_AND_LONG_METHOD = getMethodIfAvailable("updateBlob", String.class, InputStream.class, long.class);

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
        invoke(UPDATE_BLOB_WITH_STRING_AND_LONG_METHOD, columnLabel, inputStream, length);
    }

    private static Method UPDATE_CLOB_WITH_INT_AND_LONG_METHOD = getMethodIfAvailable("updateClob", int.class, Reader.class, long.class);

    @Override
    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        invoke(UPDATE_CLOB_WITH_INT_AND_LONG_METHOD, columnIndex, reader, length);
    }

    private static Method UPDATE_CLOB_WITH_STRING_AND_LONG_METHOD = getMethodIfAvailable("updateClob", String.class, Reader.class, long.class);

    @Override
    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        invoke(UPDATE_CLOB_WITH_STRING_AND_LONG_METHOD, columnLabel, reader, length);
    }

    private static Method UPDATE_NCLOB_WITH_INT_AND_LONG_METHOD = getMethodIfAvailable("updateNClob", int.class, Reader.class, long.class);

    @Override
    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        invoke(UPDATE_NCLOB_WITH_INT_AND_LONG_METHOD, columnIndex, reader, length);
    }

    private static Method UPDATE_NCLOB_WITH_STRING_AND_LONG_METHOD = getMethodIfAvailable("updateNClob", String.class, Reader.class, long.class);

    @Override
    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        invoke(UPDATE_NCLOB_WITH_STRING_AND_LONG_METHOD, columnLabel, reader, length);
    }

    private static Method UPDATE_NCHARACTER_STREAM_WITH_INT_METHOD = getMethodIfAvailable("updateNCharacterStream", int.class, Reader.class);

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
        invoke(UPDATE_NCHARACTER_STREAM_WITH_INT_METHOD, columnIndex, x);
    }

    private static Method UPDATE_NCHARACTER_STREAM_WITH_STRING_METHOD = getMethodIfAvailable("updateNCharacterStream", String.class, Reader.class);

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
        invoke(UPDATE_NCHARACTER_STREAM_WITH_STRING_METHOD, columnLabel, reader);
    }

    private static Method UPDATE_ASCII_STREAM_WITH_INT_METHOD = getMethodIfAvailable("updateAsciiStream", int.class, InputStream.class);

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        invoke(UPDATE_ASCII_STREAM_WITH_INT_METHOD, columnIndex, x);
    }

    private static Method UPDATE_BINARY_STREAM_WITH_INT_METHOD = getMethodIfAvailable("updateBinaryStream", int.class, InputStream.class);

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        invoke(UPDATE_BINARY_STREAM_WITH_INT_METHOD, columnIndex, x);
    }

    private static Method UPDATE_CHARACTER_STREAM_WITH_INT_METHOD = getMethodIfAvailable("updateCharacterStream", int.class, Reader.class);

    @Override
    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
        invoke(UPDATE_CHARACTER_STREAM_WITH_INT_METHOD, columnIndex, x);
    }

    private static Method UPDATE_ASCII_STREAM_WITH_STRING_METHOD = getMethodIfAvailable("updateAsciiStream", String.class, InputStream.class);

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        invoke(UPDATE_ASCII_STREAM_WITH_STRING_METHOD, columnLabel, x);
    }

    private static Method UPDATE_BINARY_STREAM_WITH_STRING_METHOD = getMethodIfAvailable("updateBinaryStream", String.class, InputStream.class);

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        invoke(UPDATE_BINARY_STREAM_WITH_STRING_METHOD, columnLabel, x);
    }

    private static Method UPDATE_CHARACTER_STREAM_WITH_STRING_METHOD = getMethodIfAvailable("updateCharacterStream", String.class, Reader.class);

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
        invoke(UPDATE_CHARACTER_STREAM_WITH_STRING_METHOD, columnLabel, reader);
    }

    private static Method UPDATE_BLOB_WITH_INT_AND_INPUT_STREAM_METHOD = getMethodIfAvailable("updateBlob", int.class, InputStream.class);


    @Override
    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        invoke(UPDATE_BLOB_WITH_INT_AND_INPUT_STREAM_METHOD, columnIndex, inputStream);
    }

    private static Method UPDATE_BLOB_WITH_STRING_AND_INPUT_STREAM_METHOD = getMethodIfAvailable("updateBlob", String.class, InputStream.class);

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        invoke(UPDATE_BLOB_WITH_STRING_AND_INPUT_STREAM_METHOD, columnLabel, inputStream);
    }

    private static Method UPDATE_CLOB_WITH_INT_AND_INPUT_STREAM_METHOD = getMethodIfAvailable("updateClob", int.class, Reader.class);

    @Override
    public void updateClob(int columnIndex, Reader reader) throws SQLException {
        invoke(UPDATE_CLOB_WITH_INT_AND_INPUT_STREAM_METHOD, columnIndex, reader);
    }

    private static Method UPDATE_CLOB_WITH_STRING_AND_INPUT_STREAM_METHOD = getMethodIfAvailable("updateClob", String.class, Reader.class);

    @Override
    public void updateClob(String columnLabel, Reader reader) throws SQLException {
        invoke(UPDATE_CLOB_WITH_STRING_AND_INPUT_STREAM_METHOD, columnLabel, reader);
    }

    private static Method UPDATE_NCLOB_WITH_INT_AND_INPUT_STREAM_METHOD = getMethodIfAvailable("updateNClob", int.class, Reader.class);

    @Override
    public void updateNClob(int columnIndex, Reader reader) throws SQLException {
        invoke(UPDATE_NCLOB_WITH_INT_AND_INPUT_STREAM_METHOD, columnIndex, reader);
    }

    private static Method UPDATE_NCLOB_WITH_STRING_AND_INPUT_STREAM_METHOD = getMethodIfAvailable("updateNClob", String.class, Reader.class);

    @Override
    public void updateNClob(String columnLabel, Reader reader) throws SQLException {
        invoke(UPDATE_NCLOB_WITH_STRING_AND_INPUT_STREAM_METHOD, columnLabel, reader);
    }

    private static Method GET_OBJECT_WITH_INT_AND_CLASS_METHOD = getMethodIfAvailable("getObject", int.class, Class.class);

    @Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        return (T) invoke(GET_OBJECT_WITH_INT_AND_CLASS_METHOD, columnIndex, type);
    }

    private static Method GET_OBJECT_WITH_STRING_AND_CLASS_METHOD = getMethodIfAvailable("getObject", String.class, Class.class);

    @Override
    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        return (T) invoke(GET_OBJECT_WITH_STRING_AND_CLASS_METHOD, columnLabel, type);
    }

    private static Method UNWRAP_METHOD = getMethodIfAvailable("unwrap", Class.class);

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return (T) invoke(UNWRAP_METHOD, iface);
    }

    private static Method IS_WRAPPER_FOR_METHOD = getMethodIfAvailable("isWrapperFor", Class.class);

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return (Boolean) invoke(IS_WRAPPER_FOR_METHOD, iface);
    }

    private static Method UPDATE_OBJECT_WITH_INT_AND_SQL_TYPE_AND_INT_METHOD = getMethodIfAvailable("updateObject", int.class, Object.class, SQLType.class, int.class);

    @Override
    public void updateObject(int columnIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
        invoke(UPDATE_OBJECT_WITH_INT_AND_SQL_TYPE_AND_INT_METHOD, columnIndex, x, targetSqlType, scaleOrLength);
    }

    private static Method UPDATE_OBJECT_WITH_STRING_AND_SQL_TYPE_AND_INT_METHOD = getMethodIfAvailable("updateObject", String.class, Object.class, SQLType.class, int.class);

    @Override
    public void updateObject(String columnLabel, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
        invoke(UPDATE_OBJECT_WITH_STRING_AND_SQL_TYPE_AND_INT_METHOD, columnLabel, x, targetSqlType, scaleOrLength);
    }

    private static Method UPDATE_OBJECT_WITH_INT_AND_SQL_TYPE_METHOD = getMethodIfAvailable("updateObject", int.class, Object.class, SQLType.class);

    @Override
    public void updateObject(int columnIndex, Object x, SQLType targetSqlType) throws SQLException {
        invoke(UPDATE_OBJECT_WITH_INT_AND_SQL_TYPE_METHOD, columnIndex, x, targetSqlType);
    }

    private static Method UPDATE_OBJECT_WITH_STRING_AND_SQL_TYPE_METHOD = getMethodIfAvailable("updateObject", String.class, Object.class, SQLType.class);

    @Override
    public void updateObject(String columnLabel, Object x, SQLType targetSqlType) throws SQLException {
        invoke(UPDATE_OBJECT_WITH_STRING_AND_SQL_TYPE_METHOD, columnLabel, x, targetSqlType);
    }

}
