package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.QueryType;
import net.ttddyy.dsproxy.test.StatementExecution;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class StatementExecutionAssert extends AbstractExecutionAssert<StatementExecutionAssert, StatementExecution> {

    public StatementExecutionAssert(StatementExecution actual) {
        super(actual, StatementExecutionAssert.class);
    }

    public StatementExecutionAssert isSuccess() {
        isExecutionSuccess();
        return this;
    }

    public StatementExecutionAssert isFailure() {
        isExecutionFailure();
        return this;
    }

    public QueryAssert query() {
        return new QueryAssert(actual);
    }

    public StatementExecutionAssert hasQueryType(QueryType queryType) {
        // TODO: impl
        return this;
    }

}
