package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.PreparedExecution;
import org.junit.Before;
import org.junit.Test;

import java.sql.Types;

import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.createSetNull;
import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.createSetParam;
import static net.ttddyy.dsproxy.test.assertj.data.ExecutionParameter.nullParam;
import static net.ttddyy.dsproxy.test.assertj.data.ExecutionParameter.param;
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

    @Test
    public void testContainsParams() {
        PreparedExecution pe = new PreparedExecution();
        pe.getParameters().add(createSetParam(1, "foo"));
        pe.getParameters().add(createSetParam(2, "bar"));
        pe.getParameters().add(createSetNull(3, Types.BIT));

        // successful call
        DataSourceProxyAssertions.assertThat(pe).containsParams(param(1, "foo"), param(2, "bar"), nullParam(3, Types.BIT));
        DataSourceProxyAssertions.assertThat(pe).containsParams(param(1, "foo"));
        DataSourceProxyAssertions.assertThat(pe).containsParams(nullParam(3));
        DataSourceProxyAssertions.assertThat(pe).containsParams(nullParam(3, Types.BIT));

        // value is wrong for param index
        try {
            DataSourceProxyAssertions.assertThat(pe).containsParams(param(1, "WRONG"));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\n" +
                    "Expecting: parameters \n" +
                    "<{1=foo, 2=bar, 3=NULL(BIT)}>\n" +
                    "to contain:\n" +
                    "<[1=WRONG]>\n" +
                    "but could not find:\n" +
                    "<[1=WRONG]>");
        }

        // value is wrong for set-null-param index
        try {
            DataSourceProxyAssertions.assertThat(pe).containsParams(nullParam(3, Types.BOOLEAN));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\n" +
                    "Expecting: parameters \n" +
                    "<{1=foo, 2=bar, 3=NULL(BIT)}>\n" +
                    "to contain:\n" +
                    "<[3=NULL(BOOLEAN)]>\n" +
                    "but could not find:\n" +
                    "<[3=NULL(BOOLEAN)]>");
        }

        // specifying set-null for normal params
        try {
            DataSourceProxyAssertions.assertThat(pe).containsParams(nullParam(2));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\n" +
                    "Expecting: prepared parameter keys\n" +
                    "<[1, 2, 3]>\n" +
                    "(params=[1, 2], set-null=[3], register-out=[])\n" +
                    "to contain:\n" +
                    "<set-null=[2]>\n" +
                    "but could not find:\n" +
                    "<set-null=[2]>");
        }

        // no param index key
        try {
            DataSourceProxyAssertions.assertThat(pe).containsParams(param(100, "bar"));
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\n" +
                    "Expecting: prepared parameter keys\n" +
                    "<[1, 2, 3]>\n" +
                    "(params=[1, 2], set-null=[3], register-out=[])\n" +
                    "to contain:\n" +
                    "<params=[100]>\n" +
                    "but could not find:\n" +
                    "<params=[100]>");
        }

    }


    @Test
    public void testContainsNullParam() {
        PreparedExecution pe = new PreparedExecution();
        pe.getParameters().add(createSetParam(1, "foo"));
        pe.getParameters().add(createSetNull(2, Types.VARCHAR));

        // successful call
        DataSourceProxyAssertions.assertThat(pe)
                .containsNullParam(2)
                .containsNullParam(2, Types.VARCHAR)
        ;

        // index with wrong value
        try {
            DataSourceProxyAssertions.assertThat(pe).containsNullParam(2, Types.BIT);
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\n" +
                    "Expecting: parameters \n" +
                    "<{1=foo, 2=NULL(VARCHAR)}>\n" +
                    "to contain:\n" +
                    "<[2=NULL(BIT)]>\n" +
                    "but could not find:\n" +
                    "<[2=NULL(BIT)]>");
        }

        // no index and value
        try {
            DataSourceProxyAssertions.assertThat(pe).containsNullParam(100, Types.BIT);
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\n" +
                    "Expecting: prepared parameter keys\n" +
                    "<[1, 2]>\n" +
                    "(params=[1], set-null=[2], register-out=[])\n" +
                    "to contain:\n" +
                    "<set-null=[100]>\n" +
                    "but could not find:\n" +
                    "<set-null=[100]>");
        }

        // no index with no value
        try {
            DataSourceProxyAssertions.assertThat(pe).containsNullParam(100);
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\n" +
                    "Expecting: prepared parameter keys\n" +
                    "<[1, 2]>\n" +
                    "(params=[1], set-null=[2], register-out=[])\n" +
                    "to contain:\n" +
                    "<set-null=[100]>\n" +
                    "but could not find:\n" +
                    "<set-null=[100]>");
        }

        // specifying index for param (not for set-null)
        try {
            DataSourceProxyAssertions.assertThat(pe).containsNullParam(1);
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\n" +
                    "Expecting: prepared parameter keys\n" +
                    "<[1, 2]>\n" +
                    "(params=[1], set-null=[2], register-out=[])\n" +
                    "to contain:\n" +
                    "<set-null=[1]>\n" +
                    "but could not find:\n" +
                    "<set-null=[1]>");
        }

        // specifying index in out-param
        try {
            DataSourceProxyAssertions.assertThat(pe).containsNullParam(3, Types.BOOLEAN);
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\n" +
                    "Expecting: prepared parameter keys\n" +
                    "<[1, 2]>\n" +
                    "(params=[1], set-null=[2], register-out=[])\n" +
                    "to contain:\n" +
                    "<set-null=[3]>\n" +
                    "but could not find:\n" +
                    "<set-null=[3]>");
        }

    }

    @Test
    public void testParamIndex() {
        PreparedExecution pe = new PreparedExecution();
        pe.getParameters().add(createSetParam(1, "foo"));
        pe.getParameters().add(createSetNull(2, Types.VARCHAR));

        // successful case
        DataSourceProxyAssertions.assertThat(pe).containsParamIndex(1);
        DataSourceProxyAssertions.assertThat(pe).containsParamIndex(2);

        // missing param key (index)
        try {
            DataSourceProxyAssertions.assertThat(pe).containsParamIndex(100);
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\n" +
                    "Expecting: prepared parameter keys\n" +
                    "<[1, 2]>\n" +
                    "to contain:\n" +
                    "<[100]>\n" +
                    "but could not find:\n" +
                    "<[100]>");
        }

    }

    @Test
    public void testParamIndexes() {
        PreparedExecution pe = new PreparedExecution();
        pe.getParameters().add(createSetParam(1, "foo"));
        pe.getParameters().add(createSetNull(2, Types.VARCHAR));


        // successful case
        DataSourceProxyAssertions.assertThat(pe).containsParamIndexes(1);
        DataSourceProxyAssertions.assertThat(pe).containsParamIndexes(1, 2);

        // missing one param key (index)
        try {
            DataSourceProxyAssertions.assertThat(pe).containsParamIndexes(1, 2, 100);
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\n" +
                    "Expecting: prepared parameter keys\n" +
                    "<[1, 2]>\n" +
                    "to contain:\n" +
                    "<[1, 2, 100]>\n" +
                    "but could not find:\n" +
                    "<[100]>");
        }

    }

    @Test
    public void testContainsParamValuesExactly() {
        PreparedExecution pe = new PreparedExecution();
        pe.getParameters().add(createSetParam(2, 100));
        pe.getParameters().add(createSetParam(1, "foo"));
        pe.getParameters().add(createSetNull(3, Types.VARCHAR));


        // successful case
        DataSourceProxyAssertions.assertThat(pe).containsParamValuesExactly("foo", 100, null);

        // extra values
        try {
            DataSourceProxyAssertions.assertThat(pe).containsParamValuesExactly("foo", 100);
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\n" +
                    "Expecting: prepared parameter values\n" +
                    "<{1=foo, 2=100, 3=NULL(VARCHAR)}>\n" +
                    "to be exactly:\n" +
                    "<{1=foo, 2=100}>\n" +
                    "but missing:\n" +
                    "<{}>\n" +
                    "extra:\n" +
                    "<{3=NULL(VARCHAR)}>");
        }

        // missing values
        try {
            DataSourceProxyAssertions.assertThat(pe).containsParamValuesExactly("foo", 100, null, "bar", "baz");
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\n" +
                    "Expecting: prepared parameter values\n" +
                    "<{1=foo, 2=100, 3=NULL(VARCHAR)}>\n" +
                    "to be exactly:\n" +
                    "<{1=foo, 2=100, 3=NULL, 4=bar, 5=baz}>\n" +
                    "but missing:\n" +
                    "<{4=bar, 5=baz}>\n" +
                    "extra:\n" +
                    "<{}>");
        }

        // missing and extra values
        try {
            DataSourceProxyAssertions.assertThat(pe).containsParamValuesExactly("foo", 100, "baz");
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\n" +
                    "Expecting: prepared parameter values\n" +
                    "<{1=foo, 2=100, 3=NULL(VARCHAR)}>\n" +
                    "to be exactly:\n" +
                    "<{1=foo, 2=100, 3=baz}>\n" +
                    "but missing:\n" +
                    "<{3=baz}>\n" +
                    "extra:\n" +
                    "<{}>");
        }


        // expecting not-null but null
        try {
            DataSourceProxyAssertions.assertThat(pe).containsParamValuesExactly("foo", null, null);
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\n" +
                    "Expecting: prepared parameter values\n" +
                    "<{1=foo, 2=100, 3=NULL(VARCHAR)}>\n" +
                    "to be exactly:\n" +
                    "<{1=foo, 2=NULL, 3=NULL}>\n" +
                    "but missing:\n" +
                    "<{2=NULL}>\n" +
                    "extra:\n" +
                    "<{}>");
        }
        // expecting null but not-null
        try {
            DataSourceProxyAssertions.assertThat(pe).containsParamValuesExactly("foo", 100, 200);
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\n" +
                    "Expecting: prepared parameter values\n" +
                    "<{1=foo, 2=100, 3=NULL(VARCHAR)}>\n" +
                    "to be exactly:\n" +
                    "<{1=foo, 2=100, 3=200}>\n" +
                    "but missing:\n" +
                    "<{3=200}>\n" +
                    "extra:\n" +
                    "<{}>");
        }

    }

}
