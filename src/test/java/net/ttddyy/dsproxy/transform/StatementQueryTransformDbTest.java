package net.ttddyy.dsproxy.transform;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.DbResourceCleaner;
import net.ttddyy.dsproxy.DatabaseTest;
import net.ttddyy.dsproxy.DbTestUtils;
import net.ttddyy.dsproxy.listener.ProxyDataSourceListener;
import net.ttddyy.dsproxy.proxy.ProxyConfig;
import net.ttddyy.dsproxy.proxy.jdk.JdkJdbcProxyFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author Tadaya Tsuyukubo
 */
@DatabaseTest
public class StatementQueryTransformDbTest {

    private DataSource rawDataSource;
    private List<String> interceptedQueries = new ArrayList<String>();

    private DbResourceCleaner cleaner;

    public StatementQueryTransformDbTest(DbResourceCleaner cleaner) {
        this.cleaner = cleaner;
    }

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
            return isSelect ? "SELECT * FROM bar" : "UPDATE bar SET name = 'BAR'";
        };

        ProxyDataSourceListener queryListener = mock(ProxyDataSourceListener.class);
        ProxyConfig proxyConfig = ProxyConfig.Builder.create()
                .listener(queryListener)
                .queryTransformer(transformer)
                .build();

        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setDataSourceName("myDS");

        Connection connection = this.rawDataSource.getConnection();
        this.cleaner.add(connection);

        return new JdkJdbcProxyFactory().createConnection(connection, connectionInfo, proxyConfig);
    }


    @Test
    public void testExecuteWithSelect() throws Exception {
        Statement stat = getProxyConnectionForSelect().createStatement();
        this.cleaner.add(stat);

        boolean result = stat.execute("SELECT name FROM foo");
        assertThat(result).isTrue();

        // verify intercepted query
        assertThat(interceptedQueries).hasSize(1).contains("SELECT name FROM foo");
    }

    @Test
    public void testExecuteWithUpdate() throws Exception {
        // stmt.execute() with update statement. (expect false)
        Statement stat = getProxyConnectionForUpdate().createStatement();
        this.cleaner.add(stat);

        boolean result = stat.execute("UPDATE foo SET name = 'FOO'");
        assertThat(result).isFalse();

        // verify intercepted query
        assertThat(interceptedQueries).hasSize(1).contains("UPDATE foo SET name = 'FOO'");

        // verify bar is updated instead of foo
        ResultSet rs = executeQuery("SELECT name FROM foo");
        assertThat(rs.next()).isTrue();
        assertThat(rs.getString("name")).isEqualTo("foo");
        rs = executeQuery("SELECT name FROM bar");
        assertThat(rs.next()).isTrue();
        assertThat(rs.getString("name")).isEqualTo("BAR");
    }

    @Test
    public void testExecuteQuery() throws Exception {
        Statement stat = getProxyConnectionForSelect().createStatement();
        this.cleaner.add(stat);

        ResultSet resultSet = stat.executeQuery("SELECT name FROM foo");
        assertThat(resultSet.next()).isTrue();
        assertThat(resultSet.getInt("id")).isEqualTo(100);
        assertThat(resultSet.getString("name")).isEqualTo("bar");

        assertThat(interceptedQueries).hasSize(1).contains("SELECT name FROM foo");
    }

    @Test
    public void testExecuteUpdate() throws Exception {
        Statement stat = getProxyConnectionForUpdate().createStatement();
        this.cleaner.add(stat);

        int count = stat.executeUpdate("UPDATE foo SET name = 'FOO'");
        assertThat(count).isEqualTo(1);

        assertThat(interceptedQueries).hasSize(1).contains("UPDATE foo SET name = 'FOO'");

        // verify bar is updated instead of foo
        ResultSet rs = executeQuery("SELECT name FROM foo");
        assertThat(rs.next()).isTrue();
        assertThat(rs.getString("name")).isEqualTo("foo");
        rs = executeQuery("SELECT name FROM bar");
        assertThat(rs.next()).isTrue();
        assertThat(rs.getString("name")).isEqualTo("BAR");
    }

    @Test
    public void testExecuteBatch() throws Exception {
        Statement stat = getProxyConnectionForUpdate().createStatement();
        this.cleaner.add(stat);

        stat.addBatch("UPDATE foo SET name = 'FOO1'");
        stat.addBatch("UPDATE foo SET name = 'FOO2'");
        int[] result = stat.executeBatch();
        assertThat(result).containsSequence(1, 1);

        assertThat(interceptedQueries).hasSize(2).containsExactly("UPDATE foo SET name = 'FOO1'", "UPDATE foo SET name = 'FOO2'");

        // verify bar is updated instead of foo
        ResultSet rs = executeQuery("SELECT name FROM foo");
        assertThat(rs.next()).isTrue();
        assertThat(rs.getString("name")).isEqualTo("foo");
        rs = executeQuery("SELECT name FROM bar");
        assertThat(rs.next()).isTrue();
        assertThat(rs.getString("name")).isEqualTo("BAR");
    }

    @Test
    public void testClearBatch() throws Exception {
        Statement stat = getProxyConnectionForUpdate().createStatement();
        this.cleaner.add(stat);

        stat.addBatch("UPDATE foo SET name = 'FOO1'");
        stat.addBatch("UPDATE foo SET name = 'FOO2'");
        stat.clearBatch();
        int[] result = stat.executeBatch();
        assertThat(result).isEmpty();

        // even though batch is canceled, interceptor should be called
        assertThat(interceptedQueries).hasSize(2).containsExactly("UPDATE foo SET name = 'FOO1'", "UPDATE foo SET name = 'FOO2'");

        // verify data should not be changed
        ResultSet rs = executeQuery("SELECT name FROM foo");
        assertThat(rs.next()).isTrue();
        assertThat(rs.getString("name")).isEqualTo("foo");
        rs = executeQuery("SELECT name FROM bar");
        assertThat(rs.next()).isTrue();
        assertThat(rs.getString("name")).isEqualTo("bar");
    }

    private ResultSet executeQuery(String query) throws SQLException {
        // verify bar is updated instead of foo
        Connection connection = this.rawDataSource.getConnection();
        Statement statement = connection.createStatement();
        this.cleaner.add(connection);
        this.cleaner.add(statement);

        return statement.executeQuery(query);
    }
}
