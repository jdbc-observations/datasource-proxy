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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Tadaya Tsuyukubo
 */
public class PreparedStatementParameterTransformTest {

    private DataSource rawDatasource;

    @Before
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

    @After
    public void teardown() throws Exception {
        TestUtils.shutdown(rawDatasource);
    }

    private Connection getProxyConnection(ParameterTransformer paramTransformer) throws Exception {
        QueryExecutionListener queryListener = mock(QueryExecutionListener.class);
        QueryTransformer queryTransformer = mock(QueryTransformer.class);
        when(queryTransformer.transformQuery(isA(TransformInfo.class))).thenAnswer(new Answer<String>() {
            public String answer(InvocationOnMock invocation) throws Throwable {
                return ((TransformInfo) invocation.getArguments()[0]).getQuery();  // return input query as is
            }
        });
        ProxyConfig proxyConfig = ProxyConfig.Builder.create()
                .queryListener(queryListener)
                .queryTransformer(queryTransformer)
                .parameterTransformer(paramTransformer)
                .build();

        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setDataSourceName("myDS");

        return new JdkJdbcProxyFactory().createConnection(rawDatasource.getConnection(), connectionInfo, proxyConfig);
    }

    @Test
    public void testClearAndReplaceParam() throws Exception {

        ParameterTransformer paramTransformer = mock(ParameterTransformer.class);
        doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ParameterReplacer replacer = (ParameterReplacer) invocation.getArguments()[0];
                replacer.clearParameters();
                replacer.setInt(1, 2);  // replace first id=1 to id=2
                replacer.setInt(2, 2);  // replace second id=1 to id=2
                return null;
            }
        }).when(paramTransformer).transformParameters(isA(ParameterReplacer.class), isA(TransformInfo.class));

        Connection connection = getProxyConnection(paramTransformer);

        // to have two parameters, checking id column twice
        PreparedStatement ps = connection.prepareStatement("SELECT id, name FROM foo WHERE id = ? AND id = ?");
        ps.setInt(1, 1);
        ps.setInt(2, 1);
        ResultSet rs = ps.executeQuery();

        assertThat(rs.next()).isTrue();
        assertThat(rs.getInt(1)).isEqualTo(2);
        assertThat(rs.getString(2)).isEqualTo("bar");

        verify(paramTransformer, only()).transformParameters(isA(ParameterReplacer.class), isA(TransformInfo.class));

    }

    @Test
    public void testReplaceParam() throws Exception {

        ParameterTransformer paramTransformer = mock(ParameterTransformer.class);
        doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ParameterReplacer replacer = (ParameterReplacer) invocation.getArguments()[0];
                replacer.setInt(2, 2);  // just replace the second id.  after this: "where id=1 and id=2"
                return null;
            }
        }).when(paramTransformer).transformParameters(isA(ParameterReplacer.class), isA(TransformInfo.class));

        Connection connection = getProxyConnection(paramTransformer);

        // to have two parameters, checking id column twice
        PreparedStatement ps = connection.prepareStatement("SELECT id, name FROM foo WHERE id = ? AND id = ?");
        ps.setInt(1, 1);
        ps.setInt(2, 1);
        ResultSet rs = ps.executeQuery();

        assertThat(rs.next()).as("should have no matching record 'where id=1 and id=2'").isFalse();
        verify(paramTransformer, only()).transformParameters(isA(ParameterReplacer.class), isA(TransformInfo.class));

    }

    @Test
    public void testBatchReplaceParam() throws Exception {

        ParameterTransformer paramTransformer = mock(ParameterTransformer.class);
        doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ParameterReplacer replacer = (ParameterReplacer) invocation.getArguments()[0];
                String name = replacer.getValue(1);
                replacer.setString(1, name + "-INTERCEPTED");
                return null;
            }
        }).when(paramTransformer).transformParameters(isA(ParameterReplacer.class), isA(TransformInfo.class));

        Connection connection = getProxyConnection(paramTransformer);

        PreparedStatement ps = connection.prepareStatement("UPDATE foo SET name = ? WHERE id = ?");
        ps.setString(1, "FOO");
        ps.setInt(2, 1);
        ps.addBatch();
        ps.setString(1, "BAR");
        ps.setInt(2, 2);
        ps.addBatch();
        int[] result = ps.executeBatch();

        assertThat(result).containsExactly(1, 1);

        String name = queryForString("SELECT name FROM foo WHERE id = 1");
        assertThat(name).isEqualTo("FOO-INTERCEPTED");

        name = queryForString("SELECT name FROM foo WHERE id = 2");
        assertThat(name).isEqualTo("BAR-INTERCEPTED");

        verify(paramTransformer, times(2)).transformParameters(isA(ParameterReplacer.class), isA(TransformInfo.class));

    }

    private String queryForString(String sql) throws SQLException {
        Statement stmt = rawDatasource.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        assertThat(rs.next()).as("ResultSet should have result").isTrue();
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
                ParameterReplacer replacer = (ParameterReplacer) invocation.getArguments()[0];
                replacer.clearParameters();
                replacer.setString(1, "BAZ");
                replacer.setInt(2, 3);  // id=3
                return null;
            }
        }).doAnswer(new Answer() {
                        // for second batch
                        public Object answer(InvocationOnMock invocation) throws Throwable {
                            // second batch. don't call clearParameters().
                            ParameterReplacer replacer = (ParameterReplacer) invocation.getArguments()[0];
                            String name = replacer.getValue(1);
                            replacer.setString(1, name + "-INTERCEPTED");
                            return null;
                        }
                    }

        ).when(paramTransformer).transformParameters(isA(ParameterReplacer.class), isA(TransformInfo.class));

        Connection connection = getProxyConnection(paramTransformer);

        PreparedStatement ps = connection.prepareStatement("UPDATE foo SET name = ? WHERE id = ?");
        ps.setString(1, "FOO");
        ps.setInt(2, 1);
        ps.addBatch();
        ps.setString(1, "BAR");
        ps.setInt(2, 2);
        ps.addBatch();
        int[] result = ps.executeBatch();

        assertThat(result).containsExactly(1, 1);

        String name = queryForString("SELECT name FROM foo WHERE id = 1");
        assertThat(name).isEqualTo("foo");

        name = queryForString("SELECT name FROM foo WHERE id = 2");
        assertThat(name).isEqualTo("BAR-INTERCEPTED");

        name = queryForString("SELECT name FROM foo WHERE id = 3");
        assertThat(name).isEqualTo("BAZ");

        verify(paramTransformer, times(2)).transformParameters(isA(ParameterReplacer.class), isA(TransformInfo.class));

    }

}
