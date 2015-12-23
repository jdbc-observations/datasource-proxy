package net.ttddyy.dsproxy.test;

import net.ttddyy.dsproxy.proxy.ParameterKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static net.ttddyy.dsproxy.proxy.ParameterKeyUtils.toIndexMap;
import static net.ttddyy.dsproxy.proxy.ParameterKeyUtils.toNameMap;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class CallableExecution extends BaseQueryExecution implements QueryHolder, ParameterByIndexHolder, ParameterByNameHolder, OutParameterHolder {

    public String query;
    public Map<ParameterKey, Object> params = new LinkedHashMap<ParameterKey, Object>();
    public Map<ParameterKey, Integer> setNullParams = new LinkedHashMap<ParameterKey, Integer>();
    public Map<ParameterKey, Object> outParams = new LinkedHashMap<ParameterKey, Object>();

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
    public Map<ParameterKey, Object> getParams() {
        return this.params;
    }

    @Override
    public Map<ParameterKey, Integer> getSetNullParams() {
        return this.setNullParams;
    }

    @Override
    public Map<ParameterKey, Object> getOutParams() {
        return this.outParams;
    }

    @Override
    public Map<String, Object> getParamsByName() {
        return toNameMap(this.params);
    }

    @Override
    public Map<Integer, Object> getParamsByIndex() {
        return toIndexMap(this.params);
    }

    @Override
    public Map<String, Integer> getSetNullParamsByName() {
        return toNameMap(this.setNullParams);
    }

    @Override
    public Map<Integer, Integer> getSetNullParamsByIndex() {
        return toIndexMap(this.setNullParams);
    }

    @Override
    public List<String> getParamNames() {
        List<String> names = new ArrayList<String>();
        names.addAll(toNameMap(this.params).keySet());
        names.addAll(toNameMap(this.setNullParams).keySet());
        return names;
    }

    @Override
    public List<Integer> getParamIndexes() {
        List<Integer> indexes = new ArrayList<Integer>();
        indexes.addAll(toIndexMap(this.params).keySet());
        indexes.addAll(toIndexMap(this.setNullParams).keySet());
        return indexes;
    }

    @Override
    public Map<ParameterKey, Object> getAllParams() {
        Map<ParameterKey, Object> params = new HashMap<ParameterKey, Object>();
        params.putAll(this.params);
        params.putAll(this.setNullParams);
        params.putAll(this.outParams);
        return params;
    }

    @Override
    public Map<Integer, Object> getOutParamsByIndex() {
        return toIndexMap(this.outParams);
    }

    @Override
    public Map<String, Object> getOutParamsByName() {
        return toNameMap(this.outParams);
    }

    @Override
    public List<Integer> getOutParamIndexes() {
        return new ArrayList<Integer>(toIndexMap(this.outParams).keySet());
    }

    @Override
    public List<String> getOutParamNames() {
        return new ArrayList<String>(toNameMap(this.outParams).keySet());
    }
}
