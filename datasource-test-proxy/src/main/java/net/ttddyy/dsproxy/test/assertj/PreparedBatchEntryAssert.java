package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.BatchExecutionEntry;
import net.ttddyy.dsproxy.test.assertj.data.BatchParameter;
import org.assertj.core.api.AbstractAssert;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class PreparedBatchEntryAssert extends AbstractAssert<PreparedBatchEntryAssert, BatchExecutionEntry> {

    public PreparedBatchEntryAssert(BatchExecutionEntry actual) {
        super(actual, PreparedBatchEntryAssert.class);
    }

    public PreparedBatchEntryAssert containsParam(int paramIndex, Object value) {
        // TODO: impl
        return this;
    }


    public PreparedBatchEntryAssert containsNullParam(int index, int sqlType) {
        // TODO: impl
        return this;
    }

    public PreparedBatchEntryAssert containsNullParam(int index) {
        // TODO: impl
        return this;
    }


    public PreparedBatchEntryAssert containsParams(BatchParameter... params) {
        // TODO: impl
        return this;
    }

    public PreparedBatchEntryAssert containsParamIndex(int paramIndexe) {
        // TODO: impl
        return this;
    }

    public PreparedBatchEntryAssert containsParamIndexes(int... paramIndexes) {
        // TODO: impl
        return this;
    }

}
