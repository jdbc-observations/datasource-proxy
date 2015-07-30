package net.ttddyy.dsproxy.test;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class CallableExecution extends BaseQueryExecution implements QueryHolder {

    public String query;

    @Override
    public String getQuery() {
        return this.query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
