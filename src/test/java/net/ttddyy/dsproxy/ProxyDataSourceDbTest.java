package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.listener.LastExecutionAwareListener;
import net.ttddyy.dsproxy.listener.MethodExecutionContext;
import net.ttddyy.dsproxy.proxy.DataSourceProxyLogic;
import net.ttddyy.dsproxy.proxy.ProxyConfig;
import net.ttddyy.dsproxy.proxy.jdk.DataSourceInvocationHandler;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * TODO: clean up & rewrite
 *
 * @author Tadaya Tsuyukubo
 */
@DatabaseTest
public class ProxyDataSourceDbTest {

    private DataSource proxyDataSource;
    private TestListener listener;
    private LastExecutionAwareListener methodListener;

    private DataSource jdbcDataSource;
    private DbResourceCleaner cleaner;

    public ProxyDataSourceDbTest(DataSource jdbcDataSource, DbResourceCleaner cleaner) {
        this.jdbcDataSource = jdbcDataSource;
        this.cleaner = cleaner;
    }

    @BeforeEach
    void setup() {
        listener = new TestListener();
        methodListener = new LastExecutionAwareListener();

        proxyDataSource = ProxyDataSourceBuilder.create(this.jdbcDataSource)
                .listener(this.listener)
                .listener(this.methodListener)
                .build();
    }

    @Test
    public void testStatementWithExecuteUpdateQuery() throws Exception {
        Connection conn = proxyDataSource.getConnection();
        Statement st = conn.createStatement();
        this.cleaner.add(conn);
        this.cleaner.add(st);
        st.executeUpdate("create table aa ( a varchar(5) primary key );");

        assertThat(listener.getBeforeCount()).isEqualTo(1);
        assertThat(listener.getAfterCount()).isEqualTo(1);
    }

    @Test
    public void testStatementWithExecuteQuery() throws Exception {
        Connection conn = proxyDataSource.getConnection();
        Statement st = conn.createStatement();
        this.cleaner.add(conn);
        this.cleaner.add(st);
        st.executeQuery("SELECT * FROM INFORMATION_SCHEMA.TABLES;");  // hsqldb system table

        assertThat(listener.getBeforeCount()).isEqualTo(1);
        assertThat(listener.getAfterCount()).isEqualTo(1);
    }

    @Test
    public void testUseStatement() throws Exception {
        Connection conn = proxyDataSource.getConnection();
        Statement st = conn.createStatement();
        this.cleaner.add(conn);
        this.cleaner.add(st);
        st.executeQuery("select * from emp;");

        assertThat(listener.getBeforeCount()).isEqualTo(1);
        assertThat(listener.getAfterCount()).isEqualTo(1);
    }

    @Test
    public void testUsePreparedStatement() throws Exception {
        Connection conn = proxyDataSource.getConnection();
        PreparedStatement st = conn.prepareStatement("select * from emp");
        this.cleaner.add(conn);
        this.cleaner.add(st);
        st.executeQuery();

        assertThat(listener.getBeforeCount()).isEqualTo(1);
        assertThat(listener.getAfterCount()).isEqualTo(1);
    }

    @Test
    public void testUsePrepareCall() throws Exception {
        Connection conn = proxyDataSource.getConnection();
        CallableStatement st = conn.prepareCall("select * from emp");
        this.cleaner.add(conn);
        this.cleaner.add(st);
        st.execute();
    }

    @Test
    public void statementGetConnection() throws Exception {
        Connection proxyConn = proxyDataSource.getConnection();
        Statement st = proxyConn.createStatement();
        Connection conn = st.getConnection();
        this.cleaner.add(proxyConn);
        this.cleaner.add(conn);
        this.cleaner.add(st);

        assertThat(conn).isSameAs(proxyConn);
    }

    @Test
    public void preparedGetConnection() throws Exception {
        Connection proxyConn = proxyDataSource.getConnection();
        PreparedStatement ps = proxyConn.prepareStatement("select * from emp");
        Connection conn = ps.getConnection();
        this.cleaner.add(proxyConn);
        this.cleaner.add(conn);
        this.cleaner.add(ps);

        assertThat(conn).isSameAs(proxyConn);
    }

