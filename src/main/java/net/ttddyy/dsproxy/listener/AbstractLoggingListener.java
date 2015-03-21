package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;

import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.3
 */
public abstract class AbstractLoggingListener implements QueryExecutionListener {

    protected LogEntryGenerator logEntryGenerator = new DefaultLogEntryGenerator();
    protected boolean writeDataSourceName = true;

    @Override
    public void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
    }

    @Override
    public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        final String entry = getEntry(execInfo, queryInfoList, this.writeDataSourceName);
        writeLog(entry);
    }

    protected String getEntry(ExecutionInfo execInfo, List<QueryInfo> queryInfoList, boolean writeDataSourceName) {
        return this.logEntryGenerator.getLogEntry(execInfo, queryInfoList, writeDataSourceName);
    }

    protected abstract void writeLog(String message);


    public void setLogEntryGenerator(LogEntryGenerator logEntryGenerator) {
        this.logEntryGenerator = logEntryGenerator;
    }

    public void setWriteDataSourceName(boolean writeDataSourceName) {
        this.writeDataSourceName = writeDataSourceName;
    }
}
