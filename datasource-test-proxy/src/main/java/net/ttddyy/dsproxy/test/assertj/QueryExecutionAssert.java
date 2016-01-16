package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.StatementType;
import net.ttddyy.dsproxy.test.CallableBatchExecution;
import net.ttddyy.dsproxy.test.CallableExecution;
import net.ttddyy.dsproxy.test.PreparedBatchExecution;
import net.ttddyy.dsproxy.test.PreparedExecution;
import net.ttddyy.dsproxy.test.QueryExecution;
import net.ttddyy.dsproxy.test.StatementBatchExecution;
import net.ttddyy.dsproxy.test.StatementExecution;

/**
 * @author Tadaya Tsuyukubo
 */
public class QueryExecutionAssert extends AbstractExecutionAssert<QueryExecutionAssert, QueryExecution> {

    public QueryExecutionAssert(QueryExecution actual) {
        super(actual, QueryExecutionAssert.class);
    }

    public QueryExecutionAssert isSuccess() {
        isExecutionSuccess();
        return this;
    }

    public QueryExecutionAssert isFailure() {
        isExecutionFailure();
        return this;
    }

    public QueryExecutionAssert isBatch() {
        if (!this.actual.isBatch()) {
            failWithMessage("%nExpecting: <%s> but was: <%s>%n", "Batch Execution", "Not Batch Execution");
        }
        return this;
    }

    public QueryExecutionAssert isStatement() {
        boolean isStatement = this.actual instanceof StatementExecution;
        if (!isStatement) {
            String actualType = getStatementTypeMessage(this.actual);
            failWithMessage("%nExpecting: <%s> but was: <%s>%n", "STATEMENT", actualType);
        }
        return this;
    }

    public QueryExecutionAssert isBatchStatement() {
        boolean isBatchStatement = this.actual instanceof StatementBatchExecution;
        if (!isBatchStatement) {
            String actualType = getStatementTypeMessage(this.actual);
            failWithMessage("%nExpecting: <%s> but was: <%s>%n", "BATCH STATEMENT", actualType);
        }
        return this;
    }

    public QueryExecutionAssert isStatementOrBatchStatement() {
        boolean isStatement = this.actual instanceof StatementExecution;
        boolean isBatchStatement = this.actual instanceof StatementBatchExecution;

        if (!(isStatement || isBatchStatement)) {
            String actualType = getStatementTypeMessage(this.actual);
            failWithMessage("%nExpecting: <%s> but was: <%s>%n", "STATEMENT or BATCH STATEMENT", actualType);
        }
        return this;
    }

    public QueryExecutionAssert isPrepared() {
        boolean isPrepared = this.actual instanceof PreparedExecution;
        if (!isPrepared) {
            String actualType = getStatementTypeMessage(this.actual);
            failWithMessage("%nExpecting: <%s> but was: <%s>%n", "PREPARED", actualType);
        }
        return this;
    }

    public QueryExecutionAssert isBatchPrepared() {
        boolean isBatchPrepared = this.actual instanceof PreparedBatchExecution;
        if (!isBatchPrepared) {
            String actualType = getStatementTypeMessage(this.actual);
            failWithMessage("%nExpecting: <%s> but was: <%s>%n", "BATCH PREPARED", actualType);
        }
        return this;
    }

    public QueryExecutionAssert isPreparedOrBatchPrepared() {
        boolean isPrepared = this.actual instanceof PreparedExecution;
        boolean isBatchPrepared = this.actual instanceof PreparedBatchExecution;

        if (!(isPrepared || isBatchPrepared)) {
            String actualType = getStatementTypeMessage(this.actual);
            failWithMessage("%nExpecting: <%s> but was: <%s>%n", "PREPARED or BATCH PREPARED", actualType);
        }

        return this;
    }

    public QueryExecutionAssert isCallable() {
        boolean isCallable = this.actual instanceof CallableExecution;
        if (!isCallable) {
            String actualType = getStatementTypeMessage(this.actual);
            failWithMessage("%nExpecting: <%s> but was: <%s>%n", "CALLABLE", actualType);
        }
        return this;
    }

    public QueryExecutionAssert isBatchCallable() {
        boolean isBatchCallable = this.actual instanceof CallableBatchExecution;
        if (!isBatchCallable) {
            String actualType = getStatementTypeMessage(this.actual);
            failWithMessage("%nExpecting: <%s> but was: <%s>%n", "BATCH CALLABLE", actualType);
        }
        return this;
    }

    public QueryExecutionAssert isCallableOrBatchCallable() {
        boolean isCallable = this.actual instanceof CallableExecution;
        boolean isBatchCallable = this.actual instanceof CallableBatchExecution;
        if (!(isCallable || isBatchCallable)) {
            String actualType = getStatementTypeMessage(this.actual);
            failWithMessage("%nExpecting: <%s> but was: <%s>%n", "CALLABLE or BATCH CALLABLE", actualType);
        }
        return this;
    }

    private String getStatementTypeMessage(QueryExecution queryExecution) {
        boolean isBatch = queryExecution.isBatch();
        StatementType type = getStatementType(queryExecution);
        return isBatch ? "BATCH " + type : type.toString();
    }

    private StatementType getStatementType(QueryExecution queryExecution) {
        boolean isBatch = queryExecution.isBatch();

        if (queryExecution instanceof StatementExecution || queryExecution instanceof StatementBatchExecution) {
            return StatementType.STATEMENT;
        } else if (queryExecution instanceof PreparedExecution || queryExecution instanceof PreparedBatchEntryAssert) {
            return StatementType.PREPARED;
        } else if (queryExecution instanceof CallableExecution || queryExecution instanceof CallableBatchExecution) {
            return StatementType.CALLABLE;
        }
        throw new IllegalArgumentException();
    }

    public StatementExecutionAssert asStatement() {
        return DataSourceProxyAssertions.assertThat((StatementExecution) this.actual);
    }

    public StatementBatchExecutionAssert asBatchStatement() {
        return DataSourceProxyAssertions.assertThat((StatementBatchExecution) this.actual);
    }

    public PreparedExecutionAssert asPrepared() {
        return DataSourceProxyAssertions.assertThat((PreparedExecution) this.actual);
    }

    public PreparedBatchExecutionAssert asBatchPrepared() {
        return DataSourceProxyAssertions.assertThat((PreparedBatchExecution) this.actual);
    }

    public CallableExecutionAssert asCallable() {
        return DataSourceProxyAssertions.assertThat((CallableExecution) this.actual);
    }

    public CallableBatchExecutionAssert asBatchCallable() {
        return DataSourceProxyAssertions.assertThat((CallableBatchExecution) this.actual);
    }


}
