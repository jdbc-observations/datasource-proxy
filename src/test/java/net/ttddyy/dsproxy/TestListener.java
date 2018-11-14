package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.listener.QueryExecutionContext;
import net.ttddyy.dsproxy.listener.ProxyDataSourceListener;

/**
 * @author Tadaya Tsuyukubo
 */
public class TestListener implements ProxyDataSourceListener {
    int beforeCount = 0;
    int afterCount = 0;

    @Override
    public void beforeQuery(QueryExecutionContext executionContext) {
        beforeCount++;
    }

    @Override
    public void afterQuery(QueryExecutionContext executionContext) {
        afterCount++;
    }


    public int getBeforeCount() {
        return beforeCount;
    }

    public int getAfterCount() {
        return afterCount;
    }
}
