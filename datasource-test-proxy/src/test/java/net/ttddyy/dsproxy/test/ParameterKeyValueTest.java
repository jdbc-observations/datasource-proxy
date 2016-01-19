package net.ttddyy.dsproxy.test;

import org.junit.Test;

import java.util.SortedSet;
import java.util.TreeSet;

import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.createRegisterOut;
import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.createSetParam;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tadaya Tsuyukubo
 */
public class ParameterKeyValueTest {

    @Test
    public void compareToIndex() {
        SortedSet<ParameterKeyValue> set = new TreeSet<ParameterKeyValue>();
        set.add(createSetParam(1, 10));
        set.add(createSetParam(3, 10));
        set.add(createSetParam(2, 10));

        assertThat(set).hasSize(3).extracting("key.index").containsExactly(1, 2, 3);
    }

    @Test
    public void compareToSameIndex() {
        SortedSet<ParameterKeyValue> set = new TreeSet<ParameterKeyValue>();
        set.add(createSetParam(1, 10));
        set.add(createSetParam(1, 30));
        set.add(createSetParam(1, 20));

        assertThat(set).hasSize(3).extracting("key.index").containsExactly(1, 1, 1);

        // value will be insertion order
        assertThat(set).extracting("value").containsExactly(10, 30, 20);
    }

    @Test
    public void compareToSameIndexDifferentValueType() {
        SortedSet<ParameterKeyValue> set = new TreeSet<ParameterKeyValue>();
        set.add(createSetParam(1, 10));
        set.add(createSetParam(1, "30"));
        set.add(createSetParam(1, 20));

        assertThat(set).hasSize(3).extracting("key.index").containsExactly(1, 1, 1);

        // value will be insertion order
        assertThat(set).extracting("value").containsExactly(10, "30", 20);
    }

    @Test
    public void compareToDifferentValue() {
        SortedSet<ParameterKeyValue> set = new TreeSet<ParameterKeyValue>();
        set.add(createSetParam(1, null));
        set.add(createRegisterOut(1, null));
        set.add(createSetParam(1, null));  // same key, value again

        // regardless operation type, it only checks key & value
        assertThat(set).hasSize(1).extracting("key.index").containsExactly(1);
    }

    @Test
    public void compareToDifferentKeyTypeWithInt() {
        SortedSet<ParameterKeyValue> set = new TreeSet<ParameterKeyValue>();
        set.add(createSetParam(1, 10));
        set.add(createSetParam("10", 20));
        set.add(createSetParam(1, 20));

        // regardless operation type, it only checks key & value
        assertThat(set).hasSize(3).extracting("key.keyAsString").containsExactly("1", "1", "10");
    }

    @Test
    public void compareToNumberKeyInString() {
        SortedSet<ParameterKeyValue> set = new TreeSet<ParameterKeyValue>();
        set.add(createSetParam(1, 10));
        set.add(createSetParam("1", 20));

        assertThat(set).hasSize(2).extracting("key.keyAsString").containsExactly("1", "1");
        assertThat(set).extracting("value").containsExactly(10, 20);
    }

    @Test
    public void compareToDifferentKeyType() {
        SortedSet<ParameterKeyValue> set = new TreeSet<ParameterKeyValue>();
        set.add(createSetParam(1, 10));
        set.add(createSetParam("foo", 200));
        set.add(createSetParam(1, 20));
        set.add(createSetParam("bar", 300));

        // regardless operation type, it only checks key & value
        assertThat(set).hasSize(4).extracting("key.keyAsString").containsExactly("1", "1", "bar", "foo");
        assertThat(set).extracting("value").containsExactly(10, 20, 300, 200);
    }
}
