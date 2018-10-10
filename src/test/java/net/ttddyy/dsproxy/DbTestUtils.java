package net.ttddyy.dsproxy;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.hsqldb.jdbc.JDBCDataSource;
import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Tadaya Tsuyukubo
 */
public class DbTestUtils {

    public static final String SYSTEM_PROPERTY_DB_TYPE = "dsproxy.dbtype";
    public static DatabaseType dbType;

    private static PostgreSQLContainer POSTGRES_CONTAINER;
    private static MySQLContainer MYSQL_CONTAINER;

    static {
//        System.setProperty(SYSTEM_PROPERTY_DB_TYPE, DatabaseType.POSTGRES.name());
//        System.setProperty(SYSTEM_PROPERTY_DB_TYPE, DatabaseType.MYSQL.name());

        // default to HSQL
        String value = System.setProperty(SYSTEM_PROPERTY_DB_TYPE, System.getProperty(SYSTEM_PROPERTY_DB_TYPE, DatabaseType.HSQL.name()));
        dbType = DatabaseType.valueOfIgnoreCase(value).orElse(DatabaseType.HSQL);  // default to HSQL

        switch (dbType) {
            case MYSQL:
                initializeMysql();
                break;
            case POSTGRES:
                initializePostgres();
                break;
            case HSQL:
                break;
        }
    }

    public static boolean isCurrentDbType(DatabaseType typeToCheck) {
        return dbType == typeToCheck;
    }

    public static boolean isHsql() {
        return dbType == DatabaseType.HSQL;
    }

    public static boolean isPostgres() {
        return dbType == DatabaseType.POSTGRES;
    }

    public static boolean isMysql() {
        return dbType == DatabaseType.MYSQL;
    }

    private static void initializePostgres() {
        POSTGRES_CONTAINER = new PostgreSQLContainer();
        POSTGRES_CONTAINER.start();
    }

    private static void initializeMysql() {
        MYSQL_CONTAINER = new MySQLContainer() {
            @Override
            public String getDriverClassName() {
                return "com.mysql.cj.jdbc.Driver";
            }
        };
        MYSQL_CONTAINER.start();
    }

    public static String getUsername() {
        switch (dbType) {
            case MYSQL:
                return MYSQL_CONTAINER.getUsername();
            case POSTGRES:
                return POSTGRES_CONTAINER.getUsername();
            case HSQL:
                return "sa";
        }
        return null;

    }

    public static String getPassword() {
        switch (dbType) {
            case MYSQL:
                return MYSQL_CONTAINER.getPassword();
            case POSTGRES:
                return POSTGRES_CONTAINER.getPassword();
            case HSQL:
                return "";
        }
        return null;
    }

    public static DataSource getDataSourceWithData() {
        DataSource dataSource = createDataSource();
        switch (dbType) {
            case MYSQL:
                populateInitialDataForMysql(dataSource);
                break;
            case POSTGRES:
                populateInitialDataForPostgres(dataSource);
                break;
            case HSQL:  // default to HSQL
                populateInitialDataForHsql(dataSource);
                break;
        }
        return dataSource;
    }

    public static DataSource createDataSource() {
        DataSource dataSource = null;
        switch (dbType) {
            case MYSQL:
                dataSource = createDataSourceForMysql();
                break;
            case POSTGRES:
                dataSource = createDataSourceForPostgres();
                break;
            case HSQL:  // default to HSQL
                dataSource = createDataSourceForHSQL();
                break;
        }
        return dataSource;
    }

    private static DataSource createDataSourceForHSQL() {
        JDBCDataSource dataSource = new JDBCDataSource();
        dataSource.setDatabase("jdbc:hsqldb:mem:aname");
        dataSource.setUser("sa");
        return dataSource;
    }

    private static DataSource createDataSourceForPostgres() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(POSTGRES_CONTAINER.getJdbcUrl());
        dataSource.setUser(POSTGRES_CONTAINER.getUsername());
        dataSource.setPassword(POSTGRES_CONTAINER.getPassword());
        return dataSource;
    }

    private static DataSource createDataSourceForMysql() {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUrl(MYSQL_CONTAINER.getJdbcUrl());
        dataSource.setUser(MYSQL_CONTAINER.getUsername());
        dataSource.setPassword(MYSQL_CONTAINER.getPassword());
        return dataSource;
    }

    private static void populateInitialDataForHsql(DataSource dataSource) {
        executeQuery(dataSource,
                "drop table if exists emp;",
                "create table emp ( id integer primary key, name varchar(10) );",
                "insert into emp ( id, name ) values (1, 'foo');",
                "insert into emp ( id, name ) values (2, 'bar');"
        );

        executeQuery(dataSource,
                "drop table if exists emp_with_auto_id;",
                "create table emp_with_auto_id ( id integer generated by default as identity primary key, name varchar(10) );",
                "ALTER TABLE emp_with_auto_id ALTER COLUMN id RESTART WITH 1;",  // make sequence start from 1
                "insert into emp_with_auto_id ( name ) values ('foo');",
                "insert into emp_with_auto_id ( name ) values ('bar');"
        );
    }

    private static void populateInitialDataForPostgres(DataSource dataSource) {
        executeQuery(dataSource,
                "drop table if exists emp;",
                "create table emp ( id integer primary key, name varchar(10) );",
                "insert into emp ( id, name ) values (1, 'foo');",
                "insert into emp ( id, name ) values (2, 'bar');"
        );

        // postgres sequence starts with 1
        executeQuery(dataSource,
                "drop table if exists emp_with_auto_id;",
                "create table emp_with_auto_id ( id SERIAL primary key, name varchar(10) );",
                "insert into emp_with_auto_id ( name ) values ('foo');",
                "insert into emp_with_auto_id ( name ) values ('bar');"
        );
    }

    private static void populateInitialDataForMysql(DataSource dataSource) {
        executeQuery(dataSource,
                "drop table if exists emp;",
                "create table emp ( id integer primary key, name varchar(10) );",
                "insert into emp ( id, name ) values (1, 'foo');",
                "insert into emp ( id, name ) values (2, 'bar');"
        );

        // postgres sequence starts with 1
        executeQuery(dataSource,
                "drop table if exists emp_with_auto_id;",
                "create table emp_with_auto_id ( id integer AUTO_INCREMENT primary key, name varchar(10) );",
                "insert into emp_with_auto_id ( name ) values ('foo');",
                "insert into emp_with_auto_id ( name ) values ('bar');"
        );
    }

    private static void executeQuery(DataSource dataSource, String... queries) {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.createStatement();
            for (String query : queries) {
                stmt.execute(query);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void shutdown(DataSource dataSource) throws Exception {
        // TODO: cleanup
        if (dbType == DatabaseType.HSQL) {
            executeQuery(dataSource, "shutdown;");
        }
    }

    public static int countTable(DataSource dataSource, String tableName) throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select count(*) from " + tableName);
        rs.next();

        int count = rs.getInt(1);

        rs.close();
        stmt.close();
        conn.close();

        return count;
    }

    public static int[] executeBatchStatements(DataSource dataSource, String... queries) throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();

        try {
            for (String query : queries) {
                stmt.addBatch(query);
            }
            return stmt.executeBatch();
        } finally {
            try {
                stmt.close();
            } finally {
                conn.close();
            }
        }
    }

}
