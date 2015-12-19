package net.ttddyy.dsproxy.proxy;

import org.junit.Test;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class ParameterKeyTest {

    @Test
    public void identityWithString() {
        ParameterKey first = new ParameterKey("foo");
        ParameterKey second = new ParameterKey("foo");
        ParameterKey third = new ParameterKey("bar");

        LinkedHashSet<ParameterKey> set = new LinkedHashSet<ParameterKey>();
        set.add(first);
        set.add(second);
        set.add(third);

        assertThat(set).hasSize(2).contains(new ParameterKey("foo"), new ParameterKey("bar"));
    }

    @Test
    public void identityWithInt() {
        ParameterKey first = new ParameterKey(100);
        ParameterKey second = new ParameterKey(100);
        ParameterKey third = new ParameterKey(99);

        Set<ParameterKey> set = new HashSet<ParameterKey>();
        set.add(first);
        set.add(second);
        set.add(third);

        assertThat(set).hasSize(2).contains(new ParameterKey(99), new ParameterKey(100));
    }

}
