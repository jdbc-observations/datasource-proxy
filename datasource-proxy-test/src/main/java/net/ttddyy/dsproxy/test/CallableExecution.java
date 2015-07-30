package net.ttddyy.dsproxy.test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class CallableExecution extends BaseQueryExecution implements QueryHolder, ParameterByIndexHolder, ParameterByNameHolder {

    public String query;
    public Map<String, Object> paramsByName = new LinkedHashMap<String, Object>();
    public Map<Integer, Object> paramsByIndex = new LinkedHashMap<Integer, Object>();


    @Override
    public String getQuery() {
        return this.query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setParamsByName(Map<String, Object> paramsByName) {
        this.paramsByName = paramsByName;
    }

    public void setParamsByIndex(Map<Integer, Object> paramsByIndex) {
        this.paramsByIndex = paramsByIndex;
    }

    @Override
    public Map<String, Object> getParamsByName() {
        return paramsByName;
    }

    @Override
    public Map<Integer, Object> getParamsByIndex() {
        return paramsByIndex;
    }

    @Override
    public List<String> getParamNames() {
        return new ArrayList<String>(this.paramsByName.keySet());
    }

    @Override
    public List<Integer> getParamIndexes() {
        return new ArrayList<Integer>(this.paramsByIndex.keySet());
    }

}
