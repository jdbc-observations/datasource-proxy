package net.ttddyy.dsproxy.listener.logging;

import org.apache.commons.logging.impl.NoOpLog;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
        System.setProperty("org.apache.commons.logging.Log", NameAwareLog.class.getName());
    }

    @After
    public void teardown() throws Exception {
        System.setProperty("org.apache.commons.logging.Log", "");
    }

    @Test
    public void defaultLoggerName() {
        CommonsQueryLoggingListener listener = new CommonsQueryLoggingListener();
        String name = ((NameAwareLog) listener.log).name;
        assertThat(name).as("Default logger name").isEqualTo("net.ttddyy.dsproxy.listener.logging.CommonsQueryLoggingListener");
    }

    @Test
    public void setLoggerName() {
        CommonsQueryLoggingListener listener = new CommonsQueryLoggingListener();
        listener.setLoggerName("my.logger");
        String name = ((NameAwareLog) listener.log).name;
        assertThat(name).as("Updated logger name").isEqualTo("my.logger");
    }

}
