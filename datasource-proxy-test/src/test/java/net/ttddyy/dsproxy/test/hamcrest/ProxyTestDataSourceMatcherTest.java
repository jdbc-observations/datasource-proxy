package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.ProxyTestDataSource;
import net.ttddyy.dsproxy.test.StatementExecution;
import org.junit.Test;

import static net.ttddyy.dsproxy.test.hamcrest.ExecutionTypeMatcher.statement;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceMatcher.executions;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionMatcher.fail;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionMatcher.success;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class ProxyTestDataSourceMatcherTest {

    @Test
    public void testExecutions() {
        StatementExecution se = new StatementExecution();

        ProxyTestDataSource ds = new ProxyTestDataSource();
        ds.getQueryExecutions().add(se);

        assertThat(ds, executions(0, ExecutionType.IS_STATEMENT));
        assertThat(ds, executions(0, statement()));
        assertThat(ds, executions(0, is(statement())));
    }

    @Test
    public void testExecutionSuccess() {
        StatementExecution se = new StatementExecution();
        se.setSuccess(true);

        ProxyTestDataSource ds = new ProxyTestDataSource();
        ds.getQueryExecutions().add(se);

        assertThat(ds, executions(0, success()));
        assertThat(ds, executions(0, is(success())));
    }

    @Test
    public void testExecutionFail() {
        StatementExecution se = new StatementExecution();
        se.setSuccess(false);

        ProxyTestDataSource ds = new ProxyTestDataSource();
        ds.getQueryExecutions().add(se);

        assertThat(ds, executions(0, fail()));
        assertThat(ds, executions(0, is(fail())));
    }
}
