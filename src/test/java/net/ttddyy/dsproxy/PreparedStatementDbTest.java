package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.listener.ProxyDataSourceListener;
import net.ttddyy.dsproxy.proxy.JdbcProxyFactory;
import net.ttddyy.dsproxy.proxy.ParameterSetOperation;
import net.ttddyy.dsproxy.proxy.ParameterSetOperations;
import net.ttddyy.dsproxy.proxy.ProxyConfig;
import net.ttddyy.dsproxy.proxy.SimpleResultSetProxyLogicFactory;
import net.ttddyy.dsproxy.proxy.jdk.JdkJdbcProxyFactory;
import net.ttddyy.dsproxy.proxy.jdk.ResultSetInvocationHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author Tadaya Tsuyukubo
 */
@DatabaseTest
public class PreparedStatementDbTest {

    private TestListener testListener;
    private LastQueryListener lastQueryListener;
    private Connection connection;

    private DataSource jdbcDataSource;
    private DbResourceCleaner cleaner;

    public PreparedStatementDbTest(DataSource jdbcDataSource, DbResourceCleaner cleaner) {
        this.jdbcDataSource = jdbcDataSource;
        this.cleaner = cleaner;
    }

    @BeforeEach
    public void setup() throws Exception {
        testListener = new TestListener();
        lastQueryListener = new LastQueryListener();

        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setDataSourceName("myDS");

        ProxyConfig proxyConfig = ProxyConfig.Builder.create()
                .listener(this.testListener)
                .listener(this.lastQueryListener)
                .build();

        final Connection conn = jdbcDataSource.getConnection();
        this.cleaner.add(conn);
        connection = new JdkJdbcProxyFactory().createConnection(conn, connectionInfo, proxyConfig);
    }

    @Test
    public void testException() throws Exception {
        final String query = "select * from emp;";
        PreparedStatement stat = connection.prepareStatement(query);
        this.cleaner.add(stat);

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

        List<QueryInfo> beforeQueries = lastQueryListener.getBeforeExecInfo().getQueries();
        assertThat(beforeQueries).hasSize(1);
        assertThat(beforeQueries.get(0).getQuery()).isEqualTo(query);

        List<QueryInfo> afterQueries = lastQueryListener.getAfterExecInfo().getQueries();
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
        this.cleaner.add(stat);
        stat.executeQuery();

        assertThat(testListener.beforeCount).isEqualTo(1);
        assertThat(testListener.afterCount).isEqualTo(1);

        List<QueryInfo> beforeQueries = lastQueryListener.getBeforeExecInfo().getQueries();
        assertThat(beforeQueries).hasSize(1);
        assertThat(beforeQueries.get(0).getQuery()).isEqualTo(query);
        assertThat(beforeQueries.get(0).getParameterSetOperations()).hasSize(1);
        assertThat(beforeQueries.get(0).getParameterSetOperations().get(0).getOperations())
                .extracting("args", Object[].class).isEmpty();

        List<QueryInfo> afterQueries = lastQueryListener.getAfterExecInfo().getQueries();
        assertThat(afterQueries).hasSize(1);
        assertThat(afterQueries.get(0).getQuery()).isEqualTo(query);
        assertThat(afterQueries.get(0).getParameterSetOperations()).hasSize(1);
        assertThat(afterQueries.get(0).getParameterSetOperations().get(0).getOperations())
                .extracting("args", Object[].class).isEmpty();
    }

