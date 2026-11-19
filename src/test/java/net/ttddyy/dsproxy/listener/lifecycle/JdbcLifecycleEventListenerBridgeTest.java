package net.ttddyy.dsproxy.listener.lifecycle;

import net.ttddyy.dsproxy.TestUtils;
import net.ttddyy.dsproxy.listener.MethodExecutionContext;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tadaya Tsuyukubo
 */
class JdbcLifecycleEventListenerBridgeTest {

    private DataSource dataSource;

    @AfterEach
    void teardown() throws Exception {
        if (this.dataSource != null) {
            TestUtils.shutdown(this.dataSource);
        }
    }

    @Test
    void lifecycleListenerReceivesTypedCallbacks() throws Exception {
        AtomicInteger beforeCommit = new AtomicInteger();
        AtomicInteger afterExecuteQuery = new AtomicInteger();
        JdbcLifecycleEventListener lifecycleListener = new JdbcLifecycleEventListener() {
            @Override
            public void beforeCommit(MethodExecutionContext executionContext) {
                beforeCommit.incrementAndGet();
            }

            @Override
            public void afterExecuteQuery(MethodExecutionContext executionContext) {
                afterExecuteQuery.incrementAndGet();
            }
        };

        this.dataSource = ProxyDataSourceBuilder.create(TestUtils.getDataSourceWithData())
                .listener(JdbcLifecycleEventListener.asListener(lifecycleListener))
                .build();

        try (Connection conn = this.dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery("SELECT id FROM emp")) {
                rs.next();
            }
            conn.commit();
        }

        assertThat(beforeCommit).hasValue(1);
        assertThat(afterExecuteQuery).hasValue(1);
    }

}
