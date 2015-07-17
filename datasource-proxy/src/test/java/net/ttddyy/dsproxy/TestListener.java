package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;

import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 */
public class TestListener implements QueryExecutionListener {
    int beforeCount = 0;
    int afterCount = 0;

    public void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        beforeCount++;
    }

    public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        afterCount++;
    }


    public int getBeforeCount() {
        return beforeCount;
    }

    public int getAfterCount() {
        return afterCount;
    }
}
