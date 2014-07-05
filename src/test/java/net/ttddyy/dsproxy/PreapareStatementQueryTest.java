package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.listener.ChainListener;
import net.ttddyy.dsproxy.proxy.InterceptorHolder;
import net.ttddyy.dsproxy.proxy.jdk.JdkJdbcProxyFactory;
import net.ttddyy.dsproxy.transform.QueryTransformer;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

/**
 * @author Tadaya Tsuyukubo
 */
public class PreapareStatementQueryTest {

    private DataSource jdbcDataSource;
    private TestListener testListener;
    private LastQueryListener lastQueryListener;
    private Connection connection;


    @BeforeMethod
    public void setup() throws Exception {
        testListener = new TestListener();
        lastQueryListener = new LastQueryListener();

        ChainListener chainListener = new ChainListener();
        chainListener.addListener(testListener);
        chainListener.addListener(lastQueryListener);
        InterceptorHolder interceptorHolder = new InterceptorHolder(chainListener, QueryTransformer.DEFAULT);

        // real datasource
        jdbcDataSource = TestUtils.getDataSourceWithData();

        final Connection conn = jdbcDataSource.getConnection();
        connection = new JdkJdbcProxyFactory().createConnection(conn, interceptorHolder);
    }

    @AfterMethod
    public void teardown() throws Exception {
        TestUtils.shutdown(jdbcDataSource);
    }

    @Test
    public void testException() throws Exception {
        final String query = "select * from emp;";
        PreparedStatement stat = connection.prepareStatement(query);

        Exception ex = null;
        try {
            // executeUpdate cannot execute select query 
            stat.executeUpdate();
        } catch (Exception e) {
            ex = e;
        }
        assertNotNull(ex);

        assertEquals(testListener.beforeCount, 1);
        assertEquals(testListener.afterCount, 1);

        List<QueryInfo> beforeQueries = lastQueryListener.getBeforeQueries();
        assertNotNull(beforeQueries);
        assertEquals(beforeQueries.size(), 1);
        assertEquals(beforeQueries.get(0).getQuery(), query);

        ExecutionInfo beforeExec = lastQueryListener.getBeforeExecInfo();
        assertNotNull(beforeExec);
        assertNull(beforeExec.getThrowable());

        List<QueryInfo> afterQueries = lastQueryListener.getAfterQueries();
        assertNotNull(afterQueries);
        assertEquals(afterQueries.size(), 1);
        assertEquals(afterQueries.get(0).getQuery(), query);

        ExecutionInfo afterExec = lastQueryListener.getAfterExecInfo();
        Assert.assertNotNull(afterExec);
        Assert.assertNotNull(afterExec.getThrowable());

        Throwable thrownException = afterExec.getThrowable();
        assertSame(thrownException, ex);

    }

    @Test
    public void testExecuteQueryWithNoParam() throws Exception {
        final String query = "select * from emp;";
        PreparedStatement stat = connection.prepareStatement(query);
        stat.executeQuery();

        assertEquals(testListener.beforeCount, 1);
        assertEquals(testListener.afterCount, 1);

        List<QueryInfo> beforeQueries = lastQueryListener.getBeforeQueries();
        assertNotNull(beforeQueries);
        assertEquals(beforeQueries.size(), 1);
        assertEquals(beforeQueries.get(0).getQuery(), query);
        assertTrue(beforeQueries.get(0).getQueryArgs().isEmpty());

        List<QueryInfo> afterQueries = lastQueryListener.getAfterQueries();
        assertNotNull(afterQueries);
        assertEquals(afterQueries.size(), 1);
        assertEquals(afterQueries.get(0).getQuery(), query);
        assertTrue(afterQueries.get(0).getQueryArgs().isEmpty());
    }

