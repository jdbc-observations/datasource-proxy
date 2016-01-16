package net.ttddyy.dsproxy.test.assertj.helper;

import net.ttddyy.dsproxy.test.BatchExecutionEntry;
import net.ttddyy.dsproxy.test.BatchParameterHolder;
import net.ttddyy.dsproxy.test.ParameterHolder;
import org.assertj.core.api.WritableAssertionInfo;

import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 */
// TODO: better name
public class BatchExecutionEntryAsserts extends AbstractHelperAsserts {

    public BatchExecutionEntryAsserts(WritableAssertionInfo info) {
        super(info);
    }

    public void assertBatchSize(BatchParameterHolder batchParameterHolder, int batchSize, String batchType) {
        int actualSize = batchParameterHolder.getBatchExecutionEntries().size();
        if (actualSize != batchSize) {
            failWithMessage("%nExpected batch size:<%s> but was:<%s> in batch %s executions%n", batchSize, actualSize, batchType);
        }
    }

    public void assertBatchExecutionEntry(BatchParameterHolder batchParameterHolder, int batchIndex, Class<? extends ParameterHolder> batchExecutionEntryClass) {
        // validate batch index size
        List<BatchExecutionEntry> batchEntries = batchParameterHolder.getBatchExecutionEntries();
        int batchSize = batchEntries.size();

        if (batchIndex < 0) {
            String message = String.format("\nExpecting: batch index <%d> should be greater than equal to <0>", batchIndex);
            failWithMessage(message);
        } else if (batchSize <= batchIndex) {
            String message = String.format("\nExpecting: batch index <%d> is too big for the batch size <%d>", batchIndex, batchSize);
            failWithMessage(message);
        }

        // validate batch execution entry type
        BatchExecutionEntry batchEntry = batchParameterHolder.getBatchExecutionEntries().get(batchIndex);
        if (!(batchEntry.getClass().isAssignableFrom(batchExecutionEntryClass))) {
            failWithMessage("\nExpecting: batch entry\n<%s>\nbut was\n<%s>",
                    batchExecutionEntryClass.getSimpleName(),
                    batchEntry.getClass().getSimpleName());
        }
    }

}
