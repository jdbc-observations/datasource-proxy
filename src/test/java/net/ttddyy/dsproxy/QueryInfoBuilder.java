package net.ttddyy.dsproxy;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author Tadaya Tsuyukubo
 */
public class QueryInfoBuilder {
    private String query;
    private Map<Object, Object> queryArgs = new LinkedHashMap<Object, Object>();

    // key:batch-index, val: map of query args
    private SortedMap<Integer, Map<Object, Object>> batchParams = new TreeMap<Integer, Map<Object, Object>>();

    public static QueryInfoBuilder create() {
        return new QueryInfoBuilder();
    }

    public QueryInfoBuilder query(String query) {
        this.query = query;
        return this;
    }

    public QueryInfoBuilder param(Object key, Object value) {
        queryArgs.put(key, value);
        return this;
    }

    public QueryInfoBuilder batchParam(int batchIndex, Object key, Object value) {
        Map<Object, Object> args = batchParams.get(batchIndex);
        if (args == null) {
            args = new LinkedHashMap<Object, Object>();
            batchParams.put(batchIndex, args);
        }
        args.put(key, value);
        return this;
    }

    public QueryInfo build() {
        QueryInfo queryInfo = new QueryInfo();
        queryInfo.setQuery(query);

        // query parameters
        if (!batchParams.isEmpty()) {  // consider it's batch mode
            // already ordered by batchIndex
            for (Map<Object, Object> map : batchParams.values()) {
                queryInfo.getQueryArgsList().add(map);
            }
        } else {
            queryInfo.getQueryArgsList().add(queryArgs);
        }

        return queryInfo;
    }
}
