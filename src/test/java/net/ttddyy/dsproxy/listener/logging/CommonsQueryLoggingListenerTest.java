package net.ttddyy.dsproxy.listener.logging;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.3.1
 */
public class CommonsQueryLoggingListenerTest {

    @Before
    public void setup() throws Exception {
        // TODO: clean up logger intercept mechanism
        System.setProperty("org.apache.commons.logging.Log", InMemoryCommonsLog.class.getCanonicalName());
    }

    @After
    public void teardown() throws Exception {
        System.setProperty("org.apache.commons.logging.Log", "");
    }

    @Test
    public void defaultLoggerName() {
        CommonsQueryLoggingListener listener = new CommonsQueryLoggingListener();
        String name = ((InMemoryCommonsLog) listener.log).getName();
        assertThat(name).as("Default logger name").isEqualTo("net.ttddyy.dsproxy.listener.logging.CommonsQueryLoggingListener");
    }

    @Test
    public void setLoggerName() {
        CommonsQueryLoggingListener listener = new CommonsQueryLoggingListener();
        listener.setLoggerName("my.logger");
        String name = ((InMemoryCommonsLog) listener.log).getName();
        assertThat(name).as("Updated logger name").isEqualTo("my.logger");
    }

}
