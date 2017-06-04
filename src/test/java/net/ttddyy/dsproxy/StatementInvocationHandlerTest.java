package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.proxy.InterceptorHolder;
import net.ttddyy.dsproxy.proxy.jdk.JdkJdbcProxyFactory;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;


/**
 * @author Tadaya Tsuyukubo
 */
public class StatementInvocationHandlerTest {

    private DataSource jdbcDataSource;
    private TestListener testListener;
    private LastQueryListener lastQueryListener;
    private Statement statement;

    @Before
    public void setup() throws Exception {
        testListener = new TestListener();
        lastQueryListener = new LastQueryListener();

        InterceptorHolder interceptorHolder = new InterceptorHolder();
        interceptorHolder.addListener(testListener);
        interceptorHolder.addListener(lastQueryListener);


        // real datasource
        jdbcDataSource = TestUtils.getDataSourceWithData();

        Connection connection = jdbcDataSource.getConnection();
        Statement stmt = connection.createStatement();

        statement = new JdkJdbcProxyFactory().createStatement(stmt, interceptorHolder, "myDS", null);
    }

    @After
    public void teardown() throws Exception {
        TestUtils.shutdown(jdbcDataSource);
    }


    @Test
    public void testException() throws Exception {
        final String query = "select * from emp;";

        Exception ex = null;
        try {
            statement.executeUpdate(query);
            fail("select query with executeUpdate() should fail");
        } catch (Exception e) {
            ex = e;
        }
        assertThat(ex, is(notNullValue()));

        assertThat(testListener.beforeCount, is(1));
        assertThat(testListener.afterCount, is(1));

        List<QueryInfo> beforeQueries = lastQueryListener.getBeforeQueries();
        assertThat(beforeQueries, hasSize(1));
        assertThat(beforeQueries.get(0).getQuery(), is(query));

        List<QueryInfo> afterQueries = lastQueryListener.getAfterQueries();
        assertThat(afterQueries, hasSize(1));
        assertThat(afterQueries.get(0).getQuery(), is(query));

        ExecutionInfo afterExec = lastQueryListener.getAfterExecInfo();
        assertThat(afterExec, is(notNullValue()));
        assertThat(afterExec.getThrowable(), is(notNullValue()));

        Throwable thrownException = afterExec.getThrowable();
        assertThat(thrownException, sameInstance((Throwable) ex));

    }

    @Test
    public void testExecuteQuery() throws Exception {
        final String query = "select * from emp;";
        statement.executeQuery(query);

        assertThat(testListener.beforeCount, is(1));
        assertThat(testListener.afterCount, is(1));

        List<QueryInfo> beforeQueries = lastQueryListener.getBeforeQueries();
        assertThat(beforeQueries, hasSize(1));
        assertThat(beforeQueries.get(0).getQuery(), is(query));

        List<QueryInfo> afterQueries = lastQueryListener.getAfterQueries();
        assertThat(afterQueries, hasSize(1));
        assertThat(afterQueries.get(0).getQuery(), is(query));
    }

    @Test
    public void testExecuteUpdate() throws Exception {
        final String query = "update emp set name = 'bar';";
        statement.executeUpdate(query);

        assertThat(testListener.beforeCount, is(1));
        assertThat(testListener.afterCount, is(1));

        List<QueryInfo> beforeQueries = lastQueryListener.getBeforeQueries();
        assertThat(beforeQueries, hasSize(1));
        assertThat(beforeQueries.get(0).getQuery(), is(query));

        List<QueryInfo> afterQueries = lastQueryListener.getAfterQueries();
        assertThat(afterQueries, hasSize(1));
        assertThat(afterQueries.get(0).getQuery(), is(query));
    }

    @Test
    public void testExecuteBatch() throws Exception {
        final String query1 = "insert into emp ( id, name ) values (3, 'baz');";
        statement.addBatch(query1);
        assertThat(testListener.beforeCount, is(0));
        assertThat(testListener.afterCount, is(0));

        final String query2 = "update emp set name = 'FOO';";
        statement.addBatch(query2);
        assertThat(testListener.beforeCount, is(0));
        assertThat(testListener.afterCount, is(0));

        int[] result = statement.executeBatch();
        assertThat(testListener.beforeCount, is(1));
        assertThat(testListener.afterCount, is(1));

        assertThat(result.length, is(2));
        assertThat("one row inserted", result[0], is(1));
        assertThat("two rows updated", result[1], is(3));

        List<QueryInfo> beforeQueries = lastQueryListener.getBeforeQueries();
        assertThat(beforeQueries, hasSize(2));
        assertThat(beforeQueries.get(0).getQuery(), is(query1));
        assertThat(beforeQueries.get(1).getQuery(), is(query2));

        List<QueryInfo> afterQueries = lastQueryListener.getAfterQueries();
        assertThat(afterQueries, hasSize(2));
        assertThat(afterQueries.get(0).getQuery(), is(query1));
        assertThat(afterQueries.get(1).getQuery(), is(query2));

    }

    @Test
    public void testClearBatch() throws Exception {
        statement.addBatch("insert into emp ( id, name ) values (2, 'bar');");
        assertThat(testListener.beforeCount, is(0));
        assertThat(testListener.afterCount, is(0));

        statement.clearBatch();
        assertThat(testListener.beforeCount, is(0));
        assertThat(testListener.afterCount, is(0));

        List<QueryInfo> beforeQueries = lastQueryListener.getBeforeQueries();
        assertThat(beforeQueries, hasSize(0));

        List<QueryInfo> afterQueries = lastQueryListener.getAfterQueries();
        assertThat(afterQueries, hasSize(0));
    }

    /**
     * When "executeBatch" is called, List<QueryInfo> should be cleared.
     * reported:  https://github.com/ttddyy/datasource-proxy/issues/9
     */
    @Test
    public void testExecuteBatchShouldClearQueries() throws Exception {

        statement.addBatch("insert into emp ( id, name )values (100, 'FOO');");
        statement.addBatch("insert into emp ( id, name )values (200, 'BAR');");
        statement.executeBatch();  // 1st execution

        List<QueryInfo> afterQueries = lastQueryListener.getAfterQueries();
        assertThat(afterQueries, notNullValue());
        assertThat("should pass two QueryInfo (FOO,BAR)", afterQueries, hasSize(2));

        statement.addBatch("insert into emp ( id, name )values (300, 'BAZ');");

        int[] updateCount = statement.executeBatch();  // 2nd execution
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

    @Test
    public void sameInstanceOfExecutionInfo() throws Exception {
        final String query = "select * from emp;";
        statement.executeQuery(query);

        ExecutionInfo before = lastQueryListener.getBeforeExecInfo();
        ExecutionInfo after = lastQueryListener.getAfterExecInfo();

        Assertions.assertThat(before).as("before and after uses same ExecutionInfo instance").isSameAs(after);
    }
}
