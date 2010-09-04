package net.ttddyy.dsproxy;

import org.hsqldb.jdbc.jdbcDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

/**
 * @author Tadaya Tsuyukubo
 */
public class TestUtils {

    public static DataSource getDataSourceWithData() throws Exception {
        jdbcDataSource dataSource = new jdbcDataSource();
        dataSource.setDatabase("jdbc:hsqldb:mem:aname");
        dataSource.setUser("sa");

        executeQuery(dataSource,
                "create table emp ( id integer primary key, name varchar(10) );",
                "insert into emp ( id, name )values (1, 'foo');",
                "insert into emp ( id, name )values (2, 'bar');"
        );

        return dataSource;
    }

    private static void executeQuery(DataSource dataSource, String... queries) throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
        for (String query : queries) {
            stmt.executeQuery(query);
        }
        conn.close();
    }

    public static void shutdown(DataSource dataSource) throws Exception {
        executeQuery(dataSource, "shutdown;");
    }

}
