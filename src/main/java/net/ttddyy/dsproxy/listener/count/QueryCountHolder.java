package net.ttddyy.dsproxy.listener.count;

import java.util.Map;
import java.util.Set;

/**
 * Hold QueryCount object by datasource name.
 *
 * Default uses {@link ThreadQueryCountStrategy} to store the {@link QueryCount} per thread.
 * This is suitable to get {@link QueryCount} during a request-response lifecycle, if it is used in
 * request per thread model. (e.g: servlet environment).
 *
 * @author Tadaya Tsuyukubo
 * @see QueryCountStrategy
 */
public class QueryCountHolder {

    private static QueryCountStrategy strategy = new ThreadQueryCountStrategy();

    public static void setQueryCountStrategy(QueryCountStrategy strategy) {
        QueryCountHolder.strategy = strategy;
    }

    public static QueryCountStrategy getQueryCountStrategy() {
        return strategy;
    }

    public static QueryCount getOrCreateQueryCount(String dataSourceName) {
        return strategy.getOrCreateQueryCount(dataSourceName);
    }

    public static QueryCount getGrandTotal() {
        QueryCount totalCount = new QueryCount();
        Map<String, QueryCount> map = strategy.getAll();
        for (QueryCount queryCount : map.values()) {
            totalCount.setSelect(totalCount.getSelect() + queryCount.getSelect());
            totalCount.setInsert(totalCount.getInsert() + queryCount.getInsert());
            totalCount.setUpdate(totalCount.getUpdate() + queryCount.getUpdate());
            totalCount.setDelete(totalCount.getDelete() + queryCount.getDelete());
            totalCount.setOther(totalCount.getOther() + queryCount.getOther());
            totalCount.setTotal(totalCount.getTotal() + queryCount.getTotal());
            totalCount.setSuccess(totalCount.getSuccess() + queryCount.getSuccess());
            totalCount.setFailure(totalCount.getFailure() + queryCount.getFailure());
            totalCount.setTime(totalCount.getTime() + queryCount.getTime());
        }
        return totalCount;
    }

    public static Set<String> getDataSourceNames() {
        return strategy.getAll().keySet();
    }

    public static void clearAll() {
        strategy.clearAll();
    }

    public static void clear(String dataSourceName) {
        strategy.clear(dataSourceName);
    }
}
