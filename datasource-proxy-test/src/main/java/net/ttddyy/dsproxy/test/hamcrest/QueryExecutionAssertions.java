package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.QueryExecution;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class QueryExecutionAssertions {

    public static Matcher<QueryExecution> success() {
        return new TypeSafeMatcher<QueryExecution>() {
            @Override
            protected boolean matchesSafely(QueryExecution item) {
                return item.isSuccess();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("success");
            }

            @Override
            protected void describeMismatchSafely(QueryExecution item, Description mismatchDescription) {
                mismatchDescription.appendText("was failure");
            }
        };
    }

    public static Matcher<QueryExecution> fail() {
        return new TypeSafeMatcher<QueryExecution>() {
            @Override
            protected boolean matchesSafely(QueryExecution item) {
                return !item.isSuccess();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("failure");
            }

            @Override
            protected void describeMismatchSafely(QueryExecution item, Description mismatchDescription) {
                mismatchDescription.appendText("was success");
            }
        };
    }
}
