package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.BatchExecutionEntry;
import net.ttddyy.dsproxy.test.PreparedBatchExecution;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

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

    @Test
    public void testHasBatchSize() {
        BatchExecutionEntry entry = mock(BatchExecutionEntry.class);

        ArrayList<BatchExecutionEntry> entries = new ArrayList<BatchExecutionEntry>();
        entries.addAll(Arrays.asList(entry, entry, entry));

        PreparedBatchExecution pbe = mock(PreparedBatchExecution.class);
        given(pbe.getBatchExecutionEntries()).willReturn(entries);

        DataSourceProxyAssertions.assertThat(pbe).hasBatchSize(3);

        try {
            DataSourceProxyAssertions.assertThat(pbe).hasBatchSize(1);
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected batch size:<1> but was:<3> in batch prepared executions\n");
        }
    }

}
