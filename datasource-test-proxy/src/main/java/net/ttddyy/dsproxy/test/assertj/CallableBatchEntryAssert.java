package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.BatchExecutionEntry;
import net.ttddyy.dsproxy.test.assertj.data.BatchParameter;
import org.assertj.core.api.AbstractAssert;

import java.sql.SQLType;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class CallableBatchEntryAssert extends AbstractAssert<CallableBatchEntryAssert, BatchExecutionEntry> {

    public CallableBatchEntryAssert(BatchExecutionEntry actual) {
        super(actual, CallableBatchEntryAssert.class);
    }

    public CallableBatchEntryAssert containsParam(int paramIndex, Object value) {
        return this;
    }

    public CallableBatchEntryAssert containsParam(String paramName, Object value) {
        return this;
    }

    public CallableBatchEntryAssert containsOutParam(int paramIndex, int sqlType) {
        return this;
    }

    public CallableBatchEntryAssert containsOutParam(int paramIndex, SQLType sqlType) {
        return this;
    }

    public CallableBatchEntryAssert containsOutParam(String paramName, int sqlType) {
        return this;
    }

    public CallableBatchEntryAssert containsOutParam(String paramName, SQLType sqlType) {
        return this;
    }

    public CallableBatchEntryAssert containsNullParam(int index, int sqlType) {
        return this;
    }

    public CallableBatchEntryAssert containsNullParam(int index) {
        return this;
    }

    public CallableBatchEntryAssert containsNullParam(String name, int sqlType) {
        return this;
    }

    public CallableBatchEntryAssert containsNullParam(String name) {
        return this;
    }


    public CallableBatchEntryAssert containsParams(BatchParameter... params) {
        return this;
    }

    public CallableBatchEntryAssert containsParamKey(Object params) {
        // TODO: they should be either string or int
        return this;
    }

    public CallableBatchEntryAssert containsParamKeys(Object... params) {
        // TODO: they should be either string or int
        return this;
    }

    public CallableBatchEntryAssert containsParamIndex(int paramIndexe) {
        return this;
    }

    public CallableBatchEntryAssert containsParamIndexes(int... paramIndexes) {
        return this;
    }

    public CallableBatchEntryAssert containsParamName(String paramName) {
        return this;
    }

    public CallableBatchEntryAssert containsParamNames(String... paramNames) {
        return this;
    }

}
