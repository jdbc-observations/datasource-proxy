package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.listener.QueryExecutionContext;
import net.ttddyy.dsproxy.listener.ProxyDataSourceListener;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.3
 */
public abstract class AbstractQueryLoggingListener implements ProxyDataSourceListener {

    protected QueryLogEntryCreator queryLogEntryCreator = new DefaultQueryLogEntryCreator();
    protected boolean writeDataSourceName = true;
    protected boolean writeConnectionId = true;
    protected boolean writeIsolation;
    protected LoggingCondition loggingCondition;
    protected LoggingFilter loggingFilter = LoggingFilter.ALLOW_ALL;

    @Override
    public void beforeQuery(QueryExecutionContext context) {
    }

    @Override
    public void afterQuery(QueryExecutionContext context) {
        if (this.loggingCondition.getAsBoolean() && this.loggingFilter.shouldLog(context)) {
            final String entry = getEntry(context);
            writeLog(entry);
        }
    }

    protected String getEntry(QueryExecutionContext context) {
        return this.queryLogEntryCreator.getLogEntry(context, this.writeDataSourceName, this.writeConnectionId, this.writeIsolation);
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

    /**
     * @since 1.4.2
     */
    public void setWriteConnectionId(boolean writeConnectionId) {
        this.writeConnectionId = writeConnectionId;
    }

    /**
     * @since 1.8
     */
    public void setWriteIsolation(boolean writeIsolation) {
        this.writeIsolation = writeIsolation;
    }

    /**
     * A boolean supplier that determines whether to perform logging logic.
     *
     * @param loggingCondition boolean supplier
     * @since 1.4.3
     */
    public void setLoggingCondition(LoggingCondition loggingCondition) {
        this.loggingCondition = loggingCondition;
    }

    /**
     * Filter that controls which queries are logged.
     *
     * @param loggingFilter the filter to use
     * @since 1.11
     */
    public void setLoggingFilter(LoggingFilter loggingFilter) {
        this.loggingFilter = loggingFilter;
    }

    /**
     * @return current logging filter
     * @since 1.11
     */
    public LoggingFilter getLoggingFilter() {
        return this.loggingFilter;
    }
}
