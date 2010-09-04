package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 */
public class LastQueryListener implements QueryExecutionListener {
    private ExecutionInfo beforeExecInfo;
    private ExecutionInfo afterExecInfo;
    private List<QueryInfo> beforeQueries = new ArrayList<QueryInfo>();
    private List<QueryInfo> afterQueries = new ArrayList<QueryInfo>();

    public void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        beforeExecInfo = execInfo;
        beforeQueries = queryInfoList;
    }

    public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        afterExecInfo = execInfo;
        afterQueries = queryInfoList;
    }

    public ExecutionInfo getBeforeExecInfo() {
        return beforeExecInfo;
    }

    public ExecutionInfo getAfterExecInfo() {
        return afterExecInfo;
    }

    public List<QueryInfo> getBeforeQueries() {
        return beforeQueries;
    }

    public List<QueryInfo> getAfterQueries() {
        return afterQueries;
    }
}