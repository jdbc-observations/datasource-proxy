package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.ExecutionInfo;

/**
 * Generate logging entry.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.3
 */
public interface QueryLogEntryCreator {

    String getLogEntry(ExecutionInfo execInfo, boolean writeDataSourceName, boolean writeConnectionId);

}
