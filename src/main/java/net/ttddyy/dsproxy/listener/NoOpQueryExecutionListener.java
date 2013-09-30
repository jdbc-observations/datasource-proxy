package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;

import java.util.List;

/**
 * No operation implementation of {@link QueryExecutionListener}
 *
 * @author Tadaya Tsuyukubo
 * @since 1.2
 */
public class NoOpQueryExecutionListener implements QueryExecutionListener {
    public void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        // do nothing
    }

    public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        // do nothing
    }
}