    @Test
    public void callableGetConnection() throws Exception {
        Connection proxyConn = proxyDataSource.getConnection();
        CallableStatement cs = proxyConn.prepareCall("select * from emp");
        Connection conn = cs.getConnection();
        this.cleaner.add(proxyConn);
        this.cleaner.add(conn);
        this.cleaner.add(cs);

        assertThat(conn).isSameAs(proxyConn);
    }

    @Test
    public void methodExecutionListener() throws Throwable {
        assertFalse(this.methodListener.isBeforeMethodCalled());
        assertFalse(this.methodListener.isAfterMethodCalled());

        Connection connection = proxyDataSource.getConnection();

        assertTrue(this.methodListener.isBeforeMethodCalled(), "methodListener should be called for getConnection");
        assertTrue(this.methodListener.isAfterMethodCalled(), "methodListener should be called for getConnection");

        MethodExecutionContext context = this.methodListener.getAfterMethodContext();
        assertThat(context.getTarget()).isSameAs(this.jdbcDataSource);
        assertThat(context.getResult()).isSameAs(connection);
        assertThat(context.getMethod().getDeclaringClass()).isSameAs(DataSource.class);
        assertThat(context.getMethod().getName()).isEqualTo("getConnection");
        assertThat(context.getConnectionInfo()).isNotNull();

        // adding connection set in cleaner calls hashCode() method on connection, thus call it after verification
        this.cleaner.add(connection);

        this.methodListener.reset();

        String username = DbTestUtils.getUsername();
        String password = DbTestUtils.getPassword();
        connection = proxyDataSource.getConnection(username, password);

        assertTrue(this.methodListener.isBeforeMethodCalled(), "methodListener should be called for getConnection");
        assertTrue(this.methodListener.isAfterMethodCalled(), "methodListener should be called for getConnection");

        this.cleaner.add(connection);
        this.methodListener.reset();

        proxyDataSource.getLoginTimeout();
        assertTrue(this.methodListener.isBeforeMethodCalled(), "methodListener should be called for getLoginTimeout");
        assertTrue(this.methodListener.isAfterMethodCalled(), "methodListener should be called for getLoginTimeout");

        this.methodListener.reset();

        proxyDataSource.setLoginTimeout(100);
        assertTrue(this.methodListener.isBeforeMethodCalled(), "methodListener should be called for setLoginTimeout");
        assertTrue(this.methodListener.isAfterMethodCalled(), "methodListener should be called for setLoginTimeout");

        this.methodListener.reset();

        PrintWriter writer = proxyDataSource.getLogWriter();
        assertTrue(this.methodListener.isBeforeMethodCalled(), "methodListener should be called for getLogWriter");
        assertTrue(this.methodListener.isAfterMethodCalled(), "methodListener should be called for getLogWriter");

        this.methodListener.reset();

        proxyDataSource.setLogWriter(writer);
        assertTrue(this.methodListener.isBeforeMethodCalled(), "methodListener should be called for setLogWriter");
        assertTrue(this.methodListener.isAfterMethodCalled(), "methodListener should be called for setLogWriter");
    }

    @Test
    public void connectionClose() throws Exception {
        ProxyConfig proxyConfig = getProxyConfig(this.proxyDataSource);
        ConnectionIdManager connIdManager = proxyConfig.getConnectionIdManager();
        Connection conn = proxyDataSource.getConnection();
        Statement st = conn.createStatement();
        this.cleaner.add(conn);
        this.cleaner.add(st);

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
        conn.setAutoCommit(false);
        Statement st = conn.createStatement();
        this.cleaner.add(conn);
        this.cleaner.add(st);

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

    private ProxyConfig getProxyConfig(DataSource proxyDataSource) {
        // reflectively retrieve ProxyConfig for test sake
        try {
            InvocationHandler ih = Proxy.getInvocationHandler(proxyDataSource);
            Field delegateField = DataSourceInvocationHandler.class.getDeclaredField("delegate");
            delegateField.setAccessible(true);
            DataSourceProxyLogic logic = (DataSourceProxyLogic) delegateField.get(ih);
            Field proxyConfigField = DataSourceProxyLogic.class.getDeclaredField("proxyConfig");
            proxyConfigField.setAccessible(true);
            return (ProxyConfig) proxyConfigField.get(logic);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
