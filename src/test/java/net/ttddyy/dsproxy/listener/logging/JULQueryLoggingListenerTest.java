package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.ExecutionInfoBuilder;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.QueryInfoBuilder;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4.1
 */
public class JULQueryLoggingListenerTest {

    @Test
    public void defaultLoggerName() {
        JULQueryLoggingListener listener = new JULQueryLoggingListener();
        assertThat(listener.getLogger().getName())
                .as("Default logger name").isEqualTo("net.ttddyy.dsproxy.listener.logging.JULQueryLoggingListener");
    }

    @Test
    public void setLoggerName() {
        JULQueryLoggingListener listener = new JULQueryLoggingListener();
        listener.setLogger("my.logger");
        assertThat(listener.getLogger().getName()).as("Updated logger name").isEqualTo("my.logger");
    }

    @Test
    public void loggingCondition() {
        JULQueryLoggingListener listener = new JULQueryLoggingListener();
        InMemoryJULLogger logger = new InMemoryJULLogger();
        listener.setLogger(logger);

        ExecutionInfo execInfo = ExecutionInfoBuilder.create().build();
        List<QueryInfo> queryInfoList = new ArrayList<QueryInfo>();
        queryInfoList.add(QueryInfoBuilder.create().query("select * ").build());

        // listener writes to more serious level
        listener.setLogLevel(Level.WARNING);
        logger.setLoggerLevel(Level.INFO);

        listener.afterQuery(execInfo, queryInfoList);

        assertThat(logger.getSevereMessages()).isEmpty();
        assertThat(logger.getWarningMessages()).hasSize(1);
        assertThat(logger.getInfoMessages()).isEmpty();
        assertThat(logger.getConfigMessages()).isEmpty();
        assertThat(logger.getFineMessages()).isEmpty();
        assertThat(logger.getFinerMessages()).isEmpty();
        assertThat(logger.getFinestMessages()).isEmpty();

        // listener writes to less serious level
        logger.reset();
        listener.setLogLevel(Level.FINE);
        logger.setLoggerLevel(Level.WARNING);

        listener.afterQuery(execInfo, queryInfoList);

        assertThat(logger.getSevereMessages()).isEmpty();
        assertThat(logger.getWarningMessages()).isEmpty();
        assertThat(logger.getInfoMessages()).isEmpty();
        assertThat(logger.getConfigMessages()).isEmpty();
        assertThat(logger.getFineMessages()).isEmpty();
        assertThat(logger.getFinerMessages()).isEmpty();
        assertThat(logger.getFinestMessages()).isEmpty();

        // listener writes to same level
        logger.reset();
        listener.setLogLevel(Level.WARNING);
        logger.setLoggerLevel(Level.WARNING);

        listener.afterQuery(execInfo, queryInfoList);

        assertThat(logger.getSevereMessages()).isEmpty();
        assertThat(logger.getWarningMessages()).hasSize(1);
        assertThat(logger.getInfoMessages()).isEmpty();
        assertThat(logger.getConfigMessages()).isEmpty();
        assertThat(logger.getFineMessages()).isEmpty();
        assertThat(logger.getFinerMessages()).isEmpty();
        assertThat(logger.getFinestMessages()).isEmpty();

    }

    @Test
    public void loggingFilterCustomLogic() {
        JULQueryLoggingListener listener = new JULQueryLoggingListener();
        InMemoryJULLogger logger = new InMemoryJULLogger();
        listener.setLogger(logger);
        listener.setLogLevel(Level.FINE);
        logger.setLoggerLevel(Level.FINE);

        ExecutionInfo execInfo = ExecutionInfoBuilder.create().build();
        List<QueryInfo> logmeList = new ArrayList<QueryInfo>();
        logmeList.add(QueryInfoBuilder.create().query("select * from logme").build());
        List<QueryInfo> skipList = new ArrayList<QueryInfo>();
        skipList.add(QueryInfoBuilder.create().query("select * from skipme").build());

        // Filter with custom logic: skip queries containing 'skipme'
        listener.setLoggingFilter(new LoggingFilter() {
            public boolean shouldLog(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
                for (QueryInfo qi : queryInfoList) {
                    if (qi.getQuery().contains("skipme")) {
                        return false;
                    }
                }
                return true;
            }
        });

        listener.afterQuery(execInfo, logmeList);

        assertThat(logger.getFineMessages()).hasSize(1);

        logger.reset();
        listener.afterQuery(execInfo, skipList);

        assertThat(logger.getFineMessages()).isEmpty();
    }
}
