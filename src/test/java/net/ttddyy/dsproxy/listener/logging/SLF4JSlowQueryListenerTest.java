package net.ttddyy.dsproxy.listener.logging;

import static net.ttddyy.dsproxy.QueryExecutionContextBuilder.withQueries;
import net.ttddyy.dsproxy.proxy.MutableQueryExecutionContext;
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
class SLF4JSlowQueryListenerTest {

    private SLF4JSlowQueryListener listener;

    @BeforeEach
    void setup() throws Exception {
        this.listener = new SLF4JSlowQueryListener();
        this.listener.setLogger(new InMemorySLF4JLogger());
    }


    @Test
    void logMessage() throws Exception {
        this.listener.setThreshold(50);
        this.listener.setThresholdTimeUnit(TimeUnit.MILLISECONDS);

        MutableQueryExecutionContext executionInfo = new MutableQueryExecutionContext();
        QueryInfo queryInfo = new QueryInfo();
        queryInfo.setQuery("SELECT 1");
        List<QueryInfo> queryInfos = new ArrayList<QueryInfo>();
        queryInfos.add(queryInfo);

        this.listener.beforeQuery(withQueries(executionInfo, queryInfos));
        TimeUnit.MILLISECONDS.sleep(100);
        this.listener.afterQuery(withQueries(executionInfo, queryInfos));

        InMemorySLF4JLogger logger = (InMemorySLF4JLogger) this.listener.getLogger();
        List<String> messages = logger.getWarnMessages();
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0)).contains("SELECT 1");
    }

    @Test
    void defaultLoggerName() {
        SLF4JSlowQueryListener listener = new SLF4JSlowQueryListener();
        assertThat(listener.getLogger().getName())
                .as("Default logger name").isEqualTo("net.ttddyy.dsproxy.listener.logging.SLF4JSlowQueryListener");
    }

}
