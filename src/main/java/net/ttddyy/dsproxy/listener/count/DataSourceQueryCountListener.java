package net.ttddyy.dsproxy.listener.count;

import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.QueryType;
import net.ttddyy.dsproxy.listener.ProxyDataSourceListenerAdapter;
import net.ttddyy.dsproxy.listener.QueryExecutionContext;
import net.ttddyy.dsproxy.listener.QueryUtils;

/**
 * Listener to update {@link QueryCount}.
 *
 * The {@link QueryCount} can be retrieved by {@link QueryCountHolder#getOrCreateQueryCount(String)} static method.
 *
 * <p>Default implementation uses {@link ThreadQueryCountStrategy} strategy that uses thread local to keep
 * {@link QueryCount}. {@link QueryCount} can be retrieved by {@link QueryCountHolder#getOrCreateQueryCount(String)}.
 *
 * <p>Alternatively, {@link SingleQueryCountStrategy} strategy may be used. This strategy uses single instance to keep
 * {@link QueryCount}; therefore, {@link QueryCount} holds accumulated total values from any threads until values are cleared.
 *
 * <p>In typical servlet web application, one http request is handled by one thread.
 * Storing database access information into a thread local provides metrics information per http request.
 * You need to clear the ThreadLocal value at the beginning or end of request-response lifecycle in order to have
 * accurate counts during the lifecycle.
 * On the other hand, using single instance to store database access information allows you to retrieve total accumulated
 * numbers since application has started.
 *
 * <p>{@link QueryCount} holds following data:
 * <ul>
 * <li> datasource name
 * <li> number of database call
 * <li> total query execution time
 * <li> number of queries by type
 * </ul>
 *
 * @author Tadaya Tsuyukubo
 * @see QueryCount
 * @see QueryCountHolder
 * @see QueryCountStrategy
 */
public class DataSourceQueryCountListener extends ProxyDataSourceListenerAdapter {

    @Override
    public void afterQuery(QueryExecutionContext executionContext) {
        String dataSourceName = executionContext.getDataSourceName();

        QueryCount count = QueryCountHolder.getOrCreateQueryCount(dataSourceName);

        // increment db call
        count.incrementTotal();
        if (executionContext.isSuccess()) {
            count.incrementSuccess();
        } else {
            count.incrementFailure();
        }

        // increment elapsed time
        long elapsedTime = executionContext.getElapsedTime();
        count.incrementTime(elapsedTime);

        // increment statement type
        count.increment(executionContext.getStatementType());

        // increment query count
        for (QueryInfo queryInfo : executionContext.getQueries()) {
            String query = queryInfo.getQuery();
            QueryType type = QueryUtils.getQueryType(query);
            count.increment(type);
        }

    }

}
