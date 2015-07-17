package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.StatementExecution;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class StatementExecutionMatcher {

    public static Matcher<StatementExecution> query(final Matcher<String> stringMatcher) {

        return new FeatureMatcher<StatementExecution, String>(stringMatcher, "query", "query") {
            @Override
            protected String featureValueOf(StatementExecution actual) {
                return actual.getQuery();
            }
        };
    }
}
