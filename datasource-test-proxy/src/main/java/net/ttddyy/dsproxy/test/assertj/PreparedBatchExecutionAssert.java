package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.BatchExecutionEntry;
import net.ttddyy.dsproxy.test.PreparedBatchExecution;
import net.ttddyy.dsproxy.test.assertj.data.ExecutionParameters;
import net.ttddyy.dsproxy.test.assertj.helper.BatchExecutionEntryAsserts;
import net.ttddyy.dsproxy.test.assertj.helper.ExecutionParameterAsserts;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class PreparedBatchExecutionAssert extends AbstractExecutionAssert<PreparedBatchExecutionAssert, PreparedBatchExecution> {

    private BatchExecutionEntryAsserts batchAssert = new BatchExecutionEntryAsserts(this.info);
    private ExecutionParameterAsserts parameterAssert = new ExecutionParameterAsserts(this.info);

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

    public PreparedBatchExecutionAssert hasBatchSize(int batchSize) {
        this.batchAssert.assertBatchSize(this.actual, batchSize, "prepared");
        return this;
    }


    public PreparedBatchExecutionAssert batch(int batchIndex, ExecutionParameters params) {

        this.batchAssert.assertBatchExecutionEntry(this.actual, batchIndex, PreparedBatchExecution.PreparedBatchExecutionEntry.class);

        // entry is validated to be the one for prepared
        PreparedBatchExecution.PreparedBatchExecutionEntry batchEntry = (PreparedBatchExecution.PreparedBatchExecutionEntry) this.actual.getBatchExecutionEntries().get(batchIndex);
        this.parameterAssert.assertParameterKeys(batchEntry, params, false);

        if (ExecutionParameters.ExecutionParametersType.CONTAINS_KEYS_ONLY == params.getType()) {
            return this;  // only check keys
        }

        // validate key-value pairs
        parameterAssert.assertExecutionParameters(batchEntry, params);

        return this;
    }

    // TODO: impl
    public PreparedBatchEntryAssert batch(int batchIndex) {
        BatchExecutionEntry batchExecutionEntry = this.actual.getBatchExecutionEntries().get(batchIndex);
        return new PreparedBatchEntryAssert(batchExecutionEntry);
    }

}
