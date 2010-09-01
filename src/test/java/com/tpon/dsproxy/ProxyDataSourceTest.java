package com.tpon.dsproxy;

import com.tpon.dsproxy.support.ProxyDataSource;
import org.hsqldb.jdbc.jdbcDataSource;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * TODO: clean up & rewrite
 *
 * @author Tadaya Tsuyukubo
 */
public class ProxyDataSourceTest {

    private ProxyDataSource proxyDataSource;
    private TestListener listener;

    @BeforeMethod
    public void setup() throws Exception {
        DataSource dataSource = TestUtils.getDataSourceWithData();

        listener = new TestListener();

        proxyDataSource = new ProxyDataSource();
        proxyDataSource.setDataSource(dataSource);
        proxyDataSource.setListener(listener);
    }

    @AfterMethod
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

        assertEquals(listener.getBeforeCount(), 1);
        assertEquals(listener.getAfterCount(), 1);
    }

    @Test
    public void testStatementWithExecuteQuery() throws Exception {
        Connection conn = proxyDataSource.getConnection();
        Statement st = conn.createStatement();
        st.executeQuery("create table aa ( a varchar(5) primary key );");

        assertEquals(listener.getBeforeCount(), 1);
        assertEquals(listener.getAfterCount(), 1);
    }

    @Test
    public void testUseStatement() throws Exception {
        Connection conn = proxyDataSource.getConnection();
        Statement st = conn.createStatement();
        st.executeQuery("select * from emp;");

        assertEquals(listener.getBeforeCount(), 1);
        assertEquals(listener.getAfterCount(), 1);
    }

    @Test
    public void testUsePreparedStatement() throws Exception {
        Connection conn = proxyDataSource.getConnection();
        PreparedStatement st = conn.prepareStatement("select * from emp");
        st.executeQuery();

        assertEquals(listener.getBeforeCount(), 1);
        assertEquals(listener.getAfterCount(), 1);
    }

    @Test
    public void testUsePreapareCall() throws Exception {
        Connection conn = proxyDataSource.getConnection();
        CallableStatement st = conn.prepareCall("select * from emp");
        st.execute();
    }

    public void question() throws Exception {
        jdbcDataSource dataSource = new jdbcDataSource();
        dataSource.setDatabase("jdbc:hsqldb:mem:aname");
        dataSource.setUser("sa");

        Connection conn = dataSource.getConnection();

        Statement st = conn.createStatement();
        st.executeUpdate("create table foo ( a varchar(5) primary key );");
        st.executeUpdate("insert into foo ( a )values ('foo');");
        st.executeUpdate("create table bar ( a varchar(5) primary key );");
        st.executeUpdate("insert into bar ( a )values ('bar');");


        PreparedStatement ps = conn.prepareStatement("select * from foo");
//        ResultSet rs = st.executeQuery("select * from bar");
        ResultSet rs = ps.executeQuery("select * from bar"); // => throws exception

        rs.next();
        String val = rs.getString("a");
        System.out.println(val);

    }
}
