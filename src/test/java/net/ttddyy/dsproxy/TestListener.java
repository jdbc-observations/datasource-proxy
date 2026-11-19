package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.listener.ProxyDataSourceListener;
import net.ttddyy.dsproxy.listener.QueryExecutionContext;

/**
 * @author Tadaya Tsuyukubo
 */
public class TestListener implements ProxyDataSourceListener {
    int beforeCount = 0;
    int afterCount = 0;

    @Override
    public void beforeQuery(QueryExecutionContext context) {
        beforeCount++;
    }

    @Override
    public void afterQuery(QueryExecutionContext context) {
        afterCount++;
    }


    public int getBeforeCount() {
        return beforeCount;
    }

    public int getAfterCount() {
        return afterCount;
    }
}
