package net.ttddyy.dsproxy.test;

import java.util.List;
import java.util.Map;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public interface ParameterByNameHolder extends ParameterHolder {

    Map<String, Object> getSetParamsByName();

    Map<String, Integer> getSetNullParamsByName();

    /**
     * Keys of parameters.
     * Includes both set_param and set_null parameters.
     *
     * @return String keys.
     */
    List<String> getParamNames();

}
