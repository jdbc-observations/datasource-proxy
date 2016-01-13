package net.ttddyy.dsproxy.test;

import net.ttddyy.dsproxy.proxy.ParameterKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.ttddyy.dsproxy.proxy.ParameterKeyUtils.toIndexMap;
import static net.ttddyy.dsproxy.proxy.ParameterKeyUtils.toNameMap;
import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.filterBy;
import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.toKeyValueMap;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class CallableExecution extends BaseQueryExecution implements QueryHolder, ParameterByIndexHolder, ParameterByNameHolder, OutParameterHolder {

    private String query;
    private List<ParameterKeyValue> parameters = new ArrayList<ParameterKeyValue>();

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
    public List<ParameterKeyValue> getParameters() {
        return this.parameters;
    }

    @Override
    public Map<ParameterKey, Object> getParams() {
        return toKeyValueMap(filterBy(this.parameters, ParameterKeyValue.OperationType.SET_PARAM));
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
        return toNameMap(getParams());
    }

    @Override
    public Map<Integer, Object> getParamsByIndex() {
        return toIndexMap(getParams());
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
    public List<String> getParamNames() {
        List<String> names = new ArrayList<String>();
        names.addAll(toNameMap(getParams()).keySet());
        names.addAll(toNameMap(getSetNullParams()).keySet());
        return names;
    }

    @Override
    public List<Integer> getParamIndexes() {
        List<Integer> indexes = new ArrayList<Integer>();
        indexes.addAll(toIndexMap(getParams()).keySet());
        indexes.addAll(toIndexMap(getSetNullParams()).keySet());
        return indexes;
    }

    @Override
    public Map<ParameterKey, Object> getAllParams() {
        return toKeyValueMap(this.parameters);
    }

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
