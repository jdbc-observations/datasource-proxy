package net.ttddyy.dsproxy.listener.logging;

import java.util.ArrayList;
import java.util.List;
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

    // TODO: add other levels if needed
    private List<String> fineMessages = new ArrayList<String>();


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
        if (level == Level.FINE) {
            this.fineMessages.add(message);
        } else {
            throw new UnsupportedOperationException("log level " + level + " is not supported yet.");
        }
    }

    @Override
    public boolean isLoggable(Level level) {
        return true;  // always record log regardless of level
    }

    public List<String> getFineMessages() {
        return fineMessages;
    }

    public void setFineMessages(List<String> fineMessages) {
        this.fineMessages = fineMessages;
    }
}
