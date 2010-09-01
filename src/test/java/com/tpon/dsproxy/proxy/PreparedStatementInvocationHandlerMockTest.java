package com.tpon.dsproxy.proxy;

import com.tpon.dsproxy.ExecutionInfo;
import com.tpon.dsproxy.QueryInfo;
import com.tpon.dsproxy.listener.QueryExecutionListener;
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
import org.testng.annotations.Test;
import static org.testng.Assert.assertTrue;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.sql.Connection;
import java.util.List;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;

/**
 * @author Tadaya Tsuyukubo
 */
public class PreparedStatementInvocationHandlerMockTest {

    private static final String DS_NAME = "myDS";

    @Test
    public void testExecuteQuery() throws Exception {
        final String query = "select * from emp where id = ?";

        PreparedStatement stat = mock(PreparedStatement.class);
        QueryExecutionListener listener = mock(QueryExecutionListener.class);

        PreparedStatement statement = getProxyStatement(stat, query, listener);


        Array array = mock(Array.class);
        InputStream inputStream = mock(InputStream.class);
        BigDecimal bigDecimal = mock(BigDecimal.class);
        InputStream binaryStream = mock(InputStream.class);
        Blob blob = mock(Blob.class);
        boolean booleanValue = true;
        Reader reader = mock(Reader.class);
        Clob clob = mock(Clob.class);
        Date date = mock(Date.class);
        double doubleValue = 10.0d;
        Float floatValue = 20f;
        Integer intvalue = 30;
        Long longValue = 40L;
        Object object = mock(Object.class);
        Ref ref = mock(Ref.class);
        Short shortValue = 50;
        String stringValue = "str";
        Time time = mock(Time.class);
        Timestamp timestamp = mock(Timestamp.class);
        URL url = new URL("http://foo.com");


        statement.setArray(1, array);
        statement.setAsciiStream(2, inputStream);
        statement.setBigDecimal(3, bigDecimal);
        statement.setBinaryStream(4, binaryStream);
        statement.setBlob(5, blob);
        statement.setBoolean(6, booleanValue);
        statement.setCharacterStream(7, reader);
        statement.setClob(8, clob);
        statement.setDate(9, date);
        statement.setDouble(10, doubleValue);
        statement.setFloat(11, floatValue);
        statement.setInt(12, intvalue);
        statement.setLong(13, longValue);
        statement.setNull(14, Types.VARCHAR);
        statement.setObject(15, object);
        statement.setRef(16, ref);
        statement.setShort(17, shortValue);
        statement.setString(18, stringValue);
        statement.setTime(19, time);
        statement.setTimestamp(20, timestamp);
        statement.setURL(21, url);

        statement.executeQuery();

        verify(stat).setArray(1, array);
        verify(stat).setAsciiStream(2, inputStream);
        verify(stat).setBigDecimal(3, bigDecimal);
        verify(stat).setBinaryStream(4, binaryStream);
        verify(stat).setBlob(5, blob);
        verify(stat).setBoolean(6, booleanValue);
        verify(stat).setCharacterStream(7, reader);
        verify(stat).setClob(8, clob);
        verify(stat).setDate(9, date);
        verify(stat).setDouble(10, doubleValue);
        verify(stat).setFloat(11, floatValue);
        verify(stat).setInt(12, intvalue);
        verify(stat).setLong(13, longValue);
        verify(stat).setNull(14, Types.VARCHAR);
        verify(stat).setObject(15, object);
        verify(stat).setRef(16, ref);
        verify(stat).setShort(17, shortValue);
        verify(stat).setString(18, stringValue);
        verify(stat).setTime(19, time);
        verify(stat).setTimestamp(20, timestamp);
        verify(stat).setURL(21, url);
        verify(stat).executeQuery();

        verifyListener(listener, "executeQuery", query, array, inputStream, bigDecimal, binaryStream, blob,
                booleanValue, reader, clob, date, doubleValue, floatValue, intvalue, longValue, Types.VARCHAR,
                object, ref, shortValue, stringValue, time, timestamp, url);

    }

    private PreparedStatement getProxyStatement(PreparedStatement statement, String query, QueryExecutionListener listener) {
        return JdbcProxyFactory.createPreparedStatement(statement, query, listener, DS_NAME);
    }

    @SuppressWarnings("unchecked")
    private void verifyListener(QueryExecutionListener listener, String methodName, String query, Object... expectedQueryArgs) {
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
        assertThat(queryArgs.size(), is(expectedQueryArgs.length));

        for (int i = 0; i < queryArgs.size(); i++) {
            Object value = queryArgs.get(i);
            Object expected = expectedQueryArgs[i];

            assertThat(value, is(expected));
        }
    }

