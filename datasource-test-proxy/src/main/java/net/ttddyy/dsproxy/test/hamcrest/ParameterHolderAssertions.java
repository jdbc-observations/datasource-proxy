package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.ParameterByIndexHolder;
import net.ttddyy.dsproxy.test.ParameterByNameHolder;
import net.ttddyy.dsproxy.test.ParameterHolder;
import org.hamcrest.BaseMatcher;
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
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Hamcrest matchers for {@link ParameterHolder}({@link ParameterByIndexHolder}, {@link ParameterByNameHolder}).
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class ParameterHolderAssertions {


    /**
     * Matcher to examine parameters by name as a {@link Map}(key={@link String}, value={@link Object}).
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, paramsByName(hasEntry("foo", (Object) "FOO"))); </pre>
     *
     * @param mapMatcher a {@link Map} matcher
     */
    public static Matcher<? super ParameterHolder> paramsByName(final Matcher<Map<? extends String, ?>> mapMatcher) {
        return new ParameterHolderMatcher.ParameterByNameMatcher<Map<? extends String, ?>>(mapMatcher) {
            @Override
            public Map<? extends String, ?> featureValueOf(ParameterByNameHolder actual) {
                this.descForExpected.appendText("parameters as a ");
                return actual.getSetParamsByName();
            }
        };
    }

    /**
     * Matcher to examine parameters by index as a {@link Map}(key={@link Integer}, value={@link Object}).
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, paramsByName(paramsByIndex(1, (Object) "FOO"))); </pre>
     *
     * @param mapMatcher a {@link Map} matcher
     */
    public static Matcher<? super ParameterHolder> paramsByIndex(final Matcher<Map<? extends Integer, ?>> mapMatcher) {
        return new ParameterHolderMatcher.ParameterByIndexMatcher<Map<? extends Integer, ?>>(mapMatcher) {
            @Override
            public Map<? extends Integer, ?> featureValueOf(ParameterByIndexHolder actual) {
                this.descForExpected.appendText("parameters as a ");
                return actual.getParamsByIndex();
            }
        };
    }


    /**
     * Matcher to examine parameter indexes as a {@link Collection} of {@link Integer}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, paramIndexes(hasItem(1), hasItem(2))); </pre>
     *
     * @param collectionMatcher a {@link Collection} matcher
     */
    public static Matcher<? super ParameterHolder> paramIndexes(Matcher<? super Collection<Integer>> collectionMatcher) {
        return new ParameterHolderMatcher.ParameterByIndexMatcher<Collection<Integer>>(collectionMatcher) {
            @Override
            public Collection<Integer> featureValueOf(ParameterByIndexHolder actual) {
                descForExpected.appendText("parameter indexes as ");
                return actual.getParamIndexes();
            }
        };
    }

    /**
     * Matcher to examine parameter indexes.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, paramIndexes(1,2,3)); </pre>
     *
     * @param indexes parameter indexes
     */
    public static Matcher<? super ParameterHolder> paramIndexes(Integer... indexes) {
        return paramIndexes(Matchers.hasItems(indexes));
    }

    /**
     * Matcher to examine parameter names as a {@link Collection} of {@link String}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, paramNames(hasItem("foo"), hasItem("bar"))); </pre>
     *
     * @param collectionMatcher a {@link Collection} matcher
     */
    public static Matcher<? super ParameterHolder> paramNames(Matcher<? super Collection<String>> collectionMatcher) {
        return new ParameterHolderMatcher.ParameterByNameMatcher<Collection<String>>(collectionMatcher) {
            @Override
            public Collection<String> featureValueOf(ParameterByNameHolder actual) {
                descForExpected.appendText("parameter names as ");
                return actual.getParamNames();
            }
        };
    }

    /**
     * Matcher to examine parameter names.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, paramNames("foo","bar","baz")); </pre>
     *
     * @param names parameter names
     */
    public static Matcher<? super ParameterHolder> paramNames(String... names) {
        return paramNames(Matchers.hasItems(names));
    }


    /**
     * Matcher to examine parameter by name.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, param("foo", is((Object) "FOO"))); </pre>
     *
     * @param name parameter name
     */
    public static Matcher<? super ParameterHolder> param(final String name, Matcher<Object> matcher) {
        return new ParameterHolderMatcher.ParameterByNameMatcher<Object>(matcher) {
            @Override
            public Object featureValueOf(ParameterByNameHolder actual) {
                return actual.getSetParamsByName().get(name);
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

    /**
     * Matcher to examine parameter by index.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, param(1, is((Object) "FOO"))); </pre>
     *
     * @param index parameter index
     */
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

    /**
     * Matcher to examine parameter by name with given class type value.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, param("foo", String.class, is("FOO"))); </pre>
     *
     * @param name  parameter name
     * @param clazz value type
     */
    public static <T> Matcher<? super ParameterHolder> param(final String name, final Class<T> clazz, Matcher<? super T> matcher) {
        return new ParameterHolderMatcher.ParameterByNameMatcher<T>(matcher) {
            @Override
            @SuppressWarnings("unchecked")
            public T featureValueOf(ParameterByNameHolder actual) {
                return (T) actual.getSetParamsByName().get(name);
            }

            @Override
            public boolean validateParameterByName(ParameterByNameHolder actual, Description descForExpected, Description descForFailure) {
                Object value = actual.getSetParamsByName().get(name);
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

    /**
     * Matcher to examine parameter by index with given class type value.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, param(1, String.class, is("FOO"))); </pre>
     *
     * @param index parameter index
     * @param clazz value type
     */
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

    /**
     * Matcher to examine parameter by index with value as {@link String}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, paramAsString(1, is("FOO"))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsString(Integer index, Matcher<? super String> matcher) {
        return param(index, String.class, matcher);
    }

    /**
     * Matcher to examine parameter by index with value as {@link Integer}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, paramAsInteger(1, is(100))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsInteger(Integer index, Matcher<? super Integer> matcher) {
        return param(index, Integer.class, matcher);
    }

    /**
     * Matcher to examine parameter by index with value as {@link Long}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, paramAsLong(1, is(100L))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsLong(Integer index, Matcher<? super Long> matcher) {
        return param(index, Long.class, matcher);
    }

    /**
     * Matcher to examine parameter by index with value as {@link Double}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, paramAsDouble(1, is(10.0))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsDouble(Integer index, Matcher<? super Double> matcher) {
        return param(index, Double.class, matcher);
    }

    /**
     * Matcher to examine parameter by index with value as {@link Short}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, paramAsShort(1, is((short)1))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsShort(Integer index, Matcher<? super Short> matcher) {
        return param(index, Short.class, matcher);
    }

    /**
     * Matcher to examine parameter by index with value as {@link Boolean}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, paramAsBoolean(1, is(true))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsBoolean(Integer index, Matcher<? super Boolean> matcher) {
        return param(index, Boolean.class, matcher);
    }

    /**
     * Matcher to examine parameter by index with value as {@link Byte}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, paramAsByte(1, is((byte)1))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsByte(Integer index, Matcher<? super Byte> matcher) {
        return param(index, Byte.class, matcher);
    }

    /**
     * Matcher to examine parameter by index with value as {@link Float}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, paramAsFloat(1, is((float)1.0))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsFloat(Integer index, Matcher<? super Float> matcher) {
        return param(index, Float.class, matcher);
    }

    /**
     * Matcher to examine parameter by index with value as {@link BigDecimal}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, paramAsBigDecimal(1, is(new BigDecimal(10))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsBigDecimal(Integer index, Matcher<? super BigDecimal> matcher) {
        return param(index, BigDecimal.class, matcher);
    }

    /**
     * Matcher to examine parameter by index with value as {@code byte[]}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, paramAsBytes(1, is(new byte[]{0xa, 0xb}))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsBytes(Integer index, Matcher<? super byte[]> matcher) {
        return param(index, byte[].class, matcher);
    }

    /**
     * Matcher to examine parameter by index with value as {@link Date}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, paramAsDate(1, is((float)1.0))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsDate(Integer index, Matcher<? super Date> matcher) {
        return param(index, Date.class, matcher);
    }

    /**
     * Matcher to examine parameter by index with value as {@link Time}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, paramAsTime(1, is(new Time(1000)))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsTime(Integer index, Matcher<? super Time> matcher) {
        return param(index, Time.class, matcher);
    }

    /**
     * Matcher to examine parameter by index with value as {@link Timestamp}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, paramAsTimestamp(1, is(new Timestamp(1000)))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsTimestamp(Integer index, Matcher<? super Timestamp> matcher) {
        return param(index, Timestamp.class, matcher);
    }

    /**
     * Matcher to examine parameter by index with value as {@link Array}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, paramAsArray(1, is(array))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsArray(Integer index, Matcher<? super Array> matcher) {
        return param(index, Array.class, matcher);
    }

    /**
     * Matcher to examine parameter by name with value as {@link String}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, paramAsString("foo", is("FOO"))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsString(String name, Matcher<? super String> matcher) {
        return param(name, String.class, matcher);
    }

    /**
     * Matcher to examine parameter by name with value as {@link Integer}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, paramAsInteger("foo", is(100))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsInteger(String name, Matcher<? super Integer> matcher) {
        return param(name, Integer.class, matcher);
    }

    /**
     * Matcher to examine parameter by name with value as {@link Long}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, paramAsLong("foo", is(100L))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsLong(String name, Matcher<? super Long> matcher) {
        return param(name, Long.class, matcher);
    }

    /**
     * Matcher to examine parameter by name with value as {@link Double}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, paramAsDouble("foo", is(10.0))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsDouble(String name, Matcher<? super Double> matcher) {
        return param(name, Double.class, matcher);
    }

    /**
     * Matcher to examine parameter by name with value as {@link Short}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, paramAsShort("foo", is((short)1))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsShort(String name, Matcher<? super Short> matcher) {
        return param(name, Short.class, matcher);
    }

    /**
     * Matcher to examine parameter by name with value as {@link Boolean}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, paramAsBoolean("foo", is(true))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsBoolean(String name, Matcher<? super Boolean> matcher) {
        return param(name, Boolean.class, matcher);
    }

    /**
     * Matcher to examine parameter by name with value as {@code byte[]}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, paramAsBytes("foo", is(new byte[]{0xa, 0xb}))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsByte(String name, Matcher<? super Byte> matcher) {
        return param(name, Byte.class, matcher);
    }

    /**
     * Matcher to examine parameter by name with value as {@link Float}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, paramAsFloat("foo", is((float)1.0))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsFloat(String name, Matcher<? super Float> matcher) {
        return param(name, Float.class, matcher);
    }

    /**
     * Matcher to examine parameter by name with value as {@link BigDecimal}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, paramAsBigDecimal("foo", is(new BigDecimal(10))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsBigDecimal(String name, Matcher<? super BigDecimal> matcher) {
        return param(name, BigDecimal.class, matcher);
    }

    /**
     * Matcher to examine parameter by name with value as {@code byte[]}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, paramAsBytes("foo", is(new byte[]{0xa, 0xb}))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsBytes(String name, Matcher<? super byte[]> matcher) {
        return param(name, byte[].class, matcher);
    }

    /**
     * Matcher to examine parameter by name with value as {@link Time}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, paramAsTime("foo", is(new Time(1000)))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsDate(String name, Matcher<? super Date> matcher) {
        return param(name, Date.class, matcher);
    }


    /**
     * Matcher to examine parameter by name with value as {@link Time}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, paramAsTime("foo", is(new Time(1000)))); </pre>
     */

    public static Matcher<? super ParameterHolder> paramAsTime(String name, Matcher<? super Time> matcher) {
        return param(name, Time.class, matcher);
    }

    /**
     * Matcher to examine parameter by name with value as {@link Timestamp}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, paramAsTimestamp("foo", is(new Timestamp(1000)))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsTimestamp(String name, Matcher<? super Timestamp> matcher) {
        return param(name, Timestamp.class, matcher);
    }

    /**
     * Matcher to examine parameter by name with value as {@link Array}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, paramAsArray("foo", is(array))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsArray(String name, Matcher<? super Array> matcher) {
        return param(name, Array.class, matcher);
    }

    // Blob, Clob, NClob, java.net.URL

    /**
     * Matcher to examine parameter by index is {@code setNull} operation with given {@link java.sql.Types}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, nullParam(1, is(Types.VARCHAR))); </pre>
     */
    public static Matcher<? super ParameterHolder> nullParam(final int index, int sqlType) {
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

    /**
     * Matcher to examine parameter by name is {@code setNull} operation with given {@link java.sql.Types}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, nullParam("foo", is(Types.VARCHAR))); </pre>
     */
    public static Matcher<? super ParameterHolder> nullParam(final String name, int sqlType) {
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

    /**
     * Matcher to examine parameter by name is {@code setNull} operation.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, nullParam("foo")); </pre>
     */
    public static Matcher<? super ParameterHolder> nullParam(final String name) {
        EmptyParameterHolderMatcher emptyMatcher = new EmptyParameterHolderMatcher();
        return new ParameterHolderMatcher.ParameterByNameMatcher<ParameterHolder>(emptyMatcher) {
            @Override
            public ParameterHolder featureValueOf(ParameterByNameHolder actual) {
                return null;  // won't be used
            }

            @Override
            public boolean validateParameterByName(ParameterByNameHolder actual, Description descForExpected, Description descForFailure) {
                Set<String> names = actual.getSetNullParamsByName().keySet();
                if (!names.contains(name)) {
                    descForExpected.appendText("params[" + name + "] is NULL");
                    SortedSet<String> sorted = new TreeSet<String>(names);
                    descForFailure.appendText("setNull names are ").appendValueList("[", ", ", "]", sorted);
                    return false;
                }
                return true;
            }
        };
    }

    /**
     * Matcher to examine parameter by index is {@code setNull} operation.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, nullParam(1)); </pre>
     */
    public static Matcher<? super ParameterHolder> nullParam(final int index) {
        EmptyParameterHolderMatcher emptyMatcher = new EmptyParameterHolderMatcher();
        return new ParameterHolderMatcher.ParameterByIndexMatcher<ParameterHolder>(emptyMatcher) {
            @Override
            public ParameterHolder featureValueOf(ParameterByIndexHolder actual) {
                return null;  // won't be used
            }

            @Override
            public boolean validateParameterByIndex(ParameterByIndexHolder actual, Description descForExpected, Description descForFailure) {
                Set<Integer> indexes = actual.getSetNullParamsByIndex().keySet();
                if (!indexes.contains(index)) {
                    descForExpected.appendText("params[" + index + "] is NULL");
                    SortedSet<Integer> sorted = new TreeSet<Integer>(indexes);
                    descForFailure.appendText("setNull indexes are ").appendValueList("[", ", ", "]", sorted);
                    return false;
                }
                return true;
            }
        };
    }


    private static class EmptyParameterHolderMatcher extends BaseMatcher<ParameterHolder> {

        @Override
        public boolean matches(Object item) {
            return true;
        }

        @Override
        public void describeTo(Description description) {
        }
    }

}
