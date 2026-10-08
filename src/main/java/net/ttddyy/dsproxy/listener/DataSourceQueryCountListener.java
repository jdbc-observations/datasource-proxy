package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryCount;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.QueryType;

import java.util.List;

/**
 * Tracks database access statistics.
 *
 * <p>The default implementation uses the {@link ThreadQueryCountHolder} strategy, which stores
 * {@link QueryCount} objects in a thread-local variable. A {@link QueryCount} can be retrieved with
 * {@link net.ttddyy.dsproxy.QueryCountHolder#get(String)}.
 *
 * <p>Alternatively, the {@link SingleQueryCountHolder} strategy can be used. It uses a single instance to store
 * {@link QueryCount} objects, which accumulate values across all threads until they are cleared.
 *
 * <p>In a web application, each HTTP request is handled by a single thread. Storing database access information in a
 * thread-local variable provides metrics for each request. Using a single instance instead lets you retrieve
 * accumulated totals since the application started.
 *
 * <p>{@link net.ttddyy.dsproxy.QueryCount} holds following data:
 * <ul>
 * <li>data source name
 * <li>number of database calls
 * <li>total query execution time
 * <li>number of queries by type
 * </ul>
 *
 * @author Tadaya Tsuyukubo
 * @see net.ttddyy.dsproxy.QueryCount
 * @see net.ttddyy.dsproxy.QueryCountHolder
 * @see net.ttddyy.dsproxy.listener.QueryCountStrategy
 * @see net.ttddyy.dsproxy.support.CommonsQueryCountLoggingServletFilter
 * @see net.ttddyy.dsproxy.support.CommonsQueryCountLoggingRequestListener
 * @see net.ttddyy.dsproxy.support.CommonsQueryCountLoggingHandlerInterceptor
 */
public class DataSourceQueryCountListener implements QueryExecutionListener {

    // Use the per-thread implementation by default.
    private QueryCountStrategy queryCountStrategy = new ThreadQueryCountHolder();

    @Override
    public void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
    }

    @Override
    public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        final String dataSourceName = execInfo.getDataSourceName();

        QueryCount count = this.queryCountStrategy.getOrCreateQueryCount(dataSourceName);

        // increment db call
        count.incrementTotal();
        if (execInfo.isSuccess()) {
            count.incrementSuccess();
        } else {
            count.incrementFailure();
        }

        // increment elapsed time
        final long elapsedTime = execInfo.getElapsedTime();
        count.incrementTime(elapsedTime);

        // increment statement type
        count.increment(execInfo.getStatementType());

        // increment query count
        for (QueryInfo queryInfo : queryInfoList) {
            final String query = queryInfo.getQuery();
            final QueryType type = QueryUtils.getQueryType(query);
            count.increment(type);
        }

    }

    /**
     * @since 1.4.2
     */
    public QueryCountStrategy getQueryCountStrategy() {
        return queryCountStrategy;
    }

    /**
     * @since 1.4.2
     */
    public void setQueryCountStrategy(QueryCountStrategy queryCountStrategy) {
        this.queryCountStrategy = queryCountStrategy;
    }

}
