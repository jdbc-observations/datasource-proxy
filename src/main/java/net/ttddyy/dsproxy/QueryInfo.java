package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.proxy.ParameterSetOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds query and parameter information.
 *
 * <p>For {@link java.sql.Statement} batch execution, there is one {@code QueryInfo} per batch entry.
 * For {@link java.sql.PreparedStatement} or {@link java.sql.CallableStatement} batch execution, there is one
 * {@code QueryInfo} with multiple entries in its parameter list.
 *
 * @author Tadaya Tsuyukubo
 */
public class QueryInfo {
    private String query;

    private List<List<ParameterSetOperation>> parametersList = new ArrayList<List<ParameterSetOperation>>();

    public QueryInfo() {
    }

    public QueryInfo(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * Deprecated because the return value does not include method information. Use {@link #getParametersList()} instead.
     *
     * @return list of parameter maps, where each key is the first argument converted to a string and each value is the second argument
     * @deprecated use {@link #getParametersList()}
     */
    @Deprecated
    public List<Map<String, Object>> getQueryArgsList() {
        // simulate old implementation behavior
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (List<ParameterSetOperation> paramsList : this.parametersList) {
            Map<String, Object> map = new HashMap<String, Object>();

            for (ParameterSetOperation param : paramsList) {
                Object[] args = param.getArgs();
                map.put(args[0].toString(), args[1]);
            }

            result.add(map);
        }
        return result;
    }


    /**
     * Lists parameter-operation groups.
     *
     * <p>For non-batch Prepared/Callable execution, this list contains one element containing all parameter-set
     * operations for the execution.
     * For batch Prepared/Callable executions, this list contains one element per batch entry.
     *
     * @return list of parameter-operation lists
     * @since 1.4
     */
    public List<List<ParameterSetOperation>> getParametersList() {
        return parametersList;
    }

    public void setParametersList(List<List<ParameterSetOperation>> parametersList) {
        this.parametersList = parametersList;
    }
}
