package net.ttddyy.dsproxy.listener.count;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Uses {@link ThreadLocal} to hold {@link QueryCount}.
 *
 * This strategy holds {@link QueryCount} per thread per datasource name.
 * This is suitable to acquire database access stats for web-application that uses per thread
 * request-response lifecycle.
 * At the end or beginning of request-response lifecycle, {@link ThreadLocal} needs to be
 * cleared in order to get accurate counts.
 *
 * @author Tadaya Tsuyukubo
 * @see QueryCountHolder
 * @since 1.4.2
 */
public class ThreadQueryCountStrategy implements QueryCountStrategy {

    private ThreadLocal<ConcurrentMap<String, QueryCount>> queryCountMapHolder = ThreadLocal.withInitial(ConcurrentHashMap::new);

    @Override
    public QueryCount getOrCreateQueryCount(String dataSourceName) {
        ConcurrentMap<String, QueryCount> queryCountMap = this.queryCountMapHolder.get();
        QueryCount queryCount = queryCountMap.get(dataSourceName);
        if (queryCount == null) {
            queryCountMap.putIfAbsent(dataSourceName, new QueryCount());
            return queryCountMap.get(dataSourceName);
        }
        return queryCount;
    }

    @Override
    public Map<String, QueryCount> getAll() {
        return this.queryCountMapHolder.get();
    }

    @Override
    public void clearAll() {
        this.queryCountMapHolder.get().clear();
    }

    @Override
    public void clear(String dataSourceName) {
        this.queryCountMapHolder.get().remove(dataSourceName);
    }
}
