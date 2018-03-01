package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.ProxyDataSourceListener;

import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.3
 */
public abstract class AbstractQueryLoggingListener implements ProxyDataSourceListener {

    protected QueryLogEntryCreator queryLogEntryCreator = new DefaultQueryLogEntryCreator();
    protected boolean writeConnectionId = true;
    protected BooleanSupplier loggingCondition;

    @Override
    public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        // only perform logging logic when the condition returns true
        if (this.loggingCondition.getAsBoolean()) {
            final String entry = getEntry(execInfo, queryInfoList);
            writeLog(entry);
        }
    }

    protected String getEntry(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        boolean writeDataSourceName = execInfo.getDataSourceName() != null && !execInfo.getDataSourceName().trim().equals("");
        return this.queryLogEntryCreator.getLogEntry(execInfo, queryInfoList, writeDataSourceName, this.writeConnectionId);
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

    /**
     * @since 1.4.2
     */
    public void setWriteConnectionId(boolean writeConnectionId) {
        this.writeConnectionId = writeConnectionId;
    }

    /**
     * A boolean supplier that determines whether to perform logging logic.
     *
     * @param loggingCondition boolean supplier
     * @since 1.4.3
     */
    public void setLoggingCondition(BooleanSupplier loggingCondition) {
        this.loggingCondition = loggingCondition;
    }
}
