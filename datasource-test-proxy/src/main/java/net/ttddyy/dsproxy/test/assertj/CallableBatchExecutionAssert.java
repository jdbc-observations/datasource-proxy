package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.listener.RegisterOutParameterValueConverter;
import net.ttddyy.dsproxy.listener.SetNullParameterValueConverter;
import net.ttddyy.dsproxy.test.BatchExecutionEntry;
import net.ttddyy.dsproxy.test.CallableBatchExecution;
import net.ttddyy.dsproxy.test.assertj.data.ExecutionParameter;
import net.ttddyy.dsproxy.test.assertj.data.ExecutionParameters;

import java.sql.SQLType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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


    private void validateParameterByIndex(int index, Object expected, Map<Integer, Object> params) {

//        validateIndexParamKeys(params.keySet(), index, "parameter");

        Object actualValue = params.get(index);
        if (expected != actualValue) {
            TreeMap<Integer, Object> sortedParams = new TreeMap<Integer, Object>(params);  // display {key=val, key2=val2}
            String expectedEntry = String.format("index=%d, value=%s", index, expected);
            failWithMessage("%nExpecting: parameters %n<%s>%nto contain:%n<[%s]>%nbut could not find:%n<[%s]>", sortedParams, expectedEntry, expectedEntry);
        }
    }

    private void validateParameterByName(String name, Object expected, Map<String, Object> params) {

//        validateNameParamKeys(params.keySet(), name, "parameter");

        Object actualValue = params.get(name);
        if (expected != actualValue) {
            TreeMap<String, Object> sortedParams = new TreeMap<String, Object>(params);

            String expectedEntry = String.format("name=%s, value=%s", name, expected);
            failWithMessage("%nExpecting: parameters %n<%s>%nto contain:%n<[%s]>%nbut could not find:%n<[%s]>", sortedParams, expectedEntry, expectedEntry);
        }
    }

    private void validateSetNullParameterByIndex(int index, Integer sqlType, Map<Integer, Integer> setNullParams) {

        validateIndexParamKeys(setNullParams.keySet(), index, "setNull parameter");

        if (sqlType == null) {
            return;  // don't check if sqlType is null
        }

        Object actualValue = setNullParams.get(index);
        if (sqlType != actualValue) {
            TreeMap<Integer, String> sortedParams = new TreeMap<Integer, String>();
            for (Map.Entry<Integer, Integer> entry : setNullParams.entrySet()) {
                sortedParams.put(entry.getKey(), setNullValueConverter.getDisplayValue(entry.getValue()));
            }

            String displayValue = setNullValueConverter.getDisplayValue(sqlType);
            String expectedEntry = String.format("index=%d, value=%s", index, displayValue);
            failWithMessage("%nExpecting: setNull parameters %n<%s>%nto contain:%n<[%s]>%nbut could not find:%n<[%s]>", sortedParams, expectedEntry, expectedEntry);
        }
    }

    private void validateSetNullParameterByName(String name, Integer sqlType, Map<String, Integer> setNullParams) {

        validateNameParamKeys(setNullParams.keySet(), name, "setNull parameter");

        if (sqlType == null) {
            return;  // don't check if sqlType is null
        }

        Object actualValue = setNullParams.get(name);
        if (sqlType != actualValue) {
            TreeMap<String, String> sortedParams = new TreeMap<String, String>();
            for (Map.Entry<String, Integer> entry : setNullParams.entrySet()) {
                sortedParams.put(entry.getKey(), setNullValueConverter.getDisplayValue(entry.getValue()));
            }

            String displayValue = setNullValueConverter.getDisplayValue(sqlType);
            String expectedEntry = String.format("name=%s, value=%s", name, displayValue);
            failWithMessage("%nExpecting: setNull parameters %n<%s>%nto contain:%n<[%s]>%nbut could not find:%n<[%s]>", sortedParams, expectedEntry, expectedEntry);
        }
    }


    private void validateOutParamParameterByIndexWithInt(int index, int sqlType, Map<Integer, Object> outParams) {

        validateIndexParamKeys(outParams.keySet(), index, "registerOutParameter parameter");

        Object actualValue = outParams.get(index);
        if (!new Integer(sqlType).equals(actualValue)) {
            TreeMap<Integer, Object> sortedParams = new TreeMap<Integer, Object>();
            for (Map.Entry<Integer, Object> entry : outParams.entrySet()) {
                sortedParams.put(entry.getKey(), registerOutParameterValueConverter.getDisplayValue(entry.getValue()));
            }

            String displayValue = registerOutParameterValueConverter.getDisplayValue(sqlType);
            String expectedEntry = String.format("index=%d, value=%s", index, displayValue);
            failWithMessage("%nExpecting: registerOut parameters %n<%s>%nto contain:%n<[%s]>%nbut could not find:%n<[%s]>", sortedParams, expectedEntry, expectedEntry);
        }
    }

    private void validateOutParamParameterByIndexWithSQLType(int index, SQLType sqlType, Map<Integer, Object> outParams) {

        validateIndexParamKeys(outParams.keySet(), index, "registerOutParameter parameter");

        Object actualValue = outParams.get(index);
        if (sqlType != actualValue) {
            TreeMap<Integer, Object> sortedParams = new TreeMap<Integer, Object>();
            for (Map.Entry<Integer, Object> entry : outParams.entrySet()) {
                sortedParams.put(entry.getKey(), registerOutParameterValueConverter.getDisplayValue(entry.getValue()));
            }

            String displayValue = registerOutParameterValueConverter.getDisplayValue(sqlType);
            String expectedEntry = String.format("index=%d, value=%s", index, displayValue);
            failWithMessage("%nExpecting: registerOut parameters %n<%s>%nto contain:%n<[%s]>%nbut could not find:%n<[%s]>", sortedParams, expectedEntry, expectedEntry);
        }
    }

    private void validateOutParamParameterByNameWithInt(String name, Integer sqlType, Map<String, Object> outParams) {

        validateNameParamKeys(outParams.keySet(), name, "registerOutParameter parameter");

        Object actualValue = outParams.get(name);
        if (sqlType != actualValue) {
            TreeMap<String, Object> sortedParams = new TreeMap<String, Object>();
            for (Map.Entry<String, Object> entry : outParams.entrySet()) {
                sortedParams.put(entry.getKey(), registerOutParameterValueConverter.getDisplayValue(entry.getValue()));
            }

            String displayValue = registerOutParameterValueConverter.getDisplayValue(sqlType);
            String expectedEntry = String.format("name=%s, value=%s", name, displayValue);
            failWithMessage("%nExpecting: registerOut parameters %n<%s>%nto contain:%n<[%s]>%nbut could not find:%n<[%s]>", sortedParams, expectedEntry, expectedEntry);
        }
    }

    private void validateOutParamParameterByNameWithSQLType(String name, SQLType sqlType, Map<String, Object> outParams) {

        validateNameParamKeys(outParams.keySet(), name, "registerOutParameter parameter");

        Object actualValue = outParams.get(name);
        if (sqlType != actualValue) {
            TreeMap<String, Object> sortedParams = new TreeMap<String, Object>();
            for (Map.Entry<String, Object> entry : outParams.entrySet()) {
                sortedParams.put(entry.getKey(), registerOutParameterValueConverter.getDisplayValue(entry.getValue()));
            }

            String displayValue = registerOutParameterValueConverter.getDisplayValue(sqlType);
            String expectedEntry = String.format("name=%s, value=%s", name, displayValue);
            failWithMessage("%nExpecting: registerOut parameters %n<%s>%nto contain:%n<[%s]>%nbut could not find:%n<[%s]>", sortedParams, expectedEntry, expectedEntry);
        }
    }

    private void validateIndexParamKeys(Set<Integer> indexes, int index, String paramType) {
        if (!indexes.contains(index)) {
            List<Integer> sortedIndexes = new ArrayList<Integer>(indexes);
            Collections.sort(sortedIndexes);  // display [1, 2, 3]
            failWithMessage("%nExpecting: %s indexes%n<%s>%nto contain:%n<%s>%nbut could not find:%n<%s>",
                    paramType, sortedIndexes, index, index);
        }
    }

    private void validateNameParamKeys(Set<String> names, String name, String paramType) {
        if (!names.contains(name)) {
            List<String> sortedParamNames = new ArrayList<String>(names);
            Collections.sort(sortedParamNames);
            failWithMessage("%nExpecting: %s names%n<%s>%nto contain:%n<%s>%nbut could not find:%n<%s>",
                    paramType, sortedParamNames, name, name);
        }
    }


    public CallableBatchExecutionAssert batch(int batchIndex, ExecutionParameters params) {

        // check index exists
        List<BatchExecutionEntry> batchEntries = this.actual.getBatchExecutionEntries();
        int batchSize = batchEntries.size();

        if (batchIndex < 0) {
            String message = String.format("\nExpecting: batch index <%d> should be greater than equal to <0>", batchIndex);
            failWithMessage(message);
        } else if (batchSize <= batchIndex) {
            String message = String.format("\nExpecting: batch index <%d> is too big for the batch size <%d>", batchIndex, batchSize);
            failWithMessage(message);
        }

        BatchExecutionEntry batchEntry = this.actual.getBatchExecutionEntries().get(batchIndex);
        if (!(batchEntry instanceof CallableBatchExecution.CallableBatchExecutionEntry)) {
            failWithMessage("\nExpecting: batch entry\n<%s>\nbut was\n<%s>",
                    CallableBatchExecution.CallableBatchExecutionEntry.class.getSimpleName(),
                    batchEntry.getClass().getSimpleName());
        }

        CallableBatchExecution.CallableBatchExecutionEntry entry = (CallableBatchExecution.CallableBatchExecutionEntry) this.actual.getBatchExecutionEntries().get(batchIndex);

        List<ExecutionParameter> executionParameters = params.getParameters();
        ExecutionParameters.ExecutionParametersType parametersType = params.getType();

        Set<Integer> expectedParamIndexes = new HashSet<Integer>();
        Set<String> expectedParamNames = new HashSet<String>();
        for (ExecutionParameter executionParameter : executionParameters) {
            if (executionParameter instanceof ExecutionParameter.ExecutionParameterByIndex) {
                int index = ((ExecutionParameter.ExecutionParameterByIndex) executionParameter).getParamIndex();
                expectedParamIndexes.add(index);
            } else if (executionParameter instanceof ExecutionParameter.ExecutionParameterByName) {
                String name = ((ExecutionParameter.ExecutionParameterByName) executionParameter).getParamName();
                expectedParamNames.add(name);
            }
        }

        // TODO: for setNull and registerOut params
        // validate keys
        List<Integer> actualParamIndexes = entry.getParamIndexes();
        List<String> actualParamNames = entry.getParamNames();
        boolean containsAll = actualParamIndexes.containsAll(expectedParamIndexes) && actualParamNames.containsAll(expectedParamNames);
        if (!containsAll) {
            List expectedAllParamKeys = new ArrayList();
            expectedAllParamKeys.addAll(expectedParamIndexes);
            expectedAllParamKeys.addAll(expectedParamNames);

            List actualAllParamKeys = new ArrayList();
            actualAllParamKeys.addAll(actualParamIndexes);
            actualAllParamKeys.addAll(actualParamNames);

            List notFoundKeys = new ArrayList(expectedAllParamKeys);
            notFoundKeys.removeAll(actualAllParamKeys);

            failWithMessage("%nExpecting: %s parameter keys%n<%s>%nto contain:%n<%s>%nbut could not find:%n<%s>",
                    "callable", actualAllParamKeys, expectedAllParamKeys, notFoundKeys);
        }

        if (ExecutionParameters.ExecutionParametersType.CONTAINS_EXACTLY == parametersType) {
            boolean isSameSize = actualParamIndexes.size() == expectedParamIndexes.size() && actualParamNames.size() == expectedParamNames.size();
            if (!isSameSize) {

                List expectedAllParamKeys = new ArrayList();
                expectedAllParamKeys.addAll(expectedParamIndexes);
                expectedAllParamKeys.addAll(expectedParamNames);

                List actualAllParamKeys = new ArrayList();
                actualAllParamKeys.addAll(actualParamIndexes);
                actualAllParamKeys.addAll(actualParamNames);

                List missingKeys = new ArrayList(expectedAllParamKeys);
                missingKeys.removeAll(actualAllParamKeys);

                List extraKeys = new ArrayList(actualAllParamKeys);
                extraKeys.removeAll(expectedAllParamKeys);

                // missing keys
                // extra keys
                failWithMessage("%nExpecting: %s parameter keys%n<%s>%nto be exactly:%n<%s>%nbut missing keys:%n<%s>%nextra keys:%n<%s>",
                        "callable", actualAllParamKeys, expectedAllParamKeys, missingKeys, extraKeys);
            }
        }

        if (ExecutionParameters.ExecutionParametersType.CONTAINS_KEYS_ONLY == parametersType) {
            return this;  // only check keys
        }


        // validate key-value pairs

        for (ExecutionParameter param : executionParameters) {

            if (param instanceof ExecutionParameter.ParamExecutionByIndex) {
                int index = ((ExecutionParameter.ParamExecutionByIndex) param).getParamIndex();
                Object expectedValue = ((ExecutionParameter.ParamExecutionByIndex) param).getValue();
                Map<Integer, Object> map = entry.getParamsByIndex();

                validateParameterByIndex(index, expectedValue, map);

            } else if (param instanceof ExecutionParameter.ParamExecutionByName) {
                String name = ((ExecutionParameter.ParamExecutionByName) param).getParamName();
                Object expectedValue = ((ExecutionParameter.ParamExecutionByName) param).getValue();
                Map<String, Object> map = entry.getParamsByName();

                validateParameterByName(name, expectedValue, map);

            } else if (param instanceof ExecutionParameter.NullParamExecutionByIndex) {
                int index = ((ExecutionParameter.NullParamExecutionByIndex) param).getParamIndex();
                Integer sqlType = ((ExecutionParameter.NullParamExecutionByIndex) param).getSqlType();
                Map<Integer, Integer> setNullParamsByIndex = entry.getSetNullParamsByIndex();

                validateSetNullParameterByIndex(index, sqlType, setNullParamsByIndex);

            } else if (param instanceof ExecutionParameter.NullParamExecutionByName) {
                String name = ((ExecutionParameter.NullParamExecutionByName) param).getParamName();
                Integer sqlType = ((ExecutionParameter.NullParamExecutionByName) param).getSqlType();
                Map<String, Integer> setNullParamsByName = entry.getSetNullParamsByName();

                validateSetNullParameterByName(name, sqlType, setNullParamsByName);

            } else if (param instanceof ExecutionParameter.OutParamExecutionByIndexWithIntType) {
                int index = ((ExecutionParameter.OutParamExecutionByIndexWithIntType) param).getParamIndex();
                int sqlType = ((ExecutionParameter.OutParamExecutionByIndexWithIntType) param).getSqlType();
                Map<Integer, Object> outParamsByIndex = entry.getOutParamsByIndex();


                validateOutParamParameterByIndexWithInt(index, sqlType, outParamsByIndex);

            } else if (param instanceof ExecutionParameter.OutParamExecutionByIndexWithSQLType) {
                int index = ((ExecutionParameter.OutParamExecutionByIndexWithSQLType) param).getParamIndex();
                SQLType sqlType = ((ExecutionParameter.OutParamExecutionByIndexWithSQLType) param).getSqlType();
                Map<Integer, Object> outParamsByIndex = entry.getOutParamsByIndex();


                validateOutParamParameterByIndexWithSQLType(index, sqlType, outParamsByIndex);


            } else if (param instanceof ExecutionParameter.OutParamExecutionByNameWithIntType) {
                String name = ((ExecutionParameter.OutParamExecutionByNameWithIntType) param).getParamName();
                int sqlType = ((ExecutionParameter.OutParamExecutionByNameWithIntType) param).getSqlType();
                Map<String, Object> outParamsByName = entry.getOutParamsByName();

                validateOutParamParameterByNameWithInt(name, sqlType, outParamsByName);

            } else if (param instanceof ExecutionParameter.OutParamExecutionByNameWithSQLType) {
                String name = ((ExecutionParameter.OutParamExecutionByNameWithSQLType) param).getParamName();
                SQLType sqlType = ((ExecutionParameter.OutParamExecutionByNameWithSQLType) param).getSqlType();
                Map<String, Object> outParamsByName = entry.getOutParamsByName();

                validateOutParamParameterByNameWithSQLType(name, sqlType, outParamsByName);

            } else {
                // TODO: better error message
                throw new IllegalStateException("wrong parameter type");
            }
        }


        return this;
    }

    public CallableBatchEntryAssert batch(int batchIndex) {
        BatchExecutionEntry batchExecutionEntry = this.actual.getBatchExecutionEntries().get(batchIndex);
        return new CallableBatchEntryAssert(batchExecutionEntry);
    }


}
