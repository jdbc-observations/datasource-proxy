package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.support.ProxyDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertSame;


/**
 * TODO: clean up & rewrite
 *
 * @author Tadaya Tsuyukubo
 */
public class ProxyDataSourceTest {

    private ProxyDataSource proxyDataSource;
    private TestListener listener;

    @Before
    public void setup() throws Exception {
        DataSource dataSource = TestUtils.getDataSourceWithData();

        listener = new TestListener();

        proxyDataSource = new ProxyDataSource();
        proxyDataSource.setDataSource(dataSource);
        proxyDataSource.setListener(listener);
    }

    @After
    public void teardown() throws Exception {
        TestUtils.shutdown(proxyDataSource);
    }

    public void example() throws Exception {
        Connection conn = proxyDataSource.getConnection();
        Statement st = conn.createStatement();
        st.executeUpdate("create table aa ( a varchar(5) primary key );");
        st.executeUpdate("insert into aa ( a )values ('abc');");
        ResultSet rs = st.executeQuery("select a from aa;");
        rs.next();
        String val = rs.getString("a");
        System.out.println(val);
    }

    @Test
    public void testStatementWithExecuteUpdateQuery() throws Exception {
        Connection conn = proxyDataSource.getConnection();
        Statement st = conn.createStatement();
        st.executeUpdate("create table aa ( a varchar(5) primary key );");

        assertThat(listener.getBeforeCount()).isEqualTo(1);
        assertThat(listener.getAfterCount()).isEqualTo(1);
    }

    @Test
    public void testStatementWithExecuteQuery() throws Exception {
        Connection conn = proxyDataSource.getConnection();
        Statement st = conn.createStatement();
        st.executeQuery("SELECT * FROM INFORMATION_SCHEMA.TABLES;");  // hsqldb system table

        assertThat(listener.getBeforeCount()).isEqualTo(1);
        assertThat(listener.getAfterCount()).isEqualTo(1);
    }

    @Test
    public void testUseStatement() throws Exception {
        Connection conn = proxyDataSource.getConnection();
        Statement st = conn.createStatement();
        st.executeQuery("select * from emp;");

        assertThat(listener.getBeforeCount()).isEqualTo(1);
        assertThat(listener.getAfterCount()).isEqualTo(1);
    }

    @Test
    public void testUsePreparedStatement() throws Exception {
        Connection conn = proxyDataSource.getConnection();
        PreparedStatement st = conn.prepareStatement("select * from emp");
        st.executeQuery();

        assertThat(listener.getBeforeCount()).isEqualTo(1);
        assertThat(listener.getAfterCount()).isEqualTo(1);
    }

    @Test
    public void testUsePreapareCall() throws Exception {
        Connection conn = proxyDataSource.getConnection();
        CallableStatement st = conn.prepareCall("select * from emp");
        st.execute();
    }

    @Test
    public void statementGetConnection() throws Exception {
        Connection proxyConn = proxyDataSource.getConnection();
        Statement st = proxyConn.createStatement();
        Connection conn = st.getConnection();

        assertThat(conn).isSameAs(proxyConn);
    }

    @Test
    public void preparedGetConnection() throws Exception {
        Connection proxyConn = proxyDataSource.getConnection();
        PreparedStatement ps = proxyConn.prepareStatement("select * from emp");
        Connection conn = ps.getConnection();

        assertThat(conn).isSameAs(proxyConn);
    }

    @Test
    public void callableGetConnection() throws Exception {
        Connection proxyConn = proxyDataSource.getConnection();
        CallableStatement cs = proxyConn.prepareCall("select * from emp");
        Connection conn = cs.getConnection();

        assertThat(conn).isSameAs(proxyConn);
    }

}
