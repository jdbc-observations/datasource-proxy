package net.ttddyy.dsproxy;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tadaya Tsuyukubo
 */
public class QueryInfo {
    private String query;
    private List<Map<Object, Object>> queryArgsList = new ArrayList<Map<Object, Object>>();

    public QueryInfo() {
    }

    public QueryInfo(String query) {
        this.query = query;
    }

    public QueryInfo(String query, Map<Object, Object> firstArgs) {
        this.query = query;
        if (firstArgs != null) {
            this.queryArgsList.add(new LinkedHashMap<Object, Object>(firstArgs));
        }
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<Map<Object, Object>> getQueryArgsList() {
        return queryArgsList;
    }

    public void setQueryArgsList(List<Map<Object, Object>> queryArgsList) {
        this.queryArgsList = queryArgsList;
    }

}
