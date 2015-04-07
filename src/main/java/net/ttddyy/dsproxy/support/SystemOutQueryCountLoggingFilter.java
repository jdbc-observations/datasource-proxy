package net.ttddyy.dsproxy.support;

/**
 * Servlet Filter to log query metrics per http request using {@link java.lang.System#out}.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.3
 */
public class SystemOutQueryCountLoggingFilter extends AbstractQueryCountLoggingFilter {
    @Override
    protected void initLogLevelFromFilterConfigIfSpecified(String logLevelParam) {
        // NO-OP
    }

    @Override
    protected void writeLog(String message) {
        System.out.println(message);
    }
}
