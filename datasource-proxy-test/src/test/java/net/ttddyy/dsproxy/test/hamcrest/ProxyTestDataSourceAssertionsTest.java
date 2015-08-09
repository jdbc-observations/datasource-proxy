package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.ProxyTestDataSource;
import net.ttddyy.dsproxy.test.QueryExecution;
import net.ttddyy.dsproxy.test.StatementBatchExecution;
import net.ttddyy.dsproxy.test.StatementExecution;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.ttddyy.dsproxy.test.hamcrest.ExecutionTypeMatcher.statement;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.count;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.executions;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.failure;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.success;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;


/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class ProxyTestDataSourceAssertionsTest {

    @Test
    public void testExecutions() {
        StatementExecution se = new StatementExecution();

        ProxyTestDataSource ds = new ProxyTestDataSource();
        ds.getQueryExecutions().add(se);

        Assert.assertThat(ds, executions(0, ExecutionType.IS_STATEMENT));
        Assert.assertThat(ds, executions(0, statement()));
        Assert.assertThat(ds, executions(0, is(statement())));
    }

    @Test
    public void testExecutionSuccess() {
        StatementExecution se = new StatementExecution();
        se.setSuccess(true);

        ProxyTestDataSource ds = new ProxyTestDataSource();
        ds.getQueryExecutions().add(se);

        Assert.assertThat(ds, executions(0, success()));
        Assert.assertThat(ds, executions(0, is(success())));
    }

    @Test
    public void testExecutionFail() {
        StatementExecution se = new StatementExecution();
        se.setSuccess(false);

        ProxyTestDataSource ds = new ProxyTestDataSource();
        ds.getQueryExecutions().add(se);

        Assert.assertThat(ds, executions(0, failure()));
        Assert.assertThat(ds, executions(0, is(failure())));
    }

    @Test
    public void testCount() {
        List<QueryExecution> list = mock(List.class);
        given(list.size()).willReturn(4);

        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getQueryExecutions()).willReturn(list);

        Assert.assertThat(ds, count(4));
    }

    @Test
    public void testCountAssertionMessage() {
        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getQueryExecutions()).willReturn(new ArrayList<QueryExecution>());  // return empty list

        try {
            Assert.assertThat(ds, count(4));
            fail("AssertionError should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: 4 query executions\n     but: was 0 query executions");
        }

    }


    @Test
    public void testFirstStatement() {
        StatementExecution se1 = new StatementExecution();
        se1.setQuery("query-1");
        StatementExecution se2 = new StatementExecution();
        se2.setQuery("query-2");

        ProxyTestDataSource ds = new ProxyTestDataSource();
        ds.getQueryExecutions().add(se1);
        ds.getQueryExecutions().add(se2);

//        assertThat(ds, firstStatement(query(is("query-1"))));
//        assertThat(ds, firstStatement(is(query(is("query-1")))));
    }

    // TODO: test for fistStatement when there is no StatementExecution

    @Test
    public void testFirstBatchStatement() {
        StatementBatchExecution sbe1 = new StatementBatchExecution();
        sbe1.setQueries(Arrays.asList("query1-1", "query1-2"));
        StatementBatchExecution sbe2 = new StatementBatchExecution();
        sbe2.setQueries(Arrays.asList("query2-1", "query2-2"));

        ProxyTestDataSource ds = new ProxyTestDataSource();
        ds.getQueryExecutions().add(sbe1);
        ds.getQueryExecutions().add(sbe2);

//        assertThat(ds, firstBatchStatement(queries(hasItems("query1-1", "query1-2"))));
//        assertThat(ds, firstBatchStatement(is(queries(hasItems("query1-1", "query1-2")))));
    }

    // TODO: test for fistBatchStatement when there is no StatementExecution
}
