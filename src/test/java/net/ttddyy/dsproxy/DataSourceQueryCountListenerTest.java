package net.ttddyy.dsproxy;

import java.time.Duration;
import static net.ttddyy.dsproxy.QueryExecutionContextBuilder.withQueries;
import net.ttddyy.dsproxy.proxy.MutableQueryExecutionContext;
import net.ttddyy.dsproxy.listener.DataSourceQueryCountListener;
import net.ttddyy.dsproxy.listener.SingleQueryCountHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * @author Tadaya Tsuyukubo
 */
class DataSourceQueryCountListenerTest {

    private QueryInfo queryInfo;
    private List<QueryInfo> queryInfoList;
    private MutableQueryExecutionContext executionInfo;
    private DataSourceQueryCountListener listener;

    @BeforeEach
    void setUp() {
        queryInfo = mock(QueryInfo.class);

        queryInfoList = new ArrayList<QueryInfo>();
        queryInfoList.add(queryInfo);

        executionInfo = new MutableQueryExecutionContext();
        executionInfo.setDataSourceName("testDS");
        executionInfo.setElapsedTime(Duration.ofMillis(123));
        executionInfo.setStatementType(StatementType.STATEMENT);


        listener = new DataSourceQueryCountListener();
    }

    @AfterEach
    void tearDown() {
        QueryCountHolder.clear();
    }


    @Test
    void select() {
        given(queryInfo.getQuery()).willReturn("select * from emp");
        listener.afterQuery(withQueries(executionInfo, queryInfoList));
        verifyQueryCount(1, 0, 0, 0, 0);
    }

    @Test
    void insert() {
        given(queryInfo.getQuery()).willReturn("insert into emp (id) values (1)");
        listener.afterQuery(withQueries(executionInfo, queryInfoList));
        verifyQueryCount(0, 1, 0, 0, 0);
    }

    @Test
    void update() {
        given(queryInfo.getQuery()).willReturn("update emp set id = 1");
        listener.afterQuery(withQueries(executionInfo, queryInfoList));
        verifyQueryCount(0, 0, 1, 0, 0);
    }

    @Test
    void delete() {
        given(queryInfo.getQuery()).willReturn("delete * from emp");
        listener.afterQuery(withQueries(executionInfo, queryInfoList));
        verifyQueryCount(0, 0, 0, 1, 0);
    }

    @Test
    void other() {
        given(queryInfo.getQuery()).willReturn("create table aa(...)");
        listener.afterQuery(withQueries(executionInfo, queryInfoList));
        verifyQueryCount(0, 0, 0, 0, 1);
    }

    private void verifyQueryCount(int select, int insert, int update, int delete, int other) {
        QueryCount queryCount = QueryCountHolder.get("testDS");
        assertThat(queryCount).isInstanceOf(QueryCount.class);
        assertThat(queryCount.getTime()).as("total time").isEqualTo(123L);
        assertThat(queryCount.getSelect()).as("num of select").isEqualTo(select);
        assertThat(queryCount.getInsert()).as("num of insert").isEqualTo(insert);
        assertThat(queryCount.getUpdate()).as("num of update").isEqualTo(update);
        assertThat(queryCount.getDelete()).as("num of delete").isEqualTo(delete);
        assertThat(queryCount.getOther()).as("num of other").isEqualTo(other);
    }

    @Test
    void statement() {
        given(queryInfo.getQuery()).willReturn("foo");
        executionInfo.setStatementType(StatementType.STATEMENT);
        listener.afterQuery(withQueries(executionInfo, queryInfoList));

        verifyStatementTypeCount(1, 0, 0);
    }

    @Test
    void prepared() {
        given(queryInfo.getQuery()).willReturn("foo");
        executionInfo.setStatementType(StatementType.PREPARED);
        listener.afterQuery(withQueries(executionInfo, queryInfoList));
        verifyStatementTypeCount(0, 1, 0);
    }

    @Test
    void callable() {
        given(queryInfo.getQuery()).willReturn("foo");
        executionInfo.setStatementType(StatementType.CALLABLE);
        listener.afterQuery(withQueries(executionInfo, queryInfoList));
        verifyStatementTypeCount(0, 0, 1);
    }

