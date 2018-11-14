package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.DatabaseTest;
import net.ttddyy.dsproxy.DatabaseType;
import net.ttddyy.dsproxy.DbResourceCleaner;
import net.ttddyy.dsproxy.EnabledOnDatabase;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * @author Tadaya Tsuyukubo
 */
@DatabaseTest
public class SlowQueryListenerDbTest {

    private DataSource jdbcDataSource;
    private DbResourceCleaner cleaner;

    public SlowQueryListenerDbTest(DataSource jdbcDataSource, DbResourceCleaner cleaner) {
        this.jdbcDataSource = jdbcDataSource;
        this.cleaner = cleaner;
    }

    @Test
    public void onSlowQuery() throws Exception {

        final QueryExecutionContext queryExecutionContext = new QueryExecutionContext();

        final AtomicInteger counter = new AtomicInteger();

        SlowQueryListener listener = new SlowQueryListener(50, TimeUnit.MILLISECONDS, queryContext -> {
            counter.incrementAndGet();
            assertThat(queryContext).isSameAs(queryExecutionContext);
        });

        // simulate slow query
        listener.beforeQuery(queryExecutionContext);
        TimeUnit.MILLISECONDS.sleep(200);  // ample time
        listener.afterQuery(queryExecutionContext);

        assertThat(counter.get()).as("callback should be called, and it should be once").isEqualTo(1);
    }

    @Test
    public void onSlowQueryWithFastQuery() {

        SlowQueryListener listener = new SlowQueryListener(100, TimeUnit.MILLISECONDS, queryContext -> {
            fail("onSlowQuery method should not be called for fast query");
        });

        final QueryExecutionContext queryExecutionContext = new QueryExecutionContext();

        // calling immediately after
        listener.beforeQuery(queryExecutionContext);
        listener.afterQuery(queryExecutionContext);
    }


    @EnabledOnDatabase(DatabaseType.HSQL)
    public void executionTime() throws Exception {

        final AtomicLong executionTime = new AtomicLong(0);
        SlowQueryListener listener = new SlowQueryListener(100, TimeUnit.MILLISECONDS, executionContext -> {
            executionTime.set(executionContext.getElapsedTime());
        });

        DataSource pds = ProxyDataSourceBuilder.create(jdbcDataSource).listener(listener).build();

        String funcSleep = "CREATE FUNCTION funcSleep()" +
                " RETURNS INTEGER" +
                " LANGUAGE JAVA DETERMINISTIC NO SQL" +
                " EXTERNAL NAME 'CLASSPATH:net.ttddyy.dsproxy.listener.SlowQueryListenerDbTest.funcSleep'";

        Connection conn = pds.getConnection();
        Statement st = conn.createStatement();
        this.cleaner.add(conn);
        this.cleaner.add(st);
        st.execute(funcSleep);

        CallableStatement cs = conn.prepareCall("CALL funcSleep()");
        this.cleaner.add(cs);
        cs.execute();

        assertThat(executionTime.get()).as("queryContext.elapsedTime should be populated").isGreaterThanOrEqualTo(100).isLessThan(300);
    }


    /**
     * hsqldb function to sleep 200 msec
     */
    public static int funcSleep() throws Exception {
        TimeUnit.MILLISECONDS.sleep(200);
        return 0;
    }


}