    @Test
    public void testBatch() throws Exception {
        final String query = "update emp set name = ? where id = ?";

        PreparedStatement stat = mock(PreparedStatement.class);
        when(stat.executeBatch()).thenReturn(new int[]{1, 1, 1});

        QueryExecutionListener listener = mock(QueryExecutionListener.class);

        PreparedStatement statement = getProxyStatement(stat, query, listener);
        statement.setString(1, "foo");
        statement.setInt(2, 10);
        statement.addBatch();

        statement.setString(1, "bar");
        statement.setInt(2, 20);
        statement.addBatch();

        statement.setString(1, "baz");
        statement.setInt(2, 30);
        statement.addBatch();

        int[] result = statement.executeBatch();

        assertThat(result.length, is(3));
        assertThat(result[0], is(1));
        assertThat(result[1], is(1));
        assertThat(result[2], is(1));

        verify(stat).setString(1, "foo");
        verify(stat).setInt(2, 10);
        verify(stat).setString(1, "bar");
        verify(stat).setInt(2, 20);
        verify(stat).setString(1, "baz");
        verify(stat).setInt(2, 30);
        verify(stat, times(3)).addBatch();


        MockTestUtils.verifyListenerForBatch(listener, DS_NAME, query, new Object[][]{{"foo", 10}, {"bar", 20}, {"baz", 30}});

    }

    @Test
    public void testBatchWithClearBatch() throws Exception {
        final String query = "update emp set name = ? where id = ?";

        PreparedStatement stat = mock(PreparedStatement.class);
        when(stat.executeBatch()).thenReturn(new int[]{1});

        QueryExecutionListener listener = mock(QueryExecutionListener.class);

        PreparedStatement statement = getProxyStatement(stat, query, listener);
        statement.setString(1, "foo");
        statement.setInt(2, 10);
        statement.addBatch();

        statement.clearBatch();

        statement.setString(1, "FOO");
        statement.setInt(2, 20);
        statement.addBatch();

        int[] result = statement.executeBatch();

        assertThat(result.length, is(1));
        assertThat(result[0], is(1));

        verify(stat).setString(1, "foo");
        verify(stat).setInt(2, 10);
        verify(stat).clearBatch();
        verify(stat).setString(1, "FOO");
        verify(stat).setInt(2, 20);
        verify(stat, times(2)).addBatch();

        MockTestUtils.verifyListenerForBatch(listener, DS_NAME, query, new Object[][]{{"FOO", 20}});

    }

    @Test
    public void testBatchWithClearParameeters() throws Exception {
        final String query = "update emp set name = ? where id = ?";

        PreparedStatement stat = mock(PreparedStatement.class);
        when(stat.executeBatch()).thenReturn(new int[]{1});

        QueryExecutionListener listener = mock(QueryExecutionListener.class);

        PreparedStatement statement = getProxyStatement(stat, query, listener);
        statement.setString(1, "foo");

        statement.clearParameters();

        statement.setString(1, "FOO");

        statement.setInt(2, 10);
        statement.addBatch();


        int[] result = statement.executeBatch();

        assertThat(result.length, is(1));
        assertThat(result[0], is(1));

        verify(stat).setString(1, "foo");
        verify(stat).clearParameters();
        verify(stat).setString(1, "FOO");
        verify(stat).setInt(2, 10);
        verify(stat).addBatch();

        MockTestUtils.verifyListenerForBatch(listener, DS_NAME, query, new Object[][]{{"FOO", 10}});

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testClearParameeters() throws Exception {
        final String query = "update emp set name = ? where id = ?";

        PreparedStatement stat = mock(PreparedStatement.class);

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


    @Test
    public void testGetTarget() {
        PreparedStatement orig = mock(PreparedStatement.class);
        PreparedStatement proxy = getProxyStatement(orig, null, null);

        assertThat(proxy, is(not(sameInstance(orig))));
        assertThat(proxy, is(instanceOf(ProxyJdbcObject.class)));

        Object result = ((ProxyJdbcObject) proxy).getTarget();

        assertThat(result, is(instanceOf(PreparedStatement.class)));

        PreparedStatement resultStmt = (PreparedStatement) result;

        assertThat(resultStmt, is(sameInstance(orig)));
    }

    @Test
    public void testUnwrap() throws Exception {
        PreparedStatement mock = mock(PreparedStatement.class);
        when(mock.unwrap(String.class)).thenReturn("called");

        PreparedStatement ps = getProxyStatement(mock, null, null);

        String result = ps.unwrap(String.class);

        verify(mock).unwrap(String.class);
        assertThat(result, is("called"));
    }

    @Test
    public void testIsWrapperFor() throws Exception {
        PreparedStatement mock = mock(PreparedStatement.class);
        when(mock.isWrapperFor(String.class)).thenReturn(true);

        PreparedStatement ps = getProxyStatement(mock, null, null);

        boolean result = ps.isWrapperFor(String.class);

        verify(mock).isWrapperFor(String.class);
        assertThat(result, is(true));
    }

    @Test
    public void testGetConnection() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement stat = mock(PreparedStatement.class);

        when(stat.getConnection()).thenReturn(conn);
        PreparedStatement statement = getProxyStatement(stat, null, null);

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
