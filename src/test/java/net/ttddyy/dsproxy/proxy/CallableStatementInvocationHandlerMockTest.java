package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import org.mockito.ArgumentCaptor;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;

/**
 * @author Tadaya Tsuyukubo
 */
public class CallableStatementInvocationHandlerMockTest {
    private static final String DS_NAME = "myDS";

    @Test
    public void testExecuteWithNoParam() throws Exception {
        final String query = "{call procedure_name}";

        CallableStatement stat = mock(CallableStatement.class);
        QueryExecutionListener listener = mock(QueryExecutionListener.class);

        when(stat.execute()).thenReturn(true);

        CallableStatement statement = getProxyStatement(stat, query, listener);

        boolean result = statement.execute();
        assertTrue(result);
        verifyListenerWithNoParam(listener, "execute", query);
    }

    @Test
    public void testExecuteWithParamByPosition() throws Exception {
        final String query = "{call procedure_name}";

        CallableStatement stat = mock(CallableStatement.class);
        QueryExecutionListener listener = mock(QueryExecutionListener.class);

        when(stat.execute()).thenReturn(true);

        CallableStatement statement = getProxyStatement(stat, query, listener);

        setParameterByPosition(statement);
        boolean result = statement.execute();
        assertTrue(result);
        verifyParametersByPosition(stat);

        verifyListenerWithParamByPosition(listener, "execute", query);
    }

    @Test
    public void testExecuteWithParamByName() throws Exception {
        final String query = "{call procedure_name}";

        CallableStatement stat = mock(CallableStatement.class);
        QueryExecutionListener listener = mock(QueryExecutionListener.class);

        when(stat.execute()).thenReturn(true);

        CallableStatement statement = getProxyStatement(stat, query, listener);

        setParameterByName(statement);
        boolean result = statement.execute();
        assertTrue(result);
        verifyParametersByName(stat);

        verifyListenerWithParamByName(listener, "execute", query);
    }

    @Test
    public void testExecuteUpdateWithNoParam() throws Exception {
        final String query = "{call procedure_name}";

        CallableStatement stat = mock(CallableStatement.class);
        QueryExecutionListener listener = mock(QueryExecutionListener.class);

        when(stat.executeUpdate()).thenReturn(100);

        CallableStatement statement = getProxyStatement(stat, query, listener);

        int result = statement.executeUpdate();
        assertThat(result, is(100));
        verifyListenerWithNoParam(listener, "executeUpdate", query);
    }

    @Test
    public void testExecuteUpdateWithParamByPosition() throws Exception {
        final String query = "{call procedure_name}";

        CallableStatement stat = mock(CallableStatement.class);
        QueryExecutionListener listener = mock(QueryExecutionListener.class);

        when(stat.executeUpdate()).thenReturn(100);

        CallableStatement statement = getProxyStatement(stat, query, listener);

        setParameterByPosition(statement);
        int result = statement.executeUpdate();
        assertThat(result, is(100));
        verifyParametersByPosition(stat);

        verifyListenerWithParamByPosition(listener, "executeUpdate", query);
    }

    @Test
    public void testExecuteUpdateWithParamByName() throws Exception {
        final String query = "{call procedure_name}";

        CallableStatement stat = mock(CallableStatement.class);
        QueryExecutionListener listener = mock(QueryExecutionListener.class);

        when(stat.executeUpdate()).thenReturn(100);

        CallableStatement statement = getProxyStatement(stat, query, listener);

        setParameterByName(statement);
        int result = statement.executeUpdate();
        assertThat(result, is(100));
        verifyParametersByName(stat);

        verifyListenerWithParamByName(listener, "executeUpdate", query);
    }

    @Test
    public void testExecuteQueryWithNoParam() throws Exception {
        final String query = "{call procedure_name}";

        CallableStatement stat = mock(CallableStatement.class);
        QueryExecutionListener listener = mock(QueryExecutionListener.class);

        ResultSet mockResultSet = mock(ResultSet.class);
        when(stat.executeQuery()).thenReturn(mockResultSet);

        CallableStatement statement = getProxyStatement(stat, query, listener);

        ResultSet result = statement.executeQuery();
        assertThat(result, is(mockResultSet));
        verifyListenerWithNoParam(listener, "executeQuery", query);
    }

