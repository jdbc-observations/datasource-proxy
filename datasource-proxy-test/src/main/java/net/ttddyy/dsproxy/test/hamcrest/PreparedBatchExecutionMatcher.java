package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.BatchExecutionEntry;
import net.ttddyy.dsproxy.test.PreparedBatchExecution;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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

    public static Matcher<PreparedBatchExecution> query(final Matcher<String> stringMatcher) {
        return new FeatureMatcher<PreparedBatchExecution, String>(stringMatcher, "query", "query") {
            @Override
            protected String featureValueOf(PreparedBatchExecution actual) {
                return actual.getQuery();
            }
        };
    }

    public static Matcher<PreparedBatchExecution> batchSize(final int batchSize) {
        return new FeatureMatcher<PreparedBatchExecution, Integer>(equalTo(batchSize), "batchSize", "batchSize") {
            @Override
            protected Integer featureValueOf(PreparedBatchExecution actual) {
                return actual.getBatchExecutionEntries().size();
            }
        };
    }


    public static Matcher<PreparedBatchExecution> batch(final int index, final Matcher<BatchExecutionEntry> preparedExecutionMatcher) {
        return new FeatureMatcher<PreparedBatchExecution, BatchExecutionEntry>(preparedExecutionMatcher, "query", "query") {
            @Override
            protected BatchExecutionEntry featureValueOf(PreparedBatchExecution actual) {
                List<BatchExecutionEntry> entries = actual.getBatchExecutionEntries();
                // TODO: index check
                BatchExecutionEntry entry = entries.get(index);
                return entry;
            }
        };
    }

    // TODO: maybe move to BatchExecutionEntryMatcher
    public static Matcher<BatchExecutionEntry> paramsByName(final Matcher<Map<? extends String, ?>> mapMatcher) {
        return new FeatureMatcher<BatchExecutionEntry, Map<String, Object>>(mapMatcher, "paramsByName", "paramsByName") {
            @Override
            protected Map<String, Object> featureValueOf(BatchExecutionEntry actual) {
                return actual.getParamsByName();
            }
        };
    }

    // batch(0, paramsByIndex(hasEntry(1, "foo")))
    public static Matcher<BatchExecutionEntry> paramsByIndex(final Matcher<Map<? extends Integer, ?>> mapMatcher) {
        return new FeatureMatcher<BatchExecutionEntry, Map<Integer, Object>>(mapMatcher, "paramIndexes", "paramIndexes") {
            @Override
            protected Map<Integer, Object> featureValueOf(BatchExecutionEntry actual) {
                return actual.getParamsByIndex();
            }
        };
    }

    // batch(0, paramIndexes(hasItem(1), hasItem(2)))
    public static Matcher<BatchExecutionEntry> paramIndexes(Matcher<? super Collection<Integer>> collectionMatcher) {
        return new FeatureMatcher<BatchExecutionEntry, Collection<Integer>>(collectionMatcher, "paramIndexes", "paramIndexes") {
            @Override
            protected Collection<Integer> featureValueOf(BatchExecutionEntry actual) {
                return actual.getParamIndexes();
            }
        };
    }

    // batch(0, paramIndexes(1,2,3))
    public static Matcher<BatchExecutionEntry> paramIndexes(Integer... indexes) {
        return paramIndexes(Matchers.hasItems(indexes));
    }

    // batch(0, paramNames(hasItem("foo"), hasItem("bar")))
    public static Matcher<BatchExecutionEntry> paramNames(Matcher<? super Collection<String>> collectionMatcher) {
        return new FeatureMatcher<BatchExecutionEntry, Collection<String>>(collectionMatcher, "paramNames", "paramNames") {
            @Override
            protected Collection<String> featureValueOf(BatchExecutionEntry actual) {
                return actual.getParamNames();
            }
        };
    }

    // batch(0, paramValue("foo", is((Object) "FOO")))
    public static Matcher<BatchExecutionEntry> paramValue(final String name, Matcher<Object> matcher) {
        return new FeatureMatcher<BatchExecutionEntry, Object>(matcher, "paramNames", "paramNames") {
            @Override
            protected Object featureValueOf(BatchExecutionEntry actual) {
                // TODO: null check
                return actual.getParamsByName().get(name);
            }
        };
    }

    // batch(0, paramValue(1, is((Object) "FOO")))
    public static Matcher<BatchExecutionEntry> paramValue(final Integer index, Matcher<Object> matcher) {
        return new FeatureMatcher<BatchExecutionEntry, Object>(matcher, "paramIndexes", "paramIndexes") {
            @Override
            protected Object featureValueOf(BatchExecutionEntry actual) {
                // TODO: null check
                return actual.getParamsByIndex().get(index);
            }
        };
    }

    //batch(0, paramValue("foo", String.class, is("FOO"))));
    public static <T> Matcher<BatchExecutionEntry> paramValue(final String key, final Class<T> clazz, Matcher<? super T> matcher) {
        return new FeatureMatcher<BatchExecutionEntry, T>(matcher, "paramNames", "paramNames") {
            @Override
            protected T featureValueOf(BatchExecutionEntry actual) {
                // TODO: null check
                Object value = actual.getParamsByName().get(key);
                if (clazz.isAssignableFrom(value.getClass())) {
                    // TODO: ok
                    return (T) value;
                }
                // TODO: error
                return null; // temporary
            }
        };
    }

    //batch(0, paramValue(1, String.class, is("FOO"))));
    public static <T> Matcher<BatchExecutionEntry> paramValue(final Integer index, final Class<T> clazz, Matcher<? super T> matcher) {
        return new FeatureMatcher<BatchExecutionEntry, T>(matcher, "paramIndexes", "paramIndexes") {
            @Override
            protected T featureValueOf(BatchExecutionEntry actual) {
                // TODO: null check
                Object value = actual.getParamsByIndex().get(index);
                if (clazz.isAssignableFrom(value.getClass())) {
                    // TODO: ok
                    return (T) value;
                }
                // TODO: error
                return null; // temporary
            }
        };
    }

    public static Matcher<BatchExecutionEntry> paramValueAsString(final Integer index, Matcher<? super String> matcher) {
        return paramValue(index, String.class, matcher);
    }

    public static Matcher<BatchExecutionEntry> paramValueAsInteger(final Integer index, Matcher<? super Integer> matcher) {
        return paramValue(index, Integer.class, matcher);
    }


    // TODO: alias methods
    // preparedBatch(
    // batchParamKeys  or preparedBatchParamKeys
}
