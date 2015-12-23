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
import static net.ttddyy.dsproxy.test.assertj.data.ExecutionParameter.outParam;
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
    public void batchWithContainsParams() {
        CallableBatchExecution.CallableBatchExecutionEntry entry = new CallableBatchExecution.CallableBatchExecutionEntry();
        entry.getParams().put(new ParameterKey(1), "foo");
        entry.getParams().put(new ParameterKey("bar"), "BAR");

        ArrayList<BatchExecutionEntry> entries = new ArrayList<BatchExecutionEntry>();
        entries.add(entry);

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

        // value is wrong for index
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParams(param(1, "fooABC")));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: parameters \n<{1=foo, bar=BAR}>\nto contain:\n<[1=fooABC]>\nbut could not find:\n<[1=fooABC]>");
        }

        // value is wrong for name
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParams(param("bar", "BARABC")));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: parameters \n<{1=foo, bar=BAR}>\nto contain:\n<[bar=BARABC]>\nbut could not find:\n<[bar=BARABC]>");
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
    public void containsParamsWithSetNullParameters() {
        CallableBatchExecution.CallableBatchExecutionEntry entry = new CallableBatchExecution.CallableBatchExecutionEntry();
        entry.getSetNullParams().put(new ParameterKey(1), Types.VARCHAR);
        entry.getSetNullParams().put(new ParameterKey("bar"), Types.DATE);

        ArrayList<BatchExecutionEntry> entries = new ArrayList<BatchExecutionEntry>();
        entries.add(entry);

        CallableBatchExecution cbe = mock(CallableBatchExecution.class);
        given(cbe.getBatchExecutionEntries()).willReturn(entries);

        // successful call
        DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParams(nullParam(1, Types.VARCHAR), nullParam("bar", Types.DATE)));

        // value is wrong for index
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParams(nullParam(1, Types.ARRAY)));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: parameters \n<{1=NULL(VARCHAR), bar=NULL(DATE)}>\nto contain:\n<[1=NULL(ARRAY)]>\nbut could not find:\n<[1=NULL(ARRAY)]>");
        }

        // value is wrong for name
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParams(nullParam("bar", Types.ARRAY)));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: parameters \n<{1=NULL(VARCHAR), bar=NULL(DATE)}>\nto contain:\n<[bar=NULL(ARRAY)]>\nbut could not find:\n<[bar=NULL(ARRAY)]>");
        }

        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParams(param(1, 12))); // Types.VARCHAR == 12
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: parameters \n<{1=NULL(VARCHAR), bar=NULL(DATE)}>\nto contain:\n<[1=12]>\nbut could not find:\n<[1=12]>");
        }
    }

    @Test
    public void containsParamsWithRegisterOutParameters() {
        CallableBatchExecution.CallableBatchExecutionEntry entry = new CallableBatchExecution.CallableBatchExecutionEntry();
        entry.getOutParams().put(new ParameterKey(1), Types.BOOLEAN);
        entry.getOutParams().put(new ParameterKey("bar"), Types.DOUBLE);

        ArrayList<BatchExecutionEntry> entries = new ArrayList<BatchExecutionEntry>();
        entries.add(entry);

        CallableBatchExecution cbe = mock(CallableBatchExecution.class);
        given(cbe.getBatchExecutionEntries()).willReturn(entries);

        // successful call
        DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParams(outParam(1, Types.BOOLEAN), outParam("bar", Types.DOUBLE)));

        // value is wrong for index
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParams(outParam(1, Types.ARRAY)));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: parameters \n<{1=OUTPUT(BOOLEAN), bar=OUTPUT(DOUBLE)}>\nto contain:\n<[1=OUTPUT(ARRAY)]>\nbut could not find:\n<[1=OUTPUT(ARRAY)]>");
        }

        // value is wrong for name
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParams(outParam("bar", Types.ARRAY)));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: parameters \n<{1=OUTPUT(BOOLEAN), bar=OUTPUT(DOUBLE)}>\nto contain:\n<[bar=OUTPUT(ARRAY)]>\nbut could not find:\n<[bar=OUTPUT(ARRAY)]>");
        }

        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParams(param(1, 16))); // Types.BOOLEAN == 16
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: parameters \n<{1=OUTPUT(BOOLEAN), bar=OUTPUT(DOUBLE)}>\nto contain:\n<[1=16]>\nbut could not find:\n<[1=16]>");
        }
    }

    @Test
    public void containsParamsWithMixedParameterTypes() {
        CallableBatchExecution.CallableBatchExecutionEntry entry = new CallableBatchExecution.CallableBatchExecutionEntry();
        entry.getParams().put(new ParameterKey(1), "foo");
        entry.getParams().put(new ParameterKey("foo"), "FOO");
        entry.getSetNullParams().put(new ParameterKey(2), Types.VARCHAR);
        entry.getSetNullParams().put(new ParameterKey("bar"), Types.DATE);
        entry.getOutParams().put(new ParameterKey(3), Types.BOOLEAN);
        entry.getOutParams().put(new ParameterKey("baz"), Types.BIGINT);

        ArrayList<BatchExecutionEntry> entries = new ArrayList<BatchExecutionEntry>();
        entries.add(entry);

        CallableBatchExecution cbe = mock(CallableBatchExecution.class);
        given(cbe.getBatchExecutionEntries()).willReturn(entries);

        // successful call
        DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParams(param(1, "foo"), param("foo", "FOO"),
                nullParam(2, Types.VARCHAR), nullParam("bar", Types.DATE),
                outParam(3, Types.BOOLEAN), outParam("baz", Types.BIGINT)));

        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParams(nullParam(1, Types.ARRAY)));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: parameters \n<{1=foo, 2=NULL(VARCHAR), 3=OUTPUT(BOOLEAN), bar=NULL(DATE), baz=OUTPUT(BIGINT), foo=FOO}>\nto contain:\n<[1=NULL(ARRAY)]>\nbut could not find:\n<[1=NULL(ARRAY)]>");
        }
    }


    @Test
    public void batchParamKeysWithContainsParamsExactly() {
        CallableBatchExecution.CallableBatchExecutionEntry entry = new CallableBatchExecution.CallableBatchExecutionEntry();
        entry.getParams().put(new ParameterKey(1), "foo");
        entry.getParams().put(new ParameterKey("bar"), "BAR");

        ArrayList<BatchExecutionEntry> entries = new ArrayList<BatchExecutionEntry>();
        entries.add(entry);

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
    public void batchParamKeysWithContainsParamsExactlyForSetNullParameters() {
        CallableBatchExecution.CallableBatchExecutionEntry entry = new CallableBatchExecution.CallableBatchExecutionEntry();
        entry.getSetNullParams().put(new ParameterKey(1), Types.VARCHAR);
        entry.getSetNullParams().put(new ParameterKey("bar"), Types.DATE);

        ArrayList<BatchExecutionEntry> entries = new ArrayList<BatchExecutionEntry>();
        entries.add(entry);

        CallableBatchExecution cbe = mock(CallableBatchExecution.class);
        given(cbe.getBatchExecutionEntries()).willReturn(entries);

        // successful case
        DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamsExactly(nullParam(1, Types.VARCHAR), nullParam("bar", Types.DATE)));

        // missing one param key (index)
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamsExactly(nullParam(1, Types.VARCHAR)));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: callable parameter keys\n<[1, bar]>\nto be exactly:\n<[1]>\nbut missing keys:\n<[]>\nextra keys:\n<[bar]>");
        }

        // missing one param key (name)
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamsExactly(nullParam("bar", Types.DATE)));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: callable parameter keys\n<[1, bar]>\nto be exactly:\n<[bar]>\nbut missing keys:\n<[]>\nextra keys:\n<[1]>");
        }

    }

    @Test
    public void batchParamKeysWithContainsParamsExactlyForRegisterOutParameters() {
        CallableBatchExecution.CallableBatchExecutionEntry entry = new CallableBatchExecution.CallableBatchExecutionEntry();
        entry.getOutParams().put(new ParameterKey(1), Types.BOOLEAN);
        entry.getOutParams().put(new ParameterKey("bar"), Types.DOUBLE);

        ArrayList<BatchExecutionEntry> entries = new ArrayList<BatchExecutionEntry>();
        entries.add(entry);

        CallableBatchExecution cbe = mock(CallableBatchExecution.class);
        given(cbe.getBatchExecutionEntries()).willReturn(entries);

        // successful case
        DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamsExactly(outParam(1, Types.BOOLEAN), outParam("bar", Types.DOUBLE)));

        // missing one param key (index)
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamsExactly(outParam(1, Types.BOOLEAN)));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: callable parameter keys\n<[1, bar]>\nto be exactly:\n<[1]>\nbut missing keys:\n<[]>\nextra keys:\n<[bar]>");
        }

        // missing one param key (name)
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamsExactly(outParam("bar", Types.DOUBLE)));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: callable parameter keys\n<[1, bar]>\nto be exactly:\n<[bar]>\nbut missing keys:\n<[]>\nextra keys:\n<[1]>");
        }

    }

    @Test
    public void testBatchParamKeyNames() {
        CallableBatchExecution.CallableBatchExecutionEntry entry = new CallableBatchExecution.CallableBatchExecutionEntry();
        entry.getParams().put(new ParameterKey(1), "foo");
        entry.getParams().put(new ParameterKey("foo"), "FOO");
        entry.getSetNullParams().put(new ParameterKey(2), Types.VARCHAR);
        entry.getSetNullParams().put(new ParameterKey("bar"), Types.DATE);
        entry.getOutParams().put(new ParameterKey(3), Types.BOOLEAN);
        entry.getOutParams().put(new ParameterKey("baz"), Types.BIGINT);

        ArrayList<BatchExecutionEntry> entries = new ArrayList<BatchExecutionEntry>();
        entries.add(entry);

        CallableBatchExecution cbe = mock(CallableBatchExecution.class);
        given(cbe.getBatchExecutionEntries()).willReturn(entries);

        // successful case
        DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamNames("foo", "bar", "baz"));
        DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamNames("bar"));

        // missing param key
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamNames("zzz", "bar"));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: callable parameter keys\n<[1, 2, 3, bar, baz, foo]>\nto contain:\n<[bar, zzz]>\nbut could not find:\n<[zzz]>");
        }


    }

    @Test
    public void testBatchParamKeyIndexes() {
        CallableBatchExecution.CallableBatchExecutionEntry entry = new CallableBatchExecution.CallableBatchExecutionEntry();
        entry.getParams().put(new ParameterKey(1), "foo");
        entry.getParams().put(new ParameterKey("foo"), "FOO");
        entry.getSetNullParams().put(new ParameterKey(2), Types.VARCHAR);
        entry.getSetNullParams().put(new ParameterKey("bar"), Types.DATE);
        entry.getOutParams().put(new ParameterKey(3), Types.BOOLEAN);
        entry.getOutParams().put(new ParameterKey("baz"), Types.BIGINT);

        ArrayList<BatchExecutionEntry> entries = new ArrayList<BatchExecutionEntry>();
        entries.add(entry);

        CallableBatchExecution cbe = mock(CallableBatchExecution.class);
        given(cbe.getBatchExecutionEntries()).willReturn(entries);

        // successful case
        DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamIndexes(1, 2, 3));
        DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamIndexes(2, 3, 1));
        DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamIndexes(2, 3));

        // missing one param key (index)
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamIndexes(1, 2, 100));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: callable parameter keys\n<[1, 2, 3, bar, baz, foo]>\nto contain:\n<[1, 100, 2]>\nbut could not find:\n<[100]>");
        }

    }

    @Test
    public void testBatchParamKeys() {
        CallableBatchExecution.CallableBatchExecutionEntry entry = new CallableBatchExecution.CallableBatchExecutionEntry();
        entry.getParams().put(new ParameterKey(1), "foo");
        entry.getParams().put(new ParameterKey("foo"), "FOO");
        entry.getSetNullParams().put(new ParameterKey(2), Types.VARCHAR);
        entry.getSetNullParams().put(new ParameterKey("bar"), Types.DATE);
        entry.getOutParams().put(new ParameterKey(3), Types.BOOLEAN);
        entry.getOutParams().put(new ParameterKey("baz"), Types.BIGINT);

        ArrayList<BatchExecutionEntry> entries = new ArrayList<BatchExecutionEntry>();
        entries.add(entry);

        CallableBatchExecution cbe = mock(CallableBatchExecution.class);
        given(cbe.getBatchExecutionEntries()).willReturn(entries);

        // successful case
        DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamKeys(1));
        DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamKeys("bar"));
        DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamKeys(1, "bar"));
        DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamKeys(1, 2, 3, "foo", "bar", "baz"));

        // missing keys
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamKeys(1, 2, 100, "zzz", "bar"));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: callable parameter keys\n<[1, 2, 3, bar, baz, foo]>\nto contain:\n<[1, 100, 2, bar, zzz]>\nbut could not find:\n<[100, zzz]>");
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

}
