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
public class PreparedExecution extends BaseQueryExecution implements QueryHolder, ParameterByIndexHolder {

    public String query;
    public Map<ParameterKey, Object> params = new LinkedHashMap<ParameterKey, Object>();
    public Map<ParameterKey, Integer> setNullParams = new LinkedHashMap<ParameterKey, Integer>();

    @Override
    public boolean isBatch() {
        return false;
    }

    @Override
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

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
    public Map<ParameterKey, Object> getAllParams() {
        Map<ParameterKey, Object> params = new HashMap<ParameterKey, Object>();
        params.putAll(this.params);
        params.putAll(this.setNullParams);
        return params;
    }

    @Override
    public Map<ParameterKey, Object> getParams() {
        return this.params;
    }

    @Override
    public Map<ParameterKey, Integer> getSetNullParams() {
        return this.setNullParams;
    }

}
