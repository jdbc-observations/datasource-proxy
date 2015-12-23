package net.ttddyy.dsproxy.test;

import net.ttddyy.dsproxy.proxy.ParameterKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static net.ttddyy.dsproxy.proxy.ParameterKeyUtils.toIndexMap;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class PreparedBatchExecution extends BaseQueryExecution implements QueryHolder, BatchParameterHolder, BatchExecution {

    public static class PreparedBatchExecutionEntry implements BatchExecutionEntry, ParameterByIndexHolder {

        public Map<ParameterKey, Object> params = new LinkedHashMap<ParameterKey, Object>();
        public Map<ParameterKey, Integer> setNullParams = new LinkedHashMap<ParameterKey, Integer>();

        @Override
        public Map<Integer, Object> getParamsByIndex() {
            return toIndexMap(this.params);
        }

        @Override
        public Map<Integer, Integer> getSetNullParamsByIndex() {
            return toIndexMap(this.setNullParams);
        }

        @Override
        public List<Integer> getParamIndexes() {
            List<Integer> indexes = new ArrayList<Integer>();
            indexes.addAll(toIndexMap(this.params).keySet());
            indexes.addAll(toIndexMap(this.setNullParams).keySet());
            return indexes;
        }

        @Override
        public List<Object> getParamValues() {
            List<Object> list = new ArrayList<Object>();
            list.addAll(toIndexMap(this.params).values());
            return list;
        }

        @Override
        public Map<ParameterKey, Object> getParams() {
            return this.params;
        }

        @Override
        public Map<ParameterKey, Integer> getSetNullParams() {
            return this.setNullParams;
        }

        @Override
        public Map<ParameterKey, Object> getAllParams() {
            Map<ParameterKey, Object> params = new HashMap<ParameterKey, Object>();
            params.putAll(this.params);
            params.putAll(this.setNullParams);
            return params;
        }
    }

    public String query;

    public List<BatchExecutionEntry> batchExecutionEntries = new ArrayList<BatchExecutionEntry>();

    @Override
    public boolean isBatch() {
        return true;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public String getQuery() {
        return query;
    }

    @Override
    public List<BatchExecutionEntry> getBatchExecutionEntries() {
        return batchExecutionEntries;
    }
}
