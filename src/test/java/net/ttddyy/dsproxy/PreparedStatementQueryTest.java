package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.listener.NoOpQueryExecutionListener;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.proxy.JdbcProxyFactory;
import net.ttddyy.dsproxy.proxy.ParameterSetOperation;
import net.ttddyy.dsproxy.proxy.ProxyConfig;
import net.ttddyy.dsproxy.proxy.SimpleResultSetProxyLogicFactory;
import net.ttddyy.dsproxy.proxy.jdk.JdkJdbcProxyFactory;
import net.ttddyy.dsproxy.proxy.jdk.ResultSetInvocationHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

/**
 * @author Tadaya Tsuyukubo
 */
public class PreparedStatementQueryTest {

    private DataSource jdbcDataSource;
    private TestListener testListener;
    private LastQueryListener lastQueryListener;
    private Connection connection;


    @Before
    public void setup() throws Exception {
        testListener = new TestListener();
        lastQueryListener = new LastQueryListener();

        // real datasource
        jdbcDataSource = TestUtils.getDataSourceWithData();

        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setDataSourceName("myDS");

        ProxyConfig proxyConfig = ProxyConfig.Builder.create()
                .queryListener(this.testListener)
                .queryListener(this.lastQueryListener)
                .build();

        final Connection conn = jdbcDataSource.getConnection();
        connection = new JdkJdbcProxyFactory().createConnection(conn, connectionInfo, proxyConfig);
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

    @Test
    public void sameInstanceOfExecutionInfo() throws Exception {
        final String query = "select * from emp;";
        PreparedStatement stat = connection.prepareStatement(query);
        stat.executeQuery();

        ExecutionInfo before = lastQueryListener.getBeforeExecInfo();
        ExecutionInfo after = lastQueryListener.getAfterExecInfo();

        assertThat(before).as("before and after uses same ExecutionInfo instance").isSameAs(after);
    }

    @Test
    public void resultSetProxy() throws Throwable {
        String sql = "select * from emp;";
        Connection conn = this.jdbcDataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);

        JdbcProxyFactory proxyFactory = new JdkJdbcProxyFactory();
        ProxyConfig proxyConfig = ProxyConfig.Builder.create().resultSetProxyLogicFactory(new SimpleResultSetProxyLogicFactory()).build();

        PreparedStatement proxyPs = proxyFactory.createPreparedStatement(ps, sql, new ConnectionInfo(), conn, proxyConfig);

        ResultSet result = proxyPs.executeQuery();

        assertThat(result).isInstanceOf(ResultSet.class);
        assertThat(Proxy.isProxyClass(result.getClass())).isTrue();
        assertThat(Proxy.getInvocationHandler(result)).isExactlyInstanceOf(ResultSetInvocationHandler.class);
    }

    @Test
    public void generatedKeysProxy() throws Throwable {
        String sql = "insert into emp_with_auto_id ( name ) values ('BAZ');";
        Connection conn = this.jdbcDataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        JdbcProxyFactory proxyFactory = new JdkJdbcProxyFactory();
        ProxyConfig proxyConfig = ProxyConfig.Builder.create().generatedKeysProxyLogicFactory(new SimpleResultSetProxyLogicFactory()).build();

        PreparedStatement proxyPs = proxyFactory.createPreparedStatement(ps, sql, new ConnectionInfo(), conn, proxyConfig);

        proxyPs.executeUpdate();

        // verify getGeneratedKeys
        ResultSet generatedKeys = proxyPs.getGeneratedKeys();
        assertThat(generatedKeys).isInstanceOf(ResultSet.class);
        assertThat(Proxy.isProxyClass(generatedKeys.getClass())).isTrue();
        assertThat(Proxy.getInvocationHandler(generatedKeys)).isExactlyInstanceOf(ResultSetInvocationHandler.class);

        // other ResultSet returning methods should not return proxy
        conn.close();

        sql = "select * from emp;";
        conn = this.jdbcDataSource.getConnection();
        ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        proxyPs = proxyFactory.createPreparedStatement(ps, sql, new ConnectionInfo(), conn, proxyConfig);

        // verify executeQuery
        ResultSet result = proxyPs.executeQuery();
        assertThat(result).isInstanceOf(ResultSet.class);
        assertThat(Proxy.isProxyClass(result.getClass())).isFalse();

        // generated keys will be empty
        generatedKeys = proxyPs.getGeneratedKeys();
        assertThat(generatedKeys).isInstanceOf(ResultSet.class);
        assertThat(Proxy.isProxyClass(generatedKeys.getClass())).isTrue();
        assertThat(generatedKeys.next()).isFalse();

    }

    @Test
    public void autoRetrieveGeneratedKeys() throws Throwable {
        String sql = "insert into emp_with_auto_id ( name ) values ('BAZ');";
        Connection conn = this.jdbcDataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        final AtomicReference<ExecutionInfo> listenerReceivedExecutionInfo = new AtomicReference<ExecutionInfo>();
        QueryExecutionListener listener = new NoOpQueryExecutionListener() {
            @Override
            public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
                // since generatedKeys will NOT be closed, they can be read afterwards.
                listenerReceivedExecutionInfo.set(execInfo);
            }
        };

        // autoRetrieveGeneratedKeys=true
        ProxyConfig proxyConfig = ProxyConfig.Builder.create()
                .queryListener(listener)
                .autoRetrieveGeneratedKeys(true)
                .autoCloseGeneratedKeys(false)
                .build();
        JdbcProxyFactory proxyFactory = new JdkJdbcProxyFactory();
        PreparedStatement proxyPs = proxyFactory.createPreparedStatement(ps, sql, new ConnectionInfo(), conn, proxyConfig);

