package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.listener.count.DataSourceQueryCountListener;
import net.ttddyy.dsproxy.listener.QueryExecutionContext;
import net.ttddyy.dsproxy.listener.count.QueryCount;
import net.ttddyy.dsproxy.listener.count.QueryCountHolder;
import net.ttddyy.dsproxy.listener.count.SingleQueryCountStrategy;
import net.ttddyy.dsproxy.listener.count.ThreadQueryCountStrategy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * @author Tadaya Tsuyukubo
 */
public class DataSourceQueryCountListenerTest {

    private QueryInfo queryInfo;
    private List<QueryInfo> queryInfoList;
    private QueryExecutionContext queryExecutionContext;
    private DataSourceQueryCountListener listener;

    @BeforeEach
    public void setUp() {
        this.queryInfo = mock(QueryInfo.class);

        this.queryInfoList = new ArrayList<>();
        this.queryInfoList.add(this.queryInfo);

        this.queryExecutionContext = mock(QueryExecutionContext.class);
        given(this.queryExecutionContext.getDataSourceName()).willReturn("testDS");
        given(this.queryExecutionContext.getElapsedTime()).willReturn(123L);
        given(this.queryExecutionContext.getStatementType()).willReturn(StatementType.STATEMENT);
        given(this.queryExecutionContext.getQueries()).willReturn(this.queryInfoList);
        this.listener = new DataSourceQueryCountListener();
    }

    @AfterEach
    public void tearDown() {
        QueryCountHolder.clearAll();
        // put back to default strategy
        QueryCountHolder.setQueryCountStrategy(new ThreadQueryCountStrategy());
    }


    @Test
    public void testSelect() {
        given(queryInfo.getQuery()).willReturn("select * from emp");
        listener.afterQuery(queryExecutionContext);
        verifyQueryCount(1, 0, 0, 0, 0);
    }

    @Test
    public void testInsert() {
        given(queryInfo.getQuery()).willReturn("insert into emp (id) values (1)");
        listener.afterQuery(queryExecutionContext);
        verifyQueryCount(0, 1, 0, 0, 0);
    }

    @Test
    public void testUpdate() {
        given(queryInfo.getQuery()).willReturn("update emp set id = 1");
        listener.afterQuery(queryExecutionContext);
        verifyQueryCount(0, 0, 1, 0, 0);
    }

    @Test
    public void testDelete() {
        given(queryInfo.getQuery()).willReturn("delete * from emp");
        listener.afterQuery(queryExecutionContext);
        verifyQueryCount(0, 0, 0, 1, 0);
    }

    @Test
    public void testOther() {
        given(queryInfo.getQuery()).willReturn("create table aa(...)");
        listener.afterQuery(queryExecutionContext);
        verifyQueryCount(0, 0, 0, 0, 1);
    }

    private void verifyQueryCount(int select, int insert, int update, int delete, int other) {
        QueryCount queryCount = QueryCountHolder.getOrCreateQueryCount("testDS");
        assertThat(queryCount).isNotNull().isInstanceOf(QueryCount.class);
        assertThat(queryCount.getTime()).as("total time").isEqualTo(123L);
        assertThat(queryCount.getSelect()).as("num of select").isEqualTo(select);
        assertThat(queryCount.getInsert()).as("num of insert").isEqualTo(insert);
        assertThat(queryCount.getUpdate()).as("num of update").isEqualTo(update);
        assertThat(queryCount.getDelete()).as("num of delete").isEqualTo(delete);
        assertThat(queryCount.getOther()).as("num of other").isEqualTo(other);
    }

    @Test
    public void statement() {
        given(queryInfo.getQuery()).willReturn("foo");
        given(queryExecutionContext.getStatementType()).willReturn(StatementType.STATEMENT);
        listener.afterQuery(queryExecutionContext);

        verifyStatementTypeCount(1, 0, 0);
    }

    @Test
    public void prepared() {
        given(queryInfo.getQuery()).willReturn("foo");
        given(queryExecutionContext.getStatementType()).willReturn(StatementType.PREPARED);
        listener.afterQuery(queryExecutionContext);
        verifyStatementTypeCount(0, 1, 0);
    }

    @Test
    public void callable() {
        given(queryInfo.getQuery()).willReturn("foo");
        given(queryExecutionContext.getStatementType()).willReturn(StatementType.CALLABLE);
        listener.afterQuery(queryExecutionContext);
        verifyStatementTypeCount(0, 0, 1);
    }

    private void verifyStatementTypeCount(int statement, int prepared, int callable) {
        QueryCount queryCount = QueryCountHolder.getOrCreateQueryCount("testDS");
        assertThat(queryCount).isNotNull().isInstanceOf(QueryCount.class);
        assertThat(queryCount.getStatement()).as("num of statement").isEqualTo(statement);
        assertThat(queryCount.getPrepared()).as("num of prepared").isEqualTo(prepared);
        assertThat(queryCount.getCallable()).as("num of callable").isEqualTo(callable);
    }


