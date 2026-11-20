package net.ttddyy.dsproxy.listener;

import static net.ttddyy.dsproxy.QueryExecutionContextBuilder.withQueries;
import net.ttddyy.dsproxy.proxy.MutableQueryExecutionContext;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.TestUtils;
import net.ttddyy.dsproxy.listener.logging.DefaultQueryLogEntryCreator;
import net.ttddyy.dsproxy.listener.logging.QueryLogEntryCreator;
import net.ttddyy.dsproxy.listener.logging.SLF4JSlowQueryListener;
import net.ttddyy.dsproxy.support.ProxyDataSource;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Statement;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;

/**
 * @author Tadaya Tsuyukubo
 */
public class SlowQueryListenerTest {

    private DataSource jdbcDataSource;

    @AfterEach
    void teardown() throws Exception {
        if (this.jdbcDataSource != null) {
            TestUtils.shutdown(this.jdbcDataSource);
        }
    }


    @Test
    void onSlowQuery() throws Exception {

        final MutableQueryExecutionContext executionInfo = new MutableQueryExecutionContext();
        final List<QueryInfo> queryInfo = new ArrayList<QueryInfo>();

        final AtomicInteger counter = new AtomicInteger();

        SlowQueryListener listener = new SlowQueryListener() {
            @Override
            protected void onSlowQuery(QueryExecutionContext execInfo, Duration elapsedTime) {
                counter.incrementAndGet();
                assertThat(elapsedTime).isGreaterThanOrEqualTo(Duration.ofMillis(50));
                assertThat(execInfo).isSameAs(executionInfo);
                assertThat(execInfo.getQueries()).isSameAs(queryInfo);
            }
        };
        listener.setThreshold(Duration.ofMillis(50));

        // simulate slow query
        listener.beforeQuery(withQueries(executionInfo, queryInfo));
        TimeUnit.MILLISECONDS.sleep(200);  // ample time
        listener.afterQuery(withQueries(executionInfo, queryInfo));

        assertThat(counter.get()).as("callback should be called, and it should be once").isOne();
    }

    @Test
    void beforeQueryWithoutThresholdFails() {
        SlowQueryListener listener = new SlowQueryListener();
        assertThatThrownBy(() -> listener.beforeQuery(new MutableQueryExecutionContext()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("threshold is not set");
    }

    @Test
    void thresholdMustBePositive() {
        SlowQueryListener listener = new SlowQueryListener();
        assertThatThrownBy(() -> listener.setThreshold(Duration.ZERO)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> listener.setThreshold(Duration.ofMillis(-1))).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> listener.setThreshold(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void onSlowQueryWithFastQuery() throws Exception {

        SlowQueryListener listener = new SlowQueryListener() {
            @Override
            protected void onSlowQuery(QueryExecutionContext execInfo, Duration elapsedTime) {
                fail("onSlowQuery method should not be called for fast query");
            }
        };
        listener.setThreshold(Duration.ofMillis(100));

        final MutableQueryExecutionContext executionInfo = new MutableQueryExecutionContext();
        final List<QueryInfo> queryInfo = new ArrayList<QueryInfo>();

        // calling immediately after
        listener.beforeQuery(withQueries(executionInfo, queryInfo));
        listener.afterQuery(withQueries(executionInfo, queryInfo));
    }


    @Test
    void executionTime() throws Exception {

        final AtomicLong executionTime = new AtomicLong(0);
        QueryLogEntryCreator queryLogEntryCreator = new DefaultQueryLogEntryCreator() {
            @Override
            public String getLogEntry(QueryExecutionContext execInfo, boolean writeDataSourceName, boolean writeConnectionId, boolean writeIsolation) {
                executionTime.set(execInfo.getElapsedTime().toMillis());
                return super.getLogEntry(execInfo, writeDataSourceName, writeConnectionId, writeIsolation);
            }
        };

        SLF4JSlowQueryListener listener = new SLF4JSlowQueryListener(100, TimeUnit.MILLISECONDS);
        listener.setQueryLogEntryCreator(queryLogEntryCreator);


        this.jdbcDataSource = TestUtils.getDataSourceWithData();
        ProxyDataSource pds = ProxyDataSourceBuilder.create(jdbcDataSource).listener(listener).build();

        String funcSleep = "CREATE FUNCTION funcSleep()" +
                " RETURNS INTEGER" +
                " LANGUAGE JAVA DETERMINISTIC NO SQL" +
                " EXTERNAL NAME 'CLASSPATH:net.ttddyy.dsproxy.listener.SlowQueryListenerTest.funcSleep'";

        Connection conn = pds.getConnection();
        Statement st = conn.createStatement();
        st.execute(funcSleep);

        CallableStatement cs = conn.prepareCall("CALL funcSleep()");
        cs.execute();

        assertThat(executionTime.get()).as("execInfo.elapsedTime should be populated").isGreaterThanOrEqualTo(100).isLessThan(300);
    }


    /**
     * hsqldb function to sleep 200 msec
     */
    public static int funcSleep() throws Exception {
        TimeUnit.MILLISECONDS.sleep(200);
        return 0;
    }


}
