package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.MethodExecutionListenerUtils;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.transform.QueryTransformer;
import net.ttddyy.dsproxy.transform.TransformInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.ttddyy.dsproxy.proxy.StatementMethodNames.EXEC_METHODS;
import static net.ttddyy.dsproxy.proxy.StatementMethodNames.GET_GENERATED_KEYS_METHOD;
import static net.ttddyy.dsproxy.proxy.StatementMethodNames.METHODS_TO_RETURN_RESULTSET;

/**
 * Proxy Logic implementation for {@link Statement} methods.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.2
 */
public class StatementProxyLogic {

    public static class Builder {
        private Statement stmt;
        private ConnectionInfo connectionInfo;
        private Connection proxyConnection;
        private ProxyConfig proxyConfig;

        public static Builder create() {
            return new Builder();
        }

        public StatementProxyLogic build() {
            StatementProxyLogic logic = new StatementProxyLogic();
            logic.stmt = this.stmt;
            logic.connectionInfo = this.connectionInfo;
            logic.proxyConnection = this.proxyConnection;
            logic.proxyConfig = this.proxyConfig;
            return logic;
        }

        public Builder statement(Statement statement) {
            this.stmt = statement;
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

    private static final Set<String> METHODS_TO_INTERCEPT = Collections.unmodifiableSet(
            new HashSet<String>() {
                {
                    addAll(StatementMethodNames.BATCH_PARAM_METHODS);
                    addAll(StatementMethodNames.EXEC_METHODS);
                    addAll(StatementMethodNames.JDBC4_METHODS);
                    addAll(StatementMethodNames.GET_CONNECTION_METHOD);
                    addAll(METHODS_TO_RETURN_RESULTSET);
                    add("getDataSourceName");
                    add("toString");
                    add("getTarget"); // from ProxyJdbcObject
                }
            }
    );

    private Statement stmt;
    private ConnectionInfo connectionInfo;
    private List<String> batchQueries = new ArrayList<String>();
    private Connection proxyConnection;
    private ProxyConfig proxyConfig;
    private ResultSet generatedKeys;


    public Object invoke(Method method, Object[] args) throws Throwable {

        return MethodExecutionListenerUtils.invoke(new MethodExecutionListenerUtils.MethodExecutionCallback() {
            @Override
            public Object execute(Object proxyTarget, Method method, Object[] args) throws Throwable {
                return performQueryExecutionListener(method, args);
            }
        }, this.proxyConfig, this.stmt, this.connectionInfo, method, args);

    }

    private Object performQueryExecutionListener(Method method, Object[] args) throws Throwable {

        final String methodName = method.getName();

        if (!METHODS_TO_INTERCEPT.contains(methodName)) {
            return MethodUtils.proceedExecution(method, stmt, args);
        }

        // special treat for toString method
        if ("toString".equals(methodName)) {
            final StringBuilder sb = new StringBuilder();
            sb.append(stmt.getClass().getSimpleName());
            sb.append(" [");
            sb.append(stmt.toString());
            sb.append("]");
            return sb.toString(); // differentiate toString message.
        } else if ("getDataSourceName".equals(methodName)) {
            return this.connectionInfo.getDataSourceName();
        } else if ("getTarget".equals(methodName)) {
            // ProxyJdbcObject interface has method to return original object.
            return stmt;
        }

        QueryExecutionListener queryListener = this.proxyConfig.getQueryListener();
        QueryTransformer queryTransformer = this.proxyConfig.getQueryTransformer();
        JdbcProxyFactory proxyFactory = this.proxyConfig.getJdbcProxyFactory();

        if (StatementMethodNames.JDBC4_METHODS.contains(methodName)) {
            final Class<?> clazz = (Class<?>) args[0];
            if ("unwrap".equals(methodName)) {
                return stmt.unwrap(clazz);
            } else if ("isWrapperFor".equals(methodName)) {
                return stmt.isWrapperFor(clazz);
            }
        }

        if (StatementMethodNames.GET_CONNECTION_METHOD.contains(methodName)) {
            return this.proxyConnection;
        }

        if ("addBatch".equals(methodName) || "clearBatch".equals(methodName)) {
            if ("addBatch".equals(methodName) && ObjectArrayUtils.isFirstArgString(args)) {
                final String query = (String) args[0];
                final Class<? extends Statement> clazz = Statement.class;
                final int batchCount = batchQueries.size();
                final TransformInfo transformInfo = new TransformInfo(clazz, this.connectionInfo.getDataSourceName(), query, true, batchCount);
                final String transformedQuery = queryTransformer.transformQuery(transformInfo);
                args[0] = transformedQuery;  // replace to the new query
                batchQueries.add(transformedQuery);
            } else if ("clearBatch".equals(methodName)) {
                batchQueries.clear();
            }

            // proceed execution, no need to call listener
            try {
                return method.invoke(stmt, args);
            } catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }


        final List<QueryInfo> queries = new ArrayList<QueryInfo>();
        boolean isBatchExecute = false;
        int batchSize = 0;

        if (StatementMethodNames.BATCH_EXEC_METHODS.contains(methodName)) {

            for (String batchQuery : batchQueries) {
                queries.add(new QueryInfo(batchQuery));
            }
            batchSize = batchQueries.size();
            batchQueries.clear();
            isBatchExecute = true;

        } else if (StatementMethodNames.QUERY_EXEC_METHODS.contains(methodName)) {

            if (ObjectArrayUtils.isFirstArgString(args)) {
                final String query = (String) args[0];
                final TransformInfo transformInfo = new TransformInfo(Statement.class, this.connectionInfo.getDataSourceName(), query, false, 0);
                final String transformedQuery = queryTransformer.transformQuery(transformInfo);
                args[0] = transformedQuery; // replace to the new query
                queries.add(new QueryInfo(transformedQuery));
            }
        }

        final boolean isGetGeneratedKeysMethod = GET_GENERATED_KEYS_METHOD.equals(methodName);

        // For "getGeneratedKeys()", if auto retrieval is enabled and retrieved resultset is still open, return it from
        // the cache. If it is already closed, then proceed to invoke the actual "getGeneratedKeys()" method.
        if (isGetGeneratedKeysMethod && this.generatedKeys != null) {
            if (this.generatedKeys.isClosed()) {
                this.generatedKeys = null;
            } else {
                return this.generatedKeys;  // return from cache
            }
        }

        final ExecutionInfo execInfo = new ExecutionInfo(this.connectionInfo, this.stmt, isBatchExecute, batchSize, method, args);

        queryListener.beforeQuery(execInfo, queries);

        // Invoke method on original Statement.
        try {
            final long beforeTime = System.currentTimeMillis();

            Object retVal = method.invoke(this.stmt, args);

            final long afterTime = System.currentTimeMillis();


            // method that returns ResultSet but exclude "getGeneratedKeys()"
            final boolean isResultSetReturningMethod = !isGetGeneratedKeysMethod && METHODS_TO_RETURN_RESULTSET.contains(methodName);

            final boolean isCreateGeneratedKeysProxy = isGetGeneratedKeysMethod && this.proxyConfig.isGeneratedKeysProxyEnabled();
            final boolean isCreateResultSetProxy = isResultSetReturningMethod && this.proxyConfig.isResultSetProxyEnabled();

            // create proxy for returned ResultSet
            if (isCreateGeneratedKeysProxy) {
                retVal = proxyFactory.createGeneratedKeys((ResultSet) retVal, this.connectionInfo, this.proxyConfig);
            } else if (isCreateResultSetProxy) {
                retVal = proxyFactory.createResultSet((ResultSet) retVal, this.connectionInfo, this.proxyConfig);
            }


            // cache generated-keys ResultSet if auto-retrieval is enabled
            if (this.proxyConfig.isAutoRetrieveGeneratedKeys()) {
                final boolean isQueryExecutionMethod = EXEC_METHODS.contains(methodName);

                if (isGetGeneratedKeysMethod) {
                    this.generatedKeys = (ResultSet) retVal;  // result may be proxied
                } else if (isQueryExecutionMethod) {
                    ResultSet generatedKeysResultSet = this.stmt.getGeneratedKeys();  // auto retrieve generated-keys
                    if (this.proxyConfig.isGeneratedKeysProxyEnabled()) {
                        generatedKeysResultSet = proxyFactory.createGeneratedKeys(generatedKeysResultSet, this.connectionInfo, this.proxyConfig);
                    }
                    this.generatedKeys = generatedKeysResultSet;  // cache generated-keys
                }
            }

            execInfo.setResult(retVal);
            execInfo.setGeneratedKeys(this.generatedKeys);
            execInfo.setElapsedTime(afterTime - beforeTime);
            execInfo.setSuccess(true);

            return retVal;
        } catch (InvocationTargetException ex) {
            execInfo.setThrowable(ex.getTargetException());
            execInfo.setSuccess(false);
            throw ex.getTargetException();
        } finally {
            queryListener.afterQuery(execInfo, queries);

            // auto-close the auto-retrieved generated keys. result of "getGeneratedKeys()" should not be affected.
            if (!isGetGeneratedKeysMethod && this.proxyConfig.isAutoCloseGeneratedKeys()
                    && this.generatedKeys != null && !this.generatedKeys.isClosed()) {
                this.generatedKeys.close();
                this.generatedKeys = null;
            }

        }

    }

}
