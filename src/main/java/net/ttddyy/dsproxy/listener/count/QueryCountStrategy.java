package net.ttddyy.dsproxy.listener.count;

import java.util.Map;

/**
 * @author Tadaya Tsuyukubo
 * @see DataSourceQueryCountListener
 * @since 1.4.2
 */
public interface QueryCountStrategy {

    QueryCount getOrCreateQueryCount(String dataSourceName);

    Map<String, QueryCount> getAll();

    void clearAll();

    void clear(String dataSourceName);
}
