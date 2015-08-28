package net.ttddyy.dsproxy.test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class PreparedBatchExecution extends BaseQueryExecution implements QueryHolder, BatchParameterHolder, BatchExecution {

    public static class PreparedBatchExecutionEntry implements BatchExecutionEntry, ParameterByIndexHolder {

        public Map<Integer, Object> paramsByIndex = new LinkedHashMap<Integer, Object>();
        public Map<Integer, Integer> setNullByIndex = new LinkedHashMap<Integer, Integer>();

        public void setParamsByIndex(Map<Integer, Object> paramsByIndex) {
            this.paramsByIndex = paramsByIndex;
        }

        @Override
        public Map<Integer, Object> getParamsByIndex() {
            return this.paramsByIndex;
        }

        @Override
        public Map<Integer, Integer> getSetNullParamsByIndex() {
            return this.setNullByIndex;
        }

        @Override
        public List<Integer> getParamIndexes() {
            List<Integer> indexes = new ArrayList<Integer>();
            indexes.addAll(this.paramsByIndex.keySet());
            indexes.addAll(this.setNullByIndex.keySet());
            return indexes;
        }

        @Override
        public List<Object> getParamValues() {
            List<Object> list = new ArrayList<Object>();
            list.addAll(this.paramsByIndex.values());
            return list;
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
