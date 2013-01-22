package net.ttddyy.dsproxy.transform;

import net.ttddyy.dsproxy.TestUtils;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.proxy.InterceptorHolder;
import net.ttddyy.dsproxy.proxy.JdkJdbcProxyFactory;
import org.hsqldb.jdbc.JDBCDataSource;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.sql.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

/**
 * @author Tadaya Tsuyukubo
 */
public class PreparedStatementParameterTransformTest {

    private DataSource rawDatasource;

    @BeforeMethod
    public void setup() throws Exception {
        // real datasource
        JDBCDataSource rawDataSource = new JDBCDataSource();
        rawDataSource.setDatabase("jdbc:hsqldb:mem:aname");
        rawDataSource.setUser("sa");
        this.rawDatasource = rawDataSource;

        // populate datasource
        Statement statement = rawDataSource.getConnection().createStatement();
        statement.addBatch("create table foo ( id integer primary key, name varchar(100) );");
        statement.addBatch("insert into foo ( id, name )values (1, 'foo');");
        statement.addBatch("insert into foo ( id, name )values (2, 'bar');");
        statement.addBatch("insert into foo ( id, name )values (3, 'baz');");
        statement.executeBatch();

    }

    @AfterMethod
    public void teardown() throws Exception {
        TestUtils.shutdown(rawDatasource);
    }

    private Connection getProxyConnection(ParameterTransformer paramTransformer) throws Exception {
        QueryExecutionListener queryListener = mock(QueryExecutionListener.class);
        QueryTransformer queryTransformer = mock(QueryTransformer.class);
        when(queryTransformer.transformQuery(anyString(), anyString())).thenAnswer(new Answer<String>() {
            public String answer(InvocationOnMock invocation) throws Throwable {
                return (String) invocation.getArguments()[1];  // return input as is
            }
        });
        InterceptorHolder interceptorHolder = new InterceptorHolder(queryListener, queryTransformer, paramTransformer);

        return new JdkJdbcProxyFactory().createConnection(rawDatasource.getConnection(), interceptorHolder);
    }

