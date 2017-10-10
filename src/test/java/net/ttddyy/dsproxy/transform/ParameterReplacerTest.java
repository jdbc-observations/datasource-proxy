package net.ttddyy.dsproxy.transform;

import net.ttddyy.dsproxy.proxy.ParameterKey;
import net.ttddyy.dsproxy.proxy.ParameterSetOperation;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Tadaya Tsuyukubo
 */
public class ParameterReplacerTest {

    @Test
    public void testParamReplacement() {

        Method method = null;

        Map<ParameterKey, ParameterSetOperation> input = new LinkedHashMap<ParameterKey, ParameterSetOperation>();
        input.put(new ParameterKey(1), new ParameterSetOperation(method, new Object[]{1, "value-1"}));
        input.put(new ParameterKey(2), new ParameterSetOperation(method, new Object[]{2, "value-2"}));
        input.put(new ParameterKey("foo"), new ParameterSetOperation(method, new Object[]{"foo", "value-foo"}));
        input.put(new ParameterKey("bar"), new ParameterSetOperation(method, new Object[]{"bar", "value-bar"}));

        ParameterReplacer replacer = new ParameterReplacer(input);
        replacer.setString(1, "replaced-1");
        replacer.setString("foo", "replaced-foo");

        Map<ParameterKey, ParameterSetOperation> params = replacer.getModifiedParameters();
        assertThat(params.keySet()).hasSize(4);
        assertThat(params.keySet()).containsExactly(new ParameterKey(1), new ParameterKey(2), new ParameterKey("foo"), new ParameterKey("bar"));

        assertThat((String) params.get(new ParameterKey(1)).getArgs()[1]).isEqualTo("replaced-1");
        assertThat((String) params.get(new ParameterKey(2)).getArgs()[1]).isEqualTo("value-2");
        assertThat((String) params.get(new ParameterKey("foo")).getArgs()[1]).isEqualTo("replaced-foo");
        assertThat((String) params.get(new ParameterKey("bar")).getArgs()[1]).isEqualTo("value-bar");

    }

    @Test
    public void testClearParamAndReplace() {

        Method method = null;

        Map<ParameterKey, ParameterSetOperation> input = new LinkedHashMap<ParameterKey, ParameterSetOperation>();
        input.put(new ParameterKey(1), new ParameterSetOperation(method, new Object[]{1, "value-1"}));
        input.put(new ParameterKey(2), new ParameterSetOperation(method, new Object[]{2, "value-2"}));
        input.put(new ParameterKey("foo"), new ParameterSetOperation(method, new Object[]{"foo", "value-foo"}));
        input.put(new ParameterKey("bar"), new ParameterSetOperation(method, new Object[]{"bar", "value-bar"}));

        ParameterReplacer replacer = new ParameterReplacer(input);
        replacer.clearParameters();
        replacer.setString(1, "replaced-1");
        replacer.setString("foo", "replaced-foo");

        Map<ParameterKey, ParameterSetOperation> params = replacer.getModifiedParameters();
        assertThat(params.keySet()).containsExactly(new ParameterKey(1), new ParameterKey("foo"));

        assertThat((String) params.get(new ParameterKey(1)).getArgs()[1]).isEqualTo("replaced-1");
        assertThat((String) params.get(new ParameterKey("foo")).getArgs()[1]).isEqualTo("replaced-foo");
    }

}
