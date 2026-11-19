package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.TestUtils;
import net.ttddyy.dsproxy.support.ProxyDataSource;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tadaya Tsuyukubo
 */
class MethodListenerTest {

    private DataSource jdbcDataSource;

    @BeforeEach
    void setup() throws Exception {
        // real datasource
        jdbcDataSource = TestUtils.getDataSourceWithData();
    }

    @AfterEach
    void teardown() throws Exception {
        TestUtils.shutdown(jdbcDataSource);
    }


    @Test
    void replaceMethodArgument() throws Throwable {
        ProxyDataSourceListener methodListener = new ProxyDataSourceListener() {
            @Override
            public void beforeMethod(MethodExecutionContext executionContext) {

                // replace query to find id=2
                if ("executeQuery".equals(executionContext.getMethod().getName())) {
                    executionContext.setMethodArgs(new Object[]{"select * from emp where id=2"});
                }
            }

            @Override
            public void afterMethod(MethodExecutionContext executionContext) {

            }
        };

        ProxyDataSource ds = ProxyDataSourceBuilder.create(this.jdbcDataSource).listener(methodListener).build();
        Connection conn = ds.getConnection();
        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery("select * from emp where id=1");
        rs.next();
        assertThat(rs.getInt("id")).isEqualTo(2);
        assertThat(rs.getString("name")).isEqualTo("bar");

    }
}
