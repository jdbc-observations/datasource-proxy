package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;

import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.3
 */
public abstract class AbstractQueryLoggingListener implements QueryExecutionListener {

    protected LogEntryCreator logEntryCreator = new DefaultLogEntryCreator();
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
        return this.logEntryCreator.getLogEntry(execInfo, queryInfoList, writeDataSourceName);
    }

    protected abstract void writeLog(String message);


    public void setLogEntryCreator(LogEntryCreator logEntryCreator) {
        this.logEntryCreator = logEntryCreator;
    }

    public void setWriteDataSourceName(boolean writeDataSourceName) {
        this.writeDataSourceName = writeDataSourceName;
    }
}
