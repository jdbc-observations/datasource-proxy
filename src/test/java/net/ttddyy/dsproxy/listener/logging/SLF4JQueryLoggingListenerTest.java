package net.ttddyy.dsproxy.listener.logging;

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
        assertThat(listener.getLogger().getName())
                .as("Default logger name").isEqualTo("net.ttddyy.dsproxy.listener.logging.SLF4JQueryLoggingListener");
    }

    @Test
    public void setLoggerName() {
        SLF4JQueryLoggingListener listener = new SLF4JQueryLoggingListener();
        listener.setLogger("my.logger");
        assertThat(listener.getLogger().getName()).as("Updated logger name").isEqualTo("my.logger");
    }

}
