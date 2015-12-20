package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.proxy.ParameterKey;
import net.ttddyy.dsproxy.test.BatchExecutionEntry;
import net.ttddyy.dsproxy.test.CallableBatchExecution;
import net.ttddyy.dsproxy.test.PreparedBatchExecution;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static net.ttddyy.dsproxy.test.assertj.data.ExecutionParameter.param;
import static net.ttddyy.dsproxy.test.assertj.data.ExecutionParameters.containsParamIndexes;
import static net.ttddyy.dsproxy.test.assertj.data.ExecutionParameters.containsParamKeys;
import static net.ttddyy.dsproxy.test.assertj.data.ExecutionParameters.containsParamNames;
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

    @Test
    public void testHasBatchSize() {
        BatchExecutionEntry entry = mock(BatchExecutionEntry.class);

        ArrayList<BatchExecutionEntry> entries = new ArrayList<BatchExecutionEntry>();
        entries.addAll(Arrays.asList(entry, entry, entry));

        CallableBatchExecution cbe = mock(CallableBatchExecution.class);
        given(cbe.getBatchExecutionEntries()).willReturn(entries);

        DataSourceProxyAssertions.assertThat(cbe).hasBatchSize(3);

        try {
            DataSourceProxyAssertions.assertThat(cbe).hasBatchSize(1);
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected batch size:<1> but was:<3> in batch callable executions\n");
        }
    }


    @Test
    public void testBatchWithContainsParams() {
        Map<ParameterKey, Object> paramsByIndex = new HashMap<ParameterKey, Object>();
        paramsByIndex.put(new ParameterKey(1), "foo");

        Map<ParameterKey, Object> paramsByName = new HashMap<ParameterKey, Object>();
        paramsByName.put(new ParameterKey("bar"), "BAR");

        CallableBatchExecution.CallableBatchExecutionEntry entry = new CallableBatchExecution.CallableBatchExecutionEntry();
        entry.getParams().putAll(paramsByIndex);
        entry.getParams().putAll(paramsByName);

        ArrayList<BatchExecutionEntry> entries = new ArrayList<BatchExecutionEntry>();
        entries.addAll(Arrays.asList(entry));

        CallableBatchExecution cbe = mock(CallableBatchExecution.class);
        given(cbe.getBatchExecutionEntries()).willReturn(entries);

        // successful call
        DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParams(param(1, "foo"), param("bar", "BAR")));

        // index is too small
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(-1, containsParams(param(1, "foo")));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: batch index <-1> should be greater than equal to <0>");
        }

        // index is too big
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(1, containsParams(param(1, "foo")));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: batch index <1> is too big for the batch size <1>");
        }

        // value is wrong
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParams(param(1, "fooABC")));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: parameters \n<{1=foo}>\nto contain:\n<[index=1, value=fooABC]>\nbut could not find:\n<[index=1, value=fooABC]>");
        }

        // no param index key
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParams(param(2, "bar")));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: callable parameter keys\n<[1, bar]>\nto contain:\n<[2]>\nbut could not find:\n<[2]>");
        }

        // no param name key
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParams(param("foo", "FOO")));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: callable parameter keys\n<[1, bar]>\nto contain:\n<[foo]>\nbut could not find:\n<[foo]>");
        }

    }

    @Test
    public void testBatchParamKeysWithContainsParams() {
        Map<ParameterKey, Object> paramsByIndex = new HashMap<ParameterKey, Object>();
        paramsByIndex.put(new ParameterKey(1), "foo");

        Map<ParameterKey, Object> paramsByName = new HashMap<ParameterKey, Object>();
        paramsByName.put(new ParameterKey("bar"), "BAR");

        CallableBatchExecution.CallableBatchExecutionEntry entry = new CallableBatchExecution.CallableBatchExecutionEntry();
        entry.getParams().putAll(paramsByIndex);
        entry.getParams().putAll(paramsByName);

        ArrayList<BatchExecutionEntry> entries = new ArrayList<BatchExecutionEntry>();
        entries.addAll(Arrays.asList(entry));

        CallableBatchExecution cbe = mock(CallableBatchExecution.class);
        given(cbe.getBatchExecutionEntries()).willReturn(entries);


        // no param index key
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParams(param(2, "bar")));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: callable parameter keys\n<[1, bar]>\nto contain:\n<[2]>\nbut could not find:\n<[2]>");
        }

        // no param name key
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParams(param("foo", "FOO")));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: callable parameter keys\n<[1, bar]>\nto contain:\n<[foo]>\nbut could not find:\n<[foo]>");
        }

    }

    @Test
    public void testBatchParamKeysWithContainsParamsExactly() {
        Map<ParameterKey, Object> paramsByIndex = new HashMap<ParameterKey, Object>();
        paramsByIndex.put(new ParameterKey(1), "foo");

        Map<ParameterKey, Object> paramsByName = new HashMap<ParameterKey, Object>();
        paramsByName.put(new ParameterKey("bar"), "BAR");

        CallableBatchExecution.CallableBatchExecutionEntry entry = new CallableBatchExecution.CallableBatchExecutionEntry();
        entry.getParams().putAll(paramsByIndex);
        entry.getParams().putAll(paramsByName);

        ArrayList<BatchExecutionEntry> entries = new ArrayList<BatchExecutionEntry>();
        entries.addAll(Arrays.asList(entry));

        CallableBatchExecution cbe = mock(CallableBatchExecution.class);
        given(cbe.getBatchExecutionEntries()).willReturn(entries);

        // successful case
        DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamsExactly(param(1, "foo"), param("bar", "BAR")));

        // missing one param key (index)
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamsExactly(param(1, "foo")));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: callable parameter keys\n<[1, bar]>\nto be exactly:\n<[1]>\nbut missing keys:\n<[]>\nextra keys:\n<[bar]>");
        }

        // missing one param key (name)
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamsExactly(param("bar", "BAR")));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: callable parameter keys\n<[1, bar]>\nto be exactly:\n<[bar]>\nbut missing keys:\n<[]>\nextra keys:\n<[1]>");
        }

    }

    @Test
    public void testBatchParamKeyNames() {
        Map<ParameterKey, Object> paramsByIndex = new HashMap<ParameterKey, Object>();
        paramsByIndex.put(new ParameterKey(1), "foo");

        Map<ParameterKey, Object> paramsByName = new HashMap<ParameterKey, Object>();
        paramsByName.put(new ParameterKey("bar"), "BAR");

        CallableBatchExecution.CallableBatchExecutionEntry entry = new CallableBatchExecution.CallableBatchExecutionEntry();
        entry.getParams().putAll(paramsByIndex);
        entry.getParams().putAll(paramsByName);

        ArrayList<BatchExecutionEntry> entries = new ArrayList<BatchExecutionEntry>();
        entries.addAll(Arrays.asList(entry));

        CallableBatchExecution cbe = mock(CallableBatchExecution.class);
        given(cbe.getBatchExecutionEntries()).willReturn(entries);

        // successful case
        DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamNames("bar"));

        // missing param key
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamNames("foo", "bar"));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: callable parameter keys\n<[1, bar]>\nto contain:\n<[bar, foo]>\nbut could not find:\n<[foo]>");
        }


    }

    @Test
    public void testBatchParamKeyIndexes() {
        Map<ParameterKey, Object> paramsByIndex = new HashMap<ParameterKey, Object>();
        paramsByIndex.put(new ParameterKey(1), "foo");

        Map<ParameterKey, Object> paramsByName = new HashMap<ParameterKey, Object>();
        paramsByName.put(new ParameterKey("bar"), "BAR");

        CallableBatchExecution.CallableBatchExecutionEntry entry = new CallableBatchExecution.CallableBatchExecutionEntry();
        entry.getParams().putAll(paramsByIndex);
        entry.getParams().putAll(paramsByName);

        ArrayList<BatchExecutionEntry> entries = new ArrayList<BatchExecutionEntry>();
        entries.addAll(Arrays.asList(entry));

        CallableBatchExecution cbe = mock(CallableBatchExecution.class);
        given(cbe.getBatchExecutionEntries()).willReturn(entries);

        // successful case
        DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamIndexes(1));

        // missing one param key (index)
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamIndexes(1, 2));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: callable parameter keys\n<[1, bar]>\nto contain:\n<[1, 2]>\nbut could not find:\n<[2]>");
        }

    }

    @Test
    public void testBatchParamKeys() {
        Map<ParameterKey, Object> paramsByIndex = new HashMap<ParameterKey, Object>();
        paramsByIndex.put(new ParameterKey(1), "foo");

        Map<ParameterKey, Object> paramsByName = new HashMap<ParameterKey, Object>();
        paramsByName.put(new ParameterKey("bar"), "BAR");

        CallableBatchExecution.CallableBatchExecutionEntry entry = new CallableBatchExecution.CallableBatchExecutionEntry();
        entry.getParams().putAll(paramsByIndex);
        entry.getParams().putAll(paramsByName);

        ArrayList<BatchExecutionEntry> entries = new ArrayList<BatchExecutionEntry>();
        entries.addAll(Arrays.asList(entry));

        CallableBatchExecution cbe = mock(CallableBatchExecution.class);
        given(cbe.getBatchExecutionEntries()).willReturn(entries);

        // successful case
        DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamKeys(1));
        DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamKeys("bar"));
        DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamKeys(1, "bar"));

        // missing keys
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamKeys(1, 2, "foo", "bar"));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: callable parameter keys\n<[1, bar]>\nto contain:\n<[1, 2, bar, foo]>\nbut could not find:\n<[2, foo]>");
        }

        // wrong key type
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamKeys((double) 10.01));
            fail("exception should be thrown");
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("param key should be int or String");
        }

    }

    @Test
    public void testBatchWithWrongBatchExecutionEntryType() {

        PreparedBatchExecution.PreparedBatchExecutionEntry entry = new PreparedBatchExecution.PreparedBatchExecutionEntry();

        ArrayList<BatchExecutionEntry> entries = new ArrayList<BatchExecutionEntry>();
        entries.add(entry);   // setting wrong type

        CallableBatchExecution cbe = mock(CallableBatchExecution.class);
        given(cbe.getBatchExecutionEntries()).willReturn(entries);

        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParams(param(1, "foo")));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: batch entry\n<CallableBatchExecutionEntry>\nbut was\n<PreparedBatchExecutionEntry>");
        }

    }


    // TODO: batch with setNull
    // TODO: batch with registerOutParameter
}
