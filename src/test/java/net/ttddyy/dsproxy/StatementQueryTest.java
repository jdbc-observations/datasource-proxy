package net.ttddyy.dsproxy;

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
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

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
        result = proxySt.getGeneratedKeys();
        assertThat(result).isInstanceOf(ResultSet.class);
        assertThat(Proxy.isProxyClass(result.getClass())).isTrue();
        assertThat(Proxy.getInvocationHandler(result)).isExactlyInstanceOf(ResultSetInvocationHandler.class);
    }

}