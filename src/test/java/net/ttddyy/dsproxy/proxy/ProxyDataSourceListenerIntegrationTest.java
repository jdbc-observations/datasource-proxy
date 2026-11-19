package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.TestUtils;
import net.ttddyy.dsproxy.listener.MethodExecutionContext;
import net.ttddyy.dsproxy.listener.ProxyDataSourceListener;
import net.ttddyy.dsproxy.listener.QueryExecutionContext;
import net.ttddyy.dsproxy.support.ProxyDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifies that the proxy logic dispatches through {@link ProxyDataSourceListener} against a real database.
 *
 * @author Tadaya Tsuyukubo
 */
class ProxyDataSourceListenerIntegrationTest {

    private DataSource actualDataSource;

    @BeforeEach
    void setup() throws Exception {
        this.actualDataSource = TestUtils.getDataSourceWithData();
    }

    @AfterEach
    void teardown() throws Exception {
        TestUtils.shutdown(this.actualDataSource);
    }

    private DataSource proxy(ProxyDataSourceListener... listeners) {
        ProxyConfig.Builder builder = ProxyConfig.Builder.create();
        for (ProxyDataSourceListener listener : listeners) {
            builder.listener(listener);
        }
        ProxyDataSource proxyDataSource = new ProxyDataSource(this.actualDataSource);
        proxyDataSource.setProxyConfig(builder.build());
        return proxyDataSource;
    }

    private static class RecordingListener implements ProxyDataSourceListener {
        private final List<QueryExecutionContext> afterQueries = new ArrayList<>();

        @Override
        public void afterQuery(QueryExecutionContext context) {
            this.afterQueries.add(context);
        }
    }

    @Test
    void statementBatchReportsAllQueries() throws Exception {
        RecordingListener listener = new RecordingListener();
        try (Connection conn = proxy(listener).getConnection(); Statement st = conn.createStatement()) {
            st.addBatch("INSERT INTO emp (id, name) VALUES (10, 'a')");
            st.addBatch("INSERT INTO emp (id, name) VALUES (11, 'b')");
            st.executeBatch();
        }

        assertThat(listener.afterQueries).hasSize(1);
        QueryExecutionContext ctx = listener.afterQueries.get(0);
        assertThat(ctx.isBatch()).isTrue();
        assertThat(ctx.getBatchSize()).isEqualTo(2);
        assertThat(ctx.getQueries()).extracting(QueryInfo::getQuery).containsExactly(
                "INSERT INTO emp (id, name) VALUES (10, 'a')",
                "INSERT INTO emp (id, name) VALUES (11, 'b')");
    }

    @Test
    void preparedBatchReportsBatchSize() throws Exception {
        RecordingListener listener = new RecordingListener();
        try (Connection conn = proxy(listener).getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO emp (id, name) VALUES (?, 'x')")) {
            for (int id = 20; id < 23; id++) {
                ps.setInt(1, id);
                ps.addBatch();
            }
            ps.executeBatch();
        }

        assertThat(listener.afterQueries).hasSize(1);
        QueryExecutionContext ctx = listener.afterQueries.get(0);
        assertThat(ctx.isBatch()).isTrue();
        assertThat(ctx.getBatchSize()).isEqualTo(3);
        assertThat(ctx.getQueries()).hasSize(1);
        assertThat(ctx.getQueries().get(0).getParametersList()).hasSize(3);
    }

    @Test
    void failedQueryStillCallsAfterQuery() throws Exception {
        RecordingListener listener = new RecordingListener();
        SQLException thrown = null;
        try (Connection conn = proxy(listener).getConnection(); Statement st = conn.createStatement()) {
            st.executeQuery("SELECT * FROM no_such_table");
        } catch (SQLException ex) {
            thrown = ex;
        }

        assertThat((Throwable) thrown).isNotNull();
        assertThat(listener.afterQueries).hasSize(1);
        QueryExecutionContext ctx = listener.afterQueries.get(0);
        assertThat(ctx.isSuccess()).isFalse();
        assertThat(ctx.getThrowable()).isSameAs((Throwable) thrown);
    }

    @Test
    void beforeMethodCanReplaceArguments() throws Exception {
        ProxyDataSourceListener listener = new ProxyDataSourceListener() {
            @Override
            public void beforeMethod(MethodExecutionContext context) {
                if ("executeQuery".equals(context.getMethod().getName())) {
                    context.setMethodArgs(new Object[]{"SELECT 2 FROM (VALUES(0))"});
                }
            }
        };
        try (Connection conn = proxy(listener).getConnection(); Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery("SELECT 1 FROM (VALUES(0))");
            rs.next();
            assertThat(rs.getInt(1)).isEqualTo(2);
        }
    }

