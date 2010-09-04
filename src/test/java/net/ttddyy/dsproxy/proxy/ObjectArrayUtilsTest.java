package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.proxy.ObjectArrayUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

/**
 * @author Tadaya Tsuyukubo
 */
public class ObjectArrayUtilsTest {


    @DataProvider
    public Object[][] getIsFirstArgStringData() {
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

    @Test(dataProvider = "getIsFirstArgStringData")
    public void testIsFirstArgString(boolean expected, Object[] inputArray) {
        boolean actual = ObjectArrayUtils.isFirstArgString(inputArray);
        assertEquals(actual, expected);
    }
}