    @Test
    public void testExecuteQueryWithParam() throws Exception {
        final String query = "select * from emp where id = ?;";
        PreparedStatement stat = connection.prepareStatement(query);
        this.cleaner.add(stat);
        stat.setInt(1, 1);
        stat.executeQuery();

        assertThat(testListener.beforeCount).isEqualTo(1);
        assertThat(testListener.afterCount).isEqualTo(1);

        List<QueryInfo> beforeQueries = lastQueryListener.getBeforeExecInfo().getQueries();
        assertThat(beforeQueries).hasSize(1);
        final QueryInfo beforeInfo = beforeQueries.get(0);
        assertThat(beforeInfo.getQuery()).isEqualTo(query);
        assertThat(beforeInfo.getParameterSetOperations()).hasSize(1);
        assertThat(beforeInfo.getParameterSetOperations().get(0).getOperations())
                .extracting("args", Object[].class).containsExactly(new Object[]{1, 1});

        List<QueryInfo> afterQueries = lastQueryListener.getAfterExecInfo().getQueries();
        assertThat(afterQueries).hasSize(1);
        final QueryInfo afterInfo = afterQueries.get(0);
        assertThat(afterInfo.getQuery()).isEqualTo(query);

        assertThat(afterInfo.getParameterSetOperations()).hasSize(1);
        assertThat(afterInfo.getParameterSetOperations().get(0).getOperations())
                .extracting("args", Object[].class).containsExactly(new Object[]{1, 1});
    }

    @Test
    public void testExecuteUpdate() throws Exception {
        final String query = "update emp set name = ? where id = ?;";
        PreparedStatement stat = connection.prepareStatement(query);
        this.cleaner.add(stat);
        stat.setString(1, "BAZ");
        stat.setInt(2, 1);
        stat.executeUpdate();

        assertThat(testListener.beforeCount).isEqualTo(1);
        assertThat(testListener.afterCount).isEqualTo(1);

        List<QueryInfo> beforeQueries = lastQueryListener.getBeforeExecInfo().getQueries();
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
        this.cleaner.add(stat);
        stat.setString(1, "BAZ");
        stat.setInt(2, 1);
        stat.executeUpdate();

        assertThat(testListener.beforeCount).isEqualTo(1);
        assertThat(testListener.afterCount).isEqualTo(1);

        List<QueryInfo> beforeQueries = lastQueryListener.getBeforeExecInfo().getQueries();
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

        beforeQueries = lastQueryListener.getBeforeExecInfo().getQueries();
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
        this.cleaner.add(stat);
        stat.setString(1, "baz");
        stat.setInt(2, 1);

        stat.clearParameters();

        stat.setString(1, "BAZ");
        stat.setInt(2, 2);

        stat.executeUpdate();

        assertThat(testListener.beforeCount).isEqualTo(1);
        assertThat(testListener.afterCount).isEqualTo(1);

        List<QueryInfo> beforeQueries = lastQueryListener.getBeforeExecInfo().getQueries();
        assertThat(beforeQueries).hasSize(1);

        Object[][] expectedQueryArgs = new Object[][]{
                {1, "BAZ"},
                {2, 2}
        };

        verifyQueryArgs(beforeQueries.get(0), query, expectedQueryArgs);
    }

    private void verifyQueryArgs(QueryInfo info, String expectedQuery, Object[]... expectedQueryArgs) {
        final String actualQuery = info.getQuery();
        List<ParameterSetOperations> parameterSetOperations = info.getParameterSetOperations();

        // verify query
        assertThat(actualQuery).isEqualTo(expectedQuery);

        // verify args
        assertThat(parameterSetOperations).as("non-batch execution parametersList should have size 1").hasSize(1);
        List<ParameterSetOperation> params = parameterSetOperations.get(0).getOperations();
        assertThat(params).extracting("args", Object[].class).containsExactly(expectedQueryArgs);
    }

