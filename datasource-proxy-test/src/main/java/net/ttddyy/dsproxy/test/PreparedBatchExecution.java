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

        public void setParamsByIndex(Map<Integer, Object> paramsByIndex) {
            this.paramsByIndex = paramsByIndex;
        }

        @Override
        public Map<Integer, Object> getParamsByIndex() {
            return paramsByIndex;
        }

        @Override
        public List<Integer> getParamIndexes() {
            return new ArrayList<Integer>(this.paramsByIndex.keySet());
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
