package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.ParameterByIndexHolder;
import net.ttddyy.dsproxy.test.ParameterByNameHolder;
import net.ttddyy.dsproxy.test.ParameterHolder;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
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
                this.descForExpected.appendText("parameters as a ");
                return actual.getParamsByName();
            }
        };
    }

    // paramsByIndex(hasEntry(1, "foo"))
    public static Matcher<? super ParameterHolder> paramsByIndex(final Matcher<Map<? extends Integer, ?>> mapMatcher) {
        return new ParameterHolderMatcher.ParameterByIndexMatcher<Map<? extends Integer, ?>>(mapMatcher) {
            @Override
            public Map<? extends Integer, ?> featureValueOf(ParameterByIndexHolder actual) {
                this.descForExpected.appendText("parameters as a ");
                return actual.getParamsByIndex();
            }
        };
    }


    // paramIndexes(hasItem(1), hasItem(2))
    public static Matcher<? super ParameterHolder> paramIndexes(Matcher<? super Collection<Integer>> collectionMatcher) {
        return new ParameterHolderMatcher.ParameterByIndexMatcher<Collection<Integer>>(collectionMatcher) {
            @Override
            public Collection<Integer> featureValueOf(ParameterByIndexHolder actual) {
                descForExpected.appendText("parameter indexes as ");
                return actual.getParamIndexes();
            }
        };
    }

    // paramIndexes(1,2,3)
    public static Matcher<? super ParameterHolder> paramIndexes(Integer... indexes) {
        return paramIndexes(Matchers.hasItems(indexes));
    }

    // paramNames(hasItem("foo"), hasItem("bar"))
    public static Matcher<? super ParameterHolder> paramNames(Matcher<? super Collection<String>> collectionMatcher) {
        return new ParameterHolderMatcher.ParameterByNameMatcher<Collection<String>>(collectionMatcher) {
            @Override
            public Collection<String> featureValueOf(ParameterByNameHolder actual) {
                descForExpected.appendText("parameter names as ");
                return actual.getParamNames();
            }
        };
    }

    // paramNames("foo","bar","baz")
    public static Matcher<? super ParameterHolder> paramNames(String... names) {
        return paramNames(Matchers.hasItems(names));
    }


    // param("foo", is((Object) "FOO"))
    public static Matcher<? super ParameterHolder> param(final String name, Matcher<Object> matcher) {
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
                descForExpected.appendText("params[" + name + "] ");
                descForFailure.appendText("params[" + name + "] ");
                return true;
            }
        };
    }

    // param(1, is((Object) "FOO"))
    public static Matcher<? super ParameterHolder> param(final Integer index, Matcher<Object> matcher) {
        return new ParameterHolderMatcher.ParameterByIndexMatcher<Object>(matcher) {
            @Override
            public Object featureValueOf(ParameterByIndexHolder actual) {
                return actual.getParamsByIndex().get(index);
            }

            @Override
            public boolean validateParameterByIndex(ParameterByIndexHolder actual, Description descForExpected, Description descForFailure) {
                if (!actual.getParamIndexes().contains(index)) {
                    descForExpected.appendText("parameter index " + index);
                    descForFailure.appendText("parameter index " + index + " doesn't exist.");
                    return false;
                }
                descForExpected.appendText("params[" + index + "] ");
                descForFailure.appendText("params[" + index + "] ");
                return true;
            }
        };
    }

    //param("foo", String.class, is("FOO"))
    public static <T> Matcher<? super ParameterHolder> param(final String name, final Class<T> clazz, Matcher<? super T> matcher) {
        return new ParameterHolderMatcher.ParameterByNameMatcher<T>(matcher) {
            @Override
            @SuppressWarnings("unchecked")
            public T featureValueOf(ParameterByNameHolder actual) {
                return (T) actual.getParamsByName().get(name);
            }

            @Override
            public boolean validateParameterByName(ParameterByNameHolder actual, Description descForExpected, Description descForFailure) {
                Object value = actual.getParamsByName().get(name);
                if (value == null) {
                    descForExpected.appendText("parameter name " + name);
                    descForFailure.appendText("parameter name " + name + " doesn't exist.");
                    return false;
                } else if (!clazz.isAssignableFrom(value.getClass())) {
                    descForExpected.appendText("parameter can cast to " + clazz.getSimpleName());
                    descForFailure.appendText("parameter can not cast to" + clazz.getSimpleName());
                    return false;
                }
                descForExpected.appendText("params[" + name + "] ");
                descForFailure.appendText("params[" + name + "] ");
                return true;
            }
        };
    }

    //param(1, String.class, is("FOO"))
    public static <T> Matcher<? super ParameterHolder> param(final int index, final Class<T> clazz, Matcher<? super T> matcher) {
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
                descForExpected.appendText("params[" + index + "] ");
                descForFailure.appendText("params[" + index + "] ");
                return true;
            }
        };
    }

    public static Matcher<? super ParameterHolder> paramAsString(Integer index, Matcher<? super String> matcher) {
        return param(index, String.class, matcher);
    }

    public static Matcher<? super ParameterHolder> paramAsInteger(Integer index, Matcher<? super Integer> matcher) {
        return param(index, Integer.class, matcher);
    }

    public static Matcher<? super ParameterHolder> paramAsLong(Integer index, Matcher<? super Long> matcher) {
        return param(index, Long.class, matcher);
    }

    public static Matcher<? super ParameterHolder> paramAsDouble(Integer index, Matcher<? super Double> matcher) {
        return param(index, Double.class, matcher);
    }

    public static Matcher<? super ParameterHolder> paramAsShort(Integer index, Matcher<? super Short> matcher) {
        return param(index, Short.class, matcher);
    }

    public static Matcher<? super ParameterHolder> paramAsBoolean(Integer index, Matcher<? super Boolean> matcher) {
        return param(index, Boolean.class, matcher);
    }

    public static Matcher<? super ParameterHolder> paramAsByte(Integer index, Matcher<? super Byte> matcher) {
        return param(index, Byte.class, matcher);
    }

    public static Matcher<? super ParameterHolder> paramAsFloat(Integer index, Matcher<? super Float> matcher) {
        return param(index, Float.class, matcher);
    }

    public static Matcher<? super ParameterHolder> paramAsBigDecimal(Integer index, Matcher<? super BigDecimal> matcher) {
        return param(index, BigDecimal.class, matcher);
    }

    public static Matcher<? super ParameterHolder> paramAsBytes(Integer index, Matcher<? super byte[]> matcher) {
        return param(index, byte[].class, matcher);
    }

    public static Matcher<? super ParameterHolder> paramAsDate(Integer index, Matcher<? super Date> matcher) {
        return param(index, Date.class, matcher);
    }

    public static Matcher<? super ParameterHolder> paramAsTime(Integer index, Matcher<? super Time> matcher) {
        return param(index, Time.class, matcher);
    }

    public static Matcher<? super ParameterHolder> paramAsTimestamp(Integer index, Matcher<? super Timestamp> matcher) {
        return param(index, Timestamp.class, matcher);
    }


    public static Matcher<? super ParameterHolder> paramAsArray(Integer index, Matcher<? super Array> matcher) {
        return param(index, Array.class, matcher);
    }

    public static Matcher<? super ParameterHolder> paramAsString(String name, Matcher<? super String> matcher) {
        return param(name, String.class, matcher);
    }

    public static Matcher<? super ParameterHolder> paramAsInteger(String name, Matcher<? super Integer> matcher) {
        return param(name, Integer.class, matcher);
    }

    public static Matcher<? super ParameterHolder> paramAsLong(String name, Matcher<? super Long> matcher) {
        return param(name, Long.class, matcher);
    }

    public static Matcher<? super ParameterHolder> paramAsDouble(String name, Matcher<? super Double> matcher) {
        return param(name, Double.class, matcher);
    }

    public static Matcher<? super ParameterHolder> paramAsShort(String name, Matcher<? super Short> matcher) {
        return param(name, Short.class, matcher);
    }

    public static Matcher<? super ParameterHolder> paramAsBoolean(String name, Matcher<? super Boolean> matcher) {
        return param(name, Boolean.class, matcher);
    }

    public static Matcher<? super ParameterHolder> paramAsByte(String name, Matcher<? super Byte> matcher) {
        return param(name, Byte.class, matcher);
    }

    public static Matcher<? super ParameterHolder> paramAsFloat(String name, Matcher<? super Float> matcher) {
        return param(name, Float.class, matcher);
    }

    public static Matcher<? super ParameterHolder> paramAsBigDecimal(String name, Matcher<? super BigDecimal> matcher) {
        return param(name, BigDecimal.class, matcher);
    }

    public static Matcher<? super ParameterHolder> paramAsBytes(String name, Matcher<? super byte[]> matcher) {
        return param(name, byte[].class, matcher);
    }

    public static Matcher<? super ParameterHolder> paramAsDate(String name, Matcher<? super Date> matcher) {
        return param(name, Date.class, matcher);
    }

    public static Matcher<? super ParameterHolder> paramAsTime(String name, Matcher<? super Time> matcher) {
        return param(name, Time.class, matcher);
    }

    public static Matcher<? super ParameterHolder> paramAsTimestamp(String name, Matcher<? super Timestamp> matcher) {
        return param(name, Timestamp.class, matcher);
    }

    public static Matcher<? super ParameterHolder> paramAsArray(String name, Matcher<? super Array> matcher) {
        return param(name, Array.class, matcher);
    }

    public static Matcher<? super ParameterHolder> paramSetNull(final int index, int sqlType) {
        SqlTypeMatcher sqlTypeMatcher = new SqlTypeMatcher(sqlType, "NULL[", "]");
        return new ParameterHolderMatcher.ParameterByIndexMatcher<Integer>(sqlTypeMatcher) {
            @Override
            public Integer featureValueOf(ParameterByIndexHolder actual) {
                return actual.getSetNullParamsByIndex().get(index);
            }

            @Override
            public boolean validateParameterByIndex(ParameterByIndexHolder actual, Description descForExpected, Description descForFailure) {
                if (!actual.getSetNullParamsByIndex().containsKey(index)) {
                    descForExpected.appendText("parameter index " + index);
                    descForFailure.appendText("parameter index " + index + " doesn't exist.");
                    return false;
                }
                descForExpected.appendText("params[" + index + "] is ");
                descForFailure.appendText("params[" + index + "] was ");
                return true;
            }
        };
    }

    public static Matcher<? super ParameterHolder> paramSetNull(final String name, int sqlType) {
        SqlTypeMatcher sqlTypeMatcher = new SqlTypeMatcher(sqlType, "NULL[", "]");
        return new ParameterHolderMatcher.ParameterByNameMatcher<Integer>(sqlTypeMatcher) {
            @Override
            public Integer featureValueOf(ParameterByNameHolder actual) {
                return actual.getSetNullParamsByName().get(name);
            }

            @Override
            public boolean validateParameterByName(ParameterByNameHolder actual, Description descForExpected, Description descForFailure) {
                if (!actual.getSetNullParamsByName().containsKey(name)) {
                    descForExpected.appendText("parameter name " + name);
                    descForFailure.appendText("parameter name " + name + " doesn't exist.");
                    return false;
                }
                descForExpected.appendText("params[" + name + "] is ");
                descForFailure.appendText("params[" + name + "] was ");
                return true;
            }
        };
    }

    // Blob, Clob, NClob, java.net.URL


}
