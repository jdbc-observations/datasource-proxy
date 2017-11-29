package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.MethodExecutionListenerUtils;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.transform.ParameterReplacer;
import net.ttddyy.dsproxy.transform.ParameterTransformer;
import net.ttddyy.dsproxy.transform.TransformInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static net.ttddyy.dsproxy.proxy.StatementMethodNames.EXEC_METHODS;
import static net.ttddyy.dsproxy.proxy.StatementMethodNames.GET_GENERATED_KEYS_METHOD;
import static net.ttddyy.dsproxy.proxy.StatementMethodNames.METHODS_TO_RETURN_RESULTSET;

/**
 * Shared logic for {@link PreparedStatement} and {@link CallableStatement} invocation.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.2
 */
public class PreparedStatementProxyLogic {

    /**
     * Builder for {@link PreparedStatementProxyLogic}.
     *
     * @since 1.4.2
     */
    public static class Builder {
        private PreparedStatement ps;
        private String query;
        private ConnectionInfo connectionInfo;
        private Connection proxyConnection;
        private ProxyConfig proxyConfig;

        public static Builder create() {
            return new Builder();
        }

        public PreparedStatementProxyLogic build() {
            PreparedStatementProxyLogic logic = new PreparedStatementProxyLogic();
            logic.ps = this.ps;
            logic.query = this.query;
            logic.connectionInfo = this.connectionInfo;
            logic.proxyConnection = this.proxyConnection;
            logic.proxyConfig = this.proxyConfig;
            return logic;
        }

        public Builder preparedStatement(PreparedStatement ps) {
            this.ps = ps;
            return this;
        }

        public Builder query(String query) {
            this.query = query;
            return this;
        }

        public Builder connectionInfo(ConnectionInfo connectionInfo) {
            this.connectionInfo = connectionInfo;
            return this;
        }

        public Builder proxyConnection(Connection proxyConnection) {
            this.proxyConnection = proxyConnection;
            return this;
        }

        public Builder proxyConfig(ProxyConfig proxyConfig) {
            this.proxyConfig = proxyConfig;
            return this;
        }
    }

    private PreparedStatement ps;
    private String query;
    private ConnectionInfo connectionInfo;

    // when same key(index/name) is used for parameter set operation, old value will be replaced. To implement that logic
    // using a map, so that putting same key will override the entry.
    private Map<ParameterKey, ParameterSetOperation> parameters = new LinkedHashMap<ParameterKey, ParameterSetOperation>();

    private List<Map<ParameterKey, ParameterSetOperation>> batchParameters = new ArrayList<Map<ParameterKey, ParameterSetOperation>>();

    private Connection proxyConnection;
    private ProxyConfig proxyConfig;
    private ResultSet generatedKeys;

    public Object invoke(Method method, Object[] args) throws Throwable {

        return MethodExecutionListenerUtils.invoke(new MethodExecutionListenerUtils.MethodExecutionCallback() {
            @Override
            public Object execute(Object proxyTarget, Method method, Object[] args) throws Throwable {
                return performQueryExecutionListener(method, args);
            }
        }, this.proxyConfig, this.ps, this.connectionInfo, method, args);

    }

