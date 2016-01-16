package net.ttddyy.dsproxy.test;

import org.junit.Test;

import java.util.SortedSet;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tadaya Tsuyukubo
 */
public class ParameterKeyValueTest {

    @Test
    public void compareToIndex() {
        SortedSet<ParameterKeyValue> set = new TreeSet<ParameterKeyValue>();
        set.add(new ParameterKeyValue(1, 10, ParameterKeyValue.OperationType.SET_PARAM));
        set.add(new ParameterKeyValue(3, 10, ParameterKeyValue.OperationType.SET_PARAM));
        set.add(new ParameterKeyValue(2, 10, ParameterKeyValue.OperationType.SET_PARAM));

        assertThat(set).hasSize(3).extracting("key.index").containsExactly(1, 2, 3);
    }

    @Test
    public void compareToSameIndex() {
        SortedSet<ParameterKeyValue> set = new TreeSet<ParameterKeyValue>();
        set.add(new ParameterKeyValue(1, 10, ParameterKeyValue.OperationType.SET_PARAM));
        set.add(new ParameterKeyValue(1, 30, ParameterKeyValue.OperationType.SET_PARAM));
        set.add(new ParameterKeyValue(1, 20, ParameterKeyValue.OperationType.SET_PARAM));

        assertThat(set).hasSize(3).extracting("key.index").containsExactly(1, 1, 1);

        // value will be insertion order
        assertThat(set).extracting("value").containsExactly(10, 30, 20);
    }

    @Test
    public void compareToSameIndexDifferentValueType() {
        SortedSet<ParameterKeyValue> set = new TreeSet<ParameterKeyValue>();
        set.add(new ParameterKeyValue(1, 10, ParameterKeyValue.OperationType.SET_PARAM));
        set.add(new ParameterKeyValue(1, "30", ParameterKeyValue.OperationType.SET_PARAM));
        set.add(new ParameterKeyValue(1, 20, ParameterKeyValue.OperationType.SET_PARAM));

        assertThat(set).hasSize(3).extracting("key.index").containsExactly(1, 1, 1);

        // value will be insertion order
        assertThat(set).extracting("value").containsExactly(10, "30", 20);
    }

    @Test
    public void compareToDifferentValue() {
        SortedSet<ParameterKeyValue> set = new TreeSet<ParameterKeyValue>();
        set.add(new ParameterKeyValue(1, null, ParameterKeyValue.OperationType.SET_PARAM));
        set.add(new ParameterKeyValue(1, null, ParameterKeyValue.OperationType.REGISTER_OUT));
        set.add(new ParameterKeyValue(1, null, ParameterKeyValue.OperationType.SET_PARAM));  // same key, value again

        // regardless operation type, it only checks key & value
        assertThat(set).hasSize(1).extracting("key.index").containsExactly(1);
    }

    @Test
    public void compareToDifferentKeyTypeWithInt() {
        SortedSet<ParameterKeyValue> set = new TreeSet<ParameterKeyValue>();
        set.add(new ParameterKeyValue(1, 10, ParameterKeyValue.OperationType.SET_PARAM));
        set.add(new ParameterKeyValue("10", 20, ParameterKeyValue.OperationType.SET_PARAM));
        set.add(new ParameterKeyValue(1, 20, ParameterKeyValue.OperationType.SET_PARAM));

        // regardless operation type, it only checks key & value
        assertThat(set).hasSize(3).extracting("key.keyAsString").containsExactly("1", "1", "10");
    }

    @Test
    public void compareToNumberKeyInString() {
        SortedSet<ParameterKeyValue> set = new TreeSet<ParameterKeyValue>();
        set.add(new ParameterKeyValue(1, 10, ParameterKeyValue.OperationType.SET_PARAM));
        set.add(new ParameterKeyValue("1", 20, ParameterKeyValue.OperationType.SET_PARAM));

        assertThat(set).hasSize(2).extracting("key.keyAsString").containsExactly("1", "1");
        assertThat(set).extracting("value").containsExactly(10, 20);
    }

    @Test
    public void compareToDifferentKeyType() {
        SortedSet<ParameterKeyValue> set = new TreeSet<ParameterKeyValue>();
        set.add(new ParameterKeyValue(1, 10, ParameterKeyValue.OperationType.SET_PARAM));
        set.add(new ParameterKeyValue("foo", 200, ParameterKeyValue.OperationType.SET_PARAM));
        set.add(new ParameterKeyValue(1, 20, ParameterKeyValue.OperationType.SET_PARAM));
        set.add(new ParameterKeyValue("bar", 300, ParameterKeyValue.OperationType.SET_PARAM));

        // regardless operation type, it only checks key & value
        assertThat(set).hasSize(4).extracting("key.keyAsString").containsExactly("1", "1", "bar", "foo");
        assertThat(set).extracting("value").containsExactly(10, 20, 300, 200);
    }
}
