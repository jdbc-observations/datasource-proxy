package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.listener.RegisterOutParameterValueConverter;
import net.ttddyy.dsproxy.listener.SetNullParameterValueConverter;
import net.ttddyy.dsproxy.proxy.ParameterKey;
import net.ttddyy.dsproxy.test.BatchExecutionEntry;
import net.ttddyy.dsproxy.test.CallableBatchExecution;
import net.ttddyy.dsproxy.test.ParameterHolder;
import net.ttddyy.dsproxy.test.assertj.data.ExecutionParameter;
import net.ttddyy.dsproxy.test.assertj.data.ExecutionParameters;

import java.sql.SQLType;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
// TODO: should this extend AbstractAssert??
public class CallableBatchExecutionAssert extends AbstractExecutionAssert<CallableBatchExecutionAssert, CallableBatchExecution> {

    private SetNullParameterValueConverter setNullValueConverter = new SetNullParameterValueConverter();
    private RegisterOutParameterValueConverter registerOutParameterValueConverter = new RegisterOutParameterValueConverter();


    public CallableBatchExecutionAssert(CallableBatchExecution actual) {
        super(actual, CallableBatchExecutionAssert.class);
    }

    public CallableBatchExecutionAssert isSuccess() {
        isExecutionSuccess();
        return this;
    }

    public CallableBatchExecutionAssert isFailure() {
        isExecutionFailure();
        return this;
    }

    public CallableBatchExecutionAssert hasBatchSize(int batchSize) {
        new BatchParameterHolderAssert(this.actual).hasBatchSize(batchSize, "callable");
        return this;
    }


    private void validateParameter(ParameterKey parameterKey, Object expectedValue, CallableBatchExecution.CallableBatchExecutionEntry executionEntry) {

        Object actualValue = executionEntry.getParams().get(parameterKey);
        if (expectedValue != actualValue) {
            SortedMap<String, Object> sortedParams = getAllParamsForDisplay(executionEntry);
            String expectedEntry = String.format("%s=%s", parameterKey.getKeyAsString(), expectedValue);
            failWithMessage("%nExpecting: parameters %n<%s>%nto contain:%n<[%s]>%nbut could not find:%n<[%s]>", sortedParams, expectedEntry, expectedEntry);
        }
    }

