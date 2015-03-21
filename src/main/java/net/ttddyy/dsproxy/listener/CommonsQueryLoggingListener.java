package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * Record executed query information using Commons-Logging.
 *
 * @author Tadaya Tsuyukubo
 */
public class CommonsQueryLoggingListener implements QueryExecutionListener {

    private boolean writeDataSourceName = true;

    private Log log = LogFactory.getLog(CommonsQueryLoggingListener.class);
    private CommonsLogLevel logLevel = CommonsLogLevel.DEBUG; // default DEBUG

    private LogEntryGenerator logEntryGenerator = new DefaultLogEntryGenerator();

    public void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
    }

    public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        final String entry = getEntry(execInfo, queryInfoList, this.writeDataSourceName);
        writeLog(entry);
    }

    protected String getEntry(ExecutionInfo execInfo, List<QueryInfo> queryInfoList, boolean writeDataSourceName) {
        return this.logEntryGenerator.getLogEntry(execInfo, queryInfoList, writeDataSourceName);
    }

    protected void writeLog(String message) {
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
            case TRACE:
                log.trace(message);
                break;
            case WARN:
                log.warn(message);
                break;
        }
    }

    public void setLogLevel(CommonsLogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public void setWriteDataSourceName(boolean writeDataSourceName) {
        this.writeDataSourceName = writeDataSourceName;
    }

    public void setLogEntryGenerator(LogEntryGenerator logEntryGenerator) {
        this.logEntryGenerator = logEntryGenerator;
    }
}
