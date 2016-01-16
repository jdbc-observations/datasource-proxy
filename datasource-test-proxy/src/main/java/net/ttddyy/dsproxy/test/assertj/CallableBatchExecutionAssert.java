package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.CallableBatchExecution;
import net.ttddyy.dsproxy.test.assertj.data.ExecutionParameters;
import net.ttddyy.dsproxy.test.assertj.helper.BatchExecutionEntryAsserts;
import net.ttddyy.dsproxy.test.assertj.helper.ExecutionParameterAsserts;

/**
 * @author Tadaya Tsuyukubo
 */
// TODO: should this extend AbstractAssert??
public class CallableBatchExecutionAssert extends AbstractExecutionAssert<CallableBatchExecutionAssert, CallableBatchExecution> {

    private BatchExecutionEntryAsserts batchAssert = new BatchExecutionEntryAsserts(this.info);
    private ExecutionParameterAsserts parameterAssert = new ExecutionParameterAsserts(this.info);

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
        this.batchAssert.assertBatchSize(this.actual, batchSize, "callable");
        return this;
    }


    public CallableBatchExecutionAssert batch(int batchIndex, ExecutionParameters params) {

        this.batchAssert.assertBatchExecutionEntry(this.actual, batchIndex, CallableBatchExecution.CallableBatchExecutionEntry.class);

        // entry is validated to be the one for prepared
        CallableBatchExecution.CallableBatchExecutionEntry batchEntry = (CallableBatchExecution.CallableBatchExecutionEntry) this.actual.getBatchExecutionEntries().get(batchIndex);
        this.parameterAssert.assertParameterKeys(batchEntry, params, true);


        if (ExecutionParameters.ExecutionParametersType.CONTAINS_KEYS_ONLY == params.getType()) {
            return this;  // only check keys
        }

        // validate key-value pairs
        parameterAssert.assertExecutionParameters(batchEntry, params);

        return this;
    }

}
