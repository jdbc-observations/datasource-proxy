package net.ttddyy.dsproxy.test;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class StatementExecution extends BaseQueryExecution implements QueryHolder {

    private String query;

    @Override
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

}
