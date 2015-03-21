package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;

import java.util.List;

/**
 * Generate logging entry.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.3
 */
public interface LogEntryCreator {

    String getLogEntry(ExecutionInfo execInfo, List<QueryInfo> queryInfoList, boolean writeDataSourceName);

}
