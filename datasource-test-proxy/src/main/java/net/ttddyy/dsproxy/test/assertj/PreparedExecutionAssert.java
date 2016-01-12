package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.listener.SetNullParameterValueConverter;
import net.ttddyy.dsproxy.proxy.ParameterKey;
import net.ttddyy.dsproxy.test.PreparedExecution;
import net.ttddyy.dsproxy.test.assertj.data.ExecutionParameter;
import net.ttddyy.dsproxy.test.assertj.data.ExecutionParameters;
import net.ttddyy.dsproxy.test.assertj.helper.ExecutionParameterAsserts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class PreparedExecutionAssert extends AbstractExecutionAssert<PreparedExecutionAssert, PreparedExecution> {

    private SetNullParameterValueConverter setNullValueConverter = new SetNullParameterValueConverter();
    private ExecutionParameterAsserts parameterAsserts = new ExecutionParameterAsserts(this.info);

    public PreparedExecutionAssert(PreparedExecution actual) {
        super(actual, PreparedExecutionAssert.class);
    }

    public PreparedExecutionAssert isSuccess() {
        isExecutionSuccess();
        return this;
    }

    public PreparedExecutionAssert isFailure() {
        isExecutionFailure();
        return this;
    }


    public PreparedExecutionAssert containsParam(int paramIndex, Object value) {
        containsParams(ExecutionParameter.param(paramIndex, value));
        return this;
    }


    public PreparedExecutionAssert containsNullParam(int index, int sqlType) {
        containsParams(ExecutionParameter.nullParam(index, sqlType));
        return this;
    }

    public PreparedExecutionAssert containsNullParam(int index) {
        containsParams(ExecutionParameter.nullParam(index));
        return this;
    }


    public PreparedExecutionAssert containsParams(ExecutionParameter... params) {
        ExecutionParameters executionParameters = ExecutionParameters.containsParams(params);
        parameterAsserts.assertParameterKeys(this.actual, executionParameters, false);
        parameterAsserts.assertExecutionParameters(this.actual, executionParameters);
        return this;
    }

    public PreparedExecutionAssert containsParamIndex(int paramIndex) {
        containsParamIndexes(paramIndex);
        return this;
    }

    public PreparedExecutionAssert containsParamIndexes(int... paramIndexes) {
        ExecutionParameters executionParameters = ExecutionParameters.containsParamIndexes(paramIndexes);
        parameterAsserts.assertParameterKeys(this.actual, executionParameters, false);
        return this;
    }

    /**
     * @param paramValues Expecting parameter values. setNull is represented as {@code null}.
     */
    public PreparedExecutionAssert containsParamValuesExactly(Object... paramValues) {
        // TODO: cleanup

        // actual param keys are index numbers
        SortedSet<ParameterKey> keys = new TreeSet<ParameterKey>();
        keys.addAll(this.actual.getParams().keySet());
        keys.addAll(this.actual.getSetNullParams().keySet());

        // construct map for expected
        Map<ParameterKey, Object> expectedParamAndValue = new LinkedHashMap<ParameterKey, Object>();
        for (int i = 0; i < paramValues.length; i++) {
            expectedParamAndValue.put(new ParameterKey(i + 1), paramValues[i]);
        }

        Map<ParameterKey, Object> actualParamAndValue = this.actual.getAllParams();
        List<Object> actualValues = new ArrayList<Object>();
        for (ParameterKey key : keys) {
            Object value;
            if (actual.getSetNullParams().containsKey(key)) {
                value = null;  // for setNull, use as null
            } else {
                value = actualParamAndValue.get(key);
            }
            actualValues.add(value);
        }

        // size check
        if (keys.size() != paramValues.length) {

            String failureMessage = getFailureMessageForValuesExactly(keys, actualParamAndValue, expectedParamAndValue);
            failWithMessage(failureMessage);
        }


        // value check
        for (int i = 0; i < keys.size(); i++) {
            Object actualValue = actualValues.get(i);
            Object expectedValue = paramValues[i];

            if ((actualValue == expectedValue) || (actualValue != null && actualValue.equals(expectedValue))) {
                continue;
            }

            String failureMessage = getFailureMessageForValuesExactly(keys, actualParamAndValue, expectedParamAndValue);
            failWithMessage(failureMessage);
        }

        return this;
    }

    private String getFailureMessageForValuesExactly(SortedSet<ParameterKey> keys, Map<ParameterKey, Object> actualParamValues, Map<ParameterKey, Object> expectedParamValues) {
        Set<Integer> actualSetNullParamIndexes = new HashSet<Integer>();
        actualSetNullParamIndexes.addAll(this.actual.getSetNullParamsByIndex().keySet());

        // getting actual values to display
        Map<Integer, Object> actualValueMapToDisplay = new LinkedHashMap<Integer, Object>();
        for (ParameterKey key : keys) {
            Object value;
            if (actual.getSetNullParams().containsKey(key)) {
                Integer sqlType = actual.getSetNullParams().get(key);
                value = setNullValueConverter.getDisplayValue(sqlType);
            } else {
                value = actualParamValues.get(key);
            }
            actualValueMapToDisplay.put(key.getIndex(), value);
        }

        // getting expected values for display
        Map<Integer, Object> expectedValueMapToDisplay = new LinkedHashMap<Integer, Object>();
        for (Map.Entry<ParameterKey, Object> entry : expectedParamValues.entrySet()) {
            expectedValueMapToDisplay.put(entry.getKey().getIndex(), entry.getValue() == null ? "NULL" : entry.getValue());
        }


        String missingMessage = getMissingKeyValueMessage(expectedParamValues, this.actual.getAllParams(), actualSetNullParamIndexes);
        String extraMessage = getExtraKeyValueMessage(expectedParamValues, this.actual.getAllParams(), actualSetNullParamIndexes);

        return String.format("%nExpecting: prepared parameter values%n<%s>%nto be exactly:%n<%s>%nbut missing:%n<%s>%nextra:%n<%s>",
                actualValueMapToDisplay, expectedValueMapToDisplay, missingMessage, extraMessage);

    }


    private String getMissingKeyValueMessage(Map<ParameterKey, Object> expected, Map<ParameterKey, Object> actual, Set<Integer> actualSetNullParamKeys) {
        Map<Integer, Object> missing = new LinkedHashMap<Integer, Object>();

        for (Map.Entry<ParameterKey, Object> entry : expected.entrySet()) {
            int index = entry.getKey().getIndex();
            Object expectedValue = entry.getValue();
            Object actualValue = actual.get(new ParameterKey(index));
            if (expectedValue == null) {
                if (!actualSetNullParamKeys.contains(index)) {
                    // expected null, but actual is not null
                    missing.put(index, "NULL");
                }
            } else {
                if (!expectedValue.equals(actualValue)) {
                    missing.put(index, expectedValue);  // expected has value but doesn't match
                }
            }
        }

        return missing.toString();
    }


    private String getExtraKeyValueMessage(Map<ParameterKey, Object> expected, Map<ParameterKey, Object> actual, Set<Integer> actualSetNullParamKeys) {

        Map<Integer, Object> extra = new LinkedHashMap<Integer, Object>();
        for (Map.Entry<ParameterKey, Object> entry : actual.entrySet()) {
            ParameterKey actualKey = entry.getKey();

            if (!expected.containsKey(actualKey)) {  // map may contains value=null, thus cannot compare the result of get with null
                // expected doesn't have the index.
                Object value = actualSetNullParamKeys.contains(actualKey.getIndex()) ? setNullValueConverter.getDisplayValue((Integer) entry.getValue()) : entry.getValue();
                extra.put(actualKey.getIndex(), value);
            }
        }
        return extra.toString();
    }

}
