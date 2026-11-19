package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.TestUtils;
import net.ttddyy.dsproxy.listener.ProxyDataSourceListener;
import net.ttddyy.dsproxy.listener.QueryExecutionContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for listener registration on {@link ProxyDataSourceBuilder}.
 */
class ProxyDataSourceBuilderListenerTest {

    private DataSource actualDataSource;

    @BeforeEach
    void setup() throws Exception {
        this.actualDataSource = TestUtils.getDataSourceWithData();
    }

    @AfterEach
    void teardown() throws Exception {
        TestUtils.shutdown(this.actualDataSource);
    }

    private static ProxyDataSourceListener recording(String name, List<String> calls) {
        return new ProxyDataSourceListener() {
            @Override
            public void afterQuery(QueryExecutionContext context) {
                calls.add(name + ".afterQuery");
            }
        };
    }

    @Test
    void lambdaShortcutsAndListenersRunInRegistrationOrder() throws Exception {
        List<String> calls = new ArrayList<>();
        DataSource ds = ProxyDataSourceBuilder.create(this.actualDataSource)
                .afterQuery(c -> calls.add("lambda"))
                .listener(recording("full", calls))
                .afterQuery(c -> calls.add("lambda2"))
                .build();

        try (Connection conn = ds.getConnection(); Statement st = conn.createStatement()) {
            st.executeQuery("SELECT * FROM emp").close();
        }

        assertThat(calls).containsExactly("lambda", "full.afterQuery", "lambda2");
    }

    @Test
    void beforeMethodShortcutSeesEveryJdbcMethod() throws Exception {
        List<String> names = new ArrayList<>();
        DataSource ds = ProxyDataSourceBuilder.create(this.actualDataSource)
                .beforeMethod(c -> names.add(c.getMethod().getName()))
                .build();

        Connection conn = ds.getConnection();
        Statement st = conn.createStatement();
        st.executeQuery("SELECT * FROM emp").close();
        st.close();
        conn.close();

        assertThat(names).containsSubsequence("getConnection", "createStatement", "executeQuery", "close");
    }

    @Test
    void noListenersStillWorks() throws Exception {
        DataSource ds = ProxyDataSourceBuilder.create(this.actualDataSource).build();
        try (Connection conn = ds.getConnection(); Statement st = conn.createStatement()) {
            assertThat(st.executeQuery("SELECT * FROM emp").next()).isTrue();
        }
    }
}
