package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.QueryCount;
import net.ttddyy.dsproxy.QueryCountHolder;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Uses a single instance to hold {@link net.ttddyy.dsproxy.QueryCount} objects.
 *
 * <p>The {@link QueryCount} objects hold accumulated totals for database access across all threads.
 *
 * <p>When {@link #populateQueryCountHolder} is set to {@code true} (the default), the holder also populates
 * {@link QueryCountHolder}.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.2
 */
public class SingleQueryCountHolder implements QueryCountStrategy {

    private ConcurrentMap<String, QueryCount> queryCountMap = new ConcurrentHashMap<String, QueryCount>();
    private boolean populateQueryCountHolder = true;

    @Override
    public QueryCount getOrCreateQueryCount(String dataSourceName) {
        QueryCount queryCount = queryCountMap.get(dataSourceName);
        if (queryCount == null) {
            queryCountMap.putIfAbsent(dataSourceName, new QueryCount());
            queryCount = queryCountMap.get(dataSourceName);
        }
        if (this.populateQueryCountHolder) {
            QueryCountHolder.put(dataSourceName, queryCount);
        }
        return queryCount;
    }

    public ConcurrentMap<String, QueryCount> getQueryCountMap() {
        return queryCountMap;
    }

    public void setQueryCountMap(ConcurrentMap<String, QueryCount> queryCountMap) {
        this.queryCountMap = queryCountMap;
    }

    public boolean isPopulateQueryCountHolder() {
        return populateQueryCountHolder;
    }

    public void setPopulateQueryCountHolder(boolean populateQueryCountHolder) {
        this.populateQueryCountHolder = populateQueryCountHolder;
    }

    public void clear() {
        this.queryCountMap.clear();
    }

}
