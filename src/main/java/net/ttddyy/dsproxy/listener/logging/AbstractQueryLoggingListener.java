package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;

import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.3
 */
public abstract class AbstractQueryLoggingListener implements QueryExecutionListener {

    protected QueryLogEntryCreator queryLogEntryCreator = new DefaultQueryLogEntryCreator();
    protected boolean writeDataSourceName = true;

    @Override
    public void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
    }

    @Override
    public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        final String entry = getEntry(execInfo, queryInfoList);
        writeLog(entry);
    }

    protected String getEntry(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        return this.queryLogEntryCreator.getLogEntry(execInfo, queryInfoList, this.writeDataSourceName);
    }

    protected abstract void writeLog(String message);

    /**
     * Specify logger name.
     *
     * @param loggerName logger name
     * @since 1.3.1
     * @deprecated use <code>setLog(String)</code> or <code>setLogger(String)</code>
     */
    @Deprecated
    public void setLoggerName(String loggerName) {
        resetLogger(loggerName);
    }

    /**
     * Callback method to reset the logger object in concrete class when log name is specified.
     *
     * @param loggerName logger name
     * @since 1.3.1
     * @deprecated use <code>setLog(String)</code> or <code>setLogger(String)</code>
     */
    @Deprecated
    protected void resetLogger(String loggerName) {
    }

    public void setQueryLogEntryCreator(QueryLogEntryCreator queryLogEntryCreator) {
        this.queryLogEntryCreator = queryLogEntryCreator;
    }

    /**
     * @return query log entry creator
     * @since 1.4.1
     */
    public QueryLogEntryCreator getQueryLogEntryCreator() {
        return queryLogEntryCreator;
    }

    public void setWriteDataSourceName(boolean writeDataSourceName) {
        this.writeDataSourceName = writeDataSourceName;
    }

}
