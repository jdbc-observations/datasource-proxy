package net.ttddyy.dsproxy.test;

import net.ttddyy.dsproxy.QueryType;

/**
 * @author Tadaya Tsuyukubo
 */
public abstract class BaseQueryExecution implements QueryExecution {

    private boolean success;
    private QueryType queryType;

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setQueryType(QueryType queryType) {
        this.queryType = queryType;
    }

    @Override
    public boolean isSuccess() {
        return this.success;
    }

    @Override
    public QueryType getQueryType() {
        return this.queryType;
    }
}
