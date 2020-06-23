package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.listener.CallCheckMethodExecutionListener;
import net.ttddyy.dsproxy.listener.MethodExecutionContext;
import net.ttddyy.dsproxy.proxy.ProxyConfig;
import net.ttddyy.dsproxy.support.ProxyDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.withSettings;


/**
 * TODO: clean up & rewrite
 *
 * @author Tadaya Tsuyukubo
 */
public class ProxyDataSourceTest {

    private ProxyDataSource proxyDataSource;
    private TestListener listener;
    private CallCheckMethodExecutionListener methodListener;

    @Before
    public void setup() throws Exception {
        DataSource dataSource = TestUtils.getDataSourceWithData();

        listener = new TestListener();
        methodListener = new CallCheckMethodExecutionListener();

        ProxyConfig proxyConfig = ProxyConfig.Builder.create()
                .queryListener(this.listener)
                .methodListener(this.methodListener)
                .build();

        proxyDataSource = new ProxyDataSource();
        proxyDataSource.setDataSource(dataSource);
        proxyDataSource.setProxyConfig(proxyConfig);
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

    @Test
    public void methodExecutionListener() throws Throwable {
        assertFalse(this.methodListener.isBeforeMethodCalled());
        assertFalse(this.methodListener.isAfterMethodCalled());

        Connection connection = proxyDataSource.getConnection();

        assertTrue("methodListener should be called for getConnection", this.methodListener.isBeforeMethodCalled());
        assertTrue("methodListener should be called for getConnection", this.methodListener.isAfterMethodCalled());

        MethodExecutionContext context = this.methodListener.getAfterMethodContext();
        assertThat(context.getTarget()).isSameAs(proxyDataSource);
        assertThat(context.getResult()).isSameAs(connection);
        assertThat(context.getMethod().getDeclaringClass()).isSameAs(DataSource.class);
        assertThat(context.getMethod().getName()).isEqualTo("getConnection");
        assertThat(context.getConnectionInfo()).isNotNull();

        this.methodListener.reset();

        proxyDataSource.getConnection("sa", "");

        assertTrue("methodListener should be called for getConnection", this.methodListener.isBeforeMethodCalled());
        assertTrue("methodListener should be called for getConnection", this.methodListener.isAfterMethodCalled());

        this.methodListener.reset();

        // for now, only getConnection is supported for method execution listener

        proxyDataSource.close();
        assertFalse("methodListener should NOT be called for close", this.methodListener.isBeforeMethodCalled());
        assertFalse("methodListener should NOT be called for close", this.methodListener.isAfterMethodCalled());

        this.methodListener.reset();

        proxyDataSource.getLoginTimeout();
        assertFalse("methodListener should NOT be called for getLoginTimeout", this.methodListener.isBeforeMethodCalled());
        assertFalse("methodListener should NOT be called for getLoginTimeout", this.methodListener.isAfterMethodCalled());

        this.methodListener.reset();

        proxyDataSource.setLoginTimeout(100);
        assertFalse("methodListener should NOT be called for setLoginTimeout", this.methodListener.isBeforeMethodCalled());
        assertFalse("methodListener should NOT be called for setLoginTimeout", this.methodListener.isAfterMethodCalled());

        this.methodListener.reset();

        PrintWriter writer = proxyDataSource.getLogWriter();
        assertFalse("methodListener should NOT be called for getLogWriter", this.methodListener.isBeforeMethodCalled());
        assertFalse("methodListener should NOT be called for getLogWriter", this.methodListener.isAfterMethodCalled());

        proxyDataSource.setLogWriter(writer);
        assertFalse("methodListener should NOT be called for setLogWriter", this.methodListener.isBeforeMethodCalled());
        assertFalse("methodListener should NOT be called for setLogWriter", this.methodListener.isAfterMethodCalled());
    }

    @Test
    public void connectionClose() throws Exception {
        ConnectionIdManager connIdManager = proxyDataSource.getConnectionIdManager();
        Connection conn = proxyDataSource.getConnection();
        Statement st = conn.createStatement();

        ConnectionInfo connInfo = this.methodListener.getBeforeMethodContext().getConnectionInfo();
        assertThat(connInfo.isClosed()).isFalse();
        assertThat(connIdManager.getOpenConnectionIds()).containsOnly(connInfo.getConnectionId());

        st.close();
        assertThat(connInfo.isClosed()).isFalse();
        assertThat(connIdManager.getOpenConnectionIds()).containsOnly(connInfo.getConnectionId());

        conn.close();
        assertThat(connInfo.isClosed()).isTrue();
        assertThat(connIdManager.getOpenConnectionIds()).isEmpty();
    }

    @Test
    public void commitAndRollbackCount() throws Exception {
        Connection conn = proxyDataSource.getConnection();
        Statement st = conn.createStatement();

        ConnectionInfo connInfo = this.methodListener.getBeforeMethodContext().getConnectionInfo();

        st.close();
        conn.commit();
        assertThat(connInfo.getCommitCount()).isEqualTo(1);
        assertThat(connInfo.getRollbackCount()).isEqualTo(0);

        conn.commit();
        assertThat(connInfo.getCommitCount()).isEqualTo(2);
        assertThat(connInfo.getRollbackCount()).isEqualTo(0);

        conn.rollback();
        assertThat(connInfo.getCommitCount()).isEqualTo(2);
        assertThat(connInfo.getRollbackCount()).isEqualTo(1);

        conn.rollback();
        assertThat(connInfo.getCommitCount()).isEqualTo(2);
        assertThat(connInfo.getRollbackCount()).isEqualTo(2);
    }


    @Test
    public void autoCloseable() throws Exception {
        // DS that implements AutoCloseable but not Closeable
        DataSource ds = mock(DataSource.class, withSettings().extraInterfaces(AutoCloseable.class));
        ProxyDataSource proxyDS = new ProxyDataSource(ds);

        proxyDS.close();

        verify((AutoCloseable) ds).close();
    }

}
