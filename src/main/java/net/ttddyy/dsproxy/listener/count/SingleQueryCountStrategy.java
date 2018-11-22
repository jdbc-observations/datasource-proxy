package net.ttddyy.dsproxy.listener.count;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Use a single map to hold {@link QueryCount} by datasource name.
 *
 * Compare to {@link ThreadQueryCountStrategy} which uses {@link ThreadLocal} to hold query counts,
 * this strategy holds all query stats from any threads by datasource.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.2
 */
public class SingleQueryCountStrategy implements QueryCountStrategy {

    private ConcurrentMap<String, QueryCount> queryCountMap = new ConcurrentHashMap<>();

    @Override
    public QueryCount getOrCreateQueryCount(String dataSourceName) {
        QueryCount queryCount = this.queryCountMap.get(dataSourceName);
        if (queryCount == null) {
            queryCount = new QueryCount();
            this.queryCountMap.put(dataSourceName, queryCount);
        }
        return queryCount;
    }

    @Override
    public Map<String, QueryCount> getAll() {
        return this.queryCountMap;
    }

    @Override
    public void clearAll() {
        this.queryCountMap.clear();
    }

    @Override
    public void clear(String dataSourceName) {
        this.queryCountMap.remove(dataSourceName);
    }


}
