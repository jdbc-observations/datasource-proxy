package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.BatchExecutionEntry;
import net.ttddyy.dsproxy.test.BatchParameterHolder;
import net.ttddyy.dsproxy.test.OutParameterHolder;
import net.ttddyy.dsproxy.test.ParameterHolder;
import org.hamcrest.Description;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;


/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class BatchParameterHolderAssertions {

    // TODO: rename classes. they are not actually matchers, just providing static methods.

    //  assertThat(pbe, is(success()));
    //  assertThat(pbe, query(startWith("SELECT")));
    //  assertThat(pbe.getQuery(), startWith("SELECT"));
    //  assertThat(pbe, batchSize(3));
    //  assertThat(pbe, firstBatch().args(hasEntry("1", (Object) "foo")));
    //  assertThat(pbe, batch(0, paramsByName(hasEntry("bar", (Object) "BAR")));
    //  assertThat(pbe, batch(0, paramNames(hasItem("foo"))));
    //  assertThat(pbe, batch(0, paramValue("number", is((Object) 100))));
    //  assertThat(pbe, batch(0, paramValue("number", Integer.class, is(100))));
    //  assertThat(pbe, batch(0, paramValueAsString("number", is("foo"))));
    //  assertThat(pbe, batch(0, paramAsInteger("number", is(100))));
    //  assertThat(pbe, batch(0, paramValueAsLong("number", is(100L))));

    //  assertThat(pbe, batch(0, argValuesAsString(hasItems("foo")));
    //  assertThat(pbe, batch(0, argValuesAs(String.class, hasItems("foo")));

    //  assertThat(cbe, batch(0, outParams(paramNames(hasItems("foo")));                      // Matcher<? super Collection<String>>
    //  assertThat(cbe, batch(0, outParams(paramIndexes(hasItems(1)));                        // Matcher<? super Collection<Integer>>
    //  assertThat(cbe, batch(0, outParams(byIndex(hasEntry(1, 1))));                         // Matcher<? super Map<? extends Integer, ? super Integer>>
    //  assertThat(cbe, batch(0, outParams(byIndexAndType(hasEntry(1, JDBCType.INTEGER))));   // Matcher<? super Map<? extends Integer, ? super SQLType>>
    //  assertThat(cbe, batch(0, outParams(byName(hasEntry("foo", 1))));                        // Matcher<? super Map<? extends String, ? super Integer>>
    //  assertThat(cbe, batch(0, outParams(byNameAndType(hasEntry("foo", JDBCType.INTEGER))));  // Matcher<? super Map<? extends String, ? super SQLType>>
    //  assertThat(cbe, batch(0, outParams(param("foo", Types.INTEGER)))));
    //  assertThat(cbe, batch(0, outParams(param("foo", JDBCType.INTEGER)))));
    //  assertThat(cbe, batch(0, outParams(param(1, Types.INTEGER)))));
    //  assertThat(cbe, batch(0, outParams(param(1, JDBCType.INTEGER)))));

    //  assertThat(ce, outParams(paramNames(hasItems("foo"))));                      // Matcher<? super Collection<String>>
    //  assertThat(ce, outParams(paramIndexes(hasItems(1))));                        // Matcher<? super Collection<Integer>>
    //  assertThat(ce, outParams(byIndexAndInt(hasEntry(1, 1)))));                         // Matcher<? super Map<? extends Integer, ? super Integer>>
    //  assertThat(ce, outParams(byIndexAndType(hasEntry(1, JDBCType.INTEGER))));    // Matcher<? super Map<? extends Integer, ? super SQLType>>
    //  assertThat(ce, outParams(byNameAndInt(hasEntry("foo", 1))));                        // Matcher<? super Map<? extends String, ? super Integer>>
    //  assertThat(ce, outParams(byNameAndType(hasEntry("foo", JDBCType.INTEGER))));  // Matcher<? super Map<? extends String, ? super SQLType>>

    // ParameterHolder outParams(Matcher<? super ParameterHolder>...)
    // outParams(paramNames(...)) => outParamNames(...)

    ////////////
    // TODO: handle setNull
    // TODO: actually paramNames is only for CallableStatement

    // params(Matcher<? super ParameterHolder>...)
    // outParams(Matcher<? super ParameterHolder>...)

    // param(String, Matcher<?>)
    // param(String, Class<T>, Matcher<? super T>)
    // param(int, Matcher<?>)
    // param(int, Class<T>, Matcher<? super T>)

    // outParamNames(Matcher<? super Collection<String>>)
    // outParamIndexes(Matcher<? super Collection<Integer>>)
    // outParam(String, int)
    // outParam(String, SQLType)
    // outParam(int, int)
    // outParam(int, SQLType)

    // byName(Matcher<? super Map<? extends String, ?>>)
    // byValue(Matcher<? super Map<? extends Integer, ?>>)


    // batch(int, Matcher<? super ParameterHolder>)

    // samples: PreparedExecution
    // assertThat(pe, paramIndexes(hasItem(10)));
    // assertThat(pe, paramsByIndex(hasEntry(10, (Object)"FOO")));
    // assertThat(pe, param(10, is((Object) 100));
    // assertThat(pe, param(10, Integer.class, is(100)));
    // assertThat(pe, paramAsInteger(10, is(100)));
    // assertThat(pe, paramNull(10));
    // assertThat(pe, paramNull(10, Types.INTEGER));
    // assertThat(pe, allOf(paramAsInteger(10, is(100)), paramAsInteger("foo", is(100))));

    // samples: CallableExecution   TODO: for paramByName, paramByIndex

    // params
    // assertThat(ce, paramNames(hasItem("foo")));
    // assertThat(ce, paramIndexes(hasItem(10)));
    // assertThat(ce, paramsByName(hasEntry("foo", (Object)"FOO")));
    // assertThat(ce, paramsByIndex(hasEntry(10, (Object)"FOO")));
    // assertThat(ce, param("foo", is((Object) 100));
    // assertThat(ce, param("foo", Integer.class, is(100)));
    // assertThat(ce, paramAsInteger("foo", is(100)));
    // assertThat(ce, param(10, is((Object) 100));
    // assertThat(ce, param(10, Integer.class, is(100)));
    // assertThat(ce, paramAsInteger(10, is(100)));
    // assertThat(ce, paramNull("foo"));
    // assertThat(ce, paramNull("foo", Types.INTEGER));
    // assertThat(ce, paramNull(10));
    // assertThat(ce, paramNull(10, Types.INTEGER));
    // assertThat(ce, allOf(paramAsInteger(10, is(100)), paramAsInteger("foo", is(100))));

    // outParams
    // assertThat(ce, outParamNames(hasItem("foo")));
    // assertThat(ce, outParamIndexes(hasItem(10)));
    // assertThat(ce, outParam("foo", Types.INTEGER)));
    // assertThat(ce, outParam("foo", JDBCType.INTEGER)));
    // assertThat(ce, outParam(10, Types.INTEGER)));
    // assertThat(ce, outParam(10, JDBCType.INTEGER)));
    // assertThat(ce, allOf(outParam("foo", JDBCType.INTEGER))), outParam(10, Types.INTEGER)));

    // samples: CallableBatchExecution
    // assertThat(cbe, batch(0, outParamNames(hasItem("foo"))));
    // assertThat(cbe, batch(0, outParamIndexes(hasItem(10))));
    // assertThat(cbe, batch(0, outParam("foo", Types.INTEGER))));
    // assertThat(cbe, batch(0, outParam("foo", JDBCType.INTEGER))));
    // assertThat(cbe, batch(0, outParam(10, Types.INTEGER))));
    // assertThat(cbe, batch(0, outParam(10, JDBCType.INTEGER))));

    ////////////


    //  assertThat(pbe, batch(0, params(byName(hasEntry("bar", (Object) "BAR")));
    //  assertThat(pbe, batch(0, params(byValue(hasEntry(1, (Object) "BAR")));

    //  assertThat(pbe, batch(0, params(paramNames(hasItem("foo"))));
    //  assertThat(pbe, batch(0, params(paramIndexes(hasItem(1))));

    //  assertThat(pbe, batch(0, params(byName("foo", is((Object) 100))));
    //  assertThat(pbe, batch(0, params(byIndex(1, is((Object) 100))));

    //  assertThat(pbe, batch(0, params(param("foo", is((Object) 100))));
    //  assertThat(pbe, batch(0, params(param("foo", Integer.class, is(100))));
    //  assertThat(pbe, batch(0, params(paramAsString("foo", is("foo"))));
    //  assertThat(pbe, batch(0, params(paramAsInteger("foo", is(100))));
    //  assertThat(pbe, batch(0, params(paramAsLong("foo", is(100L))));
    //  assertThat(pbe, batch(0, params(param(1, is((Object) 100))));
    //  assertThat(pbe, batch(0, params(param(1, Integer.class, is(100))));
    //  assertThat(pbe, batch(0, params(paramAsString(1, is("foo"))));
    //  assertThat(pbe, batch(0, params(paramAsInteger(1, is(100))));
    //  assertThat(pbe, batch(0, params(paramAsLong(1, is(100L))));



    //  assertThat(cbe, batch(0, outParams(String.class, hasItems("foo")));

    // TODO: for out parameters

    public static Matcher<? super BatchParameterHolder> batchSize(final int batchSize) {
        return new FeatureMatcher<BatchParameterHolder, Integer>(equalTo(batchSize), "batchSize", "batchSize") {
            @Override
            protected Integer featureValueOf(BatchParameterHolder actual) {
                return actual.getBatchExecutionEntries().size();
            }
        };
    }


    public static Matcher<? super BatchParameterHolder> batch(final int index, final Matcher<? super ParameterHolder> parameterHolderMatcher) {
        return new CompositeMatcher<BatchParameterHolder, ParameterHolder>(parameterHolderMatcher) {
            @Override
            protected boolean validateByThisMatcher(BatchParameterHolder item, Description expected, Description actual) {
                List<BatchExecutionEntry> entries = item.getBatchExecutionEntries();
                int size = entries.size();
                if (size - 1 < index) {
                    expected.appendText("batch[" + index + "] exists");
                    actual.appendText("batch[] size was " + size);
                    return false;
                }

                BatchExecutionEntry entry = entries.get(index);
                if (!(entry instanceof ParameterHolder)) {  // TODO: unit test
                    expected.appendText("batch[" + index + "] an instance of " + ParameterHolder.class.getSimpleName());
                    actual.appendText("batch[" + index + "] is a " + item.getClass().getName());
                    return false;
                }

                return true;
            }

            @Override
            public ParameterHolder getValue(BatchParameterHolder actual) {
                BatchExecutionEntry entry = actual.getBatchExecutionEntries().get(index);
                return (ParameterHolder) entry;
            }

            @Override
            public String getSubMatcherFailureDescriptionPrefix() {
                return "batch[" + index + "] ";
            }
        };

    }


    // TODO: alias methods
    // preparedBatch(
    // batchParamKeys  or preparedBatchParamKeys
}
