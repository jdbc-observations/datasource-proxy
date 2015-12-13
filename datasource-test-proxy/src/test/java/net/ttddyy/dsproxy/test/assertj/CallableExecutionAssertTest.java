package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.CallableExecution;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

/**
 * @author Tadaya Tsuyukubo
 */
public class CallableExecutionAssertTest {

    private CallableExecution ce;
    private CallableExecutionAssert ceAssert;

    @Before
    public void setUp() {
        this.ce = new CallableExecution();
        this.ceAssert = new CallableExecutionAssert(this.ce);
    }

    @Test
    public void testIsSuccess() {
        CallableExecution ce = new CallableExecution();

        // success case
        this.ce.setSuccess(true);
        this.ceAssert.isSuccess();

        // failure case
        this.ce.setSuccess(false);
        try {
            this.ceAssert.isSuccess();
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: <Successful execution> but was: <Failure execution>\n");
        }

    }

    @Test
    public void testIsFailure() {
        // success case
        this.ce.setSuccess(false);
        this.ceAssert.isFailure();

        // failure case
        this.ce.setSuccess(true);
        try {
            this.ceAssert.isFailure();
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: <Failure execution> but was: <Successful execution>\n");
        }

    }
}
