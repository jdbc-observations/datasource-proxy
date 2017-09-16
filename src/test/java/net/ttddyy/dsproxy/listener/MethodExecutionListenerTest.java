package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.TestUtils;
import net.ttddyy.dsproxy.support.ProxyDataSource;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

/**
 * @author Tadaya Tsuyukubo
 */
public class MethodExecutionListenerTest {

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
    public void test() throws Throwable {

        ProxyDataSource ds = ProxyDataSourceBuilder.create(this.jdbcDataSource).build();
        Connection conn = ds.getConnection();
        Statement statement = conn.createStatement();
        Connection c = statement.getConnection();
    }
}
