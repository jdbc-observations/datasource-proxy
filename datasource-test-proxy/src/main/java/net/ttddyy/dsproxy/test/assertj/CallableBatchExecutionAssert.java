package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.BatchExecutionEntry;
import net.ttddyy.dsproxy.test.CallableBatchExecution;
import net.ttddyy.dsproxy.test.assertj.data.ExecutionParameters;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
// TODO: should this extend AbstractAssert??
public class CallableBatchExecutionAssert extends AbstractExecutionAssert<CallableBatchExecutionAssert, CallableBatchExecution> {
    public CallableBatchExecutionAssert(CallableBatchExecution actual) {
        super(actual, CallableBatchExecutionAssert.class);
    }

    public CallableBatchExecutionAssert isSuccess() {
        isExecutionSuccess();
        return this;
    }

    public CallableBatchExecutionAssert isFailure() {
        isExecutionFailure();
        return this;
    }

    public CallableBatchExecutionAssert hasBatchSize(int batchSize) {
        return this;
    }

    // TODO: impl
    public CallableBatchExecutionAssert batch(int batchIndex, ExecutionParameters params) {
        return this;
    }

    // TODO: impl
    public CallableBatchEntryAssert batch(int batchIndex) {
        BatchExecutionEntry batchExecutionEntry = this.actual.getBatchExecutionEntries().get(batchIndex);
        return new CallableBatchEntryAssert(batchExecutionEntry);
    }


}