    @Test
    public void testExecuteBatch() throws Exception {
        final String query = "update emp set name = ? where id = ?;";
        PreparedStatement stat = connection.prepareStatement(query);
        this.cleaner.add(stat);
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


        List<QueryInfo> beforeQueries = lastQueryListener.getBeforeExecInfo().getQueries();
        assertThat(beforeQueries).as("PreparedStatement batch execution will have always one query").hasSize(1);
        QueryInfo queryInfo = beforeQueries.get(0);

        assertThat(queryInfo.getQuery()).isEqualTo(query);
        assertThat(queryInfo.getParameterSetOperations()).hasSize(2);
        assertThat(queryInfo.getParameterSetOperations().get(0).getOperations())
                .extracting("args", Object[].class).containsExactly(new Object[]{1, "FOO"}, new Object[]{2, 1});
        assertThat(queryInfo.getParameterSetOperations().get(1).getOperations())
                .extracting("args", Object[].class).containsExactly(new Object[]{1, "BAR"}, new Object[]{2, 2});

        List<QueryInfo> afterQueries = lastQueryListener.getAfterExecInfo().getQueries();
        assertThat(afterQueries).as("PreparedStatement batch execution will have always one query").hasSize(1);
        queryInfo = afterQueries.get(0);
        assertThat(queryInfo.getQuery()).isEqualTo(query);
        assertThat(queryInfo.getParameterSetOperations()).hasSize(2);
        assertThat(queryInfo.getParameterSetOperations().get(0).getOperations())
                .extracting("args", Object[].class).containsExactly(new Object[]{1, "FOO"}, new Object[]{2, 1});
        assertThat(queryInfo.getParameterSetOperations().get(1).getOperations())
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
        this.cleaner.add(stat);
        stat.setInt(1, 100);
        stat.setString(2, "FOO");
        stat.addBatch();

        stat.setInt(1, 200);
        stat.setString(2, "BAR");
        stat.addBatch();

        stat.executeBatch();  // 1st execution

        List<QueryInfo> afterQueries = lastQueryListener.getAfterExecInfo().getQueries();
        assertThat(afterQueries).as("should has one query").hasSize(1);
        QueryInfo queryInfo = afterQueries.get(0);
        assertThat(queryInfo.getParameterSetOperations()).as("should have two batch params").hasSize(2);

        stat.setInt(1, 300);
        stat.setString(2, "BAZ");
        stat.addBatch();

        int[] updateCount = stat.executeBatch();  // 2nd execution
        assertThat(updateCount).hasSize(1);

        // second execution should pass only one QueryInfo to listener
        afterQueries = lastQueryListener.getAfterExecInfo().getQueries();
        assertThat(afterQueries).isNotNull();
        assertThat(afterQueries).as("should pass one QueryInfo (BAZ)").hasSize(1);
        queryInfo = afterQueries.get(0);
        assertThat(queryInfo.getParameterSetOperations()).as("should have one batch params").hasSize(1);

        // verify actual data. 3 rows must be inserted, in addition to original data(2rows)
        int count = DbTestUtils.countTable(jdbcDataSource, "emp");
        assertThat(count).isEqualTo(5).as("2 existing data(foo,bar) and 3 insert(FOO,BAR,BAZ).");

    }

