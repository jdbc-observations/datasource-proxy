package net.ttddyy.dsproxy.test;

import net.ttddyy.dsproxy.proxy.ParameterKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.filterBy;
import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.filterByKeyType;
import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.toKeyIndexMap;
import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.toKeyNameMap;
import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.toKeyValueMap;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class CallableExecution extends BaseQueryExecution implements QueryHolder, ParameterByIndexHolder, ParameterByNameHolder, OutParameterHolder {

    private String query;
    private SortedSet<ParameterKeyValue> parameters = new TreeSet<ParameterKeyValue>();


    @Override
    public boolean isBatch() {
        return false;
    }

    @Override
    public String getQuery() {
        return this.query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public SortedSet<ParameterKeyValue> getParameters() {
        return this.parameters;
    }

    @Override
    public SortedSet<ParameterKeyValue> getSetParams() {
        return filterBy(this.parameters, ParameterKeyValue.OperationType.SET_PARAM);
    }

    @Override
    public SortedSet<ParameterKeyValue> getSetNullParams() {
        return filterBy(this.parameters, ParameterKeyValue.OperationType.SET_NULL);
    }

    @Override
    public SortedSet<ParameterKeyValue> getOutParams() {
        return filterBy(this.parameters, ParameterKeyValue.OperationType.REGISTER_OUT);
    }

    @Override
    public Map<String, Object> getParamsByName() {
        return toKeyNameMap(filterByKeyType(getSetParams(), ParameterKey.ParameterKeyType.BY_NAME));
    }

    @Override
    public Map<Integer, Object> getParamsByIndex() {
        return toKeyIndexMap(filterByKeyType(getSetParams(), ParameterKey.ParameterKeyType.BY_INDEX));
    }

    @Override
    public Map<String, Integer> getSetNullParamsByName() {
        return toKeyNameMap(filterByKeyType(getSetNullParams(), ParameterKey.ParameterKeyType.BY_NAME));
    }

    @Override
    public Map<Integer, Integer> getSetNullParamsByIndex() {
        return toKeyIndexMap(filterByKeyType(getSetNullParams(), ParameterKey.ParameterKeyType.BY_INDEX));
    }

    @Override
    public Map<String, Object> getOutParamsByName() {
        return toKeyNameMap(filterByKeyType(getOutParams(), ParameterKey.ParameterKeyType.BY_NAME));
    }

    @Override
    public Map<Integer, Object> getOutParamsByIndex() {
        return toKeyIndexMap(filterByKeyType(getOutParams(), ParameterKey.ParameterKeyType.BY_INDEX));
    }

    @Override
    public List<String> getParamNames() {
        List<String> names = new ArrayList<String>();
        names.addAll(getParamsByName().keySet());
        names.addAll(getSetNullParamsByName().keySet());
        return names;
    }

    @Override
    public List<Integer> getParamIndexes() {
        List<Integer> indexes = new ArrayList<Integer>();
        indexes.addAll(getParamsByIndex().keySet());
        indexes.addAll(getSetNullParamsByIndex().keySet());
        return indexes;
    }

    @Override
    public List<String> getOutParamNames() {
        return new ArrayList<String>(getOutParamsByName().keySet());
    }

    @Override
    public List<Integer> getOutParamIndexes() {
        return new ArrayList<Integer>(getOutParamsByIndex().keySet());
    }

    @Override
    public Map<ParameterKey, Object> getAllParams() {
        return toKeyValueMap(this.parameters);
    }


}
