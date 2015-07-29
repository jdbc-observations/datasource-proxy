package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.StatementExecution;
import org.junit.Test;

import static net.ttddyy.dsproxy.test.hamcrest.QueryHolderAssertions.query;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class StatementExecutionMatcherTest {

    @Test
    public void testQuery() {
        StatementExecution se = new StatementExecution();
        se.setQuery("foo");

        assertThat(se, query(is("foo")));
        assertThat(se, query(startsWith("fo")));
    }
}
