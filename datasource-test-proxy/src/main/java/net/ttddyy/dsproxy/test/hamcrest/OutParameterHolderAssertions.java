package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.OutParameterHolder;
import net.ttddyy.dsproxy.test.ParameterHolder;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import java.sql.SQLType;
import java.util.Collection;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Hamcrest matchers for {@link OutParameterHolder}.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class OutParameterHolderAssertions {

    /**
     * Matcher to examine out-parameter names as a {@link Collection} of {@link String}.
     * <p>
     * Example:
     * <pre> assertThat(outParameterHolder, outParamNames(hasItem("foo"), hasItem("bar"))); </pre>
     *
     * @param collectionMatcher a {@link Collection} matcher
     */
    public static Matcher<? super ParameterHolder> outParamNames(Matcher<? super Collection<String>> collectionMatcher) {
        return new ParameterHolderMatcher.OutParamMatcher<Collection<String>>(collectionMatcher) {
            @Override
            public Collection<String> featureValueOf(OutParameterHolder actual) {
                return actual.getOutParamNames();
            }
        };
    }

    /**
     * Matcher to examine out-parameter indexes as a {@link Collection} of {@link Integer}.
     * <p>
     * Example:
     * <pre> assertThat(outParameterHolder, outParamIndexes(hasItem(1))); </pre>
     *
     * @param collectionMatcher a {@link Collection} matcher
     */
    public static Matcher<? super ParameterHolder> outParamIndexes(Matcher<? super Collection<Integer>> collectionMatcher) {
        return new ParameterHolderMatcher.OutParamMatcher<Collection<Integer>>(collectionMatcher) {
            @Override
            public Collection<Integer> featureValueOf(OutParameterHolder actual) {
                return actual.getOutParamIndexes();
            }
        };
    }

    /**
     * Matcher to examine out-parameter by name and int sqlType.
     * <p>
     * Example:
     * <pre> assertThat(outParameterHolder, outParam("foo", Types.BOOLEAN)); </pre>
     *
     * @param paramName out-parameter name
     * @param sqlType   sqlType in int
     */
    public static Matcher<? super ParameterHolder> outParam(String paramName, int sqlType) {
        SqlTypeMatcher matcher = new SqlTypeMatcher(sqlType);
        return getOutputMatcherByName(paramName, matcher, Integer.class);
    }

    /**
     * Matcher to examine out-parameter by name and {@link SQLType}.
     * <p>
     * Example:
     * <pre> assertThat(outParameterHolder, outParam("foo", JDBCType.INTEGER)); </pre>
     *
     * @param paramName out-parameter name
     * @param sqlType   sqlType
     */
    public static Matcher<? super ParameterHolder> outParam(String paramName, SQLType sqlType) {
        Matcher<SQLType> matcher = Matchers.equalTo(sqlType);
        return getOutputMatcherByName(paramName, matcher, SQLType.class);
    }


    /**
     * Matcher to examine out-parameter by index and int sqlType.
     * <p>
     * Example:
     * <pre> assertThat(outParameterHolder, outParam(1, Types.BOOLEAN)); </pre>
     *
     * @param index   out-parameter index
     * @param sqlType sqlType in int
     */
    public static Matcher<? super ParameterHolder> outParam(int index, int sqlType) {
        SqlTypeMatcher matcher = new SqlTypeMatcher(sqlType);
        return getOutputMatcherByIndex(index, matcher, Integer.class);
    }

    /**
     * Matcher to examine out-parameter by index and {@link SQLType}.
     * <p>
     * Example:
     * <pre> assertThat(outParameterHolder, outParam(1, JDBCType.INTEGER)); </pre>
     *
     * @param index   out-parameter index
     * @param sqlType sqlType
     */
    public static Matcher<? super ParameterHolder> outParam(int index, SQLType sqlType) {
        Matcher<SQLType> matcher = Matchers.equalTo(sqlType);
        return getOutputMatcherByIndex(index, matcher, SQLType.class);
    }


    private static <T> Matcher<? super ParameterHolder> getOutputMatcherByName(final String paramName, Matcher<T> matcher, final Class<T> valueType) {
        return new ParameterHolderMatcher.OutParamMatcher<T>(matcher) {
            @Override
            public boolean validateParameterByOutParam(OutParameterHolder actual, Description descForExpected, Description descForFailure) {
                Object value = actual.getOutParamsByName().get(paramName);
                if (value == null) {
                    Set<String> names = actual.getOutParamsByName().keySet();
                    SortedSet<String> sorted = new TreeSet<String>(names);
                    descForExpected.appendText("out param name " + paramName + " exist");
                    descForFailure.appendText("out param names are " + sorted);
                    return false;
                } else if (!valueType.isAssignableFrom(value.getClass())) {
                    descForExpected.appendText("value of out param " + paramName + " is instance of " + valueType);
                    descForFailure.appendText("value " + value + " was " + value.getClass());
                    return false;
                }

                return true;
            }

            @Override
            @SuppressWarnings("unchecked")
            public T featureValueOf(OutParameterHolder actual) {
                this.descForExpected.appendText("out param " + paramName + " is ");
                this.descForFailure.appendText("out param " + paramName + " was ");
                return (T) actual.getOutParamsByName().get(paramName);
            }

        };
    }


    private static <T> Matcher<? super ParameterHolder> getOutputMatcherByIndex(final int index, Matcher<T> matcher, final Class<T> valueType) {
        return new ParameterHolderMatcher.OutParamMatcher<T>(matcher) {
            @Override
            public boolean validateParameterByOutParam(OutParameterHolder actual, Description descForExpected, Description descForFailure) {
                Object value = actual.getOutParamsByIndex().get(index);
                if (value == null) {
                    Set<Integer> indexes = actual.getOutParamsByIndex().keySet();
                    SortedSet<Integer> sorted = new TreeSet<Integer>(indexes);
                    descForExpected.appendText("out param index " + index + " exist");
                    descForFailure.appendText("out param indexes are " + sorted);
                    return false;
                } else if (!valueType.isAssignableFrom(value.getClass())) {
                    descForExpected.appendText("value of out param index " + index + " is instance of " + valueType);
                    descForFailure.appendText("value " + value + " was " + value.getClass());
                    return false;
                }

                return true;
            }

            @Override
            @SuppressWarnings("unchecked")
            public T featureValueOf(OutParameterHolder actual) {
                this.descForExpected.appendText("out param index " + index + " is ");
                this.descForFailure.appendText("out param index " + index + " was ");
                return (T) actual.getOutParamsByIndex().get(index);
            }
        };
    }

}
