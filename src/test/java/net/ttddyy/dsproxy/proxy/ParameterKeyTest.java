package net.ttddyy.dsproxy.proxy;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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

    @Test
    public void sort() {
        ParameterKey numOne = new ParameterKey(1);
        ParameterKey numFive = new ParameterKey(5);
        ParameterKey numTen = new ParameterKey(10);
        ParameterKey strTwo = new ParameterKey("2");
        ParameterKey strSeven = new ParameterKey("7");
        ParameterKey foo = new ParameterKey("foo");
        ParameterKey bar = new ParameterKey("bar");

        SortedSet<ParameterKey> set = new TreeSet<ParameterKey>();

        // all int numbers
        set.addAll(Arrays.asList(numFive, numOne, numTen));
        assertThat(set).containsSequence(numOne, numFive, numTen);

        // num-int and num-str
        set = new TreeSet<ParameterKey>();
        set.addAll(Arrays.asList(numFive, strTwo, strSeven));
        assertThat(set).containsSequence(strTwo, numFive, strSeven);

        // num-int and non-num-str
        set = new TreeSet<ParameterKey>();
        set.addAll(Arrays.asList(numFive, foo, strTwo));
        assertThat(set).containsSequence(strTwo, numFive, foo);


        // num-str and non-num-str
        set = new TreeSet<ParameterKey>();
        set.addAll(Arrays.asList(bar, foo, strTwo));
        assertThat(set).containsSequence(strTwo, bar, foo);


        // non-num-str and non-num-str
        set = new TreeSet<ParameterKey>();
        set.addAll(Arrays.asList(bar, foo));
        assertThat(set).containsSequence(bar, foo);


        // all
        set = new TreeSet<ParameterKey>();
        set.addAll(Arrays.asList(numFive, numOne, numTen, strSeven, strTwo, bar, foo));
        assertThat(set).containsSequence(numOne, strTwo, numFive, strSeven, numTen, bar, foo);
    }

    @Test
    public void equal() {
        ParameterKey numOne = new ParameterKey(1);
        ParameterKey anotherNumOne = new ParameterKey(1);
        ParameterKey strOne = new ParameterKey("1");
        ParameterKey anotherStrOne = new ParameterKey("1");

        assertThat(numOne).isEqualTo(anotherNumOne);
        assertThat(numOne).isNotEqualTo(strOne);
        assertThat(strOne).isEqualTo(anotherStrOne);
        assertThat(strOne).isNotEqualTo(numOne);

    }
    @Test
    public void equalComparison() {
        ParameterKey numOne = new ParameterKey(1);
        ParameterKey anotherNumOne = new ParameterKey(1);
        ParameterKey strOne = new ParameterKey("1");
        ParameterKey anotherStrOne = new ParameterKey("1");
        ParameterKey foo = new ParameterKey("foo");

        assertThat(numOne).isEqualByComparingTo(numOne);
        assertThat(numOne).isEqualByComparingTo(anotherNumOne);
        assertThat(numOne).isEqualByComparingTo(strOne);

        assertThat(strOne).isEqualByComparingTo(strOne);
        assertThat(strOne).isEqualByComparingTo(anotherStrOne);
        assertThat(strOne).isEqualByComparingTo(numOne);

        assertThat(foo).isEqualByComparingTo(foo);
    }

}
