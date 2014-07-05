package net.ttddyy.dsproxy;

import org.hsqldb.jdbc.JDBCDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author Tadaya Tsuyukubo
 */
public class TestUtils {

    public static DataSource getDataSourceWithData() throws Exception {
        JDBCDataSource dataSource = new JDBCDataSource();
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
            stmt.execute(query);
        }
        conn.close();
    }

    public static void shutdown(DataSource dataSource) throws Exception {
        executeQuery(dataSource, "shutdown;");
    }

    public static int countTable(DataSource dataSource, String tableName) throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select count(*) from " + tableName);
        rs.next();
        conn.close();
        return rs.getInt(1);
    }
}
