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
    private List<Map<String, Object>> queryArgsList = new ArrayList<Map<String, Object>>();

    public QueryInfo() {
    }

    public QueryInfo(String query) {
        this.query = query;
    }

    public QueryInfo(String query, Map<String, Object> firstArgs) {
        this.query = query;
        if (firstArgs != null) {
            this.queryArgsList.add(new LinkedHashMap<String, Object>(firstArgs));
        }
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<Map<String, Object>> getQueryArgsList() {
        return queryArgsList;
    }

    public void setQueryArgsList(List<Map<String, Object>> queryArgsList) {
        this.queryArgsList = queryArgsList;
    }

}
