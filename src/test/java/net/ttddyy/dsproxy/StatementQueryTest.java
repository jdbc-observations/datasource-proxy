package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.listener.NoOpQueryExecutionListener;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.proxy.JdbcProxyFactory;
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
public class StatementQueryTest {

    private DataSource jdbcDataSource;


    @Before
    public void setup() throws Exception {
        // real datasource
        jdbcDataSource = TestUtils.getDataSourceWithData();
    }

    @After
    public void teardown() throws Exception {
        TestUtils.shutdown(jdbcDataSource);
    }

    @Test
    public void resultSetProxy() throws Throwable {
        Connection conn = this.jdbcDataSource.getConnection();
        Statement st = conn.createStatement();

        JdbcProxyFactory proxyFactory = new JdkJdbcProxyFactory();
        ProxyConfig proxyConfig = ProxyConfig.Builder.create().resultSetProxyLogicFactory(new SimpleResultSetProxyLogicFactory()).build();
        Statement proxySt = proxyFactory.createStatement(st, new ConnectionInfo(), conn, proxyConfig);

        // verify executeQuery
        ResultSet result = proxySt.executeQuery("select * from emp;");
        assertThat(result).isInstanceOf(ResultSet.class);
        assertThat(Proxy.isProxyClass(result.getClass())).isTrue();
        assertThat(Proxy.getInvocationHandler(result)).isExactlyInstanceOf(ResultSetInvocationHandler.class);

        // verify getResultSet
        proxySt.execute("select * from emp;");
        result = proxySt.getResultSet();
        assertThat(result).isInstanceOf(ResultSet.class);
        assertThat(Proxy.isProxyClass(result.getClass())).isTrue();
        assertThat(Proxy.getInvocationHandler(result)).isExactlyInstanceOf(ResultSetInvocationHandler.class);

        // verify getGeneratedKeys
        // generatedKeys have own proxy factory, thus expecting non-proxy to be returned
        result = proxySt.getGeneratedKeys();
        assertThat(result).isInstanceOf(ResultSet.class);
        assertThat(Proxy.isProxyClass(result.getClass())).isFalse();
    }

    @Test
    public void generatedKeysProxy() throws Throwable {
        Connection conn = this.jdbcDataSource.getConnection();
        Statement st = conn.createStatement();

        JdbcProxyFactory proxyFactory = new JdkJdbcProxyFactory();
        ProxyConfig proxyConfig = ProxyConfig.Builder.create().generatedKeysProxyLogicFactory(new SimpleResultSetProxyLogicFactory()).build();
        Statement proxySt = proxyFactory.createStatement(st, new ConnectionInfo(), conn, proxyConfig);

        // verify getGeneratedKeys
        ResultSet generatedKeys = proxySt.getGeneratedKeys();
        assertThat(generatedKeys).isInstanceOf(ResultSet.class);
        assertThat(Proxy.isProxyClass(generatedKeys.getClass())).isTrue();
        assertThat(Proxy.getInvocationHandler(generatedKeys)).isExactlyInstanceOf(ResultSetInvocationHandler.class);

        // other ResultSet returning methods should not return proxy

        // verify executeQuery
        ResultSet result = proxySt.executeQuery("select * from emp;");
        assertThat(result).isInstanceOf(ResultSet.class);
        assertThat(Proxy.isProxyClass(result.getClass())).isFalse();

        // generated keys should have empty proxied result set
        generatedKeys = proxySt.getGeneratedKeys();
        assertThat(generatedKeys).isInstanceOf(ResultSet.class);
        assertThat(Proxy.isProxyClass(generatedKeys.getClass())).isTrue();
        assertThat(generatedKeys.next()).isFalse();

        // verify getResultSet
        proxySt.execute("select * from emp;");
        result = proxySt.getResultSet();
        assertThat(result).isInstanceOf(ResultSet.class);
        assertThat(Proxy.isProxyClass(result.getClass())).isFalse();

        // generated keys should have empty proxied result set
        generatedKeys = proxySt.getGeneratedKeys();
        assertThat(generatedKeys).isInstanceOf(ResultSet.class);
        assertThat(Proxy.isProxyClass(generatedKeys.getClass())).isTrue();
        assertThat(generatedKeys.next()).isFalse();
    }

