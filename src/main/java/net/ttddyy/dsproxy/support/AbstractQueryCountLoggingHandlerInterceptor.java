package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.QueryCount;
import net.ttddyy.dsproxy.QueryCountHolder;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.3
 */
public abstract class AbstractQueryCountLoggingHandlerInterceptor extends HandlerInterceptorAdapter {

    private boolean clearQueryCounter = true;
    private boolean writeAsJson = false;
    private QueryCountLogEntryCreator logFormatter = new DefaultQueryCountLogEntryCreator();

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        List<String> dsNames = QueryCountHolder.getDataSourceNamesAsList();
        Collections.sort(dsNames);

        for (String dsName : dsNames) {
            QueryCount count = QueryCountHolder.get(dsName);
            String logEntry;
            if (this.writeAsJson) {
                logEntry = logFormatter.getLogMessageAsJson(dsName, count);
            } else {
                logEntry = logFormatter.getLogMessage(dsName, count);
            }
            writeLog(logEntry);
        }

        if (clearQueryCounter) {
            QueryCountHolder.clear();
        }

    }

    protected abstract void writeLog(String logEntry);

    /**
     * Specify logger name.
     *
     * @param loggerName logger name
     * @since 1.4.1
     */
    public void setLoggerName(String loggerName) {
        resetLogger(loggerName);
    }

    /**
     * Callback method to reset the logger object in concrete class when log name is specified.
     *
     * @param loggerName logger name
     * @since 1.4.1
     */
    protected void resetLogger(String loggerName) {
    }

    public void setClearQueryCounter(boolean clearQueryCounter) {
        this.clearQueryCounter = clearQueryCounter;
    }

    public void setLogFormatter(QueryCountLogEntryCreator logFormatter) {
        this.logFormatter = logFormatter;
    }

    public void setWriteAsJson(boolean writeAsJson) {
        this.writeAsJson = writeAsJson;
    }
}
