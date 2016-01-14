package net.ttddyy.dsproxy.test;

import net.ttddyy.dsproxy.proxy.ParameterKey;

/**
 * @author Tadaya Tsuyukubo
 */
public class ParameterKeyValue implements Comparable<ParameterKeyValue> {

    public enum OperationType {
        SET_PARAM, SET_NULL, REGISTER_OUT
    }

    private ParameterKey key;
    private Object value;
    private OperationType type;

    public ParameterKeyValue(int indexKey, Object value, OperationType type) {
        this(new ParameterKey(indexKey), value, type);
    }

    public ParameterKeyValue(String nameKey, Object value, OperationType type) {
        this(new ParameterKey(nameKey), value, type);
    }

    public ParameterKeyValue(ParameterKey key, Object value, OperationType type) {
        this.key = key;
        this.value = value;
        this.type = type;
    }

    public boolean isSetParam() {
        return this.type == OperationType.SET_PARAM;
    }

    public boolean isSetNull() {
        return this.type == OperationType.SET_NULL;
    }

    public boolean isRegisterOut() {
        return this.type == OperationType.REGISTER_OUT;
    }

    public ParameterKey getKey() {
        return key;
    }

    public void setKey(ParameterKey key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public OperationType getType() {
        return type;
    }

    public void setType(OperationType type) {
        this.type = type;
    }

    @Override
    public int compareTo(ParameterKeyValue o) {
        int byKey = this.key.compareTo(o.key);  //use key for ordering
        if (byKey != 0) {
            return byKey;
        }
        return this.value == o.value ? 0 : 1;
    }

}
