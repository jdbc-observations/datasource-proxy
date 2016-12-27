package net.ttddyy.dsproxy.support;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequestEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4.1
 */
public class JULQueryCountLoggingRequestListener extends AbstractQueryCountLoggingRequestListener {

    private static final String LOG_LEVEL_PARAM = "queryCountJULLogLevel";
    private static final Level DEFAULT_LOG_LEVEL = Level.FINE;

    protected Logger logger = Logger.getLogger(JULQueryCountLoggingHandlerInterceptor.class.getName());

    @Override
    protected void writeLog(ServletRequestEvent servletRequestEvent, String logEntry) {
        ServletContext context = servletRequestEvent.getServletContext();
        String logLevelParam = context.getInitParameter(LOG_LEVEL_PARAM);
        Level logLevel = Level.parse(logLevelParam);
        if (logLevel == null) {
            logLevel = DEFAULT_LOG_LEVEL;
        }

        this.logger.log(logLevel, logEntry);
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
