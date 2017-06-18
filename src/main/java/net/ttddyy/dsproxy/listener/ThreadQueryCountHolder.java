package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.QueryCount;
import net.ttddyy.dsproxy.QueryCountHolder;

/**
 * Uses {@link QueryCountHolder} which uses thread local to hold {@link QueryCount}.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.2
 */
public class ThreadQueryCountHolder implements QueryCountStrategy {

    @Override
    public QueryCount getOrCreateQueryCount(String dataSourceName) {
        QueryCount queryCount = QueryCountHolder.get(dataSourceName);
        if (queryCount == null) {
            queryCount = new QueryCount();
            QueryCountHolder.put(dataSourceName, queryCount);
        }
        return queryCount;
    }

}
