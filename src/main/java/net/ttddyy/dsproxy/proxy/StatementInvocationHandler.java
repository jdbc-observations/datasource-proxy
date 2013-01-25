package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.transform.QueryTransformer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Statement;
import java.util.*;

/**
 * Proxy InvocationHandler for {@link java.sql.Statement}.
 *
 * @author Tadaya Tsuyukubo
 */
public class StatementInvocationHandler implements InvocationHandler {

    private static final Set<String> BATCH_PARAM_METHODS = Collections.unmodifiableSet(
            new HashSet<String>(Arrays.asList("addBatch", "clearBatch"))
    );

    private static final Set<String> EXEC_METHODS = Collections.unmodifiableSet(
            new HashSet<String>(Arrays.asList("executeBatch", "executeQuery", "executeUpdate", "execute"))
    );

    private static final Set<String> JDBC4_METHODS = Collections.unmodifiableSet(
            new HashSet<String>(Arrays.asList("unwrap", "isWrapperFor"))
    );

    private static final Set<String> GET_CONNECTION_METHOD = Collections.unmodifiableSet(
            new HashSet<String>(Arrays.asList("getConnection"))
    );

    private static final Set<String> METHODS_TO_INTERCEPT = Collections.unmodifiableSet(
            new HashSet<String>() {
                {
                    addAll(BATCH_PARAM_METHODS);
                    addAll(EXEC_METHODS);
                    addAll(JDBC4_METHODS);
                    addAll(GET_CONNECTION_METHOD);
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

    public StatementInvocationHandler(Statement stmt) {
        this.stmt = stmt;
    }

    @Deprecated
    public StatementInvocationHandler(Statement stmt, QueryExecutionListener listener) {
        this.stmt = stmt;
        this.interceptorHolder = new InterceptorHolder(listener, QueryTransformer.DEFAULT);
    }

    @Deprecated
    public StatementInvocationHandler(
            Statement stmt, QueryExecutionListener listener, String dataSourceName, JdbcProxyFactory jdbcProxyFactory) {
        this.stmt = stmt;
        this.interceptorHolder = new InterceptorHolder(listener, QueryTransformer.DEFAULT);
        this.dataSourceName = dataSourceName;
        this.jdbcProxyFactory = jdbcProxyFactory;
    }

    public StatementInvocationHandler(
            Statement stmt, InterceptorHolder interceptorHolder, String dataSourceName, JdbcProxyFactory jdbcProxyFactory) {
        this.stmt = stmt;
        this.interceptorHolder = interceptorHolder;
        this.dataSourceName = dataSourceName;
        this.jdbcProxyFactory = jdbcProxyFactory;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        final String methodName = method.getName();

        if (!METHODS_TO_INTERCEPT.contains(methodName)) {
            return MethodUtils.proceedExecution(method, stmt, args);
        }

        // special treat for toString method
        if ("toString".equals(methodName)) {
            StringBuilder sb = new StringBuilder("StatementInvocationHandler [");
            sb.append(stmt.toString());
            sb.append("]");
            return sb.toString(); // differentiate toString message.
        } else if ("getDataSourceName".equals(methodName)) {
            return dataSourceName;
        } else if ("getTarget".equals(methodName)) {
            // ProxyJdbcObject interface has method to return original object.
            return stmt;
        }

        if (JDBC4_METHODS.contains(methodName)) {
            final Class<?> clazz = (Class<?>) args[0];
            if ("unwrap".equals(methodName)) {
                return stmt.unwrap(clazz);
            } else if ("isWrapperFor".equals(methodName)) {
                return stmt.isWrapperFor(clazz);
            }
        }

        if (GET_CONNECTION_METHOD.contains(methodName)) {
            final Connection conn = (Connection) MethodUtils.proceedExecution(method, stmt, args);
            return jdbcProxyFactory.createConnection(conn, interceptorHolder, dataSourceName);
        }

        if ("addBatch".equals(methodName) || "clearBatch".equals(methodName)) {
            if ("addBatch".equals(methodName) && ObjectArrayUtils.isFirstArgString(args)) {
                final QueryTransformer queryTransformer = interceptorHolder.getQueryTransformer();
                final String query = (String) args[0];
                final String transformedQuery = queryTransformer.transformQuery(dataSourceName, query);
                args[0] = transformedQuery;  // replace to the new query
                batchQueries.add(transformedQuery);
            } else if ("clearBatch".equals(methodName)) {
                batchQueries.clear();
            }

            // proceed execution, no need to call listener
            try {
                return method.invoke(stmt, args);
            }
            catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }


        final List<QueryInfo> queries = new ArrayList<QueryInfo>();

        if ("executeBatch".equals(methodName)) {
            for (String batchQuery : batchQueries) {
                queries.add(new QueryInfo(batchQuery, null));
            }
        } else if ("executeQuery".equals(methodName) || "executeUpdate".equals(methodName)
                || "execute".equals(methodName)) {

            if (ObjectArrayUtils.isFirstArgString(args)) {
                final QueryTransformer queryTransformer = interceptorHolder.getQueryTransformer();
                final String query = (String) args[0];
                final String transformedQuery = queryTransformer.transformQuery(dataSourceName, query);
                args[0] = transformedQuery; // replace to the new query
                queries.add(new QueryInfo(transformedQuery, null));
            }
        }

        final QueryExecutionListener listener = interceptorHolder.getListener();
        listener.beforeQuery(new ExecutionInfo(dataSourceName, method, args), queries);

        final ExecutionInfo execInfo = new ExecutionInfo(dataSourceName, method, args);
        // Invoke method on original Statement.
        try {
            final long beforeTime = System.currentTimeMillis();

            Object retVal = method.invoke(stmt, args);

            final long afterTime = System.currentTimeMillis();
            execInfo.setResult(retVal);
            execInfo.setElapsedTime(afterTime - beforeTime);

            return retVal;
        } catch (InvocationTargetException ex) {
            execInfo.setThrowable(ex.getTargetException());
            throw ex.getTargetException();
        } finally {
            listener.afterQuery(execInfo, queries);
        }

    }

}
