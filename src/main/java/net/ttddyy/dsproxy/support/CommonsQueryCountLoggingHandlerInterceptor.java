package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.QueryCount;
import net.ttddyy.dsproxy.QueryCountHolder;
import net.ttddyy.dsproxy.listener.CommonsLogLevel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

/**
 * Spring {@link org.springframework.web.servlet.HandlerInterceptor} to log the query metrics during a http request
 * lifecycle using Apache Commons Logging.

 * @author Tadaya Tsuyukubo
 *
 * @see CommonsQueryCountLoggingFilter
 * @see CommonsQueryCountLoggingRequestListener
 */
public class CommonsQueryCountLoggingHandlerInterceptor extends HandlerInterceptorAdapter {

    private Log log = LogFactory.getLog(CommonsQueryCountLoggingHandlerInterceptor.class);

    private boolean clearQueryCounter = true;
    private CommonsLogLevel logLevel = CommonsLogLevel.DEBUG;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        final List<String> dsNames = QueryCountHolder.getDataSourceNamesAsList();
        Collections.sort(dsNames);

        for (String dsName : dsNames) {
            final QueryCount counter = QueryCountHolder.get(dsName);
            final String message = CommonsLogUtils.getCountLogMessage(counter, dsName);
            writeLog(message);
        }

        if (clearQueryCounter) {
            QueryCountHolder.clear();
        }

    }

    private void writeLog(String message) {
        switch (logLevel) {
            case DEBUG:
                log.debug(message);
                break;
            case ERROR:
                log.error(message);
                break;
            case FATAL:
                log.fatal(message);
                break;
            case INFO:
                log.info(message);
                break;
            case TRAC:
                log.trace(message);
                break;
            case WARN:
                log.warn(message);
                break;
        }
    }


    public void setClearQueryCounter(boolean clearQueryCounter) {
        this.clearQueryCounter = clearQueryCounter;
    }

    public void setLogLevel(CommonsLogLevel logLevel) {
        this.logLevel = logLevel;
    }
}
