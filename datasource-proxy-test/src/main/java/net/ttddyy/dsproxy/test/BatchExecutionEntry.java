package net.ttddyy.dsproxy.test;

import java.util.List;
import java.util.Map;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public interface BatchExecutionEntry {

    Map<String, Object> getParamsByName();

    Map<Integer, Object> getParamsByIndex();

    List<String> getParamNames();

    List<Integer> getParamIndexes();

    List<Object> getParamValues();

}
