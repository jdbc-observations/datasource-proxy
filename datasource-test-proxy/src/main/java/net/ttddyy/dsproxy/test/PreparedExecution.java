package net.ttddyy.dsproxy.test;

import net.ttddyy.dsproxy.proxy.ParameterKey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static net.ttddyy.dsproxy.proxy.ParameterKeyUtils.toIndexMap;
import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.filterBy;
import static net.ttddyy.dsproxy.test.ParameterKeyValueUtils.toKeyValueMap;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class PreparedExecution extends BaseQueryExecution implements QueryHolder, ParameterByIndexHolder {

    private String query;
    private List<ParameterKeyValue> parameters = new ArrayList<ParameterKeyValue>();

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
    public List<ParameterKeyValue> getParameters() {
        // TODO: clean up
        Collections.sort(this.parameters, new Comparator<ParameterKeyValue>() {
            @Override
            public int compare(ParameterKeyValue left, ParameterKeyValue right) {
                return left.getKey().compareTo(right.getKey());  // use key to sort
            }
        });
        return this.parameters;
    }

    @Override
    public Map<Integer, Object> getParamsByIndex() {
        return toIndexMap(getParams());
    }

    @Override
    public Map<Integer, Integer> getSetNullParamsByIndex() {
        return toIndexMap(getSetNullParams());
    }

    @Override
    public List<Integer> getParamIndexes() {
        List<Integer> indexes = new ArrayList<Integer>();
        indexes.addAll(toIndexMap(getParams()).keySet());
        indexes.addAll(toIndexMap(getSetNullParams()).keySet());
        return indexes;
    }

    @Override
    public Map<ParameterKey, Object> getAllParams() {
        return toKeyValueMap(this.parameters);
    }

    @Override
    public Map<ParameterKey, Object> getParams() {
        return toKeyValueMap(filterBy(this.parameters, ParameterKeyValue.OperationType.SET_PARAM));
    }

    @Override
    public Map<ParameterKey, Integer> getSetNullParams() {
        return toKeyValueMap(filterBy(this.parameters, ParameterKeyValue.OperationType.SET_NULL));
    }

}
