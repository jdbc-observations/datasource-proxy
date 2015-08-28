package net.ttddyy.dsproxy.test;

import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public interface BatchExecutionEntry {

    // TODO: implement values matcher??
    /**
     * This doesn't include values for "setNull" operations
     */
    List<Object> getParamValues();

}
