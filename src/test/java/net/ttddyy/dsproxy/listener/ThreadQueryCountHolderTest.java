package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.QueryCount;
import net.ttddyy.dsproxy.QueryCountHolder;
import org.junit.After;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4.2
 */
public class ThreadQueryCountHolderTest {

    @After
    public void tearDown() {
        QueryCountHolder.clear();
    }

    @Test
    public void getQueryCount() throws Exception {
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

        assertThat(queryCount.getSuccess()).isEqualTo(1);
        assertThat(queryCount.getFailure()).isEqualTo(0);

        QueryCount fromThread = queryCountReference.get();
        assertThat(fromThread).isNotNull().isNotSameAs(queryCount);
        assertThat(fromThread.getSuccess()).isEqualTo(0);
        assertThat(fromThread.getFailure()).isEqualTo(1);

    }


}
