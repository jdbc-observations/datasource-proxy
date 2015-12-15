package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.BatchParameterHolder;
import org.assertj.core.api.AbstractAssert;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
class BatchParameterHolderAssert extends AbstractAssert<BatchParameterHolderAssert, BatchParameterHolder> {
    public BatchParameterHolderAssert(BatchParameterHolder actual) {
        super(actual, BatchParameterHolderAssert.class);
    }

    public void hasBatchSize(int batchSize, String batchType) {
        int actualSize = this.actual.getBatchExecutionEntries().size();
        if (actualSize != batchSize) {
            failWithMessage("%nExpected batch size:<%s> but was:<%s> in batch %s executions%n", batchSize, actualSize, batchType);
        }
    }


}
