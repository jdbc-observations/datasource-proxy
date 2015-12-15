package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.StatementBatchExecution;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

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

    @Test
    public void testHasBatchSize() {
        List<String> entries = new ArrayList<String>();
        entries.addAll(Arrays.asList("a", "b", "c"));

        StatementBatchExecution pbe = mock(StatementBatchExecution.class);
        given(pbe.getQueries()).willReturn(entries);

        DataSourceProxyAssertions.assertThat(pbe).hasBatchSize(3);

        try {
            DataSourceProxyAssertions.assertThat(pbe).hasBatchSize(1);
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected batch size:<1> but was:<3> in batch statement executions\n");
        }
    }


}
