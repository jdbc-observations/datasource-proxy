package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.listener.QueryTypeDetector;
import net.ttddyy.dsproxy.listener.QueryUtils;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DataSourceQueryCounterTest {

    private static final String CTE =
            "WITH e AS (SELECT * FROM emp) SELECT * FROM e";

    private DataSource actual;

    private DataSourceQueryCounter counter;

    @BeforeEach
    void setup() throws Exception {
        this.actual = TestUtils.getDataSourceWithData();
        this.counter = new DataSourceQueryCounter();
    }

    @AfterEach
    void teardown() throws Exception {
        TestUtils.shutdown(this.actual);
    }

    private DataSource proxy(String name) {
        return ProxyDataSourceBuilder.create(this.actual).name(name).countQuery(this.counter).build();
    }

    private static void execute(DataSource ds, String sql) throws SQLException {
        try (Connection conn = ds.getConnection(); Statement st = conn.createStatement()) {
            st.execute(sql);
        }
    }

    @Test
    void countsByTypeStatementTypeAndOutcome() throws Exception {
        DataSource ds = proxy("ds");
        try (Connection conn = ds.getConnection(); Statement st = conn.createStatement()) {
            st.executeQuery("SELECT * FROM emp").close();
            st.executeUpdate("UPDATE emp SET name='x' WHERE id=1");
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO emp (id, name) VALUES (?, ?)")) {
                ps.setInt(1, 3);
                ps.setString(2, "baz");
                ps.executeUpdate();
            }
            st.execute("DELETE FROM emp WHERE id=3");
            st.execute("CREATE TABLE t (id INT)");
        }

        QueryCount c = this.counter.snapshot("ds");
        assertThat(c.select()).isEqualTo(1);
        assertThat(c.insert()).isEqualTo(1);
        assertThat(c.update()).isEqualTo(1);
        assertThat(c.delete()).isEqualTo(1);
        assertThat(c.other()).isEqualTo(1);
        assertThat(c.statement()).isEqualTo(4);
        assertThat(c.prepared()).isEqualTo(1);
        assertThat(c.callable()).isZero();
        assertThat(c.total()).isEqualTo(5);
        assertThat(c.success()).isEqualTo(5);
        assertThat(c.failure()).isZero();
        assertThat(c.time()).isPositive();
        assertThat(this.counter.snapshot()).isEqualTo(c);
    }

    @Test
    void batchAndFailure() throws Exception {
        DataSource ds = proxy("ds");
        try (Connection conn = ds.getConnection(); Statement st = conn.createStatement()) {
            st.addBatch("INSERT INTO emp VALUES (10,'a')");
            st.addBatch("UPDATE emp SET name='b' WHERE id=10");
            st.executeBatch();
            assertThatThrownBy(() -> st.executeQuery("SELECT * FROM no_such_table")).isInstanceOf(SQLException.class);
        }

        QueryCount c = this.counter.snapshot("ds");
        assertThat(c.total()).isEqualTo(2);
        assertThat(c.insert()).isEqualTo(1);
        assertThat(c.update()).isEqualTo(1);
        assertThat(c.select()).isEqualTo(1);
        assertThat(c.success()).isEqualTo(1);
        assertThat(c.failure()).isEqualTo(1);
    }

    @Test
    void countsAcrossThreads() throws Exception {
        DataSource ds = proxy("ds");
        ExecutorService pool = Executors.newFixedThreadPool(8);
        try {
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < 8; i++) {
                futures.add(pool.submit(() -> {
                    for (int j = 0; j < 25; j++) {
                        execute(ds, "SELECT * FROM emp");
                    }
                    return null;
                }));
            }
            for (Future<?> f : futures) {
                f.get();
            }
        } finally {
            pool.shutdown();
        }

        assertThat(this.counter.snapshot("ds").select()).isEqualTo(200);
        assertThat(this.counter.snapshot("ds").total()).isEqualTo(200);
    }

    @Test
    void resetClearsCounts() throws Exception {
        execute(proxy("ds"), "SELECT * FROM emp");
        this.counter.reset();

        assertThat(this.counter.snapshot()).isEqualTo(QueryCount.ZERO);
        assertThat(this.counter.snapshot("ds")).isEqualTo(QueryCount.ZERO);
    }

    @Test
    void unnamedAndSameNamedDataSources() throws Exception {
        DataSource a = ProxyDataSourceBuilder.create(this.actual).countQuery(this.counter).build();
        execute(a, "SELECT * FROM emp");
        execute(proxy("b"), "SELECT * FROM emp");
        execute(proxy("b"), "SELECT * FROM emp");

        assertThat(this.counter.snapshot("").select()).isEqualTo(1);
        assertThat(this.counter.snapshot("b").select()).isEqualTo(2);
        assertThat(this.counter.snapshot().select()).isEqualTo(3);
        assertThat(this.counter.snapshot("unknown")).isEqualTo(QueryCount.ZERO);
    }

    @Test
    void customDetectorClassifiesCte() throws Exception {
        QueryTypeDetector detector = q -> q != null && q.stripLeading().regionMatches(true, 0, "WITH", 0, 4)
                ? QueryType.SELECT : QueryUtils.getQueryType(q);
        DataSource ds = ProxyDataSourceBuilder.create(this.actual).name("ds")
                .countQuery(this.counter).queryTypeDetector(detector).build();
        execute(ds, CTE);

        assertThat(this.counter.snapshot("ds").select()).isEqualTo(1);
        assertThat(this.counter.snapshot("ds").other()).isZero();
    }

    @Test
    void defaultDetectorCountsCteAsOther() throws Exception {
        execute(proxy("ds"), CTE);

        assertThat(this.counter.snapshot("ds").other()).isEqualTo(1);
    }

    @Test
    void detectorReturningNullFailsFast() throws Exception {
        DataSource ds = ProxyDataSourceBuilder.create(this.actual).name("ds")
                .countQuery(this.counter).queryTypeDetector(q -> null).build();

        assertThatThrownBy(() -> execute(ds, "SELECT * FROM emp"))
                .isInstanceOf(NullPointerException.class).hasMessageContaining("QueryTypeDetector");
    }

}
