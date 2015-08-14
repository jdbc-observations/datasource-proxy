package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.QueryType;
import net.ttddyy.dsproxy.test.CallableBatchExecution;
import net.ttddyy.dsproxy.test.CallableExecution;
import net.ttddyy.dsproxy.test.PreparedBatchExecution;
import net.ttddyy.dsproxy.test.PreparedExecution;
import net.ttddyy.dsproxy.test.QueryExecution;
import net.ttddyy.dsproxy.test.StatementBatchExecution;
import net.ttddyy.dsproxy.test.StatementExecution;
import org.junit.Assert;
import org.junit.Test;

import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.batch;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.batchCallable;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.batchPrepared;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.batchStatement;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.callable;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.callableOrBatchCallable;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.delete;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.failure;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.insert;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.other;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.prepared;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.preparedOrBatchPrepared;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.select;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.statement;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.statementOrBatchStatement;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.success;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.update;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
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
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: success\n     but: was failure");
        }
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

        try {
            Assert.assertThat(qe, failure());
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: failure\n     but: was success");
        }

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
        Assert.assertThat(ce, callableOrBatchCallable());
        Assert.assertThat(cbe, callableOrBatchCallable());

        Assert.assertThat(sbe, batch());
        Assert.assertThat(pbe, batch());
        Assert.assertThat(cbe, batch());
    }

    @Test
    public void testQueryType() {
        QueryExecution select = mock(QueryExecution.class);
        QueryExecution insert = mock(QueryExecution.class);
        QueryExecution update = mock(QueryExecution.class);
        QueryExecution delete = mock(QueryExecution.class);
        QueryExecution other = mock(QueryExecution.class);
        given(select.getQueryType()).willReturn(QueryType.SELECT);
        given(insert.getQueryType()).willReturn(QueryType.INSERT);
        given(update.getQueryType()).willReturn(QueryType.UPDATE);
        given(delete.getQueryType()).willReturn(QueryType.DELETE);
        given(other.getQueryType()).willReturn(QueryType.OTHER);

        Assert.assertThat(select, select());
        Assert.assertThat(insert, insert());
        Assert.assertThat(update, update());
        Assert.assertThat(delete, delete());
        Assert.assertThat(other, other());
    }

    @Test
    public void testQueryTypeUnmatchedMessage() {
        QueryExecution select = mock(QueryExecution.class);
        given(select.getQueryType()).willReturn(QueryType.SELECT);

        try {
            Assert.assertThat(select, insert());
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: INSERT\n     but: was SELECT");
        }

    }
}
