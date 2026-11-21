package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import static net.ttddyy.dsproxy.QueryCounterTest.select;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class QueryCounterWrapTest {

    private DataSource actual;

    private DataSource ds1;

    private DataSource ds2;

    @BeforeEach
    void setup() throws Exception {
        this.actual = TestUtils.getDataSourceWithData();
        this.ds1 = ProxyDataSourceBuilder.create(this.actual).name("a").countQuery().build();
        this.ds2 = ProxyDataSourceBuilder.create(this.actual).name("b").countQuery().build();
    }

    @AfterEach
    void teardown() throws Exception {
        assertThat(QueryCounter.current()).isNull();
        TestUtils.shutdown(this.actual);
    }

    private static void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    void wrappedRunnableAndCallableCountIntoCapturedScope() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try (var scope = QueryCounter.start()) {
            pool.submit(QueryCounter.wrap(() -> select(this.ds1))).get();
            pool.submit(QueryCounter.wrap(() -> {
                select(this.ds1);
                return 1;
            })).get();
            assertThat(scope.count().select()).isEqualTo(2);
        } finally {
            pool.shutdown();
        }
    }

    private static int countRows(DataSource ds) {
        select(ds);
        return 1;
    }

    @Test
    void wrapWorksWithCompletableFutureRunAsync() throws Exception {
        try (var scope = QueryCounter.start()) {
            java.util.concurrent.CompletableFuture.runAsync(QueryCounter.wrap(() -> {
                countRows(this.ds1);
            })).get();
            assertThat(scope.count().select()).isEqualTo(1);
        }
    }

    @Test
    void wrapWithoutScopeReturnsTaskUnchanged() {
        Runnable r = () -> {
        };
        Callable<Integer> c = () -> 1;
        assertThat(QueryCounter.wrap(r)).isSameAs(r);
        assertThat(QueryCounter.wrap(c)).isSameAs(c);
    }

    @Test
    void wrappedExecutorUsesSubmittersScopeAtSubmitTime() throws Exception {
        ExecutorService pool = Executors.newSingleThreadExecutor();
        ExecutorService wrapped = QueryCounter.wrap(pool);
        try {
            try (var a = QueryCounter.start()) {
                wrapped.submit(() -> select(this.ds1)).get();
                assertThat(a.count().select()).isEqualTo(1);
            }
            try (var b = QueryCounter.start()) {
                wrapped.invokeAll(List.<Callable<Integer>>of(() -> {
                    select(this.ds1);
                    return 1;
                }, () -> {
                    select(this.ds2);
                    return 2;
                }));
                assertThat(b.count().select()).isEqualTo(2);
            }
        } finally {
            pool.shutdown();
        }
    }

    @Test
    void wrappedPlainExecutorCarriesScope() throws Exception {
        ExecutorService pool = Executors.newSingleThreadExecutor();
        try (var scope = QueryCounter.start()) {
            CountDownLatch done = new CountDownLatch(1);
            QueryCounter.wrap((java.util.concurrent.Executor) pool).execute(() -> {
                select(this.ds1);
                done.countDown();
            });
            done.await();
            assertThat(scope.count().select()).isEqualTo(1);
        } finally {
            pool.shutdown();
        }
    }

    @Test
    void workerScopeRestoredAfterWrappedTask() throws Exception {
        ExecutorService pool = Executors.newSingleThreadExecutor();
        try (var scope = QueryCounter.start()) {
            pool.submit(QueryCounter.wrap(() -> select(this.ds1))).get();
            pool.submit(() -> {
                assertThat(QueryCounter.current()).isNull();
                select(this.ds1);
            }).get();
            assertThat(scope.count().select()).isEqualTo(1);
        } finally {
            pool.shutdown();
        }
    }

    @Test
    void lateAsyncWorkStillCountsInClosedScope() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        ExecutorService pool = Executors.newSingleThreadExecutor();
        QueryCounter.Scope scope;
        Future<?> f;
        try (var s = QueryCounter.start()) {
            scope = s;
            f = pool.submit(QueryCounter.wrap(() -> {
                await(latch);
                select(this.ds1);
            }));
        }
        latch.countDown();
        f.get();
        pool.shutdown();
        assertThat(scope.count().select()).isEqualTo(1);
    }

    @Test
    void propagatesToVirtualThreadExecutor() throws Exception {
        assumeTrue(Runtime.version().feature() >= 21);
        ExecutorService vt = (ExecutorService) Executors.class.getMethod("newVirtualThreadPerTaskExecutor").invoke(null);
        try (var scope = QueryCounter.start()) {
            ExecutorService wrapped = QueryCounter.wrap(vt);
            List<Future<?>> fs = IntStream.range(0, 50)
                    .<Future<?>>mapToObj(i -> wrapped.submit(() -> select(this.ds1))).toList();
            for (Future<?> f : fs) {
                f.get();
            }
            assertThat(scope.count().select()).isEqualTo(50);
        } finally {
            vt.shutdown();
        }
    }

}
