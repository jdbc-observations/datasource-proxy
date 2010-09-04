package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryCount;
import net.ttddyy.dsproxy.QueryCountHolder;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.QueryType;

import java.util.List;

/**
 * Update database access information to thread local value({@link net.ttddyy.dsproxy.QueryCount}).
 * <p/>
 * In web application lifecycle, one http request is handled by one thread.
 * Storing database access information into a thread local value provides metrics
 * information per http request.
 * <p/>
 * Thread local value({@link net.ttddyy.dsproxy.QueryCount}) holds following information.
 * - datasource name
 * - number of database call
 * - total query execution time
 * - number of queries by type
 * <p/>
 * {@link net.ttddyy.dsproxy.QueryCount} can be retrieved by {@link net.ttddyy.dsproxy.QueryCountHolder#get(String)}.
 *
 * @author Tadaya Tsuyukubo
 * @see net.ttddyy.dsproxy.QueryCount
 * @see net.ttddyy.dsproxy.QueryCountHolder
 * @see net.ttddyy.dsproxy.support.CommonsQueryCountLoggingFilter
 * @see net.ttddyy.dsproxy.support.CommonsQueryCountLoggingRequestListener
 * @see net.ttddyy.dsproxy.support.CommonsQueryCountLoggingHandlerInterceptor
 */
public class DataSourceQueryCountListener implements QueryExecutionListener {

    public void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
    }

    public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        final String dataSourceName = execInfo.getDataSourceName();

        QueryCount count = QueryCountHolder.get(dataSourceName);
        if (count == null) {
            count = new QueryCount();
            QueryCountHolder.put(dataSourceName, count);
        }

        // increment db call
        count.incrementCall(); // num of db call
        if (execInfo.getThrowable() != null) {
            count.incrementFailure();
        }

        // increment elapsed time
        final long elapsedTime = execInfo.getElapsedTime();
        count.incrementElapsedTime(elapsedTime);

        // increment query count
        for (QueryInfo queryInfo : queryInfoList) {
            final String query = queryInfo.getQuery();
            final QueryType type = getQueryType(query);
            count.increment(type);
        }

    }

    private QueryType getQueryType(String query) {
        final String trimmedQuery = removeCommentAndWhiteSpace(query);
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

    private String removeCommentAndWhiteSpace(String query) {
        return query.replaceAll("--.*\n", "").replaceAll("\n", "").replaceAll("/\\*.*\\*/", "").trim();
    }


}
