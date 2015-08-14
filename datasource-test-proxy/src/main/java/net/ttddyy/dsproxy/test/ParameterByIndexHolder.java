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
     * Keys of parameters.
     *
     * @return Integer keys.
     */
    List<Integer> getParamIndexes();

}
