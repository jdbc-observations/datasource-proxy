package net.ttddyy.dsproxy.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
 * @see net.ttddyy.dsproxy.proxy.jdk.ResultSetInvocationHandler
 * @since 1.4
 */
public class ResultSetProxyLogic {

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

    public static class Builder {
        private ResultSet resultSet;
        private Map<String, Integer> columnNameToIndex;
        private int columnCount;

        public static Builder create() {
            return new Builder();
        }

        public ResultSetProxyLogic build() {
            ResultSetProxyLogic logic = new ResultSetProxyLogic();
            logic.resultSet = this.resultSet;
            logic.columnNameToIndex = this.columnNameToIndex;
            logic.columnCount = this.columnCount;
            return logic;
        }

        public Builder resultSet(ResultSet resultSet) {
            this.resultSet = resultSet;
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
    private int columnCount;

    private int resultPointer;
    private boolean resultSetConsumed;
    private boolean closed;
    private Object[] currentResult;
    private final List<Object[]> cachedResults = new ArrayList<Object[]>();


    public Object invoke(Method method, Object[] args) throws Throwable {

        final String methodName = method.getName();

        if (!METHODS_TO_INTERCEPT.contains(methodName)) {
            return MethodUtils.proceedExecution(method, this.resultSet, args);
        }

        // special treat for toString method
        if ("toString".equals(methodName)) {
            final StringBuilder sb = new StringBuilder();
            sb.append(this.resultSet.getClass().getSimpleName());
            sb.append(" [");
            sb.append(this.resultSet.toString());
            sb.append("]");
            return sb.toString(); // differentiate toString message.
        } else if ("getTarget".equals(methodName)) {
            // ProxyJdbcObject interface has a method to return original object.
            return this.resultSet;
        }


        if (isGetMetaDataMethod(method)) {
            return method.invoke(resultSet, args);
        }
        if (isCloseMethod(method)) {
            closed = true;
            return method.invoke(resultSet, args);
        }
        if (closed) {
            throw new SQLException("Already closed");
        }
        if (resultSetConsumed) {
            if (isGetMethod(method)) {
                return handleGetMethodUsingCache(args);
            }
            if (isNextMethod(method)) {
                return handleNextMethodUsingCache();
            }
        } else {
            if (isGetMethod(method)) {
                return handleGetMethodByDelegating(method, args);
            }
            if (isNextMethod(method)) {
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

    private Object handleNextMethodByDelegating(Method method, Object[] args) throws IllegalAccessException, InvocationTargetException {
        Object result = method.invoke(resultSet, args);
        if (TRUE.equals(result)) {
            currentResult = new Object[columnCount + 1];
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

    private Object handleGetMethodUsingCache(Object[] args) throws SQLException {
        if (resultPointer == -1) {
            throw new SQLException("Result set not advanced. Call next before any get method!");
        } else if (resultPointer < cachedResults.size()) {
            int columnIndex = determineColumnIndex(args);
            return currentResult[columnIndex];
        } else {
            throw new SQLException(format("Result set exhausted. There were %d result(s) only", cachedResults.size()));
        }
    }

    private boolean isCloseMethod(Method method) {
        return method.getName().equals("close");
    }

    private boolean isGetMetaDataMethod(Method method) {
        return method.getName().equals("getMetaData");
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
