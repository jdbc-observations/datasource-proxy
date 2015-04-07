package net.ttddyy.dsproxy.support;

/**
 * {@link org.springframework.web.servlet.HandlerInterceptor} to log query metrics to {@link java.lang.System#out}.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.3
 */
public class SystemOutQueryCountLoggingHandlerInterceptor extends AbstractQueryCountLoggingHandlerInterceptor {
    @Override
    protected void writeLog(String logEntry) {
        System.out.println(logEntry);
    }
}
