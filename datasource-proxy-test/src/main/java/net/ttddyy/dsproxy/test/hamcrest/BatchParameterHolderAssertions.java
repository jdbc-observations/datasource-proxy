package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.BatchExecutionEntry;
import net.ttddyy.dsproxy.test.BatchParameterHolder;
import net.ttddyy.dsproxy.test.ParameterHolder;
import org.hamcrest.Description;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;


/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class BatchParameterHolderAssertions {

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

    public static Matcher<? super BatchParameterHolder> batchSize(final int batchSize) {
        return new FeatureMatcher<BatchParameterHolder, Integer>(equalTo(batchSize), "batchSize", "batchSize") {
            @Override
            protected Integer featureValueOf(BatchParameterHolder actual) {
                return actual.getBatchExecutionEntries().size();
            }
        };
    }


    public static Matcher<? super BatchParameterHolder> batch(final int index, final Matcher<? super ParameterHolder> parameterHolderMatcher) {
        return new CompositeMatcher<BatchParameterHolder, ParameterHolder>(parameterHolderMatcher) {
            @Override
            protected boolean validateByThisMatcher(BatchParameterHolder item, Description expected, Description actual) {
                List<BatchExecutionEntry> entries = item.getBatchExecutionEntries();
                int size = entries.size();
                if (size - 1 < index) {
                    expected.appendText("batch[" + index + "] exists");
                    actual.appendText("batch[] size was " + size);
                    return false;
                }

                return true;
            }

            @Override
            public ParameterHolder getValue(BatchParameterHolder actual) {
                BatchExecutionEntry entry = actual.getBatchExecutionEntries().get(index);
                return entry;
            }

            @Override
            public String getSubMatcherFailureDescriptionPrefix() {
                return "batch[" + index + "] ";
            }
        };

    }


    // TODO: alias methods
    // preparedBatch(
    // batchParamKeys  or preparedBatchParamKeys
}
