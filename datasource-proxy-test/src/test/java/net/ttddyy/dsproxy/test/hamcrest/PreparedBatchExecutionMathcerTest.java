package net.ttddyy.dsproxy.test.hamcrest;

import com.google.common.collect.ImmutableMap;
import net.ttddyy.dsproxy.test.PreparedBatchExecution;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.util.Map;

import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramIndexes;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramNames;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramValue;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramValueAsInteger;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramValueAsString;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramsByIndex;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramsByName;
import static net.ttddyy.dsproxy.test.hamcrest.PreparedBatchExecutionMatcher.batch;
import static net.ttddyy.dsproxy.test.hamcrest.PreparedBatchExecutionMatcher.batchSize;
import static net.ttddyy.dsproxy.test.hamcrest.PreparedBatchExecutionMatcher.query;
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
        Map<String, Object> params1 = ImmutableMap.<String, Object>of("foo1", "FOO", "bar1", "BAR");
        Map<String, Object> params2 = ImmutableMap.<String, Object>of("foo2", "FOO", "bar2", "BAR");

        PreparedBatchExecution.PreparedBatchExecutionEntry entry1 = new PreparedBatchExecution.PreparedBatchExecutionEntry();
        entry1.setParamsByName(params1);

        PreparedBatchExecution.PreparedBatchExecutionEntry entry2 = new PreparedBatchExecution.PreparedBatchExecutionEntry();
        entry2.setParamsByName(params2);

        PreparedBatchExecution pbe = new PreparedBatchExecution();
        pbe.getBatchExecutionEntries().add(entry1);
        pbe.getBatchExecutionEntries().add(entry2);

        assertThat(pbe, batchSize(2));
    }

    @Test
    public void testBatch() {

        Map<String, Object> params = ImmutableMap.<String, Object>of("foo", "FOO", "bar", "BAR", "number", 100);

        PreparedBatchExecution.PreparedBatchExecutionEntry entry = new PreparedBatchExecution.PreparedBatchExecutionEntry();
        entry.setParamsByName(params);

        PreparedBatchExecution pbe = new PreparedBatchExecution();
        pbe.getBatchExecutionEntries().add(entry);

//        assertThat(pbe, batch(0, paramsByName(hasEntry("bar", (Object) "BAR"))));
//        assertThat(pbe, batch(0, paramsByName(hasEntry("number", (Object) 100))));
//        assertThat(pbe, batch(0, paramNames(hasItem("foo"))));
//        assertThat(pbe, batch(0, paramNames(hasItems("foo", "bar"))));
//
//        assertThat(pbe, batch(0, paramValue("foo", is((Object) "FOO"))));
//        assertThat(pbe, batch(0, paramValue("foo", (Matcher) startsWith("FOO"))));
//        assertThat(pbe, batch(0, paramValue("number", is((Object) 100))));
//
//        assertThat(pbe, batch(0, paramValue("foo", String.class, is("FOO"))));
//        assertThat(pbe, batch(0, paramValue("foo", String.class, startsWith("FOO"))));
//        assertThat(pbe, batch(0, paramValue("number", Integer.class, is(100))));

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

        assertThat(pbe, batch(0, paramValue(1, is((Object) "FOO"))));
        assertThat(pbe, batch(0, paramValue(1, (Matcher) startsWith("FOO"))));
        assertThat(pbe, batch(0, paramValue(3, is((Object) 100))));

        assertThat(pbe, batch(0, paramValue(1, String.class, is("FOO"))));
        assertThat(pbe, batch(0, paramValue(1, String.class, startsWith("FOO"))));
        assertThat(pbe, batch(0, paramValue(3, Integer.class, is(100))));

        assertThat(pbe, batch(0, paramValueAsString(1, is("FOO"))));
        assertThat(pbe, batch(0, paramValueAsInteger(3, is(100))));

    }
}
