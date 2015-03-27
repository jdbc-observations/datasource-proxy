package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.proxy.jdk.ConnectionInvocationHandler;
import net.ttddyy.dsproxy.proxy.jdk.JdkJdbcProxyFactory;
import net.ttddyy.dsproxy.transform.QueryTransformer;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Tadaya Tsuyukubo
 */
public class PreparedStatementProxyLogicForCallableStatementMockTest {
    private static final String DS_NAME = "myDS";

    @Test
    public void testExecuteWithNoParam() throws Throwable {
        final String query = "{call procedure_name}";

        CallableStatement stat = mock(CallableStatement.class);
        QueryExecutionListener listener = mock(QueryExecutionListener.class);
        InterceptorHolder interceptorHolder = getInterceptorHolder(listener);

        when(stat.execute()).thenReturn(true);

        PreparedStatementProxyLogic logic = getProxyLogic(stat, query, interceptorHolder);

        Method method = CallableStatement.class.getMethod("execute");
        Object result = logic.invoke(method, null);

        assertThat(result, is(instanceOf(boolean.class)));
        assertThat((Boolean)result, is(Boolean.TRUE));
        verifyListenerWithNoParam(listener, "execute", query);
    }

    @Test
    public void testExecuteWithParamByPosition() throws Throwable {
        final String query = "{call procedure_name}";

        CallableStatement stat = mock(CallableStatement.class);
        QueryExecutionListener listener = mock(QueryExecutionListener.class);
        InterceptorHolder interceptorHolder = getInterceptorHolder(listener);

        when(stat.execute()).thenReturn(true);

        PreparedStatementProxyLogic logic = getProxyLogic(stat, query, interceptorHolder);

        setParameterByPosition(logic);
        Method method = CallableStatement.class.getMethod("execute");
        Object result = logic.invoke(method, null);

        assertThat(result, is(instanceOf(boolean.class)));
        assertThat((Boolean)result, is(Boolean.TRUE));
        verifyParametersByPosition(stat);

        verifyListenerWithParamByPosition(listener, "execute", query);
    }

    @Test
    public void testExecuteWithParamByName() throws Throwable {
        final String query = "{call procedure_name}";

        CallableStatement stat = mock(CallableStatement.class);
        QueryExecutionListener listener = mock(QueryExecutionListener.class);
        InterceptorHolder interceptorHolder = getInterceptorHolder(listener);

        when(stat.execute()).thenReturn(true);

        PreparedStatementProxyLogic logic = getProxyLogic(stat, query, interceptorHolder);

        setParameterByName(logic);
        Method method = CallableStatement.class.getMethod("execute");
        Object result = logic.invoke(method, null);

        assertThat(result, is(instanceOf(boolean.class)));
        assertThat((Boolean)result, is(true));
        verifyParametersByName(stat);

        verifyListenerWithParamByName(listener, "execute", query);
    }

    @Test
    public void testExecuteUpdateWithNoParam() throws Throwable {
        final String query = "{call procedure_name}";

        CallableStatement stat = mock(CallableStatement.class);
        QueryExecutionListener listener = mock(QueryExecutionListener.class);
        InterceptorHolder interceptorHolder = getInterceptorHolder(listener);

        when(stat.executeUpdate()).thenReturn(100);

        PreparedStatementProxyLogic logic = getProxyLogic(stat, query, interceptorHolder);

        Method method = CallableStatement.class.getMethod("executeUpdate");
        Object result = logic.invoke(method, null);

        assertThat(result, is(instanceOf(int.class)));
        assertThat((Integer) result, is(100));
        verifyListenerWithNoParam(listener, "executeUpdate", query);
    }

    @Test
    public void testExecuteUpdateWithParamByPosition() throws Throwable {
        final String query = "{call procedure_name}";

        CallableStatement stat = mock(CallableStatement.class);
        QueryExecutionListener listener = mock(QueryExecutionListener.class);
        InterceptorHolder interceptorHolder = getInterceptorHolder(listener);

        when(stat.executeUpdate()).thenReturn(100);

        PreparedStatementProxyLogic logic = getProxyLogic(stat, query, interceptorHolder);

        setParameterByPosition(logic);
        Method method = CallableStatement.class.getMethod("executeUpdate");
        Object result = logic.invoke(method, null);

        assertThat(result, is(instanceOf(int.class)));
        assertThat((Integer) result, is(100));
        verifyParametersByPosition(stat);

        verifyListenerWithParamByPosition(listener, "executeUpdate", query);
    }