    @Test
    void afterMethodExceptionDoesNotHideJdbcException() throws Exception {
        RuntimeException listenerException = new RuntimeException("from afterMethod");
        ProxyDataSourceListener listener = new ProxyDataSourceListener() {
            @Override
            public void afterMethod(MethodExecutionContext context) {
                if ("executeQuery".equals(context.getMethod().getName())) {
                    throw listenerException;
                }
            }
        };
        try (Connection conn = proxy(listener).getConnection(); Statement st = conn.createStatement()) {
            assertThatThrownBy(() -> st.executeQuery("SELECT * FROM no_such_table"))
                    .isInstanceOf(SQLException.class)
                    .satisfies(ex -> assertThat(ex.getSuppressed()).containsExactly(listenerException));
        }
    }

    @Test
    void afterQueryExceptionDoesNotHideJdbcException() throws Exception {
        RuntimeException listenerException = new RuntimeException("from afterQuery");
        ProxyDataSourceListener listener = new ProxyDataSourceListener() {
            @Override
            public void afterQuery(QueryExecutionContext context) {
                throw listenerException;
            }
        };
        try (Connection conn = proxy(listener).getConnection(); Statement st = conn.createStatement()) {
            assertThatThrownBy(() -> st.executeQuery("SELECT * FROM no_such_table"))
                    .isInstanceOf(SQLException.class)
                    .satisfies(ex -> assertThat(ex.getSuppressed()).containsExactly(listenerException));
        }
    }

    @Test
    void beforeQueryExceptionPropagatesAndSkipsExecution() throws Exception {
        RuntimeException listenerException = new RuntimeException("from beforeQuery");
        RecordingListener later = new RecordingListener();
        ProxyDataSourceListener failing = new ProxyDataSourceListener() {
            @Override
            public void beforeQuery(QueryExecutionContext context) {
                throw listenerException;
            }
        };
        List<String> laterBefore = new ArrayList<>();
        ProxyDataSourceListener laterBeforeListener = new ProxyDataSourceListener() {
            @Override
            public void beforeQuery(QueryExecutionContext context) {
                laterBefore.add("called");
            }
        };
        try (Connection conn = proxy(failing, laterBeforeListener, later).getConnection();
             Statement st = conn.createStatement()) {
            assertThatThrownBy(() -> st.executeUpdate("INSERT INTO emp (id, name) VALUES (30, 'z')"))
                    .isSameAs(listenerException);
        }

        assertThat(laterBefore).isEmpty();
        assertThat(TestUtils.countTable(this.actualDataSource, "emp")).isEqualTo(2);
    }

    @Test
    void afterQueryExceptionPropagatesAndSkipsLaterListeners() throws Exception {
        RuntimeException listenerException = new RuntimeException("from afterQuery");
        ProxyDataSourceListener failing = new ProxyDataSourceListener() {
            @Override
            public void afterQuery(QueryExecutionContext context) {
                throw listenerException;
            }
        };
        RecordingListener later = new RecordingListener();
        try (Connection conn = proxy(failing, later).getConnection(); Statement st = conn.createStatement()) {
            assertThatThrownBy(() -> st.executeQuery("SELECT * FROM emp")).isSameAs(listenerException);
        }

        assertThat(later.afterQueries).isEmpty();
    }

    @Test
    void elapsedTimeIsNonNegativeDuration() throws Exception {
        RecordingListener listener = new RecordingListener();
        List<MethodExecutionContext> afterMethods = new ArrayList<>();
        ProxyDataSourceListener methodListener = new ProxyDataSourceListener() {
            @Override
            public void afterMethod(MethodExecutionContext context) {
                afterMethods.add(context);
            }
        };
        try (Connection conn = proxy(listener, methodListener).getConnection(); Statement st = conn.createStatement()) {
            st.executeQuery("SELECT * FROM emp");
        }

        assertThat(listener.afterQueries).hasSize(1);
        assertThat(listener.afterQueries.get(0).getElapsedTime()).isNotNull().matches(d -> !d.isNegative(), "not negative");
        assertThat(afterMethods).isNotEmpty().allSatisfy(ctx ->
                assertThat(ctx.getElapsedTime()).isNotNull().matches(d -> !d.isNegative(), "not negative"));
    }

}
