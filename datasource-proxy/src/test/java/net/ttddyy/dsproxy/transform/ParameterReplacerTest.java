package net.ttddyy.dsproxy.transform;

import net.ttddyy.dsproxy.proxy.ParameterSetOperation;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author Tadaya Tsuyukubo
 */
public class ParameterReplacerTest {

    @Test
    public void testParamReplacement() {

        Method method = null;

        Map<Object, ParameterSetOperation> input = new LinkedHashMap<Object, ParameterSetOperation>();
        input.put(1, new ParameterSetOperation(method, new Object[]{1, "value-1"}));
        input.put(2, new ParameterSetOperation(method, new Object[]{2, "value-2"}));
        input.put("foo", new ParameterSetOperation(method, new Object[]{"foo", "value-foo"}));
        input.put("bar", new ParameterSetOperation(method, new Object[]{"bar", "value-bar"}));

        ParameterReplacer replacer = new ParameterReplacer(input);
        replacer.setString(1, "replaced-1");
        replacer.setString("foo", "replaced-foo");

        Map<Object, ParameterSetOperation> params = replacer.getModifiedParameters();
        assertThat(params.keySet(), hasSize(4));
        assertThat(params.keySet(), hasItems((Object)1, 2, "foo", "bar"));

        assertThat((String) params.get(1).getArgs()[1], is("replaced-1"));
        assertThat((String) params.get(2).getArgs()[1], is("value-2"));
        assertThat((String) params.get("foo").getArgs()[1], is("replaced-foo"));
        assertThat((String) params.get("bar").getArgs()[1], is("value-bar"));

    }

    @Test
    public void testClearParamAndReplace() {

        Method method = null;

        Map<Object, ParameterSetOperation> input = new LinkedHashMap<Object, ParameterSetOperation>();
        input.put(1, new ParameterSetOperation(method, new Object[]{1, "value-1"}));
        input.put(2, new ParameterSetOperation(method, new Object[]{2, "value-2"}));
        input.put("foo", new ParameterSetOperation(method, new Object[]{"foo", "value-foo"}));
        input.put("bar", new ParameterSetOperation(method, new Object[]{"bar", "value-bar"}));

        ParameterReplacer replacer = new ParameterReplacer(input);
        replacer.clearParameters();
        replacer.setString(1, "replaced-1");
        replacer.setString("foo", "replaced-foo");

        Map<Object, ParameterSetOperation> params = replacer.getModifiedParameters();
        assertThat(params.keySet(), hasSize(2));
        assertThat(params.keySet(), hasItems((Object)1, "foo"));

        assertThat((String) params.get(1).getArgs()[1], is("replaced-1"));
        assertThat((String) params.get("foo").getArgs()[1], is("replaced-foo"));
    }

}
