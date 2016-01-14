package net.ttddyy.dsproxy.test;

import net.ttddyy.dsproxy.proxy.ParameterKey;

import java.util.Map;
import java.util.SortedSet;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public interface ParameterHolder {

    SortedSet<ParameterKeyValue> getParameters();

    Map<ParameterKey, Object> getParams();

    Map<ParameterKey, Integer> getSetNullParams();

    Map<ParameterKey, Object> getAllParams();

}
