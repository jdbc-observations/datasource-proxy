package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.StatementExecution;
import org.assertj.core.api.AbstractAssert;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class StatementExecutionAssert extends AbstractAssert<StatementExecutionAssert, StatementExecution> {

    public StatementExecutionAssert(StatementExecution actual) {
        super(actual, StatementExecutionAssert.class);
    }

    public QueryHolderAssert query() {
        return new QueryHolderAssert(actual);
    }

}
