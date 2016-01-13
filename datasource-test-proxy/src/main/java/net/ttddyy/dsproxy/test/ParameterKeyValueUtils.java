package net.ttddyy.dsproxy.test;

import net.ttddyy.dsproxy.proxy.ParameterKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tadaya Tsuyukubo
 */
public class ParameterKeyValueUtils {

    public static ParameterKeyValue createSetParam(int index, Object value) {
        return new ParameterKeyValue(index, value, ParameterKeyValue.OperationType.SET_PARAM);
    }

    public static ParameterKeyValue createSetParam(String name, Object value) {
        return new ParameterKeyValue(name, value, ParameterKeyValue.OperationType.SET_PARAM);
    }

    public static ParameterKeyValue createSetParam(ParameterKey key, Object value) {
        return new ParameterKeyValue(key, value, ParameterKeyValue.OperationType.SET_PARAM);
    }

    public static ParameterKeyValue createSetNull(int index, Object value) {
        return new ParameterKeyValue(index, value, ParameterKeyValue.OperationType.SET_NULL);
    }

    public static ParameterKeyValue createSetNull(String name, Object value) {
        return new ParameterKeyValue(name, value, ParameterKeyValue.OperationType.SET_NULL);
    }

    public static ParameterKeyValue createSetNull(ParameterKey key, Object value) {
        return new ParameterKeyValue(key, value, ParameterKeyValue.OperationType.SET_NULL);
    }

    public static ParameterKeyValue createRegisterOut(int index, Object value) {
        return new ParameterKeyValue(index, value, ParameterKeyValue.OperationType.REGISTER_OUT);
    }

    public static ParameterKeyValue createRegisterOut(String name, Object value) {
        return new ParameterKeyValue(name, value, ParameterKeyValue.OperationType.REGISTER_OUT);
    }

    public static ParameterKeyValue createRegisterOut(ParameterKey key, Object value) {
        return new ParameterKeyValue(key, value, ParameterKeyValue.OperationType.REGISTER_OUT);
    }

    public static List<ParameterKeyValue> filterBy(List<ParameterKeyValue> parameters, ParameterKeyValue.OperationType... operationTypes) {
        List<ParameterKeyValue> result = new ArrayList<ParameterKeyValue>();
        for (ParameterKeyValue keyValue : parameters) {
            if (Arrays.asList(operationTypes).contains(keyValue.getType())) {
                result.add(keyValue);
            }
        }
        return result;
    }

    public static List<ParameterKey> toParamKeys(List<ParameterKeyValue> parameters) {
        List<ParameterKey> result = new ArrayList<ParameterKey>();
        for (ParameterKeyValue keyValue : parameters) {
            result.add(keyValue.getKey());
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T> Map<ParameterKey, T> toKeyValueMap(List<ParameterKeyValue> keyValues) {
        Map<ParameterKey, T> result = new LinkedHashMap<ParameterKey, T>();
        for (ParameterKeyValue keyValue : keyValues) {
            result.put(keyValue.getKey(), (T) keyValue.getValue());
        }
        return result;
    }

}
