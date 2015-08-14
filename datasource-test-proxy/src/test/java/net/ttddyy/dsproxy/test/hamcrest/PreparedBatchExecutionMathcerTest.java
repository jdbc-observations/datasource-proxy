package net.ttddyy.dsproxy.test.hamcrest;

import com.google.common.collect.ImmutableMap;
import net.ttddyy.dsproxy.test.PreparedBatchExecution;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.util.Map;

import static net.ttddyy.dsproxy.test.hamcrest.BatchParameterHolderAssertions.batch;
import static net.ttddyy.dsproxy.test.hamcrest.BatchParameterHolderAssertions.batchSize;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.param;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramAsInteger;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramAsString;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramIndexes;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramsByIndex;
import static net.ttddyy.dsproxy.test.hamcrest.QueryHolderAssertions.query;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class PreparedBatchExecutionMathcerTest {

    @Test
    public void testQuery() {
        PreparedBatchExecution pbe = new PreparedBatchExecution();
        pbe.setQuery("foo");

        assertThat(pbe, query(is("foo")));
        assertThat(pbe, query(startsWith("fo")));
        assertThat(pbe.getQuery(), is("foo"));
    }

    @Test
    public void testBatchSize() {
        Map<Integer, Object> params1 = ImmutableMap.<Integer, Object>of(10, "FOO", 11, "BAR");
        Map<Integer, Object> params2 = ImmutableMap.<Integer, Object>of(20, "FOO", 21, "BAR");

        PreparedBatchExecution.PreparedBatchExecutionEntry entry1 = new PreparedBatchExecution.PreparedBatchExecutionEntry();
        entry1.setParamsByIndex(params1);

        PreparedBatchExecution.PreparedBatchExecutionEntry entry2 = new PreparedBatchExecution.PreparedBatchExecutionEntry();
        entry2.setParamsByIndex(params2);

        PreparedBatchExecution pbe = new PreparedBatchExecution();
        pbe.getBatchExecutionEntries().add(entry1);
        pbe.getBatchExecutionEntries().add(entry2);

        assertThat(pbe, batchSize(2));
    }

    @Test
    public void testBatch() {

        Map<Integer, Object> params = ImmutableMap.<Integer, Object>of(1, "FOO", 2, "BAR", 10, 100);

        PreparedBatchExecution.PreparedBatchExecutionEntry entry = new PreparedBatchExecution.PreparedBatchExecutionEntry();
        entry.setParamsByIndex(params);

        PreparedBatchExecution pbe = new PreparedBatchExecution();
        pbe.getBatchExecutionEntries().add(entry);

        assertThat(pbe, batch(0, paramsByIndex(hasEntry(2, (Object) "BAR"))));
        assertThat(pbe, batch(0, paramsByIndex(hasEntry(10, (Object) 100))));
        assertThat(pbe, batch(0, paramIndexes(hasItem(1))));
        assertThat(pbe, batch(0, paramIndexes(hasItems(1, 2))));

        assertThat(pbe, batch(0, param(1, is((Object) "FOO"))));
        assertThat(pbe, batch(0, param(1, (Matcher) startsWith("FOO"))));
        assertThat(pbe, batch(0, param(10, is((Object) 100))));

        assertThat(pbe, batch(0, param(1, String.class, is("FOO"))));
        assertThat(pbe, batch(0, param(1, String.class, startsWith("FOO"))));
        assertThat(pbe, batch(0, param(10, Integer.class, is(100))));

    }

    @Test
    public void testBatchByIndex() {

        Map<Integer, Object> params = ImmutableMap.<Integer, Object>of(1, "FOO", 2, "BAR", 3, 100);

        PreparedBatchExecution.PreparedBatchExecutionEntry entry = new PreparedBatchExecution.PreparedBatchExecutionEntry();
        entry.setParamsByIndex(params);

        PreparedBatchExecution pbe = new PreparedBatchExecution();
        pbe.getBatchExecutionEntries().add(entry);

        assertThat(pbe, batch(0, paramsByIndex(hasEntry(2, (Object) "BAR"))));
        assertThat(pbe, batch(0, paramsByIndex(hasEntry(3, (Object) 100))));
        assertThat(pbe, batch(0, paramIndexes(hasItem(1))));
        assertThat(pbe, batch(0, paramIndexes(hasItems(1, 2))));
        assertThat(pbe, batch(0, paramIndexes(1)));
        assertThat(pbe, batch(0, paramIndexes(1, 2)));

        assertThat(pbe, batch(0, param(1, is((Object) "FOO"))));
        assertThat(pbe, batch(0, param(1, (Matcher) startsWith("FOO"))));
        assertThat(pbe, batch(0, param(3, is((Object) 100))));

        assertThat(pbe, batch(0, param(1, String.class, is("FOO"))));
        assertThat(pbe, batch(0, param(1, String.class, startsWith("FOO"))));
        assertThat(pbe, batch(0, param(3, Integer.class, is(100))));

        assertThat(pbe, batch(0, paramAsString(1, is("FOO"))));
        assertThat(pbe, batch(0, paramAsInteger(3, is(100))));

    }
}