    @Test
    public void testExecuteUpdateWithParamByName() throws Throwable {
        final String query = "{call procedure_name}";

        CallableStatement stat = mock(CallableStatement.class);
        QueryExecutionListener listener = mock(QueryExecutionListener.class);
        InterceptorHolder interceptorHolder = getInterceptorHolder(listener);

        when(stat.executeUpdate()).thenReturn(100);

        PreparedStatementProxyLogic logic = getProxyLogic(stat, query, interceptorHolder);

        setParameterByName(logic);
        Method method = CallableStatement.class.getMethod("executeUpdate");
        Object result = logic.invoke(method, null);

        assertThat(result, is(instanceOf(int.class)));
        assertThat((Integer) result, is(100));
        verifyParametersByName(stat);

        verifyListenerWithParamByName(listener, "executeUpdate", query);
    }

    @Test
    public void testExecuteQueryWithNoParam() throws Throwable {
        final String query = "{call procedure_name}";

        CallableStatement stat = mock(CallableStatement.class);
        QueryExecutionListener listener = mock(QueryExecutionListener.class);
        InterceptorHolder interceptorHolder = getInterceptorHolder(listener);

        ResultSet mockResultSet = mock(ResultSet.class);
        when(stat.executeQuery()).thenReturn(mockResultSet);

        PreparedStatementProxyLogic logic = getProxyLogic(stat, query, interceptorHolder);
        Method method = CallableStatement.class.getMethod("executeQuery");
        Object result = logic.invoke(method, null);

        assertThat(result, is(instanceOf(ResultSet.class)));
        assertThat((ResultSet) result, is(mockResultSet));
        verifyListenerWithNoParam(listener, "executeQuery", query);
    }

    @Test
    public void testExecuteQueryWithParamByPosition() throws Throwable {
        final String query = "{call procedure_name}";

        CallableStatement stat = mock(CallableStatement.class);
        QueryExecutionListener listener = mock(QueryExecutionListener.class);
        InterceptorHolder interceptorHolder = getInterceptorHolder(listener);

        ResultSet mockResultSet = mock(ResultSet.class);
        when(stat.executeQuery()).thenReturn(mockResultSet);

        PreparedStatementProxyLogic logic = getProxyLogic(stat, query, interceptorHolder);
        setParameterByPosition(logic);
        Method method = CallableStatement.class.getMethod("executeQuery");
        Object result = logic.invoke(method, null);

        assertThat(result, is(instanceOf(ResultSet.class)));
        assertThat((ResultSet) result, is(mockResultSet));
        verifyParametersByPosition(stat);

        verifyListenerWithParamByPosition(listener, "executeQuery", query);
    }

    @Test
    public void testExecuteQueryWithParamByName() throws Throwable {
        final String query = "{call procedure_name}";

        CallableStatement stat = mock(CallableStatement.class);
        QueryExecutionListener listener = mock(QueryExecutionListener.class);
        InterceptorHolder interceptorHolder = getInterceptorHolder(listener);

        ResultSet mockResultSet = mock(ResultSet.class);
        when(stat.executeQuery()).thenReturn(mockResultSet);

        PreparedStatementProxyLogic logic = getProxyLogic(stat, query, interceptorHolder);

        setParameterByName(logic);
        Method method = CallableStatement.class.getMethod("executeQuery");
        Object result = logic.invoke(method, null);

        assertThat(result, is(instanceOf(ResultSet.class)));
        assertThat((ResultSet) result, is(mockResultSet));
        verifyParametersByName(stat);

        verifyListenerWithParamByName(listener, "executeQuery", query);
    }

    @Test
    public void testExecuteBatch() throws Throwable {
        final String query = "{call procedure_a}";

        CallableStatement stat = mock(CallableStatement.class);
        QueryExecutionListener listener = mock(QueryExecutionListener.class);
        InterceptorHolder interceptorHolder = getInterceptorHolder(listener);

        PreparedStatementProxyLogic logic = getProxyLogic(stat, query, interceptorHolder);

        Method setString = CallableStatement.class.getMethod("setString", int.class, String.class);
        Method setInt = CallableStatement.class.getMethod("setInt", int.class, int.class);
        Method addBatch = CallableStatement.class.getMethod("addBatch");
        Method executeBatch = CallableStatement.class.getMethod("executeBatch");

        logic.invoke(setString, new Object[]{1, "foo"});
        logic.invoke(setInt, new Object[]{2, 10});
        logic.invoke(addBatch, null);

        logic.invoke(setString, new Object[]{1, "bar"});
        logic.invoke(setInt, new Object[]{2, 20});
        logic.invoke(addBatch, null);

        logic.invoke(setString, new Object[]{1, "baz"});
        logic.invoke(setInt, new Object[]{2, 30});
        logic.invoke(addBatch, null);


        logic.invoke(executeBatch, null);

        MockTestUtils.verifyListenerForBatch(listener, DS_NAME, query,
                new Object[][]{{"foo", 10}, {"bar", 20}, {"baz", 30}});
    }

