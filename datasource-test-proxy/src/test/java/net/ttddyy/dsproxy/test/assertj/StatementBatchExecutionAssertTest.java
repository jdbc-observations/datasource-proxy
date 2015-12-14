package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.StatementBatchExecution;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class StatementBatchExecutionAssertTest {
    private StatementBatchExecution sbe;
    private StatementBatchExecutionAssert sbeAssert;

    @Before
    public void setUp() {
        this.sbe = new StatementBatchExecution();
        this.sbeAssert = new StatementBatchExecutionAssert(this.sbe);
    }

    @Test
    public void testIsSuccess() {
        // success case
        this.sbe.setSuccess(true);
        this.sbeAssert.isSuccess();

        // failure case
        this.sbe.setSuccess(false);
        try {
            this.sbeAssert.isSuccess();
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: <Successful execution> but was: <Failure execution>\n");
        }

    }

    @Test
    public void testIsFailure() {
        // success case
        this.sbe.setSuccess(false);
        this.sbeAssert.isFailure();

        // failure case
        this.sbe.setSuccess(true);
        try {
            this.sbeAssert.isFailure();
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: <Failure execution> but was: <Successful execution>\n");
        }

    }

}
