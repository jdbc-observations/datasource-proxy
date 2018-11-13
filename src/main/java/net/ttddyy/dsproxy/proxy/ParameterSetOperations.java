package net.ttddyy.dsproxy.proxy;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents set of {@link ParameterSetOperation} performed on current {@link java.sql.Statement}.
 *
 * @author Tadaya Tsuyukubo
 * @since 2.0
 */
public class ParameterSetOperations {

    /**
     * Internal representation of parameter set operations.
     *
     * When same key(index/name) is used for parameter set operation, old value will be replaced.
     * To implement the override, use a map; so that, putting same key will override the entry.
     * Also, keeps the order of parameter set operations.
     */
    private Map<ParameterKey, ParameterSetOperation> parameterSetOperations = new LinkedHashMap<>();

    /**
     * Return copy of the list of {@link ParameterSetOperation}.
     *
     * To add new {@link ParameterSetOperation}, need to call {@link #add(ParameterSetOperation)}.
     *
     * @return copy of parameter set operations list
     */
    public List<ParameterSetOperation> getOperations() {
        return new ArrayList<>(this.parameterSetOperations.values());
    }

    public void add(ParameterSetOperation parameterSetOperation) {
        this.parameterSetOperations.put(parameterSetOperation.getParameterKey(), parameterSetOperation);
    }

    public void clear() {
        this.parameterSetOperations.clear();
    }

}
