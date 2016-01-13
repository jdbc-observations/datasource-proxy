package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.PreparedBatchExecution;
import org.hamcrest.Matcher;
import org.junit.Test;

import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.createSetParam;
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
        PreparedBatchExecution.PreparedBatchExecutionEntry entry1 = new PreparedBatchExecution.PreparedBatchExecutionEntry();
        entry1.getParameters().add(createSetParam(10, "FOO"));
        entry1.getParameters().add(createSetParam(11, "BAR"));

        PreparedBatchExecution.PreparedBatchExecutionEntry entry2 = new PreparedBatchExecution.PreparedBatchExecutionEntry();
        entry2.getParameters().add(createSetParam(20, "FOO"));
        entry2.getParameters().add(createSetParam(21, "BAR"));

        PreparedBatchExecution pbe = new PreparedBatchExecution();
        pbe.getBatchExecutionEntries().add(entry1);
        pbe.getBatchExecutionEntries().add(entry2);

        assertThat(pbe, batchSize(2));
    }

    @Test
    public void testBatch() {

        PreparedBatchExecution.PreparedBatchExecutionEntry entry = new PreparedBatchExecution.PreparedBatchExecutionEntry();
        entry.getParameters().add(createSetParam(1, "FOO"));
        entry.getParameters().add(createSetParam(2, "BAR"));
        entry.getParameters().add(createSetParam(10, 100));

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

        PreparedBatchExecution.PreparedBatchExecutionEntry entry = new PreparedBatchExecution.PreparedBatchExecutionEntry();
        entry.getParameters().add(createSetParam(1, "FOO"));
        entry.getParameters().add(createSetParam(2, "BAR"));
        entry.getParameters().add(createSetParam(3, 100));


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
