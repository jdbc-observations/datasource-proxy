package net.ttddyy.dsproxy.proxy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tadaya Tsuyukubo
 */
public class ObjectArrayUtilsTest {

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

    @ParameterizedTest
    @MethodSource("getIsFirstArgStringData")
    public void testIsFirstArgString(boolean expected, Object[] inputArray) {
        boolean actual = ObjectArrayUtils.isFirstArgString(inputArray);
        assertThat(actual).isEqualTo(expected);
    }
}
