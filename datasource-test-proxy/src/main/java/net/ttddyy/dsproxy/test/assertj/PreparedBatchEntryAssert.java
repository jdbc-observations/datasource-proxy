package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.BatchExecutionEntry;
import net.ttddyy.dsproxy.test.assertj.data.ExecutionParameter;
import org.assertj.core.api.AbstractAssert;

/**
 * @author Tadaya Tsuyukubo
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


    public PreparedBatchEntryAssert containsParams(ExecutionParameter... params) {
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

    public PreparedBatchEntryAssert containsParamValuesExactly(Object... paramValues) {
        // TODO: impl
        return this;
    }

}
