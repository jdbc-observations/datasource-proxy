package net.ttddyy.dsproxy.transform;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.DatabaseType;
import net.ttddyy.dsproxy.DbResourceCleaner;
import net.ttddyy.dsproxy.DatabaseTest;
import net.ttddyy.dsproxy.EnabledOnDatabase;
import net.ttddyy.dsproxy.DbTestUtils;
import net.ttddyy.dsproxy.listener.ProxyDataSourceListener;
import net.ttddyy.dsproxy.proxy.ProxyConfig;
import net.ttddyy.dsproxy.proxy.jdk.JdkJdbcProxyFactory;
import org.hsqldb.jdbc.JDBCDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.CallableStatement;
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
@EnabledOnDatabase(DatabaseType.HSQL)
@DatabaseTest
public class CallableStatementQueryTransformerDbTest {

    // hsqldb stored procedure. insert to table foo.
    public static void insertFoo(Connection conn, int id, String name) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("INSERT INTO foo (id, name) VALUES (" + id + ", '" + name + "')");
        stmt.close();
    }

    // hsqldb stored procedure. insert to table bar.
    public static void insertBar(Connection conn, int id, String name) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("INSERT INTO bar (id, name) VALUES (" + id + ", '" + name + "')");
        stmt.close();
    }

    // hsqldb stored procedure
    public static String procFoo(String s) {
        return "foo=" + s;
    }

    // hsqldb stored procedure
    public static String procBar(String s) {
        return "bar=" + s;
    }

    private DataSource rawDataSource;
    private List<String> interceptedQueries = new ArrayList<String>();

    private DbResourceCleaner cleaner;

    public CallableStatementQueryTransformerDbTest(DbResourceCleaner cleaner) {
        this.cleaner = cleaner;
    }

    @BeforeEach
    public void setup() throws Exception {
        // real datasource
        JDBCDataSource rawDataSource = new JDBCDataSource();
        rawDataSource.setDatabase("jdbc:hsqldb:mem:aname");
        rawDataSource.setUser("sa");
        this.rawDataSource = DbTestUtils.createDataSource();

        // create stored procedure
        String dropFuncFoo = "DROP FUNCTION IF EXISTS prof_foo";
        String dropFuncBar = "DROP FUNCTION IF EXISTS prof_bar";
        String procFoo = "CREATE FUNCTION proc_foo(s VARCHAR(50)) RETURNS VARCHAR(50) LANGUAGE JAVA DETERMINISTIC NO SQL EXTERNAL NAME 'CLASSPATH:net.ttddyy.dsproxy.transform.CallableStatementQueryTransformerDbTest.procFoo'";
        String procBar = "CREATE FUNCTION proc_bar(s VARCHAR(50)) RETURNS VARCHAR(50) LANGUAGE JAVA DETERMINISTIC NO SQL EXTERNAL NAME 'CLASSPATH:net.ttddyy.dsproxy.transform.CallableStatementQueryTransformerDbTest.procBar'";

        String dropTableFoo = "DROP TABLE IF EXISTS foo;";
        String dropTableBar = "DROP TABLE IF EXISTS bar;";
        String tableFoo = "CREATE TABLE foo ( id INTEGER PRIMARY KEY, name VARCHAR(10) );";
        String tableBar = "CREATE TABLE bar ( id INTEGER PRIMARY KEY, name VARCHAR(10) );";
        String insertFoo = "CREATE PROCEDURE insert_foo(IN id INT, IN name VARCHAR(50)) LANGUAGE JAVA NOT DETERMINISTIC MODIFIES SQL DATA EXTERNAL NAME 'CLASSPATH:net.ttddyy.dsproxy.transform.CallableStatementQueryTransformerDbTest.insertFoo'";
        String insertBar = "CREATE PROCEDURE insert_bar(IN id INT, IN name VARCHAR(50)) LANGUAGE JAVA NOT DETERMINISTIC MODIFIES SQL DATA EXTERNAL NAME 'CLASSPATH:net.ttddyy.dsproxy.transform.CallableStatementQueryTransformerDbTest.insertBar'";

        DbTestUtils.executeBatchStatements(this.rawDataSource,
                dropFuncFoo,
                dropFuncBar,
                dropTableFoo,
                dropTableBar,
                procFoo,
                procBar,
                tableFoo,
                tableBar,
                insertFoo,
                insertBar
        );
    }


    @AfterEach
    public void teardown() throws Exception {
        interceptedQueries.clear();
        DbTestUtils.shutdown(rawDataSource);
    }

    private Connection getProxyConnection(final String replacedQuery) throws Exception {

        QueryTransformer transformer = transformInfo -> {
            interceptedQueries.add(transformInfo.getQuery());
            return replacedQuery;
        };

        ProxyDataSourceListener queryListener = mock(ProxyDataSourceListener.class);
        ProxyConfig proxyConfig = ProxyConfig.Builder.create()
                .listener(queryListener)
                .queryTransformer(transformer)
                .build();

        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setDataSourceName("myDS");

        Connection connection = rawDataSource.getConnection();
        this.cleaner.add(connection);

        return new JdkJdbcProxyFactory().createConnection(connection, connectionInfo, proxyConfig);
    }

    @Test
    public void testExecute() throws Exception {
        String queryToReplace = "CALL proc_bar(?)";
        CallableStatement cs = getProxyConnection(queryToReplace).prepareCall("CALL proc_foo(?)");
        this.cleaner.add(cs);

        cs.setString(1, "FOO");
        boolean result = cs.execute();
        assertThat(result).isTrue();

        ResultSet rs = cs.getResultSet();
        assertThat(rs.next()).isTrue();
        String output = rs.getString(1);
        assertThat(output).isEqualTo("bar=FOO");

        // verify intercepted query
        assertThat(interceptedQueries).hasSize(1).contains("CALL proc_foo(?)");
    }

    @Test
    public void testBatch() throws Exception {
        String queryToReplace = "CALL insert_bar(?, ?)";
        CallableStatement cs = getProxyConnection(queryToReplace).prepareCall("CALL insert_foo(?, ?)");
        this.cleaner.add(cs);

        cs.setInt(1, 100);
        cs.setString(2, "FOO1");
        cs.addBatch();
        cs.setInt(1, 200);
        cs.setString(2, "FOO2");
        cs.addBatch();
        int[] result = cs.executeBatch();

        assertThat(result.length).isEqualTo(2);

        // verify intercepted query.
        assertThat(interceptedQueries).hasSize(1).contains("CALL insert_foo(?, ?)");


        ResultSet rs = executeQuery("SELECT COUNT(*) FROM foo");
        assertThat(rs.next()).isTrue();
        assertThat(rs.getInt(1)).as("table foo has no records").isEqualTo(0);

        rs = executeQuery("SELECT COUNT(*) FROM bar");
        assertThat(rs.next()).isTrue();
        assertThat(rs.getInt(1)).as("table foo has 2 records").isEqualTo(2);

        rs = executeQuery("SELECT id, name FROM bar ORDER BY id ASC");
        assertThat(rs.next()).isTrue();
        assertThat(rs.getInt("id")).isEqualTo(100);
        assertThat(rs.getString("name")).isEqualTo("FOO1");

        assertThat(rs.next()).isTrue();
        assertThat(rs.getInt("id")).isEqualTo(200);
        assertThat(rs.getString("name")).isEqualTo("FOO2");
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
