package net.ttddyy.dsproxy.test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class StatementBatchExecution extends BaseQueryExecution implements QueriesHolder, BatchExecution {

    private List<String> queries = new ArrayList<String>();

    @Override
    public boolean isBatch() {
        return true;
    }

    @Override
    public List<String> getQueries() {
        return queries;
    }

    public void setQueries(List<String> queries) {
        this.queries = queries;
    }

}
