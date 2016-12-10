package net.ttddyy.dsproxy.proxy;

import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class ResultSetProxyLogicTest {

    private static final int NUMBER_OF_COLUMNS = 3;
    private static final String COLUMN_1_LABEL = "FIRST";
    private static final String COLUMN_2_LABEL = "SECOND";
    private static final String COLUMN_3_LABEL = "THIRD";
    private static final String COLUMN_1_VALUE = "result1";
    private static final Integer COLUMN_2_VALUE = 999;
    private static final Timestamp COLUMN_3_VALUE = new Timestamp(2312413L);

    @Test
    public void unsupportedMethodsThrowUnsupportedOperationException() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        final ResultSetProxyLogic resultSetProxyLogic = ResultSetProxyLogic.resultSetProxyLogic(resultSet);

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
        ResultSetProxyLogic resultSetProxyLogic = ResultSetProxyLogic.resultSetProxyLogic(resultSet);

        Method getTarget = ProxyJdbcObject.class.getMethod("getTarget");

        Object result = resultSetProxyLogic.invoke(getTarget, null);

        assertThat(result).isSameAs(resultSet);
    }

    @Test
    public void getResultSetMetaDataReturnsTheResultSetMetaDataFromTheTarget() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        ResultSetProxyLogic resultSetProxyLogic = ResultSetProxyLogic.resultSetProxyLogic(resultSet);

        Method getMetaData = ResultSet.class.getMethod("getMetaData");

        Object result = resultSetProxyLogic.invoke(getMetaData, null);

        assertThat(result).isSameAs(resultSet.getMetaData());
    }

    @Test
    public void closeCallsCloseOnTheTarget() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        ResultSetProxyLogic resultSetProxyLogic = ResultSetProxyLogic.resultSetProxyLogic(resultSet);

        invokeClose(resultSetProxyLogic);

        verify(resultSet).close();
    }

    @Test
    public void getColumnOnResultSetThatHasBeenConsumedTwiceThrowsException() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        final ResultSetProxyLogic resultSetProxyLogic = ResultSetProxyLogic.resultSetProxyLogic(resultSet);

        consumeResultSetAndCallBeforeFirst(resultSet, resultSetProxyLogic);
        consumeResultSet(resultSet, resultSetProxyLogic);

        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                ResultSetProxyLogicTest.this.invokeGetString(resultSetProxyLogic, 1);
            }
        }).isInstanceOf(SQLException.class).hasMessage("Result set exhausted. There were 2 result(s) only");
    }

    @Test
    public void getColumnOnClosedResultSetThatHasBeenConsumedOnceThrowsException() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        final ResultSetProxyLogic resultSetProxyLogic = ResultSetProxyLogic.resultSetProxyLogic(resultSet);

        consumeResultSetAndCallBeforeFirst(resultSet, resultSetProxyLogic);
        invokeClose(resultSetProxyLogic);

        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                ResultSetProxyLogicTest.this.invokeGetString(resultSetProxyLogic, 1);
            }
        }).isInstanceOf(SQLException.class).hasMessage("Already closed");
    }

    @Test
    public void nextOnUnconsumedResultSetThatHasMoreResultsDelegatesToTheTarget() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        ResultSetProxyLogic resultSetProxyLogic = ResultSetProxyLogic.resultSetProxyLogic(resultSet);
        when(resultSet.next()).thenReturn(true);

        boolean result = invokeNext(resultSetProxyLogic);

        assertThat(result).isEqualTo(true);
    }

    @Test
    public void nextOnUnconsumedResultThatHasNoMoreResultsSetDelegatesToTheTarget() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        ResultSetProxyLogic resultSetProxyLogic = ResultSetProxyLogic.resultSetProxyLogic(resultSet);
        when(resultSet.next()).thenReturn(false);

        boolean result = invokeNext(resultSetProxyLogic);

        assertThat(result).isEqualTo(false);
    }

    @Test
    public void getColumnByIndexOnUnconsumedResultSetThatHasMoreResultsDelegatesToTheTarget() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        ResultSetProxyLogic resultSetProxyLogic = ResultSetProxyLogic.resultSetProxyLogic(resultSet);
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
        ResultSetProxyLogic resultSetProxyLogic = ResultSetProxyLogic.resultSetProxyLogic(resultSet);
        when(resultSet.next()).thenReturn(true);
        invokeNext(resultSetProxyLogic);

        when(resultSet.getString(COLUMN_1_LABEL)).thenReturn(COLUMN_1_VALUE);

        String result = invokeGetString(resultSetProxyLogic, COLUMN_1_LABEL);

        assertThat(result).isEqualTo(COLUMN_1_VALUE);
    }

    @Test
    public void getColumnByIndexOnConsumedResultSetBeforeCallingNextThrowsSQLException() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        final ResultSetProxyLogic resultSetProxyLogic = ResultSetProxyLogic.resultSetProxyLogic(resultSet);

        consumeResultSetAndCallBeforeFirst(resultSet, resultSetProxyLogic);

        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                ResultSetProxyLogicTest.this.invokeGetString(resultSetProxyLogic, 1);
            }
        }).isInstanceOf(SQLException.class).hasMessage("Result set not advanced. Call next before any get method!");
    }

    @Test
    public void getColumnByIndexOnConsumedResultSetThatHasMoreResultsReturnsTheResultThatTheTargetDidTheFirstTime() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        ResultSetProxyLogic resultSetProxyLogic = ResultSetProxyLogic.resultSetProxyLogic(resultSet);

        consumeResultSetAndCallBeforeFirst(resultSet, resultSetProxyLogic);
        invokeNext(resultSetProxyLogic);

        String result = invokeGetString(resultSetProxyLogic, 1);

        assertThat(result).isEqualTo(COLUMN_1_VALUE);
    }

    @Test
    public void getColumnByLabelOnConsumedResultSetThatHasMoreResultsReturnsTheResultThatTheTargetDidTheFirstTime() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        ResultSetProxyLogic resultSetProxyLogic = ResultSetProxyLogic.resultSetProxyLogic(resultSet);

        consumeResultSetAndCallBeforeFirst(resultSet, resultSetProxyLogic);
        invokeNext(resultSetProxyLogic);

        String result = invokeGetString(resultSetProxyLogic, COLUMN_1_LABEL);

        assertThat(result).isEqualTo(COLUMN_1_VALUE);
    }

    @Test
    public void getColumnByLabelOnConsumedResultSetWithUnknownLabelThrowsIllegalArgumentException() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        final ResultSetProxyLogic resultSetProxyLogic = ResultSetProxyLogic.resultSetProxyLogic(resultSet);

        consumeResultSetAndCallBeforeFirst(resultSet, resultSetProxyLogic);
        invokeNext(resultSetProxyLogic);

        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                invokeGetString(resultSetProxyLogic, "bad");
            }
        }).isInstanceOf(SQLException.class).hasMessage("Unknown column name 'bad'");
    }

    private void consumeResultSetAndCallBeforeFirst(ResultSet resultSet, ResultSetProxyLogic resultSetProxyLogic) throws Throwable {
        consumeResultSet(resultSet, resultSetProxyLogic);
        invokeBeforeFirst(resultSetProxyLogic);
    }

    private void consumeResultSet(ResultSet resultSet, ResultSetProxyLogic resultSetProxyLogic) throws Throwable {
        when(resultSet.next()).thenReturn(true, true, false);

        when(resultSet.getString(1)).thenReturn(COLUMN_1_VALUE);
        when(resultSet.getInt(2)).thenReturn(COLUMN_2_VALUE);
        when(resultSet.getTimestamp(3)).thenReturn(COLUMN_3_VALUE);

        when(resultSet.getString(COLUMN_1_LABEL)).thenReturn(COLUMN_1_VALUE);
        when(resultSet.getInt(COLUMN_2_LABEL)).thenReturn(COLUMN_2_VALUE);
        when(resultSet.getTimestamp(COLUMN_3_LABEL)).thenReturn(COLUMN_3_VALUE);

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

    private void invokeClose(ResultSetProxyLogic resultSetProxyLogic) throws Throwable {
        Method next = ResultSet.class.getMethod("close");
        resultSetProxyLogic.invoke(next, null);
    }

    private void invokeBeforeFirst(ResultSetProxyLogic resultSetProxyLogic) throws Throwable {
        Method beforeFirst = ResultSet.class.getMethod("beforeFirst");
        resultSetProxyLogic.invoke(beforeFirst, null);
    }

    private boolean invokeNext(ResultSetProxyLogic resultSetProxyLogic) throws Throwable {
        Method next = ResultSet.class.getMethod("next");
        return (Boolean) resultSetProxyLogic.invoke(next, null);
    }

    private String invokeGetString(ResultSetProxyLogic resultSetProxyLogic, int columnIndex) throws Throwable {
        Method getString = ResultSet.class.getMethod("getString", int.class);
        return (String) resultSetProxyLogic.invoke(getString, new Object[]{columnIndex});
    }

    private int invokeGetInt(ResultSetProxyLogic resultSetProxyLogic, int columnIndex) throws Throwable {
        Method getInt = ResultSet.class.getMethod("getInt", int.class);
        return (Integer) resultSetProxyLogic.invoke(getInt, new Object[]{columnIndex});
    }

    private Timestamp invokeGetTimestamp(ResultSetProxyLogic resultSetProxyLogic, int columnIndex) throws Throwable {
        Method getTimestamp = ResultSet.class.getMethod("getTimestamp", int.class);
        return (Timestamp) resultSetProxyLogic.invoke(getTimestamp, new Object[]{columnIndex});
    }

    private String invokeGetString(ResultSetProxyLogic resultSetProxyLogic, String columnLabel) throws Throwable {
        Method getString = ResultSet.class.getMethod("getString", String.class);
        return (String) resultSetProxyLogic.invoke(getString, new Object[]{columnLabel});
    }

    private int invokeGetInt(ResultSetProxyLogic resultSetProxyLogic, String columnLabel) throws Throwable {
        Method getInt = ResultSet.class.getMethod("getInt", String.class);
        return (Integer) resultSetProxyLogic.invoke(getInt, new Object[]{columnLabel});
    }

    private Timestamp invokeGetTimestamp(ResultSetProxyLogic resultSetProxyLogic, String columnLabel) throws Throwable {
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
}