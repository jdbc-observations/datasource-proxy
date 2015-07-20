package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.PreparedExecution;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import java.util.Collection;
import java.util.Map;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class PreparedExecutionMatcher {

    public static Matcher<PreparedExecution> query(final Matcher<String> stringMatcher) {
        return new FeatureMatcher<PreparedExecution, String>(stringMatcher, "query", "query") {
            @Override
            protected String featureValueOf(PreparedExecution actual) {
                return actual.getQuery();
            }
        };
    }


    public static Matcher<PreparedExecution> params(final Matcher<Map<? extends String, ?>> mapMatcher) {
        return new FeatureMatcher<PreparedExecution, Map<String, Object>>(mapMatcher, "params", "params") {
            @Override
            protected Map<String, Object> featureValueOf(PreparedExecution actual) {
                return actual.getParams();
            }
        };
    }

    public static Matcher<PreparedExecution> paramKeys(Matcher<? super Collection<String>> collectionMatcher) {
        return new FeatureMatcher<PreparedExecution, Collection<String>>(collectionMatcher, "paramKeys", "paramKeys") {
            @Override
            protected Collection<String> featureValueOf(PreparedExecution actual) {
                return actual.getParamKeys();
            }
        };

    }

    public static Matcher<PreparedExecution> paramValue(final String key, Matcher<Object> matcher) {
        return new FeatureMatcher<PreparedExecution, Object>(matcher, "paramKeys", "paramKeys") {
            @Override
            protected Object featureValueOf(PreparedExecution actual) {
                // TODO: null check
                return actual.getParams().get(key);
            }
        };
    }

    public static <T> Matcher<PreparedExecution> paramValue(final String key, final Class<T> clazz, Matcher<? super T> matcher) {
        return new FeatureMatcher<PreparedExecution, T>(matcher, "paramKeys", "paramKeys") {
            @Override
            protected T featureValueOf(PreparedExecution actual) {
                // TODO: null check
                Object value = actual.getParams().get(key);
                if (clazz.isAssignableFrom(value.getClass())) {
                    // TODO: ok
                    return (T) value;
                }
                // TODO: error
                return null; // temporary
            }
        };

    }

    // paramValue(String key, matcher
    // paramValue(String key, Class, matcher
    // paramValues(matcher)
    // paramValuesAs(Class, matcher)


}
