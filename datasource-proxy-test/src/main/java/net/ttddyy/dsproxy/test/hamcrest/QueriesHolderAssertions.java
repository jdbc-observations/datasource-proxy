package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.QueriesHolder;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import java.util.Collection;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class QueriesHolderAssertions {

    public static Matcher<? super QueriesHolder> queries(final int index, Matcher<String> stringMatcher) {
        String msg = "queries[" + index + "]";
        return new FeatureMatcher<QueriesHolder, String>(stringMatcher, msg, msg) {
            @Override
            protected String featureValueOf(QueriesHolder actual) {
                return actual.getQueries().get(index);  // TODO: list size check
            }
        };
    }

    public static Matcher<? super QueriesHolder> queries(Matcher<? super Collection<String>> collectionMatcher) {
        return new FeatureMatcher<QueriesHolder, Collection<String>>(collectionMatcher, "queries", "queries") {
            @Override
            protected Collection<String> featureValueOf(QueriesHolder actual) {
                return actual.getQueries();
            }
        };
    }


}
