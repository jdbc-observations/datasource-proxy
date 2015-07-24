package net.ttddyy.dsproxy.test;

import org.flywaydb.core.Flyway;
import org.hsqldb.jdbc.JDBCDataSource;
import org.junit.rules.ExternalResource;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.junit.Assert.fail;

/**
 * @author Tadaya Tsuyukubo
 */
public class DatabaseTestRule extends ExternalResource {

    public DataSource dataSource;

    @Override
    protected void before() throws Throwable {
        JDBCDataSource ds = new JDBCDataSource();
        ds.setDatabase("jdbc:hsqldb:mem:aname");
        ds.setUser("sa");

        Flyway flyway = new Flyway();
        flyway.setDataSource(ds);
        flyway.migrate();

        this.dataSource = ds;
    }

    @Override
    protected void after() {
        try {
            executeQuery(dataSource, "shutdown;");
        } catch (Exception e) {
            fail("Failed to shutdown database");
        }
    }

    private static void executeQuery(DataSource dataSource, String... queries) throws Exception {
        Connection conn = dataSource.getConnection();
        java.sql.Statement stmt = conn.createStatement();
        for (String query : queries) {
            stmt.execute(query);
        }
        conn.close();
    }


}