    @Test
    public void sameInstanceOfExecutionInfo() throws Exception {
        final String query = "select * from emp;";
        PreparedStatement stat = connection.prepareStatement(query);
        this.cleaner.add(stat);
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
        this.cleaner.add(conn);
        this.cleaner.add(ps);

        JdbcProxyFactory proxyFactory = new JdkJdbcProxyFactory();
        ProxyConfig proxyConfig = ProxyConfig.Builder.create().resultSetProxyLogicFactory(new SimpleResultSetProxyLogicFactory()).build();

        PreparedStatement proxyPs = proxyFactory.createPreparedStatement(ps, sql, new ConnectionInfo(), conn, proxyConfig, false);

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
        this.cleaner.add(conn);
        this.cleaner.add(ps);

        JdbcProxyFactory proxyFactory = new JdkJdbcProxyFactory();
        ProxyConfig proxyConfig = ProxyConfig.Builder.create().generatedKeysProxyLogicFactory(new SimpleResultSetProxyLogicFactory()).build();

        PreparedStatement proxyPs = proxyFactory.createPreparedStatement(ps, sql, new ConnectionInfo(), conn, proxyConfig, false);

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
        this.cleaner.add(conn);
        this.cleaner.add(ps);
        proxyPs = proxyFactory.createPreparedStatement(ps, sql, new ConnectionInfo(), conn, proxyConfig, false);

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
        this.cleaner.add(conn);
        this.cleaner.add(ps);

        final AtomicReference<ExecutionInfo> listenerReceivedExecutionInfo = new AtomicReference<ExecutionInfo>();
        ProxyDataSourceListener listener = new ProxyDataSourceListener() {
            @Override
            public void afterQuery(ExecutionInfo execInfo) {
                // since generatedKeys will NOT be closed, they can be read afterwards.
                listenerReceivedExecutionInfo.set(execInfo);
            }
        };

        // autoRetrieveGeneratedKeys=true
        ProxyConfig proxyConfig = ProxyConfig.Builder.create()
                .listener(listener)
                .autoRetrieveGeneratedKeys(true)
                .autoCloseGeneratedKeys(false)
                .build();
        JdbcProxyFactory proxyFactory = new JdkJdbcProxyFactory();
        PreparedStatement proxyPs = proxyFactory.createPreparedStatement(ps, sql, new ConnectionInfo(), conn, proxyConfig, true);

        proxyPs.executeUpdate();

        ExecutionInfo info = listenerReceivedExecutionInfo.get();
        assertThat(info).isNotNull();

        ResultSet generatedKeys = info.getGeneratedKeys();
        assertThat(generatedKeys).isInstanceOf(ResultSet.class);
        assertThat(Proxy.isProxyClass(generatedKeys.getClass())).isFalse();

        // calling "statement.getGeneratedKeys()" should return the same object
        ResultSet directGeneratedKeys = proxyPs.getGeneratedKeys();
        assertThat(directGeneratedKeys).isSameAs(generatedKeys);

        // verify generated keys ResultSet
        generatedKeys.next();
        int generatedId = generatedKeys.getInt(1);
        assertThat(generatedId).as("generated ID").isEqualTo(3); // sequence starts from 1 and 2 rows are inserted as init data

        // reset
        listenerReceivedExecutionInfo.set(null);

        // autoRetrieveGeneratedKeys=false
        proxyConfig = ProxyConfig.Builder.create()
                .listener(listener)
                .autoRetrieveGeneratedKeys(false)
                .autoCloseGeneratedKeys(false)
                .build();
        proxyPs = proxyFactory.createPreparedStatement(ps, sql, new ConnectionInfo(), conn, proxyConfig, false);

        proxyPs.executeUpdate();

        info = listenerReceivedExecutionInfo.get();
        assertThat(info).isNotNull();

        assertThat(info.getGeneratedKeys()).isNull();

    }

