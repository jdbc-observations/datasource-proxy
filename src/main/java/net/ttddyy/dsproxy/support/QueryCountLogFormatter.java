package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.QueryCount;

/**
 * @author Tadaya Tsuyukubo
 */
public interface QueryCountLogFormatter {

    String getLogMessage(String datasourceName, QueryCount queryCount);
}
