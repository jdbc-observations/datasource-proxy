package net.ttddyy.dsproxy.test;

import net.ttddyy.dsproxy.proxy.ParameterKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import static net.ttddyy.dsproxy.proxy.ParameterKeyUtils.toIndexMap;
import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.filterBy;
import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.filterByKeyType;
import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.toKeyIndexMap;
import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.toKeyValueMap;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class PreparedBatchExecution extends BaseQueryExecution implements QueryHolder, BatchParameterHolder, BatchExecution {

    public static class PreparedBatchExecutionEntry implements BatchExecutionEntry, ParameterByIndexHolder {

        private SortedSet<ParameterKeyValue> parameters = new TreeSet<ParameterKeyValue>();

        @Override
        public SortedSet<ParameterKeyValue> getParameters() {
            return this.parameters;
        }

        @Override
        public Map<Integer, Object> getParamsByIndex() {
            return toKeyIndexMap(filterByKeyType(getSetParams(), ParameterKey.ParameterKeyType.BY_INDEX));
        }

        @Override
        public Map<Integer, Integer> getSetNullParamsByIndex() {
            return toIndexMap(getSetNullParams());
        }

        @Override
        public List<Integer> getParamIndexes() {
            List<Integer> indexes = new ArrayList<Integer>();
            indexes.addAll(getParamsByIndex().keySet());
            indexes.addAll(toIndexMap(getSetNullParams()).keySet());
            return indexes;
        }

        @Override
        public List<Object> getParamValues() {
            List<Object> list = new ArrayList<Object>();
            list.addAll(toKeyValueMap(getSetParams()).values());
            return list;
        }

        @Override
        public SortedSet<ParameterKeyValue> getSetParams() {
            return filterBy(this.parameters, ParameterKeyValue.OperationType.SET_PARAM);
        }

        @Override
        public Map<ParameterKey, Integer> getSetNullParams() {
            return toKeyValueMap(filterBy(this.parameters, ParameterKeyValue.OperationType.SET_NULL));
        }

        @Override
        public Map<ParameterKey, Object> getAllParams() {
            return toKeyValueMap(this.parameters);
        }
    }

    private String query;
    private List<BatchExecutionEntry> batchExecutionEntries = new ArrayList<BatchExecutionEntry>();

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
