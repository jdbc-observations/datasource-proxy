package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.BaseQueryExecution;
import org.assertj.core.api.AbstractAssert;

import static org.assertj.core.error.ShouldBeEqual.shouldBeEqual;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class BaseQueryExecutionAssert extends AbstractAssert<BaseQueryExecutionAssert, BaseQueryExecution> {

    public BaseQueryExecutionAssert(BaseQueryExecution actual) {
        super(actual, BaseQueryExecutionAssert.class);
    }

    public BaseQueryExecutionAssert success() {
        // TODO: impl
        shouldBeEqual(actual.isSuccess(), true, info.representation());
        if (!actual.isSuccess()) {
            failWithMessage("Expected");
        }
        return this;
    }
}
