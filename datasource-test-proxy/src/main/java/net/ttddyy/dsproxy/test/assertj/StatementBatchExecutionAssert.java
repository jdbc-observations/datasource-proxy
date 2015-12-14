package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.QueryType;
import net.ttddyy.dsproxy.test.StatementBatchExecution;
import org.assertj.core.api.AbstractCharSequenceAssert;
import org.assertj.core.api.AbstractListAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Index;

import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
// TODO: should this extend AbstractAssert??
public class StatementBatchExecutionAssert extends AbstractExecutionAssert<StatementBatchExecutionAssert, StatementBatchExecution> {

    public StatementBatchExecutionAssert(StatementBatchExecution actual) {
        super(actual, StatementBatchExecutionAssert.class);
    }

    public StatementBatchExecutionAssert isSuccess() {
        isExecutionSuccess();
        return this;
    }

    public StatementBatchExecutionAssert isFailure() {
        isExecutionFailure();
        return this;
    }

    public AbstractListAssert<?, ? extends List<? extends String>, String> queries() {
        return Assertions.assertThat(actual.getQueries());
    }

    public AbstractCharSequenceAssert<?, String> query(int index) {
        String query = actual.getQueries().get(index);
        return Assertions.assertThat(query);
    }

    public StatementBatchExecutionAssert contains(QueryType queryType, Index index) {
        // TODO: impl
        return this;
    }
}
