package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.listener.DataSourceQueryCountListener;
import net.ttddyy.dsproxy.listener.QueryUtils;
import net.ttddyy.dsproxy.listener.SingleQueryCountHolder;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.hsqldb.jdbc.JDBCDataSource;
import org.junit.After;
import org.junit.Test;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class QueryTypeDetectorTest {

    private static final String REPORTED_CTE = "WITH RECURSIVE t(n, level_num) AS (SELECT next value for SEQ_PARENT_ID as n, 1 as level_num UNION ALL SELECT next value for SEQ_PARENT_ID as n, level_num + 1 as level_num  FROM t  WHERE level_num < ?) SELECT n FROM t";
    private static final String EXECUTABLE_CTE = "WITH RECURSIVE t(n) AS (VALUES (1) UNION ALL SELECT n + 1 FROM t WHERE n < 3) SELECT n FROM t";

    @After
    public void clearCounts() {
        QueryCountHolder.clear();
    }

    @Test
    public void defaultClassificationAndCountersAreUnchanged() {
        DataSourceQueryCountListener listener = new DataSourceQueryCountListener();
        listener.afterQuery(execution(true, StatementType.PREPARED, 17), queries(
                null, "", " /* comment */ select 1", "insert into t values (1)",
                "update t set n = 1", "delete from t", REPORTED_CTE));
        QueryCount count = QueryCountHolder.get("detector");
        assertEquals(1, count.getSelect());
        assertEquals(1, count.getInsert());
        assertEquals(1, count.getUpdate());
        assertEquals(1, count.getDelete());
        assertEquals(3, count.getOther());
        assertEquals(1, count.getTotal());
        assertEquals(1, count.getSuccess());
        assertEquals(0, count.getFailure());
        assertEquals(1, count.getPrepared());
        assertEquals(17, count.getTime());
        assertSame(QueryType.OTHER, QueryUtils.getQueryType(REPORTED_CTE));
    }

    @Test
    public void defaultBatchAndFailureCountersAreUnchanged() {
        DataSourceQueryCountListener listener = new DataSourceQueryCountListener();
        ExecutionInfo info = execution(false, StatementType.STATEMENT, 9);
        info.setBatch(true);
        info.setBatchSize(2);
        listener.afterQuery(info, queries("insert into t values (1)", "update t set n = 2"));
        QueryCount count = QueryCountHolder.get("detector");
        assertEquals(1, count.getInsert());
        assertEquals(1, count.getUpdate());
        assertEquals(1, count.getTotal());
        assertEquals(1, count.getFailure());
        assertEquals(0, count.getSuccess());
        assertEquals(1, count.getStatement());
        assertEquals(9, count.getTime());
    }

    @Test
    public void customDetectorReceivesUnmodifiedQueriesIncludingNull() throws Exception {
        DataSourceQueryCountListener listener = new DataSourceQueryCountListener();
        final List<String> seen = new ArrayList<String>();
        Object detector = install(listener, seen);
        listener.afterQuery(execution(true, StatementType.CALLABLE, 23), queries(REPORTED_CTE, null, "  select 2  "));
        assertEquals(Arrays.asList(REPORTED_CTE, null, "  select 2  "), seen);
        assertSame(detector, listener.getClass().getMethod("getQueryTypeDetector").invoke(listener));
        QueryCount count = QueryCountHolder.get("detector");
        assertEquals(2, count.getSelect());
        assertEquals(1, count.getOther());
        assertEquals(1, count.getCallable());
        assertEquals(1, count.getSuccess());
        assertEquals(23, count.getTime());
    }

    @Test
    public void customDetectorWorksWithIndependentCountStorageAndBatchFailure() throws Exception {
        DataSourceQueryCountListener listener = new DataSourceQueryCountListener();
        SingleQueryCountHolder holder = new SingleQueryCountHolder();
        holder.setPopulateQueryCountHolder(false);
        listener.setQueryCountStrategy(holder);
        install(listener, new ArrayList<String>());
        ExecutionInfo info = execution(false, StatementType.PREPARED, 11);
        info.setBatch(true);
        info.setBatchSize(2);
        listener.afterQuery(info, queries(REPORTED_CTE));
        assertSame(holder, listener.getQueryCountStrategy());
        QueryCount count = holder.getQueryCountMap().get("detector");
        assertEquals(1, count.getSelect());
        assertEquals(1, count.getTotal());
        assertEquals(1, count.getFailure());
        assertEquals(1, count.getPrepared());
        assertEquals(11, count.getTime());
    }

    @Test
    public void defaultDetectorDelegatesLegacyClassification() throws Exception {
        DataSourceQueryCountListener listener = new DataSourceQueryCountListener();
        Object detector = listener.getClass().getMethod("getQueryTypeDetector").invoke(listener);
        assertEquals("net.ttddyy.dsproxy.listener.DefaultQueryTypeDetector", detector.getClass().getName());
        Method classify = detector.getClass().getMethod("getQueryType", String.class);
        for (String query : Arrays.asList(null, "", "select 1", " /* comment */ insert into t values (1)",
                "update t set n = 1", "delete from t", REPORTED_CTE)) {
            assertSame(QueryUtils.getQueryType(query), classify.invoke(detector, new Object[]{query}));
        }
    }

    @Test
    public void customDetectorDoesNotChangeOtherListeners() throws Exception {
        DataSourceQueryCountListener custom = new DataSourceQueryCountListener();
        DataSourceQueryCountListener other = new DataSourceQueryCountListener();
        install(custom, new ArrayList<String>());
        custom.afterQuery(execution(true, StatementType.STATEMENT, 0), queries(REPORTED_CTE));
        other.afterQuery(execution(true, StatementType.STATEMENT, 0), queries(REPORTED_CTE));
        QueryCount count = QueryCountHolder.get("detector");
        assertEquals(1, count.getSelect());
        assertEquals(1, count.getOther());
        assertEquals(2, count.getTotal());
    }

    @Test
    public void nullDetectorIsRejectedWithoutReplacingDefault() throws Exception {
        DataSourceQueryCountListener listener = new DataSourceQueryCountListener();
        Object original = listener.getClass().getMethod("getQueryTypeDetector").invoke(listener);
        try {
            listener.getClass().getMethod("setQueryTypeDetector", detectorType()).invoke(listener, new Object[]{null});
            fail("null detector should be rejected");
        } catch (InvocationTargetException ex) {
            assertTrue(ex.getCause() instanceof IllegalArgumentException);
        }
        assertSame(original, listener.getClass().getMethod("getQueryTypeDetector").invoke(listener));
    }

    @Test
    public void publicBuilderUsesCustomDetectorForExecutedRecursiveCte() throws Exception {
        DataSourceQueryCountListener listener = new DataSourceQueryCountListener();
        List<String> seen = new ArrayList<String>();
        install(listener, seen);
        JDBCDataSource original = new JDBCDataSource();
        original.setDatabase("jdbc:hsqldb:mem:queryTypeDetector");
        original.setUser("sa");
        DataSource dataSource = ProxyDataSourceBuilder.create(original).name("detector").listener(listener).build();
        try {
            try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(EXECUTABLE_CTE)) {
                    for (int expected = 1; expected <= 3; expected++) {
                        assertTrue(resultSet.next());
                        assertEquals(expected, resultSet.getInt(1));
                    }
                    assertFalse(resultSet.next());
                }
            }
            assertEquals(Arrays.asList(EXECUTABLE_CTE), seen);
            QueryCount count = QueryCountHolder.get("detector");
            assertEquals(1, count.getSelect());
            assertEquals(0, count.getOther());
            assertEquals(1, count.getTotal());
            assertEquals(1, count.getSuccess());
            assertEquals(1, count.getStatement());
        } finally {
            TestUtils.shutdown(original);
        }
    }

    // Reflective lookup lets the identical regression source run on the pre-SPI base.
    // Missing extension points fail these tests on base, while legacy controls still run.
    private static Class<?> detectorType() throws ClassNotFoundException {
        return Class.forName("net.ttddyy.dsproxy.listener.QueryTypeDetector");
    }

    private static Object install(DataSourceQueryCountListener listener, final List<String> seen) throws Exception {
        Class<?> type = detectorType();
        Object detector = Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                String query = (String) args[0];
                seen.add(query);
                // Only these known SELECT fixtures are special-cased; this is not a generic WITH parser.
                return REPORTED_CTE.equals(query) || EXECUTABLE_CTE.equals(query)
                        ? QueryType.SELECT : QueryUtils.getQueryType(query);
            }
        });
        listener.getClass().getMethod("setQueryTypeDetector", type).invoke(listener, detector);
        return detector;
    }

    private static List<QueryInfo> queries(String... queries) {
        List<QueryInfo> result = new ArrayList<QueryInfo>();
        for (String query : queries) {
            result.add(new QueryInfo(query));
        }
        return result;
    }

    private static ExecutionInfo execution(boolean success, StatementType type, long time) {
        ExecutionInfo info = new ExecutionInfo();
        info.setDataSourceName("detector");
        info.setSuccess(success);
        info.setStatementType(type);
        info.setElapsedTime(time);
        return info;
    }
}
