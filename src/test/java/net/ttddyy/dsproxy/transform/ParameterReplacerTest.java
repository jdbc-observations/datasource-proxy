package net.ttddyy.dsproxy.transform;

import net.ttddyy.dsproxy.proxy.ParameterOperationHolder;
import net.ttddyy.dsproxy.proxy.ParameterSetOperation;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.SortedMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author Tadaya Tsuyukubo
 */
public class ParameterReplacerTest {

    @Test
    public void testParamReplacement() {

        Method method = null;

        ParameterOperationHolder input = new ParameterOperationHolder();
        input.getParamsByIndex().put(1, new ParameterSetOperation(method, new Object[]{1, "value-1"}));
        input.getParamsByIndex().put(2, new ParameterSetOperation(method, new Object[]{2, "value-2"}));
        input.getParamsByName().put("foo", new ParameterSetOperation(method, new Object[]{"foo", "value-foo"}));
        input.getParamsByName().put("bar", new ParameterSetOperation(method, new Object[]{"bar", "value-bar"}));

        ParameterReplacer replacer = new ParameterReplacer(input);
        replacer.setString(1, "replaced-1");
        replacer.setString("foo", "replaced-foo");

        ParameterOperationHolder params = replacer.getModifiedParameters();
        SortedMap<Integer, ParameterSetOperation> paramsByIndex = params.getParamsByIndex();
        assertThat(paramsByIndex.keySet(), hasSize(2));
        assertThat(paramsByIndex.keySet(), hasItems(1, 2));

        SortedMap<String, ParameterSetOperation> paramsByName = params.getParamsByName();
        assertThat(paramsByName.keySet(), hasSize(2));
        assertThat(paramsByName.keySet(), hasItems("foo", "bar"));

        assertThat((String) params.getParamsByIndex().get(1).getArgs()[1], is("replaced-1"));
        assertThat((String) params.getParamsByIndex().get(2).getArgs()[1], is("value-2"));
        assertThat((String) params.getParamsByName().get("foo").getArgs()[1], is("replaced-foo"));
        assertThat((String) params.getParamsByName().get("bar").getArgs()[1], is("value-bar"));

    }

    @Test
    public void testClearParamAndReplace() {

        Method method = null;

        ParameterOperationHolder input = new ParameterOperationHolder();
        input.getParamsByIndex().put(1, new ParameterSetOperation(method, new Object[]{1, "value-1"}));
        input.getParamsByIndex().put(2, new ParameterSetOperation(method, new Object[]{2, "value-2"}));
        input.getParamsByName().put("foo", new ParameterSetOperation(method, new Object[]{"foo", "value-foo"}));
        input.getParamsByName().put("bar", new ParameterSetOperation(method, new Object[]{"bar", "value-bar"}));

        ParameterReplacer replacer = new ParameterReplacer(input);
        replacer.clearParameters();
        replacer.setString(1, "replaced-1");
        replacer.setString("foo", "replaced-foo");

        ParameterOperationHolder params = replacer.getModifiedParameters();
        SortedMap<Integer, ParameterSetOperation> paramsByIndex = params.getParamsByIndex();
        assertThat(paramsByIndex.keySet(), hasSize(1));
        assertThat(paramsByIndex.keySet(), hasItems(1));

        SortedMap<String, ParameterSetOperation> paramsByName = params.getParamsByName();
        assertThat(paramsByName.keySet(), hasSize(1));
        assertThat(paramsByName.keySet(), hasItems("foo"));

        assertThat((String) params.getParamsByIndex().get(1).getArgs()[1], is("replaced-1"));
        assertThat((String) params.getParamsByName().get("foo").getArgs()[1], is("replaced-foo"));
    }

}
