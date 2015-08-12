package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.CallableBatchExecution;
import net.ttddyy.dsproxy.test.CallableExecution;
import net.ttddyy.dsproxy.test.PreparedBatchExecution;
import net.ttddyy.dsproxy.test.PreparedExecution;
import net.ttddyy.dsproxy.test.QueryExecution;
import net.ttddyy.dsproxy.test.StatementBatchExecution;
import net.ttddyy.dsproxy.test.StatementExecution;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.batch;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.batchCallable;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.batchPrepared;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.batchStatement;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.callable;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.callabledOrBatchCallable;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.failure;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.prepared;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.preparedOrBatchPrepared;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.statement;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.statementOrBatchStatement;
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


    @Test
    public void testExecutions() {
        StatementExecution se = new StatementExecution();
        StatementBatchExecution sbe = new StatementBatchExecution();
        PreparedExecution pe = new PreparedExecution();
        PreparedBatchExecution pbe = new PreparedBatchExecution();
        CallableExecution ce = new CallableExecution();
        CallableBatchExecution cbe = new CallableBatchExecution();

        Assert.assertThat(se, statement());
        Assert.assertThat(sbe, batchStatement());
        Assert.assertThat(se, statementOrBatchStatement());
        Assert.assertThat(sbe, statementOrBatchStatement());

        Assert.assertThat(pe, prepared());
        Assert.assertThat(pbe, batchPrepared());
        Assert.assertThat(pe, preparedOrBatchPrepared());
        Assert.assertThat(pbe, preparedOrBatchPrepared());

        Assert.assertThat(ce, callable());
        Assert.assertThat(cbe, batchCallable());
        Assert.assertThat(ce, callabledOrBatchCallable());
        Assert.assertThat(cbe, callabledOrBatchCallable());

        Assert.assertThat(sbe, batch());
        Assert.assertThat(pbe, batch());
        Assert.assertThat(cbe, batch());
    }
}
