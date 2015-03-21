package net.ttddyy.dsproxy.listener;

/**
 * Output query logging to System.out.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.3
 */
public class SystemOutLoggingListener extends AbstractLoggingListener {
    @Override
    protected void writeLog(String message) {
        System.out.println(message);
    }
}
