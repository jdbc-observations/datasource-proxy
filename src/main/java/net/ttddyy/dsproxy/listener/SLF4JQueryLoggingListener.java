package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Log executed query information using SLF4J.
 *
 * @author Tadaya Tsuyukubo
 */
public class SLF4JQueryLoggingListener implements QueryExecutionListener {

    private Logger logger = LoggerFactory.getLogger(SLF4JQueryLoggingListener.class);
    private SLF4JLogLevel logLevel = SLF4JLogLevel.DEBUG; // default DEBUG
    private LogEntryGenerator logEntryGenerator = new DefaultLogEntryGenerator();
    private boolean writeDataSourceName = true;

    public void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
    }

    public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        final String entry = getEntry(execInfo, queryInfoList, this.writeDataSourceName);
        writeLog(entry);
    }

    protected String getEntry(ExecutionInfo execInfo, List<QueryInfo> queryInfoList, boolean writeDataSourceName) {
        return this.logEntryGenerator.getLogEntry(execInfo, queryInfoList, writeDataSourceName);
    }

    private void writeLog(String message) {
        switch (logLevel) {
            case DEBUG:
                logger.debug(message);
                break;
            case ERROR:
                logger.error(message);
                break;
            case INFO:
                logger.info(message);
                break;
            case TRACE:
                logger.trace(message);
                break;
            case WARN:
                logger.warn(message);
                break;
        }
    }

    public void setLogLevel(SLF4JLogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public void setWriteDataSourceName(boolean writeDataSourceName) {
        this.writeDataSourceName = writeDataSourceName;
    }

    public void setLogEntryGenerator(LogEntryGenerator logEntryGenerator) {
        this.logEntryGenerator = logEntryGenerator;
    }
}