    @Test
    public void testExecuteQueryWithParamByPosition() throws Exception {
        final String query = "{call procedure_name}";

        CallableStatement stat = mock(CallableStatement.class);
        QueryExecutionListener listener = mock(QueryExecutionListener.class);

        ResultSet mockResultSet = mock(ResultSet.class);
        when(stat.executeQuery()).thenReturn(mockResultSet);

        CallableStatement statement = getProxyStatement(stat, query, listener);

        setParameterByPosition(statement);
        ResultSet result = statement.executeQuery();
        assertThat(result, is(mockResultSet));
        verifyParametersByPosition(stat);

        verifyListenerWithParamByPosition(listener, "executeQuery", query);
    }

    @Test
    public void testExecuteQueryWithParamByName() throws Exception {
        final String query = "{call procedure_name}";

        CallableStatement stat = mock(CallableStatement.class);
        QueryExecutionListener listener = mock(QueryExecutionListener.class);

        ResultSet mockResultSet = mock(ResultSet.class);
        when(stat.executeQuery()).thenReturn(mockResultSet);

        CallableStatement statement = getProxyStatement(stat, query, listener);

        setParameterByName(statement);
        ResultSet result = statement.executeQuery();
        assertThat(result, is(mockResultSet));
        verifyParametersByName(stat);

        verifyListenerWithParamByName(listener, "executeQuery", query);
    }

    @Test
    public void testExecuteBatch() throws Exception {
        final String query = "{call procedure_a}";

        CallableStatement stat = mock(CallableStatement.class);
        QueryExecutionListener listener = mock(QueryExecutionListener.class);

        CallableStatement statement = getProxyStatement(stat, query, listener);

        statement.setString(1, "foo");
        statement.setInt(2, 10);
        statement.addBatch();

        statement.setString(1, "bar");
        statement.setInt(2, 20);
        statement.addBatch();

        statement.setString(1, "baz");
        statement.setInt(2, 30);
        statement.addBatch();

        statement.executeBatch();

        MockTestUtils.verifyListenerForBatch(listener, DS_NAME, query,
                new Object[][]{{"foo", 10}, {"bar", 20}, {"baz", 30}});
    }

    @Test
    public void testBatchWithClearBatch() throws Exception {
        final String query = "{call procedure_a}";

        CallableStatement stat = mock(CallableStatement.class);
        QueryExecutionListener listener = mock(QueryExecutionListener.class);

        PreparedStatement statement = getProxyStatement(stat, query, listener);
        statement.setString(1, "foo");
        statement.setInt(2, 10);
        statement.addBatch();

        statement.clearBatch();

        statement.setString(1, "FOO");
        statement.setInt(2, 20);
        statement.addBatch();

        statement.executeBatch();

        verify(stat).setString(1, "foo");
        verify(stat).setInt(2, 10);
        verify(stat).clearBatch();
        verify(stat).setString(1, "FOO");
        verify(stat).setInt(2, 20);
        verify(stat, times(2)).addBatch();

        MockTestUtils.verifyListenerForBatch(listener, DS_NAME, query, new Object[][]{{"FOO", 20}});
    }

