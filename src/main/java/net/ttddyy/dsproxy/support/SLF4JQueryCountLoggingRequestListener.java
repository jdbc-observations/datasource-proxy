package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequestEvent;

/**
 * @author Tadaya Tsuyukubo
 */
public class SLF4JQueryCountLoggingRequestListener extends AbstractQueryCountLoggingRequestListener {

    private static final String LOG_LEVEL_PARAM = "queryCountSLF4JLogLevel";
    private static final SLF4JLogLevel DEFAULT_LOG_LEVEL = SLF4JLogLevel.DEBUG;

    private Logger logger = LoggerFactory.getLogger(SLF4JQueryCountLoggingRequestListener.class);

    @Override
    protected void writeLog(ServletRequestEvent servletRequestEvent, String logEntry) {
        ServletContext context = servletRequestEvent.getServletContext();
        String logLevelParam = context.getInitParameter(LOG_LEVEL_PARAM);
        SLF4JLogLevel logLevel = SLF4JLogLevel.nullSafeValueOf(logLevelParam);
        if (logLevel == null) {
            logLevel = DEFAULT_LOG_LEVEL;
        }

        SLF4JLogUtils.writeLog(logger, logLevel, logEntry);
    }

    /**
     * Override {@link Logger} instance.
     *
     * @param logger new log instance
     * @since 1.4.1
     */
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

}
