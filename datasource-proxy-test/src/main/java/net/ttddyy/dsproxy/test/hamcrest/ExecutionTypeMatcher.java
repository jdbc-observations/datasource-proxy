package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.QueryExecution;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Arrays;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class ExecutionTypeMatcher extends TypeSafeMatcher<QueryExecution> {

    public static Matcher<QueryExecution> batch() {
        return new ExecutionTypeMatcher(ExecutionType.IS_BATCH);
    }

    public static Matcher<QueryExecution> statement() {
        return new ExecutionTypeMatcher(ExecutionType.IS_STATEMENT);
    }

    public static Matcher<QueryExecution> batchStatement() {
        return new ExecutionTypeMatcher(ExecutionType.IS_BATCH_STATEMENT);
    }

    public static Matcher<QueryExecution> statementOrBatchStatement() {
        return new ExecutionTypeMatcher(ExecutionType.IS_STATEMENT_OR_BATCH_STATEMENT);
    }

    private ExecutionType expectedType;

    public ExecutionTypeMatcher(ExecutionType expected) {
        this.expectedType = expected;
    }

    @Override
    protected boolean matchesSafely(QueryExecution item) {
        return Arrays.asList(expectedType.getExecutionTypes()).contains(item.getClass());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(this.expectedType.getDescription());
        // TODO: impl
    }

    @Override
    protected void describeMismatchSafely(QueryExecution item, Description mismatchDescription) {
        mismatchDescription.appendText("was ");

        // TODO: impl
        ExecutionType executionType = ExecutionType.valueOf(item);
        if (executionType == null) {
            mismatchDescription.appendText(null);
        } else {
            mismatchDescription.appendText(expectedType.getDescription());
        }
    }
}
