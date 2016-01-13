package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.CallableExecution;
import org.hamcrest.Matcher;
import org.junit.Test;

import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.createSetParam;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramIndexes;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramNames;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramsByIndex;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramsByName;
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
public class CallableExecutionTest {

    @Test
    public void testQuery() {
        CallableExecution ce = new CallableExecution();
        ce.setQuery("foo");

        assertThat(ce, query(is("foo")));
        assertThat(ce, query(startsWith("fo")));
        assertThat(ce.getQuery(), is("foo"));
    }

    @Test
    public void testParamsByName() {
        CallableExecution ce = new CallableExecution();
        ce.getParameters().add(createSetParam("foo", "FOO"));
        ce.getParameters().add(createSetParam("bar", "BAR"));
        ce.getParameters().add(createSetParam("baz", "BAZ"));
        ce.getParameters().add(createSetParam("number", 100));
        assertThat(ce, paramsByName(hasEntry("bar", (Object) "BAR")));
        assertThat(ce, paramsByName(hasEntry("number", (Object) 100)));
    }

    @Test
    public void testParamsByIndex() {
        CallableExecution ce = new CallableExecution();
        ce.getParameters().add(createSetParam(1, "FOO"));
        ce.getParameters().add(createSetParam(2, "BAR"));
        ce.getParameters().add(createSetParam(3, "BAZ"));
        ce.getParameters().add(createSetParam(10, 100));
        assertThat(ce, paramsByIndex(hasEntry(2, (Object) "BAR")));
        assertThat(ce, paramsByIndex(hasEntry(10, (Object) 100)));
    }


    @Test
    public void testParamNames() {
        CallableExecution ce = new CallableExecution();
        ce.getParameters().add(createSetParam("foo", "FOO"));
        ce.getParameters().add(createSetParam("bar", "BAR"));
        ce.getParameters().add(createSetParam("baz", "BAZ"));
        assertThat(ce, paramNames(hasItem("foo")));
        assertThat(ce, paramNames(hasSize(3)));
    }

    @Test
    public void testParamIndexes() {
        CallableExecution ce = new CallableExecution();
        ce.getParameters().add(createSetParam(1, "FOO"));
        ce.getParameters().add(createSetParam(2, "BAR"));
        ce.getParameters().add(createSetParam(3, "BAZ"));
        assertThat(ce, paramIndexes(hasItem(1)));
        assertThat(ce, paramIndexes(hasSize(3)));
    }

    @Test
    public void testParamValueByName() {
        CallableExecution ce = new CallableExecution();
        ce.getParameters().add(createSetParam("foo", "FOO"));
        ce.getParameters().add(createSetParam("number", 100));

        assertThat(ce, ParameterHolderAssertions.param("foo", is((Object) "FOO")));

        assertThat(ce, ParameterHolderAssertions.param("foo", (Matcher) startsWith("FOO")));
        assertThat(ce, ParameterHolderAssertions.param("number", is((Object) 100)));
    }

    @Test
    public void testParamValueByIndex() {
        CallableExecution ce = new CallableExecution();
        ce.getParameters().add(createSetParam(1, "FOO"));
        ce.getParameters().add(createSetParam(2, 100));

        assertThat(ce, ParameterHolderAssertions.param(1, is((Object) "FOO")));

        assertThat(ce, ParameterHolderAssertions.param(1, (Matcher) startsWith("FOO")));
        assertThat(ce, ParameterHolderAssertions.param(2, is((Object) 100)));
    }
//
//    @Test
//    public void testParamValueWithClass() {
//        PreparedExecution pe = new PreparedExecution();
//        pe.getParams().put("foo", "FOO");
//        pe.getParams().put("number", 100);
//
//        assertThat(pe, paramValue("foo", String.class, is("FOO")));
//        assertThat(pe, paramValue("foo", String.class, startsWith("FOO")));
//        assertThat(pe, paramValue("number", Integer.class, is(100)));
//    }

}