    @Test
    public void testClearAndReplaceParam() throws Exception {

        ParameterTransformer paramTransformer = mock(ParameterTransformer.class);
        doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ParameterReplacer replacer = (ParameterReplacer) invocation.getArguments()[2];
                replacer.clearParameters();
                replacer.setInt(1, 2);  // replace first id=1 to id=2
                replacer.setInt(2, 2);  // replace second id=1 to id=2
                return null;
            }
        }).when(paramTransformer).transformParameters(anyString(), anyString(), isA(ParameterReplacer.class));

        Connection connection = getProxyConnection(paramTransformer);

        // to have two parameters, checking id column twice
        PreparedStatement ps = connection.prepareStatement("SELECT id, name FROM foo WHERE id = ? AND id = ?");
        ps.setInt(1, 1);
        ps.setInt(2, 1);
        ResultSet rs = ps.executeQuery();

        assertThat(rs.next(), is(true));
        assertThat(rs.getInt(1), is(2));
        assertThat(rs.getString(2), is("bar"));

        verify(paramTransformer, only()).transformParameters(anyString(), anyString(), isA(ParameterReplacer.class));

    }

    @Test
    public void testReplaceParam() throws Exception {

        ParameterTransformer paramTransformer = mock(ParameterTransformer.class);
        doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ParameterReplacer replacer = (ParameterReplacer) invocation.getArguments()[2];
                replacer.setInt(2, 2);  // just replace the second id.  after this: "where id=1 and id=2"
                return null;
            }
        }).when(paramTransformer).transformParameters(anyString(), anyString(), isA(ParameterReplacer.class));

        Connection connection = getProxyConnection(paramTransformer);

        // to have two parameters, checking id column twice
        PreparedStatement ps = connection.prepareStatement("SELECT id, name FROM foo WHERE id = ? AND id = ?");
        ps.setInt(1, 1);
        ps.setInt(2, 1);
        ResultSet rs = ps.executeQuery();

        assertThat("should have no matching record 'where id=1 and id=2'", rs.next(), is(false));
        verify(paramTransformer, only()).transformParameters(anyString(), anyString(), isA(ParameterReplacer.class));

    }

    @Test
    public void testBatchReplaceParam() throws Exception {

        ParameterTransformer paramTransformer = mock(ParameterTransformer.class);
        doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ParameterReplacer replacer = (ParameterReplacer) invocation.getArguments()[2];
                String name = replacer.getValue(1);
                replacer.setString(1, name + "-INTERCEPTED");
                return null;
            }
        }).when(paramTransformer).transformParameters(anyString(), anyString(), isA(ParameterReplacer.class));

        Connection connection = getProxyConnection(paramTransformer);

        PreparedStatement ps = connection.prepareStatement("UPDATE foo SET name = ? WHERE id = ?");
        ps.setString(1, "FOO");
        ps.setInt(2, 1);
        ps.addBatch();
        ps.setString(1, "BAR");
        ps.setInt(2, 2);
        ps.addBatch();
        int[] result = ps.executeBatch();

        assertThat(result.length, is(2));
        assertThat(result[0], is(1));
        assertThat(result[1], is(1));

        String name = queryForString("SELECT name FROM foo WHERE id = 1");
        assertThat(name, is("FOO-INTERCEPTED"));

        name = queryForString("SELECT name FROM foo WHERE id = 2");
        assertThat(name, is("BAR-INTERCEPTED"));

        verify(paramTransformer, times(2)).transformParameters(anyString(), anyString(), isA(ParameterReplacer.class));

    }

    private String queryForString(String sql) throws SQLException {
        Statement stmt = rawDatasource.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        assertThat("ResultSet should have result ", rs.next(), is(true));
        String result = rs.getString(1);
        rs.close();
        stmt.close();
        return result;
    }

    @Test
    public void testBatchClearAndReplaceParam() throws Exception {

        ParameterTransformer paramTransformer = mock(ParameterTransformer.class);
        doAnswer(new Answer() {
            // for first batch
            public Object answer(InvocationOnMock invocation) throws Throwable {
                // first batch. call clearParameters().
                ParameterReplacer replacer = (ParameterReplacer) invocation.getArguments()[2];
                replacer.clearParameters();
                replacer.setString(1, "BAZ");
                replacer.setInt(2, 3);  // id=3
                return null;
            }
        }).doAnswer(new Answer() {
            // for second batch
            public Object answer(InvocationOnMock invocation) throws Throwable {
                // second batch. don't call clearParameters().
                ParameterReplacer replacer = (ParameterReplacer) invocation.getArguments()[2];
                String name = replacer.getValue(1);
                replacer.setString(1, name + "-INTERCEPTED");
                return null;
            }
        }

        ).when(paramTransformer).transformParameters(anyString(), anyString(), isA(ParameterReplacer.class));

        Connection connection = getProxyConnection(paramTransformer);

        PreparedStatement ps = connection.prepareStatement("UPDATE foo SET name = ? WHERE id = ?");
        ps.setString(1, "FOO");
        ps.setInt(2, 1);
        ps.addBatch();
        ps.setString(1, "BAR");
        ps.setInt(2, 2);
        ps.addBatch();
        int[] result = ps.executeBatch();

        assertThat(result.length, is(2));
        assertThat(result[0], is(1));
        assertThat(result[1], is(1));

        String name = queryForString("SELECT name FROM foo WHERE id = 1");
        assertThat(name, is("foo"));

        name = queryForString("SELECT name FROM foo WHERE id = 2");
        assertThat(name, is("BAR-INTERCEPTED"));

        name = queryForString("SELECT name FROM foo WHERE id = 3");
        assertThat(name, is("BAZ"));

        verify(paramTransformer, times(2)).transformParameters(anyString(), anyString(), isA(ParameterReplacer.class));

    }

}
