package com.tpon.dsproxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Hold QueryCount object by datasource name.
 *
 * @author Tadaya Tsuyukubo
 */
public class QueryCountHolder {

    private static ThreadLocal<Map<String, QueryCount>> queryCountMapHolder = new ThreadLocal<Map<String, QueryCount>>() {
        @Override
        protected Map<String, QueryCount> initialValue() {
            return new HashMap<String, QueryCount>();
        }
    };

    public static QueryCount get(String dataSourceName) {
        final Map<String, QueryCount> map = queryCountMapHolder.get();
        return map.get(dataSourceName);
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
