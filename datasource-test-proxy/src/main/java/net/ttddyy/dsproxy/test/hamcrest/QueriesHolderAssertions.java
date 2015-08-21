package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.QueriesHolder;
import net.ttddyy.dsproxy.test.QueryHolder;
import org.hamcrest.Description;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import java.util.Collection;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class QueriesHolderAssertions {

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

    public static Matcher<? super QueriesHolder> queries(Matcher<? super Collection<String>> collectionMatcher) {
        return new FeatureMatcher<QueriesHolder, Collection<String>>(collectionMatcher, "queries[]", "queries[]") {
            @Override
            protected Collection<String> featureValueOf(QueriesHolder actual) {
                return actual.getQueries();
            }
        };
    }

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
