package net.ttddyy.dsproxy.test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class PreparedExecution {

    public String query;

    public Map<String, Object> params = new LinkedHashMap<String, Object>();

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public List<String> getParamKeys() {
        return new ArrayList<String>(this.params.keySet());
    }

    public List<Object> getParamValues() {
        return new ArrayList<Object>(this.params.values());
    }

}
