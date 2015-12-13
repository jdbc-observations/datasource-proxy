package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.QueryExecution;
import org.assertj.core.api.AbstractAssert;

/**
 * Shared assertion methods.
 *
 * @author Tadaya Tsuyukubo
 * @see StatementExecutionAssert
 * @see StatementBatchExecutionAssert
 * @see PreparedExecutionAssert
 * @see PreparedBatchExecutionAssert
 * @see CallableExecutionAssert
 * @see CallableBatchExecutionAssert
 * @since 1.4
 */
public abstract class AbstractExecutionAssert<S extends AbstractAssert<S, A>, A extends QueryExecution> extends AbstractAssert<S, A> {
    public AbstractExecutionAssert(A actual, Class<?> selfType) {
        super(actual, selfType);
    }

    protected void isExecutionSuccess() {
        if (!actual.isSuccess()) {
            failWithMessage("%nExpecting: <%s> but was: <%s>%n", "Successful execution", "Failure execution");
        }
    }

    protected void isExecutionFailure() {
        if (actual.isSuccess()) {
            failWithMessage("%nExpecting: <%s> but was: <%s>%n", "Failure execution", "Successful execution");
        }
    }

}
