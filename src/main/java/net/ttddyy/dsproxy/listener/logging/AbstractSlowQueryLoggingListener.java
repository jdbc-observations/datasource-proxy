package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.listener.SlowQueryListener;

/**
 * Abstract class to log slow query.
 *
 * This class delegates actual log writing to subclasses.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.1
 */
public abstract class AbstractSlowQueryLoggingListener extends SlowQueryListener {

    protected boolean writeDataSourceName = true;
    protected boolean writeConnectionId = true;
    protected QueryLogEntryCreator queryLogEntryCreator = new DefaultQueryLogEntryCreator();
    protected String prefix;

    @Override
    protected void onSlowQuery(ExecutionInfo execInfo, long startTimeInMills) {
        String entry = this.queryLogEntryCreator.getLogEntry(execInfo, this.writeDataSourceName, this.writeConnectionId);
        if (this.prefix != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(this.prefix);
            sb.append(entry);
            entry = sb.toString();
        }
        writeLog(entry);
    }

    protected abstract void writeLog(String message);


    public void setQueryLogEntryCreator(QueryLogEntryCreator queryLogEntryCreator) {
        this.queryLogEntryCreator = queryLogEntryCreator;
    }

    public QueryLogEntryCreator getQueryLogEntryCreator() {
        return queryLogEntryCreator;
    }

    /**
     * @since 1.4.2
     */
    public void setWriteDataSourceName(boolean writeDataSourceName) {
        this.writeDataSourceName = writeDataSourceName;
    }

    /**
     * @since 1.4.2
     */
    public void setWriteConnectionId(boolean writeConnectionId) {
        this.writeConnectionId = writeConnectionId;
    }
}
