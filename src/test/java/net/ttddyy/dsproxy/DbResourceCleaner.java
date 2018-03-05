package net.ttddyy.dsproxy;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Tadaya Tsuyukubo
 */
public class DbResourceCleaner {

    private Set<Connection> connections = new HashSet<>();
    private Set<Statement> statements = new HashSet<>();

    public void add(Connection connection) {
        this.connections.add(connection);
    }

    public void add(Statement statement) {
        this.statements.add(statement);
    }

    public void closeAll() throws SQLException {
        for (Statement statement : statements) {
            statement.close();
        }
        for (Connection connection : connections) {
            connection.close();
        }
    }
}
