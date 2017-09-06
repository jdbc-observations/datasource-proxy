package net.ttddyy.dsproxy.listener.logging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * JUL Logger for test use.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.1
 */
public class InMemoryJULLogger extends Logger {

    private Level loggerLevel = Level.FINE;
    private Map<Level, List<String>> messages = new HashMap<Level, List<String>>();

    {
        this.messages.put(Level.SEVERE, new ArrayList<String>());
        this.messages.put(Level.WARNING, new ArrayList<String>());
        this.messages.put(Level.INFO, new ArrayList<String>());
        this.messages.put(Level.CONFIG, new ArrayList<String>());
        this.messages.put(Level.FINE, new ArrayList<String>());
        this.messages.put(Level.FINER, new ArrayList<String>());
        this.messages.put(Level.FINEST, new ArrayList<String>());
    }


    public InMemoryJULLogger() {
        super("in-memory-jul-logger", null);
    }

    private InMemoryJULLogger(String name, String resourceBundleName) {
        super(name, resourceBundleName);
    }

    @Override
    public void log(LogRecord record) {
        String message = record.getMessage();
        Level level = record.getLevel();
        this.messages.get(level).add(message);
    }

    @Override
    public boolean isLoggable(Level level) {
        if (level.intValue() < loggerLevel.intValue() || loggerLevel.intValue() == Level.OFF.intValue()) {
            return false;
        }
        return true;
    }

    public List<String> getSevereMessages() {
        return this.messages.get(Level.SEVERE);
    }

    public List<String> getWarningMessages() {
        return this.messages.get(Level.WARNING);
    }

    public List<String> getInfoMessages() {
        return this.messages.get(Level.INFO);
    }

    public List<String> getConfigMessages() {
        return this.messages.get(Level.CONFIG);
    }

    public List<String> getFineMessages() {
        return this.messages.get(Level.FINE);
    }

    public List<String> getFinerMessages() {
        return this.messages.get(Level.FINER);
    }

    public List<String> getFinestMessages() {
        return this.messages.get(Level.FINEST);
    }

    public void setLoggerLevel(Level loggerLevel) {
        this.loggerLevel = loggerLevel;
    }

    public void reset() {
        for (List<String> messages : this.messages.values()) {
            messages.clear();
        }
    }
}