    @Test
    public void testExecuteQueryWithParam() throws Exception {
        final String query = "select * from emp where id = ?;";
        PreparedStatement stat = connection.prepareStatement(query);
        stat.setInt(1, 1);
        stat.executeQuery();

        assertEquals(testListener.beforeCount, 1);
        assertEquals(testListener.afterCount, 1);

        List<QueryInfo> beforeQueries = lastQueryListener.getBeforeQueries();
        assertNotNull(beforeQueries);
        assertEquals(beforeQueries.size(), 1);
        final QueryInfo beforeInfo = beforeQueries.get(0);
        assertEquals(beforeInfo.getQuery(), query);
        assertNotNull(beforeInfo.getQueryArgs());
        assertEquals(beforeInfo.getQueryArgs().size(), 1);
        assertEquals(beforeInfo.getQueryArgs().get(0), 1, "id=1");

        List<QueryInfo> afterQueries = lastQueryListener.getAfterQueries();
        assertNotNull(afterQueries);
        assertEquals(afterQueries.size(), 1);
        final QueryInfo afterInfo = afterQueries.get(0);
        assertEquals(afterInfo.getQuery(), query);
        assertNotNull(afterInfo.getQueryArgs());
        assertEquals(afterInfo.getQueryArgs().size(), 1);
        assertEquals(afterInfo.getQueryArgs().get(0), 1, "id=1");
    }

    @Test
    public void testExecuteUpdate() throws Exception {
        final String query = "update emp set name = ? where id = ?;";
        PreparedStatement stat = connection.prepareStatement(query);
        stat.setString(1, "BAZ");
        stat.setInt(2, 1);
        stat.executeUpdate();

        assertEquals(testListener.beforeCount, 1);
        assertEquals(testListener.afterCount, 1);

        List<QueryInfo> beforeQueries = lastQueryListener.getBeforeQueries();
        assertNotNull(beforeQueries);
        assertEquals(beforeQueries.size(), 1);
        verifyQueryArgs(beforeQueries.get(0), query, "BAZ", 1);
    }

    @Test
    public void testExecuteUpdateWithParamReuse() throws Exception {
        final String query = "update emp set name = ? where id = ?;";
        PreparedStatement stat = connection.prepareStatement(query);
        stat.setString(1, "BAZ");
        stat.setInt(2, 1);
        stat.executeUpdate();

        assertEquals(testListener.beforeCount, 1);
        assertEquals(testListener.afterCount, 1);

        List<QueryInfo> beforeQueries = lastQueryListener.getBeforeQueries();
        assertNotNull(beforeQueries);
        assertEquals(beforeQueries.size(), 1);
        verifyQueryArgs(beforeQueries.get(0), query, "BAZ", 1);

        // reuse name="BAZ"
        stat.setInt(2, 2);
        stat.executeUpdate();

        assertEquals(testListener.beforeCount, 2);
        assertEquals(testListener.afterCount, 2);

        beforeQueries = lastQueryListener.getBeforeQueries();
        assertNotNull(beforeQueries);
        assertEquals(beforeQueries.size(), 1);
        verifyQueryArgs(beforeQueries.get(0), query, "BAZ", 2);
    }

    @Test
    public void testClearParameters() throws Exception {
        final String query = "update emp set name = ? where id = ?;";
        PreparedStatement stat = connection.prepareStatement(query);
        stat.setString(1, "baz");
        stat.setInt(2, 1);

        stat.clearParameters();

        stat.setString(1, "BAZ");
        stat.setInt(2, 2);

        stat.executeUpdate();

        assertEquals(testListener.beforeCount, 1);
        assertEquals(testListener.afterCount, 1);

        List<QueryInfo> beforeQueries = lastQueryListener.getBeforeQueries();
        assertNotNull(beforeQueries);
        assertEquals(beforeQueries.size(), 1);
        verifyQueryArgs(beforeQueries.get(0), query, "BAZ", 2);
    }

