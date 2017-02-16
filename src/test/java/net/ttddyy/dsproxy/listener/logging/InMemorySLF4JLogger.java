package net.ttddyy.dsproxy.listener.logging;

import org.slf4j.helpers.MarkerIgnoringBase;

import java.util.ArrayList;
import java.util.List;

/**
 * SLF4J Logger for test use.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.1
 */
public class InMemorySLF4JLogger extends MarkerIgnoringBase {

    private List<String> traceMessages = new ArrayList<String>();
    private List<String> debugMessages = new ArrayList<String>();
    private List<String> infoMessages = new ArrayList<String>();
    private List<String> warnMessages = new ArrayList<String>();
    private List<String> errorMessages = new ArrayList<String>();

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public void trace(String msg) {
        this.traceMessages.add(msg);
    }

    @Override
    public void trace(String format, Object arg) {
        this.traceMessages.add(String.format(format, arg));
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        this.traceMessages.add(String.format(format, arg1, arg2));
    }

    @Override
    public void trace(String format, Object... arguments) {
        this.traceMessages.add(String.format(format, arguments));
    }

    @Override
    public void trace(String msg, Throwable t) {
        this.traceMessages.add(msg);
    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public void debug(String msg) {
        this.debugMessages.add(msg);
    }

    @Override
    public void debug(String format, Object arg) {
        this.debugMessages.add(String.format(format, arg));
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        this.debugMessages.add(String.format(format, arg1, arg2));
    }

    @Override
    public void debug(String format, Object... arguments) {
        this.debugMessages.add(String.format(format, arguments));
    }

    @Override
    public void debug(String msg, Throwable t) {
        this.debugMessages.add(String.format(msg));
    }

    @Override
    public boolean isInfoEnabled() {
        return false;
    }

    @Override
    public void info(String msg) {
        this.infoMessages.add(msg);
    }

    @Override
    public void info(String format, Object arg) {
        this.infoMessages.add(String.format(format, arg));
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        this.infoMessages.add(String.format(format, arg1, arg2));
    }

    @Override
    public void info(String format, Object... arguments) {
        this.infoMessages.add(String.format(format, arguments));
    }

    @Override
    public void info(String msg, Throwable t) {
        this.infoMessages.add(msg);
    }

    @Override
    public boolean isWarnEnabled() {
        return false;
    }

    @Override
    public void warn(String msg) {
        this.warnMessages.add(msg);
    }

    @Override
    public void warn(String format, Object arg) {
        this.warnMessages.add(String.format(format, arg));
    }

    @Override
    public void warn(String format, Object... arguments) {
        this.warnMessages.add(String.format(format, arguments));
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        this.warnMessages.add(String.format(format, arg1, arg2));
    }

    @Override
    public void warn(String msg, Throwable t) {
        this.warnMessages.add(msg);
    }

    @Override
    public boolean isErrorEnabled() {
        return false;
    }

    @Override
    public void error(String msg) {
        this.errorMessages.add(msg);
    }

    @Override
    public void error(String format, Object arg) {
        this.errorMessages.add(String.format(format, arg));
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        this.errorMessages.add(String.format(format, arg1, arg2));

    }

    @Override
    public void error(String format, Object... arguments) {
        this.errorMessages.add(String.format(format, arguments));
    }

    @Override
    public void error(String msg, Throwable t) {
        this.errorMessages.add(msg);
    }

    public List<String> getTraceMessages() {
        return traceMessages;
    }

    public void setTraceMessages(List<String> traceMessages) {
        this.traceMessages = traceMessages;
    }

    public List<String> getDebugMessages() {
        return debugMessages;
    }

    public void setDebugMessages(List<String> debugMessages) {
        this.debugMessages = debugMessages;
    }

    public List<String> getInfoMessages() {
        return infoMessages;
    }

    public void setInfoMessages(List<String> infoMessages) {
        this.infoMessages = infoMessages;
    }

    public List<String> getWarnMessages() {
        return warnMessages;
    }

    public void setWarnMessages(List<String> warnMessages) {
        this.warnMessages = warnMessages;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }
}
