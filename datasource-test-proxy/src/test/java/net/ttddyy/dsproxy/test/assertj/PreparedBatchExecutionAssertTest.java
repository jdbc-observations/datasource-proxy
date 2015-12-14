package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.PreparedBatchExecution;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class PreparedBatchExecutionAssertTest {
    private PreparedBatchExecution pbe;
    private PreparedBatchExecutionAssert pbeAssert;

    @Before
    public void setUp() {
        this.pbe = new PreparedBatchExecution();
        this.pbeAssert = new PreparedBatchExecutionAssert(this.pbe);
    }

    @Test
    public void testIsSuccess() {
        // success case
        this.pbe.setSuccess(true);
        this.pbeAssert.isSuccess();

        // failure case
        this.pbe.setSuccess(false);
        try {
            this.pbeAssert.isSuccess();
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: <Successful execution> but was: <Failure execution>\n");
        }

    }

    @Test
    public void testIsFailure() {
        // success case
        this.pbe.setSuccess(false);
        this.pbeAssert.isFailure();

        // failure case
        this.pbe.setSuccess(true);
        try {
            this.pbeAssert.isFailure();
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: <Failure execution> but was: <Successful execution>\n");
        }

    }

}
