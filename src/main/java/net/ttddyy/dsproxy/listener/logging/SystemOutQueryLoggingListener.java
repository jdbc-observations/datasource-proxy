package net.ttddyy.dsproxy.listener.logging;

/**
 * Output query logging to System.out.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.3
 */
public class SystemOutQueryLoggingListener extends AbstractQueryLoggingListener {

    public SystemOutQueryLoggingListener() {
        this.loggingCondition = new LoggingCondition() {
            @Override
            public boolean getAsBoolean() {
                return true;  // always perform logging
            }
        };
    }

    @Override
    protected void writeLog(String message) {
        System.out.println(message);
    }
}
