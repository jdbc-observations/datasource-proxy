package net.ttddyy.dsproxy.test;

import org.flywaydb.core.Flyway;
import org.hsqldb.jdbc.JDBCDataSource;
import org.junit.rules.ExternalResource;

import javax.sql.DataSource;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class DatabaseTestRule extends ExternalResource {

    public DataSource dataSource;

    private Flyway flyway;

    @Override
    protected void before() throws Throwable {

        // prepare ds
        JDBCDataSource ds = new JDBCDataSource();
        ds.setDatabase("jdbc:hsqldb:mem:aname");
        ds.setUser("sa");
        this.dataSource = ds;

        // populate data
        this.flyway = new Flyway();
        this.flyway.setDataSource(ds);
        this.flyway.migrate();

    }

    @Override
    protected void after() {
        flyway.clean();
    }

}
