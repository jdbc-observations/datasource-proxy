package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.QueryCount;
import net.ttddyy.dsproxy.QueryCountHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4.2
 */
class ThreadQueryCountHolderTest {

    @AfterEach
    void tearDown() {
        QueryCountHolder.clear();
    }

    @Test
    void getQueryCount() throws Exception {
        final ThreadQueryCountHolder holder = new ThreadQueryCountHolder();
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

        assertThat(queryCount.getSuccess()).isOne();
        assertThat(queryCount.getFailure()).isZero();

        QueryCount fromThread = queryCountReference.get();
        assertThat(fromThread).isNotNull().isNotSameAs(queryCount);
        assertThat(fromThread.getSuccess()).isZero();
        assertThat(fromThread.getFailure()).isOne();

    }


}