    private void verifyStatementTypeCount(int statement, int prepared, int callable) {
        QueryCount queryCount = QueryCountHolder.get("testDS");
        assertThat(queryCount).isInstanceOf(QueryCount.class);
        assertThat(queryCount.getStatement()).as("num of statement").isEqualTo(statement);
        assertThat(queryCount.getPrepared()).as("num of prepared").isEqualTo(prepared);
        assertThat(queryCount.getCallable()).as("num of callable").isEqualTo(callable);
    }


    @Test
    void threadLocalHolderStrategy() throws Exception {
        // perform on main thread
        QueryInfo queryInfo = mock(QueryInfo.class);
        given(queryInfo.getQuery()).willReturn("insert into emp (id) values (1)");
        // use default strategy
        listener.afterQuery(withQueries(executionInfo, Collections.singletonList(queryInfo)));

        // perform on separate thread
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Throwable> failureInThread = new AtomicReference<Throwable>();

        Runnable threadA = new Runnable() {
            @Override
            public void run() {
                QueryInfo queryInfo = mock(QueryInfo.class);
                given(queryInfo.getQuery()).willReturn("select * from emp");
                listener.afterQuery(withQueries(executionInfo, Collections.singletonList(queryInfo)));

                // verify count within thread
                try {
                    QueryCount queryCount = QueryCountHolder.get("testDS");
                    assertThat(queryCount).isInstanceOf(QueryCount.class);
                    assertThat(queryCount.getSelect()).as("num of select").isOne();
                    assertThat(queryCount.getInsert()).as("num of insert").isZero();
                    assertThat(queryCount.getTotal()).as("num of queries").isOne();
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
        QueryCount queryCount = QueryCountHolder.get("testDS");
        assertThat(queryCount).isInstanceOf(QueryCount.class);
        assertThat(queryCount.getSelect()).as("num of select").isZero();
        assertThat(queryCount.getInsert()).as("num of insert").isOne();
        assertThat(queryCount.getTotal()).as("num of queries").isOne();

    }

    @Test
    void instanceHolderStrategy() throws Exception {
        // set query count holder strategy
        listener.setQueryCountStrategy(new SingleQueryCountHolder());

        // perform on main thread
        QueryInfo queryInfo = mock(QueryInfo.class);
        given(queryInfo.getQuery()).willReturn("insert into emp (id) values (1)");
        listener.afterQuery(withQueries(executionInfo, Collections.singletonList(queryInfo)));

        // perform on separate thread
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Throwable> failureInThread = new AtomicReference<Throwable>();
        final AtomicReference<QueryCount> queryCountFromHolderInDifferentThread = new AtomicReference<QueryCount>();

        Runnable threadA = new Runnable() {
            @Override
            public void run() {
                QueryInfo queryInfo = mock(QueryInfo.class);
                given(queryInfo.getQuery()).willReturn("select * from emp");
                listener.afterQuery(withQueries(executionInfo, Collections.singletonList(queryInfo)));

                // verify count within thread
                try {
                    QueryCount queryCountFromThreadLocal = QueryCountHolder.get("testDS");
                    queryCountFromHolderInDifferentThread.set(queryCountFromThreadLocal);

                    QueryCount queryCount = listener.getQueryCountStrategy().getOrCreateQueryCount("testDS");
                    assertThat(queryCount).isInstanceOf(QueryCount.class);
                    assertThat(queryCount.getSelect()).as("num of select").isOne();
                    assertThat(queryCount.getInsert()).as("num of insert").isOne();
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
            throw new RuntimeException("Verification failure in separate thread", failureInThread.get());
        }

        // verify count in main thread
        QueryCount queryCount = listener.getQueryCountStrategy().getOrCreateQueryCount("testDS");
        assertThat(queryCount).isInstanceOf(QueryCount.class);
        assertThat(queryCount.getSelect()).as("num of select").isOne();
        assertThat(queryCount.getInsert()).as("num of insert").isOne();
        assertThat(queryCount.getTotal()).as("num of queries").isEqualTo(2);

        // verify QueryCountHolder.get() performed in separate thread
        QueryCount queryCountFromThreadLocal = queryCountFromHolderInDifferentThread.get();
        assertThat(queryCountFromThreadLocal)
                .as("QueryCountHolder should be populated in separate thread")
                .isNotNull()
                .isSameAs(queryCount);
    }

}
