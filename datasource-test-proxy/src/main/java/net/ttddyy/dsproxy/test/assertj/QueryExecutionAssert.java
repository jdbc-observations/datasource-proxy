package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.CallableBatchExecution;
import net.ttddyy.dsproxy.test.PreparedBatchExecution;
import net.ttddyy.dsproxy.test.PreparedExecution;
import net.ttddyy.dsproxy.test.QueryExecution;
import net.ttddyy.dsproxy.test.StatementBatchExecution;
import net.ttddyy.dsproxy.test.StatementExecution;
import org.assertj.core.api.AbstractAssert;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class QueryExecutionAssert extends AbstractAssert<QueryExecutionAssert, QueryExecution> {

    public QueryExecutionAssert(QueryExecution actual) {
        super(actual, QueryExecution.class);
    }

    public QueryExecutionAssert isSuccess() {
        // TODO: impl
        return this;
    }
    public QueryExecutionAssert isFailure() {
        // TODO: impl
        return this;
    }

    public QueryExecutionAssert isBatch() {
        // TODO: impl
        return this;
    }

    public QueryExecutionAssert isStatement() {
        // TODO: impl
        return this;
    }

    public QueryExecutionAssert isBatchStatement() {
        // TODO: impl
        return this;
    }

    public QueryExecutionAssert isStatementOrBatchStatement() {
        // TODO: impl
        return this;
    }

    public QueryExecutionAssert isPrepared() {
        // TODO: impl
        return this;
    }

    public QueryExecutionAssert isBatchPrepared() {
        // TODO: impl
        return this;
    }

    public QueryExecutionAssert isPreparedOrBatchPrepared() {
        // TODO: impl
        return this;
    }

    public QueryExecutionAssert isCallable() {
        // TODO: impl
        return this;
    }

    public QueryExecutionAssert isBatchCallable() {
        // TODO: impl
        return this;
    }

    public QueryExecutionAssert isCallableOrBatchCallable() {
        // TODO: impl
        return this;
    }

    public StatementExecutionAssert asStatement() {
        // TODO: impl
        return DataSourceProxyAssertions.assertThat((StatementExecution) this.actual);
    }

    public StatementBatchExecutionAssert asBatchStatement() {
        // TODO: impl
        return DataSourceProxyAssertions.assertThat((StatementBatchExecution) this.actual);
    }

    public PreparedExecutionAssert asPrepared() {
        // TODO: impl
        return DataSourceProxyAssertions.assertThat((PreparedExecution) this.actual);
    }

    public PreparedBatchExecutionAssert asBatchPrepared() {
        // TODO: impl
        return DataSourceProxyAssertions.assertThat((PreparedBatchExecution) this.actual);
    }

    public StatementExecutionAssert asCallable() {
        // TODO: impl
        return DataSourceProxyAssertions.assertThat((StatementExecution) this.actual);
    }

    public CallableBatchExecutionAssert asBatchCallable() {
        // TODO: impl
        return DataSourceProxyAssertions.assertThat((CallableBatchExecution) this.actual);
    }


}
