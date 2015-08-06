package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.QueryExecution;
import org.junit.Assert;
import org.junit.Test;

import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.fail;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.success;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class QueryExecutionAssertionsTest {

    @Test
    public void testSuccess() {
        QueryExecution qe = mock(QueryExecution.class);
        when(qe.isSuccess()).thenReturn(true);

        Assert.assertThat(qe, success());
    }

    @Test
    public void testSuccessWithFailureMessage() {
        QueryExecution qe = mock(QueryExecution.class);
        when(qe.isSuccess()).thenReturn(false);

        try {
            Assert.assertThat(qe, success());
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: success\n     but: was failure");
        }
    }


    @Test
    public void testFail() {
        QueryExecution qe = mock(QueryExecution.class);
        when(qe.isSuccess()).thenReturn(false);

        Assert.assertThat(qe, fail());
    }

    @Test
    public void testFailWithFailureMessage() {
        QueryExecution qe = mock(QueryExecution.class);
        when(qe.isSuccess()).thenReturn(true);

        try {
            Assert.assertThat(qe, fail());
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: failure\n     but: was success");
        }
    }
}
