package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.listener.count.QueryCount;
import net.ttddyy.dsproxy.listener.count.QueryCountHolder;
import net.ttddyy.dsproxy.listener.count.SingleQueryCountStrategy;
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
public class SingleQueryCountStrategyTest {

    @AfterEach
    public void cleanUp() {
        QueryCountHolder.clearAll();
    }

    @Test
    public void getQueryCount() throws Exception {
        final SingleQueryCountStrategy strategy = new SingleQueryCountStrategy();
        QueryCountHolder.setQueryCountStrategy(strategy);

        QueryCount mainQueryCount = strategy.getOrCreateQueryCount("testDS");
        QueryCount mainQueryCountHolderCount = QueryCountHolder.getOrCreateQueryCount("testDS");

        assertSame(mainQueryCount, mainQueryCountHolderCount);

        // check the retrieved queryCounts in different thread are same
        final AtomicReference<QueryCount> createdQueryCountReference = new AtomicReference<QueryCount>();
        final AtomicReference<QueryCount> holderQueryCountReference = new AtomicReference<QueryCount>();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                QueryCount queryCount = strategy.getOrCreateQueryCount("testDS");
                createdQueryCountReference.set(queryCount);
                holderQueryCountReference.set(QueryCountHolder.getOrCreateQueryCount("testDS"));
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        thread.join();

        assertThat(mainQueryCountHolderCount).isNotNull().isSameAs(mainQueryCount);
        assertThat(createdQueryCountReference.get()).isNotNull().isSameAs(mainQueryCount);
        assertThat(holderQueryCountReference.get()).isNotNull().isSameAs(mainQueryCount);

    }

    @Test
    void getOrCreateQueryCountMultipleTimes() {
        SingleQueryCountStrategy strategy = new SingleQueryCountStrategy();

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