    @Test
    public void testBatchWithClearBatch() throws Throwable {
        final String query = "{call procedure_a}";

        CallableStatement stat = mock(CallableStatement.class);
        QueryExecutionListener listener = mock(QueryExecutionListener.class);
        InterceptorHolder interceptorHolder = getInterceptorHolder(listener);

        PreparedStatementProxyLogic logic = getProxyLogic(stat, query, interceptorHolder);

        Method setString = CallableStatement.class.getMethod("setString", int.class, String.class);
        Method setInt = CallableStatement.class.getMethod("setInt", int.class, int.class);
        Method addBatch = CallableStatement.class.getMethod("addBatch");
        Method executeBatch = CallableStatement.class.getMethod("executeBatch");
        Method clearBatch = CallableStatement.class.getMethod("clearBatch");

        logic.invoke(setString, new Object[]{1, "foo"});
        logic.invoke(setInt, new Object[]{2, 10});
        logic.invoke(addBatch, null);

        logic.invoke(clearBatch, null);


        logic.invoke(setString, new Object[]{1, "FOO"});
        logic.invoke(setInt, new Object[]{2, 20});
        logic.invoke(addBatch, null);

        logic.invoke(executeBatch, null);

        verify(stat).setString(1, "foo");
        verify(stat).setInt(2, 10);
        verify(stat).clearBatch();
        verify(stat).setString(1, "FOO");
        verify(stat).setInt(2, 20);
        verify(stat, times(2)).addBatch();

        MockTestUtils.verifyListenerForBatch(listener, DS_NAME, query, new Object[][]{{"FOO", 20}});
    }

