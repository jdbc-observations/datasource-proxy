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
    private QueryCountLogFormatter logFormatter = new DefaultQueryCountLogFormatter();

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


    public void setClearQueryCounter(boolean clearQueryCounter) {
        this.clearQueryCounter = clearQueryCounter;
    }

    public void setLogFormatter(QueryCountLogFormatter logFormatter) {
        this.logFormatter = logFormatter;
    }

    public void setWriteAsJson(boolean writeAsJson) {
        this.writeAsJson = writeAsJson;
    }
}
