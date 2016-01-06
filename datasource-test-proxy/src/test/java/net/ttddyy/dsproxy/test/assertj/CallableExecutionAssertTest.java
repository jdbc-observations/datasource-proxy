package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.proxy.ParameterKey;
import net.ttddyy.dsproxy.test.CallableExecution;
import org.junit.Before;
import org.junit.Test;

import java.sql.Types;

import static net.ttddyy.dsproxy.test.assertj.data.ExecutionParameter.nullParam;
import static net.ttddyy.dsproxy.test.assertj.data.ExecutionParameter.outParam;
import static net.ttddyy.dsproxy.test.assertj.data.ExecutionParameter.param;
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


    @Test
    public void testContainsParams() {
        CallableExecution ce = new CallableExecution();
        ce.getParams().put(new ParameterKey(1), "foo");
        ce.getParams().put(new ParameterKey("bar"), "BAR");

        // successful call
        DataSourceProxyAssertions.assertThat(ce).containsParams(param(1, "foo"), param("bar", "BAR"));
        DataSourceProxyAssertions.assertThat(ce).containsParams(param(1, "foo"));
        DataSourceProxyAssertions.assertThat(ce).containsParams(param("bar", "BAR"));

        // value is wrong for index
        try {
            DataSourceProxyAssertions.assertThat(ce).containsParams(param(1, "fooABC"));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: parameters \n<{1=foo, bar=BAR}>\nto contain:\n<[1=fooABC]>\nbut could not find:\n<[1=fooABC]>");
        }

        // value is wrong for name
        try {
            DataSourceProxyAssertions.assertThat(ce).containsParams(param("bar", "BARABC"));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: parameters \n<{1=foo, bar=BAR}>\nto contain:\n<[bar=BARABC]>\nbut could not find:\n<[bar=BARABC]>");
        }

        // no param index key
        try {
            DataSourceProxyAssertions.assertThat(ce).containsParams(param(2, "bar"));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: callable parameter keys\n<[1, bar]>\nto contain:\n<[2]>\nbut could not find:\n<[2]>");
        }

        // no param name key
        try {
            DataSourceProxyAssertions.assertThat(ce).containsParams(param("foo", "FOO"));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: callable parameter keys\n<[1, bar]>\nto contain:\n<[foo]>\nbut could not find:\n<[foo]>");
        }

    }

    @Test
    public void containsParamsWithMixedParameterTypes() {
        CallableExecution ce = new CallableExecution();
        ce.getParams().put(new ParameterKey(1), "foo");
        ce.getParams().put(new ParameterKey("foo"), "FOO");
        ce.getSetNullParams().put(new ParameterKey(2), Types.VARCHAR);
        ce.getSetNullParams().put(new ParameterKey("bar"), Types.DATE);
        ce.getOutParams().put(new ParameterKey(3), Types.BOOLEAN);
        ce.getOutParams().put(new ParameterKey("baz"), Types.BIGINT);

        // successful call
        DataSourceProxyAssertions.assertThat(ce).containsParams(param(1, "foo"), param("foo", "FOO"),
                nullParam(2, Types.VARCHAR), nullParam("bar", Types.DATE),
                outParam(3, Types.BOOLEAN), outParam("baz", Types.BIGINT));

        try {
            DataSourceProxyAssertions.assertThat(ce).containsParams(nullParam(1, Types.ARRAY));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: parameters \n<{1=foo, 2=NULL(VARCHAR), 3=OUTPUT(BOOLEAN[16]), bar=NULL(DATE), baz=OUTPUT(BIGINT[-5]), foo=FOO}>\nto contain:\n<[1=NULL(ARRAY)]>\nbut could not find:\n<[1=NULL(ARRAY)]>");
        }
    }

    @Test
    public void paramKeysWithContainsParamsExactly() {
        CallableExecution ce = new CallableExecution();
        ce.getParams().put(new ParameterKey(1), "foo");
        ce.getParams().put(new ParameterKey("bar"), "BAR");

        // successful case
        DataSourceProxyAssertions.assertThat(ce).containsParamsExactly(param(1, "foo"), param("bar", "BAR"));

        // missing one param key (index)
        try {
            DataSourceProxyAssertions.assertThat(ce).containsParamsExactly(param(1, "foo"));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: callable parameter keys\n<[1, bar]>\nto be exactly:\n<[1]>\nbut missing keys:\n<[]>\nextra keys:\n<[bar]>");
        }

        // missing one param key (name)
        try {
            DataSourceProxyAssertions.assertThat(ce).containsParamsExactly(param("bar", "BAR"));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: callable parameter keys\n<[1, bar]>\nto be exactly:\n<[bar]>\nbut missing keys:\n<[]>\nextra keys:\n<[1]>");
        }
    }


    @Test
    public void paramKeysWithContainsParamsExactlyForSetNullParameters() {
        CallableExecution ce = new CallableExecution();
        ce.getSetNullParams().put(new ParameterKey(1), Types.VARCHAR);
        ce.getSetNullParams().put(new ParameterKey("bar"), Types.DATE);

        // successful case
        DataSourceProxyAssertions.assertThat(ce).containsParamsExactly(nullParam(1, Types.VARCHAR), nullParam("bar", Types.DATE));

        // missing one param key (index)
        try {
            DataSourceProxyAssertions.assertThat(ce).containsParamsExactly(nullParam(1, Types.VARCHAR));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: callable parameter keys\n<[1, bar]>\nto be exactly:\n<[1]>\nbut missing keys:\n<[]>\nextra keys:\n<[bar]>");
        }

        // missing one param key (name)
        try {
            DataSourceProxyAssertions.assertThat(ce).containsParamsExactly(nullParam("bar", Types.DATE));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: callable parameter keys\n<[1, bar]>\nto be exactly:\n<[bar]>\nbut missing keys:\n<[]>\nextra keys:\n<[1]>");
        }
    }

    @Test
    public void paramKeysWithContainsParamsExactlyForRegisterOutParameters() {
        CallableExecution ce = new CallableExecution();
        ce.getOutParams().put(new ParameterKey(1), Types.BOOLEAN);
        ce.getOutParams().put(new ParameterKey("bar"), Types.DOUBLE);

        // successful case
        DataSourceProxyAssertions.assertThat(ce).containsParamsExactly(outParam(1, Types.BOOLEAN), outParam("bar", Types.DOUBLE));

        // missing one param key (index)
        try {
            DataSourceProxyAssertions.assertThat(ce).containsParamsExactly(outParam(1, Types.BOOLEAN));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: callable parameter keys\n<[1, bar]>\nto be exactly:\n<[1]>\nbut missing keys:\n<[]>\nextra keys:\n<[bar]>");
        }

        // missing one param key (name)
        try {
            DataSourceProxyAssertions.assertThat(ce).containsParamsExactly(outParam("bar", Types.DOUBLE));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: callable parameter keys\n<[1, bar]>\nto be exactly:\n<[bar]>\nbut missing keys:\n<[]>\nextra keys:\n<[1]>");
        }

    }

    @Test
    public void containsParamNames() {
        CallableExecution ce = new CallableExecution();
        ce.getParams().put(new ParameterKey(1), "foo");
        ce.getParams().put(new ParameterKey("foo"), "FOO");
        ce.getSetNullParams().put(new ParameterKey(2), Types.VARCHAR);
        ce.getSetNullParams().put(new ParameterKey("bar"), Types.DATE);
        ce.getOutParams().put(new ParameterKey(3), Types.BOOLEAN);
        ce.getOutParams().put(new ParameterKey("baz"), Types.BIGINT);

        // successful case
        DataSourceProxyAssertions.assertThat(ce).containsParamNames("foo", "bar", "baz");
        DataSourceProxyAssertions.assertThat(ce).containsParamNames("bar");

        // missing param key
        try {
            DataSourceProxyAssertions.assertThat(ce).containsParamNames("zzz", "bar");
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: callable parameter keys\n<[1, 2, 3, bar, baz, foo]>\nto contain:\n<[bar, zzz]>\nbut could not find:\n<[zzz]>");
        }
    }

    @Test
    public void containsParamName() {
        CallableExecution ce = new CallableExecution();
        ce.getParams().put(new ParameterKey(1), "foo");
        ce.getParams().put(new ParameterKey("foo"), "FOO");
        ce.getSetNullParams().put(new ParameterKey(2), Types.VARCHAR);
        ce.getSetNullParams().put(new ParameterKey("bar"), Types.DATE);
        ce.getOutParams().put(new ParameterKey(3), Types.BOOLEAN);
        ce.getOutParams().put(new ParameterKey("baz"), Types.BIGINT);

        // successful case
        DataSourceProxyAssertions.assertThat(ce).containsParamName("foo");
        DataSourceProxyAssertions.assertThat(ce).containsParamName("bar");

        // missing param key
        try {
            DataSourceProxyAssertions.assertThat(ce).containsParamName("zzz");
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: callable parameter keys\n<[1, 2, 3, bar, baz, foo]>\nto contain:\n<[zzz]>\nbut could not find:\n<[zzz]>");
        }
    }


    @Test
    public void paramKeyIndexes() {
        CallableExecution ce = new CallableExecution();
        ce.getParams().put(new ParameterKey(1), "foo");
        ce.getParams().put(new ParameterKey("foo"), "FOO");
        ce.getSetNullParams().put(new ParameterKey(2), Types.VARCHAR);
        ce.getSetNullParams().put(new ParameterKey("bar"), Types.DATE);
        ce.getOutParams().put(new ParameterKey(3), Types.BOOLEAN);
        ce.getOutParams().put(new ParameterKey("baz"), Types.BIGINT);


        // successful case
        DataSourceProxyAssertions.assertThat(ce).containsParamIndexes(1, 2, 3);
        DataSourceProxyAssertions.assertThat(ce).containsParamIndexes(2, 3, 1);
        DataSourceProxyAssertions.assertThat(ce).containsParamIndexes(2, 3);

        // missing one param key (index)
        try {
            DataSourceProxyAssertions.assertThat(ce).containsParamIndexes(1, 2, 100);
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: callable parameter keys\n<[1, 2, 3, bar, baz, foo]>\nto contain:\n<[1, 2, 100]>\nbut could not find:\n<[100]>");
        }

    }

    @Test
    public void paramKeyIndex() {
        CallableExecution ce = new CallableExecution();
        ce.getParams().put(new ParameterKey(1), "foo");
        ce.getParams().put(new ParameterKey("foo"), "FOO");
        ce.getSetNullParams().put(new ParameterKey(2), Types.VARCHAR);
        ce.getSetNullParams().put(new ParameterKey("bar"), Types.DATE);
        ce.getOutParams().put(new ParameterKey(3), Types.BOOLEAN);
        ce.getOutParams().put(new ParameterKey("baz"), Types.BIGINT);


        // successful case
        DataSourceProxyAssertions.assertThat(ce).containsParamIndex(1);
        DataSourceProxyAssertions.assertThat(ce).containsParamIndex(2);
        DataSourceProxyAssertions.assertThat(ce).containsParamIndex(3);

        // missing param key (index)
        try {
            DataSourceProxyAssertions.assertThat(ce).containsParamIndex(100);
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: callable parameter keys\n<[1, 2, 3, bar, baz, foo]>\nto contain:\n<[100]>\nbut could not find:\n<[100]>");
        }

    }

    @Test
    public void containsParamKeys() {
        CallableExecution ce = new CallableExecution();
        ce.getParams().put(new ParameterKey(1), "foo");
        ce.getParams().put(new ParameterKey("foo"), "FOO");
        ce.getSetNullParams().put(new ParameterKey(2), Types.VARCHAR);
        ce.getSetNullParams().put(new ParameterKey("bar"), Types.DATE);
        ce.getOutParams().put(new ParameterKey(3), Types.BOOLEAN);
        ce.getOutParams().put(new ParameterKey("baz"), Types.BIGINT);

        // successful case
        DataSourceProxyAssertions.assertThat(ce).containsParamKeys(1);
        DataSourceProxyAssertions.assertThat(ce).containsParamKeys("bar");
        DataSourceProxyAssertions.assertThat(ce).containsParamKeys(1, "bar");
        DataSourceProxyAssertions.assertThat(ce).containsParamKeys(1, 2, 3, "foo", "bar", "baz");

        // missing keys
        try {
            DataSourceProxyAssertions.assertThat(ce).containsParamKeys(1, 2, 100, "zzz", "bar");
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: callable parameter keys\n<[1, 2, 3, bar, baz, foo]>\nto contain:\n<[1, 2, 100, bar, zzz]>\nbut could not find:\n<[100, zzz]>");
        }

        // wrong key type
        try {
            DataSourceProxyAssertions.assertThat(ce).containsParamKeys((double) 10.01);
            fail("exception should be thrown");
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("param key should be int or String");
        }

    }

    @Test
    public void containsParamKey() {
        CallableExecution ce = new CallableExecution();
        ce.getParams().put(new ParameterKey(1), "foo");
        ce.getParams().put(new ParameterKey("foo"), "FOO");
        ce.getSetNullParams().put(new ParameterKey(2), Types.VARCHAR);
        ce.getSetNullParams().put(new ParameterKey("bar"), Types.DATE);
        ce.getOutParams().put(new ParameterKey(3), Types.BOOLEAN);
        ce.getOutParams().put(new ParameterKey("baz"), Types.BIGINT);

        // successful case
        DataSourceProxyAssertions.assertThat(ce).containsParamKey(1);
        DataSourceProxyAssertions.assertThat(ce).containsParamKey(2);
        DataSourceProxyAssertions.assertThat(ce).containsParamKey(3);
        DataSourceProxyAssertions.assertThat(ce).containsParamKey("foo");
        DataSourceProxyAssertions.assertThat(ce).containsParamKey("bar");
        DataSourceProxyAssertions.assertThat(ce).containsParamKey("baz");

        // missing keys
        try {
            DataSourceProxyAssertions.assertThat(ce).containsParamKey(100);
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: callable parameter keys\n<[1, 2, 3, bar, baz, foo]>\nto contain:\n<[100]>\nbut could not find:\n<[100]>");
        }
        try {
            DataSourceProxyAssertions.assertThat(ce).containsParamKey("zzz");
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: callable parameter keys\n<[1, 2, 3, bar, baz, foo]>\nto contain:\n<[zzz]>\nbut could not find:\n<[zzz]>");
        }

        // wrong key type
        try {
            DataSourceProxyAssertions.assertThat(ce).containsParamKey((double) 10.01);
            fail("exception should be thrown");
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("param key should be int or String");
        }

    }

    @Test
    public void testContainsParam() {
        CallableExecution ce = new CallableExecution();
        ce.getParams().put(new ParameterKey(1), "foo");
        ce.getParams().put(new ParameterKey("foo"), "FOO");
        ce.getSetNullParams().put(new ParameterKey(2), Types.VARCHAR);
        ce.getSetNullParams().put(new ParameterKey("bar"), Types.DATE);
        ce.getOutParams().put(new ParameterKey(3), Types.BOOLEAN);
        ce.getOutParams().put(new ParameterKey("baz"), Types.BIGINT);

        // successful call
        DataSourceProxyAssertions.assertThat(ce).containsParam(1, "foo").containsParam("foo", "FOO");

        // index with wrong value
        try {
            DataSourceProxyAssertions.assertThat(ce).containsParam(1, "WRONG");
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: parameters \n<{1=foo, 2=NULL(VARCHAR), 3=OUTPUT(BOOLEAN[16]), bar=NULL(DATE), baz=OUTPUT(BIGINT[-5]), foo=FOO}>\nto contain:\n<[1=WRONG]>\nbut could not find:\n<[1=WRONG]>");
        }

        // name with wrong value
        try {
            DataSourceProxyAssertions.assertThat(ce).containsParam("foo", "WRONG");
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: parameters \n<{1=foo, 2=NULL(VARCHAR), 3=OUTPUT(BOOLEAN[16]), bar=NULL(DATE), baz=OUTPUT(BIGINT[-5]), foo=FOO}>\nto contain:\n<[foo=WRONG]>\nbut could not find:\n<[foo=WRONG]>");
        }

        // no index and value
        try {
            DataSourceProxyAssertions.assertThat(ce).containsParam(100, "WRONG");
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: callable parameter keys\n<[1, 2, 3, bar, baz, foo]>\nto contain:\n<[100]>\nbut could not find:\n<[100]>");
        }

        // no name and value
        try {
            DataSourceProxyAssertions.assertThat(ce).containsParam("WRONG", "WRONG");
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: callable parameter keys\n<[1, 2, 3, bar, baz, foo]>\nto contain:\n<[WRONG]>\nbut could not find:\n<[WRONG]>");
        }

        // index null param
        try {
            DataSourceProxyAssertions.assertThat(ce).containsParam(2, Types.VARCHAR);
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: parameters \n<{1=foo, 2=NULL(VARCHAR), 3=OUTPUT(BOOLEAN[16]), bar=NULL(DATE), baz=OUTPUT(BIGINT[-5]), foo=FOO}>\nto contain:\n<[2=12]>\nbut could not find:\n<[2=12]>");
        }

        // name null param
        try {
            DataSourceProxyAssertions.assertThat(ce).containsParam("bar", Types.DATE);
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: parameters \n<{1=foo, 2=NULL(VARCHAR), 3=OUTPUT(BOOLEAN[16]), bar=NULL(DATE), baz=OUTPUT(BIGINT[-5]), foo=FOO}>\nto contain:\n<[bar=91]>\nbut could not find:\n<[bar=91]>");
        }

        // index out param
        try {
            DataSourceProxyAssertions.assertThat(ce).containsParam(3, Types.BOOLEAN);
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: parameters \n<{1=foo, 2=NULL(VARCHAR), 3=OUTPUT(BOOLEAN[16]), bar=NULL(DATE), baz=OUTPUT(BIGINT[-5]), foo=FOO}>\nto contain:\n<[3=16]>\nbut could not find:\n<[3=16]>");
        }

        // name out param
        try {
            DataSourceProxyAssertions.assertThat(ce).containsParam("baz", Types.BIGINT);
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: parameters \n<{1=foo, 2=NULL(VARCHAR), 3=OUTPUT(BOOLEAN[16]), bar=NULL(DATE), baz=OUTPUT(BIGINT[-5]), foo=FOO}>\nto contain:\n<[baz=-5]>\nbut could not find:\n<[baz=-5]>");
        }

    }

}
