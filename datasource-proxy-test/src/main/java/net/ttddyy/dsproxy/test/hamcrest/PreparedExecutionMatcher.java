package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.PreparedExecution;
import org.hamcrest.Matcher;

import static net.ttddyy.dsproxy.test.hamcrest.QueryHolderAssertions.query;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class PreparedExecutionMatcher {


    // TODO: alias methods
    // preparedParams
    // preparedParamKeys
    // preparedParamValue

    public static Matcher<? super PreparedExecution> preparedQuery(Matcher<String> stringMatcher) {
        return query(stringMatcher);
    }


    // paramValue(String key, matcher
    // paramValue(String key, Class, matcher
    // paramValues(matcher)
    // paramValuesAs(Class, matcher)


}
