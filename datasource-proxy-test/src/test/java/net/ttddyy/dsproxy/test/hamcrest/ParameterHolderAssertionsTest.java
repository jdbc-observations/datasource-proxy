package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.ParameterByNameHolder;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramNames;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
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
}
