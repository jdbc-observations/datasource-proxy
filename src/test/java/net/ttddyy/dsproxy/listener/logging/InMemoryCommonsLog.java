package net.ttddyy.dsproxy.listener.logging;

import org.apache.commons.logging.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Commons-logging Log implementation for unit test.
 *
 * @author Tadaya Tsuyukubo
 */
public class InMemoryCommonsLog implements Log {

    private Map<CommonsLogLevel, List<String>> messages = new HashMap<CommonsLogLevel, List<String>>();
    private CommonsLogLevel enabledLogLevel = CommonsLogLevel.DEBUG;

    {
        initialize();
    }

    private void initialize() {
        for (CommonsLogLevel level : CommonsLogLevel.values()) {
            this.messages.put(level, new ArrayList<String>());
        }
    }

    public void reset() {
        this.messages.clear();
        initialize();
    }


    public void setEnabledLogLevel(CommonsLogLevel enabledLogLevel) {
        this.enabledLogLevel = enabledLogLevel;
    }

    public boolean isDebugEnabled() {
        return CommonsLogLevel.DEBUG.compareTo(this.enabledLogLevel) >= 0;
    }

    public boolean isErrorEnabled() {
        return CommonsLogLevel.ERROR.compareTo(this.enabledLogLevel) >= 0;
    }

    public boolean isFatalEnabled() {
        return CommonsLogLevel.FATAL.compareTo(this.enabledLogLevel) >= 0;
    }

    public boolean isInfoEnabled() {
        return CommonsLogLevel.INFO.compareTo(this.enabledLogLevel) >= 0;
    }

    public boolean isTraceEnabled() {
        return CommonsLogLevel.TRACE.compareTo(this.enabledLogLevel) >= 0;
    }

    public boolean isWarnEnabled() {
        return CommonsLogLevel.WARN.compareTo(this.enabledLogLevel) >= 0;
    }

    public void trace(Object message) {
        addMessage(CommonsLogLevel.TRACE, message);
    }

    public void trace(Object message, Throwable t) {
        addMessage(CommonsLogLevel.TRACE, message);
    }

    public void debug(Object message) {
        addMessage(CommonsLogLevel.DEBUG, message);
    }

    public void debug(Object message, Throwable t) {
        addMessage(CommonsLogLevel.DEBUG, message);
    }

    public void info(Object message) {
        addMessage(CommonsLogLevel.INFO, message);
    }

    public void info(Object message, Throwable t) {
        addMessage(CommonsLogLevel.INFO, message);
    }

    public void warn(Object message) {
        addMessage(CommonsLogLevel.WARN, message);
    }

    public void warn(Object message, Throwable t) {
        addMessage(CommonsLogLevel.WARN, message);
    }

    public void error(Object message) {
        addMessage(CommonsLogLevel.ERROR, message);
    }

    public void error(Object message, Throwable t) {
        addMessage(CommonsLogLevel.ERROR, message);
    }

    public void fatal(Object message) {
        addMessage(CommonsLogLevel.FATAL, message);
    }

    public void fatal(Object message, Throwable t) {
        addMessage(CommonsLogLevel.FATAL, message);
    }

    private void addMessage(CommonsLogLevel level, Object message) {
        if (!(message instanceof String)) {
            throw new UnsupportedOperationException("Currently only support String message");
        }
        this.messages.get(level).add((String) message);
    }

    public List<String> getDebugMessages() {
        return this.messages.get(CommonsLogLevel.DEBUG);
    }

    public List<String> getErrorMessages() {
        return this.messages.get(CommonsLogLevel.ERROR);
    }

    public List<String> getFatalMessages() {
        return this.messages.get(CommonsLogLevel.FATAL);
    }

    public List<String> getInfoMessages() {
        return this.messages.get(CommonsLogLevel.INFO);
    }

    public List<String> getTraceMessages() {
        return this.messages.get(CommonsLogLevel.TRACE);
    }

    public List<String> getWarnMessages() {
        return this.messages.get(CommonsLogLevel.WARN);
    }

    public List<String> getMessages(CommonsLogLevel level) {
        return this.messages.get(level);
    }

}
