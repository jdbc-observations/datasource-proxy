package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.listener.QueryExecutionContext;

/**
 * Generate logging entry.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.3
 */
public interface QueryLogEntryCreator {

    String getLogEntry(QueryExecutionContext context, boolean writeDataSourceName, boolean writeConnectionId, boolean writeIsolation);

}
