package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.PreparedExecution;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class PreparedExecutionAssertTest {

    private PreparedExecution pe;
    private PreparedExecutionAssert peAssert;

    @Before
    public void setUp() {
        this.pe = new PreparedExecution();
        this.peAssert = new PreparedExecutionAssert(this.pe);
    }

    @Test
    public void testIsSuccess() {
        PreparedExecution ce = new PreparedExecution();

        // success case
        this.pe.setSuccess(true);
        this.peAssert.isSuccess();

        // failure case
        this.pe.setSuccess(false);
        try {
            this.peAssert.isSuccess();
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: <Successful execution> but was: <Failure execution>\n");
        }

    }

    @Test
    public void testIsFailure() {
        // success case
        this.pe.setSuccess(false);
        this.peAssert.isFailure();

        // failure case
        this.pe.setSuccess(true);
        try {
            this.peAssert.isFailure();
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: <Failure execution> but was: <Successful execution>\n");
        }

    }
}
