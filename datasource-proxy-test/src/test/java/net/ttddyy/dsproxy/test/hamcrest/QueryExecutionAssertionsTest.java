package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.QueryExecution;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.failure;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.success;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class QueryExecutionAssertionsTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

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


        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("\nExpected: success\n     but: was failure");
        Assert.assertThat(qe, success());
    }


    @Test
    public void testFailure() {
        QueryExecution qe = mock(QueryExecution.class);
        when(qe.isSuccess()).thenReturn(false);

        Assert.assertThat(qe, failure());
    }

    @Test
    public void testFailWithFailureMessage() {
        QueryExecution qe = mock(QueryExecution.class);
        when(qe.isSuccess()).thenReturn(true);

        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("\nExpected: failure\n     but: was success");
        Assert.assertThat(qe, failure());
    }
}
