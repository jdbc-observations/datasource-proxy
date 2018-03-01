package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.proxy.ParameterSetOperation;

import java.util.ArrayList;
import java.util.List;

/**
 * Hold query and parameter information.
 *
 * For Statement batch execution, there will be multiple QueryInfo.
 * For Prepared/Callable batch execution, there will be one QueryInfo with multiple elements in parameters.
 * For batch execution, single instance of this class represents each batch entry.
 *
 * @author Tadaya Tsuyukubo
 */
public class QueryInfo {
    private String query;

    private List<List<ParameterSetOperation>> parametersList = new ArrayList<>();

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
     * List of parameter-operation-list.
     *
     * For non-batch Prepared/Callable execution, this list contains 1 element that is a list which contains all
     * parameter sets operations for the execution.
     * For batch Prepared/Callable executions, this list will have N number of elements.
     *
     * @return list of prameter operation list
     * @since 1.4
     */
    public List<List<ParameterSetOperation>> getParametersList() {
        return parametersList;
    }

    public void setParametersList(List<List<ParameterSetOperation>> parametersList) {
        this.parametersList = parametersList;
    }
}
