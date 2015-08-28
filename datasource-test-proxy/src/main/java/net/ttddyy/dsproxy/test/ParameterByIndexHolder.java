package net.ttddyy.dsproxy.test;

import java.util.List;
import java.util.Map;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public interface ParameterByIndexHolder extends ParameterHolder {

    Map<Integer, Object> getParamsByIndex();

    /**
     * setNull operations.
     *
     * @return key is index, value is {@link java.sql.Types} (int).
     */
    Map<Integer, Integer> getSetNullParamsByIndex();

    /**
     * Keys of parameters.
     *
     * @return Integer keys.
     */
    List<Integer> getParamIndexes();

}
