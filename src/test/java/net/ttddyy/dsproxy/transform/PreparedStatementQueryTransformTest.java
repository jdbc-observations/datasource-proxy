package net.ttddyy.dsproxy.transform;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.TestUtils;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.proxy.ProxyConfig;
import net.ttddyy.dsproxy.proxy.jdk.JdkJdbcProxyFactory;
import org.hsqldb.jdbc.JDBCDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author Tadaya Tsuyukubo
 */
public class PreparedStatementQueryTransformTest {
    private DataSource rawDatasource;
    private List<String> interceptedQueries = new ArrayList<String>();

    @Before
    public void setup() throws Exception {
        // real datasource
        JDBCDataSource rawDataSource = new JDBCDataSource();
        rawDataSource.setDatabase("jdbc:hsqldb:mem:aname");
        rawDataSource.setUser("sa");
        this.rawDatasource = rawDataSource;

        // populate datasource
        Statement statement = rawDataSource.getConnection().createStatement();
        statement.addBatch("create table foo ( id integer primary key, name varchar(10) );");
        statement.addBatch("create table bar ( id integer primary key, name varchar(10) );");
        statement.addBatch("insert into foo ( id, name )values (1, 'foo');");
        statement.addBatch("insert into bar ( id, name )values (100, 'bar');");
        statement.executeBatch();
    }


    @After
    public void teardown() throws Exception {
        interceptedQueries.clear();
        TestUtils.shutdown(rawDatasource);
    }

    private Connection getProxyConnectionForSelect() throws Exception {
        return getProxyConnection(true);
    }

    private Connection getProxyConnectionForUpdate() throws Exception {
        return getProxyConnection(false);
    }

    private Connection getProxyConnection(final boolean isSelect) throws Exception {

        QueryTransformer transformer = new QueryTransformer() {
            public String transformQuery(TransformInfo transformInfo) {
                interceptedQueries.add(transformInfo.getQuery());
                return isSelect ? "SELECT * FROM bar" : "UPDATE bar SET name = ?";
            }
        };

        QueryExecutionListener queryListener = mock(QueryExecutionListener.class);
        ProxyConfig proxyConfig = ProxyConfig.Builder.create()
                .queryListener(queryListener)
                .queryTransformer(transformer)
                .build();

        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setDataSourceName("myDS");

        return new JdkJdbcProxyFactory().createConnection(rawDatasource.getConnection(), connectionInfo, proxyConfig);
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
        boolean result = ps.execute();
        assertThat(result).isTrue();

        // verify intercepted query
        assertThat(interceptedQueries).hasSize(1).containsExactly("SELECT name FROM foo");
    }

    @Test
    public void testExecuteWithUpdate() throws Exception {
        PreparedStatement ps = getProxyConnectionForUpdate().prepareStatement("UPDATE foo SET name = ?");
        ps.setString(1, "FOO");
        boolean result = ps.execute();
        assertThat(result).isFalse();

        // verify intercepted query
        assertThat(interceptedQueries).hasSize(1).containsExactly("UPDATE foo SET name = ?");

        // verify bar is updated instead of foo
        ResultSet rs = rawDatasource.getConnection().createStatement().executeQuery("SELECT name FROM foo");
        assertThat(rs.next()).isTrue();
        assertThat(rs.getString("name")).isEqualTo("foo");
        rs = rawDatasource.getConnection().createStatement().executeQuery("SELECT name FROM bar");
        assertThat(rs.next()).isTrue();
        assertThat(rs.getString("name")).isEqualTo("FOO");
    }

    @Test
    public void testExecuteQuery() throws Exception {
        PreparedStatement ps = getProxyConnectionForSelect().prepareStatement("SELECT name FROM foo");
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
        ps.setString(1, "FOO");
        int count = ps.executeUpdate();
        assertThat(count).isEqualTo(1);

        // verify intercepted query
        assertThat(interceptedQueries).hasSize(1).containsExactly("UPDATE foo SET name = ?");

        // verify bar is updated instead of foo
        ResultSet rs = rawDatasource.getConnection().createStatement().executeQuery("SELECT name FROM foo");
        assertThat(rs.next()).isTrue();
        assertThat(rs.getString("name")).isEqualTo("foo");
        rs = rawDatasource.getConnection().createStatement().executeQuery("SELECT name FROM bar");
        assertThat(rs.next()).isTrue();
        assertThat(rs.getString("name")).isEqualTo("FOO");
    }

    @Test
    public void testExecuteBatch() throws Exception {
        PreparedStatement ps = getProxyConnectionForUpdate().prepareStatement("UPDATE foo SET name = ?");
        ps.setString(1, "FOO1");
        ps.addBatch();
        ps.setString(1, "FOO2");
        ps.addBatch();
        int[] result = ps.executeBatch();
        assertThat(result).containsExactly(1, 1);

        // verify intercepted query
        assertThat(interceptedQueries).hasSize(1).containsExactly("UPDATE foo SET name = ?");

        // verify bar is updated instead of foo
        ResultSet rs = rawDatasource.getConnection().createStatement().executeQuery("SELECT name FROM foo");
        assertThat(rs.next()).isTrue();
        assertThat(rs.getString("name")).isEqualTo("foo");
        rs = rawDatasource.getConnection().createStatement().executeQuery("SELECT name FROM bar");
        assertThat(rs.next()).isTrue();
        assertThat(rs.getString("name")).isEqualTo("FOO2");
    }

    @Test
    public void testClearBatch() throws Exception {
        PreparedStatement ps = getProxyConnectionForUpdate().prepareStatement("UPDATE foo SET name = ?");
        ps.clearBatch();

        // even though batch is canceled, interceptor was called once.
        assertThat(interceptedQueries).hasSize(1).containsExactly("UPDATE foo SET name = ?");
    }

}
