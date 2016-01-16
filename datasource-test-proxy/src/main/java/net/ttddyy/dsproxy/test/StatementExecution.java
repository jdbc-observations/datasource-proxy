package net.ttddyy.dsproxy.test;

/**
 * @author Tadaya Tsuyukubo
 */
public class StatementExecution extends BaseQueryExecution implements QueryHolder {

    private String query;

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

}
