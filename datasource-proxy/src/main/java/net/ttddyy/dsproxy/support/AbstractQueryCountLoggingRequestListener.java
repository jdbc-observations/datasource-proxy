package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.QueryCount;
import net.ttddyy.dsproxy.QueryCountHolder;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import java.util.Collections;
import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.3
 */
public abstract class AbstractQueryCountLoggingRequestListener implements ServletRequestListener {
    private QueryCountLogEntryCreator logFormatter = new DefaultQueryCountLogEntryCreator();
    private boolean writeAsJson = false;

    @Override
    public void requestInitialized(ServletRequestEvent servletRequestEvent) {
        // No-op
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {

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
            writeLog(sre, logEntry);
        }

        QueryCountHolder.clear();
    }

    protected abstract void writeLog(ServletRequestEvent servletRequestEvent, String logEntry);

    public void setLogFormatter(QueryCountLogEntryCreator logFormatter) {
        this.logFormatter = logFormatter;
    }

    public void setWriteAsJson(boolean writeAsJson) {
        this.writeAsJson = writeAsJson;
    }

}
