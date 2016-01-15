package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.QueryType;
import net.ttddyy.dsproxy.listener.QueryUtils;
import net.ttddyy.dsproxy.test.StatementBatchExecution;
import org.assertj.core.api.AbstractCharSequenceAssert;
import org.assertj.core.api.AbstractListAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Index;

import java.util.List;

/**
 * @author Tadaya Tsuyukubo
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

    public StatementBatchExecutionAssert hasBatchSize(int batchSize) {
        int actualSize = this.actual.getQueries().size();
        if (actualSize != batchSize) {
            failWithMessage("%nExpected batch size:<%s> but was:<%s> in batch statement executions%n", batchSize, actualSize);
        }
        return this;
    }

    public AbstractListAssert<?, ? extends List<? extends String>, String> queries() {
        return Assertions.assertThat(this.actual.getQueries());
    }

    public AbstractCharSequenceAssert<?, String> query(int index) {
        String query = this.actual.getQueries().get(index);
        return Assertions.assertThat(query);
    }

    public StatementBatchExecutionAssert contains(QueryType queryType, Index index) {
        return contains(queryType, index.value);
    }

    public StatementBatchExecutionAssert contains(QueryType queryType, int index) {
        String query = this.actual.getQueries().get(index);

        QueryType actualType = QueryUtils.getQueryType(query);
        if (actualType != queryType) {
            failWithMessage("%nExpected query type:<%s> but was:<%s> at index:<%d>%n", queryType, actualType, index);
        }
        return this;
    }

    public StatementBatchExecutionAssert hasSelectCount(int count) {
        return hasQueryCount(QueryType.SELECT, count);
    }

    public StatementBatchExecutionAssert hasInsertCount(int count) {
        return hasQueryCount(QueryType.INSERT, count);
    }

    public StatementBatchExecutionAssert hasUpdateCount(int count) {
        return hasQueryCount(QueryType.UPDATE, count);
    }

    public StatementBatchExecutionAssert hasDeleteCount(int count) {
        return hasQueryCount(QueryType.DELETE, count);
    }

    public StatementBatchExecutionAssert hasOtherCount(int count) {
        return hasQueryCount(QueryType.OTHER, count);
    }

    public StatementBatchExecutionAssert hasQueryCount(QueryType queryType, int count) {

        int matchedCount = 0;
        int selectCount = 0;
        int insertCount = 0;
        int updateCount = 0;
        int deleteCount = 0;
        int otherCount = 0;
        for (String query : this.actual.getQueries()) {
            QueryType actualQueryType = QueryUtils.getQueryType(query);
            switch (actualQueryType) {
                case SELECT:
                    selectCount++;
                    break;
                case INSERT:
                    insertCount++;
                    break;
                case UPDATE:
                    updateCount++;
                    break;
                case DELETE:
                    deleteCount++;
                    break;
                case OTHER:
                    otherCount++;
                    break;
            }
            if (queryType.equals(actualQueryType)) {
                matchedCount++;
            }
        }
        if (matchedCount != count) {
            String summary = String.format("select=%d, insert=%d, update=%d, delete=%d, other=%d",
                    selectCount, insertCount, updateCount, deleteCount, otherCount);
            failWithMessage("%nExpected %s count:<%d> but was:<%d> in:%n<%s>", queryType, count, matchedCount, summary);
        }

        return this;
    }
}
