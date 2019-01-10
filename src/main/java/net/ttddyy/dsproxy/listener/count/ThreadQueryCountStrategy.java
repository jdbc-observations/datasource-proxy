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

    // ThreadLocal.withInitial(ConcurrentHashMap::new) is from java8
    private ThreadLocal<ConcurrentMap<String, QueryCount>> queryCountMapHolder = new ThreadLocal<ConcurrentMap<String, QueryCount>>() {
        @Override
        protected ConcurrentMap<String, QueryCount> initialValue() {
            return new ConcurrentHashMap<>();
        }
    };

    @Override
    public QueryCount getOrCreateQueryCount(String dataSourceName) {
        QueryCount queryCount = this.queryCountMapHolder.get().get(dataSourceName);
        if (queryCount == null) {
            queryCount = new QueryCount();
            this.queryCountMapHolder.get().put(dataSourceName, queryCount);
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