    @Test
    public void autoRetrieveGeneratedKeysWithGenerateKeyParameter() throws Throwable {
        String sql = "insert into emp_with_auto_id ( name ) values ('BAZ');";
        Connection conn = this.jdbcDataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        this.cleaner.add(conn);
        this.cleaner.add(ps);

        final AtomicReference<ResultSet> listenerReceivedGeneratedKeys = new AtomicReference<ResultSet>();
        ProxyDataSourceListener listener = new ProxyDataSourceListener() {
            @Override
            public void afterQuery(ExecutionInfo execInfo) {
                listenerReceivedGeneratedKeys.set(execInfo.getGeneratedKeys());
            }
        };

        // autoRetrieveGeneratedKeys=true
        ProxyConfig proxyConfig = ProxyConfig.Builder.create()
                .listener(listener)
                .autoRetrieveGeneratedKeys(true)
                .autoCloseGeneratedKeys(false)
                .build();
        JdbcProxyFactory proxyFactory = new JdkJdbcProxyFactory();
        PreparedStatement proxyPs;

        // for execute() method
        proxyPs = proxyFactory.createPreparedStatement(ps, sql, new ConnectionInfo(), conn, proxyConfig, true);
        proxyPs.execute();
        assertThat(listenerReceivedGeneratedKeys.get()).isNotNull();

        // for executeUpdate() method
        proxyPs = proxyFactory.createPreparedStatement(ps, sql, new ConnectionInfo(), conn, proxyConfig, true);
        proxyPs.executeUpdate();
        assertThat(listenerReceivedGeneratedKeys.get()).isNotNull();

        // for Postgres, executeLargeUpdate is not yet implemented.
        if (!DbTestUtils.isPostgres()) {

            // for executeLargeUpdate() method
            proxyPs = proxyFactory.createPreparedStatement(ps, sql, new ConnectionInfo(), conn, proxyConfig, true);
            proxyPs.executeLargeUpdate();
            assertThat(listenerReceivedGeneratedKeys.get()).isNotNull();
        }


        // When generateKey=false is specified

        // for execute() method
        proxyPs = proxyFactory.createPreparedStatement(ps, sql, new ConnectionInfo(), conn, proxyConfig, false);
        proxyPs.execute();
        assertThat(listenerReceivedGeneratedKeys.get()).isNull();

        // for executeUpdate() method
        proxyPs = proxyFactory.createPreparedStatement(ps, sql, new ConnectionInfo(), conn, proxyConfig, false);
        proxyPs.executeUpdate();
        assertThat(listenerReceivedGeneratedKeys.get()).isNull();

        // for Postgres, executeLargeUpdate is not yet implemented.
        if (!DbTestUtils.isPostgres()) {
            // for executeLargeUpdate() method
            proxyPs = proxyFactory.createPreparedStatement(ps, sql, new ConnectionInfo(), conn, proxyConfig, false);
            proxyPs.executeLargeUpdate();
            assertThat(listenerReceivedGeneratedKeys.get()).isNull();
        }

    }

    @Test
    public void autoRetrieveGeneratedKeysWithExecuteQueryMethod() throws Throwable {
        String sql = "select * from emp;";
        Connection conn = this.jdbcDataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        this.cleaner.add(conn);
        this.cleaner.add(ps);

        final AtomicReference<ResultSet> listenerReceivedGeneratedKeys = new AtomicReference<ResultSet>();
        ProxyDataSourceListener listener = new ProxyDataSourceListener() {
            @Override
            public void afterQuery(ExecutionInfo execInfo) {
                listenerReceivedGeneratedKeys.set(execInfo.getGeneratedKeys());
            }
        };

        // autoRetrieveGeneratedKeys=true
        ProxyConfig proxyConfig = ProxyConfig.Builder.create()
                .listener(listener)
                .autoRetrieveGeneratedKeys(true)
                .autoCloseGeneratedKeys(false)
                .build();
        JdbcProxyFactory proxyFactory = new JdkJdbcProxyFactory();
        PreparedStatement proxyPs;

        // for executeQuery() method, it should NOT retrieve generated-keys
        proxyPs = proxyFactory.createPreparedStatement(ps, sql, new ConnectionInfo(), conn, proxyConfig, true);
        proxyPs.executeQuery();
        assertThat(listenerReceivedGeneratedKeys.get()).isNull();

    }

