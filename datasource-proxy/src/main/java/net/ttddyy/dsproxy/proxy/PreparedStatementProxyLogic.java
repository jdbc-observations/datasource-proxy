package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.transform.ParameterReplacer;
import net.ttddyy.dsproxy.transform.ParameterTransformer;
import net.ttddyy.dsproxy.transform.TransformInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Shared logic for {@link PreparedStatement} and {@link CallableStatement} invocation.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.2
 */
public class PreparedStatementProxyLogic {

    private PreparedStatement ps;
    private String query;
    private List<Integer> outParamIndexes= new ArrayList<Integer>();
    private List<String> outParamNames= new ArrayList<String>();
    private String dataSourceName;
    private Map<Object, ParameterSetOperation> parameters = new LinkedHashMap<Object, ParameterSetOperation>();
    private InterceptorHolder interceptorHolder;
    private JdbcProxyFactory jdbcProxyFactory = JdbcProxyFactory.DEFAULT;

    private List<Map<Object, ParameterSetOperation>> batchParameters = new ArrayList<Map<Object, ParameterSetOperation>>();

    public PreparedStatementProxyLogic() {
    }

    public PreparedStatementProxyLogic(PreparedStatement ps, String query, InterceptorHolder interceptorHolder, String dataSourceName, JdbcProxyFactory jdbcProxyFactory) {
        this.ps = ps;
        this.query = query;
        this.interceptorHolder = interceptorHolder;
        this.dataSourceName = dataSourceName;
        this.jdbcProxyFactory = jdbcProxyFactory;
    }