    private void validateSetNullParameter(ParameterKey parameterKey, Integer sqlType, CallableBatchExecution.CallableBatchExecutionEntry executionEntry) {
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

    // TODO: consider both int and SQLType to be inter-changeable. (consider changing display value)
    private void validateOutParamParameterWithInt(ParameterKey parameterKey, int sqlType, CallableBatchExecution.CallableBatchExecutionEntry executionEntry) {
        Object actualValue = executionEntry.getOutParams().get(parameterKey);
        if (!new Integer(sqlType).equals(actualValue)) {
            SortedMap<String, Object> sortedParams = getAllParamsForDisplay(executionEntry);
            String displayValue = registerOutParameterValueConverter.getDisplayValue(sqlType);
            String expectedEntry = String.format("%s=%s", parameterKey.getKeyAsString(), displayValue);
            failWithMessage("%nExpecting: parameters %n<%s>%nto contain:%n<[%s]>%nbut could not find:%n<[%s]>", sortedParams, expectedEntry, expectedEntry);
        }
    }

    private void validateOutParamParameterWithSQLType(ParameterKey parameterKey, SQLType sqlType, CallableBatchExecution.CallableBatchExecutionEntry executionEntry) {
        Object actualValue = executionEntry.getOutParams().get(parameterKey);
        if (sqlType != actualValue) {
            SortedMap<String, Object> sortedParams = getAllParamsForDisplay(executionEntry);
            String displayValue = registerOutParameterValueConverter.getDisplayValue(sqlType);
            String expectedEntry = String.format("%s=%s", parameterKey.getKeyAsString(), displayValue);
            failWithMessage("%nExpecting: parameters %n<%s>%nto contain:%n<[%s]>%nbut could not find:%n<[%s]>", sortedParams, expectedEntry, expectedEntry);
        }
    }


    private SortedMap<String, Object> getAllParamsForDisplay(CallableBatchExecution.CallableBatchExecutionEntry entry) {

        Set<ParameterKey> nullParamKeys = entry.getSetNullParams().keySet();
        Set<ParameterKey> outParamKeys = entry.getOutParams().keySet();

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


    private void validateBatchIndexSize(int batchIndex) {
        List<BatchExecutionEntry> batchEntries = this.actual.getBatchExecutionEntries();
        int batchSize = batchEntries.size();

        if (batchIndex < 0) {
            String message = String.format("\nExpecting: batch index <%d> should be greater than equal to <0>", batchIndex);
            failWithMessage(message);
        } else if (batchSize <= batchIndex) {
            String message = String.format("\nExpecting: batch index <%d> is too big for the batch size <%d>", batchIndex, batchSize);
            failWithMessage(message);
        }
    }

    private void validateBatchExecutionEntryType(BatchExecutionEntry batchEntry, Class<? extends BatchExecutionEntry> batchExecutionEntryClass) {
        if (!(batchEntry.getClass().isAssignableFrom(batchExecutionEntryClass))) {
            failWithMessage("\nExpecting: batch entry\n<%s>\nbut was\n<%s>",
                    batchExecutionEntryClass.getSimpleName(),
                    batchEntry.getClass().getSimpleName());
        }
    }

    private void validateParameterKeys(ParameterHolder entry, ExecutionParameters params) {
        ExecutionParameters.ExecutionParametersType parametersType = params.getType();
        SortedSet<ParameterKey> expectedParamKeys = new TreeSet<ParameterKey>(params.getParameterKeys());

        SortedSet<ParameterKey> actualParamKeys = new TreeSet<ParameterKey>(entry.getAllParams().keySet());

        // validate keys
        boolean containsAll = actualParamKeys.containsAll(expectedParamKeys);
        if (!containsAll) {

            SortedSet<ParameterKey> notFoundParameterKeys = new TreeSet<ParameterKey>(expectedParamKeys);
            notFoundParameterKeys.removeAll(actualParamKeys);

            String actualKeys = getParameterKeysAsString(actualParamKeys);
            String expectedKeys = getParameterKeysAsString(expectedParamKeys);
            String notFoundKeys = getParameterKeysAsString(notFoundParameterKeys);

            failWithMessage("%nExpecting: %s parameter keys%n<%s>%nto contain:%n<%s>%nbut could not find:%n<%s>",
                    "callable", actualKeys, expectedKeys, notFoundKeys);
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

    public CallableBatchExecutionAssert batch(int batchIndex, ExecutionParameters params) {

        // check index exists
        validateBatchIndexSize(batchIndex);

        BatchExecutionEntry batchEntry = this.actual.getBatchExecutionEntries().get(batchIndex);

        validateBatchExecutionEntryType(batchEntry, CallableBatchExecution.CallableBatchExecutionEntry.class);

        CallableBatchExecution.CallableBatchExecutionEntry entry = (CallableBatchExecution.CallableBatchExecutionEntry) batchEntry;
        validateParameterKeys(entry, params);

        if (ExecutionParameters.ExecutionParametersType.CONTAINS_KEYS_ONLY == params.getType()) {
            return this;  // only check keys
        }


        // validate key-value pairs
        for (ExecutionParameter param : params.getParameters()) {
            ParameterKey parameterKey = param.getKey();

            if (param instanceof ExecutionParameter.ParamExecution) {
                Object expectedValue = ((ExecutionParameter.ParamExecution) param).getValue();
                validateParameter(parameterKey, expectedValue, entry);

            } else if (param instanceof ExecutionParameter.SetNullParamExecution) {
                Integer sqlType = ((ExecutionParameter.SetNullParamExecution) param).getSqlType();
                validateSetNullParameter(parameterKey, sqlType, entry);

            } else if (param instanceof ExecutionParameter.RegisterOutParamExecutionWithIntType) {
                int sqlType = ((ExecutionParameter.RegisterOutParamExecutionWithIntType) param).getSqlType();
                validateOutParamParameterWithInt(parameterKey, sqlType, entry);

            } else if (param instanceof ExecutionParameter.RegisterOutParamExecutionWithSQLType) {
                SQLType sqlType = ((ExecutionParameter.RegisterOutParamExecutionWithSQLType) param).getSqlType();
                validateOutParamParameterWithSQLType(parameterKey, sqlType, entry);

            } else {
                // TODO: better error message
                throw new IllegalStateException("wrong parameter type: " + param.getClass().getSimpleName());
            }
        }

        return this;
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

    public CallableBatchEntryAssert batch(int batchIndex) {
        BatchExecutionEntry batchExecutionEntry = this.actual.getBatchExecutionEntries().get(batchIndex);
        return new CallableBatchEntryAssert(batchExecutionEntry);
    }


}
