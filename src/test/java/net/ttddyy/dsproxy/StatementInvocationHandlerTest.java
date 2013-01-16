package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.listener.ChainListener;
import net.ttddyy.dsproxy.proxy.JdbcProxyFactory;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;

import net.ttddyy.dsproxy.proxy.JdkJdbcProxyFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;


/**
 * @author Tadaya Tsuyukubo
 */
public class StatementInvocationHandlerTest {

    private DataSource jdbcDataSource;
    private TestListener testListener;
    private LastQueryListener lastQueryListener;
    private Statement statement;

    @BeforeMethod
    public void setup() throws Exception {
        testListener = new TestListener();
        lastQueryListener = new LastQueryListener();

        ChainListener chainListener = new ChainListener();
        chainListener.addListener(testListener);
        chainListener.addListener(lastQueryListener);

        // real datasource
        jdbcDataSource = TestUtils.getDataSourceWithData();

        Connection connection = jdbcDataSource.getConnection();
        Statement stmt = connection.createStatement();

        statement = new JdkJdbcProxyFactory().createStatement(stmt, chainListener);
    }

    @AfterMethod
    public void teardown() throws Exception {
        TestUtils.shutdown(jdbcDataSource);
    }


    @Test
    public void testException() throws Exception {
        final String query = "select * from emp;";

        Exception ex = null;
        try {
            statement.executeUpdate(query);
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
        assertNotNull(afterExec);
        assertNotNull(afterExec.getThrowable());

        Throwable thrownException = afterExec.getThrowable();
        assertSame(thrownException, ex);

    }

    @Test
    public void testExecuteQuery() throws Exception {
        final String query = "select * from emp;";
        statement.executeQuery(query);

        assertEquals(testListener.beforeCount, 1);
        assertEquals(testListener.afterCount, 1);

        List<QueryInfo> beforeQueries = lastQueryListener.getBeforeQueries();
        assertNotNull(beforeQueries);
        assertEquals(beforeQueries.size(), 1);
        assertEquals(beforeQueries.get(0).getQuery(), query);

        List<QueryInfo> afterQueries = lastQueryListener.getAfterQueries();
        assertNotNull(afterQueries);
        assertEquals(afterQueries.size(), 1);
        assertEquals(afterQueries.get(0).getQuery(), query);
    }

    @Test
    public void testExecuteUpdate() throws Exception {
        final String query = "update emp set name = 'bar';";
        statement.executeUpdate(query);

        assertEquals(testListener.beforeCount, 1);
        assertEquals(testListener.afterCount, 1);

        List<QueryInfo> beforeQueries = lastQueryListener.getBeforeQueries();
        assertNotNull(beforeQueries);
        assertEquals(beforeQueries.size(), 1);
        assertEquals(beforeQueries.get(0).getQuery(), query);

        List<QueryInfo> afterQueries = lastQueryListener.getAfterQueries();
        assertNotNull(afterQueries);
        assertEquals(afterQueries.size(), 1);
        assertEquals(afterQueries.get(0).getQuery(), query);
    }

    @Test
    public void testExecuteBatch() throws Exception {
        final String query1 = "insert into emp ( id, name ) values (3, 'baz');";
        statement.addBatch(query1);
        assertEquals(testListener.beforeCount, 0);
        assertEquals(testListener.afterCount, 0);

        final String query2 = "update emp set name = 'FOO';";
        statement.addBatch(query2);
        assertEquals(testListener.beforeCount, 0);
        assertEquals(testListener.afterCount, 0);

        int[] result = statement.executeBatch();
        assertEquals(testListener.beforeCount, 1);
        assertEquals(testListener.afterCount, 1);

        assertEquals(result.length, 2);
        assertEquals(result[0], 1, "one row inserted");
        assertEquals(result[1], 3, "two rows updated");

        List<QueryInfo> beforeQueries = lastQueryListener.getBeforeQueries();
        assertNotNull(beforeQueries);
        assertEquals(beforeQueries.size(), 2);
        assertEquals(beforeQueries.get(0).getQuery(), query1);
        assertEquals(beforeQueries.get(1).getQuery(), query2);

        List<QueryInfo> afterQueries = lastQueryListener.getAfterQueries();
        assertNotNull(afterQueries);
        assertEquals(afterQueries.size(), 2);
        assertEquals(afterQueries.get(0).getQuery(), query1);
        assertEquals(afterQueries.get(1).getQuery(), query2);

    }

    @Test
    public void testClearBatch() throws Exception {
        statement.addBatch("insert into emp ( id, name ) values (2, 'bar');");
        assertEquals(testListener.beforeCount, 0);
        assertEquals(testListener.afterCount, 0);

        statement.clearBatch();
        assertEquals(testListener.beforeCount, 0);
        assertEquals(testListener.afterCount, 0);

        List<QueryInfo> beforeQueries = lastQueryListener.getBeforeQueries();
        assertNotNull(beforeQueries);
        assertEquals(beforeQueries.size(), 0);

        List<QueryInfo> afterQueries = lastQueryListener.getAfterQueries();
        assertNotNull(afterQueries);
        assertEquals(afterQueries.size(), 0);
    }
}
