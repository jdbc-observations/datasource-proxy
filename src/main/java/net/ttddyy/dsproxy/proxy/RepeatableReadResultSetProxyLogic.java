package net.ttddyy.dsproxy.proxy;

import java.util.HashMap;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.listener.MethodExecutionContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.Boolean.TRUE;
import static java.lang.String.format;

/**
 * Allows {@link java.sql.ResultSet} to be consumed more than once.
 *
 * @author Liam Williams
 * @author Réda Housni Alaoui
 * @see net.ttddyy.dsproxy.proxy.jdk.ResultSetInvocationHandler
 * @since 1.4
 */
public class RepeatableReadResultSetProxyLogic extends ProxyLogicSupport implements ResultSetProxyLogic {

    private static final Set<String> METHODS_TO_INTERCEPT = Collections.unmodifiableSet(
            new HashSet<String>() {
                {
                    // getDeclaredMethods does NOT include parent class methods(e.g: Wrapper#unwrap()"
                    for (Method method : ResultSet.class.getDeclaredMethods()) {
                        add(method.getName());
                    }
                    add("toString");
                    add("getTarget"); // from ProxyJdbcObject
                }
            }
    );

    private static final Map<String, Method> NUMBER_X_VALUE_METHOD_PER_NUMERIC_TYPE = Collections.unmodifiableMap(new HashMap<String, Method>() {
        private static final String METHOD_SUFFIX = "Value";

        {
            for (Method method : Number.class.getDeclaredMethods()) {
                String methodName = method.getName();
                if (!methodName.endsWith(METHOD_SUFFIX)) {
                    continue;
                }
                put(methodName.substring(0, methodName.indexOf(METHOD_SUFFIX)), method);
            }
        }
    });

    private static final Object UNCONSUMED_RESULT_COLUMN = new Object();

    public static class Builder {
        private ResultSet resultSet;
        private ConnectionInfo connectionInfo;
        private ProxyConfig proxyConfig;
        private Map<String, Integer> columnNameToIndex;
        private int columnCount;

        public static Builder create() {
            return new Builder();
        }

        public RepeatableReadResultSetProxyLogic build() {
            RepeatableReadResultSetProxyLogic logic = new RepeatableReadResultSetProxyLogic();
            logic.resultSet = this.resultSet;
            logic.connectionInfo = this.connectionInfo;
            logic.proxyConfig = this.proxyConfig;
            logic.columnNameToIndex = this.columnNameToIndex;
            logic.columnCount = this.columnCount;
            return logic;
        }

        public Builder resultSet(ResultSet resultSet) {
            this.resultSet = resultSet;
            return this;
        }

        public Builder connectionInfo(ConnectionInfo connectionInfo) {
            this.connectionInfo = connectionInfo;
            return this;
        }

        public Builder proxyConfig(ProxyConfig proxyConfig) {
            this.proxyConfig = proxyConfig;
            return this;
        }

        public Builder columnNameToIndex(Map<String, Integer> columnNameToIndex) {
            this.columnNameToIndex = columnNameToIndex;
            return this;
        }

        public Builder columnCount(int columnCount) {
            this.columnCount = columnCount;
            return this;
        }
    }

    private Map<String, Integer> columnNameToIndex;
    private ResultSet resultSet;
    private ConnectionInfo connectionInfo;
    private int columnCount;
    private ProxyConfig proxyConfig;

    private int resultPointer;
    private boolean resultSetConsumed;
    private boolean closed;
    private Object[] currentResult;
    private final List<Object[]> cachedResults = new ArrayList<Object[]>();

