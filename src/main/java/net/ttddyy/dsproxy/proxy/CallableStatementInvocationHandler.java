package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Proxy InvocationHandler for {@link java.sql.CallableStatement}.
 *
 * @author Tadaya Tsuyukubo
 */
public class CallableStatementInvocationHandler implements InvocationHandler {

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
            new HashSet<String>(Arrays.asList("executeBatch", "executeQuery",
                    "executeUpdate", "execute"))
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
                    add("toString");
                    add("getDataSourceName");
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

    private CallableStatement cs;
    private String query;
    private String dataSourceName;
    private SortedMap<Object, Object> queryParams = new TreeMap<Object, Object>(); // sorted by key(int or String)
    private QueryExecutionListener listener;
    private JdbcProxyFactory jdbcProxyFactory = JdbcProxyFactory.DEFAULT;

    private List<BatchQueryHolder> batchQueries = new ArrayList<BatchQueryHolder>();

    public CallableStatementInvocationHandler() {
    }

    public CallableStatementInvocationHandler(
            CallableStatement cs, String query, QueryExecutionListener listener, String dataSourceName, JdbcProxyFactory jdbcProxyFactory) {
        this.cs = cs;
        this.query = query;
        this.listener = listener;
        this.dataSourceName = dataSourceName;
        this.jdbcProxyFactory = jdbcProxyFactory;
    }


    public Object invoke(Object o, Method method, Object[] args) throws Throwable {

        final String methodName = method.getName();

        if (!METHODS_TO_INTERCEPT.contains(methodName)) {
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

        if (JDBC4_METHODS.contains(methodName)) {
            final Class<?> clazz = (Class<?>) args[0];
            if ("unwrap".equals(methodName)) {
                return cs.unwrap(clazz);
            } else if ("isWrapperFor".equals(methodName)) {
                return cs.isWrapperFor(clazz);
            }
        }

        if (GET_CONNECTION_METHOD.contains(methodName)) {
            final Connection conn = (Connection) MethodUtils.proceedExecution(method, cs, args);
            return jdbcProxyFactory.createConnection(conn, listener, dataSourceName);
        }


        if (METHODS_TO_OPERATE_PARAMETER.contains(methodName)) {
            // for parameter operation method

            if (PARAMETER_METHODS.contains(methodName)) {

                // operation to set or clear parameters
                if ("clearParameters".equals(methodName)) {
                    queryParams.clear();
                } else {
                    final Object paramKey = args[0]; // key can be int or string
                    final Object paramValue = args[1];
                    queryParams.put(paramKey, paramValue);
                }

            } else if (BATCH_PARAM_METHODS.contains(methodName)) {

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
