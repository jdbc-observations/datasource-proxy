package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.ParameterByIndexHolder;
import net.ttddyy.dsproxy.test.ParameterByNameHolder;
import net.ttddyy.dsproxy.test.ParameterHolder;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import java.util.Collection;
import java.util.Map;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class ParameterHolderAssertions {


    public static Matcher<? super ParameterHolder> paramsByName(final Matcher<Map<? extends String, ?>> mapMatcher) {
        return new ParameterHolderMatcher.ParameterByNameMatcher<Map<? extends String, ?>>(mapMatcher) {
            @Override
            public Map<? extends String, ?> featureValueOf(ParameterByNameHolder actual) {
                return actual.getParamsByName();
            }
        };
    }

    // batch(0, paramsByIndex(hasEntry(1, "foo")))
    public static Matcher<? super ParameterHolder> paramsByIndex(final Matcher<Map<? extends Integer, ?>> mapMatcher) {
        return new ParameterHolderMatcher.ParameterByIndexMatcher<Map<? extends Integer, ?>>(mapMatcher) {
            @Override
            public Map<? extends Integer, ?> featureValueOf(ParameterByIndexHolder actual) {
                return actual.getParamsByIndex();
            }
        };
    }


    // batch(0, paramIndexes(hasItem(1), hasItem(2)))
    public static Matcher<? super ParameterHolder> paramIndexes(Matcher<? super Collection<Integer>> collectionMatcher) {
        return new ParameterHolderMatcher.ParameterByIndexMatcher<Collection<Integer>>(collectionMatcher) {
            @Override
            public Collection<Integer> featureValueOf(ParameterByIndexHolder actual) {
                return actual.getParamIndexes();
            }
        };
    }

    // batch(0, paramIndexes(1,2,3))
    public static Matcher<? super ParameterHolder> paramIndexes(Integer... indexes) {
        return paramIndexes(Matchers.hasItems(indexes));
    }

    // batch(0, paramNames(hasItem("foo"), hasItem("bar")))
    public static Matcher<? super ParameterHolder> paramNames(Matcher<? super Collection<String>> collectionMatcher) {
        return new ParameterHolderMatcher.ParameterByNameMatcher<Collection<String>>(collectionMatcher) {
            @Override
            public Collection<String> featureValueOf(ParameterByNameHolder actual) {
                return actual.getParamNames();
            }
        };
    }

    // batch(0, paramValue("foo", is((Object) "FOO")))
    public static Matcher<? super ParameterHolder> paramValue(final String name, Matcher<Object> matcher) {
        return new ParameterHolderMatcher.ParameterByNameMatcher<Object>(matcher) {
            @Override
            public Object featureValueOf(ParameterByNameHolder actual) {
                return actual.getParamsByName().get(name);
            }

            @Override
            public boolean validateParameterByName(ParameterByNameHolder actual, Description descForExpected, Description descForFailure) {
                if (!actual.getParamNames().contains(name)) {
                    descForExpected.appendText("parameter name " + name);
                    descForFailure.appendText("parameter name " + name + " doesn't exist.");
                    return false;
                }
                return true;
            }
        };
    }

    // batch(0, paramValue(1, is((Object) "FOO")))
    public static Matcher<? super ParameterHolder> paramValue(final Integer index, Matcher<Object> matcher) {
        return new ParameterHolderMatcher.ParameterByIndexMatcher<Object>(matcher) {
            @Override
            public Object featureValueOf(ParameterByIndexHolder actual) {
                return actual.getParamIndexes().get(index);
            }

            @Override
            public boolean validateParameterByIndex(ParameterByIndexHolder actual, Description descForExpected, Description descForFailure) {
                if (!actual.getParamIndexes().contains(index)) {
                    descForExpected.appendText("parameter index " + index);
                    descForFailure.appendText("parameter index " + index + " doesn't exist.");
                    return false;
                }
                return true;
            }
        };
    }

    //batch(0, paramValue("foo", String.class, is("FOO"))));
    public static <T> Matcher<? super ParameterHolder> paramValue(final String key, final Class<T> clazz, Matcher<? super T> matcher) {
        return new ParameterHolderMatcher.ParameterByNameMatcher<T>(matcher) {
            @Override
            @SuppressWarnings("unchecked")
            public T featureValueOf(ParameterByNameHolder actual) {
                return (T) actual.getParamsByName().get(key);
            }

            @Override
            public boolean validateParameterByName(ParameterByNameHolder actual, Description descForExpected, Description descForFailure) {
                Object value = actual.getParamsByName().get(key);
                if (value == null) {
                    descForExpected.appendText("parameter name " + key);
                    descForFailure.appendText("parameter name " + key + " doesn't exist.");
                    return false;
                } else if (!clazz.isAssignableFrom(value.getClass())) {
                    descForExpected.appendText("parameter can cast to " + clazz.getSimpleName());
                    descForFailure.appendText("parameter can not cast to" + clazz.getSimpleName());
                    return false;
                }
                return true;
            }
        };
    }

    //batch(0, paramValue(1, String.class, is("FOO"))));
    public static <T> Matcher<? super ParameterHolder> paramValue(final Integer index, final Class<T> clazz, Matcher<? super T> matcher) {
        return new ParameterHolderMatcher.ParameterByIndexMatcher<T>(matcher) {
            @Override
            @SuppressWarnings("unchecked")
            public T featureValueOf(ParameterByIndexHolder actual) {
                return (T) actual.getParamsByIndex().get(index);
            }

            @Override
            public boolean validateParameterByIndex(ParameterByIndexHolder actual, Description descForExpected, Description descForFailure) {
                Object value = actual.getParamsByIndex().get(index);
                if (value == null) {
                    descForExpected.appendText("parameter index " + index);
                    descForFailure.appendText("parameter index " + index + " doesn't exist.");
                    return false;
                } else if (!clazz.isAssignableFrom(value.getClass())) {
                    descForExpected.appendText("parameter can cast to " + clazz.getSimpleName());
                    descForFailure.appendText("parameter can not cast to" + clazz.getSimpleName());
                    return false;
                }
                return true;
            }
        };
    }

    public static Matcher<? super ParameterHolder> paramValueAsString(final Integer index, Matcher<? super String> matcher) {
        return paramValue(index, String.class, matcher);
    }

    public static Matcher<? super ParameterHolder> paramValueAsInteger(final Integer index, Matcher<? super Integer> matcher) {
        return paramValue(index, Integer.class, matcher);
    }


}
