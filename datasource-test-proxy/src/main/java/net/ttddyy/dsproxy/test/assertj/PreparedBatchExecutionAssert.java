package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.BatchExecutionEntry;
import net.ttddyy.dsproxy.test.PreparedBatchExecution;
import net.ttddyy.dsproxy.test.assertj.data.ExecutionParameters;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class PreparedBatchExecutionAssert extends AbstractExecutionAssert<PreparedBatchExecutionAssert, PreparedBatchExecution> {

    public PreparedBatchExecutionAssert(PreparedBatchExecution actual) {
        super(actual, PreparedBatchExecutionAssert.class);
    }

    public PreparedBatchExecutionAssert isSuccess() {
        isExecutionSuccess();
        return this;
    }

    public PreparedBatchExecutionAssert isFailure() {
        isExecutionFailure();
        return this;
    }

    // TODO: impl
    public PreparedBatchExecutionAssert hasBatchSize(int batchSize) {
        return this;
    }

    // TODO: impl
    public PreparedBatchExecutionAssert batch(int batchIndex, ExecutionParameters params) {
        return this;
    }

    // TODO: impl
    public PreparedBatchEntryAssert batch(int batchIndex) {
        BatchExecutionEntry batchExecutionEntry = this.actual.getBatchExecutionEntries().get(batchIndex);
        return new PreparedBatchEntryAssert(batchExecutionEntry);
    }

}
