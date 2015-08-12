package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.QueryType;
import net.ttddyy.dsproxy.listener.QueryUtils;
import net.ttddyy.dsproxy.test.QueryHolder;
import org.hamcrest.Description;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

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

    public static Matcher<? super QueryHolder> queryType(final QueryType expectedType) {

        return new TypeSafeMatcher<QueryHolder>() {
            @Override
            protected boolean matchesSafely(QueryHolder item) {
                String query = item.getQuery();
                QueryType actualType = QueryUtils.getQueryType(query);
                if (expectedType != actualType) {
                    return false;
                }
                return true;
            }

            @Override
            public void describeTo(Description description) {
                // expected clause
                description.appendText("query type is \"" + expectedType + "\"");
            }

            @Override
            protected void describeMismatchSafely(QueryHolder item, Description mismatchDescription) {
                // but was clause
                String query = item.getQuery();
                QueryType actualType = QueryUtils.getQueryType(query);

                String msg = "query type was \"" + actualType + "\" (" + query + ")";
                mismatchDescription.appendText(msg);
            }
        };
    }

}
