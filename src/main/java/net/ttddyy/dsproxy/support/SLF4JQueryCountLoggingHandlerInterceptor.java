package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.listener.SLF4JLogLevel;
import net.ttddyy.dsproxy.QueryCountHolder;
import net.ttddyy.dsproxy.QueryCount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Collections;

/**
 * @author Tadaya Tsuyukubo
 */
public class SLF4JQueryCountLoggingHandlerInterceptor extends HandlerInterceptorAdapter {

    private Logger logger = LoggerFactory.getLogger(SLF4JQueryCountLoggingHandlerInterceptor.class);

    private boolean clearQueryCounter = true;
    private SLF4JLogLevel logLevel = SLF4JLogLevel.DEBUG;
    private QueryCountLogFormatter logFormatter = new DefaultQueryCountLogFormatter();

    public SLF4JQueryCountLoggingHandlerInterceptor() {
    }

    public SLF4JQueryCountLoggingHandlerInterceptor(SLF4JLogLevel logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        final List<String> dsNames = QueryCountHolder.getDataSourceNamesAsList();
        Collections.sort(dsNames);

        for (String dsName : dsNames) {
            final QueryCount count = QueryCountHolder.get(dsName);
            final String message = logFormatter.getLogMessage(dsName, count);
            SLF4JLogUtils.writeLog(logger, logLevel, message);
        }

        if (clearQueryCounter) {
            QueryCountHolder.clear();
        }

    }

    public void setClearQueryCounter(boolean clearQueryCounter) {
        this.clearQueryCounter = clearQueryCounter;
    }

    public void setLogLevel(SLF4JLogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public void setLogFormatter(QueryCountLogFormatter logFormatter) {
        this.logFormatter = logFormatter;
    }
}
