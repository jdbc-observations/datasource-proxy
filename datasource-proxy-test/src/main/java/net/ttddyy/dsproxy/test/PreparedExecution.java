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
    public List<Integer> getParamIndexes() {
        return new ArrayList<Integer>(this.paramsByIndex.keySet());
    }
}
