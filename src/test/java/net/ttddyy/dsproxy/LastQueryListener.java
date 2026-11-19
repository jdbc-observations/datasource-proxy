package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.listener.ProxyDataSourceListener;
import net.ttddyy.dsproxy.listener.QueryExecutionContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 */
public class LastQueryListener implements ProxyDataSourceListener {
    private QueryExecutionContext beforeExecInfo;
    private QueryExecutionContext afterExecInfo;
    private List<QueryInfo> beforeQueries = new ArrayList<QueryInfo>();
    private List<QueryInfo> afterQueries = new ArrayList<QueryInfo>();

    @Override
    public void beforeQuery(QueryExecutionContext context) {
        beforeExecInfo = context;
        beforeQueries = context.getQueries();
    }

    @Override
    public void afterQuery(QueryExecutionContext context) {
        afterExecInfo = context;
        afterQueries = context.getQueries();
    }

    public QueryExecutionContext getBeforeExecInfo() {
        return beforeExecInfo;
    }

    public QueryExecutionContext getAfterExecInfo() {
        return afterExecInfo;
    }

    public List<QueryInfo> getBeforeQueries() {
        return beforeQueries;
    }

    public List<QueryInfo> getAfterQueries() {
        return afterQueries;
    }
}