    @Test
    public void autoRetrieveGeneratedKeys() throws Throwable {
        Connection conn = this.jdbcDataSource.getConnection();
        Statement st = conn.createStatement();

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
        Statement proxySt = proxyFactory.createStatement(st, new ConnectionInfo(), conn, proxyConfig);

        proxySt.executeUpdate("insert into emp_with_auto_id ( name ) values ('BAZ');", Statement.RETURN_GENERATED_KEYS);

        ExecutionInfo info = listenerReceivedExecutionInfo.get();
        assertThat(info).isNotNull();

        ResultSet generatedKeys = info.getGeneratedKeys();
        assertThat(generatedKeys).isInstanceOf(ResultSet.class);
        assertThat(Proxy.isProxyClass(generatedKeys.getClass())).isFalse();

        // calling "statement.getGeneratedKeys()" should return the same object
        ResultSet directGeneratedKeys = proxySt.getGeneratedKeys();
        assertThat(directGeneratedKeys).isSameAs(generatedKeys);

        // verify generated keys ResultSet
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
        proxySt = proxyFactory.createStatement(st, new ConnectionInfo(), conn, proxyConfig);

        proxySt.executeUpdate("insert into emp_with_auto_id ( name ) values ('BAZ');", Statement.RETURN_GENERATED_KEYS);

        info = listenerReceivedExecutionInfo.get();
        assertThat(info).isNotNull();

        assertThat(info.getGeneratedKeys()).isNull();
    }

    @Test
    public void autoRetrieveGeneratedKeysWithExecuteQueryMethod() throws Throwable {
        Connection conn = this.jdbcDataSource.getConnection();
        Statement st = conn.createStatement();

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
        Statement proxySt = proxyFactory.createStatement(st, new ConnectionInfo(), conn, proxyConfig);

        // it should NOT generate keys for executeQuery method
        proxySt.executeQuery("insert into emp_with_auto_id ( name ) values ('BAZ');");
        assertThat(listenerReceivedExecutionInfo.get().getGeneratedKeys()).isNull();


    }

    @Test
    public void autoRetrieveGeneratedKeysWithQueryExecutionMethods() throws Throwable {
        Connection conn = this.jdbcDataSource.getConnection();
        Statement st = conn.createStatement();

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
        Statement proxySt = proxyFactory.createStatement(st, new ConnectionInfo(), conn, proxyConfig);


        // Test with NOT enabling generated-keys
        proxySt.execute("insert into emp_with_auto_id ( name ) values ('BAZ');");
        assertThat(listenerReceivedExecutionInfo.get().getGeneratedKeys()).isNull();

        proxySt = proxyFactory.createStatement(st, new ConnectionInfo(), conn, proxyConfig);
        proxySt.executeUpdate("insert into emp_with_auto_id ( name ) values ('BAZ');");
        assertThat(listenerReceivedExecutionInfo.get().getGeneratedKeys()).isNull();

        // Statement#executeLargeUpdate is not implemented in HSQL yet

        // Specify NO_GENERATED_KEYS
        proxySt.execute("insert into emp_with_auto_id ( name ) values ('BAZ');", Statement.NO_GENERATED_KEYS);
        assertThat(listenerReceivedExecutionInfo.get().getGeneratedKeys()).isNull();

        proxySt = proxyFactory.createStatement(st, new ConnectionInfo(), conn, proxyConfig);
        proxySt.executeUpdate("insert into emp_with_auto_id ( name ) values ('BAZ');", Statement.NO_GENERATED_KEYS);
        assertThat(listenerReceivedExecutionInfo.get().getGeneratedKeys()).isNull();


        // Test with enabling generated-keys

        // with Statement.RETURN_GENERATED_KEYS
        proxySt = proxyFactory.createStatement(st, new ConnectionInfo(), conn, proxyConfig);
        proxySt.execute("insert into emp_with_auto_id ( name ) values ('BAZ');", Statement.RETURN_GENERATED_KEYS);
        assertThat(listenerReceivedExecutionInfo.get().getGeneratedKeys()).isNotNull();
        listenerReceivedExecutionInfo.set(null);

        proxySt = proxyFactory.createStatement(st, new ConnectionInfo(), conn, proxyConfig);
        proxySt.executeUpdate("insert into emp_with_auto_id ( name ) values ('BAZ');", Statement.RETURN_GENERATED_KEYS);
        assertThat(listenerReceivedExecutionInfo.get().getGeneratedKeys()).isNotNull();
        listenerReceivedExecutionInfo.set(null);

        // with int[]
        proxySt = proxyFactory.createStatement(st, new ConnectionInfo(), conn, proxyConfig);
        proxySt.execute("insert into emp_with_auto_id ( name ) values ('BAZ');", new int[]{1});
        assertThat(listenerReceivedExecutionInfo.get().getGeneratedKeys()).isNotNull();
        listenerReceivedExecutionInfo.set(null);

        proxySt = proxyFactory.createStatement(st, new ConnectionInfo(), conn, proxyConfig);
        proxySt.executeUpdate("insert into emp_with_auto_id ( name ) values ('BAZ');", new int[]{1});
        assertThat(listenerReceivedExecutionInfo.get().getGeneratedKeys()).isNotNull();
        listenerReceivedExecutionInfo.set(null);

        // with String[]
        proxySt = proxyFactory.createStatement(st, new ConnectionInfo(), conn, proxyConfig);
        proxySt.execute("insert into emp_with_auto_id ( name ) values ('BAZ');", new String[]{"id"});
        assertThat(listenerReceivedExecutionInfo.get().getGeneratedKeys()).isNotNull();
        listenerReceivedExecutionInfo.set(null);

        proxySt = proxyFactory.createStatement(st, new ConnectionInfo(), conn, proxyConfig);
        proxySt.executeUpdate("insert into emp_with_auto_id ( name ) values ('BAZ');", new String[]{"id"});
        assertThat(listenerReceivedExecutionInfo.get().getGeneratedKeys()).isNotNull();
        listenerReceivedExecutionInfo.set(null);

    }

