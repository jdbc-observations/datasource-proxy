package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.ConnectionInfo;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Log all JDBC API interaction.
 *
 * To log interaction with {@link java.sql.ResultSet}, proxying result set needs to be enabled.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.4
 */
public class TracingMethodListener implements MethodExecutionListener {

    private static final int DEFAULT_SINGLE_PARAM_LENGTH = 15;

    private AtomicLong sequenceNumber = new AtomicLong(1);
    protected int parameterDisplayLength = DEFAULT_SINGLE_PARAM_LENGTH;

    @Override
    public void beforeMethod(MethodExecutionContext executionContext) {
        // no-op
    }

    @Override
    public void afterMethod(MethodExecutionContext executionContext) {

        Method method = executionContext.getMethod();
        Class<?> targetClass = executionContext.getTarget().getClass();

        Throwable thrown = executionContext.getThrown();
        long execTime = executionContext.getElapsedTime();

        ConnectionInfo connectionInfo = executionContext.getConnectionInfo();
        long connectionId = -1;
        if (connectionInfo != null) {
            connectionId = connectionInfo.getConnectionId();
        }

        long seq = this.sequenceNumber.getAndIncrement();
        String args = getArguments(executionContext.getMethodArgs());

        String message = constructMessage(seq, thrown, execTime, connectionId, targetClass, method, args);
        logMessage(message);

    }

    /**
     * Convert method parameters to a string.
     *
     * @param args argument for the invoked method
     * @return message string
     */
    protected String getArguments(Object[] args) {

        if (args == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            boolean lastArg = i == args.length - 1;
            String argAsString = getArgumentAsString(arg);
            argAsString = getDisplayArg(argAsString);
            if (arg instanceof String) {
                sb.append("\"");
                sb.append(argAsString);
                sb.append("\"");
            } else {
                sb.append(argAsString);
            }

            if (!lastArg) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    /**
     * Convert single parameter to String.
     *
     * @param arg string
     * @return string representation
     */
    protected String getArgumentAsString(Object arg) {
        if (arg instanceof String) {
            return (String) arg;
        }
        return arg == null ? "null" : arg.toString();
    }

    /**
     * Construct parameter value to display.
     *
     * Default implementation truncate the parameter string if it is too long.
     *
     * @param argAsString parameter value as string
     * @return parameter value to display
     */
    protected String getDisplayArg(String argAsString) {
        if (argAsString.length() <= this.parameterDisplayLength) {
            return argAsString;
        }
        StringBuilder sb = new StringBuilder(argAsString);
        sb.substring(0, this.parameterDisplayLength - 3);
        sb.append("...");
        return sb.toString();
    }

    /**
     * Construct a message to log.
     *
     * @param seq          sequence number
     * @param thrown       thrown exception
     * @param execTime     time took to perform the method
     * @param connectionId connection id
     * @param targetClass  invoked class
     * @param method       invoked method
     * @param args         method arguments(parameters)
     * @return message to log
     */
    protected String constructMessage(long seq, Throwable thrown, long execTime,
                                      long connectionId, Class<?> targetClass, Method method, String args) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(seq);
        sb.append("]");

        sb.append("[");
        sb.append(thrown == null ? "success" : "fail");
        sb.append("]");

        sb.append("[");
        sb.append(execTime);
        sb.append("ms]");

        sb.append("[conn=");
        sb.append(connectionId);
        sb.append("]");

        if (thrown != null) {
            sb.append("[error=");
            sb.append(thrown.getMessage());
            sb.append("]");
        }

        sb.append(" ");
        sb.append(targetClass.getSimpleName());
        sb.append("#");
        sb.append(method.getName());
        sb.append("(");
        sb.append(args);
        sb.append(")");

        return sb.toString();
    }

    /**
     * log message
     *
     * Default implementation writes out to console.
     *
     * @param message message to log
     */
    protected void logMessage(String message) {
        // TODO: maybe overridable by lambda
        System.out.println(message);
    }

    public void setParameterDisplayLength(int parameterDisplayLength) {
        this.parameterDisplayLength = parameterDisplayLength;
    }

}
