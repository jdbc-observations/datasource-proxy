package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.listener.MethodExecutionContext;
import net.ttddyy.dsproxy.listener.ProxyDataSourceListener;
import net.ttddyy.dsproxy.listener.QueryExecutionContext;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test for {@link CompositeProxyDataSourceListener}.
 */
class CompositeProxyDataSourceListenerTest {

    @Test
    void callsListenersInRegistrationOrderForBeforeAndAfter() {
        List<String> calls = new ArrayList<>();
        var a = recording("a", calls);
        var b = recording("b", calls);
        var composite = new CompositeProxyDataSourceListener(List.of(a, b, a));  // duplicates allowed
        var ctx = new MutableQueryExecutionContext();
        composite.beforeQuery(ctx);
        composite.afterQuery(ctx);
        composite.beforeMethod(methodCtx());
        composite.afterMethod(methodCtx());
        assertThat(calls).containsExactly(
                "a.beforeQuery", "b.beforeQuery", "a.beforeQuery",
                "a.afterQuery", "b.afterQuery", "a.afterQuery",
                "a.beforeMethod", "b.beforeMethod", "a.beforeMethod",
                "a.afterMethod", "b.afterMethod", "a.afterMethod");
    }

    @Test
    void exceptionStopsLaterListenersAndPropagates() {
        List<String> calls = new ArrayList<>();
        ProxyDataSourceListener boom = new ProxyDataSourceListener() {
            @Override
            public void beforeQuery(QueryExecutionContext c) {
                throw new IllegalStateException("boom");
            }
        };
        var composite = new CompositeProxyDataSourceListener(List.of(boom, recording("b", calls)));
        assertThatThrownBy(() -> composite.beforeQuery(new MutableQueryExecutionContext()))
                .isInstanceOf(IllegalStateException.class).hasMessage("boom");
        assertThat(calls).isEmpty();
    }

    @Test
    void listenerImplementingNothingIsNoOp() {
        new CompositeProxyDataSourceListener(List.of(new ProxyDataSourceListener() {
        })).beforeQuery(new MutableQueryExecutionContext());
    }

    @Test
    void getListenersIsUnmodifiableCopy() {
        var source = new ArrayList<ProxyDataSourceListener>();
        var composite = new CompositeProxyDataSourceListener(source);
        source.add(new ProxyDataSourceListener() {
        });
        assertThat(composite.getListeners()).isEmpty();
        assertThatThrownBy(() -> composite.getListeners().add(new ProxyDataSourceListener() {
        })).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void customValuesRoundTrip() {
        var ctx = new MutableQueryExecutionContext();
        ctx.addCustomValue("k", "v");
        assertThat(ctx.getCustomValue("k", String.class)).isEqualTo("v");
    }

    private static MethodExecutionContext methodCtx() {
        return MethodExecutionContext.Builder.create().build();
    }

    private static ProxyDataSourceListener recording(String name, List<String> calls) {
        return new ProxyDataSourceListener() {
            @Override
            public void beforeQuery(QueryExecutionContext c) {
                calls.add(name + ".beforeQuery");
            }

            @Override
            public void afterQuery(QueryExecutionContext c) {
                calls.add(name + ".afterQuery");
            }

            @Override
            public void beforeMethod(MethodExecutionContext c) {
                calls.add(name + ".beforeMethod");
            }

            @Override
            public void afterMethod(MethodExecutionContext c) {
                calls.add(name + ".afterMethod");
            }
        };
    }
}