    @Test
    public void testBatchWithClearParameters() throws Exception {
        final String query = "{call procedure_a}";

        CallableStatement stat = mock(CallableStatement.class);
        QueryExecutionListener listener = mock(QueryExecutionListener.class);

        PreparedStatement statement = getProxyStatement(stat, query, listener);
        statement.setString(1, "foo");

        statement.clearParameters();

        statement.setString(1, "FOO");

        statement.setInt(2, 10);
        statement.addBatch();


        statement.executeBatch();

        verify(stat).setString(1, "foo");
        verify(stat).clearParameters();
        verify(stat).setString(1, "FOO");
        verify(stat).setInt(2, 10);
        verify(stat).addBatch();

        MockTestUtils.verifyListenerForBatch(listener, DS_NAME, query, new Object[][]{{"FOO", 10}});

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testClearParameters() throws Exception {
        final String query = "{call procedure_a}";

        CallableStatement stat = mock(CallableStatement.class);
        QueryExecutionListener listener = mock(QueryExecutionListener.class);

        PreparedStatement statement = getProxyStatement(stat, query, listener);
        statement.setString(1, "foo");
        statement.setInt(2, 10);
        statement.clearParameters();
        statement.addBatch();

        statement.executeBatch();

        verify(stat).setString(1, "foo");
        verify(stat).setInt(2, 10);
        verify(stat).clearParameters();
        verify(stat).addBatch();

        ArgumentCaptor<List> queryInfoListCaptor = ArgumentCaptor.forClass(List.class);

        verify(listener).afterQuery(any(ExecutionInfo.class), queryInfoListCaptor.capture());

        List<QueryInfo> queryInfoList = queryInfoListCaptor.getValue();
        assertThat(queryInfoList.size(), is(1));

        assertThat(queryInfoList.get(0).getQueryArgs(), is(notNullValue()));
        assertThat(queryInfoList.get(0).getQueryArgs().size(), is(0));

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


    private void setParameterByPosition(CallableStatement statement) throws Exception {
        statement.setArray(PARAM_ARRAY.index, PARAM_ARRAY.value);
        statement.setAsciiStream(PARAM_ASCIISTREAM.index, PARAM_ASCIISTREAM.value);
        statement.setBigDecimal(PARAM_BIGDECIMAL.index, PARAM_BIGDECIMAL.value);
        statement.setBinaryStream(PARAM_BINARYSTREAM.index, PARAM_BINARYSTREAM.value);
        statement.setBlob(PARAM_BLOB.index, PARAM_BLOB.value);
        statement.setBoolean(PARAM_BOOLEAN.index, PARAM_BOOLEAN.value);
        statement.setCharacterStream(PARAM_CHARACTERSTREAM.index, PARAM_CHARACTERSTREAM.value);
        statement.setClob(PARAM_CLOB.index, PARAM_CLOB.value);
        statement.setDate(PARAM_DATE.index, PARAM_DATE.value);
        statement.setDouble(PARAM_DOUBLE.index, PARAM_DOUBLE.value);
        statement.setFloat(PARAM_FLOAT.index, PARAM_FLOAT.value);
        statement.setInt(PARAM_INT.index, PARAM_INT.value);
        statement.setLong(PARAM_LONG.index, PARAM_LONG.value);
        statement.setNull(PARAM_NULL.index, PARAM_NULL.value);
        statement.setObject(PARAM_OBJECT.index, PARAM_OBJECT.value);
        statement.setRef(PARAM_REF.index, PARAM_REF.value);
        statement.setShort(PARAM_SHORT.index, PARAM_SHORT.value);
        statement.setString(PARAM_STRING.index, PARAM_STRING.value);
        statement.setTime(PARAM_TIME.index, PARAM_TIME.value);
        statement.setTimestamp(PARAM_TIMESTAMP.index, PARAM_TIMESTAMP.value);
        statement.setURL(PARAM_URL.index, PARAM_URL.value);
    }

    private void setParameterByName(CallableStatement statement) throws Exception {
        statement.setAsciiStream(PARAM_ASCIISTREAM.strIndex, PARAM_ASCIISTREAM.value);
        statement.setBigDecimal(PARAM_BIGDECIMAL.strIndex, PARAM_BIGDECIMAL.value);
        statement.setBinaryStream(PARAM_BINARYSTREAM.strIndex, PARAM_BINARYSTREAM.value);
        statement.setBlob(PARAM_BLOB.strIndex, PARAM_BLOB.value);
        statement.setBoolean(PARAM_BOOLEAN.strIndex, PARAM_BOOLEAN.value);
        statement.setCharacterStream(PARAM_CHARACTERSTREAM.strIndex, PARAM_CHARACTERSTREAM.value);
        statement.setClob(PARAM_CLOB.strIndex, PARAM_CLOB.value);
        statement.setDate(PARAM_DATE.strIndex, PARAM_DATE.value);
        statement.setDouble(PARAM_DOUBLE.strIndex, PARAM_DOUBLE.value);
        statement.setFloat(PARAM_FLOAT.strIndex, PARAM_FLOAT.value);
        statement.setInt(PARAM_INT.strIndex, PARAM_INT.value);
        statement.setLong(PARAM_LONG.strIndex, PARAM_LONG.value);
        statement.setNull(PARAM_NULL.strIndex, PARAM_NULL.value);
        statement.setObject(PARAM_OBJECT.strIndex, PARAM_OBJECT.value);
        statement.setShort(PARAM_SHORT.strIndex, PARAM_SHORT.value);
        statement.setString(PARAM_STRING.strIndex, PARAM_STRING.value);
        statement.setTime(PARAM_TIME.strIndex, PARAM_TIME.value);
        statement.setTimestamp(PARAM_TIMESTAMP.strIndex, PARAM_TIMESTAMP.value);
        statement.setURL(PARAM_URL.strIndex, PARAM_URL.value);
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

        List<?> queryArgs = queryInfo.getQueryArgs();
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
                Collections.sort(paramsForStrIndex, new Comparator<Param>() {
                    public int compare(Param left, Param right) {
                        return left.strIndex.compareTo(right.strIndex);
                    }
                });


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


    private CallableStatement getProxyStatement(CallableStatement statement, String query,
                                                QueryExecutionListener listener) {
        return new JdkJdbcProxyFactory().createCallableStatement(statement, query, listener, DS_NAME);
    }

    @Test
    public void testGetTarget() {
        CallableStatement orig = mock(CallableStatement.class);
        CallableStatement proxy = getProxyStatement(orig, null, null);

        assertThat(proxy, is(not(sameInstance(orig))));
        assertThat(proxy, is(instanceOf(ProxyJdbcObject.class)));

        Object result = ((ProxyJdbcObject) proxy).getTarget();

        assertThat(result, is(instanceOf(CallableStatement.class)));

        CallableStatement resultStmt = (CallableStatement) result;

        assertThat(resultStmt, is(sameInstance(orig)));
    }

    @Test
    public void testUnwrap() throws Exception {
        CallableStatement mock = mock(CallableStatement.class);
        when(mock.unwrap(String.class)).thenReturn("called");

        CallableStatement cs = getProxyStatement(mock, null, null);

        String result = cs.unwrap(String.class);

        verify(mock).unwrap(String.class);
        assertThat(result, is("called"));
    }

    @Test
    public void testIsWrapperFor() throws Exception {
        CallableStatement mock = mock(CallableStatement.class);
        when(mock.isWrapperFor(String.class)).thenReturn(true);

        CallableStatement cs = getProxyStatement(mock, null, null);

        boolean result = cs.isWrapperFor(String.class);

        verify(mock).isWrapperFor(String.class);
        assertThat(result, is(true));
    }

    @Test
    public void testGetConnection() throws Exception {
        Connection conn = mock(Connection.class);
        CallableStatement stat = mock(CallableStatement.class);

        when(stat.getConnection()).thenReturn(conn);
        CallableStatement statement = getProxyStatement(stat, null, null);

        Connection result = statement.getConnection();

        verify(stat).getConnection();

        assertTrue(Proxy.isProxyClass(result.getClass()));

        InvocationHandler handler = Proxy.getInvocationHandler(result);
        assertThat(handler, is(instanceOf(ConnectionInvocationHandler.class)));

        assertThat(result, is(instanceOf(ProxyJdbcObject.class)));
        Object obj = ((ProxyJdbcObject) result).getTarget();

        assertThat(obj, is(instanceOf(Connection.class)));
        Connection resultConn = (Connection) obj;
        assertThat(resultConn, is(sameInstance(conn)));
    }

}
