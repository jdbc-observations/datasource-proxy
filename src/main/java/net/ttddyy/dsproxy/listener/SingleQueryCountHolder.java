package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.QueryCount;
import net.ttddyy.dsproxy.QueryCountHolder;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Use single instance to hold  {@link net.ttddyy.dsproxy.QueryCount}.
 *
 * The {@link QueryCount} holds total accumulated values from all threads where database access has performed.
 *
 * When {@link #populateQueryCountHolder} is set to {@code true}(default), it populates {@link QueryCountHolder}.
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
