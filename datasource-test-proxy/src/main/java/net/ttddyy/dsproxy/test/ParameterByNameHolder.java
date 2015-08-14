package net.ttddyy.dsproxy.test;

import java.util.List;
import java.util.Map;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public interface ParameterByNameHolder extends ParameterHolder {

    Map<String, Object> getParamsByName();

    /**
     * Keys of parameters.
     *
     * @return String keys.
     */
    List<String> getParamNames();

}