    private void verifyQueryArgs(QueryInfo info, String expectedQuery, Object... expectedValues) {
        final String actualQuery = info.getQuery();
        final List<?> args = info.getQueryArgs();

        // verify query
        assertEquals(actualQuery, expectedQuery);

        // verify args
        final int size = expectedValues.length;
        assertNotNull(args);
        assertEquals(args.size(), size);

        for (int i = 0; i < size; i++) {
            Object expected = expectedValues[i];
            Object actual = args.get(i);
            assertEquals(actual, expected);
        }
    }

    @Test
    public void testExecuteBatch() throws Exception {
        final String query = "update emp set name = ? where id = ?;";
        PreparedStatement stat = connection.prepareStatement(query);
        stat.setString(1, "FOO");
        stat.setInt(2, 1);
        stat.addBatch();

        assertEquals(testListener.beforeCount, 0);
        assertEquals(testListener.afterCount, 0);

        stat.setString(1, "BAR");
        stat.setInt(2, 2);
        stat.addBatch();

        assertEquals(testListener.beforeCount, 0);
        assertEquals(testListener.afterCount, 0);

        int[] updateCount = stat.executeBatch();

        assertEquals(testListener.beforeCount, 1);
        assertEquals(testListener.afterCount, 1);

        assertNotNull(updateCount);
        assertEquals(updateCount.length, 2);
        assertEquals(updateCount[0], 1);
        assertEquals(updateCount[1], 1);


        List<QueryInfo> beforeQueries = lastQueryListener.getBeforeQueries();
        assertNotNull(beforeQueries);
        assertEquals(beforeQueries.size(), 2);
        verifyQueryArgs(beforeQueries.get(0), query, "FOO", 1);
        verifyQueryArgs(beforeQueries.get(1), query, "BAR", 2);

//        List<QueryInfo> afterQueries = lastQueryListener.getAfterQueries();
//        assertNotNull(afterQueries);
//        assertEquals(afterQueries.size(), 1);
//        final QueryInfo afterInfo = afterQueries.get(0);
//        assertEquals(afterInfo.getQuery(), query);
//        assertNotNull(afterInfo.getQueryArgs());
//        assertEquals(afterInfo.getQueryArgs().size(), 1);
//        assertEquals(afterInfo.getQueryArgs().get(0), 1, "id=1");
    }

    public void testExecuteBatchWithParamReuse() {
        // TODO: implement
    }

    /**
     * When "executeBatch" is called, List<QueryInfo> should be cleared.
     * reported:  https://github.com/ttddyy/datasource-proxy/issues/9
     */
    @Test
    public void testExecuteBatchShouldClearQueries() throws Exception {

        final String query = "insert into emp ( id, name )values (?, ?);";
        PreparedStatement stat = connection.prepareStatement(query);
        stat.setInt(1, 100);
        stat.setString(2, "FOO");
        stat.addBatch();

        stat.setInt(1, 200);
        stat.setString(2, "BAR");
        stat.addBatch();

        stat.executeBatch();  // 1st execution

        List<QueryInfo> afterQueries = lastQueryListener.getAfterQueries();
        assertThat(afterQueries, notNullValue());
        assertThat("should pass two QueryInfo (FOO,BAR)", afterQueries, hasSize(2));

        stat.setInt(1, 300);
        stat.setString(2, "BAZ");
        stat.addBatch();

        int[] updateCount = stat.executeBatch();  // 2nd execution
        assertThat(updateCount, notNullValue());
        assertThat(updateCount.length, is(1));

        // second execution should pass only one QueryInfo to listener
        afterQueries = lastQueryListener.getAfterQueries();
        assertThat(afterQueries, notNullValue());
        assertThat("should pass one QueryInfo (BAZ)", afterQueries, hasSize(1));

        // verify actual data. 3 rows must be inserted, in addition to original data(2rows)
        int count = TestUtils.countTable(jdbcDataSource, "emp");
        assertThat("2 existing data(foo,bar) and 3 insert(FOO,BAR,BAZ).", count, is(5));

    }

}