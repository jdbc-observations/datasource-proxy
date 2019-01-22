package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.listener.QueryExecutionContext;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.StatementType;
import net.ttddyy.dsproxy.listener.MethodExecutionContext;
import net.ttddyy.dsproxy.listener.ProxyDataSourceListener;
import net.ttddyy.dsproxy.transform.QueryTransformer;
import net.ttddyy.dsproxy.transform.TransformInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static net.ttddyy.dsproxy.proxy.StatementMethodNames.GET_GENERATED_KEYS_METHOD;
import static net.ttddyy.dsproxy.proxy.StatementMethodNames.GET_RESULTSET_METHOD;
import static net.ttddyy.dsproxy.proxy.StatementMethodNames.METHODS_TO_RETURN_RESULTSET;

/**
 * Shared proxy logic for {@link Statement}, {@link PreparedStatement} and {@link CallableStatement} invocation.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.2
 */
public class StatementProxyLogic extends ProxyLogicSupport {

    /**
     * Builder for {@link StatementProxyLogic}.
     *
     * @since 1.4.2
     */
    public static class Builder {
        private Statement statement;
        private StatementType statementType;
        private String query;
        private ConnectionInfo connectionInfo;
        private Connection proxyConnection;
        private ProxyConfig proxyConfig;
        private boolean generateKey;

        public static Builder create() {
            return new Builder();
        }

        public StatementProxyLogic build() {
            StatementProxyLogic logic = new StatementProxyLogic();
            logic.statement = this.statement;
            logic.query = this.query;
            logic.connectionInfo = this.connectionInfo;
            logic.proxyConnection = this.proxyConnection;
            logic.proxyConfig = this.proxyConfig;
            logic.statementType = this.statementType;
            logic.generateKey = this.generateKey;
            return logic;
        }

        public Builder statement(Statement statement, StatementType statementType) {
            this.statement = statement;
            this.statementType = statementType;
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

        public Builder generateKey(boolean generateKey) {
            this.generateKey = generateKey;
            return this;
        }
    }

    private Statement statement;
    private StatementType statementType;
    private String query;
    private ConnectionInfo connectionInfo;

    private ParameterSetOperations parameterSetOperations = new ParameterSetOperations();

    private List<String> batchQueries = new ArrayList<>();  // used for batch statement
    private List<ParameterSetOperations> batchParameterSetOperations = new ArrayList<>();

    private Connection proxyConnection;
    private ProxyConfig proxyConfig;
    private ResultSet generatedKeys;
    private boolean generateKey;  // set true if auto-generate keys is enabled at "Connection#prepareStatement()"

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return proceedMethodExecution(this.proxyConfig, this.statement, this.connectionInfo, proxy, method, args);
    }

