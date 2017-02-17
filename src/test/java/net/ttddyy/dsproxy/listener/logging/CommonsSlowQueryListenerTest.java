package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import org.junit.After;
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
public class CommonsSlowQueryListenerTest {

    private CommonsSlowQueryListener listener;

    @Before
    public void setup() throws Exception {
        this.listener = new CommonsSlowQueryListener();
        this.listener.setLog(new InMemoryLog());
    }

    @After
    public void teardown() throws Exception {
        // since it stores logs in static variable, need to clear them
        InMemoryLog.clear();
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
        TimeUnit.MILLISECONDS.sleep(100);
        this.listener.afterQuery(executionInfo, queryInfos);

        InMemoryLog log = (InMemoryLog) this.listener.getLog();
        List<Object> messages = log.getDebugMessages();
        assertThat(messages).hasSize(1);
        assertThat((String) messages.get(0)).contains("SELECT 1");
    }
}
