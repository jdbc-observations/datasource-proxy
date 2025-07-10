package net.ttddyy.dsproxy;

import java.io.Closeable;
import java.util.concurrent.atomic.AtomicBoolean;
import net.ttddyy.dsproxy.listener.CallCheckMethodExecutionListener;
import net.ttddyy.dsproxy.listener.MethodExecutionContext;
import net.ttddyy.dsproxy.proxy.ProxyConfig;
import net.ttddyy.dsproxy.support.ProxyDataSource;
import org.assertj.core.api.ThrowableAssert;
import org.hsqldb.jdbc.JDBCDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.SmartDataSource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;


/**
 * TODO: clean up & rewrite
 *
 * @author Tadaya Tsuyukubo
 * @author Réda Housni Alaoui
 */
public class ProxyDataSourceTest {

    private DataSource originalDataSource;
    private ProxyDataSource proxyDataSource;
    private TestListener listener;
    private CallCheckMethodExecutionListener methodListener;

    @Before
    public void setup() throws Exception {
        this.originalDataSource = TestUtils.getDataSourceWithData();

        this.listener = new TestListener();
        this.methodListener = new CallCheckMethodExecutionListener();

        ProxyConfig proxyConfig = ProxyConfig.Builder.create()
                .queryListener(this.listener)
                .methodListener(this.methodListener)
                .build();

        this.proxyDataSource = new ProxyDataSource();
        this.proxyDataSource.setDataSource(this.originalDataSource);
        this.proxyDataSource.setProxyConfig(proxyConfig);
    }

    @After
    public void teardown() throws Exception {
        TestUtils.shutdown(this.proxyDataSource);
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
        assertThat(context.getTarget()).isSameAs(this.originalDataSource);
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

	@Test
	public void closeProxyOfAutoCloseableViaClose() throws Exception {
		DataSource ds = mock(DataSource.class);
		when(ds.isWrapperFor(AutoCloseable.class)).thenReturn(true);

		AtomicBoolean closed = new AtomicBoolean();
		AutoCloseable autoCloseable = () -> closed.set(true);
		when(ds.unwrap(AutoCloseable.class)).thenReturn(autoCloseable);

		new ProxyDataSource(ds).close();
		assertThat(closed).isTrue();
	}

	@Test
	public void closeProxyOfCloseableViaClose() throws Exception {
		DataSource ds = mock(DataSource.class);
		when(ds.isWrapperFor(Closeable.class)).thenReturn(true);

		AtomicBoolean closed = new AtomicBoolean();
		Closeable closeable = () -> closed.set(true);
		when(ds.unwrap(Closeable.class)).thenReturn(closeable);

		new ProxyDataSource(ds).close();
		assertThat(closed).isTrue();
	}

	@Test
	public void closeProxyOfAutoCloseableViaUnwrap() throws Exception {
		DataSource ds = mock(DataSource.class);
		when(ds.isWrapperFor(AutoCloseable.class)).thenReturn(true);

		AtomicBoolean closed = new AtomicBoolean();
		AutoCloseable autoCloseable = () -> closed.set(true);
		when(ds.unwrap(AutoCloseable.class)).thenReturn(autoCloseable);

		new ProxyDataSource(ds).unwrap(AutoCloseable.class).close();
		assertThat(closed).isTrue();
	}

	@Test
	public void closeProxyOfCloseableViaUnwrap() throws Exception {
		DataSource ds = mock(DataSource.class);
		when(ds.isWrapperFor(Closeable.class)).thenReturn(true);

		AtomicBoolean closed = new AtomicBoolean();
		Closeable closeable = () -> closed.set(true);
		when(ds.unwrap(Closeable.class)).thenReturn(closeable);

		new ProxyDataSource(ds).unwrap(Closeable.class).close();
		assertThat(closed).isTrue();
	}

    @Test
    public void getDataSource() {
        DataSource original = mock(DataSource.class);
        ProxyDataSource proxyDataSource = new ProxyDataSource(original);
        assertThat(proxyDataSource.getDataSource()).isSameAs(original);
    }

    @Test
    public void isWrapperFor() throws Exception {
        assertThat(this.proxyDataSource.isWrapperFor(ProxyDataSource.class)).isTrue();
        assertThat(this.proxyDataSource.isWrapperFor(JDBCDataSource.class)).isTrue();
        assertThat(this.proxyDataSource.isWrapperFor(DataSource.class)).isTrue();

        assertThat(this.proxyDataSource.isWrapperFor(SmartDataSource.class)).isFalse();
    }

    @Test
    public void unwrap() throws Exception {
        assertThat(this.proxyDataSource.unwrap(ProxyDataSource.class)).isSameAs(this.proxyDataSource);
        assertThat(this.proxyDataSource.unwrap(JDBCDataSource.class)).isSameAs(this.originalDataSource);
        assertThat(this.proxyDataSource.unwrap(DataSource.class)).isSameAs(this.proxyDataSource);

        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                ProxyDataSourceTest.this.proxyDataSource.unwrap(SmartDataSource.class);
            }
        }).isInstanceOf(SQLException.class);
    }
}