    @Test
    public void autoRetrieveGeneratedKeysWithBatchStatement() throws Throwable {
        Connection conn = this.jdbcDataSource.getConnection();
        Statement st = conn.createStatement();

        final AtomicReference<ExecutionInfo> listenerReceivedExecutionInfo = new AtomicReference<ExecutionInfo>();
        QueryExecutionListener listener = new NoOpQueryExecutionListener() {
            @Override
            public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
                // since generatedKeys will NOT be closed, they can be read afterwards.
                listenerReceivedExecutionInfo.set(execInfo);
            }
        };

        JdbcProxyFactory proxyFactory = new JdkJdbcProxyFactory();
        ProxyConfig proxyConfig;
        Statement proxySt;

        // default value (expected to NOT auto-retrieve)
        proxyConfig = ProxyConfig.Builder.create()
                .queryListener(listener)
                .autoRetrieveGeneratedKeys(true)
                .autoCloseGeneratedKeys(false)
                .build();
        proxySt = proxyFactory.createStatement(st, new ConnectionInfo(), conn, proxyConfig);

        proxySt.addBatch("insert into emp_with_auto_id ( name ) values ('BAZ');");
        proxySt.addBatch("insert into emp_with_auto_id ( name ) values ('BAZ');");
        proxySt.addBatch("insert into emp_with_auto_id ( name ) values ('BAZ');");
        proxySt.executeBatch();
        assertThat(listenerReceivedExecutionInfo.get().getGeneratedKeys()).isNull();
        listenerReceivedExecutionInfo.set(null);

        // executeLargeBatch is not implemented for HSQLDB

        // autoRetrieve for batch statement = true
        proxyConfig = ProxyConfig.Builder.create()
                .queryListener(listener)
                .autoRetrieveGeneratedKeys(true)
                .autoCloseGeneratedKeys(false)
                .retrieveGeneratedKeysForBatchStatement(true)  // set true
                .build();
        proxySt = proxyFactory.createStatement(st, new ConnectionInfo(), conn, proxyConfig);

        proxySt.addBatch("insert into emp_with_auto_id ( name ) values ('BAZ');");
        proxySt.addBatch("insert into emp_with_auto_id ( name ) values ('BAZ');");
        proxySt.addBatch("insert into emp_with_auto_id ( name ) values ('BAZ');");
        proxySt.executeBatch();
        assertThat(listenerReceivedExecutionInfo.get().getGeneratedKeys()).isNotNull();
        listenerReceivedExecutionInfo.set(null);


        // autoRetrieve for batch statement = false
        proxyConfig = ProxyConfig.Builder.create()
                .queryListener(listener)
                .autoRetrieveGeneratedKeys(true)
                .autoCloseGeneratedKeys(false)
                .retrieveGeneratedKeysForBatchStatement(false)  // set false
                .build();
        proxySt = proxyFactory.createStatement(st, new ConnectionInfo(), conn, proxyConfig);

