package net.ttddyy.dsproxy.proxy;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class ParameterKeyUtils {
    public static <T> Map<ParameterKey, T> filterBy(Map<ParameterKey, T> map, ParameterKey.ParameterKeyType type) {
        Map<ParameterKey, T> result = new LinkedHashMap<ParameterKey, T>();
        for (Map.Entry<ParameterKey, T> entry : map.entrySet()) {
            ParameterKey key = entry.getKey();
            if (key.getType() == type) {
                result.put(key, entry.getValue());
            }
        }
        return result;
    }

    public static <T> Map<String, T> toNameMap(Map<ParameterKey, T> map) {
        Map<ParameterKey, T> filtered = filterBy(map, ParameterKey.ParameterKeyType.BY_NAME);
        Map<String, T> result = new LinkedHashMap<String, T>();
        for (Map.Entry<ParameterKey, T> entry : filtered.entrySet()) {
            result.put(entry.getKey().getName(), entry.getValue());
        }
        return result;
    }

    public static <T> Map<Integer, T> toIndexMap(Map<ParameterKey, T> map) {
        Map<ParameterKey, T> filtered = filterBy(map, ParameterKey.ParameterKeyType.BY_INDEX);
        Map<Integer, T> result = new LinkedHashMap<Integer, T>();
        for (Map.Entry<ParameterKey, T> entry : filtered.entrySet()) {
            result.put(entry.getKey().getIndex(), entry.getValue());
        }
        return result;
    }

}
