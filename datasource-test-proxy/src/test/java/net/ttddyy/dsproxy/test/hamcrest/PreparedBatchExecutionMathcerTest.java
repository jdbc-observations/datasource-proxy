package net.ttddyy.dsproxy.test.hamcrest;

import com.google.common.collect.ImmutableMap;
import net.ttddyy.dsproxy.proxy.ParameterKey;
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
        Map<ParameterKey, Object> params1 = ImmutableMap.<ParameterKey, Object>of(new ParameterKey(10), "FOO", new ParameterKey(11), "BAR");
        Map<ParameterKey, Object> params2 = ImmutableMap.<ParameterKey, Object>of(new ParameterKey(20), "FOO", new ParameterKey(21), "BAR");

        PreparedBatchExecution.PreparedBatchExecutionEntry entry1 = new PreparedBatchExecution.PreparedBatchExecutionEntry();
        entry1.getParams().putAll(params1);

        PreparedBatchExecution.PreparedBatchExecutionEntry entry2 = new PreparedBatchExecution.PreparedBatchExecutionEntry();
        entry2.getParams().putAll(params2);

        PreparedBatchExecution pbe = new PreparedBatchExecution();
        pbe.getBatchExecutionEntries().add(entry1);
        pbe.getBatchExecutionEntries().add(entry2);

        assertThat(pbe, batchSize(2));
    }

    @Test
    public void testBatch() {

        Map<ParameterKey, Object> params = ImmutableMap.<ParameterKey, Object>of(new ParameterKey(1), "FOO", new ParameterKey(2), "BAR", new ParameterKey(10), 100);

        PreparedBatchExecution.PreparedBatchExecutionEntry entry = new PreparedBatchExecution.PreparedBatchExecutionEntry();
        entry.getParams().putAll(params);

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

        Map<ParameterKey, Object> params = ImmutableMap.<ParameterKey, Object>of(new ParameterKey(1), "FOO", new ParameterKey(2), "BAR", new ParameterKey(3), 100);

        PreparedBatchExecution.PreparedBatchExecutionEntry entry = new PreparedBatchExecution.PreparedBatchExecutionEntry();
        entry.getParams().putAll(params);

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
