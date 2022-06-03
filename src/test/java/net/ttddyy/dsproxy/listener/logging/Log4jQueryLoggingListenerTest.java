package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.ExecutionInfoBuilder;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.QueryInfoBuilder;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.junit.LoggerContextRule;
import org.apache.logging.log4j.test.appender.ListAppender;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Ivan Jose Sanchez Pagador
 */
public class Log4jQueryLoggingListenerTest {

    Logger logger;
    private ListAppender app;
    private static final String CONFIG = "log4j-test2.xml";

    @ClassRule
    public static LoggerContextRule context = new LoggerContextRule(CONFIG);

    @Before
    public void before() {
        logger = context.getLogger();
        reset();

    }
    
    private void reset() {
        app = context.getListAppender("List").clear();
    }
    

    @Test
    public void defaultLoggerName() {
        Log4jQueryLoggingListener listener = new Log4jQueryLoggingListener();
        assertThat(listener.getLogger().getName())
                .as("Default logger name").isEqualTo("net.ttddyy.dsproxy.listener.logging.Log4jQueryLoggingListener");
    }

    @Test
    public void setLoggerName() {
        Log4jQueryLoggingListener listener = new Log4jQueryLoggingListener();
        listener.setLogger("my.logger");
        assertThat(listener.getLogger().getName()).as("Updated logger name").isEqualTo("my.logger");
    }

    @Test
    public void loggingCondition() {
        Log4jQueryLoggingListener listener = new Log4jQueryLoggingListener();

        List<QueryInfo> queryInfoList = new ArrayList<QueryInfo>();
        ExecutionInfo execInfo = ExecutionInfoBuilder.create().build();
        queryInfoList.add(QueryInfoBuilder.create().query("select * ").build());


        // listener DEBUG, logger TRACE
        Configurator.setLevel(this.logger.getName(), Level.TRACE);
        listener.setLogger(logger);
        listener.setLogLevel(Log4jLogLevel.DEBUG);
        listener.afterQuery(execInfo, queryInfoList);
        List<LogEvent> eventsTrace = app.getEvents();

        assertThat(eventsTrace).hasSize(1);
        assertThat(eventsTrace.get(0).getLevel()).isEqualTo(Level.DEBUG);
        assertThat(eventsTrace.get(0).getLevel()).isNotEqualTo(Level.TRACE);
        assertThat(eventsTrace.get(0).getLevel()).isNotEqualTo(Level.WARN);
        assertThat(eventsTrace.get(0).getLevel()).isNotEqualTo(Level.INFO);
        assertThat(eventsTrace.get(0).getLevel()).isNotEqualTo(Level.ERROR);

        // listener TRACE, logger DEBUG
        listener.setLogLevel(Log4jLogLevel.TRACE);
        Configurator.setLevel(this.logger.getName(), Level.DEBUG);
        reset();
        listener.afterQuery(execInfo, queryInfoList);
        List<LogEvent> eventsDebug = app.getEvents();

        assertThat(eventsDebug).hasSize(0);

        // listener DEBUG, logger DEBUG
        listener.setLogLevel(Log4jLogLevel.DEBUG);
        Configurator.setLevel(this.logger.getName(), Level.DEBUG);
        reset();
        listener.afterQuery(execInfo, queryInfoList);
        List<LogEvent> eventsDebug2 = app.getEvents();

        assertThat(eventsDebug2).hasSize(1);
        assertThat(eventsDebug2.get(0).getLevel()).isEqualTo(Level.DEBUG);
        assertThat(eventsDebug2.get(0).getLevel()).isNotEqualTo(Level.TRACE);
        assertThat(eventsDebug2.get(0).getLevel()).isNotEqualTo(Level.WARN);
        assertThat(eventsDebug2.get(0).getLevel()).isNotEqualTo(Level.INFO);
        assertThat(eventsDebug2.get(0).getLevel()).isNotEqualTo(Level.ERROR);

        // listener INFO, logger DEBUG
        listener.setLogLevel(Log4jLogLevel.INFO);
        Configurator.setLevel(this.logger.getName(), Level.DEBUG);
        reset();
        listener.afterQuery(execInfo, queryInfoList);
        List<LogEvent> eventsInfo = app.getEvents();

        assertThat(eventsInfo).hasSize(1);
        assertThat(eventsInfo.get(0).getLevel()).isEqualTo(Level.INFO);
        assertThat(eventsInfo.get(0).getLevel()).isNotEqualTo(Level.TRACE);
        assertThat(eventsInfo.get(0).getLevel()).isNotEqualTo(Level.WARN);
        assertThat(eventsInfo.get(0).getLevel()).isNotEqualTo(Level.DEBUG);
        assertThat(eventsInfo.get(0).getLevel()).isNotEqualTo(Level.ERROR);

        // listener INFO, logger DEBUG
        listener.setLogLevel(Log4jLogLevel.DEBUG);
        Configurator.setLevel(this.logger.getName(), Level.INFO);
        reset();
        listener.afterQuery(execInfo, queryInfoList);
        assertThat(app.getEvents()).hasSize(0);
    }

}
