package net.ttddyy.dsproxy.test.hamcrest;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.hamcrest.internal.ReflectiveTypeFinder;

/**
 * Since {@link org.hamcrest.FeatureMatcher} doesn't support flexible error messaging, this class provides callbacks
 * for error message.
 *
 * @param <T> type for this matcher
 * @param <S> type for sub-matcher
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public abstract class CompositeMatcher<T, S> extends BaseMatcher<T> {
    private static final ReflectiveTypeFinder TYPE_FINDER = new ReflectiveTypeFinder("getValue", 1, 0);

    protected Matcher<? super S> subMatcher;
    private Class<?> expectedType;
    protected Description expected = new StringDescription();
    protected Description actual = new StringDescription();

    protected CompositeMatcher(Matcher<? super S> subMatcher) {
        this.subMatcher = subMatcher;
        this.expectedType = TYPE_FINDER.findExpectedType(getClass());
    }


    @Override
    @SuppressWarnings({"unchecked"})
    public boolean matches(Object item) {

        if (!expectedType.isInstance(item)) {
            this.expected.appendText(this.expectedType.getName());
            this.actual.appendText("was a ")
                    .appendText(item.getClass().getName())
                    .appendText(" (")
                    .appendValue(item)
                    .appendText(")");
            return false;
        }

        T safelyConverted = (T) item;
        if (!validateByThisMatcher(safelyConverted, this.expected, this.actual)) {
            return false;
        }

        S targetValue = getValue(safelyConverted);
        if (!this.subMatcher.matches(targetValue)) {
            this.describeExpectedBySubMatcher(this.subMatcher, targetValue, this.expected, this.actual);
            return false;
        }
        return true;
    }

    public abstract S getValue(T actual);

    protected boolean validateByThisMatcher(T item, Description expected, Description actual) {
        return true;
    }

    protected void describeExpectedBySubMatcher(Matcher<? super S> subMatcher, S targetValue, Description expected, Description actual) {
        String prefix = getSubMatcherFailureDescriptionPrefix();
        if (prefix != null) {
            expected.appendText(prefix);
            actual.appendText(prefix);
        }

        subMatcher.describeTo(expected);
        subMatcher.describeMismatch(targetValue, actual);
    }

    public abstract String getSubMatcherFailureDescriptionPrefix();


    @Override
    public void describeTo(Description description) {
        // for expected message
        description.appendText(this.expected.toString());
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        // for actual(but was) message
        description.appendText(this.actual.toString());
    }
}
