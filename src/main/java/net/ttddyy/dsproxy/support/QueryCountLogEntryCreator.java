package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.QueryCount;

/**
 * @author Tadaya Tsuyukubo
 */
public interface QueryCountLogEntryCreator {

    String getLogMessage(String datasourceName, QueryCount queryCount);
    String getLogMessageAsJson(String datasourceName, QueryCount queryCount);
}
