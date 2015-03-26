package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.listener.CommonsLogLevel;
import net.ttddyy.dsproxy.listener.CommonsQueryLoggingListener;
import net.ttddyy.dsproxy.support.ProxyDataSource;
import org.apache.commons.logging.LogFactory;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author Tadaya Tsuyukubo
 */
public class LoggingListenerTest {
    private DataSource jdbcDataSource;
    private ProxyDataSource proxyDataSource;
    private CommonsQueryLoggingListener loggingListener;

    @Before
    public void setup() throws Exception {
        // TODO: clean up logger intercept mechanism
        System.setProperty("org.apache.commons.logging.Log", InMemoryLog.class.getCanonicalName());

        loggingListener = new CommonsQueryLoggingListener();

        // real datasource
        jdbcDataSource = TestUtils.getDataSourceWithData();

        proxyDataSource = new ProxyDataSource();
        proxyDataSource.setDataSource(jdbcDataSource);
        proxyDataSource.setListener(loggingListener);
    }

    @After
    public void teardown() throws Exception {
        TestUtils.shutdown(jdbcDataSource);

        InMemoryLog.clear();
        System.setProperty("org.apache.commons.logging.Log", "");
    }


    @Test
    public void testStatement() throws Exception {
        Connection connection = proxyDataSource.getConnection();
        Statement statement = connection.createStatement();
        statement.executeQuery("select * from emp");


        final InMemoryLog log = getInMemoryLog();
        verifyMessage(CommonsLogLevel.DEBUG, log, "select * from emp");
    }

    @Test
    public void testStatementWithBatch() throws Exception {
        Connection connection = proxyDataSource.getConnection();
        Statement statement = connection.createStatement();
        statement.addBatch("select * from emp where id = 1");
        statement.addBatch("select * from emp where id = 2");
        statement.executeBatch();

        final InMemoryLog log = getInMemoryLog();

        // this is batch execution, so query call will be just one time
        verifyMessage(CommonsLogLevel.DEBUG, log, "select * from emp where id = 1");
        verifyMessage(CommonsLogLevel.DEBUG, log, "select * from emp where id = 2");
    }

    @Test
    public void testPreparedStatement() throws Exception {
        Connection connection = proxyDataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("select * from emp where id = ?");
        statement.setInt(1, 2);
        statement.executeQuery();


        final InMemoryLog log = getInMemoryLog();
        verifyMessage(CommonsLogLevel.DEBUG, log, "select * from emp where id = ?");
    }

    @Test
    public void testPreparedStatementWithBatch() throws Exception {
        Connection connection = proxyDataSource.getConnection();
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


        final InMemoryLog log = getInMemoryLog();
        verifyMessage(CommonsLogLevel.DEBUG, log, "update emp set name = ? where id = ?");
        verifyMessage(CommonsLogLevel.DEBUG, log, "(FOO,1)");
        verifyMessage(CommonsLogLevel.DEBUG, log, "(BAR,2)");
    }

    private InMemoryLog getInMemoryLog() {
        final InMemoryLog log = (InMemoryLog) LogFactory.getLog(CommonsQueryLoggingListener.class);
        LogFactory.releaseAll(); // release the Log cache
        return log;
    }


    private void verifyMessage(CommonsLogLevel logLevel, InMemoryLog log, String... queries) {
        Map<CommonsLogLevel, List> messages = new HashMap<CommonsLogLevel, List>();
        messages.put(CommonsLogLevel.DEBUG, log.getDebugMessages());
        messages.put(CommonsLogLevel.ERROR, log.getErrorMessages());
        messages.put(CommonsLogLevel.FATAL, log.getFatalMessages());
        messages.put(CommonsLogLevel.INFO, log.getInfoMessages());
        messages.put(CommonsLogLevel.TRACE, log.getTraceMessages());
        messages.put(CommonsLogLevel.WARN, log.getWarnMessages());

        for (Map.Entry<CommonsLogLevel, List> entry : messages.entrySet()) {
            CommonsLogLevel msgLevel = entry.getKey();
            List<?> messageList = entry.getValue();

            int expectedMsgSize = (msgLevel == logLevel) ? queries.length : 0;

            assertThat(messageList, hasSize(expectedMsgSize));
            if (expectedMsgSize > 0) {
                for (int i = 0; i < queries.length; i++) {
                    final String query = queries[i];

                    assertThat(messageList.get(i), is(instanceOf(String.class)));
                    String message = (String) messageList.get(i);
                    assertThat(message, containsString(query));
                }
            }
        }
    }
}
