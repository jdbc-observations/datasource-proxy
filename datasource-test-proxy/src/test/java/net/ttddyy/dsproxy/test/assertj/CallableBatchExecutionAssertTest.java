package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.CallableBatchExecution;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class CallableBatchExecutionAssertTest {

    @Test
    public void testIsSuccess() {
        CallableBatchExecution cbe = new CallableBatchExecution();

        // success case
        cbe.setSuccess(true);
        CallableBatchExecutionAssert cbeAssert = new CallableBatchExecutionAssert(cbe);
        cbeAssert.isSuccess();

        // failure case
        cbe.setSuccess(false);
        cbeAssert = new CallableBatchExecutionAssert(cbe);
        try {
            cbeAssert.isSuccess();
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: <Successful execution> but was: <Failure execution>\n");
        }

    }

    @Test
    public void testIsFailure() {
        CallableBatchExecution cbe = new CallableBatchExecution();

        // success case
        cbe.setSuccess(false);
        CallableBatchExecutionAssert cbeAssert = new CallableBatchExecutionAssert(cbe);
        cbeAssert.isFailure();

        // failure case
        cbe.setSuccess(true);
        cbeAssert = new CallableBatchExecutionAssert(cbe);
        try {
            cbeAssert.isFailure();
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: <Failure execution> but was: <Successful execution>\n");
        }

    }
}
