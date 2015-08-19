package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.listener.ChainListener;
import net.ttddyy.dsproxy.proxy.InterceptorHolder;
import net.ttddyy.dsproxy.proxy.ParameterSetOperation;
import net.ttddyy.dsproxy.proxy.jdk.JdkJdbcProxyFactory;
import net.ttddyy.dsproxy.transform.QueryTransformer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tadaya Tsuyukubo
 */
public class PreapareStatementQueryTest {

    private DataSource jdbcDataSource;
    private TestListener testListener;
    private LastQueryListener lastQueryListener;
    private Connection connection;


    @Before
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

    @After
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
        assertThat(ex).isNotNull();

        assertThat(testListener.beforeCount).isEqualTo(1);
        assertThat(testListener.afterCount).isEqualTo(1);

        List<QueryInfo> beforeQueries = lastQueryListener.getBeforeQueries();
        assertThat(beforeQueries).hasSize(1);
        assertThat(beforeQueries.get(0).getQuery()).isEqualTo(query);

        ExecutionInfo beforeExec = lastQueryListener.getBeforeExecInfo();
        assertThat(beforeExec).isNotNull();
        assertThat(beforeExec.getThrowable()).isNull();

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
    public void testExecuteQueryWithNoParam() throws Exception {
        final String query = "select * from emp;";
        PreparedStatement stat = connection.prepareStatement(query);
        stat.executeQuery();

        assertThat(testListener.beforeCount).isEqualTo(1);
        assertThat(testListener.afterCount).isEqualTo(1);

        List<QueryInfo> beforeQueries = lastQueryListener.getBeforeQueries();
        assertThat(beforeQueries).hasSize(1);
        assertThat(beforeQueries.get(0).getQuery()).isEqualTo(query);
        assertThat(beforeQueries.get(0).getParametersList()).hasSize(1);
        assertThat(beforeQueries.get(0).getParametersList().get(0))
                .extracting("args", Object[].class).isEmpty();

        List<QueryInfo> afterQueries = lastQueryListener.getAfterQueries();
        assertThat(afterQueries).hasSize(1);
        assertThat(afterQueries.get(0).getQuery()).isEqualTo(query);
        assertThat(afterQueries.get(0).getParametersList()).hasSize(1);
        assertThat(afterQueries.get(0).getParametersList().get(0))
                .extracting("args", Object[].class).isEmpty();
    }

    @Test
    public void testExecuteQueryWithParam() throws Exception {
        final String query = "select * from emp where id = ?;";
        PreparedStatement stat = connection.prepareStatement(query);
        stat.setInt(1, 1);
        stat.executeQuery();

        assertThat(testListener.beforeCount).isEqualTo(1);
        assertThat(testListener.afterCount).isEqualTo(1);

        List<QueryInfo> beforeQueries = lastQueryListener.getBeforeQueries();
        assertThat(beforeQueries).hasSize(1);
        final QueryInfo beforeInfo = beforeQueries.get(0);
        assertThat(beforeInfo.getQuery()).isEqualTo(query);
        assertThat(beforeInfo.getParametersList()).hasSize(1);
        assertThat(beforeInfo.getParametersList().get(0))
                .extracting("args", Object[].class).containsExactly(new Object[]{1, 1});

        List<QueryInfo> afterQueries = lastQueryListener.getAfterQueries();
        assertThat(afterQueries).hasSize(1);
        final QueryInfo afterInfo = afterQueries.get(0);
        assertThat(afterInfo.getQuery()).isEqualTo(query);

        assertThat(afterInfo.getParametersList()).hasSize(1);
        assertThat(afterInfo.getParametersList().get(0))
                .extracting("args", Object[].class).containsExactly(new Object[]{1, 1});
    }

    @Test
    public void testExecuteUpdate() throws Exception {
        final String query = "update emp set name = ? where id = ?;";
        PreparedStatement stat = connection.prepareStatement(query);
        stat.setString(1, "BAZ");
        stat.setInt(2, 1);
        stat.executeUpdate();

        assertThat(testListener.beforeCount).isEqualTo(1);
        assertThat(testListener.afterCount).isEqualTo(1);

        List<QueryInfo> beforeQueries = lastQueryListener.getBeforeQueries();
        assertThat(beforeQueries).hasSize(1);

        Object[][] expectedQueryArgs = new Object[][]{
                {1, "BAZ"},
                {2, 1}
        };

        verifyQueryArgs(beforeQueries.get(0), query, expectedQueryArgs);
    }

