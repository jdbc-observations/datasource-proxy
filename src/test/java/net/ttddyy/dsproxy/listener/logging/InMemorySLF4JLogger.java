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

    private SLF4JLogLevel enabledLogLevel = SLF4JLogLevel.DEBUG;

    @Override
    public boolean isTraceEnabled() {
        return SLF4JLogLevel.TRACE.compareTo(this.enabledLogLevel) >= 0;
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
        return SLF4JLogLevel.DEBUG.compareTo(this.enabledLogLevel) >= 0;
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
        return SLF4JLogLevel.INFO.compareTo(this.enabledLogLevel) >= 0;
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
        return SLF4JLogLevel.WARN.compareTo(this.enabledLogLevel) >= 0;
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
        return SLF4JLogLevel.ERROR.compareTo(this.enabledLogLevel) >= 0;
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

    public void setEnabledLogLevel(SLF4JLogLevel enabledLogLevel) {
        this.enabledLogLevel = enabledLogLevel;
    }

    public void reset() {
        this.traceMessages.clear();
        this.debugMessages.clear();
        this.infoMessages.clear();
        this.warnMessages.clear();
        this.errorMessages.clear();
    }


}
