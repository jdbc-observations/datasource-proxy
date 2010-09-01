package com.tpon.dsproxy;

import org.apache.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Commons-logging Log implementation for unit test.
 *
 * @author Tadaya Tsuyukubo
 */
public class InMemoryLog implements Log {

    private String name;

    private List<Object> debugMessages = new ArrayList<Object>();
    private List<Object> errorMessages = new ArrayList<Object>();
    private List<Object> fatalMessages = new ArrayList<Object>();
    private List<Object> infoMessages = new ArrayList<Object>();
    private List<Object> traceMessages = new ArrayList<Object>();
    private List<Object> warnMessages = new ArrayList<Object>();

    public InMemoryLog() {
    }

    public InMemoryLog(String name) {
        this.name = name;
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
        traceMessages.add(message);
    }

    public void trace(Object message, Throwable t) {
        traceMessages.add(message);
    }

    public void debug(Object message) {
        debugMessages.add(message);
    }

    public void debug(Object message, Throwable t) {
        debugMessages.add(message);
    }

    public void info(Object message) {
        infoMessages.add(message);
    }

    public void info(Object message, Throwable t) {
        infoMessages.add(message);
    }

    public void warn(Object message) {
        warnMessages.add(message);
    }

    public void warn(Object message, Throwable t) {
        warnMessages.add(message);
    }

    public void error(Object message) {
        errorMessages.add(message);
    }

    public void error(Object message, Throwable t) {
        errorMessages.add(message);
    }

    public void fatal(Object message) {
        fatalMessages.add(message);
    }

    public void fatal(Object message, Throwable t) {
        fatalMessages.add(message);
    }


    public String getName() {
        return name;
    }

    public List<Object> getDebugMessages() {
        return debugMessages;
    }

    public List<Object> getErrorMessages() {
        return errorMessages;
    }

    public List<Object> getFatalMessages() {
        return fatalMessages;
    }

    public List<Object> getInfoMessages() {
        return infoMessages;
    }

    public List<Object> getTraceMessages() {
        return traceMessages;
    }

    public List<Object> getWarnMessages() {
        return warnMessages;
    }
}