    @Test
    public void threadLocalHolderStrategy() throws Exception {
        // set thread local strategy
        QueryCountHolder.setQueryCountStrategy(new ThreadQueryCountStrategy());

        // perform on main thread
        QueryInfo queryInfo = mock(QueryInfo.class);
        given(queryInfo.getQuery()).willReturn("insert into emp (id) values (1)");
        given(this.queryExecutionContext.getQueries()).willReturn(Collections.singletonList(queryInfo));

        this.listener.afterQuery(this.queryExecutionContext);

        // perform on separate thread
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Throwable> failureInThread = new AtomicReference<Throwable>();

        Runnable threadA = new Runnable() {
            @Override
            public void run() {
                QueryInfo queryInfo = mock(QueryInfo.class);
                given(queryInfo.getQuery()).willReturn("select * from emp");
                given(queryExecutionContext.getQueries()).willReturn(Collections.singletonList(queryInfo));
                listener.afterQuery(queryExecutionContext);

                // verify count within thread
                try {
                    QueryCount queryCount = QueryCountHolder.getOrCreateQueryCount("testDS");
                    assertThat(queryCount).isNotNull().isInstanceOf(QueryCount.class);
                    assertThat(queryCount.getSelect()).as("num of select").isEqualTo(1);
                    assertThat(queryCount.getInsert()).as("num of insert").isEqualTo(0);
                    assertThat(queryCount.getTotal()).as("num of queries").isEqualTo(1);
                } catch (Throwable e) {
                    failureInThread.set(e);
                }

                latch.countDown();
            }
        };
        new Thread(threadA).start();
        latch.await();

        if (failureInThread.get() != null) {
            throw new RuntimeException("Verification failure in separate thread", failureInThread.get());
        }

        // verify count in main thread
        QueryCount queryCount = QueryCountHolder.getOrCreateQueryCount("testDS");
        assertThat(queryCount).isNotNull().isInstanceOf(QueryCount.class);
        assertThat(queryCount.getSelect()).as("num of select").isEqualTo(0);
        assertThat(queryCount.getInsert()).as("num of insert").isEqualTo(1);
        assertThat(queryCount.getTotal()).as("num of queries").isEqualTo(1);

    }

    @Test
    public void instanceHolderStrategy() throws Exception {
        // set query count holder strategy
        QueryCountHolder.setQueryCountStrategy(new SingleQueryCountStrategy());

        // perform on main thread
        QueryInfo queryInfo = mock(QueryInfo.class);
        given(queryInfo.getQuery()).willReturn("insert into emp (id) values (1)");
        given(this.queryExecutionContext.getQueries()).willReturn(Collections.singletonList(queryInfo));

        listener.afterQuery(queryExecutionContext);

        // perform on separate thread
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Throwable> failureInThread = new AtomicReference<Throwable>();
        final AtomicReference<QueryCount> queryCountFromHolderInDifferentThread = new AtomicReference<QueryCount>();

        Runnable threadA = new Runnable() {
            @Override
            public void run() {
                QueryInfo queryInfo = mock(QueryInfo.class);
                given(queryInfo.getQuery()).willReturn("select * from emp");
                given(queryExecutionContext.getQueries()).willReturn(Collections.singletonList(queryInfo));

                listener.afterQuery(queryExecutionContext);

                // verify count within thread
                try {
                    QueryCount queryCountFromThreadLocal = QueryCountHolder.getOrCreateQueryCount("testDS");
                    queryCountFromHolderInDifferentThread.set(queryCountFromThreadLocal);

                    QueryCount queryCount = QueryCountHolder.getOrCreateQueryCount("testDS");
                    assertThat(queryCount).isNotNull().isInstanceOf(QueryCount.class);
                    assertThat(queryCount.getSelect()).as("num of select").isEqualTo(1);
                    assertThat(queryCount.getInsert()).as("num of insert").isEqualTo(1);
                    assertThat(queryCount.getTotal()).as("num of queries").isEqualTo(2);
                } catch (Throwable e) {
                    failureInThread.set(e);
                }

                latch.countDown();
            }
        };
        new Thread(threadA).start();
        latch.await();

        if (failureInThread.get() != null) {
            fail("Verification failure in separate thread", failureInThread.get());
        }

        // verify count in main thread
        QueryCount queryCount = QueryCountHolder.getOrCreateQueryCount("testDS");
        assertThat(queryCount).isNotNull().isInstanceOf(QueryCount.class);
        assertThat(queryCount.getSelect()).as("num of select").isEqualTo(1);
        assertThat(queryCount.getInsert()).as("num of insert").isEqualTo(1);
        assertThat(queryCount.getTotal()).as("num of queries").isEqualTo(2);

        // verify QueryCountHolder.get() performed in separate thread
        QueryCount queryCountFromThreadLocal = queryCountFromHolderInDifferentThread.get();
        assertThat(queryCountFromThreadLocal)
                .as("QueryCountHolder should be populated in separate thread")
                .isNotNull()
                .isSameAs(queryCount);
    }

}
