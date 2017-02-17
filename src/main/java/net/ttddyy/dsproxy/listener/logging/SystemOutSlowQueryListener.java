package net.ttddyy.dsproxy.listener.logging;

/**
 * Log slow query to System.out.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.1
 */
public class SystemOutSlowQueryListener extends AbstractSlowQueryLoggingListener {

    @Override
    protected void writeLog(String message) {
        System.out.println(message);
    }

}
