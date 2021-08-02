package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.TestUtils;
import net.ttddyy.dsproxy.listener.logging.DefaultQueryLogEntryCreator;
import net.ttddyy.dsproxy.listener.logging.QueryLogEntryCreator;
import net.ttddyy.dsproxy.listener.logging.SLF4JSlowQueryListener;
import net.ttddyy.dsproxy.support.ProxyDataSource;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.junit.After;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * @author Tadaya Tsuyukubo
 */
public class SlowQueryListenerTest {

    private DataSource jdbcDataSource;

    @After
    public void teardown() throws Exception {
        if (this.jdbcDataSource != null) {
            TestUtils.shutdown(this.jdbcDataSource);
        }
    }


    @Test
    public void onSlowQuery() throws Exception {

        final ExecutionInfo executionInfo = new ExecutionInfo();
        final List<QueryInfo> queryInfo = new ArrayList<QueryInfo>();

        final AtomicInteger counter = new AtomicInteger();

        SlowQueryListener listener = new SlowQueryListener() {
            @Override
            protected void onSlowQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList, long startTimeInMills) {
                counter.incrementAndGet();
                assertThat(execInfo).isSameAs(executionInfo);
                assertThat(queryInfoList).isSameAs(queryInfo);
            }
        };
        listener.setThreshold(50);
        listener.setThresholdTimeUnit(TimeUnit.MILLISECONDS);

        // simulate slow query
        listener.beforeQuery(executionInfo, queryInfo);
        TimeUnit.MILLISECONDS.sleep(200);  // ample time
        listener.afterQuery(executionInfo, queryInfo);

        assertThat(counter.get()).as("callback should be called, and it should be once").isEqualTo(1);
    }

    @Test
    public void onSlowQueryWithFastQuery() throws Exception {

        SlowQueryListener listener = new SlowQueryListener() {
            @Override
            protected void onSlowQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList, long startTimeInMills) {
                fail("onSlowQuery method should not be called for fast query");
            }
        };
        listener.setThreshold(100);
        listener.setThresholdTimeUnit(TimeUnit.MILLISECONDS);

        final ExecutionInfo executionInfo = new ExecutionInfo();
        final List<QueryInfo> queryInfo = new ArrayList<QueryInfo>();

        // calling immediately after
        listener.beforeQuery(executionInfo, queryInfo);
        listener.afterQuery(executionInfo, queryInfo);
    }


    @Test
    public void executionTime() throws Exception {

        final AtomicLong executionTime = new AtomicLong(0);
        QueryLogEntryCreator queryLogEntryCreator = new DefaultQueryLogEntryCreator() {
            @Override
            public String getLogEntry(ExecutionInfo execInfo, List<QueryInfo> queryInfoList, boolean writeDataSourceName, boolean writeConnectionId, boolean writeIsolation) {
                executionTime.set(execInfo.getElapsedTime());
                return super.getLogEntry(execInfo, queryInfoList, writeDataSourceName, writeConnectionId, writeIsolation);
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
