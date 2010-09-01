package com.tpon.dsproxy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 */
public class QueryInfo {
    private String query;
    private List<?> queryArgs = new ArrayList<Object>();

    public QueryInfo() {
    }

    public QueryInfo(String query, List queryArgs) {
        this.query = query;
        if (queryArgs != null) {
            this.queryArgs.addAll(queryArgs);
        }
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<?> getQueryArgs() {
        return queryArgs;
    }

    public void setQueryArgs(List<?> queryArgs) {
        this.queryArgs = queryArgs;
    }

}
