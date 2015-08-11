package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.QueryType;
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
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.deleteCount;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.executions;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.insertCount;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.otherCount;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.selectCount;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.totalCount;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.updateCount;
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
    public void testTotalCount() {
        List<QueryExecution> list = mock(List.class);
        given(list.size()).willReturn(4);

        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getQueryExecutions()).willReturn(list);

        Assert.assertThat(ds, totalCount(4));
    }

    @Test
    public void testTotalCountAssertionMessage() {
        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getQueryExecutions()).willReturn(new ArrayList<QueryExecution>());  // return empty list

        try {
            Assert.assertThat(ds, totalCount(4));
            fail("AssertionError should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: 4 query executions\n     but: was 0 query executions");
        }

    }

    @Test
    public void testSelectCount() {
        QueryExecution select1 = mock(QueryExecution.class);
        given(select1.getQueryType()).willReturn(QueryType.SELECT);
        QueryExecution select2 = mock(QueryExecution.class);
        given(select2.getQueryType()).willReturn(QueryType.SELECT);
        QueryExecution delete = mock(QueryExecution.class);
        given(delete.getQueryType()).willReturn(QueryType.DELETE);

        List<QueryExecution> queryExecutions = new ArrayList<QueryExecution>();
        queryExecutions.addAll(Arrays.asList(select1, delete, select2));

        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getQueryExecutions()).willReturn(queryExecutions);

        Assert.assertThat(ds, selectCount(2));
    }

    @Test
    public void testSelectCountAssertionMessage() {
        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getQueryExecutions()).willReturn(new ArrayList<QueryExecution>());  // return empty list

        try {
            Assert.assertThat(ds, selectCount(4));
            fail("AssertionError should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: 4 SELECT query executions\n     but: was 0 SELECT query executions");
        }
    }

    @Test
    public void testInsertCount() {
        QueryExecution insert1 = mock(QueryExecution.class);
        given(insert1.getQueryType()).willReturn(QueryType.INSERT);
        QueryExecution insert2 = mock(QueryExecution.class);
        given(insert2.getQueryType()).willReturn(QueryType.INSERT);
        QueryExecution delete = mock(QueryExecution.class);
        given(delete.getQueryType()).willReturn(QueryType.DELETE);

        List<QueryExecution> queryExecutions = new ArrayList<QueryExecution>();
        queryExecutions.addAll(Arrays.asList(insert1, delete, insert2));

        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getQueryExecutions()).willReturn(queryExecutions);

        Assert.assertThat(ds, insertCount(2));
    }

    @Test
    public void testInsertCountAssertionMessage() {
        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getQueryExecutions()).willReturn(new ArrayList<QueryExecution>());  // return empty list

        try {
            Assert.assertThat(ds, insertCount(4));
            fail("AssertionError should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: 4 INSERT query executions\n     but: was 0 INSERT query executions");
        }
    }

    @Test
    public void testUpdateCount() {
        QueryExecution update1 = mock(QueryExecution.class);
        given(update1.getQueryType()).willReturn(QueryType.UPDATE);
        QueryExecution update2 = mock(QueryExecution.class);
        given(update2.getQueryType()).willReturn(QueryType.UPDATE);
        QueryExecution delete = mock(QueryExecution.class);
        given(delete.getQueryType()).willReturn(QueryType.DELETE);

        List<QueryExecution> queryExecutions = new ArrayList<QueryExecution>();
        queryExecutions.addAll(Arrays.asList(update1, delete, update2));

        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getQueryExecutions()).willReturn(queryExecutions);

        Assert.assertThat(ds, updateCount(2));
    }

    @Test
    public void testUpdateCountAssertionMessage() {
        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getQueryExecutions()).willReturn(new ArrayList<QueryExecution>());  // return empty list

        try {
            Assert.assertThat(ds, updateCount(4));
            fail("AssertionError should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: 4 UPDATE query executions\n     but: was 0 UPDATE query executions");
        }
    }

    @Test
    public void testDeleteCount() {
        QueryExecution delete1 = mock(QueryExecution.class);
        given(delete1.getQueryType()).willReturn(QueryType.DELETE);
        QueryExecution delete2 = mock(QueryExecution.class);
        given(delete2.getQueryType()).willReturn(QueryType.DELETE);
        QueryExecution select = mock(QueryExecution.class);
        given(select.getQueryType()).willReturn(QueryType.SELECT);

        List<QueryExecution> queryExecutions = new ArrayList<QueryExecution>();
        queryExecutions.addAll(Arrays.asList(delete1, select, delete2));

        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getQueryExecutions()).willReturn(queryExecutions);

        Assert.assertThat(ds, deleteCount(2));
    }

    @Test
    public void testDeleteCountAssertionMessage() {
        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getQueryExecutions()).willReturn(new ArrayList<QueryExecution>());  // return empty list

        try {
            Assert.assertThat(ds, deleteCount(4));
            fail("AssertionError should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: 4 DELETE query executions\n     but: was 0 DELETE query executions");
        }
    }

    @Test
    public void testOtherCount() {
        QueryExecution other1 = mock(QueryExecution.class);
        given(other1.getQueryType()).willReturn(QueryType.OTHER);
        QueryExecution other2 = mock(QueryExecution.class);
        given(other2.getQueryType()).willReturn(QueryType.OTHER);
        QueryExecution delete = mock(QueryExecution.class);
        given(delete.getQueryType()).willReturn(QueryType.DELETE);

        List<QueryExecution> queryExecutions = new ArrayList<QueryExecution>();
        queryExecutions.addAll(Arrays.asList(other1, delete, other2));

        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getQueryExecutions()).willReturn(queryExecutions);

        Assert.assertThat(ds, otherCount(2));
    }

    @Test
    public void testOtherCountAssertionMessage() {
        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getQueryExecutions()).willReturn(new ArrayList<QueryExecution>());  // return empty list

        try {
            Assert.assertThat(ds, otherCount(4));
            fail("AssertionError should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: 4 OTHER query executions\n     but: was 0 OTHER query executions");
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
