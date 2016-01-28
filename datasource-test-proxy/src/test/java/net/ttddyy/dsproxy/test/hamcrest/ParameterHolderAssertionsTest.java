package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.ParameterByIndexHolder;
import net.ttddyy.dsproxy.test.ParameterByNameHolder;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.param;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramAsArray;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramAsBigDecimal;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramAsBoolean;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramAsByte;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramAsBytes;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramAsDate;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramAsDouble;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramAsFloat;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramAsInteger;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramAsLong;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramAsShort;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramAsString;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramAsTime;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramAsTimestamp;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramIndexes;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramNames;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramsByIndex;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramsByName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class ParameterHolderAssertionsTest {

    @Test
    public void testParamNames() {
        ParameterByNameHolder holder = mock(ParameterByNameHolder.class);
        given(holder.getParamNames()).willReturn(Arrays.asList("foo", "bar"));

        Assert.assertThat(holder, paramNames(hasItem("foo")));
        Assert.assertThat(holder, paramNames("foo", "bar"));
    }

    @Test
    public void testParamNamesUnmatchedMessage() {
        ParameterByNameHolder holder = mock(ParameterByNameHolder.class);
        given(holder.getParamNames()).willReturn(Arrays.asList("foo", "bar"));

        try {
            Assert.assertThat(holder, paramNames(hasItem("BAZ")));
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: parameter names as a collection containing \"BAZ\"\n     but: mismatches were: [was \"foo\", was \"bar\"]");

        }
    }

    @Test
    public void testParamIndexes() {
        ParameterByIndexHolder holder = mock(ParameterByIndexHolder.class);
        given(holder.getParamIndexes()).willReturn(Arrays.asList(1, 2, 3));

        Assert.assertThat(holder, paramIndexes(hasItem(1)));
        Assert.assertThat(holder, paramIndexes(1, 3));
    }

    @Test
    public void testParamIndexesUnmatchedMessage() {
        ParameterByIndexHolder holder = mock(ParameterByIndexHolder.class);
        given(holder.getParamIndexes()).willReturn(Arrays.asList(1, 2));

        try {
            Assert.assertThat(holder, paramIndexes(hasItem(100)));
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: parameter indexes as a collection containing <100>\n     but: mismatches were: [was <1>, was <2>]");

        }
    }


    @Test
    public void testParamsByName() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("foo", 100);

        ParameterByNameHolder holder = mock(ParameterByNameHolder.class);
        given(holder.getSetParamsByName()).willReturn(map);

        Assert.assertThat(holder, paramsByName(hasEntry("foo", (Object) 100)));
    }

    @Test
    public void testParamsByNameUnmatchedMessage() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("foo", 100);
        map.put("bar", 200);

        ParameterByNameHolder holder = mock(ParameterByNameHolder.class);
        given(holder.getSetParamsByName()).willReturn(map);

        try {
            Assert.assertThat(holder, paramsByName(hasEntry("BAZ", (Object) 10)));
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: parameters as a map containing [\"BAZ\"-><10>]\n     but: map was [<bar=200>, <foo=100>]");

        }
    }

    @Test
    public void testParamsByIndex() {
        Map<Integer, Object> map = new HashMap<Integer, Object>();
        map.put(10, 100);

        ParameterByIndexHolder holder = mock(ParameterByIndexHolder.class);
        given(holder.getSetParamsByIndex()).willReturn(map);

        Assert.assertThat(holder, paramsByIndex(hasEntry(10, (Object) 100)));
    }

    @Test
    public void testParamsByIndexUnmatchedMessage() {
        Map<Integer, Object> map = new HashMap<Integer, Object>();
        map.put(10, 100);

        ParameterByIndexHolder holder = mock(ParameterByIndexHolder.class);
        given(holder.getSetParamsByIndex()).willReturn(map);

        try {
            Assert.assertThat(holder, paramsByIndex(hasEntry(30, (Object) 10)));
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: parameters as a map containing [<30>-><10>]\n     but: map was [<10=100>]");

        }
    }

    @Test
    public void paramWithIndex() {
        Map<Integer, Object> map = new HashMap<Integer, Object>();
        map.put(10, 100);

        ParameterByIndexHolder holder = mock(ParameterByIndexHolder.class);
        given(holder.getSetParamsByIndex()).willReturn(map);
        given(holder.getParamIndexes()).willReturn(new ArrayList<Integer>(map.keySet()));

        Assert.assertThat(holder, param(10, is((Object) 100)));
    }

    @Test
    public void paramWithIndexUnmatchedMessage() {
        Map<Integer, Object> map = new HashMap<Integer, Object>();
        map.put(10, 100);

        ParameterByIndexHolder holder = mock(ParameterByIndexHolder.class);
        given(holder.getSetParamsByIndex()).willReturn(map);
        given(holder.getParamIndexes()).willReturn(new ArrayList<Integer>(map.keySet()));

        try {
            Assert.assertThat(holder, param(10, is((Object) 101)));
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: params[10] is <101>\n     but: params[10] was <100>");

        }
    }

    @Test
    public void paramWithName() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("foo", 100);

        ParameterByNameHolder holder = mock(ParameterByNameHolder.class);
        given(holder.getSetParamsByName()).willReturn(map);
        given(holder.getParamNames()).willReturn(new ArrayList<String>(map.keySet()));

        Assert.assertThat(holder, param("foo", is((Object) 100)));
    }

    @Test
    public void paramWithNameUnmatchedMessage() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("foo", 100);

        ParameterByNameHolder holder = mock(ParameterByNameHolder.class);
        given(holder.getSetParamsByName()).willReturn(map);
        given(holder.getParamNames()).willReturn(new ArrayList<String>(map.keySet()));

        try {
            Assert.assertThat(holder, param("foo", is((Object) 101)));
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: params[foo] is <101>\n     but: params[foo] was <100>");

        }
    }

    @Test
    public void paramWithIndexAndType() {
        Map<Integer, Object> map = new HashMap<Integer, Object>();
        map.put(10, 100);

        ParameterByIndexHolder holder = mock(ParameterByIndexHolder.class);
        given(holder.getSetParamsByIndex()).willReturn(map);
        given(holder.getParamIndexes()).willReturn(new ArrayList<Integer>(map.keySet()));

        Assert.assertThat(holder, param(10, Integer.class, is(100)));
    }

    @Test
    public void paramWithIndexAndTypeUnmatchedMessage() {
        Map<Integer, Object> map = new HashMap<Integer, Object>();
        map.put(10, 100);

        ParameterByIndexHolder holder = mock(ParameterByIndexHolder.class);
        given(holder.getSetParamsByIndex()).willReturn(map);
        given(holder.getParamIndexes()).willReturn(new ArrayList<Integer>(map.keySet()));

        try {
            Assert.assertThat(holder, param(10, Integer.class, is(101)));
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: params[10] is <101>\n     but: params[10] was <100>");

        }
    }

    @Test
    public void paramWithNameAndType() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("foo", 100);

        ParameterByNameHolder holder = mock(ParameterByNameHolder.class);
        given(holder.getSetParamsByName()).willReturn(map);
        given(holder.getParamNames()).willReturn(new ArrayList<String>(map.keySet()));

        Assert.assertThat(holder, param("foo", Integer.class, is(100)));
    }

    @Test
    public void paramWithNameAndTypeUnmatchedMessage() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("foo", 100);

        ParameterByNameHolder holder = mock(ParameterByNameHolder.class);
        given(holder.getSetParamsByName()).willReturn(map);
        given(holder.getParamNames()).willReturn(new ArrayList<String>(map.keySet()));

        try {
            Assert.assertThat(holder, param("foo", Integer.class, is(101)));
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: params[foo] is <101>\n     but: params[foo] was <100>");

        }
    }

    @Test
    public void paramAsWithIndex() {
        byte b = 0xa;
        byte[] bytes = new byte[]{0xa, 0xb};
        Date date = new Date(1000);
        Time time = new Time(1000);
        Timestamp timestamp = new Timestamp(1000);
        Array array = mock(Array.class);

        Map<Integer, Object> map = new HashMap<Integer, Object>();
        map.put(10, "100");
        map.put(11, 101);
        map.put(12, 102L);
        map.put(13, (double) 103);
        map.put(14, (short) 104);
        map.put(15, false);
        map.put(16, b);
        map.put(17, (float) 105);
        map.put(18, new BigDecimal(106));
        map.put(19, bytes);
        map.put(20, date);
        map.put(21, time);
        map.put(22, timestamp);
        map.put(23, array);

        ParameterByIndexHolder holder = mock(ParameterByIndexHolder.class);
        given(holder.getSetParamsByIndex()).willReturn(map);

        Assert.assertThat(holder, paramAsString(10, is("100")));
        Assert.assertThat(holder, paramAsInteger(11, is(101)));
        Assert.assertThat(holder, paramAsLong(12, is(102L)));
        Assert.assertThat(holder, paramAsDouble(13, is((double) 103)));
        Assert.assertThat(holder, paramAsShort(14, is((short) 104)));
        Assert.assertThat(holder, paramAsBoolean(15, is(false)));
        Assert.assertThat(holder, paramAsByte(16, is(b)));
        Assert.assertThat(holder, paramAsFloat(17, is((float) 105)));
        Assert.assertThat(holder, paramAsBigDecimal(18, is(new BigDecimal(106))));
        Assert.assertThat(holder, paramAsBytes(19, is(bytes)));
        Assert.assertThat(holder, paramAsDate(20, is(date)));
        Assert.assertThat(holder, paramAsTime(21, is(time)));
        Assert.assertThat(holder, paramAsTimestamp(22, is(timestamp)));
        Assert.assertThat(holder, paramAsArray(23, sameInstance(array)));
    }

    @Test
    public void paramAsWithName() {
        byte b = 0xa;
        byte[] bytes = new byte[]{0xa, 0xb};
        Date date = new Date(1000);
        Time time = new Time(1000);
        Timestamp timestamp = new Timestamp(1000);
        Array array = mock(Array.class);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("aa", "100");
        map.put("bb", 101);
        map.put("cc", 102L);
        map.put("dd", (double) 103);
        map.put("ee", (short) 104);
        map.put("ff", false);
        map.put("gg", b);
        map.put("hh", (float) 105);
        map.put("ii", new BigDecimal(106));
        map.put("jj", bytes);
        map.put("kk", date);
        map.put("ll", time);
        map.put("mm", timestamp);
        map.put("nn", array);

        ParameterByNameHolder holder = mock(ParameterByNameHolder.class);
        given(holder.getSetParamsByName()).willReturn(map);

        Assert.assertThat(holder, paramAsString("aa", is("100")));
        Assert.assertThat(holder, paramAsInteger("bb", is(101)));
        Assert.assertThat(holder, paramAsLong("cc", is(102L)));
        Assert.assertThat(holder, paramAsDouble("dd", is((double) 103)));
        Assert.assertThat(holder, paramAsShort("ee", is((short) 104)));
        Assert.assertThat(holder, paramAsBoolean("ff", is(false)));
        Assert.assertThat(holder, paramAsByte("gg", is(b)));
        Assert.assertThat(holder, paramAsFloat("hh", is((float) 105)));
        Assert.assertThat(holder, paramAsBigDecimal("ii", is(new BigDecimal(106))));
        Assert.assertThat(holder, paramAsBytes("jj", is(bytes)));
        Assert.assertThat(holder, paramAsDate("kk", is(date)));
        Assert.assertThat(holder, paramAsTime("ll", is(time)));
        Assert.assertThat(holder, paramAsTimestamp("mm", is(timestamp)));
        Assert.assertThat(holder, paramAsArray("nn", sameInstance(array)));
    }


    @Test
    public void setNullByIndex() {
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        map.put(10, Types.ARRAY);
        map.put(1, 9999);

        ParameterByIndexHolder holder = mock(ParameterByIndexHolder.class);
        given(holder.getSetNullParamsByIndex()).willReturn(map);

        Assert.assertThat(holder, ParameterHolderAssertions.nullParam(10, Types.ARRAY));
        Assert.assertThat(holder, ParameterHolderAssertions.nullParam(10));

        // index out of range
        try {
            Assert.assertThat(holder, ParameterHolderAssertions.nullParam(100, Types.ARRAY));
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: parameter index 100\n     but: parameter index 100 doesn't exist.");
        }

        // type mismatch
        try {
            Assert.assertThat(holder, ParameterHolderAssertions.nullParam(10, Types.BIT));
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: params[10] is NULL[BIT:-7]\n     but: params[10] was NULL[ARRAY:2003]");
        }

        // unknown sql type
        try {
            Assert.assertThat(holder, ParameterHolderAssertions.nullParam(1, Types.BIT));
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: params[1] is NULL[BIT:-7]\n     but: params[1] was NULL[UNKNOWN:9999]");
        }


        // not exist
        try {
            Assert.assertThat(holder, ParameterHolderAssertions.nullParam(100));
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: params[100] is NULL\n     but: setNull indexes are [<1>, <10>]");
        }

    }

    @Test
    public void setNullByName() {
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("foo", Types.ARRAY);
        map.put("bar", 9999);

        ParameterByNameHolder holder = mock(ParameterByNameHolder.class);
        given(holder.getSetNullParamsByName()).willReturn(map);

        Assert.assertThat(holder, ParameterHolderAssertions.nullParam("foo", Types.ARRAY));
        Assert.assertThat(holder, ParameterHolderAssertions.nullParam("foo"));

        // no key
        try {
            Assert.assertThat(holder, ParameterHolderAssertions.nullParam("NOT_EXIST", Types.ARRAY));
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: parameter name NOT_EXIST\n     but: parameter name NOT_EXIST doesn't exist.");
        }

        // type mismatch
        try {
            Assert.assertThat(holder, ParameterHolderAssertions.nullParam("foo", Types.BIT));
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: params[foo] is NULL[BIT:-7]\n     but: params[foo] was NULL[ARRAY:2003]");
        }

        // unknown sql type
        try {
            Assert.assertThat(holder, ParameterHolderAssertions.nullParam("bar", Types.BIT));
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: params[bar] is NULL[BIT:-7]\n     but: params[bar] was NULL[UNKNOWN:9999]");
        }


        // not exist
        try {
            Assert.assertThat(holder, ParameterHolderAssertions.nullParam("NOT_EXIST"));
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: params[NOT_EXIST] is NULL\n     but: setNull names are [\"bar\", \"foo\"]");
        }

    }
}
