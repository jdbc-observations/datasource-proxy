package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.CallableBatchExecution;
import net.ttddyy.dsproxy.test.CallableExecution;
import net.ttddyy.dsproxy.test.PreparedBatchExecution;
import net.ttddyy.dsproxy.test.PreparedExecution;
import net.ttddyy.dsproxy.test.ProxyTestDataSource;
import net.ttddyy.dsproxy.test.QueryExecution;
import net.ttddyy.dsproxy.test.QueryHolder;
import net.ttddyy.dsproxy.test.StatementBatchExecution;
import net.ttddyy.dsproxy.test.StatementExecution;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.batchCallableCount;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.batchPreparedCount;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.batchStatementCount;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.callableCount;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.callableOrBatchCallableCount;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.deleteCount;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.executionCount;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.executions;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.insertCount;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.otherCount;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.preparedCount;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.preparedOrBatchPreparedCount;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.selectCount;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.statementCount;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.statementOrBatchStatementCount;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.totalQueryCount;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.updateCount;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.failure;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.success;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;


/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class ProxyTestDataSourceAssertionsTest {


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
    public void executionsAssertionMessage() {
        StatementExecution se = new StatementExecution();
        se.setSuccess(false);

        ProxyTestDataSource ds = new ProxyTestDataSource();
        ds.getQueryExecutions().add(se);

        try {
            Assert.assertThat(ds, executions(0, success()));
            fail("AssertionError should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: queryExecutions[0] success\n     but: queryExecutions[0] was failure");
        }
    }

    @Test
    public void executionsAssertionMessageForOutOfIndex() {
        StatementExecution se = new StatementExecution();
        se.setSuccess(false);

        ProxyTestDataSource ds = new ProxyTestDataSource();
        ds.getQueryExecutions().add(se);

        try {
            Assert.assertThat(ds, executions(10, success()));
            fail("AssertionError should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: queryExecutions[10] exists\n     but: queryExecutions[] size was 1");
        }
    }


    @Test
    public void testTotalCount() {
        List<QueryExecution> queryExecutions = new ArrayList<QueryExecution>();
        queryExecutions.add(getMockSelectQueryExecution());  // select
        queryExecutions.add(getMockSelectQueryExecution());  // select
        queryExecutions.add(getMockInsertQueryExecution());  // insert
        queryExecutions.add(getMockUpdateQueryExecution());  // update
        queryExecutions.add(getMockDeleteQueryExecution());  // delete

        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getQueryExecutions()).willReturn(queryExecutions);

        Assert.assertThat(ds, totalQueryCount(5));
    }

    @Test
    public void testTotalCountAssertionMessage() {
        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getQueryExecutions()).willReturn(new ArrayList<QueryExecution>());  // return empty list

        try {
            Assert.assertThat(ds, totalQueryCount(4));
            fail("AssertionError should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: 4 query executions\n     but: was 0 query executions");
        }

    }

    @Test
    public void testSelectCount() {
        List<QueryExecution> queryExecutions = new ArrayList<QueryExecution>();
        queryExecutions.add(getMockSelectQueryExecution());  // select
        queryExecutions.add(getMockSelectQueryExecution());  // select
        queryExecutions.add(getMockDeleteQueryExecution());  // delete

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
        List<QueryExecution> queryExecutions = new ArrayList<QueryExecution>();
        queryExecutions.add(getMockInsertQueryExecution());  // insert
        queryExecutions.add(getMockInsertQueryExecution());  // insert
        queryExecutions.add(getMockDeleteQueryExecution());  // delete


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
        List<QueryExecution> queryExecutions = new ArrayList<QueryExecution>();
        queryExecutions.add(getMockUpdateQueryExecution());  // update
        queryExecutions.add(getMockUpdateQueryExecution());  // update
        queryExecutions.add(getMockSelectQueryExecution());  // select

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

        List<QueryExecution> queryExecutions = new ArrayList<QueryExecution>();
        queryExecutions.add(getMockDeleteQueryExecution());  // delete
        queryExecutions.add(getMockDeleteQueryExecution());  // delete
        queryExecutions.add(getMockSelectQueryExecution());  // select


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
        List<QueryExecution> queryExecutions = new ArrayList<QueryExecution>();
        queryExecutions.add(getMockOtherQueryExecution());  // other
        queryExecutions.add(getMockOtherQueryExecution());  // other
        queryExecutions.add(getMockDeleteQueryExecution());  // delete

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

    @Test
    public void withStatementBatchExecution() {
        StatementExecution se = new StatementExecution();
        se.setQuery("SELECT");

        // for batch statement, each query is counted
        StatementBatchExecution sbe = new StatementBatchExecution();
        sbe.setQueries(Arrays.asList("SELECT", "UPDATE"));

        ProxyTestDataSource ds = new ProxyTestDataSource();
        ds.getQueryExecutions().add(se);
        ds.getQueryExecutions().add(sbe);

        Assert.assertThat(ds, totalQueryCount(3));
        Assert.assertThat(ds, selectCount(2));
        Assert.assertThat(ds, updateCount(1));
    }


    private QueryExecution getMockSelectQueryExecution() {
        QueryHolder select = mock(QueryHolder.class, withSettings().extraInterfaces(QueryExecution.class));
        given(select.getQuery()).willReturn("SELECT...");
        return (QueryExecution) select;
    }

    private QueryExecution getMockInsertQueryExecution() {
        QueryHolder insert = mock(QueryHolder.class, withSettings().extraInterfaces(QueryExecution.class));
        given(insert.getQuery()).willReturn("INSERT...");
        return (QueryExecution) insert;
    }

    private QueryExecution getMockUpdateQueryExecution() {
        QueryHolder update = mock(QueryHolder.class, withSettings().extraInterfaces(QueryExecution.class));
        given(update.getQuery()).willReturn("UPDATE...");
        return (QueryExecution) update;
    }

    private QueryExecution getMockDeleteQueryExecution() {
        QueryHolder delete = mock(QueryHolder.class, withSettings().extraInterfaces(QueryExecution.class));
        given(delete.getQuery()).willReturn("DELETE...");
        return (QueryExecution) delete;
    }

    private QueryExecution getMockOtherQueryExecution() {
        QueryHolder other = mock(QueryHolder.class, withSettings().extraInterfaces(QueryExecution.class));
        given(other.getQuery()).willReturn("OTHER...");
        return (QueryExecution) other;
    }


    @Test
    public void queryExecutionCount() {
        List<QueryExecution> queryExecutions = new ArrayList<QueryExecution>();
        queryExecutions.add(new StatementExecution());
        queryExecutions.add(new StatementBatchExecution());
        queryExecutions.add(new PreparedExecution());
        queryExecutions.add(new PreparedBatchExecution());
        queryExecutions.add(new CallableExecution());
        queryExecutions.add(new CallableBatchExecution());

        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getQueryExecutions()).willReturn(queryExecutions);

        Assert.assertThat(ds, executionCount(6));

        try {
            Assert.assertThat(ds, executionCount(10));
            fail("AssertionError should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: queryExecutions size <10>\n     but: queryExecutions size was <6>");
        }
    }


    @Test
    public void testStatementCount() {
        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getStatements()).willReturn(Arrays.asList(new StatementExecution()));

        Assert.assertThat(ds, statementCount(1));

        try {
            Assert.assertThat(ds, statementCount(10));
            fail("AssertionError should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: StatementExecution size <10>\n     but: StatementExecution size was <1>");
        }
    }


    @Test
    public void testBatchStatementCount() {
        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getBatchStatements()).willReturn(Arrays.asList(new StatementBatchExecution()));

        Assert.assertThat(ds, batchStatementCount(1));

        try {
            Assert.assertThat(ds, batchStatementCount(10));
            fail("AssertionError should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: StatementBatchExecution size <10>\n     but: StatementBatchExecution size was <1>");
        }
    }


    @Test
    public void testStatementOrBatchStatementCount() {
        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getStatements()).willReturn(Arrays.asList(new StatementExecution()));
        given(ds.getBatchStatements()).willReturn(Arrays.asList(new StatementBatchExecution()));

        Assert.assertThat(ds, statementOrBatchStatementCount(2));

        try {
            Assert.assertThat(ds, statementOrBatchStatementCount(10));
            fail("AssertionError should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: StatementExecution or StatementBatchExecution size <10>\n     but: StatementExecution or StatementBatchExecution size was <2>");
        }
    }


    @Test
    public void testPreparedCount() {
        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getPrepareds()).willReturn(Arrays.asList(new PreparedExecution()));

        Assert.assertThat(ds, preparedCount(1));

        try {
            Assert.assertThat(ds, preparedCount(10));
            fail("AssertionError should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: PreparedExecution size <10>\n     but: PreparedExecution size was <1>");
        }
    }

    @Test
    public void testBatchPreparedCount() {
        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getBatchPrepareds()).willReturn(Arrays.asList(new PreparedBatchExecution()));

        Assert.assertThat(ds, batchPreparedCount(1));
        try {
            Assert.assertThat(ds, batchPreparedCount(10));
            fail("AssertionError should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: PreparedBatchExecution size <10>\n     but: PreparedBatchExecution size was <1>");
        }
    }

    @Test
    public void testPreparedOrBatchPreparedCount() {
        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getPrepareds()).willReturn(Arrays.asList(new PreparedExecution()));
        given(ds.getBatchPrepareds()).willReturn(Arrays.asList(new PreparedBatchExecution()));

        Assert.assertThat(ds, preparedOrBatchPreparedCount(2));

        try {
            Assert.assertThat(ds, preparedOrBatchPreparedCount(10));
            fail("AssertionError should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: PreparedExecution or PreparedBatchExecution size <10>\n     but: PreparedExecution or PreparedBatchExecution size was <2>");
        }

    }

    @Test
    public void testCallableCount() {
        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getCallables()).willReturn(Arrays.asList(new CallableExecution()));

        Assert.assertThat(ds, callableCount(1));

        try {
            Assert.assertThat(ds, callableCount(10));
            fail("AssertionError should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: CallableExecution size <10>\n     but: CallableExecution size was <1>");
        }
    }

    @Test
    public void testBatchCallableCount() {
        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getBatchCallables()).willReturn(Arrays.asList(new CallableBatchExecution()));

        Assert.assertThat(ds, batchCallableCount(1));
        try {
            Assert.assertThat(ds, batchCallableCount(10));
            fail("AssertionError should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: CallableBatchExecution size <10>\n     but: CallableBatchExecution size was <1>");
        }
    }

    @Test
    public void testCallableOrBatchCallableCount() {
        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getCallables()).willReturn(Arrays.asList(new CallableExecution()));
        given(ds.getBatchCallables()).willReturn(Arrays.asList(new CallableBatchExecution()));

        Assert.assertThat(ds, callableOrBatchCallableCount(2));

        try {
            Assert.assertThat(ds, callableOrBatchCallableCount(10));
            fail("AssertionError should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: CallableExecution or CallableBatchExecution size <10>\n     but: CallableExecution or CallableBatchExecution size was <2>");
        }

    }

}
