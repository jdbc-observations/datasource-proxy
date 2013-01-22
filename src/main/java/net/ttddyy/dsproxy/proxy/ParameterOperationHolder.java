package net.ttddyy.dsproxy.proxy;

import java.lang.reflect.Method;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * TODO: rename
 *
 * @author Tadaya Tsuyukubo
 * @since 1.2
 */
public class ParameterOperationHolder {

    private SortedMap<Integer, ParameterSetOperation> paramsByIndex = new TreeMap<Integer, ParameterSetOperation>();
    private SortedMap<String, ParameterSetOperation> paramsByName = new TreeMap<String, ParameterSetOperation>();

    // clear, put, size
    public void put(Integer index, Method method, Object... args) {
        paramsByIndex.put(index, new ParameterSetOperation(method, args));
    }

    public void put(String name, Method method, Object... args) {
        paramsByName.put(name, new ParameterSetOperation(method, args));
    }

    public void clear(){
        paramsByIndex.clear();
        paramsByName.clear();
    }
    public int totalSize(){
        return paramsByIndex.size() + paramsByName.size();
    }

    public SortedMap<Integer, ParameterSetOperation> getParamsByIndex() {
        return paramsByIndex;
    }

    public SortedMap<String, ParameterSetOperation> getParamsByName() {
        return paramsByName;
    }
}
