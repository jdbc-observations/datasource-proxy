package net.ttddyy.dsproxy.listener.logging;

import org.junit.Test;

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

}
