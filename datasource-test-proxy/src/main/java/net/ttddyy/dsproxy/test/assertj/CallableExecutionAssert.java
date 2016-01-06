package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.CallableExecution;
import net.ttddyy.dsproxy.test.assertj.data.ExecutionParameter;
import net.ttddyy.dsproxy.test.assertj.data.ExecutionParameters;
import net.ttddyy.dsproxy.test.assertj.helper.ExecutionParameterAsserts;

import java.sql.SQLType;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class CallableExecutionAssert extends AbstractExecutionAssert<CallableExecutionAssert, CallableExecution> {

    private ExecutionParameterAsserts parameterAsserts = new ExecutionParameterAsserts(this.info);

    public CallableExecutionAssert(CallableExecution actual) {
        super(actual, CallableExecutionAssert.class);
    }

    public CallableExecutionAssert isSuccess() {
        this.isExecutionSuccess();
        return this;
    }

    public CallableExecutionAssert isFailure() {
        this.isExecutionFailure();
        return this;
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


    //////////

    public CallableExecutionAssert containsParams(ExecutionParameter... params) {
        ExecutionParameters executionParameters = ExecutionParameters.containsParams(params);
        parameterAsserts.assertParameterKeys(this.actual, executionParameters, true);  // TODO: align the param order in method
        parameterAsserts.assertExecutionParameters(executionParameters, this.actual);
        return this;
    }

    public CallableExecutionAssert containsParamsExactly(ExecutionParameter... params) {
        ExecutionParameters executionParameters = ExecutionParameters.containsParamsExactly(params);
        parameterAsserts.assertParameterKeys(this.actual, executionParameters, true);
        parameterAsserts.assertExecutionParameters(executionParameters, this.actual);
        return this;
    }

    public CallableExecutionAssert containsParamKey(Object paramKey) {
        containsParamKeys(paramKey);
        return this;
    }

    public CallableExecutionAssert containsParamKeys(Object... paramKeys) {
        ExecutionParameters executionParameters = ExecutionParameters.containsParamKeys(paramKeys);
        parameterAsserts.assertParameterKeys(this.actual, executionParameters, true);
        return this;
    }

    public CallableExecutionAssert containsParamIndex(int paramIndexe) {
        containsParamIndexes(paramIndexe);
        return this;
    }

    public CallableExecutionAssert containsParamIndexes(int... paramIndexes) {
        ExecutionParameters executionParameters = ExecutionParameters.containsParamIndexes(paramIndexes);
        parameterAsserts.assertParameterKeys(this.actual, executionParameters, true);
        return this;
    }

    public CallableExecutionAssert containsParamName(String paramName) {
        containsParamNames(paramName);
        return this;
    }

    public CallableExecutionAssert containsParamNames(String... paramNames) {
        ExecutionParameters executionParameters = ExecutionParameters.containsParamNames(paramNames);
        parameterAsserts.assertParameterKeys(this.actual, executionParameters, true);
        return this;
    }

}
