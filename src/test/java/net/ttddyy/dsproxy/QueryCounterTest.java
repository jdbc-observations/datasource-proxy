package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QueryCounterTest {

    private DataSource actual;

    private DataSource ds1;

    private DataSource ds2;

    private DataSource plain;

    @BeforeEach
    void setup() throws Exception {
        this.actual = TestUtils.getDataSourceWithData();
        this.ds1 = ProxyDataSourceBuilder.create(this.actual).name("a").countQuery().build();
        this.ds2 = ProxyDataSourceBuilder.create(this.actual).name("b").countQuery().build();
        this.plain = ProxyDataSourceBuilder.create(this.actual).name("p").build();
    }

    @AfterEach
    void teardown() throws Exception {
        assertThat(QueryCounter.current()).isNull();
        TestUtils.shutdown(this.actual);
    }

    static void select(DataSource ds) {
        try (Connection conn = ds.getConnection(); Statement st = conn.createStatement()) {
            st.executeQuery("SELECT * FROM emp").close();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    void lambdaFormReturnsGrandTotal() {
        assertThat(QueryCounter.count(() -> {
            select(this.ds1);
            select(this.ds2);
        }).select()).isEqualTo(2);
    }

    @Test
    void scopeGivesPerDataSourceAndTotal() {
        try (var scope = QueryCounter.start()) {
            select(this.ds1);
            select(this.ds1);
            select(this.ds2);
            assertThat(scope.count("a").select()).isEqualTo(2);
            assertThat(scope.count("b").select()).isEqualTo(1);
            assertThat(scope.count().select()).isEqualTo(3);
            assertThat(scope.count("none")).isEqualTo(QueryCount.ZERO);
        }
    }

    @Test
    void nestedScopesCountIntoOuter() {
        try (var outer = QueryCounter.start()) {
            select(this.ds1);
            try (var inner = QueryCounter.start()) {
                select(this.ds1);
                assertThat(inner.count().select()).isEqualTo(1);
                assertThat(outer.count().select()).isEqualTo(2);
            }
            select(this.ds1);
            assertThat(outer.count().select()).isEqualTo(3);
        }
    }

    @Test
    void onlyCallingThreadCountsByDefault() throws Exception {
        try (var scope = QueryCounter.start()) {
            Thread t = new Thread(() -> select(this.ds1));
            t.start();
            t.join();
            assertThat(scope.count().total()).isZero();
        }
    }

    @Test
    void noQueriesOutsideScopeOrWithoutCountQuery() {
        select(this.ds1);
        try (var scope = QueryCounter.start()) {
            select(this.plain);
            assertThat(scope.count().total()).isZero();
        }
    }

    @Test
    void bothModesActiveOnOneDataSource() {
        var counter = new DataSourceQueryCounter();
        DataSource both = ProxyDataSourceBuilder.create(this.actual).name("c").countQuery(counter).build();
        try (var scope = QueryCounter.start()) {
            select(both);
            assertThat(scope.count("c").select()).isEqualTo(1);
        }
        select(both);
        assertThat(counter.snapshot("c").select()).isEqualTo(2);
    }

    @Test
    void exceptionInBodyStillClosesScope() {
        assertThatThrownBy(() -> QueryCounter.count(() -> {
            throw new IllegalStateException("boom");
        })).hasMessage("boom");
        assertThat(QueryCounter.current()).isNull();
    }

    @Test
    void closingNonCurrentScopeThrows() {
        var outer = QueryCounter.start();
        var inner = QueryCounter.start();
        assertThatThrownBy(outer::close).isInstanceOf(IllegalStateException.class);
        inner.close();
        outer.close();
        outer.close();
    }

}
