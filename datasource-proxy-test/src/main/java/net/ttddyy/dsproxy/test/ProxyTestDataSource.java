package net.ttddyy.dsproxy.test;

import net.ttddyy.dsproxy.support.ProxyDataSource;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class ProxyTestDataSource extends ProxyDataSource {

    private List<QueryExecution> queryExecutions = new ArrayList<QueryExecution>();

    public List<StatementExecution> getStatements() {
        return getQueryExecutionsFilteredBy(StatementExecution.class);
    }

    public StatementExecution getFirstStatement() {
        return getFirstQueryExecution(StatementExecution.class);
    }

    public StatementExecution getLastStatement() {
        return getLastQueryExecution(StatementExecution.class);
    }

    public List<StatementBatchExecution> getBatchStatements() {
        return getQueryExecutionsFilteredBy(StatementBatchExecution.class);
    }

    public StatementBatchExecution getFirstBatchStatement() {
        return getFirstQueryExecution(StatementBatchExecution.class);
    }

    public StatementBatchExecution getLastBatchStatement() {
        return getLastQueryExecution(StatementBatchExecution.class);
    }

    @SuppressWarnings("unchecked")
    private <T extends QueryExecution> List<T> getQueryExecutionsFilteredBy(Class<T> classToFilter) {
        List<T> result = new ArrayList<T>();
        for (QueryExecution queryExecution : this.queryExecutions) {
            if (classToFilter.isAssignableFrom(queryExecution.getClass())) {
                result.add((T) queryExecution);
            }
        }
        return result;
    }

    private <T extends QueryExecution> T getFirstQueryExecution(Class<T> classToFilter) {
        List<T> filtered = getQueryExecutionsFilteredBy(classToFilter);
        // TODO: if there is no element
        return filtered.get(0);
    }

    private <T extends QueryExecution> T getLastQueryExecution(Class<T> classToFilter) {
        List<T> filtered = getQueryExecutionsFilteredBy(classToFilter);
        // TODO: if there is no element
        return filtered.get(filtered.size() - 1);
    }

    public List<QueryExecution> getQueryExecutions() {
        return queryExecutions;
    }
}
