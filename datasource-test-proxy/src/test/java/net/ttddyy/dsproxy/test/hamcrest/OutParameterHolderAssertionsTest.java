package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.OutParameterHolder;
import org.junit.Assert;
import org.junit.Test;

import java.sql.JDBCType;
import java.sql.Types;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static net.ttddyy.dsproxy.test.hamcrest.OutParameterHolderAssertions.outParam;
import static net.ttddyy.dsproxy.test.hamcrest.OutParameterHolderAssertions.outParamIndexes;
import static net.ttddyy.dsproxy.test.hamcrest.OutParameterHolderAssertions.outParamNames;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class OutParameterHolderAssertionsTest {

    @Test
    public void testOutParamNames() {
        OutParameterHolder holder = mock(OutParameterHolder.class);
        given(holder.getOutParamNames()).willReturn(Arrays.asList("foo", "bar"));

        Assert.assertThat(holder, outParamNames(hasItem("foo")));
    }


    @Test
    public void testOutParamsByName() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("foo", Types.ARRAY);
        map.put("bar", JDBCType.BOOLEAN);

        OutParameterHolder holder = mock(OutParameterHolder.class);
        given(holder.getOutParamsByName()).willReturn(map);

        // successful
        Assert.assertThat(holder, outParam("foo", Types.ARRAY));
        Assert.assertThat(holder, outParam("bar", JDBCType.BOOLEAN));
    }

    @Test
    public void testOutParamsByNameFailureByExpectationMismatch() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("foo", Types.ARRAY);
        map.put("bar", JDBCType.BOOLEAN);

        OutParameterHolder holder = mock(OutParameterHolder.class);
        given(holder.getOutParamsByName()).willReturn(map);

        // expectation didn't match
        try {
            Assert.assertThat(holder, outParam("foo", Types.BIGINT));
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: out param foo is BIGINT:-5\n     but: out param foo was ARRAY:2003");
        }

        try {
            Assert.assertThat(holder, outParam("bar", JDBCType.CHAR));
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: out param bar is <CHAR>\n     but: out param bar was was <BOOLEAN>");
        }
    }

    @Test
    public void testOutParamsByNameFailureByKeyNotExist() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("foo", Types.ARRAY);
        map.put("bar", Types.ARRAY);
        map.put("baz", JDBCType.BOOLEAN);

        OutParameterHolder holder = mock(OutParameterHolder.class);
        given(holder.getOutParamsByName()).willReturn(map);

        // key didn't exist
        try {
            Assert.assertThat(holder, outParam("FOO", Types.ARRAY));
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: out param name FOO exist\n     but: out param names are [bar, baz, foo]");
        }

        try {
            Assert.assertThat(holder, outParam("BAZ", JDBCType.BOOLEAN));
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: out param name BAZ exist\n     but: out param names are [bar, baz, foo]");
        }
    }

    @Test
    public void testOutParamsByNameFailureByWrongValueType() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("foo", Types.ARRAY);
        map.put("bar", JDBCType.BOOLEAN);

        OutParameterHolder holder = mock(OutParameterHolder.class);
        given(holder.getOutParamsByName()).willReturn(map);

        // wrong type
        try {
            Assert.assertThat(holder, outParam("foo", JDBCType.ARRAY));
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: value of out param foo is instance of interface java.sql.SQLType\n     but: value 2003 was class java.lang.Integer");
        }

        try {
            Assert.assertThat(holder, outParam("bar", Types.BOOLEAN));
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: value of out param bar is instance of class java.lang.Integer\n     but: value BOOLEAN was class java.sql.JDBCType");
        }
    }

    @Test
    public void testOutParamIndexes() {
        OutParameterHolder holder = mock(OutParameterHolder.class);
        given(holder.getOutParamIndexes()).willReturn(Arrays.asList(1, 2, 3));

        Assert.assertThat(holder, outParamIndexes(hasItem(1)));
    }


    @Test
    public void testOutParamsByIndex() {
        Map<Integer, Object> map = new HashMap<Integer, Object>();
        map.put(1, Types.ARRAY);
        map.put(2, JDBCType.BOOLEAN);

        OutParameterHolder holder = mock(OutParameterHolder.class);
        given(holder.getOutParamsByIndex()).willReturn(map);

        // successful
        Assert.assertThat(holder, outParam(1, Types.ARRAY));
        Assert.assertThat(holder, outParam(2, JDBCType.BOOLEAN));
    }

    @Test
    public void testOutParamsByIndexFailureByExpectationMismatch() {
        Map<Integer, Object> map = new HashMap<Integer, Object>();
        map.put(1, Types.ARRAY);
        map.put(2, JDBCType.BOOLEAN);

        OutParameterHolder holder = mock(OutParameterHolder.class);
        given(holder.getOutParamsByIndex()).willReturn(map);

        // expectation didn't match
        try {
            Assert.assertThat(holder, outParam(1, Types.BIGINT));
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: out param index 1 is BIGINT:-5\n     but: out param index 1 was ARRAY:2003");
        }

        try {
            Assert.assertThat(holder, outParam(2, JDBCType.CHAR));
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: out param index 2 is <CHAR>\n     but: out param index 2 was was <BOOLEAN>");
        }
    }

    @Test
    public void testOutParamsByIndexFailureByKeyNotExist() {
        Map<Integer, Object> map = new HashMap<Integer, Object>();
        map.put(1, Types.ARRAY);
        map.put(2, Types.ARRAY);
        map.put(3, JDBCType.BOOLEAN);

        OutParameterHolder holder = mock(OutParameterHolder.class);
        given(holder.getOutParamsByIndex()).willReturn(map);

        // key didn't exist
        try {
            Assert.assertThat(holder, outParam(100, Types.ARRAY));
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: out param index 100 exist\n     but: out param indexes are [1, 2, 3]");
        }

        try {
            Assert.assertThat(holder, outParam(200, JDBCType.BOOLEAN));
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: out param index 200 exist\n     but: out param indexes are [1, 2, 3]");
        }
    }

    @Test
    public void testOutParamsByIndexFailureByWrongValueType() {
        Map<Integer, Object> map = new HashMap<Integer, Object>();
        map.put(1, Types.ARRAY);
        map.put(2, JDBCType.BOOLEAN);

        OutParameterHolder holder = mock(OutParameterHolder.class);
        given(holder.getOutParamsByIndex()).willReturn(map);

        // wrong type
        try {
            Assert.assertThat(holder, outParam(1, JDBCType.ARRAY));
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: value of out param index 1 is instance of interface java.sql.SQLType\n     but: value 2003 was class java.lang.Integer");
        }

        try {
            Assert.assertThat(holder, outParam(2, Types.BOOLEAN));
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: value of out param index 2 is instance of class java.lang.Integer\n     but: value BOOLEAN was class java.sql.JDBCType");
        }
    }

}
