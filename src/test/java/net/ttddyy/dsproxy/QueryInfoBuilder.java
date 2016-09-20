package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.proxy.ParameterSetOperation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author Tadaya Tsuyukubo
 */
public class QueryInfoBuilder {

    private static final Method DUMMY_METHOD;

    static {
        try {
            DUMMY_METHOD = Object.class.getMethod("toString");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to register dummy method", e);
        }
    }

    private String query;
    private Map<String, Object> queryArgs = new LinkedHashMap<String, Object>();

    // key:batch-index, val: map of query args
    private SortedMap<Integer, Map<String, Object>> batchParams = new TreeMap<Integer, Map<String, Object>>();

    public static QueryInfoBuilder create() {
        return new QueryInfoBuilder();
    }

    public QueryInfoBuilder query(String query) {
        this.query = query;
        return this;
    }

    public QueryInfoBuilder param(int parameterIndex, Object value) {
        return this.param(Integer.toString(parameterIndex), value);
    }

    public QueryInfoBuilder param(String parameterName, Object value) {
        queryArgs.put(parameterName, value);
        return this;
    }

    public QueryInfoBuilder batchParam(int batchIndex, int parameterName, Object value) {
        return this.batchParam(batchIndex, Integer.toString(parameterName), value);
    }

    public QueryInfoBuilder batchParam(int batchIndex, String parameterIndex, Object value) {
        Map<String, Object> args = batchParams.get(batchIndex);
        if (args == null) {
            args = new LinkedHashMap<String, Object>();
            batchParams.put(batchIndex, args);
        }
        args.put(parameterIndex, value);
        return this;
    }

    public QueryInfo build() {
        QueryInfo queryInfo = new QueryInfo();
        queryInfo.setQuery(query);

        // query parameters
        if (!batchParams.isEmpty()) {  // consider it's batch mode
            // already ordered by batchIndex
            for (Map<String, Object> map : batchParams.values()) {
                List<ParameterSetOperation> params = new ArrayList<ParameterSetOperation>();
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    ParameterSetOperation param = new ParameterSetOperation();
                    param.setArgs(new Object[]{entry.getKey(), entry.getValue()});
                    param.setMethod(DUMMY_METHOD);
                    params.add(param);
                }
                queryInfo.getParametersList().add(params);
            }
        } else {
            List<ParameterSetOperation> params = new ArrayList<ParameterSetOperation>();
            for (Map.Entry<String, Object> entry : queryArgs.entrySet()) {
                ParameterSetOperation param = new ParameterSetOperation();
                param.setArgs(new Object[]{entry.getKey(), entry.getValue()});
                param.setMethod(DUMMY_METHOD);
                params.add(param);
            }
            queryInfo.getParametersList().add(params);
        }

        return queryInfo;
    }
}
