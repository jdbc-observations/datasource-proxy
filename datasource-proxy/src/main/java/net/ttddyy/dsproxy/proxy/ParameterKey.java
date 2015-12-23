package net.ttddyy.dsproxy.proxy;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class ParameterKey implements Comparable<ParameterKey> {

    public enum ParameterKeyType {
        BY_INDEX, BY_NAME
    }

    private int index;
    private String name;
    private ParameterKeyType type;

    public ParameterKey(int index) {
        this.index = index;
        this.type = ParameterKeyType.BY_INDEX;
    }

    public ParameterKey(String name) {
        this.name = name;
        this.type = ParameterKeyType.BY_NAME;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public ParameterKeyType getType() {
        return type;
    }

    public boolean isByIndex() {
        return this.type == ParameterKeyType.BY_INDEX;
    }

    public boolean isByName() {
        return this.type == ParameterKeyType.BY_NAME;
    }

    public String getKeyAsString() {
        return this.type == ParameterKeyType.BY_INDEX ? Integer.toString(this.index) : this.name;
    }

    @Override
    public int compareTo(ParameterKey other) {
        return this.getKeyAsString().compareTo(other.getKeyAsString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParameterKey that = (ParameterKey) o;

        if (index != that.index) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return type == that.type;

    }

    @Override
    public int hashCode() {
        int result = index;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ParameterKey[" +
                "type=" + type +
                ", index=" + index +
                ", name='" + name + '\'' +
                ']';
    }
}
