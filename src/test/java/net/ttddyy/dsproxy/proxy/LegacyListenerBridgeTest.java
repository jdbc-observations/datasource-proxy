package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.NoOpQueryExecutionListener;
import net.ttddyy.dsproxy.listener.ProxyDataSourceListener;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// shortcut: temporary 1.x listener bridge, removed in Task 7 (Phase 2)
class LegacyListenerBridgeTest {

    private static class Recording extends NoOpQueryExecutionListener {
        final List<ExecutionInfo> infos = new ArrayList<>();
        final List<List<QueryInfo>> queries = new ArrayList<>();

        @Override
        public void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
            this.infos.add(execInfo);
            this.queries.add(queryInfoList);
        }

        @Override
        public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
            this.infos.add(execInfo);
            this.queries.add(queryInfoList);
        }
    }

    private static MutableQueryExecutionContext context(StopwatchFactory stopwatchFactory) {
        MutableQueryExecutionContext context = new MutableQueryExecutionContext();
        context.setDataSourceName("ds");
        context.setConnectionId("conn");
        context.setBatch(true);
        context.setBatchSize(3);
        context.setQueries(List.of(new QueryInfo("SELECT 1")));
        context.stopwatchFactory = stopwatchFactory;
        return context;
    }

    @Test
    void sharesOneExecutionInfoAndQueryListAcrossListenersAndPhases() {
        Recording first = new Recording();
        Recording second = new Recording();
        ProxyDataSourceListener bridge1 = LegacyListenerBridge.ofQuery(first);
        ProxyDataSourceListener bridge2 = LegacyListenerBridge.ofQuery(second);
        MutableQueryExecutionContext context = context(new SystemStopwatchFactory());

        bridge1.beforeQuery(context);
        bridge2.beforeQuery(context);
        context.setSuccess(true);
        context.setResult("result");
        bridge1.afterQuery(context);
        bridge2.afterQuery(context);

        ExecutionInfo info = first.infos.get(0);
        assertThat(first.infos).containsOnly(info);
        assertThat(second.infos).containsOnly(info);
        assertThat(first.queries).allSatisfy(q -> assertThat(q).isSameAs(context.getQueries()));
        assertThat(info.getDataSourceName()).isEqualTo("ds");
        assertThat(info.getConnectionId()).isEqualTo("conn");
        assertThat(info.isBatch()).isTrue();
        assertThat(info.getBatchSize()).isEqualTo(3);
        assertThat(info.isSuccess()).isTrue();
        assertThat(info.getResult()).isEqualTo("result");
    }

    @Test
    void customValuesFlowBothWays() {
        ProxyDataSourceListener bridge = LegacyListenerBridge.ofQuery(new NoOpQueryExecutionListener() {
            @Override
            public void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
                execInfo.addCustomValue("fromOld", "old");
            }

            @Override
            public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
                execInfo.addCustomValue("seenNew", execInfo.getCustomValue("fromNew", String.class));
            }
        });
        MutableQueryExecutionContext context = context(new SystemStopwatchFactory());

        bridge.beforeQuery(context);
        assertThat(context.getCustomValue("fromOld", String.class)).isEqualTo("old");

        context.addCustomValue("fromNew", "new");
        bridge.afterQuery(context);
        assertThat(context.getCustomValue("seenNew", String.class)).isEqualTo("new");
    }

    @Test
    void failureAndElapsedTimeInLegacyUnits() {
        Recording millis = new Recording();
        MutableQueryExecutionContext context = context(new SystemStopwatchFactory());
        SQLException failure = new SQLException("boom");
        context.setSuccess(false);
        context.setThrowable(failure);
        context.setElapsedTime(Duration.ofMillis(7));
        LegacyListenerBridge.ofQuery(millis).afterQuery(context);

        assertThat(millis.infos.get(0).isSuccess()).isFalse();
        assertThat(millis.infos.get(0).getThrowable()).isSameAs(failure);
        assertThat(millis.infos.get(0).getElapsedTime()).isEqualTo(7);

        Recording nanos = new Recording();
        MutableQueryExecutionContext nanoContext = context(new NanoTimeStopwatchFactory());
        nanoContext.setElapsedTime(Duration.ofNanos(1234));
        LegacyListenerBridge.ofQuery(nanos).afterQuery(nanoContext);

        assertThat(nanos.infos.get(0).getElapsedTime()).isEqualTo(1234);
    }

}
