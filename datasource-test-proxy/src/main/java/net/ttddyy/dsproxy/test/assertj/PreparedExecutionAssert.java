package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.PreparedExecution;
import net.ttddyy.dsproxy.test.assertj.data.ExecutionParameter;
import org.assertj.core.api.AbstractAssert;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class PreparedExecutionAssert extends AbstractAssert<PreparedExecutionAssert, PreparedExecution> {

    public PreparedExecutionAssert(PreparedExecution actual) {
        super(actual, PreparedExecutionAssert.class);
    }

    public PreparedExecutionAssert aa() {
        new ParameterByIndexHolderAssertion(this.actual).foo();
        return this;
    }

    public PreparedExecutionAssert bb() {
//        new QueryHolderAssert(this.actual).foo("abc");
        return this;
    }

    public PreparedExecutionAssert isSuccess() {
        // TODO: impl
        return this;
    }

    public PreparedExecutionAssert isFailure() {
        // TODO: impl
        return this;
    }


    public PreparedExecutionAssert containsParam(int paramIndex, Object value) {
        // TODO: impl
        return this;
    }


    public PreparedExecutionAssert containsNullParam(int index, int sqlType) {
        // TODO: impl
        return this;
    }

    public PreparedExecutionAssert containsNullParam(int index) {
        // TODO: impl
        return this;
    }


    public PreparedExecutionAssert containsParams(ExecutionParameter... params) {
        // TODO: impl
        return this;
    }

    public PreparedExecutionAssert containsParamIndex(int paramIndexe) {
        // TODO: impl
        return this;
    }

    public PreparedExecutionAssert containsParamIndexes(int... paramIndexes) {
        // TODO: impl
        return this;
    }

    public PreparedExecutionAssert containsParamValuesExactly(Object... paramValues) {
        // TODO: impl
        return this;
    }

}
