package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.*;

import java.util.List;

/**
 * Update database access information to thread local value({@link net.ttddyy.dsproxy.QueryCount}).
 *
 * <p>In web application lifecycle, one http request is handled by one thread.
 * Storing database access information into a thread local value provides metrics
 * information per http request.
 *
 * <p>Thread local value({@link net.ttddyy.dsproxy.QueryCount}) holds following data:
 * <ul>
 * <li> datasource name
 * <li> number of database call
 * <li> total query execution time
 * <li> number of queries by type
 * </ul>
 *
 * <p>{@link net.ttddyy.dsproxy.QueryCount} can be retrieved by {@link net.ttddyy.dsproxy.QueryCountHolder#get(String)}.
 *
 * @author Tadaya Tsuyukubo
 * @see net.ttddyy.dsproxy.QueryCount
 * @see net.ttddyy.dsproxy.QueryCountHolder
 * @see net.ttddyy.dsproxy.support.CommonsQueryCountLoggingFilter
 * @see net.ttddyy.dsproxy.support.CommonsQueryCountLoggingRequestListener
 * @see net.ttddyy.dsproxy.support.CommonsQueryCountLoggingHandlerInterceptor
 */
public class DataSourceQueryCountListener implements QueryExecutionListener {

    @Override
    public void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
    }

    @Override
    public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        final String dataSourceName = execInfo.getDataSourceName();

        QueryCount count = QueryCountHolder.get(dataSourceName);
        if (count == null) {
            count = new QueryCount();
            QueryCountHolder.put(dataSourceName, count);
        }

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
            final QueryType type = getQueryType(query);
            count.increment(type);
        }

    }

    protected QueryType getQueryType(String query) {
        final String trimmedQuery = QueryUtils.removeCommentAndWhiteSpace(query);
        final char firstChar = trimmedQuery.charAt(0);

        final QueryType type;
        switch (firstChar) {
            case 'S':
            case 's':
                type = QueryType.SELECT;
                break;
            case 'I':
            case 'i':
                type = QueryType.INSERT;
                break;
            case 'U':
            case 'u':
                type = QueryType.UPDATE;
                break;
            case 'D':
            case 'd':
                type = QueryType.DELETE;
                break;
            default:
                type = QueryType.OTHER;
        }
        return type;
    }

}
