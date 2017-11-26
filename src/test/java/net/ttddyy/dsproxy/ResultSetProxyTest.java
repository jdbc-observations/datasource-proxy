package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.support.ProxyDataSource;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.hsqldb.jdbc.JDBCDataSource;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Liam Williams
 */
public class ResultSetProxyTest {

    @Test
    public void checkThatResultSetCanBeConsumedMoreThanOnce() throws Exception {
        JDBCDataSource dataSourceWithData = dataSourceWithData();

        LoggingExecutionListener listener = new LoggingExecutionListener();
        ProxyDataSource proxyDataSource = ProxyDataSourceBuilder.create(dataSourceWithData)
                .listener(listener)
                .repeatableReadResultSet()
                .build();

        checkThatResultSetCanBeConsumedViaTheProxyDataSource(proxyDataSource);
        checkThatTheResultSetWasAlsoConsumedInTheListener(listener);
    }

    private void checkThatTheResultSetWasAlsoConsumedInTheListener(LoggingExecutionListener listener) {
        assertThat(listener.table.columns).containsExactly("A", "B");
        assertThat(listener.table.rows).containsExactly(
                new String[]{"1", "2"},
                new String[]{"3", "4"},
                new String[]{"5", "6"}
        );
    }

    private JDBCDataSource dataSourceWithData() throws SQLException {
        JDBCDataSource dataSource = new JDBCDataSource();
        dataSource.setDatabase("jdbc:hsqldb:mem:test");
        Connection connection = dataSource.getConnection();
        connection.createStatement().execute("CREATE TABLE test(a INT, b INT)");
        connection.prepareStatement("INSERT INTO test(a, b) VALUES(1,2)").executeUpdate();
        connection.prepareStatement("INSERT INTO test(a, b) VALUES(3,4)").executeUpdate();
        connection.prepareStatement("INSERT INTO test(a, b) VALUES(5,6)").executeUpdate();
        return dataSource;
    }

    private void checkThatResultSetCanBeConsumedViaTheProxyDataSource(ProxyDataSource proxyDataSource) throws SQLException {
        Connection connection = proxyDataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * from test");
        ResultSet resultSet = preparedStatement.executeQuery();

        resultSet.next();
        assertThat(resultSet.getInt("a")).isEqualTo(1);
        assertThat(resultSet.getInt(2)).isEqualTo(2);

        resultSet.next();
        assertThat(resultSet.getInt(1)).isEqualTo(3);
        assertThat(resultSet.getInt("b")).isEqualTo(4);

        resultSet.next();
        assertThat(resultSet.getInt("a")).isEqualTo(5);
        assertThat(resultSet.getInt("b")).isEqualTo(6);

        assertThat(resultSet.next()).isFalse();

        assertThat(resultSet.isClosed()).isFalse();

        resultSet.close();

        assertThat(resultSet.isClosed()).isTrue();
    }

    private static class LoggingExecutionListener implements QueryExecutionListener {

        private Table table;

        @Override
        public void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {

        }

        @Override
        public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
            if (execInfo.getResult() instanceof ResultSet) {
                table = extractTable((ResultSet) execInfo.getResult());
            }
        }

        private static Table extractTable(ResultSet resultSet) {
            try {
                String[] columns = extractColumns(resultSet);
                List<String[]> rows = new ArrayList<String[]>();
                while (resultSet.next()) {
                    rows.add(extractRow(resultSet));
                }
                resultSet.beforeFirst();
                return new Table(columns, rows);
            } catch (SQLException e) {
                throw new IllegalStateException("Could not extract result set", e);
            }
        }

        private static String[] extractColumns(ResultSet resultSet) throws SQLException {
            ResultSetMetaData metaData = resultSet.getMetaData();
            String[] columns = new String[metaData.getColumnCount()];
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                columns[i - 1] = metaData.getColumnLabel(i);
            }
            return columns;
        }

        private static String[] extractRow(ResultSet resultSet) throws SQLException {
            ResultSetMetaData metaData = resultSet.getMetaData();
            String[] row = new String[metaData.getColumnCount()];
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                row[i - 1] = String.valueOf(resultSet.getObject(i));
            }
            return row;
        }
    }

    private static class Table {
        private final String[] columns;
        private final List<String[]> rows;

        private Table(String[] columns, List<String[]> rows) {
            this.columns = columns;
            this.rows = rows;
        }
    }
}
