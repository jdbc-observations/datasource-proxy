package net.ttddyy.dsproxy.test;

import net.ttddyy.dsproxy.proxy.ParameterKey;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

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

    public static SortedSet<ParameterKeyValue> filterBy(SortedSet<ParameterKeyValue> parameters, ParameterKeyValue.OperationType... operationTypes) {
        SortedSet<ParameterKeyValue> result = new TreeSet<ParameterKeyValue>();
        for (ParameterKeyValue keyValue : parameters) {
            if (Arrays.asList(operationTypes).contains(keyValue.getType())) {
                result.add(keyValue);
            }
        }
        return result;
    }

    public static SortedSet<ParameterKeyValue> filterByKeyType(SortedSet<ParameterKeyValue> parameters, ParameterKey.ParameterKeyType keyType) {
        SortedSet<ParameterKeyValue> result = new TreeSet<ParameterKeyValue>();
        for (ParameterKeyValue keyValue : parameters) {
            if (keyValue.getKey().getType() == keyType) {
                result.add(keyValue);
            }
        }
        return result;
    }

    public static SortedSet<ParameterKey> toParamKeys(SortedSet<ParameterKeyValue> parameters) {
        SortedSet<ParameterKey> result = new TreeSet<ParameterKey>();
        for (ParameterKeyValue keyValue : parameters) {
            result.add(keyValue.getKey());
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T> Map<ParameterKey, T> toKeyValueMap(SortedSet<ParameterKeyValue> keyValues) {
        Map<ParameterKey, T> result = new LinkedHashMap<ParameterKey, T>();
        for (ParameterKeyValue keyValue : keyValues) {
            result.put(keyValue.getKey(), (T) keyValue.getValue());
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T> Map<Integer, T> toKeyIndexMap(SortedSet<ParameterKeyValue> keyValues) {
        Map<Integer, T> result = new LinkedHashMap<Integer, T>();
        for (ParameterKeyValue keyValue : keyValues) {
            result.put(keyValue.getKey().getIndex(), (T)keyValue.getValue());
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T> Map<String, T> toKeyNameMap(SortedSet<ParameterKeyValue> keyValues) {
        Map<String, T> result = new LinkedHashMap<String, T>();
        for (ParameterKeyValue keyValue : keyValues) {
            result.put(keyValue.getKey().getName(), (T)keyValue.getValue());
        }
        return result;
    }

    public static Map<ParameterKey, ParameterKeyValue> toParamKeyMap(SortedSet<ParameterKeyValue> keyValues) {
        Map<ParameterKey, ParameterKeyValue> result = new LinkedHashMap<ParameterKey, ParameterKeyValue>();
        for (ParameterKeyValue keyValue : keyValues) {
            result.put(keyValue.getKey(), keyValue);
        }
        return result;
    }

}
