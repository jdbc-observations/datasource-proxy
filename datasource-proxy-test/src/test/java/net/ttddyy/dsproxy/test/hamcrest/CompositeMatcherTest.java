package net.ttddyy.dsproxy.test.hamcrest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.fail;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class CompositeMatcherTest {

    @Test
    public void whenWrongExpectedTypeForCompositeMatcher() {
        Matcher<String> subMatcher = startsWith("foo");

        CompositeMatcher matcher = new CompositeMatcher<String, String>(subMatcher) {
            @Override
            public String getValue(String actual) {
                return null;
            }

            @Override
            public String getSubMatcherFailureDescriptionPrefix() {
                return "pre ";
            }
        };


        try {
            Assert.assertThat(100, matcher);
            fail("validation should fail");
        } catch (Throwable e) {
            assertThat(e).hasMessageContaining("Expected: java.lang.String\n     but: was a java.lang.Integer (<100>)");
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void whenWrongExpectedTypeForSubMatcher() {
        Matcher subMatcher = startsWith("foo");

        CompositeMatcher<String, Object> matcher = new CompositeMatcher<String, Object>(subMatcher) {
            @Override
            public Object getValue(String actual) {
                return 100;  // return int instead of String
            }

            @Override
            public String getSubMatcherFailureDescriptionPrefix() {
                return "pre ";
            }
        };


        try {
            Assert.assertThat("FOO", matcher);
            fail("validation should fail");
        } catch (Throwable e) {
            assertThat(e).hasMessageContaining("Expected: pre a string starting with \"foo\"\n     but: pre was a java.lang.Integer (<100>)");
        }
    }

    @Test
    public void whenValidationByCompositeMatcherFailed() {
        Matcher<String> subMatcher = startsWith("foo");

        CompositeMatcher<String, String> matcher = new CompositeMatcher<String, String>(subMatcher) {
            @Override
            public String getValue(String actual) {
                return "foo";
            }

            @Override
            protected boolean validateByThisMatcher(String item, Description expected, Description actual) {
                assertThat(item).isEqualTo("AAA");
                expected.appendText("expected");
                actual.appendText("actual");
                return false;
            }

            @Override
            public String getSubMatcherFailureDescriptionPrefix() {
                return "pre ";
            }
        };


        try {
            Assert.assertThat("AAA", matcher);
            fail("validateByThisMatcher method should fail");
        } catch (Throwable e) {
            assertThat(e).hasMessageContaining("Expected: expected\n     but: actual");
        }
    }

    @Test
    public void whenExpectationBySubMatcherFailed() {
        Matcher<String> subMatcher = startsWith("foo");

        CompositeMatcher<String, String> matcher = new CompositeMatcher<String, String>(subMatcher) {
            @Override
            public String getValue(String actual) {
                return "FOO";
            }

            @Override
            public String getSubMatcherFailureDescriptionPrefix() {
                return "pre ";
            }
        };


        try {
            Assert.assertThat("AAA", matcher);
            fail("validateByThisMatcher method should fail");
        } catch (Throwable e) {
            assertThat(e).hasMessageContaining("Expected: pre a string starting with \"foo\"\n     but: pre was \"FOO\"");
        }
    }
}