    private Object performQueryExecutionListener(Method method, Object[] args) throws Throwable {

        final String methodName = method.getName();

        if (!StatementMethodNames.METHODS_TO_INTERCEPT.contains(methodName)) {
            return MethodUtils.proceedExecution(method, ps, args);
        }

        ParameterTransformer parameterTransformer = this.proxyConfig.getParameterTransformer();
        QueryExecutionListener queryListener = this.proxyConfig.getQueryListener();
        JdbcProxyFactory proxyFactory = this.proxyConfig.getJdbcProxyFactory();


        // special treat for toString method
        if ("toString".equals(methodName)) {
            final StringBuilder sb = new StringBuilder();
            sb.append(ps.getClass().getSimpleName());   // PreparedStatement or CallableStatement
            sb.append(" [");
            sb.append(ps.toString());
            sb.append("]");
            return sb.toString(); // differentiate toString message.
        } else if ("getDataSourceName".equals(methodName)) {
            return this.connectionInfo.getDataSourceName();
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
                    transformParameters(parameterTransformer, true, batchParameters.size());

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
            transformParameters(parameterTransformer, false, 0);
            QueryInfo queryInfo = new QueryInfo(this.query);
            queryInfo.getParametersList().add(new ArrayList<ParameterSetOperation>(parameters.values()));
            queries.add(queryInfo);
        }

        final ExecutionInfo execInfo = new ExecutionInfo(this.connectionInfo, this.ps, isBatchExecution, batchSize, method, args);

        queryListener.beforeQuery(execInfo, queries);

        // Invoke method on original Statement.
        try {
            final long beforeTime = System.currentTimeMillis();

            Object retVal = method.invoke(this.ps, args);

            final long afterTime = System.currentTimeMillis();

            // Retrieve generatedKeys when it is enabled
            if (EXEC_METHODS.contains(methodName) && this.proxyConfig.isAutoRetrieveGeneratedKeys()) {
                ResultSet generatedKeysResultSet = this.ps.getGeneratedKeys();
                if (this.proxyConfig.isGeneratedKeysProxyEnabled()) {
                    this.generatedKeys = proxyFactory.createGeneratedKeys(generatedKeysResultSet, this.connectionInfo, this.proxyConfig);
                } else {
                    this.generatedKeys = generatedKeysResultSet;
                }
                execInfo.setGeneratedKeys(this.generatedKeys);
            }

            // execInfo.setResult will have proxied ResultSet if enabled
            if (METHODS_TO_RETURN_RESULTSET.contains(methodName)) {
                if (GET_GENERATED_KEYS_METHOD.equals(methodName)) {
                    if (this.proxyConfig.isAutoRetrieveGeneratedKeys()) {
                        // generatedKeys is already proxied at retrieval.
                        retVal = this.generatedKeys;
                    } else if (this.proxyConfig.isGeneratedKeysProxyEnabled()) {
                        retVal = proxyFactory.createGeneratedKeys((ResultSet) retVal, this.connectionInfo, this.proxyConfig);
                    }
                    // else use raw value
                } else {
                    if (this.proxyConfig.isResultSetProxyEnabled()) {
                        retVal = proxyFactory.createResultSet((ResultSet) retVal, this.connectionInfo, this.proxyConfig);
                    }
                }
            }

            execInfo.setResult(retVal);
            execInfo.setElapsedTime(afterTime - beforeTime);
            execInfo.setSuccess(true);

            return retVal;
        } catch (InvocationTargetException ex) {
            execInfo.setThrowable(ex.getTargetException());
            execInfo.setSuccess(false);
            throw ex.getTargetException();
        } finally {
            queryListener.afterQuery(execInfo, queries);

            if (this.proxyConfig.isAutoCloseGeneratedKeys() && this.generatedKeys != null && !this.generatedKeys.isClosed()) {
                this.generatedKeys.close();
            }

        }
    }


    private void transformParameters(ParameterTransformer parameterTransformer, boolean isBatch, int count) throws SQLException, IllegalAccessException, InvocationTargetException {

        // transform parameters
        final ParameterReplacer parameterReplacer = new ParameterReplacer(this.parameters);
        final TransformInfo transformInfo = new TransformInfo(ps.getClass(), this.connectionInfo.getDataSourceName(), query, isBatch, count);
        parameterTransformer.transformParameters(parameterReplacer, transformInfo);

        if (parameterReplacer.isModified()) {

            ps.clearParameters();  // clear existing parameters

            // re-set parameters
            Map<ParameterKey, ParameterSetOperation> modifiedParameters = parameterReplacer.getModifiedParameters();
            for (ParameterSetOperation operation : modifiedParameters.values()) {
                final Method paramMethod = operation.getMethod();
                final Object[] paramArgs = operation.getArgs();
                paramMethod.invoke(this.ps, paramArgs);
            }

            // replace
            this.parameters = modifiedParameters;
        }
    }

}
