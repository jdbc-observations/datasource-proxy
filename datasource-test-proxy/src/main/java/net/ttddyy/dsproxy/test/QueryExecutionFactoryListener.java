package net.ttddyy.dsproxy.test;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.StatementType;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class QueryExecutionFactoryListener implements QueryExecutionListener {

    private List<QueryExecution> queryExecutions = new ArrayList<QueryExecution>();

    public QueryExecutionFactoryListener(List<QueryExecution> queryExecutions) {
        this.queryExecutions = queryExecutions;
    }

    @Override
    public void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        // no-op
    }

    @Override
    public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        StatementType statementType = execInfo.getStatementType();
        boolean isBatch = execInfo.isBatch();

        BaseQueryExecution queryExecution = null;
        switch (statementType) {
            case STATEMENT:
                if (isBatch) {
                    queryExecution = createStatementBatchExecution(queryInfoList);
                } else {
                    queryExecution = createStatementExecution(queryInfoList);
                }
                break;
//                case PREPARED:
//                    if (isBatch) {
//                        // TODO: impl
//                    } else {
//                        // TODO: impl
//                    }
//                    break;
//                case CALLABLE:
//                    if (isBatch) {
//                        // TODO: impl
//                    } else {
//                        // TODO: impl
//                    }
//                    break;
        }
        queryExecution.setSuccess(execInfo.isSuccess());

        queryExecutions.add(queryExecution);
    }

    private StatementExecution createStatementExecution(List<QueryInfo> queryInfoList) {
        String query = queryInfoList.get(0).getQuery();

        StatementExecution se = new StatementExecution();
        se.setQuery(query);
        return se;
    }

    private StatementBatchExecution createStatementBatchExecution(List<QueryInfo> queryInfoList) {
        StatementBatchExecution sbe = new StatementBatchExecution();

        for (QueryInfo queryInfo : queryInfoList) {
            sbe.getQueries().add(queryInfo.getQuery());
        }

        return sbe;
    }

}
