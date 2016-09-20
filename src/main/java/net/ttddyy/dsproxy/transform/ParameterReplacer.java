package net.ttddyy.dsproxy.transform;


import net.ttddyy.dsproxy.proxy.ParameterKey;
import net.ttddyy.dsproxy.proxy.ParameterSetOperation;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.2
 */
public class ParameterReplacer {

    private Map<ParameterKey, ParameterSetOperation> parameters = new LinkedHashMap<ParameterKey, ParameterSetOperation>();
    private boolean modified = false;

    public ParameterReplacer() {
    }

    public ParameterReplacer(Map<ParameterKey, ParameterSetOperation> parameters) {
        // make a copy
        this.parameters.putAll(parameters);
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(int index) {
        ParameterKey parameterKey = new ParameterKey(index);
        return (T) this.parameters.get(parameterKey).getArgs()[1];  // index 1 in arguments is always value
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(String paramName) {
        ParameterKey parameterKey = new ParameterKey(paramName);
        return (T) this.parameters.get(parameterKey).getArgs()[1];  // index 1 in arguments is always value
    }

    public void clearParameters() {
        this.parameters.clear();
        modified = true;
    }

    private Method getDeclaredMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            return clazz.getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private void record(int parameterIndex, Method paramMethod, Object... args) {
        ParameterKey parameterKey = new ParameterKey(parameterIndex);
        this.parameters.put(parameterKey, new ParameterSetOperation(paramMethod, args));
        modified = true;
    }

    private void recordByName(String parameterName, Method paramMethod, Object... args) {
        ParameterKey parameterKey = new ParameterKey(parameterName);
        this.parameters.put(parameterKey, new ParameterSetOperation(paramMethod, args));
        modified = true;
    }

    public boolean isModified() {
        return modified;
    }

    public Map<ParameterKey, ParameterSetOperation> getModifiedParameters() {
        return this.parameters;
    }


    //////  Parameter set operations for PreparedStatement

    public void setNull(int parameterIndex, int sqlType) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setNull", int.class, int.class), parameterIndex, sqlType);
    }

    public void setBoolean(int parameterIndex, boolean x) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setBoolean", int.class, boolean.class), parameterIndex, x);
    }

