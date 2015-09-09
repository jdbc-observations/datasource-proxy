package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.OutParameterHolder;
import net.ttddyy.dsproxy.test.ParameterByIndexHolder;
import net.ttddyy.dsproxy.test.ParameterByNameHolder;
import net.ttddyy.dsproxy.test.ParameterHolder;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public abstract class ParameterHolderMatcher<S> extends BaseMatcher<ParameterHolder> {

    protected static enum MatchBy {
        INDEX, NAME, OUTPARAM
    }

    private final Matcher<? super S> subMatcher;
    private MatchBy matchBy;

    protected Description descForExpected = new StringDescription();
    protected Description descForFailure = new StringDescription();

    protected ParameterHolderMatcher(Matcher<? super S> subMatcher, MatchBy matchBy) {
        this.subMatcher = subMatcher;
        this.matchBy = matchBy;
    }


    @Override
    public boolean matches(Object item) {

        if (!(item instanceof ParameterHolder)) {
            populateCompatibilityFailureMessage(ParameterHolder.class, item);
            return false;
        }
        ParameterHolder holder = (ParameterHolder) item;

        S featureValue;
        if (MatchBy.INDEX.equals(this.matchBy)) {
            if (!(holder instanceof ParameterByIndexHolder)) {
                populateCompatibilityFailureMessage(ParameterByIndexHolder.class, holder);
                return false;
            }
            ParameterByIndexHolder byIndexHolder = (ParameterByIndexHolder) holder;
            if (!validateParameterByIndex(byIndexHolder, this.descForExpected, this.descForFailure)) {
                return false;  // validation failure
            }
            featureValue = getFeatureValue(byIndexHolder);
        } else if (MatchBy.NAME.equals(this.matchBy)) {
            if (!(holder instanceof ParameterByNameHolder)) {
                populateCompatibilityFailureMessage(ParameterByNameHolder.class, holder);
                return false;
            }
            ParameterByNameHolder byNameHolder = (ParameterByNameHolder) holder;
            if (!validateParameterByName(byNameHolder, this.descForExpected, this.descForFailure)) {
                return false;  // validation failure
            }
            featureValue = getFeatureValue(byNameHolder);
        } else {
            if (!(holder instanceof OutParameterHolder)) {
                populateCompatibilityFailureMessage(OutParameterHolder.class, holder);
                return false;
            }
            OutParameterHolder byOutParamHolder = (OutParameterHolder) holder;
            if (!validateParameterByOutParam(byOutParamHolder, this.descForExpected, this.descForFailure)) {
                return false;  // validation failure
            }
            featureValue = getFeatureValue(byOutParamHolder);
        }

        if (!subMatcher.matches(featureValue)) {
            Description description = new StringDescription();
            subMatcher.describeMismatch(featureValue, description);  // get mismatch desc
            this.descForExpected.appendDescriptionOf(subMatcher);  // populate from SelfDescribing
            this.descForFailure.appendText(description.toString());
            return false;
        }

        return true;
    }

    private void populateCompatibilityFailureMessage(Class<?> expectedInterface, Object actual) {
        String interfaceName = expectedInterface.getSimpleName();
        this.descForExpected.appendText("implementation of ").appendText(interfaceName);
        this.descForFailure.appendText(actual.getClass().getSimpleName()).appendText(" didn't implement ").appendText(interfaceName);
    }

    public S getFeatureValue(ParameterByIndexHolder actual) {
        throw new UnsupportedOperationException("Subclass that uses byIndex should implement this method.");
    }

    public S getFeatureValue(ParameterByNameHolder actual) {
        throw new UnsupportedOperationException("Subclass that uses byName should implement this method.");
    }

    public S getFeatureValue(OutParameterHolder actual) {
        throw new UnsupportedOperationException("Subclass that uses byName should implement this method.");
    }

    public boolean validateParameterByIndex(ParameterByIndexHolder actual, Description descForExpected, Description descForFailure) {
        return true;
    }

    public boolean validateParameterByName(ParameterByNameHolder actual, Description descForExpected, Description descForFailure) {
        return true;
    }

    public boolean validateParameterByOutParam(OutParameterHolder actual, Description descForExpected, Description descForFailure) {
        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(this.descForExpected.toString());
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        description.appendText(this.descForFailure.toString());
    }

    public static abstract class ParameterByIndexMatcher<T> extends ParameterHolderMatcher<T> {

        public ParameterByIndexMatcher(Matcher<? super T> subMatcher) {
            super(subMatcher, MatchBy.INDEX);
        }

        @Override
        public T getFeatureValue(ParameterByIndexHolder actual) {
            return featureValueOf(actual);
        }

        public abstract T featureValueOf(ParameterByIndexHolder actual);
    }

    public static abstract class ParameterByNameMatcher<T> extends ParameterHolderMatcher<T> {

        public ParameterByNameMatcher(Matcher<? super T> subMatcher) {
            super(subMatcher, MatchBy.NAME);
        }

        @Override
        public T getFeatureValue(ParameterByNameHolder actual) {
            return featureValueOf(actual);
        }

        public abstract T featureValueOf(ParameterByNameHolder actual);
    }

    public static abstract class OutParamMatcher<T> extends ParameterHolderMatcher<T> {

        public OutParamMatcher(Matcher<? super T> subMatcher) {
            super(subMatcher, MatchBy.OUTPARAM);
        }

        @Override
        public T getFeatureValue(OutParameterHolder actual) {
            return featureValueOf(actual);
        }

        public abstract T featureValueOf(OutParameterHolder actual);
    }
}
