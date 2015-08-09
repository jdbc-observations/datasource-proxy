package net.ttddyy.dsproxy.test;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static net.ttddyy.dsproxy.test.hamcrest.ExecutionTypeMatcher.statement;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.executions;
import static net.ttddyy.dsproxy.test.hamcrest.QueryHolderAssertions.query;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class ProxyTestDataSourceTest {

    @Rule
    public DatabaseTestRule databaseTestRule = new DatabaseTestRule();

    @Test
    public void testStatementExecution() throws Exception {
        ProxyTestDataSource ds = new ProxyTestDataSource(this.databaseTestRule.dataSource);

        Connection conn = ds.getConnection();
        Statement stmt = conn.createStatement();
        stmt.executeQuery("SELECT id FROM emp");

        List<QueryExecution> queryExecutions = ds.getQueryExecutions();

        assertThat(queryExecutions).hasSize(1);
        assertThat(queryExecutions.get(0)).isInstanceOf(StatementExecution.class);
        StatementExecution se = (StatementExecution) queryExecutions.get(0);
        assertThat(se.getQuery()).isEqualTo("SELECT id FROM emp");

    }

    @Test
    public void testStatementExecutionMultiple() throws Exception {
        ProxyTestDataSource ds = new ProxyTestDataSource(this.databaseTestRule.dataSource);

        Connection conn = ds.getConnection();
        Statement stmt = conn.createStatement();
        stmt.execute("SELECT id FROM emp");
        stmt.execute("SELECT name FROM emp");
        stmt.execute("SELECT id FROM emp WHERE id = 1");

        List<QueryExecution> queryExecutions = ds.getQueryExecutions();

        assertThat(queryExecutions).hasSize(3);
        assertThat(queryExecutions.get(0)).isInstanceOf(StatementExecution.class);
        assertThat(queryExecutions.get(1)).isInstanceOf(StatementExecution.class);
        assertThat(queryExecutions.get(2)).isInstanceOf(StatementExecution.class);
        StatementExecution se;
        se = (StatementExecution) queryExecutions.get(0);
        assertThat(se.getQuery()).isEqualTo("SELECT id FROM emp");
        se = (StatementExecution) queryExecutions.get(1);
        assertThat(se.getQuery()).isEqualTo("SELECT name FROM emp");
        se = (StatementExecution) queryExecutions.get(2);
        assertThat(se.getQuery()).isEqualTo("SELECT id FROM emp WHERE id = 1");

        // junit assertions
        Assert.assertThat(ds, executions(0, is(statement())));
        Assert.assertThat(ds, executions(1, is(statement())));
        Assert.assertThat(ds, executions(2, is(statement())));
        Assert.assertThat((StatementExecution) queryExecutions.get(0), query(is("SELECT id FROM emp")));
        Assert.assertThat((StatementExecution) queryExecutions.get(1), query(is("SELECT name FROM emp")));
        Assert.assertThat((StatementExecution) queryExecutions.get(2), query(is("SELECT id FROM emp WHERE id = 1")));

    }
}