    @Test
    public void testBatchWithClearParameters() throws Throwable {
        final String query = "{call procedure_a}";

        CallableStatement stat = mock(CallableStatement.class);
        QueryExecutionListener listener = mock(QueryExecutionListener.class);
        InterceptorHolder interceptorHolder = getInterceptorHolder(listener);

        PreparedStatementProxyLogic logic = getProxyLogic(stat, query, interceptorHolder);

        Method setString = CallableStatement.class.getMethod("setString", int.class, String.class);
        Method setInt = CallableStatement.class.getMethod("setInt", int.class, int.class);
        Method addBatch = CallableStatement.class.getMethod("addBatch");
        Method executeBatch = CallableStatement.class.getMethod("executeBatch");
        Method clearParameters = CallableStatement.class.getMethod("clearParameters");

        logic.invoke(setString, new Object[]{1, "foo"});

        logic.invoke(clearParameters, null);

        logic.invoke(setString, new Object[]{1, "FOO"});
        logic.invoke(setInt, new Object[]{2, 10});
        logic.invoke(addBatch, null);

        logic.invoke(executeBatch, null);

        verify(stat).setString(1, "foo");
        verify(stat).clearParameters();
        verify(stat).setString(1, "FOO");
        verify(stat).setInt(2, 10);
        verify(stat).addBatch();

        MockTestUtils.verifyListenerForBatch(listener, DS_NAME, query, new Object[][]{{"FOO", 10}});

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testClearParameters() throws Throwable {
        final String query = "{call procedure_a}";

        CallableStatement stat = mock(CallableStatement.class);
        QueryExecutionListener listener = mock(QueryExecutionListener.class);
        InterceptorHolder interceptorHolder = getInterceptorHolder(listener);

        PreparedStatementProxyLogic logic = getProxyLogic(stat, query, interceptorHolder);
        Method setString = CallableStatement.class.getMethod("setString", int.class, String.class);
        Method setInt = CallableStatement.class.getMethod("setInt", int.class, int.class);
        Method addBatch = CallableStatement.class.getMethod("addBatch");
        Method executeBatch = CallableStatement.class.getMethod("executeBatch");
        Method clearParameters = CallableStatement.class.getMethod("clearParameters");

        logic.invoke(setString, new Object[]{1, "foo"});
        logic.invoke(setInt, new Object[]{2, 10});
        logic.invoke(clearParameters, new Object[]{});
        logic.invoke(addBatch, new Object[]{});

        logic.invoke(executeBatch, new Object[]{});

        verify(stat).setString(1, "foo");
        verify(stat).setInt(2, 10);
        verify(stat).clearParameters();
        verify(stat).addBatch();

        ArgumentCaptor<List> queryInfoListCaptor = ArgumentCaptor.forClass(List.class);

        verify(listener).afterQuery(any(ExecutionInfo.class), queryInfoListCaptor.capture());

        List<QueryInfo> queryInfoList = queryInfoListCaptor.getValue();
        assertThat(queryInfoList.size(), is(1));

        assertThat(queryInfoList.get(0).getQueryArgsList(), hasSize(1));
        assertThat("Args should be empty", queryInfoList.get(0).getQueryArgsList().get(0), hasSize(0));

    }

    private InterceptorHolder getInterceptorHolder(QueryExecutionListener listener) {
        return new InterceptorHolder(listener, QueryTransformer.DEFAULT);
    }

    private static class Param<T> {
        Class<T> clazz;
        T value;
        int index;
        String strIndex;

        private Param(Class<T> clazz, T value, int index, String strIndex) {
            this.clazz = clazz;
            this.value = value;
            this.index = index;
            this.strIndex = strIndex; // null if not applicable
        }
    }

    private static final Param<Array> PARAM_ARRAY;
    private static final Param<InputStream> PARAM_ASCIISTREAM;
    private static final Param<BigDecimal> PARAM_BIGDECIMAL;
    private static final Param<InputStream> PARAM_BINARYSTREAM;
    private static final Param<Blob> PARAM_BLOB;
    private static final Param<Boolean> PARAM_BOOLEAN;
    private static final Param<Reader> PARAM_CHARACTERSTREAM;
    private static final Param<Clob> PARAM_CLOB;
    private static final Param<Date> PARAM_DATE;
    private static final Param<Double> PARAM_DOUBLE;
    private static final Param<Float> PARAM_FLOAT;
    private static final Param<Integer> PARAM_INT;
    private static final Param<Long> PARAM_LONG;
    private static final Param<Integer> PARAM_NULL;
    private static final Param<Object> PARAM_OBJECT;
    private static final Param<Ref> PARAM_REF;
    private static final Param<Short> PARAM_SHORT;
    private static final Param<String> PARAM_STRING;
    private static final Param<Time> PARAM_TIME;
    private static final Param<Timestamp> PARAM_TIMESTAMP;
    private static final Param<URL> PARAM_URL;

    static {
        PARAM_ARRAY = new Param<Array>(Array.class, mock(Array.class), 1, null);
        PARAM_ASCIISTREAM = new Param<InputStream>(InputStream.class, mock(InputStream.class), 2, "ascii");
        PARAM_BIGDECIMAL = new Param<BigDecimal>(BigDecimal.class, mock(BigDecimal.class), 3, "bigdecimal");
        PARAM_BINARYSTREAM = new Param<InputStream>(InputStream.class, mock(InputStream.class), 4, "binarystream");
        PARAM_BLOB = new Param<Blob>(Blob.class, mock(Blob.class), 5, "blob");
        PARAM_BOOLEAN = new Param<Boolean>(Boolean.class, true, 6, "boolean");
        PARAM_CHARACTERSTREAM = new Param<Reader>(Reader.class, mock(Reader.class), 7, "reader");
        PARAM_CLOB = new Param<Clob>(Clob.class, mock(Clob.class), 8, "clob");
        PARAM_DATE = new Param<Date>(Date.class, mock(Date.class), 9, "date");
        PARAM_DOUBLE = new Param<Double>(double.class, 10.0d, 10, "double");
        PARAM_FLOAT = new Param<Float>(Float.class, 20f, 11, "float");
        PARAM_INT = new Param<Integer>(int.class, 30, 12, "int");
        PARAM_LONG = new Param<Long>(long.class, 40L, 13, "long");
        PARAM_NULL = new Param<Integer>(int.class, Types.VARCHAR, 14, "null");
        PARAM_OBJECT = new Param<Object>(Object.class, mock(Object.class), 15, "object");
        PARAM_REF = new Param<Ref>(Ref.class, mock(Ref.class), 16, null);
        PARAM_SHORT = new Param<Short>(short.class, (short) 50, 17, "short");
        PARAM_STRING = new Param<String>(String.class, "str", 18, "string");
        PARAM_TIME = new Param<Time>(Time.class, mock(Time.class), 19, "time");
        PARAM_TIMESTAMP = new Param<Timestamp>(Timestamp.class, mock(Timestamp.class), 20, "timestamp");
        try {
            PARAM_URL = new Param<URL>(URL.class, new URL("http://foo.com"), 21, "url");
        } catch (Exception e) {
            throw new RuntimeException("failed to initialize URL");
        }

    }

    private static final List<Param> PARAMS = new ArrayList<Param>() {
        {
            add(PARAM_ARRAY);
            add(PARAM_ASCIISTREAM);
            add(PARAM_BIGDECIMAL);
            add(PARAM_BINARYSTREAM);
            add(PARAM_BLOB);
            add(PARAM_BOOLEAN);
            add(PARAM_CHARACTERSTREAM);
            add(PARAM_CLOB);
            add(PARAM_DATE);
            add(PARAM_DOUBLE);
            add(PARAM_FLOAT);
            add(PARAM_INT);
            add(PARAM_LONG);
            add(PARAM_NULL);
            add(PARAM_OBJECT);
            add(PARAM_REF);
            add(PARAM_SHORT);
            add(PARAM_STRING);
            add(PARAM_TIME);
            add(PARAM_TIMESTAMP);
            add(PARAM_URL);
        }
    };


    private void setParameterByPosition(PreparedStatementProxyLogic logic) throws Throwable {

        Method setArray = CallableStatement.class.getMethod("setArray", int.class, Array.class);
        Method setAsciiStream = CallableStatement.class.getMethod("setAsciiStream", int.class, InputStream.class);
        Method setBigDecimal = CallableStatement.class.getMethod("setBigDecimal", int.class, BigDecimal.class);
        Method setBinaryStream = CallableStatement.class.getMethod("setBinaryStream", int.class, InputStream.class);
        Method setBlob = CallableStatement.class.getMethod("setBlob", int.class, Blob.class);
        Method setBoolean = CallableStatement.class.getMethod("setBoolean", int.class, boolean.class);
        Method setCharacterStream = CallableStatement.class.getMethod("setCharacterStream", int.class, Reader.class);
        Method setClob = CallableStatement.class.getMethod("setClob", int.class, Clob.class);
        Method setDate = CallableStatement.class.getMethod("setDate", int.class, Date.class);
        Method setDouble = CallableStatement.class.getMethod("setDouble", int.class, double.class);
        Method setFloat = CallableStatement.class.getMethod("setFloat", int.class, float.class);
        Method setInt = CallableStatement.class.getMethod("setInt", int.class, int.class);
        Method setLong = CallableStatement.class.getMethod("setLong", int.class, long.class);
        Method setNull = CallableStatement.class.getMethod("setNull", int.class, int.class);
        Method setObject = CallableStatement.class.getMethod("setObject", int.class, Object.class);
        Method setRef = CallableStatement.class.getMethod("setRef", int.class, Ref.class);
        Method setShort = CallableStatement.class.getMethod("setShort", int.class, short.class);
        Method setString = CallableStatement.class.getMethod("setString", int.class, String.class);
        Method setTime = CallableStatement.class.getMethod("setTime", int.class, Time.class);
        Method setTimestamp = CallableStatement.class.getMethod("setTimestamp", int.class, Timestamp.class);
        Method setURL = CallableStatement.class.getMethod("setURL", int.class, URL.class);

        logic.invoke(setArray, new Object[]{PARAM_ARRAY.index, PARAM_ARRAY.value});
        logic.invoke(setAsciiStream, new Object[]{PARAM_ASCIISTREAM.index, PARAM_ASCIISTREAM.value});
        logic.invoke(setBigDecimal, new Object[]{PARAM_BIGDECIMAL.index, PARAM_BIGDECIMAL.value});
        logic.invoke(setBinaryStream, new Object[]{PARAM_BINARYSTREAM.index, PARAM_BINARYSTREAM.value});
        logic.invoke(setBlob, new Object[]{PARAM_BLOB.index, PARAM_BLOB.value});
        logic.invoke(setBoolean, new Object[]{PARAM_BOOLEAN.index, PARAM_BOOLEAN.value});
        logic.invoke(setCharacterStream, new Object[]{PARAM_CHARACTERSTREAM.index, PARAM_CHARACTERSTREAM.value});
        logic.invoke(setClob, new Object[]{PARAM_CLOB.index, PARAM_CLOB.value});
        logic.invoke(setDate, new Object[]{PARAM_DATE.index, PARAM_DATE.value});
        logic.invoke(setDouble, new Object[]{PARAM_DOUBLE.index, PARAM_DOUBLE.value});
        logic.invoke(setFloat, new Object[]{PARAM_FLOAT.index, PARAM_FLOAT.value});
        logic.invoke(setInt, new Object[]{PARAM_INT.index, PARAM_INT.value});
        logic.invoke(setLong, new Object[]{PARAM_LONG.index, PARAM_LONG.value});
        logic.invoke(setNull, new Object[]{PARAM_NULL.index, PARAM_NULL.value});
        logic.invoke(setObject, new Object[]{PARAM_OBJECT.index, PARAM_OBJECT.value});
        logic.invoke(setRef, new Object[]{PARAM_REF.index, PARAM_REF.value});
        logic.invoke(setShort, new Object[]{PARAM_SHORT.index, PARAM_SHORT.value});
        logic.invoke(setString, new Object[]{PARAM_STRING.index, PARAM_STRING.value});
        logic.invoke(setTime, new Object[]{PARAM_TIME.index, PARAM_TIME.value});
        logic.invoke(setTimestamp, new Object[]{PARAM_TIMESTAMP.index, PARAM_TIMESTAMP.value});
        logic.invoke(setURL, new Object[]{PARAM_URL.index, PARAM_URL.value});
    }

    private void setParameterByName(PreparedStatementProxyLogic logic) throws Throwable {

        Method setAsciiStream = CallableStatement.class.getMethod("setAsciiStream", String.class, InputStream.class);
        Method setBigDecimal = CallableStatement.class.getMethod("setBigDecimal", String.class, BigDecimal.class);
        Method setBinaryStream = CallableStatement.class.getMethod("setBinaryStream", String.class, InputStream.class);
        Method setBlob = CallableStatement.class.getMethod("setBlob", String.class, Blob.class);
        Method setBoolean = CallableStatement.class.getMethod("setBoolean", String.class, boolean.class);
        Method setCharacterStream = CallableStatement.class.getMethod("setCharacterStream", String.class, Reader.class);
        Method setClob = CallableStatement.class.getMethod("setClob", String.class, Clob.class);
        Method setDate = CallableStatement.class.getMethod("setDate", String.class, Date.class);
        Method setDouble = CallableStatement.class.getMethod("setDouble", String.class, double.class);
        Method setFloat = CallableStatement.class.getMethod("setFloat", String.class, float.class);
        Method setInt = CallableStatement.class.getMethod("setInt", String.class, int.class);
        Method setLong = CallableStatement.class.getMethod("setLong", String.class, long.class);
        Method setNull = CallableStatement.class.getMethod("setNull", String.class, int.class);
        Method setObject = CallableStatement.class.getMethod("setObject", String.class, Object.class);
        Method setShort = CallableStatement.class.getMethod("setShort", String.class, short.class);
        Method setString = CallableStatement.class.getMethod("setString", String.class, String.class);
        Method setTime = CallableStatement.class.getMethod("setTime", String.class, Time.class);
        Method setTimestamp = CallableStatement.class.getMethod("setTimestamp", String.class, Timestamp.class);
        Method setURL = CallableStatement.class.getMethod("setURL", String.class, URL.class);


        logic.invoke(setAsciiStream, new Object[]{PARAM_ASCIISTREAM.strIndex, PARAM_ASCIISTREAM.value});
        logic.invoke(setBigDecimal, new Object[]{PARAM_BIGDECIMAL.strIndex, PARAM_BIGDECIMAL.value});
        logic.invoke(setBinaryStream, new Object[]{PARAM_BINARYSTREAM.strIndex, PARAM_BINARYSTREAM.value});
        logic.invoke(setBlob, new Object[]{PARAM_BLOB.strIndex, PARAM_BLOB.value});
        logic.invoke(setBoolean, new Object[]{PARAM_BOOLEAN.strIndex, PARAM_BOOLEAN.value});
        logic.invoke(setCharacterStream, new Object[]{PARAM_CHARACTERSTREAM.strIndex, PARAM_CHARACTERSTREAM.value});
        logic.invoke(setClob, new Object[]{PARAM_CLOB.strIndex, PARAM_CLOB.value});
        logic.invoke(setDate, new Object[]{PARAM_DATE.strIndex, PARAM_DATE.value});
        logic.invoke(setDouble, new Object[]{PARAM_DOUBLE.strIndex, PARAM_DOUBLE.value});
        logic.invoke(setFloat, new Object[]{PARAM_FLOAT.strIndex, PARAM_FLOAT.value});
        logic.invoke(setInt, new Object[]{PARAM_INT.strIndex, PARAM_INT.value});
        logic.invoke(setLong, new Object[]{PARAM_LONG.strIndex, PARAM_LONG.value});
        logic.invoke(setNull, new Object[]{PARAM_NULL.strIndex, PARAM_NULL.value});
        logic.invoke(setObject, new Object[]{PARAM_OBJECT.strIndex, PARAM_OBJECT.value});
        logic.invoke(setShort, new Object[]{PARAM_SHORT.strIndex, PARAM_SHORT.value});
        logic.invoke(setString, new Object[]{PARAM_STRING.strIndex, PARAM_STRING.value});
        logic.invoke(setTime, new Object[]{PARAM_TIME.strIndex, PARAM_TIME.value});
        logic.invoke(setTimestamp, new Object[]{PARAM_TIMESTAMP.strIndex, PARAM_TIMESTAMP.value});
        logic.invoke(setURL, new Object[]{PARAM_URL.strIndex, PARAM_URL.value});
    }

    private void verifyParametersByPosition(CallableStatement mockStatement) throws Exception {
        verify(mockStatement).setArray(PARAM_ARRAY.index, PARAM_ARRAY.value);
        verify(mockStatement).setAsciiStream(PARAM_ASCIISTREAM.index, PARAM_ASCIISTREAM.value);
        verify(mockStatement).setBigDecimal(PARAM_BIGDECIMAL.index, PARAM_BIGDECIMAL.value);
        verify(mockStatement).setBinaryStream(PARAM_BINARYSTREAM.index, PARAM_BINARYSTREAM.value);
        verify(mockStatement).setBlob(PARAM_BLOB.index, PARAM_BLOB.value);
        verify(mockStatement).setBoolean(PARAM_BOOLEAN.index, PARAM_BOOLEAN.value);
        verify(mockStatement).setCharacterStream(PARAM_CHARACTERSTREAM.index, PARAM_CHARACTERSTREAM.value);
        verify(mockStatement).setClob(PARAM_CLOB.index, PARAM_CLOB.value);
        verify(mockStatement).setDate(PARAM_DATE.index, PARAM_DATE.value);
        verify(mockStatement).setDouble(PARAM_DOUBLE.index, PARAM_DOUBLE.value);
        verify(mockStatement).setFloat(PARAM_FLOAT.index, PARAM_FLOAT.value);
        verify(mockStatement).setInt(PARAM_INT.index, PARAM_INT.value);
        verify(mockStatement).setLong(PARAM_LONG.index, PARAM_LONG.value);
        verify(mockStatement).setNull(PARAM_NULL.index, PARAM_NULL.value);
        verify(mockStatement).setObject(PARAM_OBJECT.index, PARAM_OBJECT.value);
        verify(mockStatement).setRef(PARAM_REF.index, PARAM_REF.value);
        verify(mockStatement).setShort(PARAM_SHORT.index, PARAM_SHORT.value);
        verify(mockStatement).setString(PARAM_STRING.index, PARAM_STRING.value);
        verify(mockStatement).setTime(PARAM_TIME.index, PARAM_TIME.value);
        verify(mockStatement).setTimestamp(PARAM_TIMESTAMP.index, PARAM_TIMESTAMP.value);
        verify(mockStatement).setURL(PARAM_URL.index, PARAM_URL.value);
    }

    private void verifyParametersByName(CallableStatement mockStatement) throws Exception {
        verify(mockStatement).setAsciiStream(PARAM_ASCIISTREAM.strIndex, PARAM_ASCIISTREAM.value);
        verify(mockStatement).setBigDecimal(PARAM_BIGDECIMAL.strIndex, PARAM_BIGDECIMAL.value);
        verify(mockStatement).setBinaryStream(PARAM_BINARYSTREAM.strIndex, PARAM_BINARYSTREAM.value);
        verify(mockStatement).setBlob(PARAM_BLOB.strIndex, PARAM_BLOB.value);
        verify(mockStatement).setBoolean(PARAM_BOOLEAN.strIndex, PARAM_BOOLEAN.value);
        verify(mockStatement).setCharacterStream(PARAM_CHARACTERSTREAM.strIndex, PARAM_CHARACTERSTREAM.value);
        verify(mockStatement).setClob(PARAM_CLOB.strIndex, PARAM_CLOB.value);
        verify(mockStatement).setDate(PARAM_DATE.strIndex, PARAM_DATE.value);
        verify(mockStatement).setDouble(PARAM_DOUBLE.strIndex, PARAM_DOUBLE.value);
        verify(mockStatement).setFloat(PARAM_FLOAT.strIndex, PARAM_FLOAT.value);
        verify(mockStatement).setInt(PARAM_INT.strIndex, PARAM_INT.value);
        verify(mockStatement).setLong(PARAM_LONG.strIndex, PARAM_LONG.value);
        verify(mockStatement).setNull(PARAM_NULL.strIndex, PARAM_NULL.value);
        verify(mockStatement).setObject(PARAM_OBJECT.strIndex, PARAM_OBJECT.value);
        verify(mockStatement).setShort(PARAM_SHORT.strIndex, PARAM_SHORT.value);
        verify(mockStatement).setString(PARAM_STRING.strIndex, PARAM_STRING.value);
        verify(mockStatement).setTime(PARAM_TIME.strIndex, PARAM_TIME.value);
        verify(mockStatement).setTimestamp(PARAM_TIMESTAMP.strIndex, PARAM_TIMESTAMP.value);
        verify(mockStatement).setURL(PARAM_URL.strIndex, PARAM_URL.value);
    }

    enum ParamStatus {
        NO_PARAM, BY_POSITION, BY_NAME
    }

    private void verifyListenerWithNoParam(QueryExecutionListener listener, String methodName, String query) {
        verifyListener(listener, methodName, query, ParamStatus.NO_PARAM);
    }

    private void verifyListenerWithParamByPosition(QueryExecutionListener listener, String methodName, String query) {
        verifyListener(listener, methodName, query, ParamStatus.BY_POSITION);
    }

    private void verifyListenerWithParamByName(QueryExecutionListener listener, String methodName, String query) {
        verifyListener(listener, methodName, query, ParamStatus.BY_NAME);
    }

    @SuppressWarnings("unchecked")
    private void verifyListener(QueryExecutionListener listener, String methodName, String query, ParamStatus paramStatus) {
        ArgumentCaptor<ExecutionInfo> executionInfoCaptor = ArgumentCaptor.forClass(ExecutionInfo.class);
        ArgumentCaptor<List> queryInfoListCaptor = ArgumentCaptor.forClass(List.class);

        verify(listener).afterQuery(executionInfoCaptor.capture(), queryInfoListCaptor.capture());

        ExecutionInfo execInfo = executionInfoCaptor.getValue();
        assertThat(execInfo.getMethod(), is(notNullValue()));
        assertThat(execInfo.getMethod().getName(), is(methodName));

        assertThat(execInfo.getMethodArgs(), is(nullValue()));
        assertThat(execInfo.getDataSourceName(), is(DS_NAME));
        assertThat(execInfo.getThrowable(), is(nullValue()));

        List<QueryInfo> queryInfoList = queryInfoListCaptor.getValue();
        assertThat(queryInfoList.size(), is(1));
        QueryInfo queryInfo = queryInfoList.get(0);
        assertThat(queryInfo.getQuery(), is(equalTo(query)));

        List<List<?>> queryArgsList = queryInfo.getQueryArgsList();
        assertThat("non-batch exec should have only one params.", queryArgsList, hasSize(1));
        List<?> queryArgs = queryArgsList.get(0);
        if (ParamStatus.NO_PARAM != paramStatus) {
            if (ParamStatus.BY_POSITION == paramStatus) {
                assertThat(queryArgs.size(), is(PARAMS.size()));

                for (int i = 0; i < PARAMS.size(); i++) {
                    Param param = PARAMS.get(i);
                    final Object value = queryArgs.get(i);

                    assertThat(value, is(instanceOf(param.clazz)));
                    assertThat(value, is(param.value));

                }

            } else {
                // By name

                int argSize = 0;
                List<Param> paramsForStrIndex = new ArrayList<Param>();
                for (Param param : PARAMS) {
                    if (param.strIndex != null) {
                        argSize++;
                        paramsForStrIndex.add(param);
                    }
                }

                // sort by name
//                Collections.sort(paramsForStrIndex, new Comparator<Param>() {
//                    public int compare(Param left, Param right) {
//                        return left.strIndex.compareTo(right.strIndex);
//                    }
//                });
                // TODO: currently depending on the set operation order, find out better way of

                assertThat(queryArgs.size(), is(argSize));

                int i = 0;
                for (Param param : paramsForStrIndex) {
                    final Object value = queryArgs.get(i++);

                    assertThat(value, is(instanceOf(param.clazz)));
                    assertThat(value, is(param.value));
                }
            }
        }
    }


    private PreparedStatementProxyLogic getProxyLogic(CallableStatement cs, String query, InterceptorHolder interceptorHolder) {
        return new PreparedStatementProxyLogic(cs, query, interceptorHolder, DS_NAME, new JdkJdbcProxyFactory());
    }


    @Test
    public void testGetTarget() throws Throwable {
        CallableStatement orig = mock(CallableStatement.class);
        PreparedStatementProxyLogic logic = getProxyLogic(orig, null, null);

        Method method = ProxyJdbcObject.class.getMethod("getTarget");
        Object result = logic.invoke(method, null);

        assertThat(result, is(instanceOf(CallableStatement.class)));
        assertThat((CallableStatement) result, is(sameInstance(orig)));
    }

    @Test
    public void testUnwrap() throws Throwable {
        CallableStatement mock = mock(CallableStatement.class);
        when(mock.unwrap(String.class)).thenReturn("called");

        PreparedStatementProxyLogic logic = getProxyLogic(mock, null, null);
        Method method = CallableStatement.class.getMethod("unwrap", Class.class);
        Object result = logic.invoke(method, new Object[]{String.class});

        verify(mock).unwrap(String.class);
        assertThat(result, is(instanceOf(String.class)));
        assertThat((String) result, is("called"));
    }

    @Test
    public void testIsWrapperFor() throws Throwable {
        CallableStatement mock = mock(CallableStatement.class);
        when(mock.isWrapperFor(String.class)).thenReturn(true);

        PreparedStatementProxyLogic logic = getProxyLogic(mock, null, null);

        Method method = CallableStatement.class.getMethod("isWrapperFor", Class.class);
        Object result = logic.invoke(method, new Object[]{String.class});

        verify(mock).isWrapperFor(String.class);
        assertThat(result, is(instanceOf(boolean.class)));
        assertThat((Boolean) result, is(true));
    }

    @Test
    public void testGetConnection() throws Throwable {
        Connection conn = mock(Connection.class);
        CallableStatement stat = mock(CallableStatement.class);

        when(stat.getConnection()).thenReturn(conn);
        PreparedStatementProxyLogic logic = getProxyLogic(stat, null, null);

        Method method = CallableStatement.class.getMethod("getConnection");
        Object result = logic.invoke(method, null);

        assertThat(result, is(instanceOf(Connection.class)));
        verify(stat).getConnection();

        assertThat(Proxy.isProxyClass(result.getClass()), is(true));

        InvocationHandler handler = Proxy.getInvocationHandler(result);
        assertThat(handler, is(instanceOf(ConnectionInvocationHandler.class)));

        assertThat(result, is(instanceOf(ProxyJdbcObject.class)));
        Object obj = ((ProxyJdbcObject) result).getTarget();

        assertThat(obj, is(instanceOf(Connection.class)));
        Connection resultConn = (Connection) obj;
        assertThat(resultConn, is(sameInstance(conn)));
    }

}
