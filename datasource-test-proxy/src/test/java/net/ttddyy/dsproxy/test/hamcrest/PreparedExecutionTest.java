package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.PreparedExecution;
import org.hamcrest.Matcher;
import org.junit.Test;

import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.createSetParam;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.param;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramIndexes;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramsByIndex;
import static net.ttddyy.dsproxy.test.hamcrest.QueryHolderAssertions.query;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class PreparedExecutionTest {

    @Test
    public void testQuery() {
        PreparedExecution pe = new PreparedExecution();
        pe.setQuery("foo");

        assertThat(pe, query(is("foo")));
        assertThat(pe, query(startsWith("fo")));
        assertThat(pe.getQuery(), is("foo"));
    }

    @Test
    public void testParams() {
        PreparedExecution pe = new PreparedExecution();
        pe.getParameters().add(createSetParam(1, "foo"));
        pe.getParameters().add(createSetParam(2, "bar"));
        pe.getParameters().add(createSetParam(3, "baz"));
        pe.getParameters().add(createSetParam(4, 100));
        assertThat(pe, paramsByIndex(hasEntry(2, (Object) "bar")));
        assertThat(pe, paramsByIndex(hasEntry(4, (Object) 100)));
    }

    @Test
    public void testParamIndexes() {
        PreparedExecution pe = new PreparedExecution();
        pe.getParameters().add(createSetParam(1, "foo"));
        pe.getParameters().add(createSetParam(2, "bar"));
        pe.getParameters().add(createSetParam(3, "baz"));
        assertThat(pe, paramIndexes(hasItem(1)));
        assertThat(pe, paramIndexes(hasSize(3)));
    }

    @Test
    public void testParamValue() {
        PreparedExecution pe = new PreparedExecution();
        pe.getParameters().add(createSetParam(1, "foo"));
        pe.getParameters().add(createSetParam(100, 100));

        assertThat(pe, param(1, is((Object) "foo")));

        assertThat(pe, param(1, (Matcher) startsWith("foo")));
        assertThat(pe, param(100, is((Object) 100)));
    }

    @Test
    public void testParamValueWithClass() {
        PreparedExecution pe = new PreparedExecution();
        pe.getParameters().add(createSetParam(1, "foo"));
        pe.getParameters().add(createSetParam(100, 100));

        assertThat(pe, param(1, String.class, is("foo")));
        assertThat(pe, param(1, String.class, startsWith("foo")));
        assertThat(pe, param(100, Integer.class, is(100)));
    }
}
