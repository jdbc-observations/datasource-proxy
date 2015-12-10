package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.BatchExecutionEntry;
import net.ttddyy.dsproxy.test.PreparedBatchExecution;
import net.ttddyy.dsproxy.test.assertj.data.BatchParameters;
import org.assertj.core.api.AbstractAssert;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class PreparedBatchExecutionAssert extends AbstractAssert<PreparedBatchExecutionAssert, PreparedBatchExecution> {

    public PreparedBatchExecutionAssert(PreparedBatchExecution actual) {
        super(actual, PreparedBatchExecutionAssert.class);
    }

    // TODO: impl
    public PreparedBatchExecutionAssert batch(int batchIndex, BatchParameters params) {
        return this;
    }

    // TODO: impl
    public PreparedBatchEntryAssert batch(int batchIndex) {
        BatchExecutionEntry batchExecutionEntry = this.actual.getBatchExecutionEntries().get(batchIndex);
        return new PreparedBatchEntryAssert(batchExecutionEntry);
    }

}
