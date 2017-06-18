package net.ttddyy.dsproxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Hold QueryCount object by datasource name.
 *
 * @author Tadaya Tsuyukubo
 * @see net.ttddyy.dsproxy.listener.QueryCountStrategy
 */
public class QueryCountHolder {

    private static ThreadLocal<ConcurrentMap<String, QueryCount>> queryCountMapHolder = new ThreadLocal<ConcurrentMap<String, QueryCount>>() {
        @Override
        protected ConcurrentMap<String, QueryCount> initialValue() {
            return new ConcurrentHashMap<String, QueryCount>();
        }
    };

    public static QueryCount get(String dataSourceName) {
        final Map<String, QueryCount> map = queryCountMapHolder.get();
        return map.get(dataSourceName);
    }

    public static QueryCount getGrandTotal() {
        final QueryCount totalCount = new QueryCount();
        final Map<String, QueryCount> map = queryCountMapHolder.get();
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

    public static void put(String dataSourceName, QueryCount count) {
        queryCountMapHolder.get().put(dataSourceName, count);
    }

    public static List<String> getDataSourceNamesAsList() {
        return new ArrayList<String>(getDataSourceNames());
    }

    public static Set<String> getDataSourceNames() {
        return queryCountMapHolder.get().keySet();
    }

    public static void clear() {
        queryCountMapHolder.get().clear();
    }
}
