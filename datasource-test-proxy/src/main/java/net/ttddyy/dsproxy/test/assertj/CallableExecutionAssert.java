package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.CallableExecution;
import org.assertj.core.api.AbstractAssert;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class CallableExecutionAssert extends AbstractAssert<CallableExecutionAssert, CallableExecution> {
    public CallableExecutionAssert(CallableExecution actual) {
        super(actual, CallableExecutionAssert.class);
    }
}
