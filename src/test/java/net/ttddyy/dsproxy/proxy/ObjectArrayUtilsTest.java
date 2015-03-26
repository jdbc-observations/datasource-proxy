package net.ttddyy.dsproxy.proxy;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tadaya Tsuyukubo
 */
@RunWith(Parameterized.class)
public class ObjectArrayUtilsTest {

    @Parameterized.Parameters
    public static Object[][] getIsFirstArgStringData() {
        return new Object[][]{
                // expected, input string
                {true, new Object[]{"str"}},
                {true, new Object[]{"str", "str"}},
                {true, new Object[]{"", "str"}},
                {false, new Object[]{}}, // empty
                {false, new Object[]{null, "str"}},
                {false, new Object[]{1, "str"}},
                {false, new Object[]{1L, "str"}},
                {false, new Object[]{1.0, "str"}},
                {false, new Object[]{'c', "str"}}, // character
                {false, new Object[]{new Object(), "str"}},
        };
    }

    private boolean expected;
    private Object[] inputArray;

    public ObjectArrayUtilsTest(boolean expected, Object[] inputArray) {
        this.expected = expected;
        this.inputArray = inputArray;
    }

    @Test
    public void testIsFirstArgString() {
        boolean actual = ObjectArrayUtils.isFirstArgString(this.inputArray);
        assertThat(actual).isEqualTo(this.expected);
    }
}