    @Test
    public void autoRetrieveGeneratedKeysWithBatchPrepared() throws Throwable {
        String sql = "insert into emp_with_auto_id ( name ) values ('BAZ');";
        Connection conn = this.jdbcDataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        this.cleaner.add(conn);
        this.cleaner.add(ps);

        final AtomicReference<ResultSet> listenerReceivedGeneratedKeys = new AtomicReference<ResultSet>();
        ProxyDataSourceListener listener = new ProxyDataSourceListener() {
            @Override
            public void afterQuery(ExecutionInfo execInfo) {
                listenerReceivedGeneratedKeys.set(execInfo.getGeneratedKeys());
            }
        };

        // for default (expected to retrieve)
        JdbcProxyFactory proxyFactory = new JdkJdbcProxyFactory();
        ProxyConfig proxyConfig;
        PreparedStatement proxyPs;
        proxyConfig = ProxyConfig.Builder.create()
                .listener(listener)
                .autoRetrieveGeneratedKeys(true)
                .autoCloseGeneratedKeys(false)
                .build();

        proxyPs = proxyFactory.createPreparedStatement(ps, sql, new ConnectionInfo(), conn, proxyConfig, true);
        proxyPs.addBatch();
        proxyPs.addBatch();
        proxyPs.addBatch();
        proxyPs.executeBatch();
        assertThat(listenerReceivedGeneratedKeys.get()).isNotNull();
        listenerReceivedGeneratedKeys.set(null);

        // executeLargeBatch is not implemented in HSQLDB

        // autoRetrieve for batch prepared = true
        proxyConfig = ProxyConfig.Builder.create()
                .listener(listener)
                .autoRetrieveGeneratedKeys(true)
                .autoCloseGeneratedKeys(false)
                .retrieveGeneratedKeysForBatchPreparedOrCallable(true)  // set true
                .build();

        proxyPs = proxyFactory.createPreparedStatement(ps, sql, new ConnectionInfo(), conn, proxyConfig, true);
        proxyPs.addBatch();
        proxyPs.addBatch();
        proxyPs.addBatch();
        proxyPs.executeBatch();
        assertThat(listenerReceivedGeneratedKeys.get()).isNotNull();
        listenerReceivedGeneratedKeys.set(null);


        // autoRetrieve for batch prepared = false
        proxyConfig = ProxyConfig.Builder.create()
                .listener(listener)
                .autoRetrieveGeneratedKeys(true)
                .autoCloseGeneratedKeys(false)
                .retrieveGeneratedKeysForBatchPreparedOrCallable(false)  // set false
                .build();

        proxyPs = proxyFactory.createPreparedStatement(ps, sql, new ConnectionInfo(), conn, proxyConfig, true);
        proxyPs.addBatch();
        proxyPs.addBatch();
        proxyPs.addBatch();
        proxyPs.executeBatch();
        assertThat(listenerReceivedGeneratedKeys.get()).isNull();
        listenerReceivedGeneratedKeys.set(null);
    }

