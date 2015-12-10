package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.CallableExecution;
import net.ttddyy.dsproxy.test.assertj.data.BatchParameter;
import org.assertj.core.api.AbstractAssert;

import java.sql.SQLType;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class CallableExecutionAssert extends AbstractAssert<CallableExecutionAssert, CallableExecution> {
    public CallableExecutionAssert(CallableExecution actual) {
        super(actual, CallableExecutionAssert.class);
    }

    public CallableExecutionAssert containsParam(int paramIndex, Object value) {
        return this;
    }

    public CallableExecutionAssert containsParam(String paramName, Object value) {
        return this;
    }

    public CallableExecutionAssert containsOutParam(int paramIndex, int sqlType) {
        return this;
    }

    public CallableExecutionAssert containsOutParam(int paramIndex, SQLType sqlType) {
        return this;
    }

    public CallableExecutionAssert containsOutParam(String paramName, int sqlType) {
        return this;
    }

    public CallableExecutionAssert containsOutParam(String paramName, SQLType sqlType) {
        return this;
    }

    public CallableExecutionAssert containsNullParam(int index, int sqlType) {
        return this;
    }

    public CallableExecutionAssert containsNullParam(int index) {
        return this;
    }

    public CallableExecutionAssert containsNullParam(String name, int sqlType) {
        return this;
    }

    public CallableExecutionAssert containsNullParam(String name) {
        return this;
    }


    public CallableExecutionAssert containsParams(BatchParameter... params) {
        return this;
    }

    public CallableExecutionAssert containsParamKey(Object params) {
        // TODO: they should be either string or int
        return this;
    }

    public CallableExecutionAssert containsParamKeys(Object... params) {
        // TODO: they should be either string or int
        return this;
    }

    public CallableExecutionAssert containsParamIndex(int paramIndexe) {
        return this;
    }

    public CallableExecutionAssert containsParamIndexes(int... paramIndexes) {
        return this;
    }

    public CallableExecutionAssert containsParamName(String paramName) {
        return this;
    }

    public CallableExecutionAssert containsParamNames(String... paramNames) {
        return this;
    }

}
