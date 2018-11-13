package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.proxy.ParameterKey;
import net.ttddyy.dsproxy.proxy.ParameterSetOperation;
import net.ttddyy.dsproxy.proxy.ParameterSetOperations;

import java.lang.reflect.Method;
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
    private ParameterSetOperations queryArgs = new ParameterSetOperations();

    // key=batch-index, val=param set ops
    private SortedMap<Integer, ParameterSetOperations> batchParams = new TreeMap<>();

    public static QueryInfoBuilder create() {
        return new QueryInfoBuilder();
    }

    public QueryInfoBuilder query(String query) {
        this.query = query;
        return this;
    }

    public QueryInfoBuilder param(int parameterIndex, Object value) {
        return param(new ParameterKey(parameterIndex), value);
    }

    public QueryInfoBuilder param(String parameterName, Object value) {
        return param(new ParameterKey(parameterName), value);
    }

    public QueryInfoBuilder param(ParameterKey parameterKey, Object value) {
        Object[] args = new Object[]{parameterKey.getKeyAsString(), value};
        ParameterSetOperation operation = new ParameterSetOperation(parameterKey, DUMMY_METHOD, args);
        this.queryArgs.add(operation);
        return this;
    }

    public QueryInfoBuilder batchParam(int batchIndex, int parameterIndex, Object value) {
        return this.batchParam(batchIndex, new ParameterKey(parameterIndex), value);
    }

    public QueryInfoBuilder batchParam(int batchIndex, String parameterName, Object value) {
        return this.batchParam(batchIndex, new ParameterKey(parameterName), value);
    }

    public QueryInfoBuilder batchParam(int batchIndex, ParameterKey parameterKey, Object value) {
        ParameterSetOperations parameterSetOperations = this.batchParams.get(batchIndex);
        if (parameterSetOperations == null) {
            parameterSetOperations = new ParameterSetOperations();
            this.batchParams.put(batchIndex, parameterSetOperations);
        }

        Object[] args = new Object[]{parameterKey.getKeyAsString(), value};
        ParameterSetOperation operation = new ParameterSetOperation(parameterKey, DUMMY_METHOD, args);
        parameterSetOperations.add(operation);
        return this;
    }

    public QueryInfo build() {
        QueryInfo queryInfo = new QueryInfo();
        queryInfo.setQuery(query);

        if (!batchParams.isEmpty()) {  // consider it's batch mode
            queryInfo.getParameterSetOperations().addAll(this.batchParams.values());
        } else {
            queryInfo.getParameterSetOperations().add(this.queryArgs);
        }
        return queryInfo;
    }
}