    public Object invoke(Method method, Object[] args) throws Throwable {

        final String methodName = method.getName();

        if (!StatementMethodNames.METHODS_TO_INTERCEPT.contains(methodName)) {
            return MethodUtils.proceedExecution(method, ps, args);
        }

        // special treat for toString method
        if ("toString".equals(methodName)) {
            final StringBuilder sb = new StringBuilder();
            sb.append(ps.getClass().getSimpleName());   // PreparedStatement or CallableStatement
            sb.append(" [");
            sb.append(ps.toString());
            sb.append("]");
            return sb.toString(); // differentiate toString message.
        } else if ("getDataSourceName".equals(methodName)) {
            return dataSourceName;
        } else if ("getTarget".equals(methodName)) {
            // ProxyJdbcObject interface has a method to return original object.
            return ps;
        }

        if (StatementMethodNames.JDBC4_METHODS.contains(methodName)) {
            final Class<?> clazz = (Class<?>) args[0];
            if ("unwrap".equals(methodName)) {
                return ps.unwrap(clazz);
            } else if ("isWrapperFor".equals(methodName)) {
                return ps.isWrapperFor(clazz);
            }
        }

        if (StatementMethodNames.GET_CONNECTION_METHOD.contains(methodName)) {
            final Connection conn = (Connection) MethodUtils.proceedExecution(method, ps, args);
            return jdbcProxyFactory.createConnection(conn, interceptorHolder, dataSourceName);
        }


        if (StatementMethodNames.METHODS_TO_OPERATE_PARAMETER.contains(methodName)) {

            // for parameter operation method
            if (StatementMethodNames.PARAMETER_METHODS.contains(methodName)) {

                // operation to set or clear parameterOperationHolder
                if ("clearParameters".equals(methodName)) {
                    parameters.clear();
                } else if(StatementMethodNames.PARAMETER_METHOD_REGISTER_OUT_PARAMETER.equals(methodName)){
                    if(args[0] instanceof Integer){
                        outParamIndexes.add((Integer)args[0]);
                    }else{
                        outParamNames.add((String)args[0]);
                    }
                }else {
                    parameters.put(args[0], new ParameterSetOperation(method, args));
                }

            } else if (StatementMethodNames.BATCH_PARAM_METHODS.contains(methodName)) {

                // Batch parameter operation
                if ("addBatch".equals(methodName)) {

                    // TODO: check
                    transformParameters(true, batchParameters.size());

                    // copy values
                    Map<Object, ParameterSetOperation> newParams = new LinkedHashMap<Object, ParameterSetOperation>(parameters);
                    batchParameters.add(newParams);

                    parameters.clear();
                } else if ("clearBatch".equals(methodName)) {
                    batchParameters.clear();
                    outParamIndexes.clear();
                    outParamNames.clear();
                }
            }

            // proceed execution, no need to call listener
            return MethodUtils.proceedExecution(method, ps, args);
        }


        // query execution methods

        final List<QueryInfo> queries = new ArrayList<QueryInfo>();
        boolean isBatchExecution = false;
        int batchSize = 0;

        if (StatementMethodNames.BATCH_EXEC_METHODS.contains(methodName)) {

            // one query with multiple parameters
            QueryInfo queryInfo = new QueryInfo(this.query);
            for (Map<Object, ParameterSetOperation> params : batchParameters) {
                queryInfo.getQueryArgsList().add(getQueryParameters(params));
                queryInfo.getOutParamIndexes().addAll(outParamIndexes);
                queryInfo.getOutParamNames().addAll(outParamNames);
            }
            queries.add(queryInfo);

            batchSize = batchParameters.size();
            batchParameters.clear();
            isBatchExecution = true;

        } else if (StatementMethodNames.QUERY_EXEC_METHODS.contains(methodName)) {
            transformParameters(false, 0);
            QueryInfo queryInfo = new QueryInfo(this.query);
            queryInfo.getQueryArgsList().add(getQueryParameters(parameters));
            queryInfo.getOutParamIndexes().addAll(outParamIndexes);
            queryInfo.getOutParamNames().addAll(outParamNames);
            queries.add(queryInfo);
        }

        final QueryExecutionListener listener = interceptorHolder.getListener();
        listener.beforeQuery(new ExecutionInfo(dataSourceName, this.ps, isBatchExecution, batchSize, method, args), queries);

        // Invoke method on original Statement.
        final ExecutionInfo execInfo = new ExecutionInfo(dataSourceName, this.ps, isBatchExecution, batchSize, method, args);

        try {
            final long beforeTime = System.currentTimeMillis();

            Object retVal = method.invoke(ps, args);

            final long afterTime = System.currentTimeMillis();

            execInfo.setResult(retVal);
            execInfo.setElapsedTime(afterTime - beforeTime);
            execInfo.setSuccess(true);

            return retVal;
        } catch (InvocationTargetException ex) {
            execInfo.setThrowable(ex.getTargetException());
            execInfo.setSuccess(false);
            throw ex.getTargetException();
        } finally {
            listener.afterQuery(execInfo, queries);
        }
    }

    private Map<String, Object> getQueryParameters(Map<Object, ParameterSetOperation> params) {
        Map<String, Object> queryParameters = new LinkedHashMap<String, Object>(params.size());
        for (ParameterSetOperation parameterSetOperation : params.values()) {
            String key = parameterSetOperation.getParameterNameOrIndexAsString();
            Object value = parameterSetOperation.getParameterValue();
            queryParameters.put(key, value);
        }
        return queryParameters;
    }

    private void transformParameters(boolean isBatch, int count) throws SQLException, IllegalAccessException, InvocationTargetException {

        // transform parameters
        final ParameterReplacer parameterReplacer = new ParameterReplacer(this.parameters);
        final TransformInfo transformInfo = new TransformInfo(ps.getClass(), dataSourceName, query, isBatch, count);
        final ParameterTransformer parameterTransformer = interceptorHolder.getParameterTransformer();
        parameterTransformer.transformParameters(parameterReplacer, transformInfo);

        if (parameterReplacer.isModified()) {

            ps.clearParameters();  // clear existing parameters

            // re-set parameters
            Map<Object, ParameterSetOperation> modifiedParameters = parameterReplacer.getModifiedParameters();
            for (ParameterSetOperation operation : modifiedParameters.values()) {
                final Method paramMethod = operation.getMethod();
                final Object[] paramArgs = operation.getArgs();
                paramMethod.invoke(ps, paramArgs);
            }

            // replace
            this.parameters = modifiedParameters;
        }
    }

}
