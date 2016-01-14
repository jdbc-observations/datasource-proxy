package net.ttddyy.dsproxy.test;

import net.ttddyy.dsproxy.proxy.ParameterKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import static net.ttddyy.dsproxy.proxy.ParameterKeyUtils.toIndexMap;
import static net.ttddyy.dsproxy.proxy.ParameterKeyUtils.toNameMap;
import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.filterBy;
import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.filterByKeyType;
import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.toKeyIndexMap;
import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.toKeyNameMap;
import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.toKeyValueMap;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class CallableBatchExecution extends BaseQueryExecution implements BatchParameterHolder, QueryHolder, BatchExecution {

    public static class CallableBatchExecutionEntry implements BatchExecutionEntry, ParameterByIndexHolder, ParameterByNameHolder, OutParameterHolder {

        private SortedSet<ParameterKeyValue> parameters = new TreeSet<ParameterKeyValue>();

        @Override
        public SortedSet<ParameterKeyValue> getParameters() {
            return this.parameters;
        }

        @Override
        public SortedSet<ParameterKeyValue> getParams() {
            return filterBy(this.parameters, ParameterKeyValue.OperationType.SET_PARAM);
        }

        @Override
        public Map<ParameterKey, Integer> getSetNullParams() {
            return toKeyValueMap(filterBy(this.parameters, ParameterKeyValue.OperationType.SET_NULL));
        }

        @Override
        public Map<ParameterKey, Object> getOutParams() {
            return toKeyValueMap(filterBy(this.parameters, ParameterKeyValue.OperationType.REGISTER_OUT));
        }

        @Override
        public Map<String, Object> getParamsByName() {
            return toKeyNameMap(filterByKeyType(getParams(), ParameterKey.ParameterKeyType.BY_NAME));
        }

        @Override
        public Map<Integer, Object> getParamsByIndex() {
            return toKeyIndexMap(filterByKeyType(getParams(), ParameterKey.ParameterKeyType.BY_INDEX));
        }

        @Override
        public Map<String, Integer> getSetNullParamsByName() {
            return toNameMap(getSetNullParams());
        }

        @Override
        public Map<Integer, Integer> getSetNullParamsByIndex() {
            return toIndexMap(getSetNullParams());
        }

        @Override
        public Map<ParameterKey, Object> getAllParams() {
            return toKeyValueMap(this.parameters);
        }

        @Override
        public List<String> getParamNames() {
            List<String> names = new ArrayList<String>();
            names.addAll(getParamsByName().keySet());
            names.addAll(toNameMap(getSetNullParams()).keySet());
            return names;
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
            list.addAll(toKeyValueMap(getParams()).values());
            return list;
        }

        // TODO: impl here
        @Override
        public Map<Integer, Object> getOutParamsByIndex() {
            return toIndexMap(getOutParams());
        }

        @Override
        public Map<String, Object> getOutParamsByName() {
            return toNameMap(getOutParams());
        }

        @Override
        public List<Integer> getOutParamIndexes() {
            return new ArrayList<Integer>(toIndexMap(getOutParams()).keySet());
        }

        @Override
        public List<String> getOutParamNames() {
            return new ArrayList<String>(toNameMap(getOutParams()).keySet());
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
