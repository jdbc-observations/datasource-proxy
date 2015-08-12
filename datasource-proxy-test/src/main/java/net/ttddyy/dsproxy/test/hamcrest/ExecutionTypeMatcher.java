package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.QueryExecution;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.Arrays;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class ExecutionTypeMatcher extends TypeSafeMatcher<QueryExecution> {

    private ExecutionType expectedType;

    public ExecutionTypeMatcher(ExecutionType expected) {
        this.expectedType = expected;
    }

    @Override
    protected boolean matchesSafely(QueryExecution item) {
        for (Class<? extends QueryExecution> executionTypeClass : expectedType.getExecutionTypes()) {
            if (item.getClass().isAssignableFrom(executionTypeClass)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(this.expectedType.getDescription());
    }

    @Override
    protected void describeMismatchSafely(QueryExecution item, Description mismatchDescription) {
        mismatchDescription.appendText("was ");

        ExecutionType executionType = ExecutionType.valueOf(item);
        if (executionType == null) {
            mismatchDescription.appendText(null);
        } else {
            mismatchDescription.appendText(executionType.getDescription());
        }
    }
}
