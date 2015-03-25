package net.ttddyy.dsproxy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 */
public class QueryInfoBuilder {
    private String query;
    private List<?> queryArgs = new ArrayList<Object>();

    public static QueryInfoBuilder create() {
        return new QueryInfoBuilder();
    }

    public QueryInfoBuilder query(String query) {
        this.query = query;
        return this;
    }

    public QueryInfoBuilder queryArgs(List<?> queryArgs) {
        this.queryArgs = queryArgs;
        return this;
    }

    public QueryInfo build() {
        QueryInfo queryInfo = new QueryInfo();
        queryInfo.setQuery(query);
        queryInfo.setQueryArgs(queryArgs);
        return queryInfo;
    }
}
