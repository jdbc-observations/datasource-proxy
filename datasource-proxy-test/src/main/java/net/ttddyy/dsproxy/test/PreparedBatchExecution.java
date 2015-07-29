package net.ttddyy.dsproxy.test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tadaya Tsuyukubo
 */
public class PreparedBatchExecution implements BatchParameterHolder {

    public static class PreparedBatchExecutionEntry implements BatchExecutionEntry, ParameterByIndexHolder {

        public Map<String, Object> paramsByName = new LinkedHashMap<String, Object>();
        public Map<Integer, Object> paramsByIndex = new LinkedHashMap<Integer, Object>();


        public void setParamsByName(Map<String, Object> paramsByName) {
            this.paramsByName = paramsByName;
        }

        public void setParamsByIndex(Map<Integer, Object> paramsByIndex) {
            this.paramsByIndex = paramsByIndex;
        }

        //        @Override
        public Map<String, Object> getParamsByName() {
            return paramsByName;
        }

        @Override
        public Map<Integer, Object> getParamsByIndex() {
            return paramsByIndex;
        }

        //        @Override
        public List<String> getParamNames() {
            return new ArrayList<String>(this.paramsByName.keySet());
        }

        @Override
        public List<Integer> getParamIndexes() {
            return new ArrayList<Integer>(this.paramsByIndex.keySet());
        }

        @Override
        public List<Object> getParamValues() {
            List<Object> list = new ArrayList<Object>();
            list.addAll(this.paramsByIndex.values());
            list.addAll(this.paramsByName.values());
            return list;
        }


    }

    public String query;

    public List<BatchExecutionEntry> batchExecutionEntries = new ArrayList<BatchExecutionEntry>();

    public void setQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    @Override
    public List<BatchExecutionEntry> getBatchExecutionEntries() {
        return batchExecutionEntries;
    }
}