    public void setByte(int parameterIndex, byte x) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setByte", int.class, byte.class), parameterIndex, x);
    }

    public void setShort(int parameterIndex, short x) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setShort", int.class, short.class), parameterIndex, x);
    }

    public void setInt(int parameterIndex, int x) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setInt", int.class, int.class), parameterIndex, x);
    }

    public void setLong(int parameterIndex, long x) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setLong", int.class, long.class), parameterIndex, x);
    }

    public void setFloat(int parameterIndex, float x) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setFloat", int.class, float.class), parameterIndex, x);
    }

    public void setDouble(int parameterIndex, double x) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setDouble", int.class, double.class), parameterIndex, x);
    }

    public void setBigDecimal(int parameterIndex, BigDecimal x) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setBigDecimal", int.class, BigDecimal.class), parameterIndex, x);
    }

    public void setString(int parameterIndex, String x) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setString", int.class, String.class), parameterIndex, x);
    }

    public void setBytes(int parameterIndex, byte x[]) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setBytes", int.class, byte[].class), parameterIndex, x);
    }

    public void setDate(int parameterIndex, java.sql.Date x) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setDate", int.class, Date.class), parameterIndex, x);
    }

    public void setTime(int parameterIndex, java.sql.Time x) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setTime", int.class, Time.class), parameterIndex, x);
    }

    public void setTimestamp(int parameterIndex, java.sql.Timestamp x) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setTimestamp", int.class, Timestamp.class), parameterIndex, x);
    }

    public void setAsciiStream(int parameterIndex, java.io.InputStream x, int length) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setAsciiStream", int.class, InputStream.class, int.class), parameterIndex, x, length);
    }

    public void setUnicodeStream(int parameterIndex, java.io.InputStream x, int length) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setUnicodeStream", int.class, InputStream.class, int.class), parameterIndex, x, length);
    }

    public void setBinaryStream(int parameterIndex, java.io.InputStream x, int length) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setBinaryStream", int.class, InputStream.class, int.class), parameterIndex, x, length);
    }

    public void setObject(int parameterIndex, Object x, int targetSqlType) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setObject", int.class, Object.class, int.class, int.class), parameterIndex, x, targetSqlType);
    }

    public void setObject(int parameterIndex, Object x) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setObject", int.class, Object.class), parameterIndex, x);
    }

    //--------------------------JDBC 2.0-----------------------------
    public void setCharacterStream(int parameterIndex, java.io.Reader reader, int length) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setCharacterStream", int.class, Reader.class, int.class), parameterIndex, reader, length);
    }

    public void setRef(int parameterIndex, Ref x) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setRef", int.class, Ref.class), parameterIndex, x);
    }

    public void setBlob(int parameterIndex, Blob x) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setBlob", int.class, Blob.class), parameterIndex, x);
    }

    public void setClob(int parameterIndex, Clob x) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setClob", int.class, Clob.class), parameterIndex, x);
    }

    public void setArray(int parameterIndex, Array x) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setArray", int.class, Array.class), parameterIndex, x);
    }

    public void setDate(int parameterIndex, java.sql.Date x, Calendar cal) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setDate", int.class, Date.class, Calendar.class), parameterIndex, x, cal);
    }

    public void setTime(int parameterIndex, java.sql.Time x, Calendar cal) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setTime", int.class, Time.class, Calendar.class), parameterIndex, x, cal);
    }

    public void setTimestamp(int parameterIndex, java.sql.Timestamp x, Calendar cal) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setTimestamp", int.class, Timestamp.class, Calendar.class), parameterIndex, x, cal);
    }

    public void setNull(int parameterIndex, int sqlType, String typeName) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setNull", int.class, int.class, String.class), parameterIndex, sqlType, typeName);
    }

    //------------------------- JDBC 3.0 -----------------------------------
    public void setURL(int parameterIndex, java.net.URL x) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setURL", int.class, URL.class), parameterIndex, x);
    }

    public void setRowId(int parameterIndex, RowId x) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setRowId", int.class, RowId.class), parameterIndex, x);
    }

    public void setNString(int parameterIndex, String value) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setNString", int.class, String.class), parameterIndex, value);
    }

    public void setNCharacterStream(int parameterIndex, Reader value, long length) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setNCharacterStream", int.class, Reader.class, long.class), parameterIndex, value, length);
    }

    public void setNClob(int parameterIndex, NClob value) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setNClob", int.class, NClob.class), parameterIndex, value);
    }

    public void setClob(int parameterIndex, Reader reader, long length) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setClob", int.class, Reader.class, long.class), parameterIndex, reader, length);
    }

    public void setBlob(int parameterIndex, InputStream inputStream, long length) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setBlob", int.class, InputStream.class, long.class), parameterIndex, inputStream, length);
    }

    public void setNClob(int parameterIndex, Reader reader, long length) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setNClob", int.class, Reader.class, long.class), parameterIndex, reader, length);
    }

    public void setSQLXML(int parameterIndex, SQLXML xmlObject) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setSQLXML", int.class, SQLXML.class), parameterIndex, xmlObject);
    }

    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setObject", int.class, Object.class, int.class, int.class), parameterIndex, x, targetSqlType, scaleOrLength);
    }

    public void setAsciiStream(int parameterIndex, java.io.InputStream x, long length) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setAsciiStream", int.class, InputStream.class, long.class), parameterIndex, x, length);
    }

    public void setBinaryStream(int parameterIndex, java.io.InputStream x, long length) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setBinaryStream", int.class, InputStream.class, long.class), parameterIndex, x, length);
    }

    public void setCharacterStream(int parameterIndex, java.io.Reader reader, long length) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setCharacterStream", int.class, Reader.class, long.class), parameterIndex, reader, length);
    }

    public void setAsciiStream(int parameterIndex, java.io.InputStream x) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setAsciiStream", int.class, InputStream.class), parameterIndex, x);
    }

    public void setBinaryStream(int parameterIndex, java.io.InputStream x) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setBinaryStream", int.class, InputStream.class), parameterIndex, x);
    }

    public void setCharacterStream(int parameterIndex, java.io.Reader reader) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setCharacterStream", int.class, Reader.class), parameterIndex, reader);
    }

    public void setNCharacterStream(int parameterIndex, Reader value) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setNCharacterStream", int.class, Reader.class), parameterIndex, value);
    }

    public void setClob(int parameterIndex, Reader reader) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setClob", int.class, Reader.class), parameterIndex, reader);
    }

    public void setBlob(int parameterIndex, InputStream inputStream) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setBlob", int.class, InputStream.class), parameterIndex, inputStream);
    }

    public void setNClob(int parameterIndex, Reader reader) {
        record(parameterIndex, getDeclaredMethod(PreparedStatement.class, "setNClob", int.class, Reader.class), parameterIndex, reader);
    }


    //////  Parameter set operations for CallableStatement

    public void setNull(String parameterName, int sqlType) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setNull", String.class, int.class), parameterName, sqlType);
    }

    public void setBoolean(String parameterName, boolean x) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setBoolean", String.class, boolean.class), parameterName, x);
    }

    public void setByte(String parameterName, byte x) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setByte", String.class, byte.class), parameterName, x);
    }

    public void setShort(String parameterName, short x) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setShort", String.class, short.class), parameterName, x);
    }

    public void setInt(String parameterName, int x) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setInt", String.class, int.class), parameterName, x);
    }

    public void setLong(String parameterName, long x) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setLong", String.class, long.class), parameterName, x);
    }

    public void setFloat(String parameterName, float x) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setFloat", String.class, float.class), parameterName, x);
    }

    public void setDouble(String parameterName, double x) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setDouble", String.class, double.class), parameterName, x);
    }

    public void setBigDecimal(String parameterName, BigDecimal x) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setBigDecimal", String.class, BigDecimal.class), parameterName, x);
    }

    public void setString(String parameterName, String x) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setString", String.class, String.class), parameterName, x);
    }

    public void setBytes(String parameterName, byte x[]) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setBytes", String.class, byte[].class), parameterName, x);
    }

    public void setDate(String parameterName, Date x) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setDate", String.class, Date.class), parameterName, x);
    }

    public void setTime(String parameterName, Time x) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setTime", String.class, Time.class), parameterName, x);
    }

    public void setTimestamp(String parameterName, Timestamp x) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setTimestamp", String.class, Timestamp.class), parameterName, x);
    }

    public void setAsciiStream(String parameterName, InputStream x, int length) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setAsciiStream", String.class, InputStream.class, int.class), parameterName, x, length);
    }

    public void setBinaryStream(String parameterName, InputStream x, int length) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setBinaryStream", String.class, InputStream.class, int.class), parameterName, x, length);
    }

    public void setObject(String parameterName, Object x, int targetSqlType, int scale) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setObject", String.class, Object.class, int.class, int.class), parameterName, x, targetSqlType, scale);
    }

    public void setObject(String parameterName, Object x, int targetSqlType) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setObject", String.class, Object.class, int.class), parameterName, x, targetSqlType);
    }

    public void setObject(String parameterName, Object x) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setObject", String.class, Object.class), parameterName, x);
    }

    public void setCharacterStream(String parameterName, Reader reader, int length) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setCharacterStream", String.class, Reader.class, int.class), parameterName, reader, length);
    }

    public void setDate(String parameterName, Date x, Calendar cal) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setDate", String.class, Date.class, Calendar.class), parameterName, x, cal);
    }

    public void setTime(String parameterName, Time x, Calendar cal) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setTime", String.class, Time.class, Calendar.class), parameterName, x, cal);
    }

    public void setTimestamp(String parameterName, Timestamp x, Calendar cal) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setTimestamp", String.class, Timestamp.class, Calendar.class), parameterName, x, cal);
    }

    public void setNull(String parameterName, int sqlType, String typeName) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setNull", String.class, int.class, String.class), parameterName, sqlType, typeName);
    }

    // since 1.6
    public void setRowId(String parameterName, RowId x) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setRowId", String.class, RowId.class), parameterName, x);
    }

    public void setNString(String parameterName, String value) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setNString", String.class, String.class), parameterName, value);
    }

    public void setNCharacterStream(String parameterName, Reader value, long length) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setNCharacterStream", String.class, Reader.class, long.class), parameterName, value, length);
    }

    public void setNClob(String parameterName, NClob value) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setNClob", String.class, NClob.class), parameterName, value);
    }

    public void setClob(String parameterName, Reader reader, long length) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setClob", String.class, Reader.class, long.class), parameterName, reader, length);
    }

    public void setBlob(String parameterName, InputStream inputStream, long length) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setBlob", String.class, InputStream.class, long.class), parameterName, inputStream, length);
    }

    public void setNClob(String parameterName, Reader reader, long length) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setNClob", String.class, Reader.class, long.class), parameterName, reader, length);
    }

    public void setSQLXML(String parameterName, SQLXML xmlObject) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setSQLXML", String.class, SQLXML.class), parameterName, xmlObject);
    }

    public void setBlob(String parameterName, Blob x) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setBlob", String.class, Blob.class), parameterName, x);
    }

    public void setClob(String parameterName, Clob x) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setClob", String.class, Clob.class), parameterName, x);
    }

    public void setAsciiStream(String parameterName, InputStream x, long length) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setAsciiStream", String.class, InputStream.class, long.class), parameterName, x, length);
    }

    public void setBinaryStream(String parameterName, InputStream x, long length) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setBinaryStream", String.class, InputStream.class), parameterName, x, length);
    }

    public void setCharacterStream(String parameterName, Reader reader, long length) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setCharacterStream", String.class, Reader.class, long.class), parameterName, reader, length);
    }

    public void setAsciiStream(String parameterName, InputStream x) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setAsciiStream", String.class, InputStream.class), parameterName, x);
    }

    public void setBinaryStream(String parameterName, InputStream x) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setBinaryStream", String.class, InputStream.class), parameterName, x);
    }

    public void setCharacterStream(String parameterName, Reader reader) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setCharacterStream", String.class, Reader.class), parameterName, reader);
    }

    public void setNCharacterStream(String parameterName, Reader value) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setNCharacterStream", String.class, Reader.class), parameterName, value);
    }

    public void setClob(String parameterName, Reader reader) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setClob", String.class, Reader.class), parameterName, reader);
    }

    public void setBlob(String parameterName, InputStream inputStream) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setBlob", String.class, InputStream.class), parameterName, inputStream);
    }

    public void setNClob(String parameterName, Reader reader) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "setNClob", String.class, Reader.class), parameterName, reader);
    }


    //////  Output parameter operations for CallableStatement

    public void registerOutParameter(int parameterIndex, int sqlType) {
        record(parameterIndex, getDeclaredMethod(CallableStatement.class, "registerOutParameter", int.class, int.class), parameterIndex, sqlType);
    }

    public void registerOutParameter(int parameterIndex, int sqlType, int scale) {
        record(parameterIndex, getDeclaredMethod(CallableStatement.class, "registerOutParameter", int.class, int.class, int.class), parameterIndex, sqlType, scale);
    }

    //--------------------------JDBC 2.0-----------------------------
    public void registerOutParameter(int parameterIndex, int sqlType, String typeName) {
        record(parameterIndex, getDeclaredMethod(CallableStatement.class, "registerOutParameter", int.class, int.class, int.class), parameterIndex, sqlType, typeName);
    }
    //--------------------------JDBC 3.0-----------------------------

    public void registerOutParameter(String parameterName, int sqlType) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "registerOutParameter", String.class, int.class), parameterName, sqlType);
    }

    public void registerOutParameter(String parameterName, int sqlType, int scale) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "registerOutParameter", String.class, int.class, int.class), parameterName, sqlType, scale);
    }

    public void registerOutParameter(String parameterName, int sqlType, String typeName) {
        recordByName(parameterName, getDeclaredMethod(CallableStatement.class, "registerOutParameter", String.class, int.class, String.class), parameterName, sqlType, typeName);
    }

}
