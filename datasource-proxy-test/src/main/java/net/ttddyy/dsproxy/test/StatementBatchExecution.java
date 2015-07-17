package net.ttddyy.dsproxy.test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class StatementBatchExecution {

    private List<String> queries = new ArrayList<String>();

    public List<String> getQueries() {
        return queries;
    }

    public void setQueries(List<String> queries) {
        this.queries = queries;
    }
}
