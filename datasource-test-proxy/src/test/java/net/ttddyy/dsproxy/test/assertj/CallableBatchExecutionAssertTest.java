package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.CallableBatchExecution;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class CallableBatchExecutionAssertTest {

    private CallableBatchExecution cbe;
    private CallableBatchExecutionAssert cbeAssert;

    @Before
    public void setUp() {
        this.cbe = new CallableBatchExecution();
        this.cbeAssert = new CallableBatchExecutionAssert(this.cbe);
    }

    @Test
    public void testIsSuccess() {
        // success case
        this.cbe.setSuccess(true);
        this.cbeAssert.isSuccess();

        // failure case
        this.cbe.setSuccess(false);
        try {
            this.cbeAssert.isSuccess();
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: <Successful execution> but was: <Failure execution>\n");
        }

    }

    @Test
    public void testIsFailure() {
        // success case
        this.cbe.setSuccess(false);
        this.cbeAssert.isFailure();

        // failure case
        this.cbe.setSuccess(true);
        try {
            this.cbeAssert.isFailure();
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: <Failure execution> but was: <Successful execution>\n");
        }

    }
}
