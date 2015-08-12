package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.StatementExecution;
import org.junit.Assert;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class ExecutionTypeMatcherTest {

    @Test
    public void testMatch() {
        StatementExecution se = new StatementExecution();
        ExecutionTypeMatcher matcher = new ExecutionTypeMatcher(ExecutionType.IS_STATEMENT);
        assertThat(matcher.matches(se)).isTrue();
    }

    @Test
    public void testUnmatchMessage() {
        StatementExecution se = new StatementExecution();
        ExecutionTypeMatcher matcher = new ExecutionTypeMatcher(ExecutionType.IS_CALLABLE);
        try {
            Assert.assertThat(se, matcher);
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: callable <1>\n     but: was statement");
        }
    }
}