        proxyPs.executeUpdate();

        ExecutionInfo info = listenerReceivedExecutionInfo.get();
        assertThat(info).isNotNull();

        ResultSet generatedKeys = info.getGeneratedKeys();
        assertThat(generatedKeys).isInstanceOf(ResultSet.class);
        assertThat(Proxy.isProxyClass(generatedKeys.getClass())).isFalse();

        generatedKeys.next();
        int generatedId = generatedKeys.getInt(1);
        assertThat(generatedId).as("generated ID").isEqualTo(2);

        // reset
        listenerReceivedExecutionInfo.set(null);

        // autoRetrieveGeneratedKeys=false
        proxyConfig = ProxyConfig.Builder.create()
                .queryListener(listener)
                .autoRetrieveGeneratedKeys(false)
                .autoCloseGeneratedKeys(false)
                .build();
        proxyPs = proxyFactory.createPreparedStatement(ps, sql, new ConnectionInfo(), conn, proxyConfig);

        proxyPs.executeUpdate();

        info = listenerReceivedExecutionInfo.get();
        assertThat(info).isNotNull();

        assertThat(info.getGeneratedKeys()).isNull();

    }

    @Test
    public void autoCloseGeneratedKeysProxy() throws Throwable {
        String sql = "insert into emp_with_auto_id ( name ) values ('BAZ');";
        Connection conn = this.jdbcDataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        final AtomicReference<ExecutionInfo> listenerReceivedExecutionInfo = new AtomicReference<ExecutionInfo>();
        QueryExecutionListener listener = new NoOpQueryExecutionListener() {
            @Override
            public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
                ResultSet generatedKeys = execInfo.getGeneratedKeys();
                boolean isClosed = true;
                try {
                    isClosed = generatedKeys.isClosed();
                } catch (SQLException ex) {
                    fail("Failed to call generatedKeys.isClosed() message=" + ex.getMessage());
                }
                assertThat(isClosed).isFalse();
                listenerReceivedExecutionInfo.set(execInfo);
            }
        };

        // autoCloseGeneratedKeys=false
        ProxyConfig proxyConfig = ProxyConfig.Builder.create()
                .queryListener(listener)
                .autoRetrieveGeneratedKeys(true)
                .autoCloseGeneratedKeys(false)
                .build();
        JdbcProxyFactory proxyFactory = new JdkJdbcProxyFactory();
        PreparedStatement proxyPs = proxyFactory.createPreparedStatement(ps, sql, new ConnectionInfo(), conn, proxyConfig);

        proxyPs.executeUpdate();

        ExecutionInfo info = listenerReceivedExecutionInfo.get();
        ResultSet generatedKeys = info.getGeneratedKeys();
        assertThat(generatedKeys.isClosed()).isFalse();

        try {
            generatedKeys.close();
        } catch (SQLException ex) {
            fail("closing non closed ResultSet should success. message=" + ex.getMessage());
        }

        listenerReceivedExecutionInfo.set(null);

        // autoCloseGeneratedKeys=true
        proxyConfig = ProxyConfig.Builder.create()
                .queryListener(listener)
                .autoRetrieveGeneratedKeys(true)
                .autoCloseGeneratedKeys(true)
                .build();
        proxyPs = proxyFactory.createPreparedStatement(ps, sql, new ConnectionInfo(), conn, proxyConfig);

        proxyPs.executeUpdate();

        info = listenerReceivedExecutionInfo.get();
        generatedKeys = info.getGeneratedKeys();
        assertThat(generatedKeys.isClosed()).isTrue();

    }

    @Test
    public void autoRetrieveGeneratedKeysWithGeneratedKeysProxy() throws Throwable {
        String sql = "insert into emp_with_auto_id ( name ) values ('BAZ');";
        Connection conn = this.jdbcDataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        final AtomicReference<ExecutionInfo> listenerReceivedExecutionInfo = new AtomicReference<ExecutionInfo>();
        QueryExecutionListener listener = new NoOpQueryExecutionListener() {
            @Override
            public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
                listenerReceivedExecutionInfo.set(execInfo);
            }
        };

        // specify autoRetrieveGeneratedKeys and proxy factory
        ProxyConfig proxyConfig = ProxyConfig.Builder.create()
                .queryListener(listener)
                .autoRetrieveGeneratedKeys(true)
                .generatedKeysProxyLogicFactory(new SimpleResultSetProxyLogicFactory())
                .autoCloseGeneratedKeys(false)
                .build();
        JdbcProxyFactory proxyFactory = new JdkJdbcProxyFactory();
        PreparedStatement proxyPs = proxyFactory.createPreparedStatement(ps, sql, new ConnectionInfo(), conn, proxyConfig);

        proxyPs.executeUpdate();

        ExecutionInfo info = listenerReceivedExecutionInfo.get();
        assertThat(info).isNotNull();
        assertThat(info.getGeneratedKeys()).isInstanceOf(ResultSet.class);

        ResultSet generatedKeys = info.getGeneratedKeys();
        assertThat(Proxy.isProxyClass(generatedKeys.getClass())).isTrue();
        assertThat(Proxy.getInvocationHandler(generatedKeys)).isExactlyInstanceOf(ResultSetInvocationHandler.class);

        generatedKeys.next();
        int generatedId = generatedKeys.getInt(1);
        assertThat(generatedId).as("generated ID").isEqualTo(2);

    }

}