    @Override
    protected Object performProxyLogic(Object proxy, Method method, Object[] args, MethodExecutionContext methodContext) throws Throwable {

        String methodName = method.getName();

        if (!StatementMethodNames.METHODS_TO_INTERCEPT.contains(methodName)) {
            return proceedExecution(method, statement, args);
        }

        QueryTransformer queryTransformer = this.proxyConfig.getQueryTransformer();
        ProxyDataSourceListener queryListener = this.proxyConfig.getListeners();
        JdbcProxyFactory proxyFactory = this.proxyConfig.getJdbcProxyFactory();


        if (isToStringMethod(methodName)) {
            // special treat for toString method
            return handleToStringMethod(this.statement);  // Statement, PreparedStatement, or CallableStatement
        } else if (isGetTargetMethod(methodName)) {
            return statement;  // ProxyJdbcObject interface has a method to return original object.
        } else if (isWrapperMethods(methodName)) {
            // "unwrap", "isWrapperFor"
            return handleWrapperMethods(methodName, this.statement, args);
        }

        // "getConnection"
        if (StatementMethodNames.GET_CONNECTION_METHOD.equals(methodName)) {
            return this.proxyConnection;
        }

        // handle add/clear batch related methods
        if (StatementType.STATEMENT == statementType) {
            if ("addBatch".equals(methodName) || "clearBatch".equals(methodName)) {
                if ("addBatch".equals(methodName)) {
                    String query = (String) args[0];
                    Class<? extends Statement> clazz = Statement.class;
                    int batchCount = batchQueries.size();
                    TransformInfo transformInfo = new TransformInfo(clazz, this.connectionInfo.getDataSourceName(), query, true, batchCount);
                    String transformedQuery = queryTransformer.transformQuery(transformInfo);
                    args[0] = transformedQuery;  // replace to the new query
                    batchQueries.add(transformedQuery);
                } else {  // for "clearBatch" method
                    batchQueries.clear();
                }

                // proceed execution, no need to call listener
                return proceedExecution(method, statement, args);
            }

        } else {
            PreparedStatement ps = (PreparedStatement) this.statement;

            if (StatementMethodNames.METHODS_TO_OPERATE_PARAMETER.contains(methodName)) {

                // for parameter operation method
                if (StatementMethodNames.PARAMETER_METHODS.contains(methodName)) {

                    // operation to set or clear parameterOperationHolder
                    if ("clearParameters".equals(methodName)) {
                        this.parameterSetOperations.clear();
                    } else {

                        ParameterKey parameterKey;
                        if (args[0] instanceof Integer) {
                            parameterKey = new ParameterKey((Integer) args[0]);
                        } else if (args[0] instanceof String) {
                            parameterKey = new ParameterKey((String) args[0]);
                        } else {
                            return proceedExecution(method, ps, args);
                        }

                        // when same key is specified, old value will be overridden
                        this.parameterSetOperations.add(new ParameterSetOperation(parameterKey, method, args));
                    }

                } else if (StatementMethodNames.BATCH_PARAM_METHODS.contains(methodName)) {

                    // Batch parameter operation
                    if ("addBatch".equals(methodName)) {

                        this.batchParameterSetOperations.add(this.parameterSetOperations);

                        // assign new instance for parameter operations on next batch
                        this.parameterSetOperations = new ParameterSetOperations();
                    } else if ("clearBatch".equals(methodName)) {
                        this.batchParameterSetOperations.clear();
                    }
                }

                // proceed execution, no need to call listener
                return proceedExecution(method, ps, args);
            }

        }


        // query execution methods

        List<QueryInfo> queries = new ArrayList<>();
        boolean isBatchExecution = StatementMethodNames.BATCH_EXEC_METHODS.contains(methodName);
        int batchSize = 0;

        // "executeBatch", "executeLargeBatch"
        if (isBatchExecution) {
            if (StatementType.STATEMENT == statementType) {

                for (String batchQuery : batchQueries) {
                    queries.add(new QueryInfo(batchQuery));
                }
                batchSize = batchQueries.size();
                batchQueries.clear();
            } else {
                // one query with multiple parameters
                QueryInfo queryInfo = new QueryInfo(this.query);
                queryInfo.getParameterSetOperations().addAll(this.batchParameterSetOperations);
                queries.add(queryInfo);

                batchSize = this.batchParameterSetOperations.size();
                this.batchParameterSetOperations.clear();
            }

            //  "executeQuery", "executeUpdate", "execute", "executeLargeUpdate"
        } else if (StatementMethodNames.QUERY_EXEC_METHODS.contains(methodName)) {
            QueryInfo queryInfo;
            if (StatementType.STATEMENT == statementType) {
                String query = (String) args[0];
                TransformInfo transformInfo = new TransformInfo(Statement.class, this.connectionInfo.getDataSourceName(), query, false, 0);
                String transformedQuery = queryTransformer.transformQuery(transformInfo);
                args[0] = transformedQuery; // replace to the new query

                queryInfo = new QueryInfo(transformedQuery);
            } else {
                queryInfo = new QueryInfo(this.query);
                queryInfo.getParameterSetOperations().add(this.parameterSetOperations);
            }
            queries.add(queryInfo);
        }

        boolean isGetGeneratedKeysMethod = GET_GENERATED_KEYS_METHOD.equals(methodName);

        // For "getGeneratedKeys()", if auto retrieval is enabled and retrieved resultset is still open, return it from
        // the cache. If it is already closed, then proceed to invoke the actual "getGeneratedKeys()" method.
        if (isGetGeneratedKeysMethod && this.generatedKeys != null) {
            if (this.generatedKeys.isClosed()) {
                this.generatedKeys = null;
            } else {
                return this.generatedKeys;  // return from cache
            }
        }

        Thread beforeQueryThread = Thread.currentThread();
        long beforeQueryThreadId = beforeQueryThread.getId();
        String beforeQueryThreadName = beforeQueryThread.getName();

        QueryExecutionContext queryContext = new QueryExecutionContext(this.connectionInfo, this.statement, isBatchExecution, batchSize, method, args, queries);
        queryContext.setThreadId(beforeQueryThreadId);
        queryContext.setThreadName(beforeQueryThreadName);


        boolean isGetResultSetMethod = GET_RESULTSET_METHOD.equals(methodName);
        boolean performQueryListener = !isGetGeneratedKeysMethod && !isGetResultSetMethod;

        if (performQueryListener) {
            queryListener.beforeQuery(queryContext);
        }

        final Stopwatch stopwatch = this.proxyConfig.getStopwatchFactory().create().start();

        // Invoke method on original Statement.
        try {

            Object retVal = method.invoke(this.statement, args);

            final long elapsedTime = stopwatch.getElapsedTime();

            // method that returns ResultSet but exclude "getGeneratedKeys()"
            boolean isResultSetReturningMethod = !isGetGeneratedKeysMethod && METHODS_TO_RETURN_RESULTSET.contains(methodName);

            boolean isCreateGeneratedKeysProxy = isGetGeneratedKeysMethod && this.proxyConfig.isGeneratedKeysProxyEnabled();
            boolean isCreateResultSetProxy = isResultSetReturningMethod && this.proxyConfig.isResultSetProxyEnabled();

            // create proxy for returned ResultSet
            if (isCreateGeneratedKeysProxy) {
                retVal = proxyFactory.createGeneratedKeys((ResultSet) retVal, this.connectionInfo, this.proxyConfig);
            } else if (isCreateResultSetProxy) {
                retVal = proxyFactory.createResultSet((ResultSet) retVal, this.connectionInfo, this.proxyConfig);
            }


            // cache generated-keys ResultSet if auto-retrieval is enabled
            if (this.proxyConfig.isAutoRetrieveGeneratedKeys()) {

                if (isGetGeneratedKeysMethod) {
                    this.generatedKeys = (ResultSet) retVal;  // result may be proxied
                } else {

                    // for query execution methods:
                    //   execute(), executeUpdate(), executeLargeUpdate(), or executeBatch() or executeLargeBatch()
                    if (GeneratedKeysUtils.isMethodToRetrieveGeneratedKeys(method)) {

                        boolean isTypeStatement = StatementType.STATEMENT == this.statementType;

                        boolean retrieveGeneratedKey;
                        if (isBatchExecution) {
                            if (isTypeStatement) {
                                // batch execution for statement: determined by configuration
                                retrieveGeneratedKey = this.proxyConfig.isRetrieveGeneratedKeysForBatchStatement();
                            } else {
                                // batch execution for prepared or callable: determined by constructor AND configuration
                                retrieveGeneratedKey = this.generateKey && this.proxyConfig.isRetrieveGeneratedKeysForBatchPreparedOrCallable();
                            }
                        } else {
                            if (isTypeStatement) {
                                // execution for statement: determined by method parameter
                                // Statement#[execute|executeUpdate|executeLargeUpdate] with int, int[], or String[] in second arg.
                                retrieveGeneratedKey = GeneratedKeysUtils.isAutoGenerateEnabledParameters(args);
                            } else {
                                // execution for prepared or callable: determined at creation of prepared/callable
                                retrieveGeneratedKey = this.generateKey;
                            }
                        }

                        if (retrieveGeneratedKey) {
                            ResultSet generatedKeysResultSet = this.statement.getGeneratedKeys();  // auto retrieve generated-keys
                            if (this.proxyConfig.isGeneratedKeysProxyEnabled()) {
                                generatedKeysResultSet = proxyFactory.createGeneratedKeys(generatedKeysResultSet, this.connectionInfo, this.proxyConfig);
                            }
                            this.generatedKeys = generatedKeysResultSet;  // cache generated-keys
                        }
                    }
                }
            }

            queryContext.setResult(retVal);
            queryContext.setGeneratedKeys(this.generatedKeys);
            queryContext.setElapsedTime(elapsedTime);
            queryContext.setSuccess(true);

            return retVal;
        } catch (InvocationTargetException ex) {
            final long elapsedTime = stopwatch.getElapsedTime();

            queryContext.setElapsedTime(elapsedTime);
            queryContext.setThrowable(ex.getTargetException());
            queryContext.setSuccess(false);
            throw ex.getTargetException();
        } finally {

            Thread afterQueryThread = Thread.currentThread();
            long afterQueryThreadId = afterQueryThread.getId();
            String afterQueryThreadName = afterQueryThread.getName();
            queryContext.setThreadId(afterQueryThreadId);
            queryContext.setThreadName(afterQueryThreadName);

            if (performQueryListener) {
                queryListener.afterQuery(queryContext);
            }

            // auto-close the auto-retrieved generated keys. result of "getGeneratedKeys()" should not be affected.
            if (!isGetGeneratedKeysMethod && this.proxyConfig.isAutoCloseGeneratedKeys()
                    && this.generatedKeys != null && !this.generatedKeys.isClosed()) {
                this.generatedKeys.close();
                this.generatedKeys = null;
            }

        }
    }

}
