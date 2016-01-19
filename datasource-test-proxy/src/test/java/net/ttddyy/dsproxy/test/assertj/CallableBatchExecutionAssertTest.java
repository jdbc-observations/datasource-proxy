package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.BatchExecutionEntry;
import net.ttddyy.dsproxy.test.CallableBatchExecution;
import net.ttddyy.dsproxy.test.PreparedBatchExecution;
import org.junit.Before;
import org.junit.Test;

import java.sql.JDBCType;
import java.sql.Types;
import java.util.Arrays;

import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.createRegisterOut;
import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.createSetNull;
import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.createSetParam;
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
        cbe.getBatchExecutionEntries().addAll(Arrays.asList(entry, entry, entry));

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
        entry.getAllParameters().add(createSetParam(1, "foo"));
        entry.getAllParameters().add(createSetParam("bar", "BAR"));

        CallableBatchExecution cbe = new CallableBatchExecution();
        cbe.getBatchExecutionEntries().add(entry);

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
            assertThat(e).hasMessage("\n" +
                    "Expecting: callable parameter keys\n" +
                    "<[1, bar]>\n" +
                    "(params=[1, bar], set-null=[], register-out=[])\n" +
                    "to contain:\n" +
                    "<params=[2]>\n" +
                    "but could not find:\n" +
                    "<params=[2]>");
        }

        // no param name key
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParams(param("foo", "FOO")));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\n" +
                    "Expecting: callable parameter keys\n" +
                    "<[1, bar]>\n" +
                    "(params=[1, bar], set-null=[], register-out=[])\n" +
                    "to contain:\n" +
                    "<params=[foo]>\n" +
                    "but could not find:\n" +
                    "<params=[foo]>");
        }

    }

    @Test
    public void containsParamsWithSetNullParameters() {
        CallableBatchExecution.CallableBatchExecutionEntry entry = new CallableBatchExecution.CallableBatchExecutionEntry();
        entry.getAllParameters().add(createSetNull(1, Types.VARCHAR));
        entry.getAllParameters().add(createSetNull("bar", Types.DATE));

        CallableBatchExecution cbe = new CallableBatchExecution();
        cbe.getBatchExecutionEntries().add(entry);

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
            assertThat(e).hasMessage("\n" +
                    "Expecting: callable parameter keys\n" +
                    "<[1, bar]>\n" +
                    "(params=[], set-null=[1, bar], register-out=[])\n" +
                    "to contain:\n" +
                    "<params=[1]>\n" +
                    "but could not find:\n" +
                    "<params=[1]>");
        }
    }

    @Test
    public void containsParamsWithSetNullParametersOnlyKeys() {
        CallableBatchExecution.CallableBatchExecutionEntry entry = new CallableBatchExecution.CallableBatchExecutionEntry();
        entry.getAllParameters().add(createSetNull(1, Types.VARCHAR));
        entry.getAllParameters().add(createSetNull("bar", Types.DATE));

        CallableBatchExecution cbe = new CallableBatchExecution();
        cbe.getBatchExecutionEntries().add(entry);

        // successful call
        DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParams(nullParam(1), nullParam("bar")));

        // index key doesn't exist
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParams(nullParam(100)));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\n" +
                    "Expecting: callable parameter keys\n" +
                    "<[1, bar]>\n" +
                    "(params=[], set-null=[1, bar], register-out=[])\n" +
                    "to contain:\n" +
                    "<set-null=[100]>\n" +
                    "but could not find:\n" +
                    "<set-null=[100]>");
        }

        // name key doesn't exist
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParams(nullParam("BAR")));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\n" +
                    "Expecting: callable parameter keys\n" +
                    "<[1, bar]>\n" +
                    "(params=[], set-null=[1, bar], register-out=[])\n" +
                    "to contain:\n" +
                    "<set-null=[BAR]>\n" +
                    "but could not find:\n" +
                    "<set-null=[BAR]>");
        }

    }

    @Test
    public void containsParamsWithRegisterOutParameters() {
        CallableBatchExecution.CallableBatchExecutionEntry entry = new CallableBatchExecution.CallableBatchExecutionEntry();
        entry.getAllParameters().add(createRegisterOut(1, Types.BOOLEAN));
        entry.getAllParameters().add(createRegisterOut("bar", Types.DOUBLE));
        entry.getAllParameters().add(createRegisterOut(2, JDBCType.VARCHAR));
        entry.getAllParameters().add(createRegisterOut("foo", JDBCType.BIGINT));

        CallableBatchExecution cbe = new CallableBatchExecution();
        cbe.getBatchExecutionEntries().add(entry);

        // successful call
        DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParams(outParam(1, Types.BOOLEAN), outParam("bar", Types.DOUBLE)));
        DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParams(outParam(2, JDBCType.VARCHAR), outParam("foo", JDBCType.BIGINT)));
        DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParams(outParam(1, Types.BOOLEAN), outParam(2, JDBCType.VARCHAR)));
        DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParams(outParam("bar", Types.DOUBLE), outParam("foo", JDBCType.BIGINT)));
        DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParams(outParam(1, Types.BOOLEAN), outParam("bar", Types.DOUBLE), outParam(2, JDBCType.VARCHAR), outParam("foo", JDBCType.BIGINT)));


        // value is wrong for index with int type
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParams(outParam(1, Types.ARRAY)));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: parameters \n<{1=OUTPUT(BOOLEAN[16]), 2=OUTPUT(VARCHAR), bar=OUTPUT(DOUBLE[8]), foo=OUTPUT(BIGINT)}>\nto contain:\n<[1=OUTPUT(ARRAY[2003])]>\nbut could not find:\n<[1=OUTPUT(ARRAY[2003])]>");
        }

        // value is wrong for name with int type
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParams(outParam("bar", Types.ARRAY)));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: parameters \n<{1=OUTPUT(BOOLEAN[16]), 2=OUTPUT(VARCHAR), bar=OUTPUT(DOUBLE[8]), foo=OUTPUT(BIGINT)}>\nto contain:\n<[bar=OUTPUT(ARRAY[2003])]>\nbut could not find:\n<[bar=OUTPUT(ARRAY[2003])]>");
        }

        // value is wrong for index with SQLType
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParams(outParam(1, JDBCType.ARRAY)));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: parameters \n<{1=OUTPUT(BOOLEAN[16]), 2=OUTPUT(VARCHAR), bar=OUTPUT(DOUBLE[8]), foo=OUTPUT(BIGINT)}>\nto contain:\n<[1=OUTPUT(ARRAY)]>\nbut could not find:\n<[1=OUTPUT(ARRAY)]>");
        }

        // value is wrong for name with SQLType
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParams(outParam("bar", JDBCType.ARRAY)));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: parameters \n<{1=OUTPUT(BOOLEAN[16]), 2=OUTPUT(VARCHAR), bar=OUTPUT(DOUBLE[8]), foo=OUTPUT(BIGINT)}>\nto contain:\n<[bar=OUTPUT(ARRAY)]>\nbut could not find:\n<[bar=OUTPUT(ARRAY)]>");
        }

        // specifing index which is not in register-out
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParams(param(1, 16))); // Types.BOOLEAN == 16
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\n" +
                    "Expecting: callable parameter keys\n" +
                    "<[1, 2, bar, foo]>\n" +
                    "(params=[], set-null=[], register-out=[1, 2, bar, foo])\n" +
                    "to contain:\n" +
                    "<params=[1]>\n" +
                    "but could not find:\n" +
                    "<params=[1]>");
        }
    }

    @Test
    public void containsParamsWithMixedParameterTypes() {
        CallableBatchExecution.CallableBatchExecutionEntry entry = new CallableBatchExecution.CallableBatchExecutionEntry();
        entry.getAllParameters().add(createSetParam(1, "foo"));
        entry.getAllParameters().add(createSetParam("foo", "FOO"));
        entry.getAllParameters().add(createSetNull(2, Types.VARCHAR));
        entry.getAllParameters().add(createSetNull("bar", Types.DATE));
        entry.getAllParameters().add(createRegisterOut(3, Types.BOOLEAN));
        entry.getAllParameters().add(createRegisterOut("baz", Types.BIGINT));

        CallableBatchExecution cbe = new CallableBatchExecution();
        cbe.getBatchExecutionEntries().add(entry);

        // successful call
        DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParams(param(1, "foo"), param("foo", "FOO"),
                nullParam(2, Types.VARCHAR), nullParam("bar", Types.DATE),
                outParam(3, Types.BOOLEAN), outParam("baz", Types.BIGINT)));

        // specifying index not in null params
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParams(nullParam(1, Types.ARRAY)));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\n" +
                    "Expecting: callable parameter keys\n" +
                    "<[1, 2, 3, bar, baz, foo]>\n" +
                    "(params=[1, foo], set-null=[2, bar], register-out=[3, baz])\n" +
                    "to contain:\n" +
                    "<set-null=[1]>\n" +
                    "but could not find:\n" +
                    "<set-null=[1]>");
        }
    }


    @Test
    public void batchParamKeysWithContainsParamsExactly() {
        CallableBatchExecution.CallableBatchExecutionEntry entry = new CallableBatchExecution.CallableBatchExecutionEntry();
        entry.getAllParameters().add(createSetParam(1, "foo"));
        entry.getAllParameters().add(createSetParam("bar", "BAR"));

        CallableBatchExecution cbe = new CallableBatchExecution();
        cbe.getBatchExecutionEntries().add(entry);

        // successful case
        DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamsExactly(param(1, "foo"), param("bar", "BAR")));

        // missing one param key (index)
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamsExactly(param(1, "foo")));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\n" +
                    "Expecting: callable parameter keys\n" +
                    "<[1, bar]>\n" +
                    "(params=[1, bar], set-null=[], register-out=[])\n" +
                    "to be exactly:\n" +
                    "<[1]>\n" +
                    "(params=[1], set-null=[], register-out=[])\n" +
                    "but missing keys:\n" +
                    "<>\n" +
                    "extra keys:\n" +
                    "<params=[bar]>");
        }

        // missing one param key (name)
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamsExactly(param("bar", "BAR")));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\n" +
                    "Expecting: callable parameter keys\n" +
                    "<[1, bar]>\n" +
                    "(params=[1, bar], set-null=[], register-out=[])\n" +
                    "to be exactly:\n" +
                    "<[bar]>\n" +
                    "(params=[bar], set-null=[], register-out=[])\n" +
                    "but missing keys:\n" +
                    "<>\n" +
                    "extra keys:\n" +
                    "<params=[1]>");
        }

    }

    @Test
    public void batchParamKeysWithContainsParamsExactlyForSetNullParameters() {
        CallableBatchExecution.CallableBatchExecutionEntry entry = new CallableBatchExecution.CallableBatchExecutionEntry();
        entry.getAllParameters().add(createSetNull(1, Types.VARCHAR));
        entry.getAllParameters().add(createSetNull("bar", Types.DATE));

        CallableBatchExecution cbe = new CallableBatchExecution();
        cbe.getBatchExecutionEntries().add(entry);

        // successful case
        DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamsExactly(nullParam(1, Types.VARCHAR), nullParam("bar", Types.DATE)));

        // missing one param key (index)
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamsExactly(nullParam(1, Types.VARCHAR)));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\n" +
                    "Expecting: callable parameter keys\n" +
                    "<[1, bar]>\n" +
                    "(params=[], set-null=[1, bar], register-out=[])\n" +
                    "to be exactly:\n" +
                    "<[1]>\n" +
                    "(params=[], set-null=[1], register-out=[])\n" +
                    "but missing keys:\n" +
                    "<>\n" +
                    "extra keys:\n" +
                    "<set-null=[bar]>");
        }

        // missing one param key (name)
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamsExactly(nullParam("bar", Types.DATE)));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\n" +
                    "Expecting: callable parameter keys\n" +
                    "<[1, bar]>\n" +
                    "(params=[], set-null=[1, bar], register-out=[])\n" +
                    "to be exactly:\n" +
                    "<[bar]>\n" +
                    "(params=[], set-null=[bar], register-out=[])\n" +
                    "but missing keys:\n" +
                    "<>\n" +
                    "extra keys:\n" +
                    "<set-null=[1]>");
        }

    }

    @Test
    public void batchParamKeysWithContainsParamsExactlyForRegisterOutParameters() {
        CallableBatchExecution.CallableBatchExecutionEntry entry = new CallableBatchExecution.CallableBatchExecutionEntry();
        entry.getAllParameters().add(createRegisterOut(1, Types.BOOLEAN));
        entry.getAllParameters().add(createRegisterOut("bar", Types.DOUBLE));

        CallableBatchExecution cbe = new CallableBatchExecution();
        cbe.getBatchExecutionEntries().add(entry);

        // successful case
        DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamsExactly(outParam(1, Types.BOOLEAN), outParam("bar", Types.DOUBLE)));

        // missing one param key (index)
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamsExactly(outParam(1, Types.BOOLEAN)));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\n" +
                    "Expecting: callable parameter keys\n" +
                    "<[1, bar]>\n" +
                    "(params=[], set-null=[], register-out=[1, bar])\n" +
                    "to be exactly:\n" +
                    "<[1]>\n" +
                    "(params=[], set-null=[], register-out=[1])\n" +
                    "but missing keys:\n" +
                    "<>\n" +
                    "extra keys:\n" +
                    "<register-out=[bar]>");
        }

        // missing one param key (name)
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamsExactly(outParam("bar", Types.DOUBLE)));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\n" +
                    "Expecting: callable parameter keys\n" +
                    "<[1, bar]>\n" +
                    "(params=[], set-null=[], register-out=[1, bar])\n" +
                    "to be exactly:\n" +
                    "<[bar]>\n" +
                    "(params=[], set-null=[], register-out=[bar])\n" +
                    "but missing keys:\n" +
                    "<>\n" +
                    "extra keys:\n" +
                    "<register-out=[1]>");
        }

    }

    @Test
    public void testBatchParamKeyNames() {
        CallableBatchExecution.CallableBatchExecutionEntry entry = new CallableBatchExecution.CallableBatchExecutionEntry();

        entry.getAllParameters().add(createSetParam(1, "foo"));
        entry.getAllParameters().add(createSetParam("foo", "FOO"));
        entry.getAllParameters().add(createSetNull(2, Types.VARCHAR));
        entry.getAllParameters().add(createSetNull("bar", Types.DATE));
        entry.getAllParameters().add(createRegisterOut(3, Types.BOOLEAN));
        entry.getAllParameters().add(createRegisterOut("baz", Types.BIGINT));

        CallableBatchExecution cbe = new CallableBatchExecution();
        cbe.getBatchExecutionEntries().add(entry);

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
        entry.getAllParameters().add(createSetParam(1, "foo"));
        entry.getAllParameters().add(createSetParam("foo", "FOO"));
        entry.getAllParameters().add(createSetNull(2, Types.VARCHAR));
        entry.getAllParameters().add(createSetNull("bar", Types.DATE));
        entry.getAllParameters().add(createRegisterOut(3, Types.BOOLEAN));
        entry.getAllParameters().add(createRegisterOut("baz", Types.BIGINT));

        CallableBatchExecution cbe = new CallableBatchExecution();
        cbe.getBatchExecutionEntries().add(entry);

        // successful case
        DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamIndexes(1, 2, 3));
        DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamIndexes(2, 3, 1));
        DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamIndexes(2, 3));

        // missing one param key (index)
        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParamIndexes(1, 2, 100));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: callable parameter keys\n<[1, 2, 3, bar, baz, foo]>\nto contain:\n<[1, 2, 100]>\nbut could not find:\n<[100]>");
        }

    }

    @Test
    public void testBatchParamKeys() {
        CallableBatchExecution.CallableBatchExecutionEntry entry = new CallableBatchExecution.CallableBatchExecutionEntry();
        entry.getAllParameters().add(createSetParam(1, "foo"));
        entry.getAllParameters().add(createSetParam("foo", "FOO"));
        entry.getAllParameters().add(createSetNull(2, Types.VARCHAR));
        entry.getAllParameters().add(createSetNull("bar", Types.DATE));
        entry.getAllParameters().add(createRegisterOut(3, Types.BOOLEAN));
        entry.getAllParameters().add(createRegisterOut("baz", Types.BIGINT));

        CallableBatchExecution cbe = new CallableBatchExecution();
        cbe.getBatchExecutionEntries().add(entry);

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
            assertThat(e).hasMessage("\nExpecting: callable parameter keys\n<[1, 2, 3, bar, baz, foo]>\nto contain:\n<[1, 2, 100, bar, zzz]>\nbut could not find:\n<[100, zzz]>");
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

        CallableBatchExecution cbe = new CallableBatchExecution();
        cbe.getBatchExecutionEntries().add(entry);  // setting wrong type

        try {
            DataSourceProxyAssertions.assertThat(cbe).batch(0, containsParams(param(1, "foo")));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: batch entry\n<CallableBatchExecutionEntry>\nbut was\n<PreparedBatchExecutionEntry>");
        }

    }

}
