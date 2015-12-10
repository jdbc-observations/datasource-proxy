package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.BatchExecutionEntry;
import net.ttddyy.dsproxy.test.CallableBatchExecution;
import net.ttddyy.dsproxy.test.assertj.data.BatchParameters;
import org.assertj.core.api.AbstractAssert;

import java.sql.SQLType;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
// TODO: should this extend AbstractAssert??
public class CallableBatchExecutionAssert extends AbstractAssert<CallableBatchExecutionAssert, CallableBatchExecution> {
    public CallableBatchExecutionAssert(CallableBatchExecution actual) {
        super(actual, CallableBatchExecutionAssert.class);
    }

    // TODO: impl
    public CallableBatchExecutionAssert batch(int batchIndex, BatchParameters params) {
        return this;
    }

    // TODO: impl
    public CallableBatchEntryAssert batch(int batchIndex) {
        BatchExecutionEntry batchExecutionEntry = this.actual.getBatchExecutionEntries().get(batchIndex);
        return new CallableBatchEntryAssert(batchExecutionEntry);
    }


}
