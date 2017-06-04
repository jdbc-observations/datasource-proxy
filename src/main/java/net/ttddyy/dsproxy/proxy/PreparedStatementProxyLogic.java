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
    private String dataSourceName;

    // when same key(index/name) is used for parameter set operation, old value will be replaced. To implement that logic
    // using a map, so that putting same key will override the entry.
    private Map<ParameterKey, ParameterSetOperation> parameters = new LinkedHashMap<ParameterKey, ParameterSetOperation>();
    private InterceptorHolder interceptorHolder;
    private JdbcProxyFactory jdbcProxyFactory = JdbcProxyFactory.DEFAULT;

    private List<Map<ParameterKey, ParameterSetOperation>> batchParameters = new ArrayList<Map<ParameterKey, ParameterSetOperation>>();

    private Connection proxyConnection;

    public PreparedStatementProxyLogic() {
    }

    public PreparedStatementProxyLogic(PreparedStatement ps, String query, InterceptorHolder interceptorHolder, String dataSourceName, JdbcProxyFactory jdbcProxyFactory, Connection proxyConnection) {
        this.ps = ps;
        this.query = query;
        this.interceptorHolder = interceptorHolder;
        this.dataSourceName = dataSourceName;
        this.jdbcProxyFactory = jdbcProxyFactory;
        this.proxyConnection = proxyConnection;
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
            return this.proxyConnection;
        }


        if (StatementMethodNames.METHODS_TO_OPERATE_PARAMETER.contains(methodName)) {

            // for parameter operation method
            if (StatementMethodNames.PARAMETER_METHODS.contains(methodName)) {

                // operation to set or clear parameterOperationHolder
                if ("clearParameters".equals(methodName)) {
                    parameters.clear();
                } else {

                    ParameterKey parameterKey;
                    if (args[0] instanceof Integer) {
                        parameterKey = new ParameterKey((Integer) args[0]);
                    } else if (args[0] instanceof String) {
                        parameterKey = new ParameterKey((String) args[0]);
                    } else {
                        return MethodUtils.proceedExecution(method, ps, args);
                    }

                    // when same key is specified, old value will be overridden
                    parameters.put(parameterKey, new ParameterSetOperation(method, args));
                }

            } else if (StatementMethodNames.BATCH_PARAM_METHODS.contains(methodName)) {

                // Batch parameter operation
                if ("addBatch".equals(methodName)) {

                    // TODO: check
                    transformParameters(true, batchParameters.size());

                    // copy values
                    Map<ParameterKey, ParameterSetOperation> newParams = new LinkedHashMap<ParameterKey, ParameterSetOperation>(parameters);
                    batchParameters.add(newParams);

                    parameters.clear();
                } else if ("clearBatch".equals(methodName)) {
                    batchParameters.clear();
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
            for (Map<ParameterKey, ParameterSetOperation> params : batchParameters) {
                queryInfo.getParametersList().add(new ArrayList<ParameterSetOperation>(params.values()));
            }
            queries.add(queryInfo);

            batchSize = batchParameters.size();
            batchParameters.clear();
            isBatchExecution = true;

        } else if (StatementMethodNames.QUERY_EXEC_METHODS.contains(methodName)) {
            transformParameters(false, 0);
            QueryInfo queryInfo = new QueryInfo(this.query);
            queryInfo.getParametersList().add(new ArrayList<ParameterSetOperation>(parameters.values()));
            queries.add(queryInfo);
        }

        final ExecutionInfo execInfo = new ExecutionInfo(dataSourceName, this.ps, isBatchExecution, batchSize, method, args);

        final QueryExecutionListener listener = interceptorHolder.getListener();
        listener.beforeQuery(execInfo, queries);

        // Invoke method on original Statement.
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


    private void transformParameters(boolean isBatch, int count) throws SQLException, IllegalAccessException, InvocationTargetException {

        // transform parameters
        final ParameterReplacer parameterReplacer = new ParameterReplacer(this.parameters);
        final TransformInfo transformInfo = new TransformInfo(ps.getClass(), dataSourceName, query, isBatch, count);
        final ParameterTransformer parameterTransformer = interceptorHolder.getParameterTransformer();
        parameterTransformer.transformParameters(parameterReplacer, transformInfo);

        if (parameterReplacer.isModified()) {

            ps.clearParameters();  // clear existing parameters

            // re-set parameters
            Map<ParameterKey, ParameterSetOperation> modifiedParameters = parameterReplacer.getModifiedParameters();
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
