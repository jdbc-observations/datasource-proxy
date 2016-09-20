package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.transform.QueryTransformer;
import net.ttddyy.dsproxy.transform.TransformInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Statement;
import java.util.*;

/**
 * Proxy Logic implementation for {@link Statement} methods.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.2
 */
public class StatementProxyLogic {

    private static final Set<String> METHODS_TO_INTERCEPT = Collections.unmodifiableSet(
            new HashSet<String>() {
                {
                    addAll(StatementMethodNames.BATCH_PARAM_METHODS);
                    addAll(StatementMethodNames.EXEC_METHODS);
                    addAll(StatementMethodNames.JDBC4_METHODS);
                    addAll(StatementMethodNames.GET_CONNECTION_METHOD);
                    add("getDataSourceName");
                    add("toString");
                    add("getTarget"); // from ProxyJdbcObject
                }
            }
    );

    private Statement stmt;
    private InterceptorHolder interceptorHolder;
    private String dataSourceName;
    private List<String> batchQueries = new ArrayList<String>();
    private JdbcProxyFactory jdbcProxyFactory = JdbcProxyFactory.DEFAULT;

    public StatementProxyLogic() {
    }

    public StatementProxyLogic(
            Statement stmt, InterceptorHolder interceptorHolder, String dataSourceName, JdbcProxyFactory jdbcProxyFactory) {
        this.stmt = stmt;
        this.interceptorHolder = interceptorHolder;
        this.dataSourceName = dataSourceName;
        this.jdbcProxyFactory = jdbcProxyFactory;
    }

    public Object invoke(Method method, Object[] args) throws Throwable {

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
            return dataSourceName;
        } else if ("getTarget".equals(methodName)) {
            // ProxyJdbcObject interface has method to return original object.
            return stmt;
        }

        if (StatementMethodNames.JDBC4_METHODS.contains(methodName)) {
            final Class<?> clazz = (Class<?>) args[0];
            if ("unwrap".equals(methodName)) {
                return stmt.unwrap(clazz);
            } else if ("isWrapperFor".equals(methodName)) {
                return stmt.isWrapperFor(clazz);
            }
        }

        if (StatementMethodNames.GET_CONNECTION_METHOD.contains(methodName)) {
            final Connection conn = (Connection) MethodUtils.proceedExecution(method, stmt, args);
            return jdbcProxyFactory.createConnection(conn, interceptorHolder, dataSourceName);
        }

        if ("addBatch".equals(methodName) || "clearBatch".equals(methodName)) {
            if ("addBatch".equals(methodName) && ObjectArrayUtils.isFirstArgString(args)) {
                final QueryTransformer queryTransformer = interceptorHolder.getQueryTransformer();
                final String query = (String) args[0];
                final Class<? extends Statement> clazz = Statement.class;
                final int batchCount = batchQueries.size();
                final TransformInfo transformInfo = new TransformInfo(clazz, dataSourceName, query, true, batchCount);
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
                final QueryTransformer queryTransformer = interceptorHolder.getQueryTransformer();
                final String query = (String) args[0];
                final TransformInfo transformInfo = new TransformInfo(Statement.class, dataSourceName, query, false, 0);
                final String transformedQuery = queryTransformer.transformQuery(transformInfo);
                args[0] = transformedQuery; // replace to the new query
                queries.add(new QueryInfo(transformedQuery));
            }
        }

        final QueryExecutionListener listener = interceptorHolder.getListener();
        listener.beforeQuery(new ExecutionInfo(dataSourceName, this.stmt, isBatchExecute, batchSize, method, args), queries);

        final ExecutionInfo execInfo = new ExecutionInfo(dataSourceName, this.stmt, isBatchExecute, batchSize, method, args);
        // Invoke method on original Statement.
        try {
            final long beforeTime = System.currentTimeMillis();

            Object retVal = method.invoke(stmt, args);

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

}
