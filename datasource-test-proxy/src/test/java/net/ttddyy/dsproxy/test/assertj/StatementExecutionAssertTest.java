package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.QueryType;
import net.ttddyy.dsproxy.test.StatementExecution;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class StatementExecutionAssertTest {

    private StatementExecution se;
    private StatementExecutionAssert seAssert;

    @Before
    public void setUp() {
        this.se = new StatementExecution();
        this.seAssert = new StatementExecutionAssert(this.se);
    }

    @Test
    public void testIsSuccess() {
        StatementExecution ce = new StatementExecution();

        // success case
        this.se.setSuccess(true);
        this.seAssert.isSuccess();

        // failure case
        this.se.setSuccess(false);
        try {
            this.seAssert.isSuccess();
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: <Successful execution> but was: <Failure execution>\n");
        }

    }

    @Test
    public void testIsFailure() {
        // success case
        this.se.setSuccess(false);
        this.seAssert.isFailure();

        // failure case
        this.se.setSuccess(true);
        try {
            this.seAssert.isFailure();
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpecting: <Failure execution> but was: <Successful execution>\n");
        }
    }

    @Test
    public void testHasQueryType() {
        StatementExecution seSelect = new StatementExecution();
        StatementExecution seInsert = new StatementExecution();
        StatementExecution seUpdate = new StatementExecution();
        StatementExecution seDelete = new StatementExecution();
        StatementExecution seOther = new StatementExecution();
        seSelect.setQuery("SELECT");
        seInsert.setQuery("INSERT");
        seUpdate.setQuery("UPDATE");
        seDelete.setQuery("DELETE");
        seOther.setQuery("OTHER");

        DataSourceProxyAssertions.assertThat(seSelect).hasQueryType(QueryType.SELECT);
        DataSourceProxyAssertions.assertThat(seInsert).hasQueryType(QueryType.INSERT);
        DataSourceProxyAssertions.assertThat(seUpdate).hasQueryType(QueryType.UPDATE);
        DataSourceProxyAssertions.assertThat(seDelete).hasQueryType(QueryType.DELETE);
        DataSourceProxyAssertions.assertThat(seOther).hasQueryType(QueryType.OTHER);
        DataSourceProxyAssertions.assertThat(seSelect).isSelect();
        DataSourceProxyAssertions.assertThat(seInsert).isInsert();
        DataSourceProxyAssertions.assertThat(seUpdate).isUpdate();
        DataSourceProxyAssertions.assertThat(seDelete).isDelete();
        DataSourceProxyAssertions.assertThat(seOther).isOther();

        // wrong type
        try {
            DataSourceProxyAssertions.assertThat(seSelect).isDelete();
            fail("exception should be thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected query type:<DELETE> but was:<SELECT>\n");
        }
    }

}
