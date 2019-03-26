package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.listener.count.QueryCount;
import net.ttddyy.dsproxy.listener.count.QueryCountHolder;
import net.ttddyy.dsproxy.listener.count.SingleQueryCountStrategy;
import net.ttddyy.dsproxy.listener.count.ThreadQueryCountStrategy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4.2
 */
public class ThreadQueryCountStrategyTest {

    @AfterEach
    public void tearDown() {
        QueryCountHolder.clearAll();
    }

    @Test
    public void getQueryCount() throws Exception {
        final ThreadQueryCountStrategy holder = new ThreadQueryCountStrategy();
        QueryCount queryCount = holder.getOrCreateQueryCount("testDS");
        queryCount.incrementSuccess();

        final AtomicReference<QueryCount> queryCountReference = new AtomicReference<QueryCount>();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                QueryCount queryCount = holder.getOrCreateQueryCount("testDS");
                queryCount.incrementFailure();
                queryCountReference.set(queryCount);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        thread.join();

        assertThat(queryCount.getSuccess()).isEqualTo(1);
        assertThat(queryCount.getFailure()).isEqualTo(0);

        QueryCount fromThread = queryCountReference.get();
        assertThat(fromThread).isNotNull().isNotSameAs(queryCount);
        assertThat(fromThread.getSuccess()).isEqualTo(0);
        assertThat(fromThread.getFailure()).isEqualTo(1);

    }

    @Test
    void getOrCreateQueryCountMultipleTimes() {
        ThreadQueryCountStrategy strategy = new ThreadQueryCountStrategy();

        QueryCount firstQueryCount = strategy.getOrCreateQueryCount("testDS-A");
        assertNotNull(firstQueryCount);

        QueryCount secondQueryCount = strategy.getOrCreateQueryCount("testDS-A");
        QueryCount thirdQueryCount = strategy.getOrCreateQueryCount("testDS-A");

        assertSame(firstQueryCount, secondQueryCount);
        assertSame(firstQueryCount, thirdQueryCount);

        QueryCount anotherFirstQueryCount = strategy.getOrCreateQueryCount("testDS-B");
        QueryCount anotherSecondQueryCount = strategy.getOrCreateQueryCount("testDS-B");
        QueryCount anotherThirdQueryCount = strategy.getOrCreateQueryCount("testDS-B");

        assertNotSame(firstQueryCount, anotherSecondQueryCount);

        assertSame(anotherFirstQueryCount, anotherSecondQueryCount);
        assertSame(anotherFirstQueryCount, anotherThirdQueryCount);

    }

}
