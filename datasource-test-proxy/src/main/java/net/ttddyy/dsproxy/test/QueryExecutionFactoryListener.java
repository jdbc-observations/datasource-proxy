package net.ttddyy.dsproxy.test;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.StatementType;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
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
                    populateParameterByIndex(pe, param);
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
                    populateParameterByIndex(batchEntry, param);
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
                    Object[] args = param.getArgs();
                    boolean isIndex = args[0] instanceof Integer;

                    // populate CallableExecution
                    if (ParameterSetOperation.isRegisterOutParameterOperation(param)) {
                        populateOutParameter(ce, param);
                    } else {
                        if (isIndex) {
                            populateParameterByIndex(ce, param);
                        } else {
                            populateParameterByName(ce, param);
                        }
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
                    Object[] args = param.getArgs();
                    boolean isIndex = args[0] instanceof Integer;

                    // populate batch entry
                    if (ParameterSetOperation.isRegisterOutParameterOperation(param)) {
                        populateOutParameter(batchEntry, param);
                    } else {
                        if (isIndex) {
                            populateParameterByIndex(batchEntry, param);
                        } else {
                            populateParameterByName(batchEntry, param);
                        }
                    }
                }

                cbe.getBatchExecutionEntries().add(batchEntry);
            }
        }

        return cbe;
    }

    private void populateParameterByIndex(ParameterByIndexHolder holder, ParameterSetOperation param) {
        Object[] args = param.getArgs();
        Integer key = (Integer) args[0];
        Object value = args[1];  // use second arg as value for the parameter-set-operation

        if (ParameterSetOperation.isSetNullParameterOperation(param)) {
            holder.getSetNullParamsByIndex().put(key, (Integer) value);
        } else {
            holder.getParamsByIndex().put(key, value);
        }

    }

    private void populateParameterByName(ParameterByNameHolder holder, ParameterSetOperation param) {
        Object[] args = param.getArgs();
        String key = (String) args[0];
        Object value = args[1];  // use second arg as value for the parameter-set-operation

        if (ParameterSetOperation.isSetNullParameterOperation(param)) {
            holder.getSetNullParamsByName().put(key, (Integer) value);
        } else {
            holder.getParamsByName().put(key, value);
        }
    }

    private void populateOutParameter(OutParameterHolder holder, ParameterSetOperation param) {
        Object[] args = param.getArgs();
        Object key = args[0];
        Object value = args[1]; // use second arg as value for the parameter-set-operation
        boolean isIndex = key instanceof Integer;

        if (isIndex) {
            holder.getOutParamsByIndex().put((Integer) key, value);
        } else {
            holder.getOutParamsByName().put((String) key, value);
        }
    }


    public List<QueryExecution> getQueryExecutions() {
        return queryExecutions;
    }

    public void reset() {
        this.queryExecutions.clear();
    }

}
