package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.listener.CommonsLogLevel;
import net.ttddyy.dsproxy.listener.CommonsQueryLoggingListener;
import net.ttddyy.dsproxy.support.ProxyDataSource;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.apache.commons.logging.LogFactory;
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
        // TODO: clean up logger intercept mechanism
        System.setProperty("org.apache.commons.logging.Log", InMemoryLog.class.getCanonicalName());

        // real datasource
        jdbcDataSource = TestUtils.getDataSourceWithData();
    }

    @After
    public void teardown() throws Exception {
        TestUtils.shutdown(jdbcDataSource);

        InMemoryLog.clear();
        System.setProperty("org.apache.commons.logging.Log", "");
    }

    @Test
    public void testLogLevel() throws Exception {

        CommonsQueryLoggingListener loggingListener = new CommonsQueryLoggingListener();
        loggingListener.setLogLevel(logLevel);

        ProxyDataSource proxyDataSource = ProxyDataSourceBuilder.create(jdbcDataSource).logQueryByCommons(this.logLevel).build();


        Connection connection = proxyDataSource.getConnection();
        Statement statement = connection.createStatement();
        statement.executeQuery("select * from emp where id=1");
        statement.executeQuery("select * from emp where id=2");


        final InMemoryLog log = (InMemoryLog) LogFactory.getLog(CommonsQueryLoggingListener.class);
        LogFactory.releaseAll(); // release the Log cache

        verifyMessage(logLevel, log, "select * from emp where id=1", "select * from emp where id=2");

    }

    // TODO: cleanup
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
