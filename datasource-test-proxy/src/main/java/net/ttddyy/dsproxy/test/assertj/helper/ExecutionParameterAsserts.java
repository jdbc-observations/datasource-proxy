package net.ttddyy.dsproxy.test.assertj.helper;

import net.ttddyy.dsproxy.listener.RegisterOutParameterValueConverter;
import net.ttddyy.dsproxy.listener.SetNullParameterValueConverter;
import net.ttddyy.dsproxy.proxy.ParameterKey;
import net.ttddyy.dsproxy.test.OutParameterHolder;
import net.ttddyy.dsproxy.test.ParameterHolder;
import net.ttddyy.dsproxy.test.assertj.data.ExecutionParameter;
import net.ttddyy.dsproxy.test.assertj.data.ExecutionParameters;
import org.assertj.core.api.WritableAssertionInfo;

import java.sql.SQLType;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Reusable assertions for <code>{@link net.ttddyy.dsproxy.test.assertj.data.ExecutionParameter}</code>s.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
// TODO: better name
public class ExecutionParameterAsserts extends AbstractHelperAsserts {


    private SetNullParameterValueConverter setNullValueConverter = new SetNullParameterValueConverter();
    private RegisterOutParameterValueConverter registerOutParameterValueConverter = new RegisterOutParameterValueConverter();


    public ExecutionParameterAsserts(WritableAssertionInfo info) {
        super(info);
    }

    public void assertParameterKeys(ParameterHolder parameterHolder, ExecutionParameters params, boolean isCallable) {
        ExecutionParameters.ExecutionParametersType parametersType = params.getType();
        SortedSet<ParameterKey> expectedParamKeys = new TreeSet<ParameterKey>(params.getParameterKeys());

        SortedSet<ParameterKey> actualParamKeys = new TreeSet<ParameterKey>(parameterHolder.getAllParams().keySet());

        // validate keys
        boolean containsAll = actualParamKeys.containsAll(expectedParamKeys);
        if (!containsAll) {

            SortedSet<ParameterKey> notFoundParameterKeys = new TreeSet<ParameterKey>(expectedParamKeys);
            notFoundParameterKeys.removeAll(actualParamKeys);

            String actualKeys = getParameterKeysAsString(actualParamKeys);
            String expectedKeys = getParameterKeysAsString(expectedParamKeys);
            String notFoundKeys = getParameterKeysAsString(notFoundParameterKeys);

            failWithMessage("%nExpecting: %s parameter keys%n<%s>%nto contain:%n<%s>%nbut could not find:%n<%s>",
                    isCallable ? "callable" : "prepared", actualKeys, expectedKeys, notFoundKeys);
        }

        if (ExecutionParameters.ExecutionParametersType.CONTAINS_EXACTLY == parametersType) {
            boolean isSameSize = actualParamKeys.size() == expectedParamKeys.size();
            if (!isSameSize) {
                SortedSet<ParameterKey> missingParameterKeys = new TreeSet<ParameterKey>(expectedParamKeys);
                missingParameterKeys.removeAll(actualParamKeys);

                SortedSet<ParameterKey> extraParameterKeys = new TreeSet<ParameterKey>(actualParamKeys);
                extraParameterKeys.removeAll(expectedParamKeys);

                String actualKeys = getParameterKeysAsString(actualParamKeys);
                String expectedKeys = getParameterKeysAsString(expectedParamKeys);
                String missingKeys = getParameterKeysAsString(missingParameterKeys);
                String extraKeys = getParameterKeysAsString(extraParameterKeys);

                failWithMessage("%nExpecting: %s parameter keys%n<%s>%nto be exactly:%n<%s>%nbut missing keys:%n<%s>%nextra keys:%n<%s>",
                        "callable", actualKeys, expectedKeys, missingKeys, extraKeys);
            }
        }
    }


    public <T extends ParameterHolder> void assertExecutionParameters(T paramHolder, ExecutionParameters params) {
        // validate key-value pairs
        for (ExecutionParameter param : params.getParameters()) {
            assertExecutionParameter(paramHolder, param);
        }

    }

    public <T extends ParameterHolder> void assertExecutionParameter(T paramHolder, ExecutionParameter param) {
        ParameterKey parameterKey = param.getKey();

        if (param instanceof ExecutionParameter.ParamExecution) {
            Object expectedValue = ((ExecutionParameter.ParamExecution) param).getValue();
            validateParameter(parameterKey, expectedValue, paramHolder);

        } else if (param instanceof ExecutionParameter.SetNullParamExecution) {
            Integer sqlType = ((ExecutionParameter.SetNullParamExecution) param).getSqlType();
            validateSetNullParameter(parameterKey, sqlType, paramHolder);

        } else if (param instanceof ExecutionParameter.RegisterOutParamExecutionWithIntType && paramHolder instanceof OutParameterHolder) {
            int sqlType = ((ExecutionParameter.RegisterOutParamExecutionWithIntType) param).getSqlType();
            validateOutParamParameterWithInt(parameterKey, sqlType, (OutParameterHolder) paramHolder);

        } else if (param instanceof ExecutionParameter.RegisterOutParamExecutionWithSQLType && paramHolder instanceof OutParameterHolder) {
            SQLType sqlType = ((ExecutionParameter.RegisterOutParamExecutionWithSQLType) param).getSqlType();
            validateOutParamParameterWithSQLType(parameterKey, sqlType, (OutParameterHolder) paramHolder);

        } else {
            // TODO: better error message
            throw new IllegalStateException("wrong parameter type: " + param.getClass().getSimpleName());
        }

    }

