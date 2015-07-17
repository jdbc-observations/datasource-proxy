package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.StatementBatchExecution;
import org.junit.Test;

import static net.ttddyy.dsproxy.test.hamcrest.StatementBatchExecutionMatcher.queries;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class StatementBatchExecutionMatcherTest {


    @Test
    public void queriesWithIndex() {
        StatementBatchExecution sbe = new StatementBatchExecution();
        sbe.getQueries().add("foo");
        sbe.getQueries().add("bar");
        sbe.getQueries().add("baz");

        assertThat(sbe, queries(0, is("foo")));
        assertThat(sbe, queries(1, is("bar")));
        assertThat(sbe, queries(2, is("baz")));

        assertThat(sbe, queries(0, startsWith("f")));
    }

    // TODO: test failure messages
    // TODO: test outside of index range

    @Test
    public void queriesWithCollectionMatcher(){
        StatementBatchExecution sbe = new StatementBatchExecution();
        sbe.getQueries().add("foo");
        sbe.getQueries().add("bar");
        sbe.getQueries().add("baz");

        assertThat(sbe, queries(hasItem("foo")));
        assertThat(sbe, queries(hasItems("foo", "bar")));
        assertThat(sbe, queries(hasItems(startsWith("f"), is("bar"))));

        assertThat(sbe, queries(hasSize(3)));
    }

}
