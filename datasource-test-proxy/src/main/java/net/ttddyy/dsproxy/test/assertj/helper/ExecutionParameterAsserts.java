package net.ttddyy.dsproxy.test.assertj.helper;

import net.ttddyy.dsproxy.listener.RegisterOutParameterValueConverter;
import net.ttddyy.dsproxy.listener.SetNullParameterValueConverter;
import net.ttddyy.dsproxy.proxy.ParameterKey;
import net.ttddyy.dsproxy.test.OutParameterHolder;
import net.ttddyy.dsproxy.test.ParameterHolder;
import net.ttddyy.dsproxy.test.ParameterKeyValue;
import net.ttddyy.dsproxy.test.assertj.data.ExecutionParameter;
import net.ttddyy.dsproxy.test.assertj.data.ExecutionParameters;
import org.assertj.core.api.WritableAssertionInfo;

import java.sql.SQLType;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.filterBy;
import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.toKeyValueMap;
import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.toParamKeys;

/**
 * Reusable assertions for <code>{@link net.ttddyy.dsproxy.test.assertj.data.ExecutionParameter}</code>s.
 *
 * TODO: clean up
 *
 * @author Tadaya Tsuyukubo
 */
// TODO: better name
public class ExecutionParameterAsserts extends AbstractHelperAsserts {


    private SetNullParameterValueConverter setNullValueConverter = new SetNullParameterValueConverter();
    private RegisterOutParameterValueConverter registerOutParameterValueConverter = new RegisterOutParameterValueConverter();


    public ExecutionParameterAsserts(WritableAssertionInfo info) {
        super(info);
    }


    private SortedSet<ParameterKey> getExpectedParamExecutionKeys(ExecutionParameters params, Class<?>... classes) {
        SortedSet<ParameterKey> keys = new TreeSet<ParameterKey>();
        for (ExecutionParameter param : params.getParameters()) {
            for (Class<?> clazz : classes) {
                if (clazz.isInstance(param)) {
                    keys.add(param.getKey());
                }
            }
        }
        return keys;
    }


