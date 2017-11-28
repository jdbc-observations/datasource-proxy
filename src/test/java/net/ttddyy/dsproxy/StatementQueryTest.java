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