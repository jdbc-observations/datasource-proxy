package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.TestUtils;
import net.ttddyy.dsproxy.listener.CallCheckMethodExecutionListener;
import net.ttddyy.dsproxy.listener.MethodExecutionContext;
import org.assertj.core.api.ThrowableAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// TODO: currently this is copy of RepeatableReadResultSetProxyLogicTest. Need refactoring.
public class CachedRowSetResultSetProxyLogicTest {

    private static final String COLUMN_1_LABEL = "id";
    private static final String COLUMN_2_LABEL = "name";
    private static final Integer COLUMN_1_ROW_1_VALUE = 1;
    private static final String COLUMN_2_ROW_1_VALUE = "foo";
    private static final Integer COLUMN_1_ROW_2_VALUE = 2;
    private static final String COLUMN_2_ROW_2_VALUE = "bar";

    private DataSource jdbcDataSource;

    @Before
    public void setUp() throws Exception {
        this.jdbcDataSource = TestUtils.getDataSourceWithData();
    }

    @After
    public void tearDown() throws Exception {
        TestUtils.shutdown(this.jdbcDataSource);
    }

    @Test
    public void unsupportedMethodsThrowUnsupportedOperationException() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        final CachedRowSetResultSetProxyLogic resultSetProxyLogic = createProxyLogic(resultSet);

