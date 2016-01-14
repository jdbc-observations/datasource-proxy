package net.ttddyy.dsproxy.test;

import net.ttddyy.dsproxy.proxy.ParameterKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import static net.ttddyy.dsproxy.proxy.ParameterKeyUtils.toIndexMap;
import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.filterBy;
import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.filterByKeyType;
import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.toKeyIndexMap;
import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.toKeyValueMap;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class PreparedExecution extends BaseQueryExecution implements QueryHolder, ParameterByIndexHolder {

    private String query;
    private SortedSet<ParameterKeyValue> parameters = new TreeSet<ParameterKeyValue>();

    @Override
    public boolean isBatch() {
        return false;
    }

    @Override
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public SortedSet<ParameterKeyValue> getParameters() {
        return this.parameters;
    }

    @Override
    public Map<Integer, Object> getParamsByIndex() {
        return toKeyIndexMap(filterByKeyType(getParams(), ParameterKey.ParameterKeyType.BY_INDEX));
    }

    @Override
    public Map<Integer, Integer> getSetNullParamsByIndex() {
        return toIndexMap(getSetNullParams());
    }

    @Override
    public List<Integer> getParamIndexes() {
        List<Integer> indexes = new ArrayList<Integer>();
        indexes.addAll(getParamsByIndex().keySet());
        indexes.addAll(toIndexMap(getSetNullParams()).keySet());
        return indexes;
    }

    @Override
    public Map<ParameterKey, Object> getAllParams() {
        return toKeyValueMap(this.parameters);
    }

    @Override
    public SortedSet<ParameterKeyValue> getParams() {
        return filterBy(this.parameters, ParameterKeyValue.OperationType.SET_PARAM);
    }

    @Override
    public Map<ParameterKey, Integer> getSetNullParams() {
        return toKeyValueMap(filterBy(this.parameters, ParameterKeyValue.OperationType.SET_NULL));
    }

}
