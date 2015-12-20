package net.ttddyy.dsproxy.test;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.StatementType;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.proxy.ParameterKey;
import net.ttddyy.dsproxy.proxy.ParameterSetOperation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class QueryExecutionFactoryListener implements QueryExecutionListener {

    private List<QueryExecution> queryExecutions = new ArrayList<QueryExecution>();

    public QueryExecutionFactoryListener() {
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
            case PREPARED:
                if (isBatch) {
                    queryExecution = createPreparedBatchExecution(queryInfoList);
                } else {
                    queryExecution = createPreparedExecution(queryInfoList);
                }
                break;
            case CALLABLE:
                if (isBatch) {
                    queryExecution = createCallableBatchExecution(queryInfoList);
                } else {
                    queryExecution = createCallableExecution(queryInfoList);
                }
                break;
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

    private PreparedExecution createPreparedExecution(List<QueryInfo> queryInfoList) {
        String query = queryInfoList.get(0).getQuery();

        PreparedExecution pe = new PreparedExecution();
        pe.setQuery(query);

        // TODO: size of queryInfoList must be 1 for Prepared
        for (QueryInfo queryInfo : queryInfoList) {
            // TODO: size of parametersList must be 1 for Prepared
            for (List<ParameterSetOperation> params : queryInfo.getParametersList()) {
                for (ParameterSetOperation param : params) {
                    if (ParameterSetOperation.isSetNullParameterOperation(param)) {
                        populateSetNullParameter(pe, param);
                    } else {
                        populateParameter(pe, param);
                    }
                }
            }
        }

        return pe;
    }

    private PreparedBatchExecution createPreparedBatchExecution(List<QueryInfo> queryInfoList) {
        String query = queryInfoList.get(0).getQuery();

        PreparedBatchExecution pbe = new PreparedBatchExecution();
        pbe.setQuery(query);

        // TODO: size of queryInfoList must be 1 for Batch Prepared
        for (QueryInfo queryInfo : queryInfoList) {

            for (List<ParameterSetOperation> params : queryInfo.getParametersList()) {
                PreparedBatchExecution.PreparedBatchExecutionEntry batchEntry = new PreparedBatchExecution.PreparedBatchExecutionEntry();
                for (ParameterSetOperation param : params) {
                    if (ParameterSetOperation.isSetNullParameterOperation(param)) {
                        populateSetNullParameter(batchEntry, param);
                    } else {
                        populateParameter(batchEntry, param);
                    }
                }
                pbe.getBatchExecutionEntries().add(batchEntry);
            }
        }

        return pbe;
    }

    private CallableExecution createCallableExecution(List<QueryInfo> queryInfoList) {
        String query = queryInfoList.get(0).getQuery();

        CallableExecution ce = new CallableExecution();
        ce.setQuery(query);

        // TODO: size of queryInfoList must be 1 for Callable
        for (QueryInfo queryInfo : queryInfoList) {
            // TODO: size of parametersList must be 1 for Callable
            for (List<ParameterSetOperation> params : queryInfo.getParametersList()) {
                for (ParameterSetOperation param : params) {
                    // populate CallableExecution
                    if (ParameterSetOperation.isRegisterOutParameterOperation(param)) {
                        populateOutParameter(ce, param);
                    } else if (ParameterSetOperation.isSetNullParameterOperation(param)) {
                        populateSetNullParameter(ce, param);
                    } else {
                        populateParameter(ce, param);
                    }

                }
            }
        }
        return ce;
    }

    private CallableBatchExecution createCallableBatchExecution(List<QueryInfo> queryInfoList) {
        String query = queryInfoList.get(0).getQuery();

        CallableBatchExecution cbe = new CallableBatchExecution();
        cbe.setQuery(query);

        // TODO: size of queryInfoList must be 1 for Batch Callable
        for (QueryInfo queryInfo : queryInfoList) {

            for (List<ParameterSetOperation> params : queryInfo.getParametersList()) {
                CallableBatchExecution.CallableBatchExecutionEntry batchEntry = new CallableBatchExecution.CallableBatchExecutionEntry();

                for (ParameterSetOperation param : params) {
                    // populate batch entry
                    if (ParameterSetOperation.isRegisterOutParameterOperation(param)) {
                        populateOutParameter(batchEntry, param);
                    } else if (ParameterSetOperation.isSetNullParameterOperation(param)) {
                        populateSetNullParameter(batchEntry, param);
                    } else {
                        populateParameter(batchEntry, param);
                    }
                }

                cbe.getBatchExecutionEntries().add(batchEntry);
            }
        }

        return cbe;
    }

    private void populateParameter(ParameterHolder holder, ParameterSetOperation param) {
        Object[] args = param.getArgs();
        Object key = args[0];
        Object value = args[1];  // use second arg as value for the parameter-set-operation
        holder.getParams().put(getParameterKey(key), value);

    }

    private void populateSetNullParameter(ParameterHolder holder, ParameterSetOperation param) {
        Object[] args = param.getArgs();
        Object key = args[0];
        Integer value = (Integer) args[1]; // use second arg as value for the parameter-set-operation
        holder.getSetNullParams().put(getParameterKey(key), value);
    }

    private void populateOutParameter(OutParameterHolder holder, ParameterSetOperation param) {
        Object[] args = param.getArgs();
        Object key = args[0];
        Object value = args[1]; // use second arg as value for the parameter-set-operation
        holder.getOutParams().put(getParameterKey(key), value);
    }

    private ParameterKey getParameterKey(Object key) {
        if (key instanceof Integer) {
            return new ParameterKey((Integer) key);
        } else {
            return new ParameterKey((String) key);
        }
    }


    public List<QueryExecution> getQueryExecutions() {
        return queryExecutions;
    }

    public void reset() {
        this.queryExecutions.clear();
    }

}
