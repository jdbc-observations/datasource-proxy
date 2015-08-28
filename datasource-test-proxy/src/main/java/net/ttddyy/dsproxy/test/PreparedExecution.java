package net.ttddyy.dsproxy.test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class PreparedExecution extends BaseQueryExecution implements QueryHolder, ParameterByIndexHolder {

    public String query;
    public Map<Integer, Object> paramsByIndex = new LinkedHashMap<Integer, Object>();
    public Map<Integer, Integer> setNullByIndex = new LinkedHashMap<Integer, Integer>();

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
}