    @Test
    public void getGeneratedKeys() throws Throwable {
        String sql = "insert into emp_with_auto_id ( name ) values ('BAZ');";
        Connection conn = this.jdbcDataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        this.cleaner.add(conn);
        this.cleaner.add(ps);

        // when no configuration is specified for generated keys (disabling generated keys related feature)
        ProxyConfig proxyConfig = ProxyConfig.Builder.create().build();
        JdbcProxyFactory proxyFactory = new JdkJdbcProxyFactory();
        PreparedStatement proxyPs = proxyFactory.createPreparedStatement(ps, sql, new ConnectionInfo(), conn, proxyConfig, false);

        proxyPs.executeUpdate();

        // calling getGeneratedKeys() multiple time is not defined in JDBC spec
        // For hsqldb, calling second time closes previously returned ResultSet and returns new ResultSet.
        ResultSet generatedKeys1 = proxyPs.getGeneratedKeys();
        assertThat(generatedKeys1.isClosed()).isFalse();

        ResultSet generatedKeys2 = proxyPs.getGeneratedKeys();
        assertThat(generatedKeys2.isClosed()).isFalse();

        if (DbTestUtils.isHsql() || DbTestUtils.isMysql()) {
            // everytime it should return a new generatedKeys
            assertThat(generatedKeys2).isNotSameAs(generatedKeys1);
        } else {
            assertThat(generatedKeys2).isSameAs(generatedKeys1);
        }


        // only specify autoRetrieveGeneratedKeys=true
        proxyConfig = ProxyConfig.Builder.create()
                .autoRetrieveGeneratedKeys(true)
                .build();
        proxyPs = proxyFactory.createPreparedStatement(ps, sql, new ConnectionInfo(), conn, proxyConfig, false);

        proxyPs.executeUpdate();

        ResultSet generatedKeys3 = proxyPs.getGeneratedKeys();
        assertThat(generatedKeys3.isClosed()).isFalse();

        ResultSet generatedKeys4 = proxyPs.getGeneratedKeys();
        assertThat(generatedKeys4.isClosed()).isFalse();

        // since first generated-keys is open, second call should return the same one
        assertThat(generatedKeys4).isSameAs(generatedKeys3);

        // From here, it is specific to HSQL
        // TODO: Add tests for other DB
        if (!DbTestUtils.isHsql()) {
            return;
        }

        generatedKeys4.close();
        ResultSet generatedKeys5 = proxyPs.getGeneratedKeys();
        assertThat(generatedKeys5.isClosed()).isFalse();

        // once it is closed, getGeneratedKeys should return a new ResultSet
        assertThat(generatedKeys5).isNotSameAs(generatedKeys4);

        ResultSet generatedKeys6 = proxyPs.getGeneratedKeys();
        assertThat(generatedKeys6.isClosed()).isFalse();

        // again it's not closed, thus same ResultSet should be returned
        assertThat(generatedKeys6).isSameAs(generatedKeys5);

    }

    @Test
    public void getGeneratedKeysWithAutoRetrievalAndAutoCloseFalse() throws Throwable {
        String sql = "insert into emp_with_auto_id ( name ) values ('BAZ');";
        Connection conn = this.jdbcDataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        this.cleaner.add(conn);
        this.cleaner.add(ps);

        // autoCloseGeneratedKeys=false
        ProxyConfig proxyConfig = ProxyConfig.Builder.create()
                .autoRetrieveGeneratedKeys(true)
                .autoCloseGeneratedKeys(false)
                .build();
        JdbcProxyFactory proxyFactory = new JdkJdbcProxyFactory();
        PreparedStatement proxyPs = proxyFactory.createPreparedStatement(ps, sql, new ConnectionInfo(), conn, proxyConfig, false);

        proxyPs.executeUpdate();

        // while they are not closed, getGeneratedKeys() should return same object
        ResultSet generatedKeys1 = proxyPs.getGeneratedKeys();
        ResultSet generatedKeys2 = proxyPs.getGeneratedKeys();

        assertThat(generatedKeys2).isSameAs(generatedKeys1);

        // From here, it is HSQL specific
        // TODO: test with other DB
        if (!DbTestUtils.isHsql()) {
            return;
        }

        // when generatedKeys is closed, getGeneratedKeys() should return new ResultSet
        generatedKeys1.close();
        ResultSet generatedKeys3 = proxyPs.getGeneratedKeys();

        assertThat(generatedKeys3).isNotSameAs(generatedKeys1);
        assertThat(generatedKeys3.isClosed()).isFalse();

        // since generatedKeys3 is open, calling getGeneratedKeys() should return the same resultset
        ResultSet generatedKeys4 = proxyPs.getGeneratedKeys();
        assertThat(generatedKeys4).isSameAs(generatedKeys3);

    }

