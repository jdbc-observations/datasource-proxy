package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.proxy.jdk.JdkJdbcProxyFactory;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author Tadaya Tsuyukubo
 */
public class JdkJdbcProxyFactoryTest {

    private JdkJdbcProxyFactory factory = new JdkJdbcProxyFactory();

    @Test
    public void testCreateConnection() {
        Connection conn = mock(Connection.class);
        ProxyConfig proxyConfig = ProxyConfig.Builder.create().build();

        Connection result = factory.createConnection(conn, getConnectionInfo(), proxyConfig);

        assertThat(result).isNotNull().isNotSameAs(conn).isInstanceOf(ProxyJdbcObject.class);
    }

    @Test
    public void testCreateStatement() {
        Statement stmt = mock(Statement.class);
        ProxyConfig proxyConfig = ProxyConfig.Builder.create().build();

        Statement result = factory.createStatement(stmt, getConnectionInfo(), null, proxyConfig);

        assertThat(result).isNotNull().isNotSameAs(stmt).isInstanceOf(ProxyJdbcObject.class);
    }

    @Test
    public void testCreatePreparedStatement() {
        PreparedStatement ps = mock(PreparedStatement.class);

        ProxyConfig proxyConfig = ProxyConfig.Builder.create().build();

        PreparedStatement result = factory.createPreparedStatement(ps, "my-query", getConnectionInfo(), null, proxyConfig, false);

        assertThat(result).isNotNull().isNotSameAs(ps).isInstanceOf(ProxyJdbcObject.class);
    }

    @Test
    public void testCreateCallableStatement() {
        CallableStatement cs = mock(CallableStatement.class);

        ProxyConfig proxyConfig = ProxyConfig.Builder.create().build();

        CallableStatement result = factory.createCallableStatement(cs, "my-query", getConnectionInfo(), null, proxyConfig);

        assertThat(result).isNotNull().isNotSameAs(cs).isInstanceOf(ProxyJdbcObject.class);
    }

    @Test
    public void testCreateDataSource() {
        DataSource ds = mock(DataSource.class);
        ProxyConfig proxyConfig = ProxyConfig.Builder.create().dataSourceName("my-ds").build();

        DataSource result = factory.createDataSource(ds, proxyConfig);

        assertThat(result).isNotNull().isNotSameAs(ds).isInstanceOf(ProxyJdbcObject.class);
    }


    private ConnectionInfo getConnectionInfo() {
        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setDataSourceName("my-ds");
        return connectionInfo;
    }

}
