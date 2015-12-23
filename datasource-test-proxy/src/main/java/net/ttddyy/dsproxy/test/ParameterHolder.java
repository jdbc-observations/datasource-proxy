package net.ttddyy.dsproxy.test;

import net.ttddyy.dsproxy.proxy.ParameterKey;

import java.util.Map;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public interface ParameterHolder {

    Map<ParameterKey, Object> getParams();

    Map<ParameterKey, Integer> getSetNullParams();

    Map<ParameterKey, Object> getAllParams();

}
