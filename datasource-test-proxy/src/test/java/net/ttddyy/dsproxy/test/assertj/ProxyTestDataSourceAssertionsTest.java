package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.CallableBatchExecution;
import net.ttddyy.dsproxy.test.CallableExecution;
import net.ttddyy.dsproxy.test.PreparedBatchExecution;
import net.ttddyy.dsproxy.test.PreparedExecution;
import net.ttddyy.dsproxy.test.ProxyTestDataSource;
import net.ttddyy.dsproxy.test.QueryExecution;
import net.ttddyy.dsproxy.test.StatementBatchExecution;
import net.ttddyy.dsproxy.test.StatementExecution;
import net.ttddyy.dsproxy.test.hamcrest.ExecutionType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Index.atIndex;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class ProxyTestDataSourceAssertionsTest {

    @Test
    public void hasExecutionType() {
        QueryExecution qe = new StatementExecution();

        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getQueryExecutions()).willReturn(Arrays.asList(qe, qe, qe));

        new ProxyTestDataSourceAssert(ds).hasExecutionType(ExecutionType.IS_STATEMENT, atIndex(0));

        // error message check
        try {
            new ProxyTestDataSourceAssert(ds).hasExecutionType(ExecutionType.IS_STATEMENT, atIndex(3));
            fail("assertion should failed");
        } catch (AssertionError error) {
            assertThat(error.getMessage()).isEqualTo("Expecting: index <3> is less than the size of query executions <3>");
        }

        // error message check
        try {
            new ProxyTestDataSourceAssert(ds).hasExecutionType(ExecutionType.IS_CALLABLE, atIndex(0));
            fail("assertion should failed");
        } catch (AssertionError error) {
            assertThat(error.getMessage()).contains("an instance of any of:\n <[net.ttddyy.dsproxy.test.CallableExecution]>");
        }

    }

    @Test
    public void hasExecutionCount() {
        QueryExecution qe = new StatementExecution();

        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getQueryExecutions()).willReturn(Arrays.asList(qe, qe, qe));

        new ProxyTestDataSourceAssert(ds).hasExecutionCount(3);

        try {
            new ProxyTestDataSourceAssert(ds).hasExecutionCount(100);
            fail("assertion should failed");
        } catch (AssertionError ex) {
            assertThat(ex.getMessage()).isEqualTo("Expected executions size: <100> but was <3>");
        }

    }

    @Test
    public void hasExecutionCountWithEmpty() {
        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getQueryExecutions()).willReturn(new ArrayList<QueryExecution>());

        new ProxyTestDataSourceAssert(ds).hasExecutionCount(0);
    }

    @Test
    public void hasStatementsCount() {
        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getStatements()).willReturn(Arrays.asList(mock(StatementExecution.class), mock(StatementExecution.class)));

        new ProxyTestDataSourceAssert(ds).hasStatementCount(2);

        try {
            new ProxyTestDataSourceAssert(ds).hasStatementCount(100);
            fail("assertion should failed");
        } catch (AssertionError ex) {
            assertThat(ex.getMessage()).isEqualTo("Expected statement executions size: <100> but was <2>");
        }

    }

    @Test
    public void hasBatchStatementsCount() {
        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getBatchStatements()).willReturn(Arrays.asList(mock(StatementBatchExecution.class), mock(StatementBatchExecution.class)));

        new ProxyTestDataSourceAssert(ds).hasBatchStatementCount(2);

        try {
            new ProxyTestDataSourceAssert(ds).hasBatchStatementCount(100);
            fail("assertion should failed");
        } catch (AssertionError ex) {
            assertThat(ex.getMessage()).isEqualTo("Expected batch statement executions size: <100> but was <2>");
        }

    }

    @Test
    public void hasStatementOrBatchStatementsCount() {
        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getStatements()).willReturn(Arrays.asList(mock(StatementExecution.class)));
        given(ds.getBatchStatements()).willReturn(Arrays.asList(mock(StatementBatchExecution.class)));

        new ProxyTestDataSourceAssert(ds).hasStatementOrBatchStatementCount(2);

        try {
            new ProxyTestDataSourceAssert(ds).hasStatementOrBatchStatementCount(100);
            fail("assertion should failed");
        } catch (AssertionError ex) {
            assertThat(ex.getMessage()).isEqualTo("Expected statement or batch statement executions size: <100> but was <2>");
        }

    }

    @Test
    public void hasPreparedCount() {
        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getPrepareds()).willReturn(Arrays.asList(mock(PreparedExecution.class), mock(PreparedExecution.class)));

        new ProxyTestDataSourceAssert(ds).hasPreparedCount(2);

        try {
            new ProxyTestDataSourceAssert(ds).hasPreparedCount(100);
            fail("assertion should failed");
        } catch (AssertionError ex) {
            assertThat(ex.getMessage()).isEqualTo("Expected prepared executions size: <100> but was <2>");
        }

    }

    @Test
    public void hasBatchPreparedCount() {
        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getBatchPrepareds()).willReturn(Arrays.asList(mock(PreparedBatchExecution.class), mock(PreparedBatchExecution.class)));

        new ProxyTestDataSourceAssert(ds).hasBatchPreparedCount(2);

        try {
            new ProxyTestDataSourceAssert(ds).hasBatchPreparedCount(100);
            fail("assertion should failed");
        } catch (AssertionError ex) {
            assertThat(ex.getMessage()).isEqualTo("Expected batch prepared executions size: <100> but was <2>");
        }
    }

    @Test
    public void hasPreparedOrBatchPreparedCount() {
        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getPrepareds()).willReturn(Arrays.asList(mock(PreparedExecution.class)));
        given(ds.getBatchPrepareds()).willReturn(Arrays.asList(mock(PreparedBatchExecution.class)));

        new ProxyTestDataSourceAssert(ds).hasPreparedOrBatchPreparedCount(2);

        try {
            new ProxyTestDataSourceAssert(ds).hasPreparedOrBatchPreparedCount(100);
            fail("assertion should failed");
        } catch (AssertionError ex) {
            assertThat(ex.getMessage()).isEqualTo("Expected prepared or batch prepared executions size: <100> but was <2>");
        }
    }


    @Test
    public void hasCallableCount() {
        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getCallables()).willReturn(Arrays.asList(mock(CallableExecution.class), mock(CallableExecution.class)));

        new ProxyTestDataSourceAssert(ds).hasCallableCount(2);

        try {
            new ProxyTestDataSourceAssert(ds).hasCallableCount(100);
            fail("assertion should failed");
        } catch (AssertionError ex) {
            assertThat(ex.getMessage()).isEqualTo("Expected callable executions size: <100> but was <2>");
        }

    }

    @Test
    public void hasBatchCallableCount() {
        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getBatchCallables()).willReturn(Arrays.asList(mock(CallableBatchExecution.class), mock(CallableBatchExecution.class)));

        new ProxyTestDataSourceAssert(ds).hasBatchCallableCount(2);

        try {
            new ProxyTestDataSourceAssert(ds).hasBatchCallableCount(100);
            fail("assertion should failed");
        } catch (AssertionError ex) {
            assertThat(ex.getMessage()).isEqualTo("Expected batch callable executions size: <100> but was <2>");
        }
    }

    @Test
    public void hasCallableOrBatchCallableCount() {
        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getCallables()).willReturn(Arrays.asList(mock(CallableExecution.class)));
        given(ds.getBatchCallables()).willReturn(Arrays.asList(mock(CallableBatchExecution.class)));

        new ProxyTestDataSourceAssert(ds).hasCallableOrBatchCallableCount(2);

        try {
            new ProxyTestDataSourceAssert(ds).hasCallableOrBatchCallableCount(100);
            fail("assertion should failed");
        } catch (AssertionError ex) {
            assertThat(ex.getMessage()).isEqualTo("Expected callable or batch callable executions size: <100> but was <2>");
        }
    }

    @Test
    public void hasTotalQueryCount() {
        StatementExecution se = mock(StatementExecution.class);
        given(se.getQuery()).willReturn("SELECT 1");

        StatementBatchExecution sbe = mock(StatementBatchExecution.class);
        given(sbe.getQueries()).willReturn(Arrays.asList("SELECT 2", "SELECT 3333333333"));


        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getQueryExecutions()).willReturn(Arrays.asList((QueryExecution) se, sbe));

        new ProxyTestDataSourceAssert(ds).hasTotalQueryCount(3);

        try {
            new ProxyTestDataSourceAssert(ds).hasTotalQueryCount(10);
            fail("assertion should failed");
        } catch (AssertionError ex) {
            assertThat(ex.getMessage()).isEqualTo("Expected query count: <10> but was <3>: [SELECT 1, SELECT 2, SELECT 33333...]");
        }
    }

    @Test
    public void hasSelectQueryCount() {
        StatementExecution se = mock(StatementExecution.class);
        given(se.getQuery()).willReturn("SELECT 1");

        StatementBatchExecution sbe = mock(StatementBatchExecution.class);
        given(sbe.getQueries()).willReturn(Arrays.asList("SELECT 2", "SELECT 3333333333"));

        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getQueryExecutions()).willReturn(Arrays.asList((QueryExecution) se, sbe));

        new ProxyTestDataSourceAssert(ds).hasSelectCount(3);

        try {
            new ProxyTestDataSourceAssert(ds).hasSelectCount(10);
            fail("assertion should failed");
        } catch (AssertionError ex) {
            assertThat(ex.getMessage()).isEqualTo("Expected SELECT count: <10> but was <3>: [SELECT 1, SELECT 2, SELECT 33333...]");
        }
    }

    @Test
    public void hasInsertQueryCount() {
        StatementExecution se = mock(StatementExecution.class);
        given(se.getQuery()).willReturn("INSERT 1");

        StatementBatchExecution sbe = mock(StatementBatchExecution.class);
        given(sbe.getQueries()).willReturn(Arrays.asList("INSERT 2", "INSERT 3333333333"));

        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getQueryExecutions()).willReturn(Arrays.asList((QueryExecution) se, sbe));

        new ProxyTestDataSourceAssert(ds).hasInsertCount(3);

        try {
            new ProxyTestDataSourceAssert(ds).hasInsertCount(10);
            fail("assertion should failed");
        } catch (AssertionError ex) {
            assertThat(ex.getMessage()).isEqualTo("Expected INSERT count: <10> but was <3>: [INSERT 1, INSERT 2, INSERT 33333...]");
        }
    }

    @Test
    public void hasUpdateQueryCount() {
        StatementExecution se = mock(StatementExecution.class);
        given(se.getQuery()).willReturn("UPDATE 1");

        StatementBatchExecution sbe = mock(StatementBatchExecution.class);
        given(sbe.getQueries()).willReturn(Arrays.asList("UPDATE 2", "UPDATE 3333333333"));

        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getQueryExecutions()).willReturn(Arrays.asList((QueryExecution) se, sbe));

        new ProxyTestDataSourceAssert(ds).hasUpdateCount(3);

        try {
            new ProxyTestDataSourceAssert(ds).hasUpdateCount(10);
            fail("assertion should failed");
        } catch (AssertionError ex) {
            assertThat(ex.getMessage()).isEqualTo("Expected UPDATE count: <10> but was <3>: [UPDATE 1, UPDATE 2, UPDATE 33333...]");
        }
    }

    @Test
    public void hasDeleteQueryCount() {
        StatementExecution se = mock(StatementExecution.class);
        given(se.getQuery()).willReturn("DELETE 1");

        StatementBatchExecution sbe = mock(StatementBatchExecution.class);
        given(sbe.getQueries()).willReturn(Arrays.asList("DELETE 2", "DELETE 3333333333"));

        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getQueryExecutions()).willReturn(Arrays.asList((QueryExecution) se, sbe));

        new ProxyTestDataSourceAssert(ds).hasDeleteCount(3);

        try {
            new ProxyTestDataSourceAssert(ds).hasDeleteCount(10);
            fail("assertion should failed");
        } catch (AssertionError ex) {
            assertThat(ex.getMessage()).isEqualTo("Expected DELETE count: <10> but was <3>: [DELETE 1, DELETE 2, DELETE 33333...]");
        }
    }

    @Test
    public void hasOtherQueryCount() {
        StatementExecution se = mock(StatementExecution.class);
        given(se.getQuery()).willReturn("OTHER 1");

        StatementBatchExecution sbe = mock(StatementBatchExecution.class);
        given(sbe.getQueries()).willReturn(Arrays.asList("OTHER 2", "OTHER 3333333333"));

        ProxyTestDataSource ds = mock(ProxyTestDataSource.class);
        given(ds.getQueryExecutions()).willReturn(Arrays.asList((QueryExecution) se, sbe));

        new ProxyTestDataSourceAssert(ds).hasOtherCount(3);

        try {
            new ProxyTestDataSourceAssert(ds).hasOtherCount(10);
            fail("assertion should failed");
        } catch (AssertionError ex) {
            assertThat(ex.getMessage()).isEqualTo("Expected OTHER count: <10> but was <3>: [OTHER 1, OTHER 2, OTHER 333333...]");
        }
    }

}
