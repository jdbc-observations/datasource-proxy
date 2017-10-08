package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.ExecutionInfoBuilder;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.QueryInfoBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.NoOpLog;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.3.1
 */
public class CommonsQueryLoggingListenerTest {

    public static class NameAwareLog extends NoOpLog {
        String name;

        public NameAwareLog(String name) {
            this.name = name;
        }
    }


    @Before
    public void setup() throws Exception {
        LogFactory.releaseAll();
        // see configuration for logger resolution order
        // https://commons.apache.org/proper/commons-logging/guide.html
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", NameAwareLog.class.getName());
    }

    @After
    public void teardown() throws Exception {
        LogFactory.getFactory().removeAttribute("org.apache.commons.logging.Log");
    }

    @Test
    public void defaultLoggerName() {
        LogFactory.getFactory().release();
        CommonsQueryLoggingListener listener = new CommonsQueryLoggingListener();
        String name = ((NameAwareLog) listener.getLog()).name;
        assertThat(name).as("Default logger name").isEqualTo("net.ttddyy.dsproxy.listener.logging.CommonsQueryLoggingListener");
    }

    @Test
    public void setLogName() {
        Log log = LogFactory.getLog("my.logger");
        CommonsQueryLoggingListener listener = new CommonsQueryLoggingListener();
        listener.setLog("my.logger");
        assertThat(listener.getLog()).isSameAs(log);
    }


    @Test
    public void loggingCondition() {
        CommonsQueryLoggingListener listener = new CommonsQueryLoggingListener();
        InMemoryCommonsLog log = new InMemoryCommonsLog();
        listener.setLog(log);

        ExecutionInfo execInfo = ExecutionInfoBuilder.create().build();
        List<QueryInfo> queryInfoList = new ArrayList<QueryInfo>();
        queryInfoList.add(QueryInfoBuilder.create().query("select * ").build());

        // listener writes to more serious level
        listener.setLogLevel(CommonsLogLevel.DEBUG);
        log.setEnabledLogLevel(CommonsLogLevel.TRACE);

        listener.afterQuery(execInfo, queryInfoList);

        assertThat(log.getTraceMessages()).isEmpty();
        assertThat(log.getDebugMessages()).hasSize(1);
        assertThat(log.getInfoMessages()).isEmpty();
        assertThat(log.getWarnMessages()).isEmpty();
        assertThat(log.getErrorMessages()).isEmpty();
        assertThat(log.getFatalMessages()).isEmpty();

        // listener writes to less serious level
        log.reset();
        listener.setLogLevel(CommonsLogLevel.TRACE);
        log.setEnabledLogLevel(CommonsLogLevel.DEBUG);

        listener.afterQuery(execInfo, queryInfoList);

        assertThat(log.getTraceMessages()).isEmpty();
        assertThat(log.getDebugMessages()).isEmpty();
        assertThat(log.getInfoMessages()).isEmpty();
        assertThat(log.getWarnMessages()).isEmpty();
        assertThat(log.getErrorMessages()).isEmpty();
        assertThat(log.getFatalMessages()).isEmpty();

        // listener writes to same level
        log.reset();
        listener.setLogLevel(CommonsLogLevel.DEBUG);
        log.setEnabledLogLevel(CommonsLogLevel.DEBUG);

        listener.afterQuery(execInfo, queryInfoList);

        assertThat(log.getTraceMessages()).isEmpty();
        assertThat(log.getDebugMessages()).hasSize(1);
        assertThat(log.getInfoMessages()).isEmpty();
        assertThat(log.getWarnMessages()).isEmpty();
        assertThat(log.getErrorMessages()).isEmpty();
        assertThat(log.getFatalMessages()).isEmpty();

    }
}
