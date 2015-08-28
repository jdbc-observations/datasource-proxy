package net.ttddyy.dsproxy.test.hamcrest;

import org.junit.Assert;
import org.junit.Test;

import java.sql.Types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;


/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class SqlTypeMatcherTest {

    @Test
    public void matcher() {
        SqlTypeMatcher matcher = new SqlTypeMatcher(Types.CHAR);
        Assert.assertThat(1, matcher);

        try {
            Assert.assertThat(Types.BIT, matcher);
            fail("AssertionError should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: CHAR:1\n     but: BIT:-7");
        }
    }

    @Test
    public void messagePrefixAndSuffix(){
        SqlTypeMatcher matcher = new SqlTypeMatcher(Types.DATE, "[PRE]", "[POST]");
        try {
            Assert.assertThat(Types.TIME, matcher);
            fail("AssertionError should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: [PRE]DATE:91[POST]\n     but: [PRE]TIME:92[POST]");
        }

    }

}
