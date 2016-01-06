package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.proxy.ParameterKey;
import net.ttddyy.dsproxy.test.BatchExecutionEntry;
import net.ttddyy.dsproxy.test.CallableBatchExecution;
import net.ttddyy.dsproxy.test.PreparedBatchExecution;
import org.junit.Before;
import org.junit.Test;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;

import static net.ttddyy.dsproxy.test.assertj.data.ExecutionParameter.nullParam;
import static net.ttddyy.dsproxy.test.assertj.data.ExecutionParameter.param;
import static net.ttddyy.dsproxy.test.assertj.data.ExecutionParameters.containsParamIndexes;
import static net.ttddyy.dsproxy.test.assertj.data.ExecutionParameters.containsParamKeys;
import static net.ttddyy.dsproxy.test.assertj.data.ExecutionParameters.containsParams;
import static net.ttddyy.dsproxy.test.assertj.data.ExecutionParameters.containsParamsExactly;
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

    @Test
    public void batchWithContainsParams() {
        PreparedBatchExecution.PreparedBatchExecutionEntry entry = new PreparedBatchExecution.PreparedBatchExecutionEntry();
        entry.getParams().put(new ParameterKey(1), "foo");
        entry.getParams().put(new ParameterKey(2), "bar");

        ArrayList<BatchExecutionEntry> entries = new ArrayList<BatchExecutionEntry>();
        entries.add(entry);

        PreparedBatchExecution pbe = mock(PreparedBatchExecution.class);
        given(pbe.getBatchExecutionEntries()).willReturn(entries);

        // successful call
        DataSourceProxyAssertions.assertThat(pbe).batch(0, containsParams(param(1, "foo"), param(2, "bar")));

        // index is too small
        try {
            DataSourceProxyAssertions.assertThat(pbe).batch(-1, containsParams(param(1, "foo")));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: batch index <-1> should be greater than equal to <0>");
        }

        // index is too big
        try {
            DataSourceProxyAssertions.assertThat(pbe).batch(1, containsParams(param(1, "foo")));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: batch index <1> is too big for the batch size <1>");
        }

        // value is wrong for index
        try {
            DataSourceProxyAssertions.assertThat(pbe).batch(0, containsParams(param(1, "fooABC")));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: parameters \n<{1=foo, 2=bar}>\nto contain:\n<[1=fooABC]>\nbut could not find:\n<[1=fooABC]>");
        }

        // no param index key
        try {
            DataSourceProxyAssertions.assertThat(pbe).batch(0, containsParams(param(100, "bar")));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: prepared parameter keys\n<[1, 2]>\nto contain:\n<[100]>\nbut could not find:\n<[100]>");
        }

        // name key (should only for callable)
        try {
            DataSourceProxyAssertions.assertThat(pbe).batch(0, containsParams(param("foo", "FOO")));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: prepared parameter keys\n<[1, 2]>\nto contain:\n<[foo]>\nbut could not find:\n<[foo]>");
        }
    }

    @Test
    public void containsParamsWithSetNullParameters() {
        PreparedBatchExecution.PreparedBatchExecutionEntry entry = new PreparedBatchExecution.PreparedBatchExecutionEntry();
        entry.getSetNullParams().put(new ParameterKey(1), Types.VARCHAR);
        entry.getSetNullParams().put(new ParameterKey(2), Types.DATE);

        ArrayList<BatchExecutionEntry> entries = new ArrayList<BatchExecutionEntry>();
        entries.add(entry);

        PreparedBatchExecution pbe = mock(PreparedBatchExecution.class);
        given(pbe.getBatchExecutionEntries()).willReturn(entries);

        // successful call
        DataSourceProxyAssertions.assertThat(pbe).batch(0, containsParams(nullParam(1, Types.VARCHAR), nullParam(2, Types.DATE)));

        // value is wrong for index
        try {
            DataSourceProxyAssertions.assertThat(pbe).batch(0, containsParams(nullParam(1, Types.ARRAY)));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: parameters \n<{1=NULL(VARCHAR), 2=NULL(DATE)}>\nto contain:\n<[1=NULL(ARRAY)]>\nbut could not find:\n<[1=NULL(ARRAY)]>");
        }

        try {
            DataSourceProxyAssertions.assertThat(pbe).batch(0, containsParams(param(1, 12))); // Types.VARCHAR == 12
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: parameters \n<{1=NULL(VARCHAR), 2=NULL(DATE)}>\nto contain:\n<[1=12]>\nbut could not find:\n<[1=12]>");
        }
    }

    @Test
    public void containsParamsWithSetNullParametersOnlyKeys() {
        PreparedBatchExecution.PreparedBatchExecutionEntry entry = new PreparedBatchExecution.PreparedBatchExecutionEntry();
        entry.getSetNullParams().put(new ParameterKey(1), Types.VARCHAR);
        entry.getSetNullParams().put(new ParameterKey(2), Types.DATE);

        ArrayList<BatchExecutionEntry> entries = new ArrayList<BatchExecutionEntry>();
        entries.add(entry);

        PreparedBatchExecution pbe = mock(PreparedBatchExecution.class);
        given(pbe.getBatchExecutionEntries()).willReturn(entries);

        // successful call
        DataSourceProxyAssertions.assertThat(pbe).batch(0, containsParams(nullParam(1), nullParam(2)));

        // index key doesn't exist
        try {
            DataSourceProxyAssertions.assertThat(pbe).batch(0, containsParams(nullParam(100)));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: prepared parameter keys\n<[1, 2]>\nto contain:\n<[100]>\nbut could not find:\n<[100]>");
        }

        // name key doesn't exist
        try {
            DataSourceProxyAssertions.assertThat(pbe).batch(0, containsParams(nullParam("BAR")));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: prepared parameter keys\n<[1, 2]>\nto contain:\n<[BAR]>\nbut could not find:\n<[BAR]>");
        }

    }

    @Test
    public void containsParamsWithMixedParameterTypes() {
        PreparedBatchExecution.PreparedBatchExecutionEntry entry = new PreparedBatchExecution.PreparedBatchExecutionEntry();
        entry.getParams().put(new ParameterKey(1), "foo");
        entry.getParams().put(new ParameterKey(10), "FOO");
        entry.getSetNullParams().put(new ParameterKey(2), Types.VARCHAR);
        entry.getSetNullParams().put(new ParameterKey(20), Types.DATE);

        ArrayList<BatchExecutionEntry> entries = new ArrayList<BatchExecutionEntry>();
        entries.add(entry);

        PreparedBatchExecution pbe = mock(PreparedBatchExecution.class);
        given(pbe.getBatchExecutionEntries()).willReturn(entries);

        // successful call
        DataSourceProxyAssertions.assertThat(pbe).batch(0, containsParams(param(1, "foo"), nullParam(2, Types.VARCHAR)));

        try {
            DataSourceProxyAssertions.assertThat(pbe).batch(0, containsParams(nullParam(1, Types.ARRAY)));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: parameters \n<{1=foo, 10=FOO, 2=NULL(VARCHAR), 20=NULL(DATE)}>\nto contain:\n<[1=NULL(ARRAY)]>\nbut could not find:\n<[1=NULL(ARRAY)]>");
        }
    }

    @Test
    public void batchParamKeysWithContainsParamsExactly() {
        PreparedBatchExecution.PreparedBatchExecutionEntry entry = new PreparedBatchExecution.PreparedBatchExecutionEntry();
        entry.getParams().put(new ParameterKey(1), "foo");
        entry.getParams().put(new ParameterKey(2), "BAR");

        ArrayList<BatchExecutionEntry> entries = new ArrayList<BatchExecutionEntry>();
        entries.add(entry);

        PreparedBatchExecution pbe = mock(PreparedBatchExecution.class);
        given(pbe.getBatchExecutionEntries()).willReturn(entries);

        // successful case
        DataSourceProxyAssertions.assertThat(pbe).batch(0, containsParamsExactly(param(1, "foo"), param(2, "BAR")));

        // missing one param key (index)
        try {
            DataSourceProxyAssertions.assertThat(pbe).batch(0, containsParamsExactly(param(1, "foo")));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: callable parameter keys\n<[1, 2]>\nto be exactly:\n<[1]>\nbut missing keys:\n<[]>\nextra keys:\n<[2]>");
        }

    }

    @Test
    public void batchParamKeysWithContainsParamsExactlyForSetNullParameters() {
        PreparedBatchExecution.PreparedBatchExecutionEntry entry = new PreparedBatchExecution.PreparedBatchExecutionEntry();
        entry.getSetNullParams().put(new ParameterKey(1), Types.VARCHAR);
        entry.getSetNullParams().put(new ParameterKey(2), Types.DATE);

        ArrayList<BatchExecutionEntry> entries = new ArrayList<BatchExecutionEntry>();
        entries.add(entry);

        PreparedBatchExecution pbe = mock(PreparedBatchExecution.class);
        given(pbe.getBatchExecutionEntries()).willReturn(entries);

        // successful case
        DataSourceProxyAssertions.assertThat(pbe).batch(0, containsParamsExactly(nullParam(1, Types.VARCHAR), nullParam(2, Types.DATE)));

        // missing one param key (index)
        try {
            DataSourceProxyAssertions.assertThat(pbe).batch(0, containsParamsExactly(nullParam(1, Types.VARCHAR)));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: callable parameter keys\n<[1, 2]>\nto be exactly:\n<[1]>\nbut missing keys:\n<[]>\nextra keys:\n<[2]>");
        }

    }


    @Test
    public void testBatchParamKeyIndexes() {
        PreparedBatchExecution.PreparedBatchExecutionEntry entry = new PreparedBatchExecution.PreparedBatchExecutionEntry();
        entry.getParams().put(new ParameterKey(1), "foo");
        entry.getSetNullParams().put(new ParameterKey(2), Types.VARCHAR);

        ArrayList<BatchExecutionEntry> entries = new ArrayList<BatchExecutionEntry>();
        entries.add(entry);

        PreparedBatchExecution pbe = mock(PreparedBatchExecution.class);
        given(pbe.getBatchExecutionEntries()).willReturn(entries);

        // successful case
        DataSourceProxyAssertions.assertThat(pbe).batch(0, containsParamIndexes(1, 2));
        DataSourceProxyAssertions.assertThat(pbe).batch(0, containsParamIndexes(2, 1));
        DataSourceProxyAssertions.assertThat(pbe).batch(0, containsParamIndexes(2));

        // missing one param key (index)
        try {
            DataSourceProxyAssertions.assertThat(pbe).batch(0, containsParamIndexes(1, 2, 100));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: prepared parameter keys\n<[1, 2]>\nto contain:\n<[1, 2, 100]>\nbut could not find:\n<[100]>");
        }

    }

    @Test
    public void testBatchParamKeys() {
        PreparedBatchExecution.PreparedBatchExecutionEntry entry = new PreparedBatchExecution.PreparedBatchExecutionEntry();
        entry.getParams().put(new ParameterKey(1), "foo");
        entry.getSetNullParams().put(new ParameterKey(2), Types.VARCHAR);

        ArrayList<BatchExecutionEntry> entries = new ArrayList<BatchExecutionEntry>();
        entries.add(entry);

        PreparedBatchExecution pbe = mock(PreparedBatchExecution.class);
        given(pbe.getBatchExecutionEntries()).willReturn(entries);

        // successful case
        DataSourceProxyAssertions.assertThat(pbe).batch(0, containsParamKeys(1));
        DataSourceProxyAssertions.assertThat(pbe).batch(0, containsParamKeys(1, 2));

        // missing keys
        try {
            DataSourceProxyAssertions.assertThat(pbe).batch(0, containsParamKeys(1, 2, 100));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: prepared parameter keys\n<[1, 2]>\nto contain:\n<[1, 2, 100]>\nbut could not find:\n<[100]>");
        }

        // wrong key type
        try {
            DataSourceProxyAssertions.assertThat(pbe).batch(0, containsParamKeys((double) 10.01));
            fail("exception should be thrown");
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("param key should be int or String");
        }

    }

    @Test
    public void testBatchWithWrongBatchExecutionEntryType() {

        CallableBatchExecution.CallableBatchExecutionEntry entry = new CallableBatchExecution.CallableBatchExecutionEntry();

        ArrayList<BatchExecutionEntry> entries = new ArrayList<BatchExecutionEntry>();
        entries.add(entry);   // setting wrong type

        PreparedBatchExecution pbe = mock(PreparedBatchExecution.class);
        given(pbe.getBatchExecutionEntries()).willReturn(entries);

        try {
            DataSourceProxyAssertions.assertThat(pbe).batch(0, containsParams(param(1, "foo")));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: batch entry\n<PreparedBatchExecutionEntry>\nbut was\n<CallableBatchExecutionEntry>");
        }

    }


}
