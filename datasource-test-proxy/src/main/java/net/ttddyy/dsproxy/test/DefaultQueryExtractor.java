package net.ttddyy.dsproxy.test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class DefaultQueryExtractor implements QueryExtractor {

    @Override
    public List<String> getQueries(QueryExecution queryExecution) {
        List<String> queries = new ArrayList<String>();
        if (queryExecution instanceof QueryHolder) {
            queries.add(((QueryHolder) queryExecution).getQuery());
        } else if (queryExecution instanceof QueriesHolder) {
            queries.addAll(((QueriesHolder) queryExecution).getQueries());
        }
        return queries;
    }

}
