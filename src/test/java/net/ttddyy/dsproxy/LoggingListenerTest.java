package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.listener.CommonsLogLevel;
import net.ttddyy.dsproxy.listener.CommonsQueryLoggingListener;
import net.ttddyy.dsproxy.support.ProxyDataSource;
import org.apache.commons.logging.LogFactory;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tadaya Tsuyukubo
 */
public class LoggingListenerTest {
    private DataSource jdbcDataSource;
    private ProxyDataSource proxyDataSource;
    private CommonsQueryLoggingListener loggingListener;

    @BeforeMethod
    public void setup() throws Exception {
        System.setProperty("org.apache.commons.logging.Log", InMemoryLog.class.getCanonicalName());

        loggingListener = new CommonsQueryLoggingListener();

        // real datasource
        jdbcDataSource = TestUtils.getDataSourceWithData();

        proxyDataSource = new ProxyDataSource();
        proxyDataSource.setDataSource(jdbcDataSource);
        proxyDataSource.setListener(loggingListener);
    }

    @AfterMethod
    public void teardown() throws Exception {
        TestUtils.shutdown(jdbcDataSource);

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
        verifyMessage(CommonsLogLevel.DEBUG, log, "[FOO,1]");
        verifyMessage(CommonsLogLevel.DEBUG, log, "[BAR,2]");
    }

    private InMemoryLog getInMemoryLog() {
        final InMemoryLog log = (InMemoryLog) LogFactory.getLog(CommonsQueryLoggingListener.class);
        LogFactory.releaseAll(); // release the Log cache
        return log;
    }

    @DataProvider
    public Object[][] getLogLevelData() {
        return new Object[][]{
                {CommonsLogLevel.DEBUG},
                {CommonsLogLevel.ERROR},
                {CommonsLogLevel.FATAL},
                {CommonsLogLevel.INFO},
                {CommonsLogLevel.TRAC},
                {CommonsLogLevel.WARN},
        };
    }

    @Test(dataProvider = "getLogLevelData")
    public void testLogLevel(CommonsLogLevel logLevel) throws Exception {
        loggingListener.setLogLevel(logLevel);

        Connection connection = proxyDataSource.getConnection();
        Statement statement = connection.createStatement();
        statement.executeQuery("select * from emp where id=1");
        statement.executeQuery("select * from emp where id=2");


        final InMemoryLog log = getInMemoryLog();

        verifyMessage(logLevel, log,
                "select * from emp where id=1", "select * from emp where id=2");

    }

    private void verifyMessage(CommonsLogLevel logLevel, InMemoryLog log, String... queries) {
        Map<CommonsLogLevel, List> messages = new HashMap<CommonsLogLevel, List>();
        messages.put(CommonsLogLevel.DEBUG, log.getDebugMessages());
        messages.put(CommonsLogLevel.ERROR, log.getErrorMessages());
        messages.put(CommonsLogLevel.FATAL, log.getFatalMessages());
        messages.put(CommonsLogLevel.INFO, log.getInfoMessages());
        messages.put(CommonsLogLevel.TRAC, log.getTraceMessages());
        messages.put(CommonsLogLevel.WARN, log.getWarnMessages());

        for (Map.Entry<CommonsLogLevel, List> entry : messages.entrySet()) {
            CommonsLogLevel msgLevel = entry.getKey();
            List messageList = entry.getValue();

            int expectedMsgSize = (msgLevel == logLevel) ? queries.length : 0;

            assertEquals(messageList.size(), expectedMsgSize);
            if (expectedMsgSize > 0) {
                for (int i = 0; i < queries.length; i++) {
                    final String query = queries[i];

                    assertTrue(messageList.get(i) instanceof String);
                    String message = (String) messageList.get(i);
                    assertTrue(message.contains(query));
                }
            }
        }
    }
}
