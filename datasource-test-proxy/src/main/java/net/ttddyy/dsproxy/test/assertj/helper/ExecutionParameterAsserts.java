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

import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.toKeyValueMap;
import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.toParamKeys;

/**
 * Reusable assertions for <code>{@link net.ttddyy.dsproxy.test.assertj.data.ExecutionParameter}</code>s.
 *
 * TODO: clean up
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

    private SortedSet<ParameterKey> getParamExecutionKeys(ExecutionParameters params) {
        SortedSet<ParameterKey> keys = new TreeSet<ParameterKey>();
        for (ExecutionParameter param : params.getParameters()) {
            if (param instanceof ExecutionParameter.ParamExecution) {
                keys.add(param.getKey());
            }
        }
        return keys;
    }

    private SortedSet<ParameterKey> getSetNullParamExecutionKeys(ExecutionParameters params) {
        SortedSet<ParameterKey> keys = new TreeSet<ParameterKey>();
        for (ExecutionParameter param : params.getParameters()) {
            if (param instanceof ExecutionParameter.SetNullParamExecution) {
                keys.add(param.getKey());
            }
        }
        return keys;
    }

    private SortedSet<ParameterKey> getOutParamExecutionKeys(ExecutionParameters params) {
        SortedSet<ParameterKey> keys = new TreeSet<ParameterKey>();
        for (ExecutionParameter param : params.getParameters()) {
            if (param instanceof ExecutionParameter.RegisterOutParamExecutionWithIntType ||
                    param instanceof ExecutionParameter.RegisterOutParamExecutionWithSQLType) {
                keys.add(param.getKey());
            }
        }
        return keys;
    }

    private SortedSet<ParameterKey> getParamKeyOnlyExecutionKeys(ExecutionParameters params) {
        SortedSet<ParameterKey> keys = new TreeSet<ParameterKey>();
        for (ExecutionParameter param : params.getParameters()) {
            if (param instanceof ExecutionParameter.ParamKeyOnlyExecution) {
                keys.add(param.getKey());
            }
        }
        return keys;
    }

    /**
     * Validate all keys(index and name) exists regardless of the types(params, set-null, or register-out).
     *
     * @see ExecutionParameters#containsParamKeys(Object...)
     * @see ExecutionParameters#containsParamIndexes(int...)
     * @see ExecutionParameters#containsParamNames(String...)
     */
    private void assertParameterKeysOnly(SortedSet<ParameterKey> actualAllKeys, SortedSet<ParameterKey> expectedParamKeyOnlyKeys, boolean isCallable) {

        if (actualAllKeys.containsAll(expectedParamKeyOnlyKeys)) {
            return;  // valid
        }

        SortedSet<ParameterKey> notFoundParameterKeys = new TreeSet<ParameterKey>(expectedParamKeyOnlyKeys);
        notFoundParameterKeys.removeAll(actualAllKeys);

        String actualKeys = getParameterKeysAsString(actualAllKeys);
        String expectedKeys = getParameterKeysAsString(expectedParamKeyOnlyKeys);
        String notFoundKeys = getParameterKeysAsString(notFoundParameterKeys);

        failWithMessage("%nExpecting: %s parameter keys%n<%s>%nto contain:%n<%s>%nbut could not find:%n<%s>",
                isCallable ? "callable" : "prepared", actualKeys, expectedKeys, notFoundKeys);

        // TODO: need param details??
    }

    // left - right
    private SortedSet<ParameterKey> getParameterKeysDiff(Set<ParameterKey> left, Set<ParameterKey> right) {
        SortedSet<ParameterKey> result = new TreeSet<ParameterKey>(left);
        result.removeAll(right);
        return result;
    }

    public void assertParameterKeys(ParameterHolder parameterHolder, ExecutionParameters params, boolean isCallable) {

        // TODO: clean up
        SortedSet<ParameterKey> actualAllKeys = new TreeSet<ParameterKey>(parameterHolder.getAllParams().keySet());

        // first check any param key only items
        SortedSet<ParameterKey> expectedParamKeyOnlyKeys = getParamKeyOnlyExecutionKeys(params);
        if (!expectedParamKeyOnlyKeys.isEmpty()) {
            this.assertParameterKeysOnly(actualAllKeys, expectedParamKeyOnlyKeys, isCallable);
        }


        boolean isOutParamHolder = parameterHolder instanceof OutParameterHolder;


        SortedSet<ParameterKey> actualParamKeys = toParamKeys(parameterHolder.getSetParams());
        SortedSet<ParameterKey> actualSetNullParamKeys = toParamKeys(parameterHolder.getSetNullParams());

        SortedSet<ParameterKey> actualOutParamKeys = new TreeSet<ParameterKey>();
        if (isOutParamHolder) {
            actualOutParamKeys.addAll(((OutParameterHolder) parameterHolder).getOutParams().keySet());
        }


        SortedSet<ParameterKey> expectedParamKeys = getParamExecutionKeys(params);
        SortedSet<ParameterKey> expectedSetNullParamKeys = getSetNullParamExecutionKeys(params);
        SortedSet<ParameterKey> expectedOutParamKeys = getOutParamExecutionKeys(params);

        SortedSet<ParameterKey> expectedAllKeys = new TreeSet<ParameterKey>();
        expectedAllKeys.addAll(expectedParamKeys);
        expectedAllKeys.addAll(expectedSetNullParamKeys);
        expectedAllKeys.addAll(expectedOutParamKeys);

        SortedSet<ParameterKey> missingParamKeys = getParameterKeysDiff(expectedParamKeys, actualParamKeys);
        SortedSet<ParameterKey> missingSetNullParamKeys = getParameterKeysDiff(expectedSetNullParamKeys, actualSetNullParamKeys);
        SortedSet<ParameterKey> missingOutParamKeys = getParameterKeysDiff(expectedOutParamKeys, actualOutParamKeys);


        boolean hasMissingParamKeys = !missingParamKeys.isEmpty();
        boolean hasMissingSetNullKeys = !missingSetNullParamKeys.isEmpty();
        boolean hasMissingOutKeys = !missingOutParamKeys.isEmpty();

        boolean hasMissingKeys = hasMissingParamKeys || hasMissingSetNullKeys || hasMissingOutKeys;

        // validate keys
        if (hasMissingKeys) {

            String actualKeys = getParameterKeysAsString(actualAllKeys);

            StringBuilder actualKeysDetail = new StringBuilder();
            actualKeysDetail.append("params=");
            actualKeysDetail.append(getParameterKeysAsString(actualParamKeys));
            actualKeysDetail.append(", set-null=");
            actualKeysDetail.append(getParameterKeysAsString(actualSetNullParamKeys));
            actualKeysDetail.append(", register-out=");
            actualKeysDetail.append(getParameterKeysAsString(actualOutParamKeys));

            String expectedKeys = getKeysAsString(hasMissingParamKeys, hasMissingSetNullKeys,
                    hasMissingOutKeys, expectedParamKeys, expectedSetNullParamKeys, expectedOutParamKeys);

            String notFoundKeys = getKeysAsString(hasMissingParamKeys, hasMissingSetNullKeys,
                    hasMissingOutKeys, missingParamKeys, missingSetNullParamKeys, missingOutParamKeys);

            failWithMessage("%nExpecting: %s parameter keys%n<%s>%n(%s)%nto contain:%n<%s>%nbut could not find:%n<%s>",
                    isCallable ? "callable" : "prepared", actualKeys, actualKeysDetail, expectedKeys, notFoundKeys);
        }

        // in addition, validate exactly same or not
        if (ExecutionParameters.ExecutionParametersType.CONTAINS_EXACTLY == params.getType()) {
            boolean isParamsSameSize = actualParamKeys.size() == expectedParamKeys.size();
            boolean isSetNullParamsSameSize = actualSetNullParamKeys.size() == expectedSetNullParamKeys.size();
            boolean isOutParamsSameSize = actualOutParamKeys.size() == expectedOutParamKeys.size();
            boolean isSameSize = isParamsSameSize && isSetNullParamsSameSize && isOutParamsSameSize;

            if (!isSameSize) {
                SortedSet<ParameterKey> extraParamKeys = getParameterKeysDiff(actualParamKeys, expectedParamKeys);
                SortedSet<ParameterKey> extraSetNullParamKeys = getParameterKeysDiff(actualSetNullParamKeys, expectedSetNullParamKeys);
                SortedSet<ParameterKey> extraOutParamKeys = getParameterKeysDiff(actualOutParamKeys, expectedOutParamKeys);

                String actualKeys = getParameterKeysAsString(actualAllKeys);
                String expectedKeys = getParameterKeysAsString(expectedAllKeys);

                StringBuilder actualKeysDetail = new StringBuilder();
                actualKeysDetail.append("params=");
                actualKeysDetail.append(getParameterKeysAsString(actualParamKeys));
                actualKeysDetail.append(", set-null=");
                actualKeysDetail.append(getParameterKeysAsString(actualSetNullParamKeys));
                actualKeysDetail.append(", register-out=");
                actualKeysDetail.append(getParameterKeysAsString(actualOutParamKeys));

                StringBuilder expectedKeysDetail = new StringBuilder();
                expectedKeysDetail.append("params=");
                expectedKeysDetail.append(getParameterKeysAsString(expectedParamKeys));
                expectedKeysDetail.append(", set-null=");
                expectedKeysDetail.append(getParameterKeysAsString(expectedSetNullParamKeys));
                expectedKeysDetail.append(", register-out=");
                expectedKeysDetail.append(getParameterKeysAsString(expectedOutParamKeys));

                boolean hasExtraParamKeys = !extraParamKeys.isEmpty();
                boolean hasExtraSetNullKeys = !extraSetNullParamKeys.isEmpty();
                boolean hasExtraOutKeys = !extraOutParamKeys.isEmpty();


                String missingKeys = getKeysAsString(hasMissingParamKeys, hasMissingSetNullKeys,
                        hasMissingOutKeys, missingParamKeys, missingSetNullParamKeys, missingOutParamKeys);
                String extraKeys = getKeysAsString(hasExtraParamKeys, hasExtraSetNullKeys,
                        hasExtraOutKeys, extraParamKeys, extraSetNullParamKeys, extraOutParamKeys);


                failWithMessage("%nExpecting: %s parameter keys%n<%s>%n(%s)%nto be exactly:%n<%s>%n(%s)%nbut missing keys:%n<%s>%nextra keys:%n<%s>",
                        "callable", actualKeys, actualKeysDetail, expectedKeys, expectedKeysDetail, missingKeys, extraKeys);
            }
        }
    }


    private String getKeysAsString(boolean writeParamKeys, boolean writeSetNullKeys, boolean writeOutKeys,
                                   SortedSet<ParameterKey> paramKeys, SortedSet<ParameterKey> setNullKeys,
                                   SortedSet<ParameterKey> registerOutKeys) {
        StringBuilder sb = new StringBuilder();
        if (writeParamKeys) {
            sb.append("params=");
            sb.append(getParameterKeysAsString(paramKeys));
        }
        if (writeSetNullKeys) {
            if (sb.length() != 0) {
                sb.append(", ");
            }
            sb.append("set-null=");
            sb.append(getParameterKeysAsString(setNullKeys));
        }
        if (writeOutKeys) {
            if (sb.length() != 0) {
                sb.append(", ");
            }
            sb.append("register-out=");
            sb.append(getParameterKeysAsString(registerOutKeys));
        }

        return sb.toString();
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

        Object actualValue = toKeyValueMap(executionEntry.getSetParams()).get(parameterKey);
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

        Object actualValue = toKeyValueMap(executionEntry.getSetNullParams()).get(parameterKey);
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
        Set<ParameterKey> nullParamKeys = toParamKeys(entry.getSetNullParams());
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
