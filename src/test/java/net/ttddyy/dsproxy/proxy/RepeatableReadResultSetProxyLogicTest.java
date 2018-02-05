package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.listener.CallCheckMethodExecutionListener;
import net.ttddyy.dsproxy.listener.MethodExecutionContext;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RepeatableReadResultSetProxyLogicTest {

    private static final int NUMBER_OF_COLUMNS = 3;
    private static final String COLUMN_1_LABEL = "FIRST";
    private static final String COLUMN_2_LABEL = "SECOND";
    private static final String COLUMN_3_LABEL = "third";
    private static final String COLUMN_1_VALUE = "result1";
    private static final Integer COLUMN_2_VALUE = 999;
    private static final Timestamp COLUMN_3_VALUE = new Timestamp(2312413L);

    @Test
    public void unsupportedMethodsThrowUnsupportedOperationException() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        final RepeatableReadResultSetProxyLogic resultSetProxyLogic = createProxyLogic(resultSet);

        final Method getCursorName = ResultSet.class.getMethod("getCursorName");

        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                resultSetProxyLogic.invoke(getCursorName, null);
            }
        }).isInstanceOf(UnsupportedOperationException.class).hasMessage("Method 'public abstract java.lang.String java.sql.ResultSet.getCursorName() throws java.sql.SQLException' is not supported by this proxy");
    }

    @Test
    public void getTargetReturnsTheResultSetFromTheTarget() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        RepeatableReadResultSetProxyLogic resultSetProxyLogic = createProxyLogic(resultSet);

        Method getTarget = ProxyJdbcObject.class.getMethod("getTarget");

        Object result = resultSetProxyLogic.invoke(getTarget, null);

        assertThat(result).isSameAs(resultSet);
    }

    @Test
    public void getResultSetMetaDataReturnsTheResultSetMetaDataFromTheTarget() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        RepeatableReadResultSetProxyLogic resultSetProxyLogic = createProxyLogic(resultSet);

        Method getMetaData = ResultSet.class.getMethod("getMetaData");

        Object result = resultSetProxyLogic.invoke(getMetaData, null);

        assertThat(result).isSameAs(resultSet.getMetaData());
    }

    @Test
    public void closeCallsCloseOnTheTarget() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        RepeatableReadResultSetProxyLogic resultSetProxyLogic = createProxyLogic(resultSet);

        invokeClose(resultSetProxyLogic);

        verify(resultSet).close();
    }

    @Test
    public void getColumnOnResultSetThatHasBeenConsumedTwiceThrowsException() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        final RepeatableReadResultSetProxyLogic resultSetProxyLogic = createProxyLogic(resultSet);

        consumeResultSetAndCallBeforeFirst(resultSet, resultSetProxyLogic);
        consumeResultSet(resultSet, resultSetProxyLogic);

        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                RepeatableReadResultSetProxyLogicTest.this.invokeGetString(resultSetProxyLogic, 1);
            }
        }).isInstanceOf(SQLException.class).hasMessage("Result set exhausted. There were 2 result(s) only");
    }

    @Test
    public void getColumnOnClosedResultSetThatHasBeenConsumedOnceThrowsException() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        final RepeatableReadResultSetProxyLogic resultSetProxyLogic = createProxyLogic(resultSet);

        consumeResultSetAndCallBeforeFirst(resultSet, resultSetProxyLogic);
        invokeClose(resultSetProxyLogic);

        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                RepeatableReadResultSetProxyLogicTest.this.invokeGetString(resultSetProxyLogic, 1);
            }
        }).isInstanceOf(SQLException.class).hasMessage("Already closed");
    }

    @Test
    public void nextOnUnconsumedResultSetThatHasMoreResultsDelegatesToTheTarget() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        RepeatableReadResultSetProxyLogic resultSetProxyLogic = createProxyLogic(resultSet);
        when(resultSet.next()).thenReturn(true);

        boolean result = invokeNext(resultSetProxyLogic);

        assertThat(result).isEqualTo(true);
    }

    @Test
    public void nextOnUnconsumedResultThatHasNoMoreResultsSetDelegatesToTheTarget() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        RepeatableReadResultSetProxyLogic resultSetProxyLogic = createProxyLogic(resultSet);
        when(resultSet.next()).thenReturn(false);

        boolean result = invokeNext(resultSetProxyLogic);

        assertThat(result).isEqualTo(false);
    }

    @Test
    public void getColumnByIndexOnUnconsumedResultSetThatHasMoreResultsDelegatesToTheTarget() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        RepeatableReadResultSetProxyLogic resultSetProxyLogic = createProxyLogic(resultSet);
        when(resultSet.next()).thenReturn(true);
        invokeNext(resultSetProxyLogic);

        int columnIndex = 1;
        String columnValue = "result";
        when(resultSet.getString(columnIndex)).thenReturn(columnValue);

        String result = invokeGetString(resultSetProxyLogic, columnIndex);

        assertThat(result).isEqualTo(columnValue);
    }

    @Test
    public void getColumnByLabelOnUnconsumedResultSetThatHasMoreResultsDelegatesToTheTarget() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        RepeatableReadResultSetProxyLogic resultSetProxyLogic = createProxyLogic(resultSet);
        when(resultSet.next()).thenReturn(true);
        invokeNext(resultSetProxyLogic);

        when(resultSet.getString(COLUMN_1_LABEL)).thenReturn(COLUMN_1_VALUE);

        String result = invokeGetString(resultSetProxyLogic, COLUMN_1_LABEL);

        assertThat(result).isEqualTo(COLUMN_1_VALUE);
    }

    @Test
    public void getColumnByIndexOnConsumedResultSetBeforeCallingNextThrowsSQLException() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        final RepeatableReadResultSetProxyLogic resultSetProxyLogic = createProxyLogic(resultSet);

        consumeResultSetAndCallBeforeFirst(resultSet, resultSetProxyLogic);

        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                RepeatableReadResultSetProxyLogicTest.this.invokeGetString(resultSetProxyLogic, 1);
            }
        }).isInstanceOf(SQLException.class).hasMessage("Result set not advanced. Call next before any get method!");
    }

    @Test
    public void getColumnByIndexOnConsumedResultSetThatHasMoreResultsReturnsTheResultThatTheTargetDidTheFirstTime() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        RepeatableReadResultSetProxyLogic resultSetProxyLogic = createProxyLogic(resultSet);

        consumeResultSetAndCallBeforeFirst(resultSet, resultSetProxyLogic);
        invokeNext(resultSetProxyLogic);

        String result = invokeGetString(resultSetProxyLogic, 1);

        assertThat(result).isEqualTo(COLUMN_1_VALUE);
    }

    @Test
    public void getColumnByNotExplicitelyConsumedIndexOnConsumedResultSetThatHasMoreResultsReturnsTheResultThatTheTargetDidTheFirstTime() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        RepeatableReadResultSetProxyLogic resultSetProxyLogic = createProxyLogic(resultSet);

        when(resultSet.next()).thenReturn(true, true, false);

        when(resultSet.getObject(1)).thenReturn(COLUMN_1_VALUE);

        assertThat(invokeNext(resultSetProxyLogic)).isTrue();

        invokeBeforeFirst(resultSetProxyLogic);
        invokeNext(resultSetProxyLogic);

        String result = invokeGetString(resultSetProxyLogic, 1);

        assertThat(result).isEqualTo(COLUMN_1_VALUE);
    }

    @Test
    public void getColumnByLabelOnConsumedResultSetThatHasMoreResultsReturnsTheResultThatTheTargetDidTheFirstTime() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        RepeatableReadResultSetProxyLogic resultSetProxyLogic = createProxyLogic(resultSet);

        consumeResultSetAndCallBeforeFirst(resultSet, resultSetProxyLogic);
        invokeNext(resultSetProxyLogic);

        String result = invokeGetString(resultSetProxyLogic, COLUMN_1_LABEL);

        assertThat(result).isEqualTo(COLUMN_1_VALUE);
    }

    @Test
    public void getColumnByLabelOnConsumedResultSetWithUnknownLabelThrowsIllegalArgumentException() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        final RepeatableReadResultSetProxyLogic resultSetProxyLogic = createProxyLogic(resultSet);

        consumeResultSetAndCallBeforeFirst(resultSet, resultSetProxyLogic);
        invokeNext(resultSetProxyLogic);

        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                invokeGetString(resultSetProxyLogic, "bad");
            }
        }).isInstanceOf(SQLException.class).hasMessage("Unknown column name 'bad'");
    }

    private RepeatableReadResultSetProxyLogic createProxyLogic(ResultSet resultSet) {
        RepeatableReadResultSetProxyLogicFactory factory = new RepeatableReadResultSetProxyLogicFactory();
        return (RepeatableReadResultSetProxyLogic) factory.create(resultSet, new ConnectionInfo(), ProxyConfig.Builder.create().build());
    }

    private void consumeResultSetAndCallBeforeFirst(ResultSet resultSet, RepeatableReadResultSetProxyLogic resultSetProxyLogic) throws Throwable {
        consumeResultSet(resultSet, resultSetProxyLogic);
        invokeBeforeFirst(resultSetProxyLogic);
    }

    private void consumeResultSet(ResultSet resultSet, RepeatableReadResultSetProxyLogic resultSetProxyLogic) throws Throwable {
        when(resultSet.next()).thenReturn(true, true, false);

        when(resultSet.getString(1)).thenReturn(COLUMN_1_VALUE);
        when(resultSet.getInt(2)).thenReturn(COLUMN_2_VALUE);
        when(resultSet.getTimestamp(3)).thenReturn(COLUMN_3_VALUE);
        when(resultSet.getObject(1)).thenReturn(COLUMN_1_VALUE);
        when(resultSet.getObject(2)).thenReturn(COLUMN_2_VALUE);
        when(resultSet.getObject(3)).thenReturn(COLUMN_3_VALUE);

        when(resultSet.getString(COLUMN_1_LABEL)).thenReturn(COLUMN_1_VALUE);
        when(resultSet.getInt(COLUMN_2_LABEL)).thenReturn(COLUMN_2_VALUE);
        when(resultSet.getTimestamp(COLUMN_3_LABEL)).thenReturn(COLUMN_3_VALUE);
        when(resultSet.getObject(COLUMN_1_LABEL)).thenReturn(COLUMN_1_VALUE);
        when(resultSet.getObject(COLUMN_2_LABEL)).thenReturn(COLUMN_2_VALUE);
        when(resultSet.getObject(COLUMN_3_LABEL)).thenReturn(COLUMN_3_VALUE);

        assertThat(invokeNext(resultSetProxyLogic)).isTrue();
        assertThat(invokeGetString(resultSetProxyLogic, COLUMN_1_LABEL)).isEqualTo(COLUMN_1_VALUE);
        assertThat(invokeGetInt(resultSetProxyLogic, 2)).isEqualTo(COLUMN_2_VALUE);
        assertThat(invokeGetTimestamp(resultSetProxyLogic, COLUMN_3_LABEL)).isEqualTo(COLUMN_3_VALUE);

        assertThat(invokeNext(resultSetProxyLogic)).isTrue();
        assertThat(invokeGetString(resultSetProxyLogic, 1)).isEqualTo(COLUMN_1_VALUE);
        assertThat(invokeGetInt(resultSetProxyLogic, COLUMN_2_LABEL)).isEqualTo(COLUMN_2_VALUE);
        assertThat(invokeGetTimestamp(resultSetProxyLogic, 3)).isEqualTo(COLUMN_3_VALUE);

        assertThat(invokeNext(resultSetProxyLogic)).isFalse();
    }

    private void invokeClose(RepeatableReadResultSetProxyLogic resultSetProxyLogic) throws Throwable {
        Method next = ResultSet.class.getMethod("close");
        resultSetProxyLogic.invoke(next, null);
    }

    private void invokeBeforeFirst(RepeatableReadResultSetProxyLogic resultSetProxyLogic) throws Throwable {
        Method beforeFirst = ResultSet.class.getMethod("beforeFirst");
        resultSetProxyLogic.invoke(beforeFirst, null);
    }

    private boolean invokeNext(RepeatableReadResultSetProxyLogic resultSetProxyLogic) throws Throwable {
        Method next = ResultSet.class.getMethod("next");
        return (Boolean) resultSetProxyLogic.invoke(next, null);
    }

    private String invokeGetString(RepeatableReadResultSetProxyLogic resultSetProxyLogic, int columnIndex) throws Throwable {
        Method getString = ResultSet.class.getMethod("getString", int.class);
        return (String) resultSetProxyLogic.invoke(getString, new Object[]{columnIndex});
    }

    private int invokeGetInt(RepeatableReadResultSetProxyLogic resultSetProxyLogic, int columnIndex) throws Throwable {
        Method getInt = ResultSet.class.getMethod("getInt", int.class);
        return (Integer) resultSetProxyLogic.invoke(getInt, new Object[]{columnIndex});
    }

    private Timestamp invokeGetTimestamp(RepeatableReadResultSetProxyLogic resultSetProxyLogic, int columnIndex) throws Throwable {
        Method getTimestamp = ResultSet.class.getMethod("getTimestamp", int.class);
        return (Timestamp) resultSetProxyLogic.invoke(getTimestamp, new Object[]{columnIndex});
    }

    private String invokeGetString(RepeatableReadResultSetProxyLogic resultSetProxyLogic, String columnLabel) throws Throwable {
        Method getString = ResultSet.class.getMethod("getString", String.class);
        return (String) resultSetProxyLogic.invoke(getString, new Object[]{columnLabel});
    }

    private int invokeGetInt(RepeatableReadResultSetProxyLogic resultSetProxyLogic, String columnLabel) throws Throwable {
        Method getInt = ResultSet.class.getMethod("getInt", String.class);
        return (Integer) resultSetProxyLogic.invoke(getInt, new Object[]{columnLabel});
    }

    private Timestamp invokeGetTimestamp(RepeatableReadResultSetProxyLogic resultSetProxyLogic, String columnLabel) throws Throwable {
        Method getTimestamp = ResultSet.class.getMethod("getTimestamp", String.class);
        return (Timestamp) resultSetProxyLogic.invoke(getTimestamp, new Object[]{columnLabel});
    }

    private ResultSet exampleResultSet() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        ResultSetMetaData resultSetMetaData = exampleResultSetMetaData();
        when(resultSet.getMetaData()).thenReturn(resultSetMetaData);
        return resultSet;
    }

    private ResultSetMetaData exampleResultSetMetaData() throws SQLException {
        ResultSetMetaData metaData = mock(ResultSetMetaData.class);
        when(metaData.getColumnCount()).thenReturn(NUMBER_OF_COLUMNS);
        when(metaData.getColumnLabel(1)).thenReturn(COLUMN_1_LABEL);
        when(metaData.getColumnLabel(2)).thenReturn(COLUMN_2_LABEL);
        when(metaData.getColumnLabel(3)).thenReturn(COLUMN_3_LABEL);
        return metaData;
    }

    @Test
    public void testToString() throws Throwable {

        ResultSet rs = exampleResultSet();
        RepeatableReadResultSetProxyLogic logic = createProxyLogic(rs);

        when(rs.toString()).thenReturn("my rs");

        Method method = Object.class.getMethod("toString");
        Object result = logic.invoke(method, null);

        assertThat(result).isInstanceOf(String.class).isEqualTo(rs.getClass().getSimpleName() + " [my rs]");
    }

    @Test
    public void testHashCode() throws Throwable {
        ResultSet rs = exampleResultSet();
        RepeatableReadResultSetProxyLogic logic = createProxyLogic(rs);

        Method method = Object.class.getMethod("hashCode");
        Object result = logic.invoke(method, null);

        assertThat(result).isInstanceOf(Integer.class).isEqualTo(rs.hashCode());
    }

    @Test
    public void testEquals() throws Throwable {
        ResultSet rs = exampleResultSet();
        RepeatableReadResultSetProxyLogic logic = createProxyLogic(rs);

        Method method = Object.class.getMethod("equals", Object.class);

        // equals(null)
        Object result = logic.invoke(method, new Object[]{null});
        assertThat(result).isEqualTo(false);

        // equals(true)
        result = logic.invoke(method, new Object[]{rs});
        assertThat(result).isEqualTo(true);
    }

    @Test
    public void methodExecutionListener() throws Throwable {
        CallCheckMethodExecutionListener listener = new CallCheckMethodExecutionListener();
        ProxyConfig proxyConfig = ProxyConfig.Builder.create().methodListener(listener).build();
        ResultSet rs = mock(ResultSet.class);
        ResultSetMetaData metaData = mock(ResultSetMetaData.class);
        when(rs.getMetaData()).thenReturn(metaData);
        ConnectionInfo connectionInfo = new ConnectionInfo();

        RepeatableReadResultSetProxyLogicFactory factory = new RepeatableReadResultSetProxyLogicFactory();
        RepeatableReadResultSetProxyLogic logic = (RepeatableReadResultSetProxyLogic) factory.create(rs, connectionInfo, proxyConfig);

        Method method = ResultSet.class.getMethod("close");
        logic.invoke(method, new Object[]{});

        assertTrue(listener.isBeforeMethodCalled());
        assertTrue(listener.isAfterMethodCalled());

        MethodExecutionContext executionContext = listener.getAfterMethodContext();
        assertSame("method should come from interface",
                ResultSet.class, executionContext.getMethod().getDeclaringClass());
        assertSame("close", executionContext.getMethod().getName());
        assertSame(rs, executionContext.getTarget());
        assertSame(connectionInfo, executionContext.getConnectionInfo());
    }

}