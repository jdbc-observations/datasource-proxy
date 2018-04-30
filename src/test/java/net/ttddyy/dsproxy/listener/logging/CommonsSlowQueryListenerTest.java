package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4.1
 */
public class CommonsSlowQueryListenerTest {

    private CommonsSlowQueryListener listener;

    @BeforeEach
    public void setup() throws Exception {
        this.listener = new CommonsSlowQueryListener();
        this.listener.setLog(new InMemoryCommonsLog());
    }

    @Test
    public void testLogMessage() throws Exception {
        this.listener.setThreshold(50);
        this.listener.setThresholdTimeUnit(TimeUnit.MILLISECONDS);

        QueryInfo queryInfo = new QueryInfo();
        queryInfo.setQuery("SELECT 1");
        List<QueryInfo> queryInfos = new ArrayList<>();
        queryInfos.add(queryInfo);

        ExecutionInfo executionInfo = new ExecutionInfo();
        executionInfo.setQueries(queryInfos);

        this.listener.beforeQuery(executionInfo);
        TimeUnit.MILLISECONDS.sleep(100);
        this.listener.afterQuery(executionInfo);

        InMemoryCommonsLog log = (InMemoryCommonsLog) this.listener.getLog();
        List<String> messages = log.getWarnMessages();
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0)).contains("SELECT 1");
    }
}
