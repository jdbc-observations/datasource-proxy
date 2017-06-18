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
public class SingleQueryCountHolderTest {

    @After
    public void cleanUp() {
        QueryCountHolder.clear();
    }

    @Test
    public void getQueryCount() throws Exception {
        final SingleQueryCountHolder holder = new SingleQueryCountHolder();
        assertThat(holder.isPopulateQueryCountHolder()).isTrue();

        QueryCount mainQueryCount = holder.getOrCreateQueryCount("testDS");
        QueryCount mainQueryCountHolderCount = QueryCountHolder.get("testDS");

        final AtomicReference<QueryCount> createdQueryCountReference = new AtomicReference<QueryCount>();
        final AtomicReference<QueryCount> holderQueryCountReference = new AtomicReference<QueryCount>();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                QueryCount queryCount = holder.getOrCreateQueryCount("testDS");
                createdQueryCountReference.set(queryCount);
                holderQueryCountReference.set(QueryCountHolder.get("testDS"));
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
    public void populateQueryCountHolder() throws Exception {
        final SingleQueryCountHolder holder = new SingleQueryCountHolder();
        holder.setPopulateQueryCountHolder(false);

        QueryCount mainQueryCount = holder.getOrCreateQueryCount("testDS");
        assertThat(QueryCountHolder.get("testDS")).isNull();

        final AtomicReference<QueryCount> createdQueryCountReference = new AtomicReference<QueryCount>();
        final AtomicReference<QueryCount> holderQueryCountReference = new AtomicReference<QueryCount>();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                QueryCount queryCount = holder.getOrCreateQueryCount("testDS");
                createdQueryCountReference.set(queryCount);
                holderQueryCountReference.set(QueryCountHolder.get("testDS"));
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        thread.join();

        assertThat(createdQueryCountReference.get()).isNotNull().isSameAs(mainQueryCount);
        assertThat(holderQueryCountReference.get()).isNull();

    }
}
