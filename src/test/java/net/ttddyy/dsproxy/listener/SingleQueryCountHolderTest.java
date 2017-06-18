package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.QueryCount;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4.2
 */
public class SingleQueryCountHolderTest {

    @Test
    public void getQueryCount() throws Exception {
        final SingleQueryCountHolder holder = new SingleQueryCountHolder();
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

        assertThat(queryCountReference.get()).isNotNull().isSameAs(queryCount);
        assertThat(queryCount.getSuccess()).isEqualTo(1);
        assertThat(queryCount.getFailure()).isEqualTo(1);

    }
}
