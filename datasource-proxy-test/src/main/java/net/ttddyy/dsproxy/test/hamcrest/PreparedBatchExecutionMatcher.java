package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.BatchExecutionEntry;
import net.ttddyy.dsproxy.test.BatchParameterHolder;
import net.ttddyy.dsproxy.test.ParameterHolder;
import net.ttddyy.dsproxy.test.PreparedBatchExecution;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;


/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class PreparedBatchExecutionMatcher {

    // TODO: rename classes. they are not actually matchers, just providing static methods.

    //  assertThat(pbe, is(success()));
    //  assertThat(pbe, query(startWith("SELECT")));
    //  assertThat(pbe.getQuery(), startWith("SELECT"));
    //  assertThat(pbe, batchSize(3));
    //  assertThat(pbe, firstBatch().args(hasEntry("1", (Object) "foo")));
    //  assertThat(pbe, batch(0, paramsByName(hasEntry("bar", (Object) "BAR")));
    //  assertThat(pbe, batch(0, paramNames(hasItem("foo"))));
    //  assertThat(pbe, batch(0, paramValue("number", is((Object) 100))));
    //  assertThat(pbe, batch(0, paramValue("number", Integer.class, is(100))));
    //  assertThat(pbe, batch(0, paramValueAsString("number", is("foo"))));
    //  assertThat(pbe, batch(0, paramValueAsInteger("number", is(100))));
    //  assertThat(pbe, batch(0, paramValueAsLong("number", is(100L))));

    //  assertThat(pbe, batch(0, argValuesAsString(hasItems("foo")));
    //  assertThat(pbe, batch(0, argValuesAs(String.class, hasItems("foo")));

    public static Matcher<PreparedBatchExecution> batchSize(final int batchSize) {
        return new FeatureMatcher<PreparedBatchExecution, Integer>(equalTo(batchSize), "batchSize", "batchSize") {
            @Override
            protected Integer featureValueOf(PreparedBatchExecution actual) {
                return actual.getBatchExecutionEntries().size();
            }
        };
    }


    public static Matcher<? super BatchParameterHolder> batch(final int index, final Matcher<? super ParameterHolder> preparedExecutionMatcher) {
        // TODO: update text
        return new FeatureMatcher<BatchParameterHolder, ParameterHolder>(preparedExecutionMatcher, "query", "query") {
            @Override
            protected ParameterHolder featureValueOf(BatchParameterHolder actual) {
                List<BatchExecutionEntry> entries = actual.getBatchExecutionEntries();
                // TODO: index check
                BatchExecutionEntry entry = entries.get(index);
                return entry;
            }
        };
    }


    // TODO: alias methods
    // preparedBatch(
    // batchParamKeys  or preparedBatchParamKeys
}
