package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4.1
 */
public class JULSlowQueryListenerTest {

    private JULSlowQueryListener listener;

    @Before
    public void setup() throws Exception {
        this.listener = new JULSlowQueryListener();
        this.listener.setLogger(new InMemoryJULLogger());
    }


    @Test
    public void testLogMessage() throws Exception {
        this.listener.setThreshold(50);
        this.listener.setThresholdTimeUnit(TimeUnit.MILLISECONDS);

        ExecutionInfo executionInfo = new ExecutionInfo();
        QueryInfo queryInfo = new QueryInfo();
        queryInfo.setQuery("SELECT 1");
        List<QueryInfo> queryInfos = new ArrayList<QueryInfo>();
        queryInfos.add(queryInfo);

        this.listener.beforeQuery(executionInfo, queryInfos);
        TimeUnit.MILLISECONDS.sleep(300);
        this.listener.afterQuery(executionInfo, queryInfos);

        InMemoryJULLogger logger = (InMemoryJULLogger) this.listener.getLogger();
        List<String> messages = logger.getWarningMessages();
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0)).contains("SELECT 1");
    }

    @Test
    public void defaultLoggerName() {
        JULSlowQueryListener listener = new JULSlowQueryListener();
        assertThat(listener.getLogger().getName())
                .as("Default logger name").isEqualTo("net.ttddyy.dsproxy.listener.logging.JULSlowQueryListener");
    }

}
