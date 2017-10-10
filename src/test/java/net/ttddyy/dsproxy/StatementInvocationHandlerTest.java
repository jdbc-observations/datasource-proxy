package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.proxy.ProxyConfig;
import net.ttddyy.dsproxy.proxy.jdk.JdkJdbcProxyFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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

        // real datasource
        jdbcDataSource = TestUtils.getDataSourceWithData();

        Connection connection = jdbcDataSource.getConnection();
        Statement stmt = connection.createStatement();

        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setDataSourceName("myDS");

        ProxyConfig proxyConfig = ProxyConfig.Builder.create()
                .queryListener(this.testListener)
                .queryListener(this.lastQueryListener)
                .build();

        statement = new JdkJdbcProxyFactory().createStatement(stmt, connectionInfo, null, proxyConfig);
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
        assertThat(ex).isNotNull();

        assertThat(testListener.beforeCount).isEqualTo(1);
        assertThat(testListener.afterCount).isEqualTo(1);

        List<QueryInfo> beforeQueries = lastQueryListener.getBeforeQueries();
        assertThat(beforeQueries).hasSize(1);
        assertThat(beforeQueries.get(0).getQuery()).isEqualTo(query);

        List<QueryInfo> afterQueries = lastQueryListener.getAfterQueries();
        assertThat(afterQueries).hasSize(1);
        assertThat(afterQueries.get(0).getQuery()).isEqualTo(query);

        ExecutionInfo afterExec = lastQueryListener.getAfterExecInfo();
        assertThat(afterExec).isNotNull();
        assertThat(afterExec.getThrowable()).isNotNull();

        Throwable thrownException = afterExec.getThrowable();
        assertThat(thrownException).isSameAs(ex);

    }

    @Test
    public void testExecuteQuery() throws Exception {
        final String query = "select * from emp;";
        statement.executeQuery(query);

        assertThat(testListener.beforeCount).isEqualTo(1);
        assertThat(testListener.afterCount).isEqualTo(1);

        List<QueryInfo> beforeQueries = lastQueryListener.getBeforeQueries();
        assertThat(beforeQueries).hasSize(1);
        assertThat(beforeQueries.get(0).getQuery()).isEqualTo(query);

        List<QueryInfo> afterQueries = lastQueryListener.getAfterQueries();
        assertThat(afterQueries).hasSize(1);
        assertThat(afterQueries.get(0).getQuery()).isEqualTo(query);
    }

    @Test
    public void testExecuteUpdate() throws Exception {
        final String query = "update emp set name = 'bar';";
        statement.executeUpdate(query);

        assertThat(testListener.beforeCount).isEqualTo(1);
        assertThat(testListener.afterCount).isEqualTo(1);

        List<QueryInfo> beforeQueries = lastQueryListener.getBeforeQueries();
        assertThat(beforeQueries).hasSize(1);
        assertThat(beforeQueries.get(0).getQuery()).isEqualTo(query);

        List<QueryInfo> afterQueries = lastQueryListener.getAfterQueries();
        assertThat(afterQueries).hasSize(1);
        assertThat(afterQueries.get(0).getQuery()).isEqualTo(query);
    }

    @Test
    public void testExecuteBatch() throws Exception {
        final String query1 = "insert into emp ( id, name ) values (3, 'baz');";
        statement.addBatch(query1);
        assertThat(testListener.beforeCount).isEqualTo(0);
        assertThat(testListener.afterCount).isEqualTo(0);

        final String query2 = "update emp set name = 'FOO';";
        statement.addBatch(query2);
        assertThat(testListener.beforeCount).isEqualTo(0);
        assertThat(testListener.afterCount).isEqualTo(0);

        int[] result = statement.executeBatch();
        assertThat(testListener.beforeCount).isEqualTo(1);
        assertThat(testListener.afterCount).isEqualTo(1);

        assertThat(result.length).isEqualTo(2);
        assertThat(result[0]).as("one row inserted").isEqualTo(1);
        assertThat(result[1]).as("two rows updated").isEqualTo(3);

        List<QueryInfo> beforeQueries = lastQueryListener.getBeforeQueries();
        assertThat(beforeQueries).hasSize(2);
        assertThat(beforeQueries.get(0).getQuery()).isEqualTo(query1);
        assertThat(beforeQueries.get(1).getQuery()).isEqualTo(query2);

        List<QueryInfo> afterQueries = lastQueryListener.getAfterQueries();
        assertThat(afterQueries).hasSize(2);
        assertThat(afterQueries.get(0).getQuery()).isEqualTo(query1);
        assertThat(afterQueries.get(1).getQuery()).isEqualTo(query2);

    }

    @Test
    public void testClearBatch() throws Exception {
        statement.addBatch("insert into emp ( id, name ) values (2, 'bar');");
        assertThat(testListener.beforeCount).isEqualTo(0);
        assertThat(testListener.afterCount).isEqualTo(0);

        statement.clearBatch();
        assertThat(testListener.beforeCount).isEqualTo(0);
        assertThat(testListener.afterCount).isEqualTo(0);

        List<QueryInfo> beforeQueries = lastQueryListener.getBeforeQueries();
        assertThat(beforeQueries).isEmpty();

        List<QueryInfo> afterQueries = lastQueryListener.getAfterQueries();
        assertThat(afterQueries).isEmpty();
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
        assertThat(afterQueries).isNotNull();
        assertThat(afterQueries).as("should pass two QueryInfo (FOO,BAR)").hasSize(2);

        statement.addBatch("insert into emp ( id, name )values (300, 'BAZ');");

        int[] updateCount = statement.executeBatch();  // 2nd execution
        assertThat(updateCount).isNotNull();
        assertThat(updateCount.length).isEqualTo(1);

        // second execution should pass only one QueryInfo to listener
        afterQueries = lastQueryListener.getAfterQueries();
        assertThat(afterQueries).isNotNull();
        assertThat(afterQueries).as("should pass one QueryInfo (BAZ)").hasSize(1);

        // verify actual data. 3 rows must be inserted, in addition to original data(2rows)
        int count = TestUtils.countTable(jdbcDataSource, "emp");
        assertThat(count).as("2 existing data(foo,bar) and 3 insert(FOO,BAR,BAZ).").isEqualTo(5);

    }

    @Test
    public void sameInstanceOfExecutionInfo() throws Exception {
        final String query = "select * from emp;";
        statement.executeQuery(query);

        ExecutionInfo before = lastQueryListener.getBeforeExecInfo();
        ExecutionInfo after = lastQueryListener.getAfterExecInfo();

        assertThat(before).as("before and after uses same ExecutionInfo instance").isSameAs(after);
    }
}
