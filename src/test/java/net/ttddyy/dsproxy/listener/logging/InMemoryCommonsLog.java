package net.ttddyy.dsproxy.listener.logging;

import org.apache.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Commons-logging Log implementation for unit test.
 *
 * @author Tadaya Tsuyukubo
 */
public class InMemoryCommonsLog implements Log {

    private String name;

    private List<Object> debugMessages = new ArrayList<Object>();
    private List<Object> errorMessages = new ArrayList<Object>();
    private List<Object> fatalMessages = new ArrayList<Object>();
    private List<Object> infoMessages = new ArrayList<Object>();
    private List<Object> traceMessages = new ArrayList<Object>();
    private List<Object> warnMessages = new ArrayList<Object>();

    public InMemoryCommonsLog() {
    }

    public InMemoryCommonsLog(String name) {
        this.name = name;
    }

    public void clear() {
        debugMessages.clear();
        errorMessages.clear();
        fatalMessages.clear();
        infoMessages.clear();
        traceMessages.clear();
        warnMessages.clear();
    }

    public boolean isDebugEnabled() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isErrorEnabled() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isFatalEnabled() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isInfoEnabled() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isTraceEnabled() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isWarnEnabled() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void trace(Object message) {
        this.traceMessages.add(message);
    }

    public void trace(Object message, Throwable t) {
        this.traceMessages.add(message);
    }

    public void debug(Object message) {
        this.debugMessages.add(message);
    }

    public void debug(Object message, Throwable t) {
        this.debugMessages.add(message);
    }

    public void info(Object message) {
        this.infoMessages.add(message);
    }

    public void info(Object message, Throwable t) {
        this.infoMessages.add(message);
    }

    public void warn(Object message) {
        this.warnMessages.add(message);
    }

    public void warn(Object message, Throwable t) {
        this.warnMessages.add(message);
    }

    public void error(Object message) {
        this.errorMessages.add(message);
    }

    public void error(Object message, Throwable t) {
        this.errorMessages.add(message);
    }

    public void fatal(Object message) {
        this.fatalMessages.add(message);
    }

    public void fatal(Object message, Throwable t) {
        this.fatalMessages.add(message);
    }


    public String getName() {
        return this.name;
    }

    public List<Object> getDebugMessages() {
        return this.debugMessages;
    }

    public List<Object> getErrorMessages() {
        return this.errorMessages;
    }

    public List<Object> getFatalMessages() {
        return this.fatalMessages;
    }

    public List<Object> getInfoMessages() {
        return this.infoMessages;
    }

    public List<Object> getTraceMessages() {
        return this.traceMessages;
    }

    public List<Object> getWarnMessages() {
        return this.warnMessages;
    }
}
