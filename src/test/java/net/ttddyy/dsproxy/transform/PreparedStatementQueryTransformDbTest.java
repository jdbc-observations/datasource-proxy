package net.ttddyy.dsproxy.transform;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.DbTestUtils;
import net.ttddyy.dsproxy.listener.ProxyDataSourceListener;
import net.ttddyy.dsproxy.proxy.ProxyConfig;
import net.ttddyy.dsproxy.proxy.jdk.JdkJdbcProxyFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author Tadaya Tsuyukubo
 */
public class PreparedStatementQueryTransformDbTest {
    private DataSource rawDataSource;
    private List<String> interceptedQueries = new ArrayList<String>();

    private Set<Connection> connectionToClose = new HashSet<>();
    private Set<Statement> statementToClose = new HashSet<>();


    @BeforeEach
    public void setup() throws Exception {
        // real datasource
        this.rawDataSource = DbTestUtils.createDataSource();

        // populate datasource
        DbTestUtils.executeBatchStatements(this.rawDataSource,
                "drop table if exists foo;",
                "drop table if exists bar;",
                "create table foo ( id integer primary key, name varchar(10) );",
                "create table bar ( id integer primary key, name varchar(10) );",
                "insert into foo ( id, name ) values (1, 'foo');",
                "insert into bar ( id, name ) values (100, 'bar');"
        );
    }


    @AfterEach
    public void teardown() throws Exception {
        interceptedQueries.clear();

        for (Statement statement : this.statementToClose) {
            statement.close();
        }
        for (Connection connection : this.connectionToClose) {
            connection.close();
        }
        DbTestUtils.shutdown(rawDataSource);
    }

    private Connection getProxyConnectionForSelect() throws Exception {
        return getProxyConnection(true);
    }

    private Connection getProxyConnectionForUpdate() throws Exception {
        return getProxyConnection(false);
    }

    private Connection getProxyConnection(final boolean isSelect) throws Exception {

        QueryTransformer transformer = transformInfo -> {
            interceptedQueries.add(transformInfo.getQuery());
            return isSelect ? "SELECT * FROM bar" : "UPDATE bar SET name = ?";
        };

        ProxyDataSourceListener queryListener = mock(ProxyDataSourceListener.class);
        ProxyConfig proxyConfig = ProxyConfig.Builder.create()
                .listener(queryListener)
                .queryTransformer(transformer)
                .build();

        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setDataSourceName("myDS");

        Connection connection = rawDataSource.getConnection();
        this.connectionToClose.add(connection);

        return new JdkJdbcProxyFactory().createConnection(connection, connectionInfo, proxyConfig);
    }

    @Test
    public void testCreatePreparedStatement() throws Exception {
        getProxyConnectionForUpdate().prepareStatement("UPDATE foo SET name = ?");

        // when statement method is called, intercept should be called
        assertThat(interceptedQueries).hasSize(1).containsExactly("UPDATE foo SET name = ?");
    }

    @Test
    public void testExecuteWithSelect() throws Exception {
        PreparedStatement ps = getProxyConnectionForSelect().prepareStatement("SELECT name FROM foo");
        this.statementToClose.add(ps);

        boolean result = ps.execute();
        assertThat(result).isTrue();

        // verify intercepted query
        assertThat(interceptedQueries).hasSize(1).containsExactly("SELECT name FROM foo");
    }

    @Test
    public void testExecuteWithUpdate() throws Exception {
        PreparedStatement ps = getProxyConnectionForUpdate().prepareStatement("UPDATE foo SET name = ?");
        this.statementToClose.add(ps);

        ps.setString(1, "FOO");
        boolean result = ps.execute();
        assertThat(result).isFalse();

        // verify intercepted query
        assertThat(interceptedQueries).hasSize(1).containsExactly("UPDATE foo SET name = ?");

        // verify bar is updated instead of foo
        ResultSet rs = executeQuery("SELECT name FROM foo");
        assertThat(rs.next()).isTrue();
        assertThat(rs.getString("name")).isEqualTo("foo");
        rs = executeQuery("SELECT name FROM bar");
        assertThat(rs.next()).isTrue();
        assertThat(rs.getString("name")).isEqualTo("FOO");
    }

    @Test
    public void testExecuteQuery() throws Exception {
        PreparedStatement ps = getProxyConnectionForSelect().prepareStatement("SELECT name FROM foo");
        this.statementToClose.add(ps);

        ResultSet resultSet = ps.executeQuery();
        assertThat(resultSet.next()).isTrue();
        assertThat(resultSet.getInt("id")).isEqualTo(100);
        assertThat(resultSet.getString("name")).isEqualTo("bar");

        // verify intercepted query
        assertThat(interceptedQueries).hasSize(1).containsExactly("SELECT name FROM foo");
    }

    @Test
    public void testExecuteUpdate() throws Exception {
        PreparedStatement ps = getProxyConnectionForUpdate().prepareStatement("UPDATE foo SET name = ?");
        this.statementToClose.add(ps);

        ps.setString(1, "FOO");
        int count = ps.executeUpdate();
        assertThat(count).isEqualTo(1);

        // verify intercepted query
        assertThat(interceptedQueries).hasSize(1).containsExactly("UPDATE foo SET name = ?");

        // verify bar is updated instead of foo
        ResultSet rs = executeQuery("SELECT name FROM foo");
        assertThat(rs.next()).isTrue();
        assertThat(rs.getString("name")).isEqualTo("foo");
        rs = executeQuery("SELECT name FROM bar");
        assertThat(rs.next()).isTrue();
        assertThat(rs.getString("name")).isEqualTo("FOO");
    }

    @Test
    public void testExecuteBatch() throws Exception {
        PreparedStatement ps = getProxyConnectionForUpdate().prepareStatement("UPDATE foo SET name = ?");
        this.statementToClose.add(ps);

        ps.setString(1, "FOO1");
        ps.addBatch();
        ps.setString(1, "FOO2");
        ps.addBatch();
        int[] result = ps.executeBatch();
        assertThat(result).containsExactly(1, 1);

        // verify intercepted query
        assertThat(interceptedQueries).hasSize(1).containsExactly("UPDATE foo SET name = ?");

        // verify bar is updated instead of foo
        ResultSet rs = executeQuery("SELECT name FROM foo");
        assertThat(rs.next()).isTrue();
        assertThat(rs.getString("name")).isEqualTo("foo");
        rs = executeQuery("SELECT name FROM bar");
        assertThat(rs.next()).isTrue();
        assertThat(rs.getString("name")).isEqualTo("FOO2");
    }

    @Test
    public void testClearBatch() throws Exception {
        PreparedStatement ps = getProxyConnectionForUpdate().prepareStatement("UPDATE foo SET name = ?");
        this.statementToClose.add(ps);

        ps.clearBatch();

        // even though batch is canceled, interceptor was called once.
        assertThat(interceptedQueries).hasSize(1).containsExactly("UPDATE foo SET name = ?");
    }

    private ResultSet executeQuery(String query) throws SQLException {
        // verify bar is updated instead of foo
        Connection connection = this.rawDataSource.getConnection();
        Statement statement = connection.createStatement();
        this.connectionToClose.add(connection);
        this.statementToClose.add(statement);

        return statement.executeQuery(query);
    }

}
