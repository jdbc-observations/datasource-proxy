package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * @author Tadaya Tsuyukubo
 */
public class SlowQueryListenerTest {

    @Test
    public void onSlowQuery() throws Exception {

        final ExecutionInfo executionInfo = new ExecutionInfo();
        final List<QueryInfo> queryInfo = new ArrayList<QueryInfo>();

        final AtomicInteger counter = new AtomicInteger();

        SlowQueryListener listener = new SlowQueryListener() {
            @Override
            protected void onSlowQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList, long startTimeInMills) {
                counter.incrementAndGet();
                assertThat(execInfo).isSameAs(executionInfo);
                assertThat(queryInfoList).isSameAs(queryInfo);
            }
        };
        listener.setThreshold(50);
        listener.setThresholdTimeUnit(TimeUnit.MILLISECONDS);

        // simulate slow query
        listener.beforeQuery(executionInfo, queryInfo);
        TimeUnit.MILLISECONDS.sleep(200);  // ample time
        listener.afterQuery(executionInfo, queryInfo);

        assertThat(counter.get()).as("callback should be called, and it should be once").isEqualTo(1);
    }

    @Test
    public void onSlowQueryWithFastQuery() throws Exception {

        SlowQueryListener listener = new SlowQueryListener() {
            @Override
            protected void onSlowQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList, long startTimeInMills) {
                fail("onSlowQuery method should not be called for fast query");
            }
        };
        listener.setThreshold(100);
        listener.setThresholdTimeUnit(TimeUnit.MILLISECONDS);

        final ExecutionInfo executionInfo = new ExecutionInfo();
        final List<QueryInfo> queryInfo = new ArrayList<QueryInfo>();

        // calling immediately after
        listener.beforeQuery(executionInfo, queryInfo);
        listener.afterQuery(executionInfo, queryInfo);
    }

}
