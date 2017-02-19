package net.ttddyy.dsproxy.listener.logging;

import java.util.concurrent.TimeUnit;

/**
 * Log slow query to System.out.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.1
 */
public class SystemOutSlowQueryListener extends AbstractSlowQueryLoggingListener {

    public SystemOutSlowQueryListener() {
    }

    public SystemOutSlowQueryListener(long threshold, TimeUnit thresholdTimeUnit) {
        this.threshold = threshold;
        this.thresholdTimeUnit = thresholdTimeUnit;
    }

    @Override
    protected void writeLog(String message) {
        System.out.println(message);
    }

}