        proxySt.addBatch("insert into emp_with_auto_id ( name ) values ('BAZ');");
        proxySt.addBatch("insert into emp_with_auto_id ( name ) values ('BAZ');");
        proxySt.addBatch("insert into emp_with_auto_id ( name ) values ('BAZ');");
        proxySt.executeBatch();
        assertThat(listenerReceivedExecutionInfo.get().getGeneratedKeys()).isNull();
    }


    @Test
    public void getGeneratedKeys() throws Throwable {
        Connection conn = this.jdbcDataSource.getConnection();
        Statement st = conn.createStatement();

        // when no configuration is specified for generated keys (disabling generated keys related feature)
        ProxyConfig proxyConfig = ProxyConfig.Builder.create().build();
        JdbcProxyFactory proxyFactory = new JdkJdbcProxyFactory();
        Statement proxySt = proxyFactory.createStatement(st, new ConnectionInfo(), conn, proxyConfig);

        proxySt.executeUpdate("insert into emp_with_auto_id ( name ) values ('BAZ');", Statement.RETURN_GENERATED_KEYS);

        // calling getGeneratedKeys() multiple time is not defined in JDBC spec
        // For hsqldb, calling second time closes previously returned ResultSet and returns new ResultSet.
        ResultSet generatedKeys1 = proxySt.getGeneratedKeys();
        assertThat(generatedKeys1.isClosed()).isFalse();

        ResultSet generatedKeys2 = proxySt.getGeneratedKeys();
        assertThat(generatedKeys2.isClosed()).isFalse();

        // everytime it should return a new generatedKeys
        assertThat(generatedKeys2).isNotSameAs(generatedKeys1);


        // only specify autoRetrieveGeneratedKeys=true
        proxyConfig = ProxyConfig.Builder.create()
                .autoRetrieveGeneratedKeys(true)
                .build();
        proxySt = proxyFactory.createStatement(st, new ConnectionInfo(), conn, proxyConfig);

        proxySt.executeUpdate("insert into emp_with_auto_id ( name ) values ('BAZ');", Statement.RETURN_GENERATED_KEYS);

        ResultSet generatedKeys3 = proxySt.getGeneratedKeys();
        assertThat(generatedKeys3.isClosed()).isFalse();

        ResultSet generatedKeys4 = proxySt.getGeneratedKeys();
        assertThat(generatedKeys4.isClosed()).isFalse();

        // since first generated-keys is open, second call should return the same one
        assertThat(generatedKeys4).isSameAs(generatedKeys3);

        generatedKeys4.close();
        ResultSet generatedKeys5 = proxySt.getGeneratedKeys();
        assertThat(generatedKeys5.isClosed()).isFalse();

        // once it is closed, getGeneratedKeys should return a new ResultSet
        assertThat(generatedKeys5).isNotSameAs(generatedKeys4);

        ResultSet generatedKeys6 = proxySt.getGeneratedKeys();
        assertThat(generatedKeys6.isClosed()).isFalse();

        // again it's not closed, thus same ResultSet should be returned
        assertThat(generatedKeys6).isSameAs(generatedKeys5);

    }

    @Test
    public void getGeneratedKeysWithAutoRetrievalAndAutoCloseFalse() throws Throwable {
        Connection conn = this.jdbcDataSource.getConnection();
        Statement st = conn.createStatement();

        // autoCloseGeneratedKeys=false
        ProxyConfig proxyConfig = ProxyConfig.Builder.create()
                .autoRetrieveGeneratedKeys(true)
                .autoCloseGeneratedKeys(false)
                .build();
        JdbcProxyFactory proxyFactory = new JdkJdbcProxyFactory();
        Statement proxySt = proxyFactory.createStatement(st, new ConnectionInfo(), conn, proxyConfig);

        proxySt.executeUpdate("insert into emp_with_auto_id ( name ) values ('BAZ');", Statement.RETURN_GENERATED_KEYS);

        // while they are not closed, getGeneratedKeys() should return same object
        ResultSet generatedKeys1 = proxySt.getGeneratedKeys();
        ResultSet generatedKeys2 = proxySt.getGeneratedKeys();

        assertThat(generatedKeys2).isSameAs(generatedKeys1);

        // when generatedKeys is closed, getGeneratedKeys() should return new ResultSet
        generatedKeys1.close();
        ResultSet generatedKeys3 = proxySt.getGeneratedKeys();

        assertThat(generatedKeys3).isNotSameAs(generatedKeys1);
        assertThat(generatedKeys3.isClosed()).isFalse();

        // since generatedKeys3 is open, calling getGeneratedKeys() should return the same resultset
        ResultSet generatedKeys4 = proxySt.getGeneratedKeys();
        assertThat(generatedKeys4).isSameAs(generatedKeys3);

    }

    @Test
    public void getGeneratedKeysWithAutoRetrievalAndAutoCloseTrue() throws Throwable {
        Connection conn = this.jdbcDataSource.getConnection();
        Statement st = conn.createStatement();

        // autoCloseGeneratedKeys=true
        ProxyConfig proxyConfig = ProxyConfig.Builder.create()
                .autoRetrieveGeneratedKeys(true)
                .autoCloseGeneratedKeys(true)
                .build();
        JdbcProxyFactory proxyFactory = new JdkJdbcProxyFactory();
        Statement proxySt = proxyFactory.createStatement(st, new ConnectionInfo(), conn, proxyConfig);

        proxySt.executeUpdate("insert into emp_with_auto_id ( name ) values ('BAZ');", Statement.RETURN_GENERATED_KEYS);

        // auto close should not affect the result of "getGeneratedKeys" method.
        ResultSet generatedKeys1 = proxySt.getGeneratedKeys();
        assertThat(generatedKeys1.isClosed()).isFalse();

        ResultSet generatedKeys2 = proxySt.getGeneratedKeys();
        assertThat(generatedKeys2.isClosed()).isFalse();

        // result of "getGeneratedKeys" is still open, thus second call of "getGeneratedKeys" should return the same one
        assertThat(generatedKeys2).isSameAs(generatedKeys1);
        assertThat(generatedKeys1.isClosed()).isFalse();
    }

    @Test
    public void autoCloseGeneratedKeysProxy() throws Throwable {
        Connection conn = this.jdbcDataSource.getConnection();
        Statement st = conn.createStatement();

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
        JdbcProxyFactory proxyFactory = new JdkJdbcProxyFactory();
        ProxyConfig proxyConfig = ProxyConfig.Builder.create()
                .queryListener(listener)
                .autoRetrieveGeneratedKeys(true)
                .autoCloseGeneratedKeys(false)
                .build();
        Statement proxySt = proxyFactory.createStatement(st, new ConnectionInfo(), conn, proxyConfig);

        proxySt.executeUpdate("insert into emp_with_auto_id ( name ) values ('BAZ');", Statement.RETURN_GENERATED_KEYS);

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
        proxySt = proxyFactory.createStatement(st, new ConnectionInfo(), conn, proxyConfig);

        proxySt.executeUpdate("insert into emp_with_auto_id ( name ) values ('QUX');", Statement.RETURN_GENERATED_KEYS);

        info = listenerReceivedExecutionInfo.get();
        generatedKeys = info.getGeneratedKeys();
        assertThat(generatedKeys.isClosed()).isTrue();

    }

    @Test
    public void autoRetrieveGeneratedKeysWithGeneratedKeysProxy() throws Throwable {
        Connection conn = this.jdbcDataSource.getConnection();
        Statement st = conn.createStatement();

        final AtomicReference<ExecutionInfo> listenerReceivedExecutionInfo = new AtomicReference<ExecutionInfo>();
        QueryExecutionListener listener = new NoOpQueryExecutionListener() {
            @Override
            public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
                listenerReceivedExecutionInfo.set(execInfo);
            }
        };

        // specify autoRetrieveGeneratedKeys and proxy factory
        JdbcProxyFactory proxyFactory = new JdkJdbcProxyFactory();
        ProxyConfig proxyConfig = ProxyConfig.Builder.create()
                .queryListener(listener)
                .autoRetrieveGeneratedKeys(true)
                .generatedKeysProxyLogicFactory(new SimpleResultSetProxyLogicFactory())
                .autoCloseGeneratedKeys(false)
                .build();

        Statement proxySt = proxyFactory.createStatement(st, new ConnectionInfo(), conn, proxyConfig);

        proxySt.executeUpdate("insert into emp_with_auto_id ( name ) values ('BAZ');", Statement.RETURN_GENERATED_KEYS);

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