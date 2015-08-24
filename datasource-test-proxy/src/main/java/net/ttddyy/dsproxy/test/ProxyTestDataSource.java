package net.ttddyy.dsproxy.test;

import net.ttddyy.dsproxy.support.ProxyDataSource;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class ProxyTestDataSource extends ProxyDataSource {

    // TODO: add clear() or reset() method

    private QueryExecutionFactoryListener queryExecutionFactoryListener = new QueryExecutionFactoryListener();


    public ProxyTestDataSource() {
        initialize();
    }

    public ProxyTestDataSource(DataSource dataSource) {
        super(dataSource);
        initialize();
    }

    private void initialize() {
        this.getInterceptorHolder().addListener(this.queryExecutionFactoryListener);
    }

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

    public List<PreparedExecution> getPrepareds() {
        return getQueryExecutionsFilteredBy(PreparedExecution.class);
    }

    public PreparedExecution getFirstPrepared() {
        return getFirstQueryExecution(PreparedExecution.class);
    }

    public PreparedExecution getLastPrepared() {
        return getLastQueryExecution(PreparedExecution.class);
    }

    public List<PreparedBatchExecution> getBatchPrepareds() {
        return getQueryExecutionsFilteredBy(PreparedBatchExecution.class);
    }

    public PreparedBatchExecution getFirstBatchPrepared() {
        return getFirstQueryExecution(PreparedBatchExecution.class);
    }

    public PreparedBatchExecution getLastBatchPrepared() {
        return getLastQueryExecution(PreparedBatchExecution.class);
    }

    public List<CallableExecution> getCallables() {
        return getQueryExecutionsFilteredBy(CallableExecution.class);
    }

    public CallableExecution getFirstCallable() {
        return getFirstQueryExecution(CallableExecution.class);
    }

    public CallableExecution getLastCallable() {
        return getLastQueryExecution(CallableExecution.class);
    }

    public List<CallableBatchExecution> getBatchCallables() {
        return getQueryExecutionsFilteredBy(CallableBatchExecution.class);
    }

    public CallableBatchExecution getFirstBatchCallable() {
        return getFirstQueryExecution(CallableBatchExecution.class);
    }

    public CallableBatchExecution getLastBatchCallable() {
        return getLastQueryExecution(CallableBatchExecution.class);
    }


    @SuppressWarnings("unchecked")
    private <T extends QueryExecution> List<T> getQueryExecutionsFilteredBy(Class<T> classToFilter) {
        List<T> result = new ArrayList<T>();
        for (QueryExecution queryExecution : getQueryExecutions()) {
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
        return this.queryExecutionFactoryListener.getQueryExecutions();
    }

    public QueryExecutionFactoryListener getQueryExecutionFactoryListener() {
        return queryExecutionFactoryListener;
    }
}
