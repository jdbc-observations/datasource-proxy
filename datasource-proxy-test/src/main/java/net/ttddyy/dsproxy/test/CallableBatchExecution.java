package net.ttddyy.dsproxy.test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class CallableBatchExecution implements BatchParameterHolder {

    // TODO: implement CallableBatchExecutionEntry


    public String query;

    public List<BatchExecutionEntry> batchExecutionEntries = new ArrayList<BatchExecutionEntry>();

    public void setQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    @Override
    public List<BatchExecutionEntry> getBatchExecutionEntries() {
        return batchExecutionEntries;
    }

}
