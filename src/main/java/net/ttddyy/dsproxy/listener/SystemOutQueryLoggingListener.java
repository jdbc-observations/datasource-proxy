package net.ttddyy.dsproxy.listener;

/**
 * Output query logging to System.out.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.3
 */
public class SystemOutQueryLoggingListener extends AbstractQueryLoggingListener {
    @Override
    protected void writeLog(String message) {
        System.out.println(message);
    }
}
