package net.ttddyy.dsproxy.support;

import javax.servlet.ServletRequestEvent;

/**
 * {@link javax.servlet.ServletRequestListener} to log query metrics per http request using {@link java.lang.System#out}.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.1
 */
public class SystemOutQueryCountLoggingRequestListener extends AbstractQueryCountLoggingRequestListener {

    @Override
    protected void writeLog(ServletRequestEvent servletRequestEvent, String logEntry) {
        System.out.println(logEntry);
    }

}