    public <T extends ParameterHolder> void validateParameter(ParameterKey parameterKey, Object expectedValue, T executionEntry) {

        Object actualValue = executionEntry.getParams().get(parameterKey);
        if (expectedValue != actualValue) {
            SortedMap<String, Object> sortedParams = getAllParamsForDisplay(executionEntry);
            String expectedEntry = String.format("%s=%s", parameterKey.getKeyAsString(), expectedValue);
            failWithMessage("%nExpecting: parameters %n<%s>%nto contain:%n<[%s]>%nbut could not find:%n<[%s]>", sortedParams, expectedEntry, expectedEntry);
        }
    }

    public <T extends ParameterHolder> void validateSetNullParameter(ParameterKey parameterKey, Integer sqlType, T executionEntry) {
        if (sqlType == null) {
            return;  // don't check if sqlType is null
        }

        Object actualValue = executionEntry.getSetNullParams().get(parameterKey);
        if (sqlType != actualValue) {
            SortedMap<String, Object> sortedParams = getAllParamsForDisplay(executionEntry);
            String displayValue = setNullValueConverter.getDisplayValue(sqlType);
            String expectedEntry = String.format("%s=%s", parameterKey.getKeyAsString(), displayValue);
            failWithMessage("%nExpecting: parameters %n<%s>%nto contain:%n<[%s]>%nbut could not find:%n<[%s]>", sortedParams, expectedEntry, expectedEntry);
        }
    }

    // TODO: consider both int and SQLType to be inter-changeable. (probably some value object to work for both)
    private void validateOutParamParameterWithInt(ParameterKey parameterKey, int sqlType, OutParameterHolder executionEntry) {
        Object actualValue = executionEntry.getOutParams().get(parameterKey);
        if (!new Integer(sqlType).equals(actualValue)) {
            SortedMap<String, Object> sortedParams = getAllParamsForDisplay(executionEntry);
            String displayValue = registerOutParameterValueConverter.getDisplayValue(sqlType);
            String expectedEntry = String.format("%s=%s", parameterKey.getKeyAsString(), displayValue);
            failWithMessage("%nExpecting: parameters %n<%s>%nto contain:%n<[%s]>%nbut could not find:%n<[%s]>", sortedParams, expectedEntry, expectedEntry);
        }
    }

    private void validateOutParamParameterWithSQLType(ParameterKey parameterKey, SQLType sqlType, OutParameterHolder executionEntry) {
        Object actualValue = executionEntry.getOutParams().get(parameterKey);
        if (sqlType != actualValue) {
            SortedMap<String, Object> sortedParams = getAllParamsForDisplay(executionEntry);
            String displayValue = registerOutParameterValueConverter.getDisplayValue(sqlType);
            String expectedEntry = String.format("%s=%s", parameterKey.getKeyAsString(), displayValue);
            failWithMessage("%nExpecting: parameters %n<%s>%nto contain:%n<[%s]>%nbut could not find:%n<[%s]>", sortedParams, expectedEntry, expectedEntry);
        }
    }

    private <T extends ParameterHolder> SortedMap<String, Object> getAllParamsForDisplay(T entry) {

        // TODO: make it work for both OutParameterHolder and ParameterHolder
        Set<ParameterKey> nullParamKeys = entry.getSetNullParams().keySet();
        Set<ParameterKey> outParamKeys = new HashSet<ParameterKey>();
        if (entry instanceof OutParameterHolder) {
            outParamKeys.addAll(((OutParameterHolder) entry).getOutParams().keySet());
        }


        SortedMap<String, Object> sorted = new TreeMap<String, Object>();
        for (Map.Entry<ParameterKey, Object> parameterEntry : entry.getAllParams().entrySet()) {
            ParameterKey key = parameterEntry.getKey();
            String valueToDisplay;
            if (nullParamKeys.contains(key)) {
                valueToDisplay = this.setNullValueConverter.getDisplayValue((Integer) parameterEntry.getValue());
            } else if (outParamKeys.contains(key)) {
                valueToDisplay = this.registerOutParameterValueConverter.getDisplayValue(parameterEntry.getValue());
            } else {
                valueToDisplay = String.valueOf(parameterEntry.getValue());
            }
            sorted.put(key.getKeyAsString(), valueToDisplay);  // display {key=val, key2=val2}
        }

        return sorted;
    }

    private String getParameterKeysAsString(Set<ParameterKey> keys) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (ParameterKey key : keys) {
            sb.append(key.getKeyAsString());
            sb.append(", ");
        }
        if (!keys.isEmpty()) {
            sb.delete(sb.length() - 2, sb.length());
        }
        sb.append(']');
        return sb.toString();
    }

}