    @Test
    public void getGeneratedKeysWithAutoRetrievalAndAutoCloseTrue() throws Throwable {
        String sql = "insert into emp_with_auto_id ( name ) values ('BAZ');";
        Connection conn = this.jdbcDataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        this.cleaner.add(conn);
        this.cleaner.add(ps);

        // autoCloseGeneratedKeys=true
        ProxyConfig proxyConfig = ProxyConfig.Builder.create()
                .autoRetrieveGeneratedKeys(true)
                .autoCloseGeneratedKeys(true)
                .build();
        JdbcProxyFactory proxyFactory = new JdkJdbcProxyFactory();
        PreparedStatement proxyPs = proxyFactory.createPreparedStatement(ps, sql, new ConnectionInfo(), conn, proxyConfig, false);

        proxyPs.executeUpdate();

        // auto close should not affect the result of "getGeneratedKeys" method.
        ResultSet generatedKeys1 = proxyPs.getGeneratedKeys();
        assertThat(generatedKeys1.isClosed()).isFalse();

        ResultSet generatedKeys2 = proxyPs.getGeneratedKeys();
        assertThat(generatedKeys2.isClosed()).isFalse();

        // result of "getGeneratedKeys" is still open, thus second call of "getGeneratedKeys" should return the same one
        assertThat(generatedKeys2).isSameAs(generatedKeys1);
        assertThat(generatedKeys1.isClosed()).isFalse();
    }


    @Test
    public void autoCloseGeneratedKeysProxy() throws Throwable {
        String sql = "insert into emp_with_auto_id ( name ) values ('BAZ');";
        Connection conn = this.jdbcDataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        this.cleaner.add(conn);
        this.cleaner.add(ps);

        final AtomicReference<ExecutionInfo> listenerReceivedExecutionInfo = new AtomicReference<ExecutionInfo>();
        ProxyDataSourceListener listener = new ProxyDataSourceListener() {
            @Override
            public void afterQuery(ExecutionInfo execInfo) {
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
                .listener(listener)
                .autoRetrieveGeneratedKeys(true)
                .autoCloseGeneratedKeys(false)
                .build();
        JdbcProxyFactory proxyFactory = new JdkJdbcProxyFactory();
        PreparedStatement proxyPs = proxyFactory.createPreparedStatement(ps, sql, new ConnectionInfo(), conn, proxyConfig, true);

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
                .listener(listener)
                .autoRetrieveGeneratedKeys(true)
                .autoCloseGeneratedKeys(true)
                .build();
        proxyPs = proxyFactory.createPreparedStatement(ps, sql, new ConnectionInfo(), conn, proxyConfig, true);

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
        this.cleaner.add(conn);
        this.cleaner.add(ps);

        final AtomicReference<ExecutionInfo> listenerReceivedExecutionInfo = new AtomicReference<ExecutionInfo>();
        ProxyDataSourceListener listener = new ProxyDataSourceListener() {
            @Override
            public void afterQuery(ExecutionInfo execInfo) {
                listenerReceivedExecutionInfo.set(execInfo);
            }
        };

        // specify autoRetrieveGeneratedKeys and proxy factory
        ProxyConfig proxyConfig = ProxyConfig.Builder.create()
                .listener(listener)
                .autoRetrieveGeneratedKeys(true)
                .generatedKeysProxyLogicFactory(new SimpleResultSetProxyLogicFactory())
                .autoCloseGeneratedKeys(false)
                .build();
        JdbcProxyFactory proxyFactory = new JdkJdbcProxyFactory();
        PreparedStatement proxyPs = proxyFactory.createPreparedStatement(ps, sql, new ConnectionInfo(), conn, proxyConfig, true);

        proxyPs.executeUpdate();

        ExecutionInfo info = listenerReceivedExecutionInfo.get();
        assertThat(info).isNotNull();
        assertThat(info.getGeneratedKeys()).isInstanceOf(ResultSet.class);

        ResultSet generatedKeys = info.getGeneratedKeys();
        assertThat(Proxy.isProxyClass(generatedKeys.getClass())).isTrue();
        assertThat(Proxy.getInvocationHandler(generatedKeys)).isExactlyInstanceOf(ResultSetInvocationHandler.class);

        generatedKeys.next();
        int generatedId = generatedKeys.getInt(1);
        assertThat(generatedId).as("generated ID").isEqualTo(3);  // sequence starts from 1 and 2 rows are inserted as init data

    }

}