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
        // TODO: for null

        if (this.isByIndex()) {
            if (other.isByIndex()) {
                return (this.index == other.index) ? 0 : (this.index < other.index ? -1 : 1);
            } else {
                if (isIntString(other.name)) {
                    return compareInt(this.index, Integer.parseInt(other.name));
                } else {
                    return -1;  // this(number) first
                }
            }
        } else {
            if (other.isByIndex()) {
                if (isIntString(this.name)) {
                    return compareInt(Integer.parseInt(this.name), other.index);
                } else {
                    return 1;  // other(number) first
                }
            } else {
                boolean thisIsIntString = isIntString(this.name);
                boolean otherIsIntString = isIntString(other.name);
                boolean bothAreIntString = thisIsIntString && otherIsIntString;

                if (bothAreIntString) {
                    return compareInt(Integer.parseInt(this.name), Integer.parseInt(other.name));
                } else if (thisIsIntString || otherIsIntString) {
                    return thisIsIntString ? -1 : 1;  // number first
                } else {
                    return this.name.compareTo(other.name);
                }

            }

        }
    }

    private boolean isIntString(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private int compareInt(int left, int right) {
        return (left < right) ? -1 : ((left == right) ? 0 : 1);
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
