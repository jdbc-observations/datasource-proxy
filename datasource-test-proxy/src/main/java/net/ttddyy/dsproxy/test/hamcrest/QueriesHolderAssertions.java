package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.QueriesHolder;
import net.ttddyy.dsproxy.test.QueryHolder;
import org.hamcrest.Description;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import java.util.Collection;

/**
 * Hamcrest matchers for {@link QueriesHolder}.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class QueriesHolderAssertions {

    /**
     * Matcher to check the specified index query as {@link String}.
     *
     * Example:
     * <pre> assertThat(ds.getFirstBatchStatement(), queries(0, startsWith("INSERT"))); </pre>
     */
    public static Matcher<? super QueriesHolder> queries(final int index, Matcher<String> stringMatcher) {
        return new CompositeMatcher<QueriesHolder, String>(stringMatcher) {
            @Override
            protected boolean validateByThisMatcher(QueriesHolder item, Description expected, Description actual) {
                int size = item.getQueries().size();
                if (size - 1 < index) {
                    expected.appendText("queries[" + index + "] exists");
                    actual.appendText("queries[] size was " + size);
                    return false;
                }
                return true;
            }

            @Override
            public String getValue(QueriesHolder actual) {
                return actual.getQueries().get(index);
            }

            @Override
            public String getSubMatcherFailureDescriptionPrefix() {
                return "queries[" + index + "] ";
            }

        };
    }

    /**
     * Matcher to check the given queries with string collection matcher.
     *
     * Example:
     * <pre> assertThat(ds.getFirstBatchStatement(), queries(hasItems("foo", "bar"))); </pre>
     */
    public static Matcher<? super QueriesHolder> queries(Matcher<? super Collection<String>> collectionMatcher) {
        return new FeatureMatcher<QueriesHolder, Collection<String>>(collectionMatcher, "queries[]", "queries[]") {
            @Override
            protected Collection<String> featureValueOf(QueriesHolder actual) {
                return actual.getQueries();
            }
        };
    }

    /**
     * Matcher to check the specified index query as {@link String}.
     *
     * Example:
     * <pre> assertThat(ds.getFirstBatchStatement(), queryTypes(0, select())); </pre>
     */
    public static Matcher<? super QueriesHolder> queryTypes(final int index, Matcher<? super QueryHolder> queryHolderMatcher) {
        return new CompositeMatcher<QueriesHolder, QueryHolder>(queryHolderMatcher) {
            @Override
            protected boolean validateByThisMatcher(QueriesHolder item, Description expected, Description actual) {
                int size = item.getQueries().size();
                if (size - 1 < index) {
                    expected.appendText("queries[" + index + "] exists");
                    actual.appendText("queries[] size was " + size);
                    return false;
                }
                return true;
            }

            @Override
            public QueryHolder getValue(QueriesHolder actual) {
                // convert query to a placeholder QueryHolder instance
                final String query = actual.getQueries().get(index);
                QueryHolder queryHolder = new QueryHolder() {
                    @Override
                    public String getQuery() {
                        return query;
                    }
                };
                return queryHolder;
            }

            @Override
            public String getSubMatcherFailureDescriptionPrefix() {
                return "queries[" + index + "] ";
            }

        };
    }


}
