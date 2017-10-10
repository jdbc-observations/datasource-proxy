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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


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
        log.setEnabledLogLevel(this.logLevel);

        CommonsQueryLoggingListener loggingListener = new CommonsQueryLoggingListener();
        loggingListener.setLog(log);
        loggingListener.setLogLevel(logLevel);

        ProxyDataSource proxyDataSource = ProxyDataSourceBuilder.create(jdbcDataSource).listener(loggingListener).build();


        Connection connection = proxyDataSource.getConnection();
        Statement statement = connection.createStatement();
        statement.executeQuery("select * from emp where id=1");
        statement.executeQuery("select * from emp where id=2");

        verifyMessage(log, "select * from emp where id=1", "select * from emp where id=2");

    }

    private void verifyMessage(InMemoryCommonsLog log, String... queries) {

        for (CommonsLogLevel commonsLogLevel : CommonsLogLevel.values()) {
            List<String> messageList = log.getMessages(commonsLogLevel);
            if (commonsLogLevel == this.logLevel) {
                assertThat(messageList).hasSize(queries.length);
                for (int i = 0; i < queries.length; i++) {
                    final String query = queries[i];
                    assertThat(messageList.get(i)).contains(query);
                }
            } else {
                assertThat(messageList).isEmpty();
            }
        }

    }

}
