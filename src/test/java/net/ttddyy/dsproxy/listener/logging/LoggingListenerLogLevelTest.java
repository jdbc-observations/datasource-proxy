package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.TestUtils;
import net.ttddyy.dsproxy.support.ProxyDataSource;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author Tadaya Tsuyukubo
 */
@RunWith(Parameterized.class)
public class LoggingListenerLogLevelTest {

    @Parameterized.Parameters
    public static Object[][] getLogLevelData() {
        return new Object[][]{
                {CommonsLogLevel.DEBUG},
                {CommonsLogLevel.ERROR},
                {CommonsLogLevel.FATAL},
                {CommonsLogLevel.INFO},
                {CommonsLogLevel.TRACE},
                {CommonsLogLevel.WARN},
        };
    }

    private DataSource jdbcDataSource;
    private CommonsLogLevel logLevel;

    public LoggingListenerLogLevelTest(CommonsLogLevel logLevel) {
        this.logLevel = logLevel;
    }

    @Before
    public void setup() throws Exception {
        // real datasource
        jdbcDataSource = TestUtils.getDataSourceWithData();
    }

    @After
    public void teardown() throws Exception {
        TestUtils.shutdown(jdbcDataSource);
    }

    @Test
    public void testLogLevel() throws Exception {

        InMemoryCommonsLog log = new InMemoryCommonsLog();

        CommonsQueryLoggingListener loggingListener = new CommonsQueryLoggingListener();
        loggingListener.setLog(log);
        loggingListener.setLogLevel(logLevel);

        ProxyDataSource proxyDataSource = ProxyDataSourceBuilder.create(jdbcDataSource).listener(loggingListener).build();


        Connection connection = proxyDataSource.getConnection();
        Statement statement = connection.createStatement();
        statement.executeQuery("select * from emp where id=1");
        statement.executeQuery("select * from emp where id=2");

        verifyMessage(logLevel, log, "select * from emp where id=1", "select * from emp where id=2");

    }

    // TODO: cleanup
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
            assertThat(messageList, hasSize(expectedMsgSize));

            if (expectedMsgSize > 0) {
                for (int i = 0; i < queries.length; i++) {
                    final String query = queries[i];
                    assertThat(messageList.get(i), containsString(query));
                }
            }
        }
    }

}
