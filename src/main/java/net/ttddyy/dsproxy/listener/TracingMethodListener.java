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

    private static final int DEFAULT_DISPLAY_PARAM_LENGTH = 50;

    /**
     * Functional interface to decide whether to perform tracing.
     *
     * This will be updated to BooleanSupplier once it is updated to java8.
     */
    public interface TracingCondition {
        boolean getAsBoolean();
    }

    /**
     * Functional interface to consume log message.
     *
     * This will be updated to string consumer once it is updated to java8.
     */
    public interface TracingMessageConsumer {
        void accept(String logMessage);
    }

    private AtomicLong sequenceNumber = new AtomicLong(1);

    protected int parameterDisplayLength = DEFAULT_DISPLAY_PARAM_LENGTH;

    protected TracingCondition tracingCondition = new TracingCondition() {
        @Override
        public boolean getAsBoolean() {
            return true;  // enable tracing by default
        }
    };

    protected TracingMessageConsumer tracingMessageConsumer = new TracingMessageConsumer() {
        @Override
        public void accept(String logMessage) {
            System.out.println(logMessage);  // write to console by default
        }
    };

    @Override
    public void beforeMethod(MethodExecutionContext executionContext) {
        // no-op
    }

    @Override
    public void afterMethod(MethodExecutionContext executionContext) {

        if (!this.tracingCondition.getAsBoolean()) {
            return;  // condition was false, skip tracing
        }

        Method method = executionContext.getMethod();
        Class<?> targetClass = executionContext.getTarget().getClass();

        Throwable thrown = executionContext.getThrown();
        long execTime = executionContext.getElapsedTime();

        ConnectionInfo connectionInfo = executionContext.getConnectionInfo();
        String connectionId = "-1";
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

        if (args == null || args.length == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        if (args.length == 1) {
            Object arg = args[0];
            String param = getSingleArgParameterAsString(arg);
            String displayParam = getSingleArgDisplayParameter(param);
            if (arg instanceof String) {
                sb.append("\"");
                sb.append(displayParam);
                sb.append("\"");
            } else {
                sb.append(displayParam);
            }
        } else {
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                boolean lastArg = i == args.length - 1;
                String param = getParameterAsString(arg);
                String displayParam = getDisplayParameter(param);
                if (arg instanceof String) {
                    sb.append("\"");
                    sb.append(displayParam);
                    sb.append("\"");
                } else {
                    sb.append(displayParam);
                }

                if (!lastArg) {
                    sb.append(",");
                }
            }
        }

        return sb.toString();
    }

    /**
     * Convert single parameter to String.
     *
     * This method is called when invoked method takes single argument.
     *
     * @param arg method parameter
     * @return string representation
     */
    protected String getSingleArgParameterAsString(Object arg) {
        if (arg instanceof String) {
            return (String) arg;
        }
        return arg == null ? "null" : arg.toString();
    }

    /**
     * Construct display string for parameter.
     *
     * This method is called when invoked method takes single argument.
     *
     * @param parameter parameter value as string
     * @return parameter value to display
     */
    protected String getSingleArgDisplayParameter(String parameter) {
        // when method takes only one argument, do not truncate the string representation.
        return parameter;
    }

    /**
     * Convert single parameter to String.
     *
     * This method is called when invoked method takes multiple arguments.
     *
     * @param arg method parameter
     * @return string representation
     */
    protected String getParameterAsString(Object arg) {
        if (arg instanceof String) {
            return (String) arg;
        }
        return arg == null ? "null" : arg.toString();
    }

    /**
     * Construct parameter value to display.
     *
     * This method is called when invoked method takes multiple arguments.
     *
     * Default implementation truncate the parameter string if it is too long.
     *
     * @param parameter parameter value as string
     * @return parameter value to display
     */
    protected String getDisplayParameter(String parameter) {
        if (parameter.length() <= this.parameterDisplayLength) {
            return parameter;
        }
        return parameter.substring(0, this.parameterDisplayLength - 3) + "...";
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
                                      String connectionId, Class<?> targetClass, Method method, String args) {
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
     * Default implementation delegates to consumer that writes out to console.
     *
     * @param message message to log
     */
    protected void logMessage(String message) {
        this.tracingMessageConsumer.accept(message);
    }

    public void setParameterDisplayLength(int parameterDisplayLength) {
        this.parameterDisplayLength = parameterDisplayLength;
    }

    public TracingCondition getTracingCondition() {
        return tracingCondition;
    }

    public void setTracingCondition(TracingCondition tracingCondition) {
        this.tracingCondition = tracingCondition;
    }

    public TracingMessageConsumer getTracingMessageConsumer() {
        return tracingMessageConsumer;
    }

    public void setTracingMessageConsumer(TracingMessageConsumer tracingMessageConsumer) {
        this.tracingMessageConsumer = tracingMessageConsumer;
    }

}
