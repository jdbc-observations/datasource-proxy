package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.TestUtils;
import net.ttddyy.dsproxy.proxy.ProxyConfig;
import net.ttddyy.dsproxy.support.ProxyDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;

/**
 * @author Tadaya Tsuyukubo
 */
public class LoggingListenerTest {
    private DataSource jdbcDataSource;
    private ProxyDataSource proxyDataSource;
    private CommonsQueryLoggingListener loggingListener;
    private InMemoryCommonsLog inMemoryLog;

    @Before
    public void setup() throws Exception {

        this.inMemoryLog = new InMemoryCommonsLog();

        this.loggingListener = new CommonsQueryLoggingListener();
        this.loggingListener.setLog(this.inMemoryLog);

        ProxyConfig proxyConfig = ProxyConfig.Builder.create().queryListener(this.loggingListener).build();

        // real datasource
        this.jdbcDataSource = TestUtils.getDataSourceWithData();

        this.proxyDataSource = new ProxyDataSource();
        this.proxyDataSource.setDataSource(this.jdbcDataSource);
        this.proxyDataSource.setProxyConfig(proxyConfig);
    }

    @After
    public void teardown() throws Exception {
        TestUtils.shutdown(this.jdbcDataSource);
    }


    @Test
    public void testStatement() throws Exception {
        Connection connection = this.proxyDataSource.getConnection();
        Statement statement = connection.createStatement();
        statement.executeQuery("select * from emp");

        verifyMessage(CommonsLogLevel.DEBUG, this.inMemoryLog, "select * from emp");
    }

    @Test
    public void testStatementWithBatch() throws Exception {
        Connection connection = this.proxyDataSource.getConnection();
        Statement statement = connection.createStatement();
        statement.addBatch("select * from emp where id = 1");
        statement.addBatch("select * from emp where id = 2");
        statement.executeBatch();

        // this is batch execution, so query call will be just one time
        verifyMessage(CommonsLogLevel.DEBUG, this.inMemoryLog, "select * from emp where id = 1");
        verifyMessage(CommonsLogLevel.DEBUG, this.inMemoryLog, "select * from emp where id = 2");
    }

    @Test
    public void testPreparedStatement() throws Exception {
        Connection connection = this.proxyDataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("select * from emp where id = ?");
        statement.setInt(1, 2);
        statement.executeQuery();


        verifyMessage(CommonsLogLevel.DEBUG, this.inMemoryLog, "select * from emp where id = ?");
    }

    @Test
    public void testPreparedStatementWithNullParam() throws Exception {
        Connection connection = this.proxyDataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("select ? as nullCol, name from emp where id = ?");
        statement.setString(1, null);
        statement.setInt(2, 2);
        statement.executeQuery();

        verifyMessage(CommonsLogLevel.DEBUG, this.inMemoryLog, "select ? as nullCol, name from emp where id = ?");
    }

    @Test
    public void testPreparedStatementWithBatch() throws Exception {
        Connection connection = this.proxyDataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("update emp set name = ? where id = ?");
        statement.setString(1, "BAZ");
        statement.setInt(2, 3);
        statement.addBatch();

        statement.clearBatch();

        statement.setString(1, "FOO");
        statement.setInt(2, 1);
        statement.addBatch();

        statement.setString(1, "BAR");
        statement.setInt(2, 2);
        statement.addBatch();

        statement.executeBatch();


        verifyMessage(CommonsLogLevel.DEBUG, this.inMemoryLog, "update emp set name = ? where id = ?");
        verifyMessage(CommonsLogLevel.DEBUG, this.inMemoryLog, "[(FOO,1),(BAR,2)]");
    }

    private void verifyMessage(CommonsLogLevel logLevel, InMemoryCommonsLog log, String... queries) {
        Map<CommonsLogLevel, List<String>> messages = new HashMap<CommonsLogLevel, List<String>>();
        messages.put(CommonsLogLevel.DEBUG, log.getDebugMessages());
        messages.put(CommonsLogLevel.ERROR, log.getErrorMessages());
        messages.put(CommonsLogLevel.FATAL, log.getFatalMessages());
        messages.put(CommonsLogLevel.INFO, log.getInfoMessages());
        messages.put(CommonsLogLevel.TRACE, log.getTraceMessages());
        messages.put(CommonsLogLevel.WARN, log.getWarnMessages());

        for (Map.Entry<CommonsLogLevel, List<String>> entry : messages.entrySet()) {
            CommonsLogLevel msgLevel = entry.getKey();
            List<String> messageList = entry.getValue();

            int expectedMsgSize = (msgLevel == logLevel) ? queries.length : 0;
            assertThat(messageList).hasSize(expectedMsgSize);

            if (expectedMsgSize > 0) {
                for (int i = 0; i < queries.length; i++) {
                    final String query = queries[i];
                    assertThat(messageList.get(i)).contains(query);
                }
            }
        }
    }
}