    @Test
    public void testExecuteUpdateWithParamReuse() throws Exception {
        final String query = "update emp set name = ? where id = ?;";
        PreparedStatement stat = connection.prepareStatement(query);
        stat.setString(1, "BAZ");
        stat.setInt(2, 1);
        stat.executeUpdate();

        assertThat(testListener.beforeCount).isEqualTo(1);
        assertThat(testListener.afterCount).isEqualTo(1);

        List<QueryInfo> beforeQueries = lastQueryListener.getBeforeQueries();
        assertThat(beforeQueries).hasSize(1);

        Object[][] expectedQueryArgs = new Object[][]{
                {1, "BAZ"},
                {2, 1}
        };


        verifyQueryArgs(beforeQueries.get(0), query, expectedQueryArgs);

        // reuse name="BAZ"
        stat.setInt(2, 2);
        stat.executeUpdate();

        assertThat(testListener.beforeCount).isEqualTo(2);
        assertThat(testListener.afterCount).isEqualTo(2);

        beforeQueries = lastQueryListener.getBeforeQueries();
        assertThat(beforeQueries).hasSize(1);

        expectedQueryArgs = new Object[][]{
                {1, "BAZ"},
                {2, 2}
        };

        verifyQueryArgs(beforeQueries.get(0), query, expectedQueryArgs);
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

        assertThat(testListener.beforeCount).isEqualTo(1);
        assertThat(testListener.afterCount).isEqualTo(1);

        List<QueryInfo> beforeQueries = lastQueryListener.getBeforeQueries();
        assertThat(beforeQueries).hasSize(1);

        Object[][] expectedQueryArgs = new Object[][]{
                {1, "BAZ"},
                {2, 2}
        };

        verifyQueryArgs(beforeQueries.get(0), query, expectedQueryArgs);
    }

    private void verifyQueryArgs(QueryInfo info, String expectedQuery, Object[]... expectedQueryArgs) {
        final String actualQuery = info.getQuery();
        List<List<ParameterSetOperation>> parametersList = info.getParametersList();

        // verify query
        assertThat(actualQuery).isEqualTo(expectedQuery);

        // verify args
        assertThat(parametersList).as("non-batch execution parametersList should have size 1").hasSize(1);
        List<ParameterSetOperation> params = parametersList.get(0);
        assertThat(params).extracting("args", Object[].class).containsExactly(expectedQueryArgs);
    }

    @Test
    public void testExecuteBatch() throws Exception {
        final String query = "update emp set name = ? where id = ?;";
        PreparedStatement stat = connection.prepareStatement(query);
        stat.setString(1, "FOO");
        stat.setInt(2, 1);
        stat.addBatch();

        assertThat(testListener.beforeCount).isEqualTo(0);
        assertThat(testListener.afterCount).isEqualTo(0);

        stat.setString(1, "BAR");
        stat.setInt(2, 2);
        stat.addBatch();

        assertThat(testListener.beforeCount).isEqualTo(0);
        assertThat(testListener.afterCount).isEqualTo(0);

        int[] updateCount = stat.executeBatch();

        assertThat(testListener.beforeCount).isEqualTo(1);
        assertThat(testListener.afterCount).isEqualTo(1);

        assertThat(updateCount).containsSequence(1, 1);


        List<QueryInfo> beforeQueries = lastQueryListener.getBeforeQueries();
        assertThat(beforeQueries).as("PreparedStatement batch execution will have always one query").hasSize(1);
        QueryInfo queryInfo = beforeQueries.get(0);

        assertThat(queryInfo.getQuery()).isEqualTo(query);
        assertThat(queryInfo.getParametersList()).hasSize(2);
        assertThat(queryInfo.getParametersList().get(0))
                .extracting("args", Object[].class).containsExactly(new Object[]{1, "FOO"}, new Object[]{2, 1});
        assertThat(queryInfo.getParametersList().get(1))
                .extracting("args", Object[].class).containsExactly(new Object[]{1, "BAR"}, new Object[]{2, 2});

        List<QueryInfo> afterQueries = lastQueryListener.getAfterQueries();
        assertThat(afterQueries).as("PreparedStatement batch execution will have always one query").hasSize(1);
        queryInfo = afterQueries.get(0);
        assertThat(queryInfo.getQuery()).isEqualTo(query);
        assertThat(queryInfo.getParametersList()).hasSize(2);
        assertThat(queryInfo.getParametersList().get(0))
                .extracting("args", Object[].class).containsExactly(new Object[]{1, "FOO"}, new Object[]{2, 1});
        assertThat(queryInfo.getParametersList().get(1))
                .extracting("args", Object[].class).containsExactly(new Object[]{1, "BAR"}, new Object[]{2, 2});
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
        assertThat(afterQueries).as("should has one query").hasSize(1);
        QueryInfo queryInfo = afterQueries.get(0);
        assertThat(queryInfo.getParametersList()).as("should have two batch params").hasSize(2);

        stat.setInt(1, 300);
        stat.setString(2, "BAZ");
        stat.addBatch();

        int[] updateCount = stat.executeBatch();  // 2nd execution
        assertThat(updateCount).hasSize(1);

        // second execution should pass only one QueryInfo to listener
        afterQueries = lastQueryListener.getAfterQueries();
        assertThat(afterQueries).isNotNull();
        assertThat(afterQueries).as("should pass one QueryInfo (BAZ)").hasSize(1);
        queryInfo = afterQueries.get(0);
        assertThat(queryInfo.getParametersList()).as("should have one batch params").hasSize(1);

        // verify actual data. 3 rows must be inserted, in addition to original data(2rows)
        int count = TestUtils.countTable(jdbcDataSource, "emp");
        assertThat(count).isEqualTo(5).as("2 existing data(foo,bar) and 3 insert(FOO,BAR,BAZ).");

    }

}