    /**
     * Validate all keys(index and name) exists regardless of the types(set-params, set-null, or register-out).
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

        SortedSet<ParameterKey> actualAllKeys = toParamKeys(parameterHolder.getAllParameters());

        // first check any param key only items
        SortedSet<ParameterKey> expectedParamKeyOnlyKeys = getExpectedParamExecutionKeys(params, ExecutionParameter.ParamKeyOnlyExecution.class);
        if (!expectedParamKeyOnlyKeys.isEmpty()) {
            this.assertParameterKeysOnly(actualAllKeys, expectedParamKeyOnlyKeys, isCallable);
//            return;
        }

        SortedSet<ParameterKeyValue> actualParamValues = parameterHolder.getAllParameters();
        SortedSet<ParameterKey> actualSetParamKeys = toParamKeys(filterBy(actualParamValues, ParameterKeyValue.OperationType.SET_PARAM));
        SortedSet<ParameterKey> actualSetNullParamKeys = toParamKeys(filterBy(actualParamValues, ParameterKeyValue.OperationType.SET_NULL));
        SortedSet<ParameterKey> actualOutParamKeys = toParamKeys(filterBy(actualParamValues, ParameterKeyValue.OperationType.REGISTER_OUT));

        SortedSet<ParameterKey> expectedSetParamKeys = getExpectedParamExecutionKeys(params, ExecutionParameter.ParamExecution.class);
        SortedSet<ParameterKey> expectedSetNullParamKeys = getExpectedParamExecutionKeys(params, ExecutionParameter.SetNullParamExecution.class);
        SortedSet<ParameterKey> expectedOutParamKeys = getExpectedParamExecutionKeys(params, ExecutionParameter.RegisterOutParamExecutionWithIntType.class, ExecutionParameter.RegisterOutParamExecutionWithSQLType.class);

        SortedSet<ParameterKey> expectedAllKeys = new TreeSet<ParameterKey>();
        expectedAllKeys.addAll(expectedSetParamKeys);
        expectedAllKeys.addAll(expectedSetNullParamKeys);
        expectedAllKeys.addAll(expectedOutParamKeys);

        SortedSet<ParameterKey> missingSetParamKeys = getParameterKeysDiff(expectedSetParamKeys, actualSetParamKeys);
        SortedSet<ParameterKey> missingSetNullParamKeys = getParameterKeysDiff(expectedSetNullParamKeys, actualSetNullParamKeys);
        SortedSet<ParameterKey> missingOutParamKeys = getParameterKeysDiff(expectedOutParamKeys, actualOutParamKeys);


        boolean hasMissingSetParamKeys = !missingSetParamKeys.isEmpty();
        boolean hasMissingSetNullKeys = !missingSetNullParamKeys.isEmpty();
        boolean hasMissingOutKeys = !missingOutParamKeys.isEmpty();

        boolean hasMissingKeys = hasMissingSetParamKeys || hasMissingSetNullKeys || hasMissingOutKeys;

        // validate keys
        if (hasMissingKeys) {

            String actualKeys = getParameterKeysAsString(actualAllKeys);

            StringBuilder actualKeysDetail = new StringBuilder();
            actualKeysDetail.append("params=");
            actualKeysDetail.append(getParameterKeysAsString(toParamKeys(filterBy(actualParamValues, ParameterKeyValue.OperationType.SET_PARAM))));
            actualKeysDetail.append(", set-null=");
            actualKeysDetail.append(getParameterKeysAsString(toParamKeys(filterBy(actualParamValues, ParameterKeyValue.OperationType.SET_NULL))));
            actualKeysDetail.append(", register-out=");
            actualKeysDetail.append(getParameterKeysAsString(toParamKeys(filterBy(actualParamValues, ParameterKeyValue.OperationType.REGISTER_OUT))));

            String expectedKeys = getKeysAsString(hasMissingSetParamKeys, hasMissingSetNullKeys,
                    hasMissingOutKeys, expectedSetParamKeys, expectedSetNullParamKeys, expectedOutParamKeys);

            String notFoundKeys = getKeysAsString(hasMissingSetParamKeys, hasMissingSetNullKeys,
                    hasMissingOutKeys, missingSetParamKeys, missingSetNullParamKeys, missingOutParamKeys);

            failWithMessage("%nExpecting: %s parameter keys%n<%s>%n(%s)%nto contain:%n<%s>%nbut could not find:%n<%s>",
                    isCallable ? "callable" : "prepared", actualKeys, actualKeysDetail, expectedKeys, notFoundKeys);
        }

        // in addition, validate exactly same or not
        if (ExecutionParameters.ExecutionParametersType.CONTAINS_EXACTLY == params.getType()) {
            boolean isParamsSameSize = actualSetParamKeys.size() == expectedSetParamKeys.size();
            boolean isSetNullParamsSameSize = actualSetNullParamKeys.size() == expectedSetNullParamKeys.size();
            boolean isOutParamsSameSize = actualOutParamKeys.size() == expectedOutParamKeys.size();
            boolean isSameSize = isParamsSameSize && isSetNullParamsSameSize && isOutParamsSameSize;

            if (!isSameSize) {
                SortedSet<ParameterKey> extraSetParamKeys = getParameterKeysDiff(actualSetParamKeys, expectedSetParamKeys);
                SortedSet<ParameterKey> extraSetNullParamKeys = getParameterKeysDiff(actualSetNullParamKeys, expectedSetNullParamKeys);
                SortedSet<ParameterKey> extraOutParamKeys = getParameterKeysDiff(actualOutParamKeys, expectedOutParamKeys);

                String actualKeys = getParameterKeysAsString(actualAllKeys);
                String expectedKeys = getParameterKeysAsString(expectedAllKeys);

                StringBuilder actualKeysDetail = new StringBuilder();
                actualKeysDetail.append("params=");
                actualKeysDetail.append(getParameterKeysAsString(actualSetParamKeys));
                actualKeysDetail.append(", set-null=");
                actualKeysDetail.append(getParameterKeysAsString(actualSetNullParamKeys));
                actualKeysDetail.append(", register-out=");
                actualKeysDetail.append(getParameterKeysAsString(actualOutParamKeys));

                StringBuilder expectedKeysDetail = new StringBuilder();
                expectedKeysDetail.append("params=");
                expectedKeysDetail.append(getParameterKeysAsString(expectedSetParamKeys));
                expectedKeysDetail.append(", set-null=");
                expectedKeysDetail.append(getParameterKeysAsString(expectedSetNullParamKeys));
                expectedKeysDetail.append(", register-out=");
                expectedKeysDetail.append(getParameterKeysAsString(expectedOutParamKeys));

                boolean hasExtraParamKeys = !extraSetParamKeys.isEmpty();
                boolean hasExtraSetNullKeys = !extraSetNullParamKeys.isEmpty();
                boolean hasExtraOutKeys = !extraOutParamKeys.isEmpty();


                String missingKeys = getKeysAsString(hasMissingSetParamKeys, hasMissingSetNullKeys,
                        hasMissingOutKeys, missingSetParamKeys, missingSetNullParamKeys, missingOutParamKeys);
                String extraKeys = getKeysAsString(hasExtraParamKeys, hasExtraSetNullKeys,
                        hasExtraOutKeys, extraSetParamKeys, extraSetNullParamKeys, extraOutParamKeys);


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
        ParameterKey expectedParamKey = param.getKey();

        if (param instanceof ExecutionParameter.ParamExecution) {
            Object expectedValue = ((ExecutionParameter.ParamExecution) param).getValue();
            validateParameter(expectedParamKey, expectedValue, paramHolder);

        } else if (param instanceof ExecutionParameter.SetNullParamExecution) {
            Integer sqlType = ((ExecutionParameter.SetNullParamExecution) param).getSqlType();
            validateSetNullParameter(expectedParamKey, sqlType, paramHolder);

        } else if (param instanceof ExecutionParameter.RegisterOutParamExecutionWithIntType && paramHolder instanceof OutParameterHolder) {
            int sqlType = ((ExecutionParameter.RegisterOutParamExecutionWithIntType) param).getSqlType();
            validateOutParamParameterWithInt(expectedParamKey, sqlType, (OutParameterHolder) paramHolder);

        } else if (param instanceof ExecutionParameter.RegisterOutParamExecutionWithSQLType && paramHolder instanceof OutParameterHolder) {
            SQLType sqlType = ((ExecutionParameter.RegisterOutParamExecutionWithSQLType) param).getSqlType();
            validateOutParamParameterWithSQLType(expectedParamKey, sqlType, (OutParameterHolder) paramHolder);

        } else {
            // TODO: better error message
            throw new IllegalStateException("wrong parameter type: " + param.getClass().getSimpleName());
        }

    }

    public <T extends ParameterHolder> void validateParameter(ParameterKey expectedParamKey, Object expectedValue, T executionEntry) {

        Object actualValue = toKeyValueMap(executionEntry.getSetParams()).get(expectedParamKey);
        if (expectedValue != actualValue) {
            SortedMap<String, Object> sortedParams = getAllParamsForDisplay(executionEntry);
            String expectedEntry = String.format("%s=%s", expectedParamKey.getKeyAsString(), expectedValue);
            failWithMessage("%nExpecting: parameters %n<%s>%nto contain:%n<[%s]>%nbut could not find:%n<[%s]>", sortedParams, expectedEntry, expectedEntry);
        }
    }

    public <T extends ParameterHolder> void validateSetNullParameter(ParameterKey expectedParamKey, Integer sqlType, T executionEntry) {
        if (sqlType == null) {
            return;  // don't check if sqlType is null
        }

        Object actualValue = toKeyValueMap(executionEntry.getSetNullParams()).get(expectedParamKey);
        if (sqlType != actualValue) {
            SortedMap<String, Object> sortedParams = getAllParamsForDisplay(executionEntry);
            String displayValue = setNullValueConverter.getDisplayValue(sqlType);
            String expectedEntry = String.format("%s=%s", expectedParamKey.getKeyAsString(), displayValue);
            failWithMessage("%nExpecting: parameters %n<%s>%nto contain:%n<[%s]>%nbut could not find:%n<[%s]>", sortedParams, expectedEntry, expectedEntry);
        }
    }

    // TODO: consider both int and SQLType to be inter-changeable. (probably some value object to work for both)
    private void validateOutParamParameterWithInt(ParameterKey expectedParamKey, int sqlType, OutParameterHolder executionEntry) {
        Object actualValue = toKeyValueMap(executionEntry.getOutParams()).get(expectedParamKey);
        if (!new Integer(sqlType).equals(actualValue)) {
            SortedMap<String, Object> sortedParams = getAllParamsForDisplay(executionEntry);
            String displayValue = registerOutParameterValueConverter.getDisplayValue(sqlType);
            String expectedEntry = String.format("%s=%s", expectedParamKey.getKeyAsString(), displayValue);
            failWithMessage("%nExpecting: parameters %n<%s>%nto contain:%n<[%s]>%nbut could not find:%n<[%s]>", sortedParams, expectedEntry, expectedEntry);
        }
    }

    private void validateOutParamParameterWithSQLType(ParameterKey expectedParamKey, SQLType sqlType, OutParameterHolder executionEntry) {
        Object actualValue = toKeyValueMap(executionEntry.getOutParams()).get(expectedParamKey);
        if (sqlType != actualValue) {
            SortedMap<String, Object> sortedParams = getAllParamsForDisplay(executionEntry);
            String displayValue = registerOutParameterValueConverter.getDisplayValue(sqlType);
            String expectedEntry = String.format("%s=%s", expectedParamKey.getKeyAsString(), displayValue);
            failWithMessage("%nExpecting: parameters %n<%s>%nto contain:%n<[%s]>%nbut could not find:%n<[%s]>", sortedParams, expectedEntry, expectedEntry);
        }
    }

    private <T extends ParameterHolder> SortedMap<String, Object> getAllParamsForDisplay(T entry) {
        SortedMap<String, Object> sorted = new TreeMap<String, Object>();
        for (ParameterKeyValue keyValue : entry.getAllParameters()) {
            sorted.put(keyValue.getKey().getKeyAsString(), keyValue.getDisplayValue());  // display {key=val, key2=val2}
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
