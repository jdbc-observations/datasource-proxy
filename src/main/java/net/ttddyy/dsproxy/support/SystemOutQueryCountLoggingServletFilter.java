package net.ttddyy.dsproxy.support;

/**
 * Servlet Filter to log query metrics per http request using {@link java.lang.System#out}.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.3
 */
public class SystemOutQueryCountLoggingServletFilter extends AbstractQueryCountLoggingServletFilter {
    @Override
    protected void initLogLevelFromFilterConfigIfSpecified(String logLevelParam) {
        // NO-OP
    }

    @Override
    protected void writeLog(String message) {
        System.out.println(message);
    }

    @Override
    protected void resetLogger(String loggerName) {
        // NO-OP
    }
}
