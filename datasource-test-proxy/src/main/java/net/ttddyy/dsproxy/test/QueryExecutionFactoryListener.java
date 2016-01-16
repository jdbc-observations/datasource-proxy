package net.ttddyy.dsproxy.test;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.StatementType;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.proxy.ParameterKey;
import net.ttddyy.dsproxy.proxy.ParameterSetOperation;

import java.util.ArrayList;
import java.util.List;

import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.createRegisterOut;
import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.createSetNull;
import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.createSetParam;

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
                populateParameterSetOperations(pe, params);
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
                populateParameterSetOperations(batchEntry, params);
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
                populateParameterSetOperations(ce, params);
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
                populateParameterSetOperations(batchEntry, params);
                cbe.getBatchExecutionEntries().add(batchEntry);
            }
        }

        return cbe;
    }

    private void populateParameterSetOperations(ParameterHolder holder, List<ParameterSetOperation> params) {
        for (ParameterSetOperation param : params) {
            populateParameterSetOperation(holder, param);
        }
    }

    private void populateParameterSetOperation(ParameterHolder holder, ParameterSetOperation setOperation) {
        Object[] args = setOperation.getArgs();
        Object key = args[0];
        Object value = args[1]; // use second arg as value for the parameter-set-operation

        ParameterKeyValue keyValue;
        if (ParameterSetOperation.isSetNullParameterOperation(setOperation)) {
            ParameterKey parameterKey = getParameterKey(key);
            keyValue = createSetNull(parameterKey, value);
        } else if (ParameterSetOperation.isRegisterOutParameterOperation(setOperation)) {
            ParameterKey parameterKey = getParameterKey(key);
            keyValue = createRegisterOut(parameterKey, value);
        } else {
            ParameterKey parameterKey = getParameterKey(key);
            keyValue = createSetParam(parameterKey, value);
        }

        holder.getParameters().add(keyValue);
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
