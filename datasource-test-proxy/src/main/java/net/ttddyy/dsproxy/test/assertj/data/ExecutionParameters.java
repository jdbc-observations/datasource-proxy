package net.ttddyy.dsproxy.test.assertj.data;

import net.ttddyy.dsproxy.proxy.ParameterKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class ExecutionParameters {

    public enum ExecutionParametersType {
        CONTAINS, CONTAINS_EXACTLY, CONTAINS_KEYS_ONLY;
    }

    public static ExecutionParameters containsParams(ExecutionParameter... params) {
        ExecutionParameters executionParameters = new ExecutionParameters();
        executionParameters.type = ExecutionParametersType.CONTAINS;
        executionParameters.getParameters().addAll(Arrays.asList(params));
        return executionParameters;
    }

    public static ExecutionParameters containsParamsExactly(ExecutionParameter... params) {
        ExecutionParameters executionParameters = new ExecutionParameters();
        executionParameters.type = ExecutionParametersType.CONTAINS_EXACTLY;
        executionParameters.getParameters().addAll(Arrays.asList(params));
        return executionParameters;

    }


    // TODO: differentiate with containsParams??
    public static ExecutionParameters containsOutParams(ExecutionParameter... params) {
        return null; // TODO: impl
    }

    // TODO: differentiate with containsParams??
    public static ExecutionParameters containsNullParams(ExecutionParameter... params) {
        return null;  // TODO: impl
    }

    //
    public static ExecutionParameters containsParamKeys(Object... paramKeys) {

        List<ExecutionParameter> params = new ArrayList<ExecutionParameter>();

        for (Object rawParamKey : paramKeys) {
            ExecutionParameter param;
            if (rawParamKey instanceof Integer) {
                param = new ExecutionParameter.ParamExecution(new ParameterKey((Integer) rawParamKey), null);
            } else if (rawParamKey instanceof String) {
                param = new ExecutionParameter.ParamExecution(new ParameterKey((String) rawParamKey), null);
            } else {
                throw new IllegalArgumentException("param key should be int or String");
            }
            params.add(param);
        }


        ExecutionParameters executionParameters = new ExecutionParameters();
        executionParameters.type = ExecutionParametersType.CONTAINS_KEYS_ONLY;
        executionParameters.getParameters().addAll(params);
        return executionParameters;
    }

    public static ExecutionParameters containsParamIndexes(int... paramIndexes) {

        List<ExecutionParameter> params = new ArrayList<ExecutionParameter>();
        for (int paramIndex : paramIndexes) {
            ExecutionParameter param = new ExecutionParameter.ParamExecution(new ParameterKey(paramIndex), null);
            params.add(param);
        }

        ExecutionParameters executionParameters = new ExecutionParameters();
        executionParameters.type = ExecutionParametersType.CONTAINS_KEYS_ONLY;
        executionParameters.getParameters().addAll(params);
        return executionParameters;
    }

    public static ExecutionParameters containsParamNames(String... paramNames) {

        List<ExecutionParameter> params = new ArrayList<ExecutionParameter>();
        for (String paramName : paramNames) {
            ExecutionParameter param = new ExecutionParameter.ParamExecution(new ParameterKey(paramName), null);
            params.add(param);
        }

        ExecutionParameters executionParameters = new ExecutionParameters();
        executionParameters.type = ExecutionParametersType.CONTAINS_KEYS_ONLY;
        executionParameters.getParameters().addAll(params);
        return executionParameters;

    }

    private List<ExecutionParameter> parameters = new ArrayList<ExecutionParameter>();
    private ExecutionParametersType type;


    public List<ExecutionParameter> getParameters() {
        return parameters;
    }

    public List<ParameterKey> getParameterKeys() {
        List<ParameterKey> keys = new ArrayList<ParameterKey>();
        for (ExecutionParameter parameter : this.parameters) {
            keys.add(parameter.getKey());
        }
        return keys;
    }

    public ExecutionParametersType getType() {
        return type;
    }
}
