package net.ttddyy.dsproxy.test;

import java.util.SortedSet;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public interface ParameterHolder {

    SortedSet<ParameterKeyValue> getParameters();

    SortedSet<ParameterKeyValue> getSetParams();

    SortedSet<ParameterKeyValue> getSetNullParams();

}
