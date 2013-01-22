package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.transform.QueryTransformer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Proxy InvocationHandler for {@link java.sql.PreparedStatement}.
 *
 * @author Tadaya Tsuyukubo
 */
public class PreparedStatementInvocationHandler implements InvocationHandler {

    private PreparedStatement ps;
    private String query;
    private String dataSourceName;
    private SortedMap<Integer, Object> queryParams = new TreeMap<Integer, Object>(); // sorted by key
    private InterceptorHolder interceptorHolder;
    private JdbcProxyFactory jdbcProxyFactory = JdbcProxyFactory.DEFAULT;

    private List<BatchQueryHolder> batchQueries = new ArrayList<BatchQueryHolder>();

    public PreparedStatementInvocationHandler(PreparedStatement ps, String query) {
        this.ps = ps;
        this.query = query;
    }

    @Deprecated
    public PreparedStatementInvocationHandler(PreparedStatement ps, String query, QueryExecutionListener listener) {
        this.ps = ps;
        this.query = query;
        this.interceptorHolder = new InterceptorHolder(listener, QueryTransformer.DEFAULT);
    }

    @Deprecated
    public PreparedStatementInvocationHandler(
            PreparedStatement ps, String query, QueryExecutionListener listener, String dataSourceName, JdbcProxyFactory jdbcProxyFactory) {
        this.ps = ps;
        this.query = query;
        this.interceptorHolder = new InterceptorHolder(listener, QueryTransformer.DEFAULT);
        this.dataSourceName = dataSourceName;
        this.jdbcProxyFactory = jdbcProxyFactory;
    }

    public PreparedStatementInvocationHandler(
            PreparedStatement ps, String query, InterceptorHolder interceptorHolder, String dataSourceName, JdbcProxyFactory jdbcProxyFactory) {
        this.ps = ps;
        this.query = query;
        this.interceptorHolder = interceptorHolder;
        this.dataSourceName = dataSourceName;
        this.jdbcProxyFactory = jdbcProxyFactory;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        final String methodName = method.getName();

        if (!StatementMethodNames.METHODS_TO_INTERCEPT.contains(methodName)) {
            return MethodUtils.proceedExecution(method, ps, args);
        }

        // special treat for toString method
        if ("toString".equals(methodName)) {
            StringBuilder sb = new StringBuilder("PreparedStatementInvocationHandler [");
            sb.append(ps.toString());
            sb.append("]");
            return sb.toString(); // differentiate toString message.
        } else if ("getDataSourceName".equals(methodName)) {
            return dataSourceName;
        } else if ("getTarget".equals(methodName)) {
            // ProxyJdbcObject interface has method to return original object.
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

            // parameter setting operation. for now ignore 3rd argument
            if (StatementMethodNames.PARAMETER_METHODS.contains(methodName)) {

                if ("clearParameters".equals(methodName)) {
                    queryParams.clear();
                } else {
                    final Integer paramIndex = (Integer) args[0];
                    final Object paramValue = args[1];
                    queryParams.put(paramIndex, paramValue);
                }

            } else if (StatementMethodNames.BATCH_PARAM_METHODS.contains(methodName)) {

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
            return MethodUtils.proceedExecution(method, ps, args);
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

            Object retVal = method.invoke(ps, args);

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
