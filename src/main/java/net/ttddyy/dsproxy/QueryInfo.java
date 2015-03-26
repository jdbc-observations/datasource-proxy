package net.ttddyy.dsproxy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 */
public class QueryInfo {
    private String query;
    private List<List<?>> queryArgsList = new ArrayList<List<?>>();

    public QueryInfo() {
    }

    public QueryInfo(String query) {
        this.query = query;
    }

    public QueryInfo(String query, List<?> firstArgs) {
        this.query = query;
        if (firstArgs != null) {
            List<Object> args = new ArrayList<Object>(firstArgs.size());
            args.addAll(firstArgs);
            this.queryArgsList.add(args);
        }
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<List<?>> getQueryArgsList() {
        return queryArgsList;
    }

    public void setQueryArgsList(List<List<?>> queryArgsList) {
        this.queryArgsList = queryArgsList;
    }

}
