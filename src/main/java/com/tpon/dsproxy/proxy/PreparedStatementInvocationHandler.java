package com.tpon.dsproxy.proxy;

import com.tpon.dsproxy.ExecutionInfo;
import com.tpon.dsproxy.QueryInfo;
import com.tpon.dsproxy.listener.QueryExecutionListener;

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
 * @author Tadaya Tsuyukubo
 */
public class PreparedStatementInvocationHandler implements InvocationHandler {

    private static final Set<String> PARAMETER_METHODS = Collections.unmodifiableSet(
            new HashSet<String>(Arrays.asList("setArray", "setAsciiStream", "setBigDecimal",
                    "setBinaryStream", "setBlob", "setBoolean", "setByte",
                    "setBytes", "setCharacterStream", "setClob", "setDate",
                    "setDouble", "setFloat", "setInt", "setLong",
                    "setNull", "setObject", "setRef", "setShort",
                    "setString", "setTime", "setTimestamp", "setUnicodeStream", "setURL",
                    "clearParameters"
            ))
    );

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
                    addAll(PARAMETER_METHODS);
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

    private static final Set<String> METHODS_TO_OPERATE_PARAMETER = Collections.unmodifiableSet(
            new HashSet<String>() {
                {
                    addAll(PARAMETER_METHODS);
                    addAll(BATCH_PARAM_METHODS);
                }
            }
    );

    private PreparedStatement ps;
    private String query;
    private String dataSourceName;
    private SortedMap<Integer, Object> queryParams = new TreeMap<Integer, Object>(); // sorted by key
    private QueryExecutionListener listener;

    private List<BatchQueryHolder> batchQueries = new ArrayList<BatchQueryHolder>();

    public PreparedStatementInvocationHandler(PreparedStatement ps, String query) {
        this.ps = ps;
        this.query = query;
    }

    public PreparedStatementInvocationHandler(PreparedStatement ps, String query, QueryExecutionListener listener) {
        this.ps = ps;
        this.query = query;
        this.listener = listener;
    }

    public PreparedStatementInvocationHandler(
            PreparedStatement ps, String query, QueryExecutionListener listener, String dataSourceName) {
        this.ps = ps;
        this.query = query;
        this.listener = listener;
        this.dataSourceName = dataSourceName;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        final String methodName = method.getName();

        if (!METHODS_TO_INTERCEPT.contains(methodName)) {
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

        if (JDBC4_METHODS.contains(methodName)) {
            final Class<?> clazz = (Class<?>) args[0];
            if ("unwrap".equals(methodName)) {
                return ps.unwrap(clazz);
            } else if ("isWrapperFor".equals(methodName)) {
                return ps.isWrapperFor(clazz);
            }
        }

        if (GET_CONNECTION_METHOD.contains(methodName)) {
            final Connection conn = (Connection) MethodUtils.proceedExecution(method, ps, args);
            return JdbcProxyFactory.createConnection(conn, listener, dataSourceName);
        }


        if (METHODS_TO_OPERATE_PARAMETER.contains(methodName)) {

            // parameter setting operation. for now ignore 3rd argument
            if (PARAMETER_METHODS.contains(methodName)) {

                if ("clearParameters".equals(methodName)) {
                    queryParams.clear();
                } else {
                    final Integer paramIndex = (Integer) args[0];
                    final Object paramValue = args[1];
                    queryParams.put(paramIndex, paramValue);
                }

            } else if (BATCH_PARAM_METHODS.contains(methodName)) {

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
