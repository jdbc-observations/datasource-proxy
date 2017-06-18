package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.QueryCount;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Use single instance to hold  {@link net.ttddyy.dsproxy.QueryCount}.
 *
 * The {@link QueryCount} holds total accumulated values from all threads where database access has performed.
 *
 * In this implementation, {@link net.ttddyy.dsproxy.QueryCountHolder} will NOT be used.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.2
 */
public class SingleQueryCountHolder implements QueryCountStrategy {

    private ConcurrentMap<String, QueryCount> queryCountMap = new ConcurrentHashMap<String, QueryCount>();

    @Override
    public QueryCount getOrCreateQueryCount(String dataSourceName) {
        QueryCount queryCount = queryCountMap.get(dataSourceName);
        if (queryCount != null) {
            return queryCount;
        }
        queryCountMap.putIfAbsent(dataSourceName, new QueryCount());
        return queryCountMap.get(dataSourceName);
    }

    public ConcurrentMap<String, QueryCount> getQueryCountMap() {
        return queryCountMap;
    }

    public void setQueryCountMap(ConcurrentMap<String, QueryCount> queryCountMap) {
        this.queryCountMap = queryCountMap;
    }


    public void clear() {
        this.queryCountMap.clear();
    }

}
