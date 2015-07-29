package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.QueryHolder;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class QueryHolderAssertions {

    public static Matcher<? super QueryHolder> query(final Matcher<String> stringMatcher) {
        return new FeatureMatcher<QueryHolder, String>(stringMatcher, "query", "query") {
            @Override
            protected String featureValueOf(QueryHolder actual) {
                return actual.getQuery();
            }
        };
    }

}
