package net.ttddyy.dsproxy.transform;

import net.ttddyy.dsproxy.TestUtils;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.proxy.InterceptorHolder;
import net.ttddyy.dsproxy.proxy.jdk.JdkJdbcProxyFactory;
import org.hsqldb.jdbc.JDBCDataSource;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;

/**
 * @author Tadaya Tsuyukubo
 */
public class CallableStatementQueryTransformerTest {

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

    private DataSource rawDatasource;
    private List<String> interceptedQueries = new ArrayList<String>();

    private QueryTransformer transformer = new QueryTransformer() {
        public String transformQuery(TransformInfo transformInfo) {
            interceptedQueries.add(transformInfo.getQuery());
            return "call proc_bar('BAR')";
        }
    };

    @BeforeMethod
    public void setup() throws Exception {
        // real datasource
        JDBCDataSource rawDataSource = new JDBCDataSource();
        rawDataSource.setDatabase("jdbc:hsqldb:mem:aname");
        rawDataSource.setUser("sa");
        this.rawDatasource = rawDataSource;

        // create stored procedure
        String procFoo = "CREATE FUNCTION proc_foo(s VARCHAR(50)) RETURNS VARCHAR(50) LANGUAGE JAVA DETERMINISTIC NO SQL EXTERNAL NAME 'CLASSPATH:net.ttddyy.dsproxy.transform.CallableStatementQueryTransformerTest.procFoo'";
        String procBar = "CREATE FUNCTION proc_bar(s VARCHAR(50)) RETURNS VARCHAR(50) LANGUAGE JAVA DETERMINISTIC NO SQL EXTERNAL NAME 'CLASSPATH:net.ttddyy.dsproxy.transform.CallableStatementQueryTransformerTest.procBar'";

        String tableFoo = "CREATE TABLE foo ( id INTEGER PRIMARY KEY, name VARCHAR(10) );";
        String tableBar = "CREATE TABLE bar ( id INTEGER PRIMARY KEY, name VARCHAR(10) );";
        String insertFoo = "CREATE PROCEDURE insert_foo(IN id INT, IN name VARCHAR(50)) LANGUAGE JAVA NOT DETERMINISTIC MODIFIES SQL DATA EXTERNAL NAME 'CLASSPATH:net.ttddyy.dsproxy.transform.CallableStatementQueryTransformerTest.insertFoo'";
        String insertBar = "CREATE PROCEDURE insert_bar(IN id INT, IN name VARCHAR(50)) LANGUAGE JAVA NOT DETERMINISTIC MODIFIES SQL DATA EXTERNAL NAME 'CLASSPATH:net.ttddyy.dsproxy.transform.CallableStatementQueryTransformerTest.insertBar'";

        Statement statement = rawDataSource.getConnection().createStatement();
        statement.addBatch(procFoo);
        statement.addBatch(procBar);
        statement.addBatch(tableFoo);
        statement.addBatch(tableBar);
        statement.addBatch(insertFoo);
        statement.addBatch(insertBar);
        statement.executeBatch();
    }


    @AfterMethod
    public void teardown() throws Exception {
        interceptedQueries.clear();
        TestUtils.shutdown(rawDatasource);
    }

    private Connection getProxyConnection(final String replacedQuery) throws Exception {

        QueryTransformer transformer = new QueryTransformer() {
            public String transformQuery(TransformInfo transformInfo) {
                interceptedQueries.add(transformInfo.getQuery());
                return replacedQuery;
            }
        };

        QueryExecutionListener queryListener = mock(QueryExecutionListener.class);
        InterceptorHolder interceptorHolder = new InterceptorHolder(queryListener, transformer);

        return new JdkJdbcProxyFactory().createConnection(rawDatasource.getConnection(), interceptorHolder);
    }

    @Test
    public void testExecute() throws Exception {
        String queryToReplace = "CALL proc_bar(?)";
        CallableStatement cs = getProxyConnection(queryToReplace).prepareCall("CALL proc_foo(?)");
        cs.setString(1, "FOO");
        boolean result = cs.execute();
        assertThat(result, is(true));

        ResultSet rs = cs.getResultSet();
        assertThat(rs.next(), is(true));
        String output = rs.getString(1);
        assertThat(output, is("bar=FOO"));

        // verify intercepted query
        assertThat(interceptedQueries, hasSize(1));
        assertThat(interceptedQueries, hasItem("CALL proc_foo(?)"));
    }

    @Test
    public void testBatch() throws Exception {
        String queryToReplace = "CALL insert_bar(?, ?)";
        CallableStatement cs = getProxyConnection(queryToReplace).prepareCall("CALL insert_foo(?, ?)");
        cs.setInt(1, 100);
        cs.setString(2, "FOO1");
        cs.addBatch();
        cs.setInt(1, 200);
        cs.setString(2, "FOO2");
        cs.addBatch();
        int[] result = cs.executeBatch();

        assertThat(result.length, is(2));

        // verify intercepted query.
        assertThat(interceptedQueries, hasSize(1));
        assertThat(interceptedQueries, hasItem("CALL insert_foo(?, ?)"));


        Statement statement = rawDatasource.getConnection().createStatement();
        ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM foo");
        assertThat(rs.next(), is(true));
        assertThat("table foo has no records", rs.getInt(1), is(0));

        rs = statement.executeQuery("SELECT COUNT(*) FROM bar");
        assertThat(rs.next(), is(true));
        assertThat("table foo has 2 records", rs.getInt(1), is(2));

        rs = statement.executeQuery("SELECT id, name FROM bar ORDER BY id ASC");
        assertThat(rs.next(), is(true));
        assertThat(rs.getInt("id"), is(100));
        assertThat(rs.getString("name"), is("FOO1"));

        assertThat(rs.next(), is(true));
        assertThat(rs.getInt("id"), is(200));
        assertThat(rs.getString("name"), is("FOO2"));
    }

}
