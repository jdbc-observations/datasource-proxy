package net.ttddyy.dsproxy.listener;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.3.1
 */
public class SLF4JQueryLoggingListenerTest {

    @Test
    public void defaultLoggerName() {
        SLF4JQueryLoggingListener listener = new SLF4JQueryLoggingListener();
        assertThat(listener.logger.getName())
                .as("Default logger name").isEqualTo("net.ttddyy.dsproxy.listener.SLF4JQueryLoggingListener");
    }

    @Test
    public void setLoggerName() {
        SLF4JQueryLoggingListener listener = new SLF4JQueryLoggingListener();
        listener.setLoggerName("my.logger");
        assertThat(listener.logger.getName()).as("Updated logger name").isEqualTo("my.logger");
    }

}
