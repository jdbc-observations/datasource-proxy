package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.QueryType;
import net.ttddyy.dsproxy.listener.QueryUtils;
import net.ttddyy.dsproxy.test.QueryHolder;
import org.hamcrest.Description;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Hamcrest matchers for {@link QueryHolder}.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class QueryHolderAssertions {

    /**
     * Matcher to examine the query with given {@link String} matcher.
     *
     * Example:
     * <pre> assertThat(qe, query(startsWith("select"))); </pre>
     */
    public static Matcher<? super QueryHolder> query(final Matcher<String> stringMatcher) {
        return new FeatureMatcher<QueryHolder, String>(stringMatcher, "query", "query") {
            @Override
            protected String featureValueOf(QueryHolder actual) {
                return actual.getQuery();
            }
        };
    }

    /**
     * Matcher to examine the query type.
     *
     * Example:
     * <pre> assertThat(qe, queryType(SELECT)); </pre>
     */
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
                description.appendText("query type \"" + expectedType + "\"");
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

    /**
     * Matcher to examine the query type is SELECT.
     *
     * Example:
     * <pre> assertThat(qe, select()); </pre>
     */
    public static Matcher<? super QueryHolder> select() {
        return queryType(QueryType.SELECT);
    }

    /**
     * Matcher to examine the query type is SELECT.
     *
     * Example:
     * <pre> assertThat(qe, insert()); </pre>
     */
    public static Matcher<? super QueryHolder> insert() {
        return queryType(QueryType.INSERT);
    }

    /**
     * Matcher to examine the query type is SELECT.
     *
     * Example:
     * <pre> assertThat(qe, update()); </pre>
     */
    public static Matcher<? super QueryHolder> update() {
        return queryType(QueryType.UPDATE);
    }

    /**
     * Matcher to examine the query type is SELECT.
     *
     * Example:
     * <pre> assertThat(qe, delete()); </pre>
     */
    public static Matcher<? super QueryHolder> delete() {
        return queryType(QueryType.DELETE);
    }

    /**
     * Matcher to examine the query type is SELECT.
     *
     * Example:
     * <pre> assertThat(qe, other()); </pre>
     */
    public static Matcher<? super QueryHolder> other() {
        return queryType(QueryType.OTHER);
    }

}
