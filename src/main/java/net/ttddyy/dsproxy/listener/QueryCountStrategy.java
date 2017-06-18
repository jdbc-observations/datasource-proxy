package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.QueryCount;

/**
 * @author Tadaya Tsuyukubo
 * @see DataSourceQueryCountListener
 * @since 1.4.2
 */
public interface QueryCountStrategy {

    QueryCount getOrCreateQueryCount(String dataSourceName);

}
