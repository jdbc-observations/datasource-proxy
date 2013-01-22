package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.transform.QueryTransformer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.*;

/**
 * Proxy InvocationHandler for {@link java.sql.CallableStatement}.
 *
 * @author Tadaya Tsuyukubo
 */
public class CallableStatementInvocationHandler implements InvocationHandler {

    private CallableStatement cs;
    private String query;
    private String dataSourceName;
    private SortedMap<Object, Object> queryParams = new TreeMap<Object, Object>(); // sorted by key(int or String)
    private InterceptorHolder interceptorHolder;
    private JdbcProxyFactory jdbcProxyFactory = JdbcProxyFactory.DEFAULT;

    private List<BatchQueryHolder> batchQueries = new ArrayList<BatchQueryHolder>();

    public CallableStatementInvocationHandler() {
    }

    @Deprecated
    public CallableStatementInvocationHandler(
            CallableStatement cs, String query, QueryExecutionListener listener, String dataSourceName, JdbcProxyFactory jdbcProxyFactory) {
        this.cs = cs;
        this.query = query;
        this.interceptorHolder = new InterceptorHolder(listener, QueryTransformer.DEFAULT);
        this.dataSourceName = dataSourceName;
        this.jdbcProxyFactory = jdbcProxyFactory;
    }

    public CallableStatementInvocationHandler(
            CallableStatement cs, String query, InterceptorHolder interceptorHolder, String dataSourceName, JdbcProxyFactory jdbcProxyFactory) {
        this.cs = cs;
        this.query = query;
        this.interceptorHolder = interceptorHolder;
        this.dataSourceName = dataSourceName;
        this.jdbcProxyFactory = jdbcProxyFactory;
    }


    public Object invoke(Object o, Method method, Object[] args) throws Throwable {

        final String methodName = method.getName();

        if (!StatementMethodNames.METHODS_TO_INTERCEPT.contains(methodName)) {
            return MethodUtils.proceedExecution(method, cs, args);
        }

        // special treat for toString method
        if ("toString".equals(methodName)) {
            final StringBuilder sb = new StringBuilder("CallableStatementInvocationHandler [");
            sb.append(cs.toString());
            sb.append("]");
            return sb.toString(); // differentiate toString message.
        } else if ("getDataSourceName".equals(methodName)) {
            return dataSourceName;
        } else if ("getTarget".equals(methodName)) {
            // ProxyJdbcObject interface has a method to return original object.
            return cs;
        }

        if (StatementMethodNames.JDBC4_METHODS.contains(methodName)) {
            final Class<?> clazz = (Class<?>) args[0];
            if ("unwrap".equals(methodName)) {
                return cs.unwrap(clazz);
            } else if ("isWrapperFor".equals(methodName)) {
                return cs.isWrapperFor(clazz);
            }
        }

        if (StatementMethodNames.GET_CONNECTION_METHOD.contains(methodName)) {
            final Connection conn = (Connection) MethodUtils.proceedExecution(method, cs, args);
            return jdbcProxyFactory.createConnection(conn, interceptorHolder, dataSourceName);
        }


        if (StatementMethodNames.METHODS_TO_OPERATE_PARAMETER.contains(methodName)) {
            // for parameter operation method

            if (StatementMethodNames.PARAMETER_METHODS.contains(methodName)) {

                // operation to set or clear parameters
                if ("clearParameters".equals(methodName)) {
                    queryParams.clear();
                } else {
                    final Object paramKey = args[0]; // key can be int or string
                    final Object paramValue = args[1];
                    queryParams.put(paramKey, paramValue);
                }

            } else if (StatementMethodNames.BATCH_PARAM_METHODS.contains(methodName)) {

                // Batch parameter operation
                if ("addBatch".equals(methodName)) {
                    BatchQueryHolder queryHolder = new BatchQueryHolder();
                    queryHolder.setQuery(query);
                    queryHolder.setArgs(new ArrayList<Object>(queryParams.values()));
                    batchQueries.add(queryHolder);
                } else if ("clearBatch".equals(methodName)) {
                    batchQueries.clear();
                }
            }

            // proceed execution, no need to call listener
            return MethodUtils.proceedExecution(method, cs, args);
        }


        // query execution methods

        final List<QueryInfo> queries = new ArrayList<QueryInfo>();

        if ("executeBatch".equals(methodName)) {
            for (BatchQueryHolder queryHolder : batchQueries) {
                queries.add(new QueryInfo(queryHolder.getQuery(), queryHolder.getArgs()));
            }
        } else if ("executeQuery".equals(methodName) || "executeUpdate".equals(methodName)
                || "execute".equals(methodName)) {

            queries.add(new QueryInfo(query, new ArrayList<Object>(queryParams.values())));
        }

        final QueryExecutionListener listener = interceptorHolder.getListener();
        listener.beforeQuery(new ExecutionInfo(dataSourceName, method, args), queries);

        // Invoke method on original Statement.
        final ExecutionInfo execInfo = new ExecutionInfo(dataSourceName, method, args);

        try {
            final long beforeTime = System.currentTimeMillis();

            Object retVal = method.invoke(cs, args);

            final long afterTime = System.currentTimeMillis();

            execInfo.setResult(retVal);
            execInfo.setElapsedTime(afterTime - beforeTime);

            return retVal;
        }
        catch (InvocationTargetException ex) {
            execInfo.setThrowable(ex.getTargetException());
            throw ex.getTargetException();
        } finally {
            listener.afterQuery(execInfo, queries);
        }
    }
}
