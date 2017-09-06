package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.ExecutionInfoBuilder;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.QueryInfoBuilder;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.3.1
 */
public class SLF4JQueryLoggingListenerTest {

    @Test
    public void defaultLoggerName() {
        SLF4JQueryLoggingListener listener = new SLF4JQueryLoggingListener();
        assertThat(listener.getLogger().getName())
                .as("Default logger name").isEqualTo("net.ttddyy.dsproxy.listener.logging.SLF4JQueryLoggingListener");
    }

    @Test
    public void setLoggerName() {
        SLF4JQueryLoggingListener listener = new SLF4JQueryLoggingListener();
        listener.setLogger("my.logger");
        assertThat(listener.getLogger().getName()).as("Updated logger name").isEqualTo("my.logger");
    }

    @Test
    public void loggingCondition() {
        SLF4JQueryLoggingListener listener = new SLF4JQueryLoggingListener();
        InMemorySLF4JLogger logger = new InMemorySLF4JLogger();

        List<QueryInfo> queryInfoList = new ArrayList<QueryInfo>();
        ExecutionInfo execInfo = ExecutionInfoBuilder.create().build();
        queryInfoList.add(QueryInfoBuilder.create().query("select * ").build());


        // listener writes to more serious level
        listener.setLogLevel(SLF4JLogLevel.DEBUG);
        logger.setEnabledLogLevel(SLF4JLogLevel.TRACE);
        listener.setLogger(logger);

        listener.afterQuery(execInfo, queryInfoList);

        assertThat(logger.getTraceMessages()).isEmpty();
        assertThat(logger.getDebugMessages()).hasSize(1);
        assertThat(logger.getInfoMessages()).isEmpty();
        assertThat(logger.getWarnMessages()).isEmpty();
        assertThat(logger.getErrorMessages()).isEmpty();

        // listener writes to less serious level
        logger.reset();
        listener.setLogLevel(SLF4JLogLevel.TRACE);
        logger.setEnabledLogLevel(SLF4JLogLevel.DEBUG);

        listener.afterQuery(execInfo, queryInfoList);

        assertThat(logger.getTraceMessages()).isEmpty();
        assertThat(logger.getDebugMessages()).isEmpty();
        assertThat(logger.getInfoMessages()).isEmpty();
        assertThat(logger.getWarnMessages()).isEmpty();
        assertThat(logger.getErrorMessages()).isEmpty();

        // listener writes to same level
        logger.reset();
        listener.setLogLevel(SLF4JLogLevel.DEBUG);
        logger.setEnabledLogLevel(SLF4JLogLevel.DEBUG);

        listener.afterQuery(execInfo, queryInfoList);

        assertThat(logger.getTraceMessages()).isEmpty();
        assertThat(logger.getDebugMessages()).hasSize(1);
        assertThat(logger.getInfoMessages()).isEmpty();
        assertThat(logger.getWarnMessages()).isEmpty();
        assertThat(logger.getErrorMessages()).isEmpty();
    }

}