    private boolean wasNull;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return proceedMethodExecution(this.proxyConfig, this.resultSet, this.connectionInfo, proxy, method, args);
    }

    @Override
    protected Object performProxyLogic(Object proxy, Method method, Object[] args, MethodExecutionContext methodContext) throws Throwable {
        final String methodName = method.getName();

        if (!METHODS_TO_INTERCEPT.contains(methodName)) {
            return proceedExecution(method, this.resultSet, args);
        }

        if (isCommonMethod(methodName)) {
            return handleCommonMethod(methodName, this.resultSet, this.connectionInfo, args);
        } else if (methodName.equals("getMetaData")) {
            return proceedExecution(method, this.resultSet, args);
        } else if (methodName.equals("close")) {
            this.closed = true;
            return proceedExecution(method, this.resultSet, args);
        } else if (methodName.equals("isClosed")) {
            return proceedExecution(method, this.resultSet, args);
        }

        if (this.closed) {
            throw new SQLException("Already closed");
        }
        if (this.resultSetConsumed) {
            if (isWasNullMethod(method)) {
                return this.wasNull;
            }
            if (isGetMethod(method)) {
                return handleGetMethodUsingCache(method, args);
            }
            if (isNextMethod(method)) {
                return handleNextMethodUsingCache();
            }
        } else {
            if (isWasNullMethod(method)) {
                return proceedExecution(method, this.resultSet, args);
            }
            if (isGetMethod(method)) {
                return handleGetMethodByDelegating(method, args);
            }

            boolean isNextMethod = isNextMethod(method);

            if (isNextMethod || isBeforeFirstMethod(method)) {
                beforeNextOrBeforeFirst();
            }
            if (isNextMethod) {
                return handleNextMethodByDelegating(method, args);
            }
            if (isBeforeFirstMethod(method)) {
                resultPointer = -1;
                resultSetConsumed = true;
                return null;
            }
        }
        throw new UnsupportedOperationException(format("Method '%s' is not supported by this proxy", method));
    }

    private void beforeNextOrBeforeFirst() throws SQLException {
        if (currentResult == null) {
            return;
        }
        for (int i = 1; i < currentResult.length; i++) {
            Object resultColumn = currentResult[i];
            if (resultColumn != UNCONSUMED_RESULT_COLUMN) {
                continue;
            }
            currentResult[i] = resultSet.getObject(i);
        }
    }

    private Object handleNextMethodByDelegating(Method method, Object[] args) throws IllegalAccessException, InvocationTargetException {
        Object result = method.invoke(resultSet, args);
        if (TRUE.equals(result)) {
            currentResult = new Object[columnCount + 1];
            Arrays.fill(this.currentResult, UNCONSUMED_RESULT_COLUMN);
            cachedResults.add(currentResult);
        }
        return result;
    }

    private Object handleGetMethodByDelegating(Method method, Object[] args) throws SQLException, IllegalAccessException, InvocationTargetException {
        int columnIndex = determineColumnIndex(args);
        Object result = method.invoke(resultSet, args);
        currentResult[columnIndex] = result;
        return result;
    }

    private Object handleNextMethodUsingCache() {
        if (resultPointer < cachedResults.size() - 1) {
            resultPointer++;
            currentResult = cachedResults.get(resultPointer);
            return true;
        } else {
            resultPointer++;
            currentResult = null;
            return false;
        }
    }

    private Object handleGetMethodUsingCache(Method method, Object[] args) throws SQLException, InvocationTargetException, IllegalAccessException {
        if (resultPointer == -1) {
            throw new SQLException("Result set not advanced. Call next before any get method!");
        } else if (resultPointer < cachedResults.size()) {
            int columnIndex = determineColumnIndex(args);
            Object columnValue = currentResult[columnIndex];
            this.wasNull = isNullValue(columnValue, method, args);
            if (!(columnValue instanceof Number)) {
                return columnValue;
            }
            return convertNumberToExpectedType(method.getName(), (Number) columnValue);
        } else {
            throw new SQLException(format("Result set exhausted. There were %d result(s) only", cachedResults.size()));
        }
    }

    /**
     * Determine whether the retrieved value is {@code null} for {@link #wasNull}.
     * <p> Subclass may override this method to provide more sophisticated wasNull check.
     *
     * @param value  result value
     * @param method getX method
     * @param args   method arguments
     * @return {@code true} if value is considered as {@code null}.
     */
    protected boolean isNullValue(Object value, Method method, Object[] args) {
        return value == null;
    }

    private Object convertNumberToExpectedType(String getMethodName, Number value) throws InvocationTargetException, IllegalAccessException {
        String targetTypeName = getMethodName.substring("get".length()).toLowerCase();
        Method converter = NUMBER_X_VALUE_METHOD_PER_NUMERIC_TYPE.get(targetTypeName);
        if (converter == null) {
            return value;
        }
        return converter.invoke(value);
    }

    private boolean isGetMethod(Method method) {
        return method.getName().startsWith("get") && method.getParameterTypes().length > 0;
    }

    private boolean isNextMethod(Method method) {
        return method.getName().equals("next");
    }

    private boolean isBeforeFirstMethod(Method method) {
        return method.getName().equals("beforeFirst");
    }

    private boolean isWasNullMethod(Method method) {
        return method.getName().equals("wasNull");
    }

    private int determineColumnIndex(Object[] args) throws SQLException {
        Object lookup = args[0];
        if (lookup instanceof Integer) {
            return (Integer) lookup;
        }
        String columnName = (String) lookup;
        Integer indexForColumnName = columnNameToIndex(columnName);
        if (indexForColumnName != null) {
            return indexForColumnName;
        } else {
            throw new SQLException(format("Unknown column name '%s'", columnName));
        }
    }

    private Integer columnNameToIndex(String columnName) {
        return columnNameToIndex.get(columnName.toUpperCase());
    }
}
