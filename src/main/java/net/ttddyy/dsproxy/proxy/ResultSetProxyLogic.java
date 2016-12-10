package net.ttddyy.dsproxy.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.TRUE;
import static java.lang.String.format;

/**
 * Allows {@link java.sql.ResultSet} to be consumed more than once.
 *
 * @author Liam Williams
 * @see net.ttddyy.dsproxy.proxy.jdk.ResultSetInvocationHandler
 * @see net.ttddyy.dsproxy.proxy.jdk.ResultSetProxyJdbcProxyFactory
 * @see net.ttddyy.dsproxy.proxy.jdk.StatementResultSetResultInvocationHandler
 */
public class ResultSetProxyLogic {

    private final Map<String, Integer> columnNameToIndex;
    private final ResultSet target;
    private final int columnCount;

    private int resultPointer;
    private boolean resultSetConsumed;
    private boolean closed;
    private Object[] currentResult;
    private final List<Object[]> cachedResults = new ArrayList<Object[]>();

    private ResultSetProxyLogic(Map<String, Integer> columnNameToIndex, ResultSet target, int columnCount) throws SQLException {
        this.columnNameToIndex = columnNameToIndex;
        this.target = target;
        this.columnCount = columnCount;
    }

    public static ResultSetProxyLogic resultSetProxyLogic(ResultSet target) throws SQLException {
        ResultSetMetaData metaData = target.getMetaData();
        int columnCount = metaData.getColumnCount();
        return new ResultSetProxyLogic(columnNameToIndex(metaData), target, columnCount);
    }

    private static Map<String, Integer> columnNameToIndex(ResultSetMetaData metaData) throws SQLException {
        Map<String, Integer> columnNameToIndex = new HashMap<String, Integer>();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            columnNameToIndex.put(metaData.getColumnLabel(i).toUpperCase(), i);
        }
        return columnNameToIndex;
    }

    public Object invoke(Method method, Object[] args) throws Throwable {
        if (isGetTargetMethod(method)) {
            // ProxyJdbcObject interface has a method to return original object.
            return target;
        }
        if (isGetMetaDataMethod(method)) {
            return method.invoke(target, args);
        }
        if (isCloseMethod(method)) {
            closed = true;
            return method.invoke(target, args);
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
        Object result = method.invoke(target, args);
        if (TRUE.equals(result)) {
            currentResult = new Object[columnCount + 1];
            cachedResults.add(currentResult);
        }
        return result;
    }

    private Object handleGetMethodByDelegating(Method method, Object[] args) throws SQLException, IllegalAccessException, InvocationTargetException {
        int columnIndex = determineColumnIndex(args);
        Object result = method.invoke(target, args);
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

    private boolean isGetTargetMethod(Method method) {
        return method.getName().equals("getTarget");
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
