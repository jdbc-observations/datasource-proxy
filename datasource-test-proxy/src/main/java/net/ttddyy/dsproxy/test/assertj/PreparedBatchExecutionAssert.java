package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.PreparedBatchExecution;
import org.assertj.core.api.AbstractAssert;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class PreparedBatchExecutionAssert extends AbstractAssert<PreparedBatchExecutionAssert, PreparedBatchExecution> {

    public PreparedBatchExecutionAssert(PreparedBatchExecution actual) {
        super(actual, PreparedBatchExecutionAssert.class);
    }


}