        final Method getCursorName = ResultSet.class.getMethod("getCursorName");

        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                resultSetProxyLogic.invoke(getCursorName, null);
            }
        }).isInstanceOf(SQLException.class);
    }

    @Test
    public void getTargetReturnsTheResultSetFromTheTarget() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        CachedRowSetResultSetProxyLogic resultSetProxyLogic = createProxyLogic(resultSet);

        Method getTarget = ProxyJdbcObject.class.getMethod("getTarget");

        Object result = resultSetProxyLogic.invoke(getTarget, null);

        assertThat(result).isSameAs(resultSet);
    }

    @Test
    public void getResultSetMetaDataReturnsTheResultSetMetaDataFromTheTarget() throws Throwable {
        ResultSet originalResultSet = exampleResultSet();
        ResultSetMetaData originalMetaData = originalResultSet.getMetaData();
        CachedRowSetResultSetProxyLogic resultSetProxyLogic = createProxyLogic(originalResultSet);

        Method getMetaData = ResultSet.class.getMethod("getMetaData");
        Object result = resultSetProxyLogic.invoke(getMetaData, null);

        assertThat(result).isInstanceOf(ResultSetMetaData.class);
        ResultSetMetaData metaData = (ResultSetMetaData) result;
        assertThat(metaData.getColumnCount()).isEqualTo(originalMetaData.getColumnCount());
    }

    @Test
    public void closeCallsCloseOnTheTarget() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        CachedRowSetResultSetProxyLogic resultSetProxyLogic = createProxyLogic(resultSet);

        invokeClose(resultSetProxyLogic);
    }

    @Test
    public void getColumnOnResultSetThatHasBeenConsumedTwiceThrowsException() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        final CachedRowSetResultSetProxyLogic resultSetProxyLogic = createProxyLogic(resultSet);

        consumeResultSetAndCallBeforeFirst(resultSetProxyLogic);
        consumeResultSet(resultSetProxyLogic);

        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                CachedRowSetResultSetProxyLogicTest.this.invokeGetString(resultSetProxyLogic, 1);
            }
        }).isInstanceOf(SQLException.class);
    }

    @Test
    public void getColumnOnClosedResultSetThatHasBeenConsumedOnceThrowsException() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        final CachedRowSetResultSetProxyLogic resultSetProxyLogic = createProxyLogic(resultSet);

        consumeResultSetAndCallBeforeFirst(resultSetProxyLogic);
        invokeClose(resultSetProxyLogic);

        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                CachedRowSetResultSetProxyLogicTest.this.invokeGetString(resultSetProxyLogic, 1);
            }
        }).isInstanceOf(SQLException.class);
    }

    @Test
    public void nextOnUnconsumedResultSetThatHasMoreResultsDelegatesToTheTarget() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        CachedRowSetResultSetProxyLogic resultSetProxyLogic = createProxyLogic(resultSet);

        boolean result = invokeNext(resultSetProxyLogic);

        assertThat(result).isEqualTo(true);
    }

    @Test
    public void nextOnUnconsumedResultThatHasNoMoreResultsSetDelegatesToTheTarget() throws Throwable {
        Connection connection = this.jdbcDataSource.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from emp where id = 1000");

        CachedRowSetResultSetProxyLogic resultSetProxyLogic = createProxyLogic(resultSet);

        boolean result = invokeNext(resultSetProxyLogic);

        assertThat(result).isEqualTo(false);
    }

    @Test
    public void getColumnByIndexOnUnconsumedResultSetThatHasMoreResultsDelegatesToTheTarget() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        CachedRowSetResultSetProxyLogic resultSetProxyLogic = createProxyLogic(resultSet);
        invokeNext(resultSetProxyLogic);

        Integer result = invokeGetInt(resultSetProxyLogic, 1);

        assertThat(result).isEqualTo(COLUMN_1_ROW_1_VALUE);
    }

    @Test
    public void getColumnByLabelOnUnconsumedResultSetThatHasMoreResultsDelegatesToTheTarget() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        CachedRowSetResultSetProxyLogic resultSetProxyLogic = createProxyLogic(resultSet);
        invokeNext(resultSetProxyLogic);

        Integer result = invokeGetInt(resultSetProxyLogic, COLUMN_1_LABEL);

        assertThat(result).isEqualTo(COLUMN_1_ROW_1_VALUE);
    }

    @Test
    public void getColumnByIndexOnConsumedResultSetBeforeCallingNextThrowsSQLException() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        final CachedRowSetResultSetProxyLogic resultSetProxyLogic = createProxyLogic(resultSet);

        consumeResultSetAndCallBeforeFirst(resultSetProxyLogic);

        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                CachedRowSetResultSetProxyLogicTest.this.invokeGetString(resultSetProxyLogic, 1);
            }
        }).isInstanceOf(SQLException.class);
    }

    @Test
    public void getColumnByIndexOnConsumedResultSetThatHasMoreResultsReturnsTheResultThatTheTargetDidTheFirstTime() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        CachedRowSetResultSetProxyLogic resultSetProxyLogic = createProxyLogic(resultSet);

        consumeResultSetAndCallBeforeFirst(resultSetProxyLogic);
        invokeNext(resultSetProxyLogic);

        Integer result = invokeGetInt(resultSetProxyLogic, 1);

        assertThat(result).isEqualTo(COLUMN_1_ROW_1_VALUE);
    }

    @Test
    public void getColumnByNotExplicitelyConsumedIndexOnConsumedResultSetThatHasMoreResultsReturnsTheResultThatTheTargetDidTheFirstTime() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        CachedRowSetResultSetProxyLogic resultSetProxyLogic = createProxyLogic(resultSet);

        assertThat(invokeNext(resultSetProxyLogic)).isTrue();

        invokeBeforeFirst(resultSetProxyLogic);
        invokeNext(resultSetProxyLogic);

        Integer result = invokeGetInt(resultSetProxyLogic, 1);

        assertThat(result).isEqualTo(COLUMN_1_ROW_1_VALUE);
    }

    @Test
    public void getColumnByLabelOnConsumedResultSetThatHasMoreResultsReturnsTheResultThatTheTargetDidTheFirstTime() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        CachedRowSetResultSetProxyLogic resultSetProxyLogic = createProxyLogic(resultSet);

        consumeResultSetAndCallBeforeFirst(resultSetProxyLogic);
        invokeNext(resultSetProxyLogic);

        Integer result = invokeGetInt(resultSetProxyLogic, COLUMN_1_LABEL);

        assertThat(result).isEqualTo(COLUMN_1_ROW_1_VALUE);
    }

    @Test
    public void getColumnByLabelOnConsumedResultSetWithUnknownLabelThrowsIllegalArgumentException() throws Throwable {
        ResultSet resultSet = exampleResultSet();
        final CachedRowSetResultSetProxyLogic resultSetProxyLogic = createProxyLogic(resultSet);

        consumeResultSetAndCallBeforeFirst(resultSetProxyLogic);
        invokeNext(resultSetProxyLogic);

        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                invokeGetString(resultSetProxyLogic, "bad");
            }
        }).isInstanceOf(SQLException.class);
    }

    private CachedRowSetResultSetProxyLogic createProxyLogic(ResultSet resultSet) {
        CachedRowSetResultSetProxyLogicFactory factory = new CachedRowSetResultSetProxyLogicFactory();
        return (CachedRowSetResultSetProxyLogic) factory.create(resultSet, new ConnectionInfo(), ProxyConfig.Builder.create().build());
    }

    private void consumeResultSetAndCallBeforeFirst(CachedRowSetResultSetProxyLogic resultSetProxyLogic) throws Throwable {
        consumeResultSet(resultSetProxyLogic);
        invokeBeforeFirst(resultSetProxyLogic);
    }

    private void consumeResultSet(CachedRowSetResultSetProxyLogic resultSetProxyLogic) throws Throwable {
        assertThat(invokeNext(resultSetProxyLogic)).isTrue();
        assertThat(invokeGetInt(resultSetProxyLogic, COLUMN_1_LABEL)).isEqualTo(COLUMN_1_ROW_1_VALUE);
        assertThat(invokeGetString(resultSetProxyLogic, 2)).isEqualTo(COLUMN_2_ROW_1_VALUE);

        assertThat(invokeNext(resultSetProxyLogic)).isTrue();
        assertThat(invokeGetInt(resultSetProxyLogic, 1)).isEqualTo(COLUMN_1_ROW_2_VALUE);
        assertThat(invokeGetString(resultSetProxyLogic, COLUMN_2_LABEL)).isEqualTo(COLUMN_2_ROW_2_VALUE);

        assertThat(invokeNext(resultSetProxyLogic)).isFalse();
    }

    private void invokeClose(CachedRowSetResultSetProxyLogic resultSetProxyLogic) throws Throwable {
        Method next = ResultSet.class.getMethod("close");
        resultSetProxyLogic.invoke(next, null);
    }

    private void invokeBeforeFirst(CachedRowSetResultSetProxyLogic resultSetProxyLogic) throws Throwable {
        Method beforeFirst = ResultSet.class.getMethod("beforeFirst");
        resultSetProxyLogic.invoke(beforeFirst, null);
    }

    private boolean invokeNext(CachedRowSetResultSetProxyLogic resultSetProxyLogic) throws Throwable {
        Method next = ResultSet.class.getMethod("next");
        return (Boolean) resultSetProxyLogic.invoke(next, null);
    }

    private String invokeGetString(CachedRowSetResultSetProxyLogic resultSetProxyLogic, int columnIndex) throws Throwable {
        Method getString = ResultSet.class.getMethod("getString", int.class);
        return (String) resultSetProxyLogic.invoke(getString, new Object[]{columnIndex});
    }

    private int invokeGetInt(CachedRowSetResultSetProxyLogic resultSetProxyLogic, int columnIndex) throws Throwable {
        Method getInt = ResultSet.class.getMethod("getInt", int.class);
        return (Integer) resultSetProxyLogic.invoke(getInt, new Object[]{columnIndex});
    }

    private String invokeGetString(CachedRowSetResultSetProxyLogic resultSetProxyLogic, String columnLabel) throws Throwable {
        Method getString = ResultSet.class.getMethod("getString", String.class);
        return (String) resultSetProxyLogic.invoke(getString, new Object[]{columnLabel});
    }

    private int invokeGetInt(CachedRowSetResultSetProxyLogic resultSetProxyLogic, String columnLabel) throws Throwable {
        Method getInt = ResultSet.class.getMethod("getInt", String.class);
        return (Integer) resultSetProxyLogic.invoke(getInt, new Object[]{columnLabel});
    }

    private ResultSet exampleResultSet() throws SQLException {
        Connection connection = this.jdbcDataSource.getConnection();
        Statement statement = connection.createStatement();
        return statement.executeQuery("select * from emp");
    }


    @Test
    public void testToString() throws Throwable {

        ResultSet rs = exampleResultSet();
        CachedRowSetResultSetProxyLogic logic = createProxyLogic(rs);

        Method method = Object.class.getMethod("toString");
        Object result = logic.invoke(method, null);

        assertThat(result).isInstanceOf(String.class)
                .asString().contains(rs.getClass().getSimpleName()).contains("[").contains("]");
    }

    @Test
    public void testHashCode() throws Throwable {
        ResultSet rs = exampleResultSet();
        CachedRowSetResultSetProxyLogic logic = createProxyLogic(rs);

        Method method = Object.class.getMethod("hashCode");
        Object result = logic.invoke(method, null);

        assertThat(result).isInstanceOf(Integer.class).isEqualTo(rs.hashCode());
    }

    @Test
    public void testEquals() throws Throwable {
        ResultSet rs = exampleResultSet();
        CachedRowSetResultSetProxyLogic logic = createProxyLogic(rs);

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

        CachedRowSetResultSetProxyLogicFactory factory = new CachedRowSetResultSetProxyLogicFactory();
        CachedRowSetResultSetProxyLogic logic = (CachedRowSetResultSetProxyLogic) factory.create(rs, connectionInfo, proxyConfig);

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