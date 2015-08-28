package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.QueryExecution;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Hamcrest matchers for {@link QueryExecution}.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class QueryExecutionAssertions {

    /**
     * Matcher to check {@link QueryExecution} was successful.
     *
     * Example:
     * <pre> assertThat(qe, success()); </pre>
     */
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

    /**
     * Matcher to check {@link QueryExecution} was failure.
     *
     * Example:
     * <pre> assertThat(qe, failure()); </pre>
     */
    public static Matcher<QueryExecution> failure() {
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

    /**
     * Matcher to check {@link QueryExecution} was a batch execution.
     *
     * Example:
     * <pre> assertThat(qe, batch()); </pre>
     */
    public static Matcher<? super QueryExecution> batch() {
        return new ExecutionTypeMatcher(ExecutionType.IS_BATCH);
    }


    /**
     * Matcher to check {@link QueryExecution} was an execution of {@link java.sql.Statement}.
     *
     * Example:
     * <pre> assertThat(qe, statement()); </pre>
     */
    public static Matcher<? super QueryExecution> statement() {
        return new ExecutionTypeMatcher(ExecutionType.IS_STATEMENT);
    }

    /**
     * Matcher to check {@link QueryExecution} was a batch execution of {@link java.sql.Statement}.
     *
     * Example:
     * <pre> assertThat(qe, batchStatement()); </pre>
     */
    public static Matcher<? super QueryExecution> batchStatement() {
        return new ExecutionTypeMatcher(ExecutionType.IS_BATCH_STATEMENT);
    }

    /**
     * Matcher to check {@link QueryExecution} was a normal or batch execution of {@link java.sql.Statement}.
     *
     * Example:
     * <pre> assertThat(qe, statementOrBatchStatement()); </pre>
     */
    public static Matcher<? super QueryExecution> statementOrBatchStatement() {
        return new ExecutionTypeMatcher(ExecutionType.IS_STATEMENT_OR_BATCH_STATEMENT);
    }

    /**
     * Matcher to check {@link QueryExecution} was an execution of {@link java.sql.PreparedStatement}.
     *
     * Example:
     * <pre> assertThat(qe, prepared()); </pre>
     */
    public static Matcher<? super QueryExecution> prepared() {
        return new ExecutionTypeMatcher(ExecutionType.IS_PREPARED);
    }

    /**
     * Matcher to check {@link QueryExecution} was a batch execution of {@link java.sql.PreparedStatement}.
     *
     * Example:
     * <pre> assertThat(qe, batchPrepared()); </pre>
     */
    public static Matcher<? super QueryExecution> batchPrepared() {
        return new ExecutionTypeMatcher(ExecutionType.IS_BATCH_PREPARED);
    }

    /**
     * Matcher to check {@link QueryExecution} was a normal or batch execution of {@link java.sql.PreparedStatement}.
     *
     * Example:
     * <pre> assertThat(qe, preparedOrBatchPrepared()); </pre>
     */
    public static Matcher<? super QueryExecution> preparedOrBatchPrepared() {
        return new ExecutionTypeMatcher(ExecutionType.IS_PREPARED_OR_BATCH_PREPARED);
    }

    /**
     * Matcher to check {@link QueryExecution} was an execution of {@link java.sql.CallableStatement}.
     *
     * Example:
     * <pre> assertThat(qe, callable()); </pre>
     */
    public static Matcher<? super QueryExecution> callable() {
        return new ExecutionTypeMatcher(ExecutionType.IS_CALLABLE);
    }

    /**
     * Matcher to check {@link QueryExecution} was a batch execution of {@link java.sql.CallableStatement}.
     *
     * Example:
     * <pre> assertThat(qe, batchCallable()); </pre>
     */
    public static Matcher<? super QueryExecution> batchCallable() {
        return new ExecutionTypeMatcher(ExecutionType.IS_BATCH_CALLABLE);
    }

    /**
     * Matcher to check {@link QueryExecution} was a normal or batch execution of {@link java.sql.CallableStatement}.
     *
     * Example:
     * <pre> assertThat(qe, callableOrBatchCallable()); </pre>
     */
    public static Matcher<? super QueryExecution> callableOrBatchCallable() {
        return new ExecutionTypeMatcher(ExecutionType.IS_CALLABLE_OR_BATCH_CALLABLE);
    }

}
